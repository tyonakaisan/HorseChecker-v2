package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.packet.hologram.HologramHandler;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PlayerActionListener implements Listener {

    private final ConfigFactory configFactory;
    private final HologramHandler hologramHandler;

    @Inject
    public PlayerActionListener(
            final ConfigFactory configFactory,
            final HologramHandler hologramHandler
    ) {
        this.configFactory = configFactory;
        this.hologramHandler = hologramHandler;
    }

    @EventHandler
    public void onMount(final EntityMountEvent event) {
        if (this.configFactory.primaryConfig().experimental().alignHorseDirectionToPlayer()
                && event.getEntity() instanceof final Player player
                && event.getMount() instanceof AbstractHorse) {
            final var yaw = player.getYaw();
            final var pitch = player.getPitch();
            player.setRotation(yaw, pitch);
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
