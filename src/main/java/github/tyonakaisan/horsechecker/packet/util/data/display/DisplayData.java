package github.tyonakaisan.horsechecker.packet.util.data.display;

import github.tyonakaisan.horsechecker.packet.util.data.EntityData;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.joml.Quaterniond;

@SuppressWarnings({"unchecked","unused"})
@DefaultQualifier(NonNull.class)
public abstract class DisplayData<B> extends EntityData<B> {

    public B interpolationDelay(int delay) {
        this.addIntData(8, delay);
        return (B) this;
    }

    public B interpolationDuration(int duration) {
        this.addIntData(9, duration);
        return (B) this;
    }

    //仮
    public B teleportInterpolationDuration(int duration) {
        this.addIntData(10, duration);
        return (B) this;
    }

    public B translation(Vector vector) {
        this.addVectorData(11, vector);
        return (B) this;
    }

    public B scale(Vector vector) {
        this.addVectorData(12, vector);
        return (B) this;
    }

    public B rotationLeft(Quaterniond quaterniond) {
        this.addQuaternionData(13, quaterniond);
        return (B) this;
    }

    public B rotationRight(Quaterniond quaterniond) {
        this.addQuaternionData(14, quaterniond);
        return (B) this;
    }

    public B billboard(Display.Billboard billboard) {
        switch (billboard) {
            case FIXED -> this.addByteData(15, (byte) 0);
            case VERTICAL -> this.addByteData(15, (byte) 1);
            case HORIZONTAL -> this.addByteData(15, (byte) 2);
            case CENTER -> this.addByteData(15, (byte) 3);
        }
        return (B) this;
    }

    public B brightness(int lightLevel) {
        this.addIntData(16, lightLevel);
        return (B) this;
    }

    public B viewRange(float range) {
        this.addFloatData(17, range);
        return (B) this;
    }

    public B shadowRadius(float radius) {
        this.addFloatData(18, radius);
        return (B) this;
    }

    public B shadowStrength(float strength) {
        this.addFloatData(19, strength);
        return (B) this;
    }

    public B width(float width) {
        this.addFloatData(20, width);
        return (B) this;
    }

    public B height(float height) {
        this.addFloatData(21, height);
        return (B) this;
    }

    public B glowColor(int color) {
        this.addIntData(22, color);
        return (B) this;
    }
}