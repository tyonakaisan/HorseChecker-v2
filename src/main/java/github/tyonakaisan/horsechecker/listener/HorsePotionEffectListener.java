package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.horse.StatsHologram;
import github.tyonakaisan.horsechecker.packet.holograms.HologramManager;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class HorsePotionEffectListener implements Listener {

    private final HorseChecker horseChecker;
    private final StatsHologram statsHologram;
    private final HologramManager hologramManager;


    @Inject
    public HorsePotionEffectListener(
            final HorseChecker horseChecker,
            final StatsHologram statsHologram,
            final HologramManager hologramManager
    ) {
        this.horseChecker = horseChecker;
        this.statsHologram = statsHologram;
        this.hologramManager = hologramManager;
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse
                && hologramManager.getHologramNames().contains(horse.getUniqueId().toString())
        ) {
            //雑すぎコード2023受賞
            //jump->他のポーションの順でやるとjumpの値戻るけど直すのめんどいしパス
            PotionEffect effect;
            if (event.getNewEffect() == null) {
                effect = new PotionEffect(PotionEffectType.GLOWING, 1, 1);
            } else {
                effect = event.getNewEffect();
            }
            new BukkitRunnable() {
                public void run() {
                    statsHologram.changeHologramText(horse, effect);
                }
            }.runTaskLater(horseChecker, 1);
        }
    }
}
