package github.tyonakaisan.horsechecker.command.commands;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.packet.hologram.HologramHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.PaperCommandManager;

@SuppressWarnings("UnstableApiUsage")
@DefaultQualifier(NonNull.class)
public final class ToggleCommand implements HorseCheckerCommand {

    private final PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager;
    private final Messages messages;
    private final StateManager stateManager;
    private final HologramHandler hologramHandler;

    @Inject
    public ToggleCommand(
            final PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager,
            final Messages messages,
            final StateManager stateManager,
            final HologramHandler hologramHandler
    ) {
        this.commandManager = commandManager;
        this.messages = messages;
        this.stateManager = stateManager;
        this.hologramHandler = hologramHandler;
    }

    @Override
    public void init() {
        final var toggle = this.commandManager.commandBuilder("horsechecker", "hc")
                .permission("horsechecker.command.toggle")
                .literal("toggle");

        this.commandManager.command(toggle.literal("stats")
                .permission("horsechecker.command.toggle.stats")
                .handler(this::toggleStats)
        );

        this.commandManager.command(toggle.literal("breed")
                .permission("horsechecker.command.toggle.breed")
                .handler(this::toggleBreed)
        );

        this.commandManager.command(toggle.literal("notification")
                .permission("horsechecker.command.toggle.notification")
                .handler(this::toggleBreedNotification)
        );
    }

    private void toggleStats(final @NonNull CommandContext<CommandSourceStack> context) {
        final var sender = context.sender().getSender();

        if (sender instanceof final Player player) {
            if (this.stateManager.toggleState(player, "stats")) {
                player.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, player, "command.toggle.success.show_stats_enable"));
                this.hologramHandler.show(player);
            } else {
                player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "command.toggle.success.show_stats_disable"));
                this.hologramHandler.cancel(player);
            }
        }
    }

    private void toggleBreed(final @NonNull CommandContext<CommandSourceStack> context) {
        final var sender = context.sender().getSender();

        if (sender instanceof final Player player) {
            if (this.stateManager.toggleState(player, "breed")) {
                player.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, player, "command.toggle.success.cancel_breed_enable"));
            } else {
                player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "command.toggle.success.cancel_breed_disable"));
            }
        }
    }

    private void toggleBreedNotification(final @NonNull CommandContext<CommandSourceStack> context) {
        final var sender = context.sender().getSender();

        if (sender instanceof final Player player) {
            if (this.stateManager.toggleState(player, "breed_notification")) {
                player.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, player, "command.notification.success.breed_notification_enable"));
            } else {
                player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "command.notification.success.breed_notification_disable"));
            }
        }
    }
}