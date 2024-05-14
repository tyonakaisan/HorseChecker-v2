package github.tyonakaisan.horsechecker.command;

import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import github.tyonakaisan.horsechecker.command.commands.ReloadCommand;
import github.tyonakaisan.horsechecker.command.commands.ShareCommand;
import github.tyonakaisan.horsechecker.command.commands.ToggleCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@DefaultQualifier(NonNull.class)
public final class CommandFactory {

    private final ReloadCommand reloadCommand;
    private final ShareCommand shareCommand;
    private final ToggleCommand toggleCommand;

    private static final LiteralArgumentBuilder<CommandSourceStack> FIRST_LITERAL_ARGUMENT = Commands.literal("horsechecker");
    private static final List<String> ALIASES = List.of("horse", "hc");

    @Inject
    public CommandFactory(
            final ReloadCommand reloadCommand,
            final ShareCommand shareCommand,
            final ToggleCommand toggleCommand
    ) {
        this.reloadCommand = reloadCommand;
        this.shareCommand = shareCommand;
        this.toggleCommand = toggleCommand;
    }

    public void registerViaBootstrap(final BootstrapContext context) {
        final LifecycleEventManager<BootstrapContext> lifecycleManager = context.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(this.bootstrapCommands(), null, ALIASES)
        );
    }

    public void registerViaEnable(final JavaPlugin plugin) {
        final LifecycleEventManager<Plugin> lifecycleManager = plugin.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(this.pluginCommands(), null, ALIASES)
        );
    }

    private LiteralCommandNode<CommandSourceStack> bootstrapCommands() {
        return FIRST_LITERAL_ARGUMENT
                .requires(source -> source.getSender().hasPermission("horsechecker.command"))
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> pluginCommands() {
        return FIRST_LITERAL_ARGUMENT
                .then(this.reloadCommand.init())
                .then(this.shareCommand.init())
                .then(this.toggleCommand.init())
                .build();

    }
}
