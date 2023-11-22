package github.tyonakaisan.horsechecker.horse;

import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class Converter {

    public HorseStatsData convertHorseStats(AbstractHorse horse) {
        var rank = HorseRank.calcEvaluateRankData(
                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue(),
                Objects.requireNonNull(horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH)).getValue());

        Location horseLocation = horse.isAdult() ? horse.getLocation() : horse.getLocation().subtract(0, 1, 0);

        return new HorseStatsData(
                this.getSpeed(horse),
                this.getHorseJump(horse),
                this.getMaxHealth(horse),
                this.getOwnerName(horse),
                this.getHorseName(horse),
                horse.getUniqueId(),
                horseLocation,
                rank);
    }

    public Component horseStatsMessage(HorseStatsData horseStatsData) {
        String stats = Messages.STATS_RESULT_SCORE.get()
                + Messages.STATS_RESULT_SPEED.get()
                + Messages.STATS_RESULT_JUMP.get()
                + Messages.STATS_RESULT_HP.get()
                + Messages.STATS_RESULT_OWNER.get();

        return MiniMessage.miniMessage().deserialize(stats,
                Formatter.number("speed", horseStatsData.speed()),
                Formatter.number("jump", horseStatsData.jump()),
                Formatter.number("health", horseStatsData.health()),
                Placeholder.parsed("owner", horseStatsData.ownerName()),
                Placeholder.parsed("rank", horseStatsData.rankData().rank()),
                TagResolver.resolver("rankcolor", Tag.styling(horseStatsData.rankData().textColor())));
    }

    private double jumpStrengthToJumpHeight(double strength) {
        return -0.1817584952 * strength * strength * strength + 3.689713992 * strength * strength + 2.128599134 * strength - 0.343930367;
    }

    private double genericSpeedToBlocPerSec(double speed) {
        return 42.162962963 * speed;
    }

    private int ageToBreedingCoolTime(double age) {
        return (int) (age / 20);
    }

    private int loveModeTicksToLoveModeTime(double loveModeTicks) {
        return (int) (loveModeTicks / 20);
    }

    public int getMaxHealth(AbstractHorse horse) {
        return (int) Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }

    public double getSpeed(AbstractHorse horse) {
        return genericSpeedToBlocPerSec(Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue());
    }

    public double getHorseJump(AbstractHorse horse) {
        return jumpStrengthToJumpHeight(Objects.requireNonNull(horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH)).getValue());
    }

    public double getAge(AbstractHorse horse) {
        return horse.getAge();
    }

    public String getOwnerName(AbstractHorse horse) {
        return horse.getOwner() != null ? "owned by <#ffa500>" + horse.getOwner().getName() + "</#ffa500>" : "no owner";
    }

    public String getHorseName(AbstractHorse horse) {
        return horse.getName().equals("Horse") ? "<lang:entity.minecraft.horse>" : horse.getName();
    }

    public int getBreedingCoolTime(AbstractHorse horse) {
        return ageToBreedingCoolTime(getAge(horse));
    }

    public int getLoveModeTime(AbstractHorse horse) {
        return loveModeTicksToLoveModeTime(horse.getLoveModeTicks());
    }

}
