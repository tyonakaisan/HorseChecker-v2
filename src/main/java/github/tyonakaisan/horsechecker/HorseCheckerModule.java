package github.tyonakaisan.horsechecker;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.packet.ProtocolLibHologramFactory;
import github.tyonakaisan.horsechecker.packet.holograms.HologramFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import java.nio.file.Path;
import java.util.function.Function;

public class HorseCheckerModule extends AbstractModule {
    private final Logger logger = LogManager.getLogger("HorseChecker");
    private final HorseChecker horseChecker;
    private final Path dataDirectory;

    HorseCheckerModule(
            final HorseChecker horseChecker,
            final Path dataDirectory
    ) {
        this.horseChecker = horseChecker;
        this.dataDirectory = dataDirectory;
    }

    @Provides
    @Singleton
    public CommandManager<CommandSender> commandManager() {
        final PaperCommandManager<CommandSender> commandManager;
        try {
            commandManager = new PaperCommandManager<>(
                    this.horseChecker,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (final Exception exception) {
            throw new RuntimeException("Failed to initialize command manager.", exception);
        }
        commandManager.registerAsynchronousCompletions();
        return commandManager;
    }

    @Provides
    @Singleton
    public NamespacedKey horseCheckerKey() {
        return new NamespacedKey(horseChecker, "HorseChecker");
    }

    @Override
    public void configure() {
        this.bind(Logger.class).toInstance(this.logger);
        this.bind(HorseChecker.class).toInstance(this.horseChecker);
        this.bind(Server.class).toInstance(this.horseChecker.getServer());
        this.bind(HologramFactory.class).to(ProtocolLibHologramFactory.class);
        this.bind(Path.class).toInstance(this.dataDirectory);
    }
}
