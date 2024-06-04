package github.tyonakaisan.horsechecker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings({"UnstableApiUsage", "unused"})
@DefaultQualifier(NonNull.class)
public final class HorseCheckerBootstrap implements PluginBootstrap {

    private @MonotonicNonNull Injector injector;

    @Override
    public void bootstrap(BootstrapContext context) {
        this.injector = Guice.createInjector(new BootstrapModule(context));

        // Commands
        final Set<HorseCheckerCommand> commands = this.injector.getInstance(Key.get(new TypeLiteral<>() {}));
        commands.forEach(HorseCheckerCommand::init);
    }

    @Override
    public @NotNull JavaPlugin createPlugin(PluginProviderContext context) {
        return new HorseChecker(this.injector);
    }
}
