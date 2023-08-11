package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.horse.StatsHologram;
import github.tyonakaisan.horsechecker.manager.StateManager;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spigotmc.event.entity.EntityDismountEvent;

@DefaultQualifier(NonNull.class)
public final class PlayerDismountListener implements Listener {

    private final StateManager stateManager;
    private final StatsHologram statsHologram;

    @Inject
    public PlayerDismountListener(
            final StateManager stateManager,
            final StatsHologram statsHologram
    ) {
        this.stateManager = stateManager;
        this.statsHologram = statsHologram;
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {

        if (event.getDismounted() instanceof AbstractHorse horse) {
            horse.getPassengers().forEach(passenger -> {
                if (passenger instanceof Player player &&
                        this.stateManager.state(player, "stats")) {
                    statsHologram.show(player);
                }
            });
        }
    }
}
