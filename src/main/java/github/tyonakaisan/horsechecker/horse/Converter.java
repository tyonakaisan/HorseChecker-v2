package github.tyonakaisan.horsechecker.horse;

import github.tyonakaisan.horsechecker.config.ConfigFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Arrays;

@DefaultQualifier(NonNull.class)
public final class Converter {

    // めっちゃ汚い
    private Converter() {}

    public static Component statsMessageResolver(ConfigFactory configFactory, WrappedHorse wrappedHorse) {
        var rank = rankMessageResolver(configFactory, wrappedHorse);
        var speed = speedMessageResolver(configFactory, wrappedHorse);
        var jump = jumpMessageResolver(configFactory, wrappedHorse);
        var health = healthMessageResolver(configFactory, wrappedHorse);
        var owner = ownerMessageResolver(configFactory, wrappedHorse);

        return MiniMessage.miniMessage().deserialize(configFactory.primaryConfig().hologram().resultText(),
                TagResolver.builder()
                        .tag("rank_score", Tag.selfClosingInserting(rank))
                        .tag("speed", Tag.selfClosingInserting(speed))
                        .tag("jump", Tag.selfClosingInserting(jump))
                        .tag("health", Tag.selfClosingInserting(health))
                        .tag("owner", Tag.selfClosingInserting(owner))
                        .build()
        );
    }

    public static Component withParentsStatsMessageResolver(ConfigFactory configFactory, WrappedHorse childrenStats, WrappedHorse motherStats, WrappedHorse fatherStats) {
        var rank = rankMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var speed = speedMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var jump = jumpMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var health = healthMessageResolver(configFactory, childrenStats, motherStats, fatherStats);
        var owner = ownerMessageResolver(configFactory, childrenStats);

        return MiniMessage.miniMessage().deserialize(configFactory.primaryConfig().hologram().resultText(),
                TagResolver.builder()
                        .tag("rank_score", Tag.selfClosingInserting(rank))
                        .tag("speed", Tag.selfClosingInserting(speed))
                        .tag("jump", Tag.selfClosingInserting(jump))
                        .tag("health", Tag.selfClosingInserting(health))
                        .tag("owner", Tag.selfClosingInserting(owner))
                        .build()
        );
    }

    private static Component rankMessageResolver(ConfigFactory configFactory, WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().rankScoreResultText(),
                TagResolver.builder()
                        .tag("rank", Tag.selfClosingInserting(Component.text(wrappedHorse.getRank().rank())))
                        .tag("rank_color", Tag.styling(style -> style.color(wrappedHorse.getRank().textColor())))
                        .build());
    }

    private static Component rankMessageResolver(ConfigFactory configFactory, WrappedHorse... wrappedHorseStats) {
        var children = Arrays.asList(wrappedHorseStats).get(0);
        var mother = Arrays.asList(wrappedHorseStats).get(1);
        var father = Arrays.asList(wrappedHorseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().parentResultText(),
                TagResolver.builder()
                        .tag("mother", Tag.selfClosingInserting(
                                Component.text(mother.getRank().rank(), mother.getRank().textColor())))
                        .tag("father", Tag.selfClosingInserting(
                                Component.text(father.getRank().rank(), father.getRank().textColor())))
                        .build());

        return rankMessageResolver(configFactory, children).appendSpace().append(parentMessage);
    }

    private static Component speedMessageResolver(ConfigFactory configFactory, WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().speedResultText(),
                TagResolver.builder()
                        .tag("speed", Tag.selfClosingInserting(Component.text(wrappedHorse.genericSpeedToBlocPerSec())))
                        .build());
    }

    private static Component speedMessageResolver(ConfigFactory configFactory, WrappedHorse... wrappedHorseStats) {
        var children = Arrays.asList(wrappedHorseStats).get(0);
        var mother = Arrays.asList(wrappedHorseStats).get(1);
        var father = Arrays.asList(wrappedHorseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().parentResultText(),
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

    private static Component jumpMessageResolver(ConfigFactory configFactory, WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().jumpResultText(),
                TagResolver.builder()
                        .tag("jump", Tag.selfClosingInserting(Component.text(wrappedHorse.jumpStrengthToJumpHeight())))
                        .build());
    }

    private static Component jumpMessageResolver(ConfigFactory configFactory, WrappedHorse... wrappedHorseStats) {
        var children = Arrays.asList(wrappedHorseStats).get(0);
        var mother = Arrays.asList(wrappedHorseStats).get(1);
        var father = Arrays.asList(wrappedHorseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().parentResultText(),
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

    private static Component healthMessageResolver(ConfigFactory configFactory, WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().healthResultText(),
                TagResolver.builder()
                        .tag("health", Tag.selfClosingInserting(Component.text(wrappedHorse.getMaxHealth())))
                        .build());
    }

    private static Component healthMessageResolver(ConfigFactory configFactory, WrappedHorse... wrappedHorseStats) {
        var children = Arrays.asList(wrappedHorseStats).get(0);
        var mother = Arrays.asList(wrappedHorseStats).get(1);
        var father = Arrays.asList(wrappedHorseStats).get(2);

        var parentMessage = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().parentResultText(),
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

    private static Component ownerMessageResolver(ConfigFactory configFactory, WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().hologram().ownerResultText(),
                TagResolver.builder()
                        .tag("owner", Tag.selfClosingInserting(wrappedHorse.ownerName()))
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
