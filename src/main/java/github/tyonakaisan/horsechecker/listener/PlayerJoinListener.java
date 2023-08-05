package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.horse.StatsHologram;
import github.tyonakaisan.horsechecker.manager.StateManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PlayerJoinListener implements Listener {

    private final StateManager stateManager;
    private final StatsHologram statsHologram;

    @Inject
    public PlayerJoinListener(
            final StateManager stateManager,
            final StatsHologram statsHologram
            ) {
        this.stateManager = stateManager;
        this.statsHologram = statsHologram;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (this.stateManager.state(event.getPlayer(), "stats")) {
            statsHologram.show(event.getPlayer());
        }
    }
}
