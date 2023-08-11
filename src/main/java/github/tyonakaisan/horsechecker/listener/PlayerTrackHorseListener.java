package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.horse.NewStatsHologram;
import github.tyonakaisan.horsechecker.manager.HorseManager;
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

    private final HorseManager horseManager;
    private final NewStatsHologram newStatsHologram;


    @Inject
    public PlayerTrackHorseListener(
            final HorseManager horseManager,
            final NewStatsHologram newStatsHologram
    ) {
        this.horseManager = horseManager;
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
