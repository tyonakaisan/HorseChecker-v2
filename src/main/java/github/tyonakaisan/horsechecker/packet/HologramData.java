package github.tyonakaisan.horsechecker.packet;

import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@DefaultQualifier(NonNull.class)
public final class HologramData {
    private final String hologramId;
    private final Component text;
    private Location location;
    private final String rank;

    private final UUID entityUid;
    private final int entityId;

    public HologramData(
            String hologramId,
            Component text,
            Location location,
            String rank
    ) {
        this.hologramId = hologramId;
        this.text = text;
        this.location = location;
        this.rank = rank;

        this.entityId = ThreadLocalRandom.current().nextInt();
        this.entityUid = UUID.randomUUID();
    }

    public String hologramId() {
        return hologramId;
    }

    public UUID entityUid() {
        return entityUid;
    }

    public int entityId() {
        return entityId;
    }

    public Component text() {
        return text;
    }

    public Location location() {
        return location;
    }

    public String rank() {
        return rank;
    }

    public void showFrom(Player player) {
        new HologramPacketManager(this).show(player);
    }

    public void hideFrom(Player player) {
        new HologramPacketManager(this).hide(player);
    }

    public void teleportTo(Location targetLocation) {
        this.location = targetLocation;
        new HologramPacketManager(this).teleport();
    }
}
