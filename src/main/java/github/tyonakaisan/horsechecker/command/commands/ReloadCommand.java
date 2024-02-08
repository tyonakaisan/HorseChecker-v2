package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ReloadCommand implements HorseCheckerCommand {

    private final ConfigFactory configFactory;
    private final Messages messages;
    private final CommandManager<CommandSender> commandManager;

    @Inject
    public ReloadCommand(
            ConfigFactory configFactory,
            Messages messages,
            CommandManager<CommandSender> commandManager
    ) {
        this.configFactory = configFactory;
        this.messages = messages;
        this.commandManager = commandManager;
    }

    @Override
    public void init() {
        final var command = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("reload")
                .permission("horsechecker.command.reload")
                .senderType(CommandSender.class)
                .handler(handler -> {
                    var sender = handler.getSender();
                    this.configFactory.reloadPrimaryConfig();
                    this.messages.reloadMessage();
                    sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.reload.success"));
                })
                .build();

        this.commandManager.command(command);
    }
}
