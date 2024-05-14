package github.tyonakaisan.horsechecker;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
@DefaultQualifier(NonNull.class)
public final class HorseCheckerBootstrap implements PluginBootstrap {

    private @MonotonicNonNull Injector injector;

    @Override
    public void bootstrap(BootstrapContext context) {
        this.injector = Guice.createInjector(new BootstrapModule(context));
        // this.injector.getInstance(CommandFactory.class).registerViaBootstrap(context);
    }

    @Override
    public @NotNull JavaPlugin createPlugin(PluginProviderContext context) {
        return new HorseChecker(this.injector);
    }
}
