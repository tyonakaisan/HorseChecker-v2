package github.tyonakaisan.horsechecker.packet;

import com.comphenix.protocol.events.PacketContainer;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.horse.HorseRank;
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
    private final HorseRank.HorseRankData horseRankData;

    private final UUID entityUid;
    private final int entityId;
    private @Nullable PacketContainer entityMetadataPacket;

    public HologramData(
            String hologramId,
            Component text,
            Location location,
            HorseRank.HorseRankData horseRankData
    ) {
        this.hologramId = hologramId;
        this.text = text;
        this.location = location;
        this.horseRankData = horseRankData;

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

    public HorseRank.HorseRankData rankData() {
        return this.horseRankData;
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

    public void updateText(Component text) {
        this.text = text;
        this.entityMetadataPacket = new HologramPacketManager(this).createEntityMetadataPacket();
        new HologramPacketManager(this).update();
    }

    public void updateLocation(Location location) {
        this.location = location;
    }
}
