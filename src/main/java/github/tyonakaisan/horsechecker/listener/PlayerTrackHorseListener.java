package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.packet.HologramHandler;
import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.event.player.PlayerTrackEntityEvent;
import io.papermc.paper.event.player.PlayerUntrackEntityEvent;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class PlayerTrackHorseListener implements Listener {

    private final HologramHandler hologramHandler;


    @Inject
    public PlayerTrackHorseListener(
            final HologramHandler hologramHandler
    ) {
        this.hologramHandler = hologramHandler;
    }

    @EventHandler
    public void onTrackHorse(PlayerTrackEntityEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse) {
            hologramHandler.createOrShowHologram(event.getPlayer(), horse);
        }
    }

    @EventHandler
    public void onUnTrackHorse(PlayerUntrackEntityEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse) {
            hologramHandler.hideHologram(event.getPlayer(), horse);
        }
    }

    @EventHandler
    public void onMoveEntity(EntityMoveEvent event) {
        if (event.getEntity() instanceof AbstractHorse horse) {
            hologramHandler.teleportHologram(horse);
        }
    }
}
