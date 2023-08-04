package github.tyonakaisan.horsechecker.horse.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.horse.ShowStats;
import github.tyonakaisan.horsechecker.manager.StateManager;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class PlayerJoinListener implements Listener {

    private final StateManager stateManager;
    private final ShowStats showStats;

    @Inject
    public PlayerJoinListener(
            final StateManager stateManager,
            final ShowStats showStats
            ) {
        this.stateManager = stateManager;
        this.showStats = showStats;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (this.stateManager.isState(event.getPlayer(), "stats")) {
            showStats.showStatsStart(event.getPlayer());
        }
    }
}
