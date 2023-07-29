package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.utils.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;

@DefaultQualifier(NonNull.class)
public class DebugCommand implements HorseCheckerCommand {
    final HorseChecker horseChecker;
    final ConfigFactory configFactory;
    final CommandManager<CommandSender> commandManager;

    @Inject
    public DebugCommand(
            HorseChecker horseChecker,
            ConfigFactory configFactory,
            CommandManager<CommandSender> commandManager
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.commandManager = commandManager;
    }
    @Override
    public void init() {
        final var debug = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("debug");

        this.commandManager.command(debug.literal("spawnrandomhorse")
                .argument(IntegerArgument.optional("time"))
                .argument(DoubleArgument.optional("addspeed"))
                .argument(DoubleArgument.optional("addjump"))
                .permission("horsechecker.command.spawnrandomhorse")
                .senderType(CommandSender.class)
                .handler(this::spawnRandomHorse)
        );
    }

    private void spawnRandomHorse(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();
        final int[] time = {(int) context.getOptional("time").orElse(1)};
        double addSpeed = (double) context.getOptional("addspeed").orElse(0.0);
        double addJump = (double) context.getOptional("addjump").orElse(0.0);
        final var random = new Random();

        new BukkitRunnable() {
            @Override
            public void run() {
                time[0]--;
                if (time[0] <= 0) {
                        this.cancel();
                }
                AbstractHorse horse = (AbstractHorse) sender.getWorld().spawnEntity(sender.getLocation(), EntityType.HORSE);
                DecimalFormat df = new DecimalFormat("###.##");
                double speed = random.nextDouble(0.225) + 0.1125 + addSpeed;
                double jump = random.nextDouble(0.6) + 0.4 + addJump;

                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(speed);
                horse.setJumpStrength(jump);
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        Messages.SPAWN_HORSE.getMessageWithPrefix(),
                        Placeholder.parsed("speed", df.format(speed)),
                        Placeholder.parsed("jump", df.format(jump))
                ));
            }
        }.runTaskTimer(horseChecker, 0, 1);
    }
}
