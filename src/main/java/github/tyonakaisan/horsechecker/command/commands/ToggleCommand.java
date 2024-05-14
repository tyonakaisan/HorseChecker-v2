package github.tyonakaisan.horsechecker.command.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.packet.hologram.HologramHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@SuppressWarnings("UnstableApiUsage")
public final class ToggleCommand implements HorseCheckerCommand {

    private final Messages messages;
    private final StateManager stateManager;
    private final HologramHandler hologramHandler;

    @Inject
    public ToggleCommand(
            final Messages messages,
            final StateManager stateManager,
            final HologramHandler hologramHandler
    ) {
        this.messages = messages;
        this.stateManager = stateManager;
        this.hologramHandler = hologramHandler;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> init() {
        return Commands.literal("toggle")
                .requires(source -> source.getSender().hasPermission("horsechecker.command.toggle"))
                .then(Commands.literal("stats")
                        .requires(source -> source.getSender().hasPermission("horsechecker.command.toggle.stats"))
                        .executes(this::toggleStats))
                .then(Commands.literal("breed")
                        .requires(source -> source.getSender().hasPermission("horsechecker.command.toggle.breed"))
                        .executes(this::toggleBreed))
                .then(Commands.literal("notification")
                        .requires(source -> source.getSender().hasPermission("horsechecker.command.toggle.notification"))
                        .executes(this::toggleBreedNotification));
    }

    private int toggleStats(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();

        if (sender instanceof final Player player) {
            if (this.stateManager.toggleState(player, "stats")) {
                player.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, player, "command.toggle.success.show_stats_enable"));
                this.hologramHandler.show(player);
            } else {
                player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "command.toggle.success.show_stats_disable"));
                this.hologramHandler.cancel(player);
            }
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendRichMessage("This command can only be used by the player.");
            return 0;
        }
    }

    private int toggleBreed(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();

        if (sender instanceof final Player player) {
            if (this.stateManager.toggleState(player, "breed")) {
                player.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, player, "command.toggle.success.cancel_breed_enable"));
            } else {
                player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "command.toggle.success.cancel_breed_disable"));
            }
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendRichMessage("This command can only be used by the player.");
            return 0;
        }
    }

    private int toggleBreedNotification(final CommandContext<CommandSourceStack> context) {
        final var sender = context.getSource().getSender();

        if (sender instanceof final Player player) {
            if (this.stateManager.toggleState(player, "breed_notification")) {
                player.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, player, "command.notification.success.breed_notification_enable"));
            } else {
                player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "command.notification.success.breed_notification_disable"));
            }
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendRichMessage("This command can only be used by the player.");
            return 0;
        }
    }
}
