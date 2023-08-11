package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.horse.NewStatsHologram;
import github.tyonakaisan.horsechecker.horse.StatsHologram;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.packet.holograms.HologramManager;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PlayerTrackHorseListener implements Listener {

    private final HorseChecker horseChecker;
    private final HologramManager hologramManager;
    private final HorseManager horseManager;
    private final StateManager stateManager;
    private final Converter converter;
    private final ConfigFactory configFactory;
    private final StatsHologram statsHologram;

    private final NewStatsHologram newStatsHologram;

    private final String statsString = Messages.STATS_RESULT_SCORE.get()
            + Messages.STATS_RESULT_SPEED.get()
            + Messages.STATS_RESULT_JUMP.get()
            + Messages.STATS_RESULT_HP.get()
            + Messages.STATS_RESULT_OWNER.get();

    @Inject
    public PlayerTrackHorseListener(
            final HorseChecker horseChecker,
            final HologramManager hologramManager,
            final HorseManager horseManager,
            final StateManager stateManager,
            final Converter converter,
            final ConfigFactory configFactory,
            final StatsHologram statsHologram,
            final NewStatsHologram newStatsHologram
    ) {
        this.horseChecker = horseChecker;
        this.hologramManager = hologramManager;
        this.horseManager = horseManager;
        this.stateManager = stateManager;
        this.converter = converter;
        this.configFactory = configFactory;
        this.statsHologram = statsHologram;
        this.newStatsHologram = newStatsHologram;
    }

    @EventHandler
    public void onTrackHorse(PlayerTrackEntityEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse
                && horseManager.isAllowedHorse(horse.getType())) {
            newStatsHologram.createHologram(event.getPlayer(), horse);
        }
    }

    @EventHandler
    public void onUnTrackHorse(PlayerUntrackEntityEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse
                && horseManager.isAllowedHorse(horse.getType())) {
            newStatsHologram.hideHologram(event.getPlayer(), horse);
        }
    }

    @EventHandler
    public void onMoveEntity(EntityMoveEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse
                && horseManager.isAllowedHorse(horse.getType())) {
            newStatsHologram.teleportHologram(horse);
        }
    }
}
