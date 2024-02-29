package github.tyonakaisan.horsechecker.packet.hologram;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.horse.WrappedHorse;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
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
    private final ComponentLogger logger;
    private final ConfigFactory configFactory;

    @Inject
    public HologramManager(
            final Server server,
            final ComponentLogger logger,
            final ConfigFactory configFactory
    ) {
        this.server = server;
        this.logger = logger;
        this.configFactory = configFactory;
    }

    public Set<String> getHologramNames() {
        return Collections.unmodifiableSet(this.hologramMap.keySet());
    }

    public HologramData getHologramData(final String hologramId) {
        return this.hologramMap.get(hologramId);
    }

    public void createHologram(final WrappedHorse wrappedHorse) {
        final var hologramId = wrappedHorse.horse().getUniqueId().toString();
        if (this.hologramMap.containsKey(hologramId)) {
            return;
        }
        final var text = Converter.statsMessageResolver(this.configFactory, wrappedHorse);
        final var hologramData = new HologramData(hologramId, text, wrappedHorse.location(), wrappedHorse.getRank().backgroundColor());

        this.hologramMap.put(hologramId, hologramData);
    }

    public void deleteHologram(final WrappedHorse wrappedHorse) {
        this.server.getOnlinePlayers()
                .forEach(player -> this.hideHologram(wrappedHorse, player));
        final var horseUuid = wrappedHorse.horse().getUniqueId().toString();
        this.hologramMap.remove(horseUuid);
    }

    public void destroyAllHologram() {
        this.logger.info("Destroy all holograms...");
        this.hologramMap.values().forEach(hologramData ->
                this.server.forEachAudience(audience -> {
                    if (audience instanceof Player player) {
                        hologramData.hideFrom(player);
                    }
                }));
        this.hologramMap.clear();
        this.logger.info("All holograms were destroyed!");
    }

    public void hideHologram(final WrappedHorse wrappedHorse, final Player player) {
        final var hologramId = wrappedHorse.horse().getUniqueId().toString();
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            hologramData.updateLocation(wrappedHorse.location());
            hologramData.hideFrom(player);
        });
    }

    public void showHologram(final WrappedHorse wrappedHorse, Player player, int vehicleId) {
        var hologramId = wrappedHorse.horse().getUniqueId().toString();
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            hologramData.updateLocation(wrappedHorse.location());
            hologramData.showFrom(player, vehicleId);
        });
    }

    public void updateHologram(final String hologramId, final WrappedHorse wrappedHorse) {
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            var text = Converter.statsMessageResolver(this.configFactory, wrappedHorse);
            hologramData.updateBackgroundColor(wrappedHorse.getRank().backgroundColor());
            hologramData.updateText(text);
            hologramData.updateHologram();
        });
    }
}
