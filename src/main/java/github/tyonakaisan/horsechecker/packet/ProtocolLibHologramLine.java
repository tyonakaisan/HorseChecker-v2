package github.tyonakaisan.horsechecker.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import github.tyonakaisan.horsechecker.horse.HorseRank;
import github.tyonakaisan.horsechecker.packet.holograms.HologramLine;
import github.tyonakaisan.horsechecker.packet.util.TextDisplayDataBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@DefaultQualifier(NonNull.class)
public final class ProtocolLibHologramLine implements HologramLine {

    private final UUID entityUid;
    private final int entityId;
    private Location location;
    private Component text = Component.empty();
    private String rank;

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
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(createDataPacket());
    }

    @Override
    public void setRank(String rank) {
        this.rank = rank;
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(createDataPacket());
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

        List<WrappedDataValue> dataValues = TextDisplayDataBuilder.data()
                .billboard(Display.Billboard.CENTER)
                .brightness(15)
                .viewRange(2f)
                .shadowRadius(0f)
                .shadowStrength(0f)
                .text(this.text)
                .backgroundColor(HorseRank.calcEvaluateRankBackgroundColor(rank))
                .seeThrough()
                .alignment(TextDisplay.TextAlignment.LEFT)
                .build();

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
