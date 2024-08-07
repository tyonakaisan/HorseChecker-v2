package github.tyonakaisan.horsechecker.packet.hologram;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.HorseCheckerProvider;
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

    private final ConfigFactory configFactory;
    private final HologramManager hologramManager;
    private final StateManager stateManager;

    private final Map<UUID, HologramTask> hologramTaskMap = new HashMap<>();

    @Inject
    public HologramHandler(
            final ConfigFactory configFactory,
            final HologramManager hologramManager,
            final StateManager stateManager
    ) {
        this.configFactory = configFactory;
        this.hologramManager = hologramManager;
        this.stateManager = stateManager;
    }

    public void show(final Player player) {
        final var playerUUID = player.getUniqueId();

        if (this.stateManager.state(player, "stats") && !player.isInsideVehicle()) {
            this.hologramTaskMap.putIfAbsent(playerUUID, new HologramTask(player, this.configFactory.primaryConfig().horse().targetRange(), this.hologramManager));
            this.hologramTaskMap.get(playerUUID).runTask(HorseCheckerProvider.instance(), 0, this.configFactory.primaryConfig().hologram().taskInterval());
        }
    }

    public void cancel(final Player player) {
        final var playerUUID = player.getUniqueId();

        Optional.ofNullable(this.hologramTaskMap.get(playerUUID)).ifPresent(task -> {
            task.cancelTask();
            this.hologramTaskMap.remove(playerUUID);
        });
    }

    public void update(final AbstractHorse horse) {
        final var wrappedHorse = new WrappedHorse(horse);

        this.hologramManager.update(wrappedHorse.horse().getUniqueId().toString(), wrappedHorse);
    }
}
