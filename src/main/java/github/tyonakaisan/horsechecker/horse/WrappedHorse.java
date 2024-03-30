package github.tyonakaisan.horsechecker.horse;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.checkerframework.checker.units.qual.C;

import java.text.DecimalFormat;
import java.util.Objects;

public record WrappedHorse(
        AbstractHorse horse
) {

    public HorseRank.HorseRankData getRank() {
        return HorseRank.calcEvaluateRankData(
                Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue(),
                Objects.requireNonNull(horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH)).getValue());
    }

    public double genericSpeedToBlocPerSec() {
        var speed = 42.162962963 * Objects.requireNonNull(this.horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue();
        return Double.parseDouble(new DecimalFormat("#.###").format(speed));
    }

    public double jumpStrengthToJumpHeight() {
        var jumpStrength = this.horse.getJumpStrength();
        var jump = -0.1817584952 * Math.pow(jumpStrength, 3) + 3.689713992 * Math.pow(jumpStrength, 2) + 2.128599134 * jumpStrength - 0.343930367;
        return Double.parseDouble(new DecimalFormat("#.##").format(jump));
    }

    public int getMaxHealth() {
        return (int) Objects.requireNonNull(this.horse.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }

    public Location getLocation() {
        return this.horse.isAdult() ? this.horse.getLocation() : this.horse.getLocation().subtract(0, 1, 0);
    }

    public Component getOwnerName() {
        if (this.horse.getOwner() == null) {
            return Component.empty();
        }

        return this.horse.getOwner().getName() != null
                ? Component.text(this.horse.getOwner().getName())
                : Component.text("unknown");
    }

    public Component getName() {
        return this.horse.getName().equals("Horse")
                ? MiniMessage.miniMessage().deserialize("<lang:" + this.horse.getType().translationKey() + ">")
                : Component.text(this.horse.getName());
    }

    public int getBreedingCoolTime() {
        return this.horse.getAge() / 20;
    }

    public int getLoveModeTime() {
        return this.horse.getLoveModeTicks() / 20;
    }

    public int getAge() {
        return this.horse.getAge();
    }


    public int getHealth() {
        return (int) this.horse.getHealth();
    }

    public int getLoveModeTicks() {
        return this.horse.getLoveModeTicks();
    }
}
