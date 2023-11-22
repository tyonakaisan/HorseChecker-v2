package github.tyonakaisan.horsechecker.command.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.command.HorseCheckerCommand;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@DefaultQualifier(NonNull.class)
public final class DebugCommand implements HorseCheckerCommand {

    private final HorseChecker horseChecker;
    private final ConfigFactory configFactory;
    private final CommandManager<CommandSender> commandManager;

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
    }

    private void spawnRandomHorse(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();
        final int[] time = {(int) context.getOptional("time").orElse(1)};
        final var horseType = (HorseType) context.getOptional("horseType").orElse(HorseType.HORSE);
        final var tame = (boolean) context.getOptional("tame").orElse(false);
        final double addSpeed = (double) context.getOptional("addSpeed").orElse(0.0);
        final double addJump = (double) context.getOptional("addJump").orElse(0.0);

        new BukkitRunnable() {
            @Override
            public void run() {
                time[0]--;
                if (time[0] <= 0) {
                    this.cancel();
                }
                AbstractHorse horse = (AbstractHorse) sender.getWorld().spawnEntity(sender.getLocation(), horseType.getType());
                DecimalFormat df = new DecimalFormat("###.##");
                double speed = ThreadLocalRandom.current().nextDouble(0.225) + 0.1125 + addSpeed;
                double jump = ThreadLocalRandom.current().nextDouble(0.6) + 0.4 + addJump;

                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(speed);
                horse.setJumpStrength(jump);
                horse.setTamed(tame);
                horse.setOwner(tame ? sender : null);
                horse.getPersistentDataContainer().set(new NamespacedKey(horseChecker, "debug"), PersistentDataType.BOOLEAN, true);
                sender.sendMessage(MiniMessage.miniMessage().deserialize(
                        Messages.SPAWN_HORSE.getMessageWithPrefix(),
                        Placeholder.parsed("speed", df.format(speed)),
                        Placeholder.parsed("jump", df.format(jump))
                ));
            }
        }.runTaskTimer(this.horseChecker, 0, 1);
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
                final EntityType type
        ) {
            this.type = type;
        }

        private EntityType getType() {
            return this.type;
        }
    }
}
