package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.horse.StatsHologram;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class HorsePotionEffectListener implements Listener {

    private final HorseChecker horseChecker;
    private final StatsHologram statsHologram;


    @Inject
    public HorsePotionEffectListener(
            final HorseChecker horseChecker,
            final StatsHologram statsHologram
    ) {
        this.horseChecker = horseChecker;
        this.statsHologram = statsHologram;
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    statsHologram.changeHologramText(horse);
                }
            }.runTaskLater(horseChecker, 1);
        }
    }
}
