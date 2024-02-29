package github.tyonakaisan.horsechecker.packet.hologram;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.WrappedHorse;
import github.tyonakaisan.horsechecker.manager.StateManager;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
@DefaultQualifier(NonNull.class)
public final class HologramHandler {

    private final HorseChecker horseChecker;
    private final ConfigFactory configFactory;
    private final HologramManager hologramManager;
    private final StateManager stateManager;

    private final Map<UUID, HologramTask> hologramTask = new HashMap<>();

    @Inject
    public HologramHandler(
            final HorseChecker horseChecker,
            final ConfigFactory configFactory,
            final HologramManager hologramManager,
            final StateManager stateManager
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.hologramManager = hologramManager;
        this.stateManager = stateManager;
    }

    public void show(final Player player) {
        if (this.stateManager.state(player, "stats") && !player.isInsideVehicle()) {
            this.hologramTask.putIfAbsent(player.getUniqueId(), new HologramTask(player, this.configFactory.primaryConfig().horse().targetRange(), this.hologramManager));
            this.hologramTask.get(player.getUniqueId()).runTask(this.horseChecker, 0, this.configFactory.primaryConfig().hologram().displayHologramTaskInterval());
        }
    }

    public void cancel(final Player player) {
        Optional.ofNullable(this.hologramTask.get(player.getUniqueId())).ifPresent(task -> {
            task.cancelTask();
            this.hologramTask.remove(player.getUniqueId());
        });
    }

    public void update(final AbstractHorse horse) {
        final var wrappedHorse = new WrappedHorse(horse);

        this.hologramManager.updateHologram(wrappedHorse.horse().getUniqueId().toString(), wrappedHorse);
    }
}
