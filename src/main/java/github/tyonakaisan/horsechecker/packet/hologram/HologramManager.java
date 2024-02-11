package github.tyonakaisan.horsechecker.packet.hologram;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.horse.HorseStats;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.*;

@Singleton
@DefaultQualifier(NonNull.class)
@SuppressWarnings("unused")
public final class HologramManager {

    private final Map<String, HologramData> hologramMap = new HashMap<>();

    private final Server server;
    private final ConfigFactory configFactory;

    @Inject
    public HologramManager(
            final Server server,
            final ConfigFactory configFactory
    ) {
        this.server = server;
        this.configFactory = configFactory;
    }

    public Set<String> getHologramNames() {
        return Collections.unmodifiableSet(this.hologramMap.keySet());
    }

    public HologramData getHologramData(String hologramId) {
        return this.hologramMap.get(hologramId);
    }

    public void createHologram(HorseStats horseStats, Component text) {
        var hologramId = horseStats.horse().getUniqueId().toString();
        if (this.hologramMap.containsKey(hologramId)) return;
        var hologramData = new HologramData(hologramId, text, horseStats.location(), horseStats);

        this.hologramMap.put(hologramId, hologramData);
    }

    public void deleteHologram(HorseStats horseStats) {
        this.server.forEachAudience(audience -> {
            if (audience instanceof Player player) this.hideHologram(horseStats, player);
        });
        var horseUuid = horseStats.horse().getUniqueId().toString();
        this.hologramMap.remove(horseUuid);
    }

    public void hideHologram(HorseStats horseStats, Player player) {
        var hologramId = horseStats.horse().getUniqueId().toString();
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            hologramData.updateLocation(horseStats.location());
            hologramData.hideFrom(player);
        });
    }

    public void showHologram(String hologramId, Player player, int vehicleId) {
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> hologramData.showFrom(player, vehicleId));
    }

    public void updateHologram(String hologramId, HorseStats horseStats) {
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            var text = Converter.statsMessageResolver(this.configFactory, horseStats);
            hologramData.updateHorseStats(horseStats);
            hologramData.updateText(text);
        });
    }
}
