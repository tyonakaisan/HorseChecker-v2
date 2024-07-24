package github.tyonakaisan.horsechecker.packet.util.entity.display;

import github.tyonakaisan.horsechecker.packet.util.entity.EntityData;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.joml.Quaternionf;

@SuppressWarnings({"unchecked","unused"})
@DefaultQualifier(NonNull.class)
public abstract class DisplayData<B> extends EntityData<B> {

    public B interpolationDelay(final int delay) {
        this.addIntData(8, delay);
        return (B) this;
    }

    public B interpolationDuration(final int duration) {
        this.addIntData(9, duration);
        return (B) this;
    }

    //仮
    public B teleportInterpolationDuration(final int duration) {
        this.addIntData(10, duration);
        return (B) this;
    }

    public B translation(final Vector vector) {
        this.addVectorData(11, vector);
        return (B) this;
    }

    public B scale(final Vector vector) {
        this.addVectorData(12, vector);
        return (B) this;
    }

    public B rotationLeft(final Quaternionf quaternionf) {
        this.addQuaternionData(13, quaternionf);
        return (B) this;
    }

    public B rotationRight(final Quaternionf quaternionf) {
        this.addQuaternionData(14, quaternionf);
        return (B) this;
    }

    public B billboard(final Display.Billboard billboard) {
        switch (billboard) {
            case FIXED -> this.addByteData(15, (byte) 0);
            case VERTICAL -> this.addByteData(15, (byte) 1);
            case HORIZONTAL -> this.addByteData(15, (byte) 2);
            case CENTER -> this.addByteData(15, (byte) 3);
        }
        return (B) this;
    }

    public B brightness(final int lightLevel) {
        this.addIntData(16, lightLevel);
        return (B) this;
    }

    public B viewRange(final float range) {
        this.addFloatData(17, range);
        return (B) this;
    }

    public B shadowRadius(final float radius) {
        this.addFloatData(18, radius);
        return (B) this;
    }

    public B shadowStrength(final float strength) {
        this.addFloatData(19, strength);
        return (B) this;
    }

    public B width(final float width) {
        this.addFloatData(20, width);
        return (B) this;
    }

    public B height(final float height) {
        this.addFloatData(21, height);
        return (B) this;
    }

    public B glowColor(final int color) {
        this.addIntData(22, color);
        return (B) this;
    }
}