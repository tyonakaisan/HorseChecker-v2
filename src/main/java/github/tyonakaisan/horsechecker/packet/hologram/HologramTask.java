package github.tyonakaisan.horsechecker.packet.hologram;

import github.tyonakaisan.horsechecker.horse.WrappedHorse;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

@DefaultQualifier(NonNls.class)
public final class HologramTask extends BukkitRunnable {

    private final Player player;
    private final HologramManager hologramManager;
    private final int targetRange;

    private @Nullable Tameable targetedEntity;

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
        if (this.getTargetEntity() instanceof final Tameable tameable) {
            if (this.targetedEntity != null && !tameable.equals(this.targetedEntity)) {
                this.hologramManager.hide(this.targetedEntity, this.player);
                this.targetedEntity = null;
                return;
            }

            if (this.targetedEntity == null) {
                if (tameable instanceof final AbstractHorse horse) {
                    final var wrappedHorse = new WrappedHorse(horse);
                    this.hologramManager.create(wrappedHorse);
                    this.hologramManager.show(wrappedHorse, this.player, horse.getEntityId());
                } else {
                    this.hologramManager.create(tameable);
                    this.hologramManager.show(tameable, this.player, tameable.getEntityId());
                }

                this.targetedEntity = tameable;
            }
        } else {
            if (this.targetedEntity == null) {
                return;
            }

            this.hologramManager.hide(this.targetedEntity, this.player);
            this.targetedEntity = null;
        }
    }

    public void runTask(final Plugin plugin, final int delay, final int period) {
         this.runTaskTimer(plugin, delay, period);
    }

    public void cancelTask() {
        if (this.targetedEntity != null) {
            this.hologramManager.hide(this.targetedEntity, this.player);
        }
        this.cancel();
    }

    // 条件に合うエンティティが居無かった場合はempty
    private @Nullable Entity getTargetEntity() {
        final @Nullable RayTraceResult raytrace = this.player.rayTraceEntities(this.targetRange, false);
        if (raytrace == null) {
            return null;
        }

        final @Nullable Entity entity = raytrace.getHitEntity();
        if (entity == null) {
            return null;
        }

        if (entity.getPassengers().isEmpty()) {
            return entity;
        }

        return null;
    }
}
