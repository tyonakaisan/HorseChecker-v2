package github.tyonakaisan.horsechecker.command.commands;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.horse.Share;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

@DefaultQualifier(NonNull.class)
public final class ShareCommand implements HorseCheckerCommand {

    private final CommandManager<CommandSender> commandManager;
    private final Share share;

    @Inject
    public ShareCommand(final CommandManager<CommandSender> commandManager, final Share share) {
        this.commandManager = commandManager;
        this.share = share;
    }

    @Override
    public void init() {
        final var command = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("share")
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .permission("horsechecker.command.share")
                .senderType(CommandSender.class)
                .handler(handler -> {
                    if (handler.sender() instanceof final Player player) {
                        final Selector<Player> target = handler.get("player");
                        this.share.broadcastShareMessage(player, target.values());
                    } else {
                        //TODO: Error message
                    }
                }).build();

        this.commandManager.command(command);
    }
}
