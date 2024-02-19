package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.packet.hologram.HologramHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ToggleCommand implements HorseCheckerCommand {

    private final CommandManager<CommandSender> commandManager;
    private final Messages messages;
    private final StateManager stateManager;
    private final HologramHandler hologramHandler;

    @Inject
    public ToggleCommand(
            CommandManager<CommandSender> commandManager,
            Messages messages,
            StateManager stateManager,
            HologramHandler hologramHandler
    ) {
        this.commandManager = commandManager;
        this.messages = messages;
        this.stateManager = stateManager;
        this.hologramHandler = hologramHandler;
    }

    @Override
    public void init() {
        final var toggle = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("toggle");

        this.commandManager.command(toggle.literal("stats")
                .permission("horsechecker.command.stats")
                .senderType(CommandSender.class)
                .handler(this::toggleStats)
        );

        this.commandManager.command(toggle.literal("breed")
                .permission("horsechecker.command.breed")
                .senderType(CommandSender.class)
                .handler(this::toggleBreed)
        );

        this.commandManager.command(toggle.literal("notification")
                .permission("horsechecker.command.notification")
                .senderType(CommandSender.class)
                .handler(this::toggleBreedNotification)
        );
    }

    private void toggleStats(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();

        if (this.stateManager.toggleState(sender, "stats")) {
            sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.toggle.success.show_stats_enable"));
            this.hologramHandler.show(sender);
        } else {
            sender.sendMessage(this.messages.translatable(Messages.Style.ERROR, sender, "command.toggle.success.show_stats_disable"));
            this.hologramHandler.cancel(sender);
        }
    }

    private void toggleBreed(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();

        if (this.stateManager.toggleState(sender, "breed")) {
            sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.toggle.success.cancel_breed_enable"));
        } else {
            sender.sendMessage(this.messages.translatable(Messages.Style.ERROR, sender, "command.toggle.success.cancel_breed_disable"));
        }
    }

    private void toggleBreedNotification(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();

        if (this.stateManager.toggleState(sender, "breed_notification")) {
            sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.notification.success.breed_notification_enable"));
        } else {
            sender.sendMessage(this.messages.translatable(Messages.Style.ERROR, sender, "command.notification.success.breed_notification_disable"));
        }
    }
}
