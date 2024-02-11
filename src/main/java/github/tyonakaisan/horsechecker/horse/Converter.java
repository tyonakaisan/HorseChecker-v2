package github.tyonakaisan.horsechecker.horse;

import github.tyonakaisan.horsechecker.config.ConfigFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Arrays;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class Converter {

    // めっちゃ汚い
    private Converter() {}

    public static HorseStats convertHorseStats(AbstractHorse horse) {
        var rank = HorseRank.calcEvaluateRankData(
                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue(),
                Objects.requireNonNull(horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH)).getValue());

        return new HorseStats(horse, rank);
    }

    public static Component statsMessageResolver(ConfigFactory configFactory, HorseStats horseStats) {
        var rank = rankMessageResolver(configFactory, horseStats);
        var speed = speedMessageResolver(configFactory, horseStats);
        var jump = jumpMessageResolver(configFactory, horseStats);
        var health = healthMessageResolver(configFactory, horseStats);
        var owner = ownerMessageResolver(configFactory, horseStats);

        return MiniMessage.miniMessage().deserialize(configFactory.primaryConfig().horse().resultText(),
                TagResolver.builder()
                        .tag("rank_score", Tag.selfClosingInserting(rank))
                        .tag("speed", Tag.selfClosingInserting(speed))
                        .tag("jump", Tag.selfClosingInserting(jump))
                        .tag("health", Tag.selfClosingInserting(health))
                        .tag("owner", Tag.selfClosingInserting(owner))
                        .build()
        );
    }

    public static Component withParentsStatsMessageResolver(ConfigFactory configFactory, HorseStats childrenStats, HorseStats motherStats, HorseStats fatherStats) {
        var rank = rankMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var speed = speedMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var jump = jumpMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var health = healthMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var owner = ownerMessageResolver(configFactory, childrenStats);

        return MiniMessage.miniMessage().deserialize(configFactory.primaryConfig().horse().resultText(),
                TagResolver.builder()
                        .tag("rank_score", Tag.selfClosingInserting(rank))
                        .tag("speed", Tag.selfClosingInserting(speed))
                        .tag("jump", Tag.selfClosingInserting(jump))
                        .tag("health", Tag.selfClosingInserting(health))
                        .tag("owner", Tag.selfClosingInserting(owner))
                        .build()
        );
    }

    private static Component rankMessageResolver(ConfigFactory configFactory, HorseStats horseStats) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().rankScoreResultText(),
                TagResolver.builder()
                        .tag("rank", Tag.selfClosingInserting(Component.text(horseStats.rankData().rank())))
                        .tag("rank_color", Tag.styling(style -> style.color(horseStats.rankData().textColor())))
                        .build());
    }

    private static Component rankMessageResolver(ConfigFactory configFactory, HorseStats... horseStats) {
        var children = Arrays.asList(horseStats).get(0);
        var mother = Arrays.asList(horseStats).get(1);
        var father = Arrays.asList(horseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().parentResultText(),
                TagResolver.builder()
                        .tag("mother", Tag.selfClosingInserting(
                                Component.text(mother.rankData().rank(), mother.rankData().textColor())))
                        .tag("father", Tag.selfClosingInserting(
                                Component.text(father.rankData().rank(), father.rankData().textColor())))
                        .build());

        return rankMessageResolver(configFactory, children).appendSpace().append(parentMessage);
    }

    private static Component speedMessageResolver(ConfigFactory configFactory, HorseStats horseStats) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().speedResultText(),
                TagResolver.builder()
                        .tag("speed", Tag.selfClosingInserting(Component.text(horseStats.genericSpeedToBlocPerSec())))
                        .build());
    }

    private static Component speedMessageResolver(ConfigFactory configFactory, HorseStats... horseStats) {
        var children = Arrays.asList(horseStats).get(0);
        var mother = Arrays.asList(horseStats).get(1);
        var father = Arrays.asList(horseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().parentResultText(),
                TagResolver.builder()
                        .tag("mother", Tag.selfClosingInserting(
                                Component.text(mother.genericSpeedToBlocPerSec())
                                        .append(comparisonStats(children.genericSpeedToBlocPerSec(), mother.genericSpeedToBlocPerSec()))))
                        .tag("father", Tag.selfClosingInserting(
                                Component.text(father.genericSpeedToBlocPerSec())
                                        .append(comparisonStats(children.genericSpeedToBlocPerSec(), father.genericSpeedToBlocPerSec()))))
                        .build());

        return speedMessageResolver(configFactory, children).appendSpace().append(parentMessage);
    }

    private static Component jumpMessageResolver(ConfigFactory configFactory, HorseStats horseStats) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().jumpResultText(),
                TagResolver.builder()
                        .tag("jump", Tag.selfClosingInserting(Component.text(horseStats.jumpStrengthToJumpHeight())))
                        .build());
    }

    private static Component jumpMessageResolver(ConfigFactory configFactory, HorseStats... horseStats) {
        var children = Arrays.asList(horseStats).get(0);
        var mother = Arrays.asList(horseStats).get(1);
        var father = Arrays.asList(horseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().parentResultText(),
                TagResolver.builder()
                        .tag("mother", Tag.selfClosingInserting(
                                Component.text(mother.jumpStrengthToJumpHeight())
                                        .append(comparisonStats(children.jumpStrengthToJumpHeight(), mother.jumpStrengthToJumpHeight()))))
                        .tag("father", Tag.selfClosingInserting(
                                Component.text(father.jumpStrengthToJumpHeight())
                                        .append(comparisonStats(children.jumpStrengthToJumpHeight(), father.jumpStrengthToJumpHeight()))))
                        .build());

        return jumpMessageResolver(configFactory, children).appendSpace().append(parentMessage);
    }

    private static Component healthMessageResolver(ConfigFactory configFactory, HorseStats horseStats) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().healthResultText(),
                TagResolver.builder()
                        .tag("health", Tag.selfClosingInserting(Component.text(horseStats.getMaxHealth())))
                        .build());
    }

    private static Component healthMessageResolver(ConfigFactory configFactory, HorseStats... horseStats) {
        var children = Arrays.asList(horseStats).get(0);
        var mother = Arrays.asList(horseStats).get(1);
        var father = Arrays.asList(horseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().parentResultText(),
                TagResolver.builder()
                        .tag("mother", Tag.selfClosingInserting(
                                Component.text(mother.getMaxHealth())
                                        .append(comparisonStats(children.getMaxHealth(), mother.getMaxHealth()))))
                        .tag("father", Tag.selfClosingInserting(
                                Component.text(father.getMaxHealth())
                                        .append(comparisonStats(children.getMaxHealth(), father.getMaxHealth()))))
                        .build());

        return healthMessageResolver(configFactory, children).appendSpace().append(parentMessage);
    }

    private static Component ownerMessageResolver(ConfigFactory configFactory, HorseStats horseStats) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().ownerResultText(),
                TagResolver.builder()
                        .tag("owner", Tag.selfClosingInserting(horseStats.ownerName()))
                        .build());
    }

    private static Component comparisonStats(double base, double comparison) {
        if (base > comparison) {
            return Component.text("↑", NamedTextColor.RED).decorate(TextDecoration.BOLD);
        } else if (base == comparison) {
            return Component.text("=", NamedTextColor.GRAY).decorate(TextDecoration.BOLD);
        } else {
            return Component.text("↓", NamedTextColor.BLUE).decorate(TextDecoration.BOLD);
        }
    }
}
