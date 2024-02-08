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

    public HorseStats convertHorseStats(AbstractHorse horse) {
        var rank = HorseRank.calcEvaluateRankData(
                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue(),
                Objects.requireNonNull(horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH)).getValue());

        return new HorseStats(horse, rank);
    }

    public Component statsMessageResolver(HorseStats horseStats, ConfigFactory configFactory) {
        var tagResolver = TagResolver.builder()
                .resolver(TagResolver.standard());

        var rank = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().rankScoreResultText(),
                tagResolver
                        .tag("rank", Tag.selfClosingInserting(Component.text(horseStats.rankData().rank())))
                        .tag("rank_color", Tag.styling(style -> style.color(horseStats.rankData().textColor())))
                        .build());

        var speed = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().speedResultText(),
                tagResolver
                        .tag("speed", Tag.selfClosingInserting(Component.text(horseStats.genericSpeedToBlocPerSec())))
                        .build());

        var jump = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().jumpResultText(),
                tagResolver
                        .tag("jump", Tag.selfClosingInserting(Component.text(horseStats.jumpStrengthToJumpHeight())))
                        .build());

        var health = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().healthResultText(),
                tagResolver
                        .tag("health", Tag.selfClosingInserting(
                                Component.text((int) Objects.requireNonNull(horseStats.horse().getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue())))
                        .build());

        var owner = MiniMessage.miniMessage().deserialize(
                configFactory.primaryConfig().horse().ownerResultText(),
                tagResolver
                        .tag("owner", Tag.selfClosingInserting(horseStats.ownerName()))
                        .build());

        return MiniMessage.miniMessage().deserialize(configFactory.primaryConfig().horse().resultText(),
                tagResolver
                        .tag("rank_score", Tag.selfClosingInserting(rank))
                        .tag("speed", Tag.selfClosingInserting(speed))
                        .tag("jump", Tag.selfClosingInserting(jump))
                        .tag("health", Tag.selfClosingInserting(health))
                        .tag("owner", Tag.selfClosingInserting(owner))
                        .build()
                );
    }
}
