package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.message.Messages;
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
public final class DebugCommand implements HorseCheckerCommand {
    private final HorseChecker horseChecker;
    private final ConfigFactory configFactory;
    private final HorseManager horseManager;
    private final CommandManager<CommandSender> commandManager;

    private final int DEBUG_FIRST_TARGET_RANGE;
    private final int DEBUG_THIRD_TARGET_RANGE;

    @Inject
    public DebugCommand(
            HorseChecker horseChecker,
            ConfigFactory configFactory,
            HorseManager horseManager,
            CommandManager<CommandSender> commandManager
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.horseManager = horseManager;
        this.commandManager = commandManager;

        this.DEBUG_FIRST_TARGET_RANGE = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();
        this.DEBUG_THIRD_TARGET_RANGE = horseManager.targetRange();
    }
    @Override
    public void init() {
        final var debug = this.commandManager.commandBuilder("horsechecker", "hc")
                .literal("debug");


        this.commandManager.command(debug.literal("spawnRandomHorse")
                .argument(IntegerArgument.optional("time"))
                .argument(EnumArgument.optional(HorseType.class, "horseType"))
                .argument(BooleanArgument.optional("tame"))
                .argument(DoubleArgument.optional("addSpeed"))
                .argument(DoubleArgument.optional("addJump"))
                .permission("horsechecker.command.spawnrandomhorse")
                .senderType(CommandSender.class)
                .handler(this::spawnRandomHorse)
        );

        this.commandManager.command(debug.literal("targetRange")
                .permission("horsechecker.command.targetRange")
                .senderType(CommandSender.class)
                .handler(this::targetRange)
        );
    }

    private void targetRange(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();
        final var DEBUG_SECOND_TARGET_RANGE = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();
        final var DEBUG_FOURTH_TARGET_RANGE = this.horseManager.targetRange();

        sender.sendRichMessage("1 -> " + this.DEBUG_FIRST_TARGET_RANGE);
        sender.sendRichMessage("2 -> " + DEBUG_SECOND_TARGET_RANGE);
        sender.sendRichMessage("3 -> " + DEBUG_THIRD_TARGET_RANGE);
        sender.sendRichMessage("4 -> " + DEBUG_FOURTH_TARGET_RANGE);
    }

    private void spawnRandomHorse(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();
        final int[] time = {(int) context.getOptional("time").orElse(1)};
        final var horseType = (HorseType) context.getOptional("horseType").orElse(HorseType.HORSE);
        final var tame = (boolean) context.getOptional("tame").orElse(false);
        final double addSpeed = (double) context.getOptional("addSpeed").orElse(0.0);
        final double addJump = (double) context.getOptional("addJump").orElse(0.0);

        new BukkitRunnable() {
            private final Random random = new Random();
            @Override
            public void run() {
                time[0]--;
                if (time[0] <= 0) {
                        this.cancel();
                }
                AbstractHorse horse = (AbstractHorse) sender.getWorld().spawnEntity(sender.getLocation(), horseType.getType());
                DecimalFormat df = new DecimalFormat("###.##");
                double speed = random.nextDouble(0.225) + 0.1125 + addSpeed;
                double jump = random.nextDouble(0.6) + 0.4 + addJump;

                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(speed);
                horse.setJumpStrength(jump);
                horse.setTamed(tame);
                horse.setOwner(tame ? sender : null);
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        Messages.SPAWN_HORSE.getMessageWithPrefix(),
                        Placeholder.parsed("speed", df.format(speed)),
                        Placeholder.parsed("jump", df.format(jump))
                ));
            }
        }.runTaskTimer(horseChecker, 0, 1);
    }

    enum HorseType {
        HORSE(EntityType.HORSE),
        SKELETON_HORSE(EntityType.SKELETON_HORSE),
        ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE),
        DONKEY(EntityType.DONKEY),
        MULE(EntityType.MULE),
        ;
        private final EntityType type;

        HorseType(
                EntityType type
        ) {
            this.type = type;
        }

        private EntityType getType() {
            return this.type;
        }
    }
}
