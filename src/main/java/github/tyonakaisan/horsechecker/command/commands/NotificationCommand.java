package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class NotificationCommand implements HorseCheckerCommand {

    private final CommandManager<CommandSender> commandManager;
    private final Messages messages;
    private final StateManager stateManager;

    @Inject
    public NotificationCommand(
            CommandManager<CommandSender> commandManager,
            Messages messages,
            StateManager stateManager
    ) {
        this.commandManager = commandManager;
        this.messages = messages;
        this.stateManager = stateManager;
    }

    @Override
    public void init() {
        final var toggle = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("notification");

        this.commandManager.command(toggle.literal("breed")
                .permission("horsechecker.command.breednotification")
                .senderType(CommandSender.class)
                .handler(this::toggleBreedNotification)
        );
    }

    private void toggleBreedNotification(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();

        if (this.stateManager.toggleState(sender, "breed_notification")) {
            sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.notification.success.breed_notification_enable"));
        } else {
            sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.notification.success.breed_notification_disable"));
        }
    }
}
