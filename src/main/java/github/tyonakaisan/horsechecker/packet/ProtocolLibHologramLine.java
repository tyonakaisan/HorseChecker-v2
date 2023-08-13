package github.tyonakaisan.horsechecker.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import github.tyonakaisan.horsechecker.horse.HorseRank;
import github.tyonakaisan.horsechecker.packet.holograms.HologramLine;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@DefaultQualifier(NonNull.class)
public final class ProtocolLibHologramLine implements HologramLine {

    private final UUID entityUid;
    private final int entityId;
    private Location location;
    private Component text = Component.empty();
    private final String rank;

    public ProtocolLibHologramLine(Location location, String rank) {
        this.location = location;
        // Could be safer but this is probably fine
        this.entityId = ThreadLocalRandom.current().nextInt();
        this.entityUid = UUID.randomUUID();
        this.rank = rank;
    }

    @Override
    public Component getText() {
        return text;
    }

    @Override
    public void showTo(Player player) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.sendServerPacket(player, createAddPacket());
        protocolManager.sendServerPacket(player, createDataPacket());
    }

    @Override
    public void hideFrom(Player player) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, createRemovePacket());
    }

    @Override
    public void teleport(Location location) {
        this.location = location;
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(createMovePacket());
    }

    @Override
    public void setText(Component text) {
        this.text = text;
        //ProtocolLibrary.getProtocolManager().broadcastServerPacket(createDataPacket());
    }

    private PacketContainer createAddPacket() {
        PacketType type = PacketType.Play.Server.SPAWN_ENTITY;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        StructureModifier<Integer> intMod = packet.getIntegers();
        StructureModifier<EntityType> typeMod = packet.getEntityTypeModifier();
        StructureModifier<UUID> uuidMod = packet.getUUIDs();
        StructureModifier<Double> doubleMod = packet.getDoubles();

        // Write id of entity
        intMod.write(0, this.entityId);

        // Write type of entity
        typeMod.write(0, EntityType.TEXT_DISPLAY);

        // Write entities UUID
        uuidMod.write(0, this.entityUid);

        // Write position
        doubleMod.write(0, location.getX());
        doubleMod.write(1, location.getY() + 2.5);
        doubleMod.write(2, location.getZ());

        return packet;
    }

    private PacketContainer createDataPacket() {
        PacketType type = PacketType.Play.Server.ENTITY_METADATA;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        WrappedDataWatcher.Serializer chatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer();
        WrappedDataWatcher.Serializer intSerializer = WrappedDataWatcher.Registry.get(Integer.class);
        WrappedDataWatcher.Serializer floatSerializer = WrappedDataWatcher.Registry.get(Float.class);

        List<WrappedDataValue> dataValues = new ArrayList<>();

        dataValues.add(new WrappedDataValue(14, byteSerializer, (byte) 3));
        dataValues.add(new WrappedDataValue(15, intSerializer, (15 << 4 | 15 << 20)));
        dataValues.add(new WrappedDataValue(16, floatSerializer, 2f));
        dataValues.add(new WrappedDataValue(17, floatSerializer, 0f));
        dataValues.add(new WrappedDataValue(18, floatSerializer, 0f));

        //Text
        WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(this.text));
        dataValues.add(new WrappedDataValue(22, chatSerializer, wrappedChatComponent.getHandle()));
        //Background color
        dataValues.add(new WrappedDataValue(24, intSerializer, HorseRank.calcEvaluateRankBackgroundColor(rank)));
        //Text opacity
        dataValues.add(new WrappedDataValue(25, byteSerializer, (byte) -1));

        byte bitmask = 0x00;
        //Shadow
        //bitmask |= 0x01;
        //See Through
        bitmask |= 0x02;
        //Alignment
        bitmask |= 0x18;
        dataValues.add(new WrappedDataValue(26, byteSerializer, bitmask));

        //Billboard
        //dataValues.add(new WrappedDataValue(14, intSerializer, 3));

        //Glow text
        dataValues.add(new WrappedDataValue(15, intSerializer, 15));

        packet.getIntegers().write(0, this.entityId);
        packet.getDataValueCollectionModifier().write(0, dataValues);

        return packet;
    }

    private PacketContainer createMovePacket() {
        PacketType type = PacketType.Play.Server.ENTITY_TELEPORT;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntegers().write(0, entityId);

        StructureModifier<Double> doubleMod = packet.getDoubles();
        doubleMod.write(0, this.location.getX());
        doubleMod.write(1, this.location.getY() + 2.5);
        doubleMod.write(2, this.location.getZ());

        return packet;
    }

    private PacketContainer createRemovePacket() {
        PacketType type = PacketType.Play.Server.ENTITY_DESTROY;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntLists().write(0, IntList.of(this.entityId));

        return packet;
    }

}
