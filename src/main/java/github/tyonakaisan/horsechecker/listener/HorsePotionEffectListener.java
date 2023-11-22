package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.packet.HologramHandler;
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
    private final HologramHandler hologramHandler;

    @Inject
    public HorsePotionEffectListener(
            final HorseChecker horseChecker,
            final HologramHandler hologramHandler
    ) {
        this.horseChecker = horseChecker;
        this.hologramHandler = hologramHandler;
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    hologramHandler.changeHologramText(horse);
                }
            }.runTaskLater(this.horseChecker, 1);
        }
    }
}
