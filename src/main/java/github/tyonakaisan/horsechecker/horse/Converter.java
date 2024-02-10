package github.tyonakaisan.horsechecker.horse;

import github.tyonakaisan.horsechecker.config.ConfigFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class Converter {

    private Converter() {}

    public static HorseStats convertHorseStats(AbstractHorse horse) {
        var rank = HorseRank.calcEvaluateRankData(
                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue(),
                Objects.requireNonNull(horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH)).getValue());

        return new HorseStats(horse, rank);
    }

    public static Component statsMessageResolver(HorseStats horseStats, ConfigFactory configFactory) {
        var rank = rankMessageResolver(horseStats, configFactory);
        var speed = speedMessageResolver(horseStats, configFactory);
        var jump = jumpMessageResolver(horseStats, configFactory);
        var health = healthMessageResolver(horseStats, configFactory);
        var owner = ownerMessageResolver(horseStats, configFactory);

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

    private static Component rankMessageResolver(HorseStats horseStats, ConfigFactory configFactory) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().rankScoreResultText(),
                TagResolver.builder()
                        .tag("rank", Tag.selfClosingInserting(Component.text(horseStats.rankData().rank())))
                        .tag("rank_color", Tag.styling(style -> style.color(horseStats.rankData().textColor())))
                        .build());
    }

    private static Component speedMessageResolver(HorseStats horseStats, ConfigFactory configFactory) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().speedResultText(),
                TagResolver.builder()
                        .tag("speed", Tag.selfClosingInserting(Component.text(horseStats.genericSpeedToBlocPerSec())))
                        .build());
    }

    private static Component jumpMessageResolver(HorseStats horseStats, ConfigFactory configFactory) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().jumpResultText(),
                TagResolver.builder()
                        .tag("jump", Tag.selfClosingInserting(Component.text(horseStats.jumpStrengthToJumpHeight())))
                        .build());
    }

    private static Component healthMessageResolver(HorseStats horseStats, ConfigFactory configFactory) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().healthResultText(),
                TagResolver.builder()
                        .tag("health", Tag.selfClosingInserting(
                                Component.text((int) Objects.requireNonNull(horseStats.horse().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue())))
                        .build());
    }

    private static Component ownerMessageResolver(HorseStats horseStats, ConfigFactory configFactory) {
        return MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().ownerResultText(),
                TagResolver.builder()
                        .tag("owner", Tag.selfClosingInserting(horseStats.ownerName()))
                        .build());
    }
}
