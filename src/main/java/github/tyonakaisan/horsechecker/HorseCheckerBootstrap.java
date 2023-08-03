package github.tyonakaisan.horsechecker;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings({"UnstableApiUsage", "unused"})
@DefaultQualifier(NonNull.class)
public final class HorseCheckerBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        // メソッド実装なし
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new HorseChecker(context.getDataDirectory(), context.getLogger());
    }
}