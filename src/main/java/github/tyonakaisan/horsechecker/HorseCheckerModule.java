package github.tyonakaisan.horsechecker;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.command.commands.ReloadCommand;
import github.tyonakaisan.horsechecker.command.commands.ShareCommand;
import github.tyonakaisan.horsechecker.command.commands.ToggleCommand;
import github.tyonakaisan.horsechecker.listener.HorseBreedListener;
import github.tyonakaisan.horsechecker.listener.HorseNameChangeListener;
import github.tyonakaisan.horsechecker.listener.HorsePotionEffectListener;
import github.tyonakaisan.horsechecker.listener.PlayerActionListener;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

@DefaultQualifier(NonNull.class)
public final class HorseCheckerModule extends AbstractModule {

    private final HorseChecker horseChecker;

    HorseCheckerModule(
            final HorseChecker horseChecker
    ) {
        this.horseChecker = horseChecker;
    }

    @Provides
    @Singleton
    public CommandManager<CommandSender> commandManager() {
        final PaperCommandManager<CommandSender> commandManager = new PaperCommandManager<>(
                this.horseChecker,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        commandManager.registerBrigadier();
        return commandManager;
    }

    @Override
    public void configure() {
        this.bind(HorseChecker.class).toInstance(this.horseChecker);
        this.bind(Server.class).toInstance(this.horseChecker.getServer());

        this.configureListeners();
        this.configureCommands();
    }

    private void configureListeners() {
        final Multibinder<Listener> listeners = Multibinder.newSetBinder(this.binder(), Listener.class);
        listeners.addBinding().to(HorseBreedListener.class).in(Scopes.SINGLETON);
        listeners.addBinding().to(HorsePotionEffectListener.class).in(Scopes.SINGLETON);
        listeners.addBinding().to(HorseNameChangeListener.class).in(Scopes.SINGLETON);
        listeners.addBinding().to(PlayerActionListener.class).in(Scopes.SINGLETON);
    }

    private void configureCommands() {
        final Multibinder<HorseCheckerCommand> commands = Multibinder.newSetBinder(this.binder(), HorseCheckerCommand.class);
        commands.addBinding().to(ReloadCommand.class).in(Scopes.SINGLETON);
        commands.addBinding().to(ShareCommand.class).in(Scopes.SINGLETON);
        commands.addBinding().to(ToggleCommand.class).in(Scopes.SINGLETON);
    }
}
