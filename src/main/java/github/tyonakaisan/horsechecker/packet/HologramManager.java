package github.tyonakaisan.horsechecker.packet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.horse.HorseStatsData;
import github.tyonakaisan.horsechecker.packet.HologramData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@DefaultQualifier(NonNull.class)
public final class HologramManager {
    private final Map<String, HologramData> hologramMap = new HashMap<>();

    private final Server server;

    @Inject
    public HologramManager(
            final Server server
    ) {
        this.server = server;
    }

    public List<String> getHologramNames() {
        return List.copyOf(this.hologramMap.keySet());
    }

    public HologramData getHologramData(String hologramId) {
        return this.hologramMap.get(hologramId);
    }

    public void createHologram(HorseStatsData statsData, Component text) {
        var hologramId = statsData.uuid().toString();
        var hologramData = new HologramData(hologramId, text, statsData.location(), statsData.rank());

        if (this.hologramMap.containsKey(hologramId)) return;
        this.hologramMap.put(hologramId, hologramData);
    }

    public void deleteHologram(String hologramId) {
        this.server.forEachAudience(audience -> {
            if (audience instanceof Player player) this.hideHologram(hologramId, player);
        });
        this.hologramMap.remove(hologramId);
    }

    public void hideHologram(String hologramId, Player player) {
        var hologramData = this.hologramMap.get(hologramId);

        if (hologramData == null) return;
        hologramData.hideFrom(player);
    }

    public void showHologram(String hologramId, Player player) {
        var hologramData = this.hologramMap.get(hologramId);

        if (hologramData == null) return;
        hologramData.showFrom(player);
    }

    public void teleportHologram(String hologramId, Location targetLocation) {
        var hologramData = this.hologramMap.get(hologramId);

        if (hologramData == null) return;
        hologramData.teleportTo(targetLocation);
    }
}
