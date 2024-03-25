package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.packet.hologram.HologramHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PlayerActionListener implements Listener {
    private final HologramHandler hologramHandler;

    @Inject
    public PlayerActionListener(
            final HologramHandler hologramHandler
    ) {
        this.hologramHandler = hologramHandler;
    }

    @EventHandler
    public void onMount(final EntityMountEvent event) {
        if (event.getEntity() instanceof final Player player) {
            this.hologramHandler.cancel(player);
        }
    }

    @EventHandler
    public void onDismount(final EntityDismountEvent event) {
        if (event.getEntity() instanceof final Player player) {
            this.hologramHandler.show(player);
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        this.hologramHandler.show(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.hologramHandler.cancel(event.getPlayer());
    }
}
