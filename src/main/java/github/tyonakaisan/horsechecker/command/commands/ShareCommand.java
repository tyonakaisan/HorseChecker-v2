package github.tyonakaisan.horsechecker.command.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.horse.Share;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
@SuppressWarnings("UnstableApiUsage")
public final class ShareCommand implements HorseCheckerCommand {

    private final Share share;

    @Inject
    public ShareCommand(
            final Share share
    ) {
        this.share = share;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> init() {
        return Commands.literal("share")
                .requires(source -> source.getSender().hasPermission("horsechecker.command.share"))
                .then(Commands.argument("player", ArgumentTypes.players())
                        .executes(source -> {
                            final List<Player> targets = source.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(source.getSource());

                            if (source.getSource().getSender() instanceof final Player player) {
                                this.share.broadcastShareMessage(player, targets);
                                return Command.SINGLE_SUCCESS;
                            } else {
                                source.getSource().getSender().sendRichMessage("This command can only be used by the player.");
                                return 0;
                            }
                        }));
    }
}
