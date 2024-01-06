package github.tyonakaisan.horsechecker.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import github.tyonakaisan.horsechecker.packet.util.PacketEntityDataBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.UUID;

@DefaultQualifier(NonNull.class)
public final class HologramPacketManager {

    private final HologramData hologramData;

    public HologramPacketManager(HologramData hologramData) {
        this.hologramData = hologramData;
    }

    public PacketContainer createEntityMetadataPacket() {
        return this.createDataPacket();
    }

    public void show(Player player, int vehicleId, PacketContainer entityMetadataPacket) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.sendServerPacket(player, this.createAddPacket());
        protocolManager.sendServerPacket(player, this.createSetPassengerPacket(vehicleId));
        protocolManager.sendServerPacket(player, entityMetadataPacket);
    }

    public void hide(Player player) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, this.createRemovePacket());
    }

    public void update() {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(this.createDataPacket());
    }

    private PacketContainer createAddPacket() {
        PacketType type = PacketType.Play.Server.SPAWN_ENTITY;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        StructureModifier<Integer> intMod = packet.getIntegers();
        StructureModifier<EntityType> typeMod = packet.getEntityTypeModifier();
        StructureModifier<UUID> uuidMod = packet.getUUIDs();
        StructureModifier<Double> doubleMod = packet.getDoubles();

        // Write id of entity
        intMod.write(0, this.hologramData.entityId());

        // Write type of entity
        typeMod.write(0, EntityType.TEXT_DISPLAY);

        // Write entities UUID
        uuidMod.write(0, this.hologramData.entityUid());

        // Write position
        doubleMod.write(0, this.hologramData.location().getX());
        doubleMod.write(1, this.hologramData.location().getY() + 1);
        doubleMod.write(2, this.hologramData.location().getZ());

        return packet;
    }

    private PacketContainer createDataPacket() {
        PacketType type = PacketType.Play.Server.ENTITY_METADATA;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntegers().write(0, this.hologramData.entityId());
        packet.getDataValueCollectionModifier().write(0, PacketEntityDataBuilder.textDisplay()
                .teleportInterpolationDuration(1)
                .translation(new Vector(0, 1, 0))
                .billboard(Display.Billboard.CENTER)
                .brightness(15)
                .viewRange(2f)
                .shadowRadius(0f)
                .shadowStrength(0f)
                .text(this.hologramData.text())
                .backgroundColor(this.hologramData.rankData().BackgroundColor())
                .seeThrough()
                .alignment(TextDisplay.TextAlignment.LEFT)
                .build());

        return packet;
    }

    private PacketContainer createSetPassengerPacket(int vehicleId) {
        PacketType type = PacketType.Play.Server.MOUNT;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntegers().write(0, vehicleId);
        packet.getIntegerArrays().write(0, new int[]{this.hologramData.entityId()});

        return packet;
    }

    private PacketContainer createRemovePacket() {
        PacketType type = PacketType.Play.Server.ENTITY_DESTROY;
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntLists().write(0, IntList.of(this.hologramData.entityId()));

        return packet;
    }
}
