package github.tyonakaisan.horsechecker;

import com.google.inject.*;
import com.tyonakaisan.glowlib.GlowLib;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Set;

@DefaultQualifier(NonNull.class)
@Singleton
public final class HorseChecker extends JavaPlugin {

    private final Injector injector;

    @Inject
    public HorseChecker(
            final Injector bootstrapInjector
    ) {
        this.injector = bootstrapInjector.createChildInjector(new HorseCheckerModule(this));

        HorseCheckerProvider.register(this);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        GlowLib.init(this);

        // Listeners
        final Set<Listener> listeners = this.injector.getInstance(Key.get(new TypeLiteral<>() {}));
        listeners.forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        // Commands
        // final Set<HorseCheckerCommand> commands = this.injector.getInstance(Key.get(new TypeLiteral<>() {}));
        // commands.forEach(HorseCheckerCommand::init);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
