package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.packet.hologram.HologramHandler;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PlayerActionListener implements Listener {

    private final StateManager stateManager;
    private final HologramHandler hologramHandler;

    @Inject
    public PlayerActionListener(
            final StateManager stateManager,
            final HologramHandler hologramHandler
    ) {
        this.stateManager = stateManager;
        this.hologramHandler = hologramHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        showHologram(event.getPlayer());
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (event.getDismounted() instanceof AbstractHorse horse) {
            horse.getPassengers().forEach(passenger -> {
                if (passenger instanceof Player player) showHologram(player);
            });
        }
    }

    private void showHologram(Player player) {
        if (this.stateManager.state(player, "stats")) {
            this.hologramHandler.show(player);
        }
    }
}
