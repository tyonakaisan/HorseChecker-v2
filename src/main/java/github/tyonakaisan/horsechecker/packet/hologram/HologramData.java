package github.tyonakaisan.horsechecker.packet.hologram;

import com.comphenix.protocol.events.PacketContainer;
import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@DefaultQualifier(NonNull.class)
public final class HologramData {

    private final String hologramId;
    private Component text;
    private Location location;
    private int backgroundColor;

    private final UUID entityUid;
    private final int entityId;
    private @Nullable PacketContainer entityMetadataPacket;

    public HologramData(
            String hologramId,
            Component text,
            Location location,
            int backgroundColor
    ) {
        this.hologramId = hologramId;
        this.text = text;
        this.location = location;
        this.backgroundColor = backgroundColor;

        this.entityId = ThreadLocalRandom.current().nextInt();
        this.entityUid = UUID.randomUUID();
    }

    public String hologramId() {
        return this.hologramId;
    }

    public UUID entityUid() {
        return this.entityUid;
    }

    public int entityId() {
        return this.entityId;
    }

    public Component text() {
        return this.text;
    }

    public Location location() {
        return this.location;
    }

    public int backgroundColor() {
        return this.backgroundColor;
    }

    public void showFrom(Player player, int vehicleId) {
        if (this.entityMetadataPacket == null) {
            this.entityMetadataPacket = new HologramPacketManager(this).createEntityMetadataPacket();
        }
        new HologramPacketManager(this).show(player, vehicleId, this.entityMetadataPacket);
    }

    public void hideFrom(Player player) {
        new HologramPacketManager(this).hide(player);
    }

    public void updateHologram() {
        this.entityMetadataPacket = new HologramPacketManager(this).createEntityMetadataPacket();
        new HologramPacketManager(this).update();
    }

    public void updateText(Component newText) {
        this.text = newText;
    }

    public void updateBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void updateLocation(Location newLocation) {
        this.location = newLocation;
    }
}
