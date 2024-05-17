package github.tyonakaisan.horsechecker.command.commands;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.packet.hologram.HologramManager;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.CommandManager;

@DefaultQualifier(NonNull.class)
public final class ReloadCommand implements HorseCheckerCommand {

    private final ConfigFactory configFactory;
    private final Messages messages;
    private final HologramManager hologramManager;
    private final CommandManager<CommandSender> commandManager;

    @Inject
    public ReloadCommand(
            final ConfigFactory configFactory,
            final Messages messages,
            final HologramManager hologramManager,
            final CommandManager<CommandSender> commandManager
    ) {
        this.configFactory = configFactory;
        this.messages = messages;
        this.hologramManager = hologramManager;
        this.commandManager = commandManager;
    }

    @Override
    public void init() {
        final var command = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("reload")
                .permission("horsechecker.command.reload")
                .senderType(CommandSender.class)
                .handler(handler -> {
                    final var sender = handler.sender();
                    this.configFactory.reloadPrimaryConfig();
                    this.messages.reloadMessage();
                    this.hologramManager.destroyAllHologram();
                    sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.reload.success"));
                })
                .build();

        this.commandManager.command(command);
    }
}