package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.ShowStats;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public class ToggleCommand implements HorseCheckerCommand {
    final HorseChecker horseChecker;
    final ConfigFactory configFactory;
    final CommandManager<CommandSender> commandManager;
    final StateManager stateManager;
    final ShowStats showStats;

    @Inject
    public ToggleCommand(
            HorseChecker horseChecker,
            ConfigFactory configFactory,
            CommandManager<CommandSender> commandManager,
            StateManager stateManager,
            ShowStats showStats
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.commandManager = commandManager;
        this.stateManager = stateManager;
        this.showStats = showStats;
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
    }

    private void toggleStats(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();

        if (stateManager.toggleState(sender, "stats")) {
            sender.sendRichMessage(Messages.SHOW_STATS_ENABLED.getMessageWithPrefix());
            showStats.showStatsStart(sender);
        } else {
            sender.sendRichMessage(Messages.SHOW_STATS_DISABLED.getMessageWithPrefix());
        }
    }

    private void toggleBreed(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();

        if (stateManager.toggleState(sender, "breed")) {
            sender.sendRichMessage(Messages.CANCEL_BREEDING_ENABLED.getMessageWithPrefix());
        } else {
            sender.sendRichMessage(Messages.CANCEL_BREEDING_DISABLED.getMessageWithPrefix());
        }
    }
}
