package github.tyonakaisan.horsechecker.command.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.packet.hologram.HologramManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@SuppressWarnings("UnstableApiUsage")
public final class ReloadCommand implements HorseCheckerCommand {

    private final ConfigFactory configFactory;
    private final Messages messages;
    private final HologramManager hologramManager;

    @Inject
    public ReloadCommand(
            final ConfigFactory configFactory,
            final Messages messages,
            final HologramManager hologramManager
    ) {
        this.configFactory = configFactory;
        this.messages = messages;
        this.hologramManager = hologramManager;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> init() {
        return Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission("horsechecker.command.reload"))
                .executes(context -> {
                    final CommandSender sender = context.getSource().getSender();
                    this.configFactory.reloadPrimaryConfig();
                    this.messages.reloadMessage();
                    this.hologramManager.destroyAllHologram();

                    sender.sendMessage(this.messages.translatable(Messages.Style.SUCCESS, sender, "command.reload.success"));
                    return Command.SINGLE_SUCCESS;
                });
    }
}
