package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.share.Share;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class ShareCommand implements HorseCheckerCommand {
    final HorseChecker horseChecker;
    final ConfigFactory configFactory;
    final CommandManager<CommandSender> commandManager;
    final Share share;

    @Inject
    public ShareCommand(
            HorseChecker horseChecker,
            ConfigFactory configFactory,
            CommandManager<CommandSender> commandManager,
            Share share
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.commandManager = commandManager;
        this.share = share;
    }

    @Override
    public void init() {
        final var command = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("share")
                .permission("horsechecker.command.share")
                .senderType(CommandSender.class)
                .handler(handler -> {
                    final var sender = (Player) handler.getSender();
                    share.broadcastShareMessage(sender);
                })
                .build();

        this.commandManager.command(command);
    }
}
