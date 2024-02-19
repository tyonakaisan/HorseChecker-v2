package github.tyonakaisan.horsechecker.packet.hologram;

import github.tyonakaisan.horsechecker.horse.WrappedHorse;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class HologramTask extends BukkitRunnable {

    private final Player player;
    private final HologramManager hologramManager;
    private final int targetRange;

    private @Nullable AbstractHorse targetedHorse;

    public HologramTask(
            final Player player,
            final int targetRange,
            final HologramManager hologramManager
            ) {
        this.player = player;
        this.targetRange = targetRange;
        this.hologramManager = hologramManager;
    }

    @Override
    public void run() {
        this.getTargetHorse().ifPresentOrElse(newTargetedHorse -> {
            if (this.targetedHorse == null) {
                this.targetedHorse = newTargetedHorse;
                var newWrappedHorse = new WrappedHorse(newTargetedHorse);

                this.hologramManager.createHologram(newWrappedHorse);
                this.hologramManager.showHologram(newWrappedHorse, this.player, newTargetedHorse.getEntityId());
            }

            if (!this.targetedHorse.equals(newTargetedHorse)) {
                var oldWrappedHorse = new WrappedHorse(this.targetedHorse);

                this.hologramManager.hideHologram(oldWrappedHorse, this.player);
                this.targetedHorse = null;
            }
        }, () -> {
            if (this.targetedHorse != null) {
                this.hologramManager.hideHologram(new WrappedHorse(this.targetedHorse), this.player);
                this.targetedHorse = null;
            }
        });
    }

    public void runTask(Plugin plugin, int delay, int period) {
         this.runTaskTimer(plugin, delay, period);
    }

    public void cancelTask() {
        if (this.targetedHorse != null) {
            this.hologramManager.hideHologram(new WrappedHorse(this.targetedHorse), this.player);
        }
        this.cancel();
    }

    private Optional<AbstractHorse> getTargetHorse() {
        return this.player.getTargetEntity(this.targetRange, false) instanceof AbstractHorse horse
                ? Optional.of(horse)
                : Optional.empty();
    }
}
