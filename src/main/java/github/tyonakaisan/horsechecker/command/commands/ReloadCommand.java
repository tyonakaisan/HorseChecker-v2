package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.utils.Messages;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class ReloadCommand implements HorseCheckerCommand {

    final HorseChecker horseChecker;
    final ConfigFactory configFactory;
    final CommandManager<CommandSender> commandManager;

    @Inject
    public ReloadCommand(
            HorseChecker horseChecker,
            ConfigFactory configFactory,
            CommandManager<CommandSender> commandManager
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.commandManager = commandManager;
    }

    @Override
    public void init() {
        final var command = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("reload")
                .permission("horsechecker.command.reload")
                .senderType(CommandSender.class)
                .handler(context -> {
                    this.configFactory.reloadPrimaryConfig();
                    context.getSender().sendRichMessage(Messages.CONFIG_RELOAD.getMessageWithPrefix());
                })
                .build();

        this.commandManager.command(command);
    }
}
