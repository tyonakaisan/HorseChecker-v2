package github.tyonakaisan.horsechecker;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.tyonakaisan.glowlib.GlowLib;
import github.tyonakaisan.horsechecker.command.CommandFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Set;

@DefaultQualifier(NonNull.class)
public final class HorseChecker extends JavaPlugin {

    private final Injector injector;

    public HorseChecker(
            final Injector bootstrapInjector
    ) {
        this.injector = bootstrapInjector.createChildInjector(new HorseCheckerModule(this));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        GlowLib.init(this);

        // Listeners
        final Set<Listener> listeners = this.injector.getInstance(Key.get(new TypeLiteral<>() {}));
        listeners.forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        // Commands
        this.injector.getInstance(CommandFactory.class).registerViaEnable(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
