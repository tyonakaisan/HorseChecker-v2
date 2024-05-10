package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class Converter {

    private final ConfigFactory configFactory;

    private static final String NAME = "name";
    private static final String RANK = "rank";
    private static final String RANK_SCORE = "rank_score";
    private static final String RANK_COLOR = "rank_color";
    private static final String SPEED = "speed";
    private static final String JUMP = "jump";
    private static final String MAX_HEALTH = "max_health";
    private static final String HEALTH = "health";
    private static final String OWNER = "owner";
    private static final String MOTHER = "mother";
    private static final String FATHER = "father";
    private static final String MOTHER_COMPARISON = "mother_comparison";
    private static final String FATHER_COMPARISON = "father_comparison";

    @Inject
    public Converter(
            final ConfigFactory configFactory
    ) {
        this.configFactory = configFactory;
    }

    public Component statsMessageResolver(final WrappedHorse wrappedHorse) {
        final var name = this.nameMessageResolver(wrappedHorse);
        final var rank = this.rankMessageResolver(wrappedHorse);
        final var speed = this.speedMessageResolver(wrappedHorse);
        final var jump = this.jumpMessageResolver(wrappedHorse);
        final var health = this.healthMessageResolver(wrappedHorse);
        final var owner = this.ownerMessageResolver(wrappedHorse);
        var resultText = this.configFactory.primaryConfig().stats().resultText();

        if (wrappedHorse.horse().getName().toUpperCase().equals(wrappedHorse.horse().getType().toString())) {
            resultText = resultText.replace("<name><newline>", "");
            resultText = resultText.replace("<name>", "");
        }

        return MiniMessage.miniMessage().deserialize(resultText,
                TagResolver.builder()
                        .tag(NAME, Tag.selfClosingInserting(name))
                        .tag(RANK_SCORE, Tag.selfClosingInserting(rank))
                        .tag(SPEED, Tag.selfClosingInserting(speed))
                        .tag(JUMP, Tag.selfClosingInserting(jump))
                        .tag(HEALTH, Tag.selfClosingInserting(health))
                        .tag(OWNER, Tag.selfClosingInserting(owner))
                        .build()
        );
    }

    public Component withParentsStatsMessageResolver(final WrappedHorse childrenHorse, final WrappedHorse motherHorse, final WrappedHorse fatherHorse) {
        final var name = this.nameMessageResolver(childrenHorse);
        final var rank = this.rankMessageResolver(childrenHorse, motherHorse, fatherHorse);
        final var speed = this.speedMessageResolver(childrenHorse, motherHorse, fatherHorse);
        final var jump = this.jumpMessageResolver(childrenHorse, motherHorse, fatherHorse);
        final var health = this.healthMessageResolver(childrenHorse, motherHorse, fatherHorse);
        final var owner = this.ownerMessageResolver(childrenHorse);
        var resultText = this.configFactory.primaryConfig().stats().resultText();

        if (childrenHorse.horse().getName().toUpperCase().equals(childrenHorse.horse().getType().toString())) {
            resultText = resultText.replace("<name><newline>", "");
            resultText = resultText.replace("<name>", "");
        }

        return MiniMessage.miniMessage().deserialize(resultText,
                TagResolver.builder()
                        .tag(NAME, Tag.selfClosingInserting(name))
                        .tag(RANK_SCORE, Tag.selfClosingInserting(rank))
                        .tag(SPEED, Tag.selfClosingInserting(speed))
                        .tag(JUMP, Tag.selfClosingInserting(jump))
                        .tag(HEALTH, Tag.selfClosingInserting(health))
                        .tag(OWNER, Tag.selfClosingInserting(owner))
                        .build()
        );
    }

    private Component rankMessageResolver(final WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().rankScoreText(),
                TagResolver.builder()
                        .tag(RANK, Tag.selfClosingInserting(Component.text(wrappedHorse.getRank().rank())))
                        .tag(RANK_COLOR, Tag.styling(style -> style.color(wrappedHorse.getRank().textColor())))
                        .build());
    }

    private Component rankMessageResolver(final WrappedHorse childrenHorse, final WrappedHorse motherHorse, final WrappedHorse fatherHorse) {
        final var parentMessage = MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().parentText(),
                TagResolver.builder()
                        .tag(MOTHER, Tag.selfClosingInserting(Component.text(motherHorse.getRank().rank(), motherHorse.getRank().textColor())))
                        .tag(MOTHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.getRank().point(), motherHorse.getRank().point())))
                        .tag(FATHER, Tag.selfClosingInserting(Component.text(fatherHorse.getRank().rank(), fatherHorse.getRank().textColor())))
                        .tag(FATHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.getRank().point(), fatherHorse.getRank().point())))
                        .build());

        return this.rankMessageResolver(childrenHorse).appendSpace().append(parentMessage);
    }

    private Component speedMessageResolver(final WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().speedText(),
                TagResolver.builder()
                        .tag(SPEED, Tag.selfClosingInserting(Component.text(wrappedHorse.genericSpeedToBlocPerSec())))
                        .build());
    }

    private Component speedMessageResolver(final WrappedHorse childrenHorse, final WrappedHorse motherHorse, final WrappedHorse fatherHorse) {
        var parentMessage = MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().parentText(),
                TagResolver.builder()
                        .tag(MOTHER, Tag.selfClosingInserting(Component.text(motherHorse.genericSpeedToBlocPerSec())))
                        .tag(MOTHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.genericSpeedToBlocPerSec(), motherHorse.genericSpeedToBlocPerSec())))
                        .tag(FATHER, Tag.selfClosingInserting(Component.text(fatherHorse.genericSpeedToBlocPerSec())))
                        .tag(FATHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.genericSpeedToBlocPerSec(), fatherHorse.genericSpeedToBlocPerSec())))
                        .build());

        return this.speedMessageResolver(childrenHorse).appendSpace().append(parentMessage);
    }

    private Component jumpMessageResolver(final WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().jumpText(),
                TagResolver.builder()
                        .tag(JUMP, Tag.selfClosingInserting(Component.text(wrappedHorse.jumpStrengthToJumpHeight())))
                        .build());
    }

    private Component jumpMessageResolver(final WrappedHorse childrenHorse, final WrappedHorse motherHorse, final WrappedHorse fatherHorse) {
        final var parentMessage = MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().parentText(),
                TagResolver.builder()
                        .tag(MOTHER, Tag.selfClosingInserting(Component.text(motherHorse.jumpStrengthToJumpHeight())))
                        .tag(MOTHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.jumpStrengthToJumpHeight(), motherHorse.jumpStrengthToJumpHeight())))
                        .tag(FATHER, Tag.selfClosingInserting(Component.text(fatherHorse.jumpStrengthToJumpHeight())))
                        .tag(FATHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.jumpStrengthToJumpHeight(), fatherHorse.jumpStrengthToJumpHeight())))
                        .build());

        return jumpMessageResolver(childrenHorse).appendSpace().append(parentMessage);
    }

    private Component healthMessageResolver(final WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().healthText(),
                TagResolver.builder()
                        .tag(MAX_HEALTH, Tag.selfClosingInserting(Component.text(wrappedHorse.getMaxHealth())))
                        .tag(HEALTH, Tag.selfClosingInserting(Component.text(wrappedHorse.getHealth())))
                        .build());
    }

    private Component healthMessageResolver(final WrappedHorse childrenHorse, final WrappedHorse motherHorse, final WrappedHorse fatherHorse) {
        final var parentMessage = MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().parentText(),
                TagResolver.builder()
                        .tag(MOTHER, Tag.selfClosingInserting(Component.text(motherHorse.getMaxHealth())))
                        .tag(MOTHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.getMaxHealth(), motherHorse.getMaxHealth())))
                        .tag(FATHER, Tag.selfClosingInserting(Component.text(fatherHorse.getMaxHealth())))
                        .tag(FATHER_COMPARISON, Tag.selfClosingInserting(this.comparisonStats(childrenHorse.getMaxHealth(), fatherHorse.getMaxHealth())))
                        .build());

        return this.healthMessageResolver(childrenHorse).appendSpace().append(parentMessage);
    }

    private Component nameMessageResolver(final WrappedHorse wrappedHorse) {
        return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().nameText(),
                TagResolver.builder()
                        .tag(NAME, Tag.selfClosingInserting(wrappedHorse.getName()))
                        .build());
    }

    private Component ownerMessageResolver(final WrappedHorse wrappedHorse) {
        if (wrappedHorse.horse().getOwner() == null) {
            return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().ownerNotFoundText());
        }

        return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().ownerFoundText(),
                TagResolver.builder()
                        .tag(OWNER, Tag.selfClosingInserting(wrappedHorse.getOwnerName()))
                        .build());
    }

    private Component comparisonStats(final double base, final double comparison) {
        if (base > comparison) {
            return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().higherText());
        } else if (base == comparison) {
            return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().equalText());
        } else {
            return MiniMessage.miniMessage().deserialize(this.configFactory.primaryConfig().stats().lowerText());
        }
    }
}
