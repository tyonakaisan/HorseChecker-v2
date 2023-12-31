package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import cloud.commandframework.bukkit.parsers.selector.MultiplePlayerSelectorArgument;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.horse.Share;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ShareCommand implements HorseCheckerCommand {

    private final CommandManager<CommandSender> commandManager;
    private final Share share;

    @Inject
    public ShareCommand(CommandManager<CommandSender> commandManager, Share share) {
        this.commandManager = commandManager;
        this.share = share;
    }

    @Override
    public void init() {
        final var command = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("share")
                .argument(MultiplePlayerSelectorArgument.of("player"))
                .permission("horsechecker.command.share")
                .senderType(CommandSender.class)
                .handler(handler -> {
            final var sender = (Player) handler.getSender();
            final MultiplePlayerSelector target = handler.get("player");
            this.share.broadcastShareMessage(sender, target);
        }).build();

        this.commandManager.command(command);
    }
}
