package github.tyonakaisan.horsechecker.packet.hologram;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import github.tyonakaisan.horsechecker.packet.util.PacketDataBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class HologramPacketManager {

    private final HologramData hologramData;

    public HologramPacketManager(final HologramData hologramData) {
        this.hologramData = hologramData;
    }

    public PacketContainer createEntityMetadataPacket() {
        return this.createDataPacket();
    }

    public void show(final Player player, final int vehicleId, final PacketContainer entityMetadataPacket) {
        final var protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.sendServerPacket(player, this.createAddPacket());
        protocolManager.sendServerPacket(player, this.createSetPassengerPacket(vehicleId));
        protocolManager.sendServerPacket(player, entityMetadataPacket);
    }

    public void hide(final Player player) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, this.createRemovePacket());
    }

    public void update() {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(this.createDataPacket());
    }

    private PacketContainer createAddPacket() {
        final var type = PacketType.Play.Server.SPAWN_ENTITY;
        final var packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        final var intMod = packet.getIntegers();
        final var typeMod = packet.getEntityTypeModifier();
        final var uuidMod = packet.getUUIDs();
        final var doubleMod = packet.getDoubles();

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
        final var type = PacketType.Play.Server.ENTITY_METADATA;
        final var packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntegers().write(0, this.hologramData.entityId());
        packet.getDataValueCollectionModifier().write(0, PacketDataBuilder.textDisplay()
                .translation(this.hologramData.hologramSettings().translation())
                .scale(this.hologramData.hologramSettings().scale())
                .billboard(this.hologramData.hologramSettings().billboard())
                .brightness(15)
                .viewRange(this.hologramData.hologramSettings().viewRange())
                .shadowRadius(0f)
                .text(this.hologramData.text())
                .textShadow()
                .backgroundColor(this.hologramData.backgroundColor())
                .seeThrough()
                .alignment(this.hologramData.hologramSettings().alignment())
                .build());

        return packet;
    }

    private PacketContainer createSetPassengerPacket(final int vehicleId) {
        final var type = PacketType.Play.Server.MOUNT;
        final var packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntegers().write(0, vehicleId);
        packet.getIntegerArrays().write(0, new int[]{this.hologramData.entityId()});

        return packet;
    }

    private PacketContainer createRemovePacket() {
        final var type = PacketType.Play.Server.ENTITY_DESTROY;
        final var packet = ProtocolLibrary.getProtocolManager().createPacket(type);

        packet.getIntLists().write(0, IntList.of(this.hologramData.entityId()));

        return packet;
    }
}
