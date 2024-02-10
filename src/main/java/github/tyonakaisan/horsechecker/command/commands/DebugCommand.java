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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
    private final Messages messages;
    private final CommandManager<CommandSender> commandManager;

    @Inject
    public DebugCommand(
            HorseChecker horseChecker,
            ConfigFactory configFactory,
            Messages messages,
            CommandManager<CommandSender> commandManager
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.messages = messages;
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

        this.commandManager.command(debug.literal("removeAllDebugHorse")
                .permission("horsechecker.command.removealldebughorse")
                .senderType(CommandSender.class)
                .handler(this::removeAllDebugHorse)
        );
    }

    private void spawnRandomHorse(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();
        final int[] time = {(int) context.getOptional("time").orElse(1)};
        final int max = 100;
        final var horseType = (HorseType) context.getOptional("horseType").orElse(HorseType.HORSE);
        final var tame = (boolean) context.getOptional("tame").orElse(false);
        final double addSpeed = (double) context.getOptional("addSpeed").orElse(0.0);
        final double addJump = (double) context.getOptional("addJump").orElse(0.0);

        if (time[0] > 100) {
            sender.sendMessage(
                    this.messages.translatable(
                            Messages.Style.ERROR,
                            sender,
                            "command.debug.error.maximum_number_on_command",
                            TagResolver.builder()
                                    .tag("max", Tag.selfClosingInserting(Component.text(max)))
                                    .build()));
            return;
        }

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
                sender.sendMessage(
                        messages.translatable(
                                Messages.Style.SUCCESS,
                                sender,
                                "command.debug.success.spawn_horse",
                                TagResolver.builder()
                                        .tag("speed", Tag.selfClosingInserting(Component.text(df.format(speed))))
                                        .tag("jump", Tag.selfClosingInserting(Component.text(df.format(jump))))
                                        .build())
                );
            }
        }.runTaskTimer(this.horseChecker, 0, 1);
    }

    private void removeAllDebugHorse(final @NonNull CommandContext<CommandSender> context) {
        final var sender = (Player) context.getSender();
        final int[] counts = {0};

        sender.getServer().getWorlds().forEach(world ->
                world.getEntities().forEach(entity -> {
                    if (entity instanceof AbstractHorse horse
                            && horse.getPersistentDataContainer().has(new NamespacedKey(horseChecker, "debug"), PersistentDataType.BOOLEAN)) {
                        horse.remove();
                        counts[0] ++;
                    }
                })
        );

        sender.sendMessage(
                this.messages.translatable(
                        Messages.Style.SUCCESS,
                        sender,
                        "command.debug.success.remove_horse",
                        TagResolver.builder()
                                .tag("counts", Tag.selfClosingInserting(Component.text(counts[0])))
                                .build()));
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
