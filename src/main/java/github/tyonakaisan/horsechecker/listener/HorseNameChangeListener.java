package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.packet.hologram.HologramHandler;
import io.papermc.paper.event.player.PlayerNameEntityEvent;
import org.bukkit.Server;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class HorseNameChangeListener implements Listener {

    private final HorseChecker horseChecker;
    private final Server server;
    private final HologramHandler hologramHandler;

    @Inject
    public HorseNameChangeListener(
            final HorseChecker horseChecker,
            final Server server,
            final HologramHandler hologramHandler
    ) {
        this.horseChecker = horseChecker;
        this.server = server;
        this.hologramHandler = hologramHandler;
    }

    @EventHandler
    public void onNameChange(final PlayerNameEntityEvent event) {
        if (event.getEntity() instanceof final AbstractHorse horse) {
            this.server.getScheduler()
                    .runTaskLater(
                            this.horseChecker,
                            () -> this.hologramHandler.update(horse),
                            1L);
        }
    }
}
