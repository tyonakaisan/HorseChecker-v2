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
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import java.nio.file.Path;

@DefaultQualifier(NonNull.class)
public final class BootstrapModule extends AbstractModule {

    private final BootstrapContext context;

    BootstrapModule(
            final BootstrapContext context
    ) {
        this.context = context;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Provides
    @Singleton
    public PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager() {
        return PaperCommandManager.builder()
                        .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                                .buildBootstrapped(this.context);
    }

    @Override
    public void configure() {
        this.bind(ComponentLogger.class).toInstance(this.context.getLogger());
        this.bind(Path.class).toInstance(this.context.getDataDirectory());
        this.configureCommands();
    }

    private void configureCommands() {
        final Multibinder<HorseCheckerCommand> commands = Multibinder.newSetBinder(this.binder(), HorseCheckerCommand.class);
        commands.addBinding().to(ReloadCommand.class).in(Scopes.SINGLETON);
        commands.addBinding().to(ShareCommand.class).in(Scopes.SINGLETON);
        commands.addBinding().to(ToggleCommand.class).in(Scopes.SINGLETON);
    }
}
