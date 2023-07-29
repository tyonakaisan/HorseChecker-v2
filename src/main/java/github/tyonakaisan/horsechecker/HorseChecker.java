package github.tyonakaisan.horsechecker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.command.commands.DebugCommand;
import github.tyonakaisan.horsechecker.command.commands.ReloadCommand;
import github.tyonakaisan.horsechecker.command.commands.ShareCommand;
import github.tyonakaisan.horsechecker.command.commands.ToggleCommand;
import github.tyonakaisan.horsechecker.event.HorseCheckerEventHandler;
import github.tyonakaisan.horsechecker.horse.listener.HorseCancelBreedListener;
import github.tyonakaisan.horsechecker.horse.listener.HorseStatsListener;
import github.tyonakaisan.horsechecker.packet.ProtocolLibHologramFactory;
import github.tyonakaisan.horsechecker.packet.holograms.HologramFactory;
import github.tyonakaisan.horsechecker.packet.holograms.HologramManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.nio.file.Path;
import java.util.Set;


@Singleton
@DefaultQualifier(NonNull.class)
public final class HorseChecker extends JavaPlugin {

    private static final Set<Class<? extends Listener>> LISTENER_CLASSES = Set.of(
            HorseStatsListener.class,
            HorseCancelBreedListener.class
    );
    private static final Set<Class<? extends HorseCheckerCommand>> COMMAND_CLASSES = Set.of(
            ReloadCommand.class,
            DebugCommand.class,
            ShareCommand.class,
            ToggleCommand.class
    );
    private final HorseCheckerEventHandler eventHandler = new HorseCheckerEventHandler();
    private @MonotonicNonNull Injector injector;
    private @MonotonicNonNull Logger logger;

    @Override
    public void onLoad() {
        this.injector = Guice.createInjector(new HorseCheckerModule(this, this.dataDirectory()));
        this.logger = LogManager.getLogger("HorseChecker-v2");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        HologramFactory protocolLibFactory = new ProtocolLibHologramFactory();
        HologramManager hologramManager = new HologramManager(protocolLibFactory);

        // Listeners
        for (final Class<? extends Listener> listenerClass : LISTENER_CLASSES) {
            var listener = this.injector.getInstance(listenerClass);
            this.getServer().getPluginManager().registerEvents(listener, this);
        }

        // Commands
        for (final Class<? extends HorseCheckerCommand> commandClass : COMMAND_CLASSES) {
            var command = this.injector.getInstance(commandClass);
            command.init();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Logger logger() {
        return this.logger;
    }

    public Path dataDirectory() {
        return this.getDataFolder().toPath();
    }

    public @NonNull HorseCheckerEventHandler eventHandler() {
        return this.eventHandler;
    }
}
