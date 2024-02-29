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
            if (!newTargetedHorse.getPassengers().isEmpty()) {
                return;
            }

            if (this.targetedHorse == null) {
                this.targetedHorse = newTargetedHorse;
                var newWrappedHorse = new WrappedHorse(newTargetedHorse);

                this.hologramManager.createHologram(newWrappedHorse);
                this.hologramManager.showHologram(newWrappedHorse, this.player, newTargetedHorse.getEntityId());
                return;
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

    public void runTask(final Plugin plugin, final int delay, final int period) {
         this.runTaskTimer(plugin, delay, period);
    }

    public void cancelTask() {
        if (this.targetedHorse != null) {
            this.hologramManager.hideHologram(new WrappedHorse(this.targetedHorse), this.player);
        }
        this.cancel();
    }

    // 条件に合う馬が居無かった場合はempty
    private Optional<AbstractHorse> getTargetHorse() {
        if (this.player.getTargetEntity(this.targetRange, false) instanceof final AbstractHorse horse) {
            return horse.getPassengers().isEmpty()
                    ? Optional.of(horse)
                    : Optional.empty();
        }
        return Optional.empty();
    }
}
