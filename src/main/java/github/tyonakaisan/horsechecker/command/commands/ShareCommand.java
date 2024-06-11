package github.tyonakaisan.horsechecker.command.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.horse.Share;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DefaultQualifier(NonNull.class)
@SuppressWarnings("UnstableApiUsage")
public final class ShareCommand implements HorseCheckerCommand {

    private final Share share;
    private final ComponentLogger logger;

    @Inject
    public ShareCommand(
            final Share share,
            final ComponentLogger logger
    ) {
        this.share = share;
        this.logger = logger;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> init() {
        return Commands.literal("share")
                .requires(source -> source.getSender().hasPermission("horsechecker.command.share"))
                .then(Commands.argument("player", ArgumentTypes.players())
                        .suggests(this::getSelectorListSuggestions)
                        .executes(source -> {
                            this.setBypassSelectorPermissionsField(source);
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

    // baka
    @SuppressWarnings("java:S3011")
    private void setBypassSelectorPermissionsField(final CommandContext<CommandSourceStack> context) {
        final var source = context.getSource();
        try {
            source.getClass().getField("bypassSelectorPermissions").setBoolean(source, true);
        } catch (ReflectiveOperationException e) {
            this.logger.error("Failed to reflection bypassSelectorPermissions.", e);
        }
    }

    // baka
    @SuppressWarnings("unchecked")
    private CompletableFuture<Suggestions> getSelectorListSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder suggestionsBuilder) {
        try {
            final var entityArgumentClass = Class.forName("net.minecraft.commands.arguments.EntityArgument");
            this.setBypassSelectorPermissionsField(context);
            return (CompletableFuture<Suggestions>) entityArgumentClass.getMethod(
                    "listSuggestions",
                    CommandContext.class,
                    SuggestionsBuilder.class
            ).invoke(
                    entityArgumentClass.getMethod("entities").invoke(null),
                    context,
                    suggestionsBuilder.restart()
            );
        } catch (ReflectiveOperationException e) {
            this.logger.error("Failed to reflection.", e);
        }
        return suggestionsBuilder.buildFuture();
    }
}