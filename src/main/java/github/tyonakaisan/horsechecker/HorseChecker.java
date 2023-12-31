package github.tyonakaisan.horsechecker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tyonakaisan.glowlib.GlowLib;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.command.commands.DebugCommand;
import github.tyonakaisan.horsechecker.command.commands.NotificationCommand;
import github.tyonakaisan.horsechecker.command.commands.ReloadCommand;
import github.tyonakaisan.horsechecker.command.commands.ShareCommand;
import github.tyonakaisan.horsechecker.command.commands.ToggleCommand;
import github.tyonakaisan.horsechecker.listener.HorseBreedListener;
import github.tyonakaisan.horsechecker.listener.HorsePotionEffectListener;
import github.tyonakaisan.horsechecker.listener.PlayerActionListener;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.nio.file.Path;
import java.util.Set;

@DefaultQualifier(NonNull.class)
public final class HorseChecker extends JavaPlugin {

    private static final Set<Class<? extends Listener>> LISTENER_CLASSES = Set.of(
            PlayerActionListener.class,
            HorseBreedListener.class,
            HorsePotionEffectListener.class
    );
    private static final Set<Class<? extends HorseCheckerCommand>> COMMAND_CLASSES = Set.of(
            ReloadCommand.class,
            DebugCommand.class,
            ShareCommand.class,
            ToggleCommand.class,
            NotificationCommand.class
    );
    private final Injector injector;

    public HorseChecker(
            final Path dataDirectory,
            final ComponentLogger logger
    ) {
        this.injector = Guice.createInjector(new HorseCheckerModule(this, dataDirectory, logger));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Beta
        GlowLib.init(this);

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
}
