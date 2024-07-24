package github.tyonakaisan.horsechecker.packet.hologram;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.horse.WrappedHorse;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.*;

@Singleton
@DefaultQualifier(NonNull.class)
@SuppressWarnings("unused")
public final class HologramManager {

    private final Map<String, HologramData> hologramMap = new HashMap<>();

    private final Converter converter;
    private final ComponentLogger logger;
    private final ConfigFactory configFactory;

    @Inject
    public HologramManager(
            final Converter converter,
            final ComponentLogger logger,
            final ConfigFactory configFactory
    ) {
        this.converter = converter;
        this.logger = logger;
        this.configFactory = configFactory;
    }

    public Set<String> getHologramNames() {
        return Collections.unmodifiableSet(this.hologramMap.keySet());
    }

    public HologramData getHologramData(final String hologramId) {
        return this.hologramMap.get(hologramId);
    }

    public void create(final Tameable tameable) {
        final var hologramId = tameable.getUniqueId().toString();
        final var text = this.converter.tamableMessageResolver(tameable);
        final var hologramData = new HologramData(hologramId, text, tameable.getLocation(), 0, this.configFactory.primaryConfig().hologram());

        this.create(hologramId, hologramData);
    }

    public void create(final WrappedHorse wrappedHorse) {
        final var hologramId = wrappedHorse.horse().getUniqueId().toString();
        final var text = this.converter.statsMessageResolver(wrappedHorse);
        final var hologramData = new HologramData(hologramId, text, wrappedHorse.getLocation(), wrappedHorse.getRank().backgroundColor(), this.configFactory.primaryConfig().hologram());

        this.create(hologramId, hologramData);
    }

    public void create(final String hologramId, final HologramData hologramData) {
        if (this.hologramMap.containsKey(hologramId)) {
            return;
        }

        this.hologramMap.put(hologramId, hologramData);
    }

    public void remove(final WrappedHorse wrappedHorse) {
        Bukkit.getServer().getOnlinePlayers().forEach(player ->
                this.hide(wrappedHorse, player));

        final var horseUuid = wrappedHorse.horse().getUniqueId().toString();
        this.hologramMap.remove(horseUuid);
    }

    public void destroyAll() {
        this.logger.info("Destroy all holograms...");

        this.hologramMap.values()
                .forEach(hologramData ->
                        Bukkit.getServer().getOnlinePlayers()
                                .forEach(hologramData::hideFrom));
        this.hologramMap.clear();

        this.logger.info("All holograms were destroyed!");
    }

    public void hide(final Tameable tameable, final Player player) {
        final var hologramId = tameable.getUniqueId().toString();
        this.hide(hologramId, tameable.getLocation(), player);
    }

    public void hide(final WrappedHorse wrappedHorse, final Player player) {
        final var hologramId = wrappedHorse.horse().getUniqueId().toString();
        this.hide(hologramId, wrappedHorse.getLocation(), player);
    }

    public void hide(final String hologramId, final Location location, final Player player) {
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            hologramData.updateLocation(location);
            hologramData.hideFrom(player);
        });
    }

    public void show(final Tameable tameable, final Player player, final int vehicleId) {
        final var hologramId = tameable.getUniqueId().toString();
        this.show(hologramId, tameable.getLocation(), player, vehicleId);
    }

    public void show(final WrappedHorse wrappedHorse, final Player player, final int vehicleId) {
        final var hologramId = wrappedHorse.horse().getUniqueId().toString();
        this.show(hologramId, wrappedHorse.getLocation(), player, vehicleId);
    }

    public void show(final String hologramId, final Location location, final Player player, final int vehicleId) {
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            hologramData.updateLocation(location);
            hologramData.showFrom(player, vehicleId);
        });
    }

    public void update(final String hologramId, final WrappedHorse wrappedHorse) {
        Optional.ofNullable(this.hologramMap.get(hologramId)).ifPresent(hologramData -> {
            var text = this.converter.statsMessageResolver(wrappedHorse);
            hologramData.updateBackgroundColor(wrappedHorse.getRank().backgroundColor());
            hologramData.updateText(text);
            hologramData.updateHologram();
        });
    }
}
