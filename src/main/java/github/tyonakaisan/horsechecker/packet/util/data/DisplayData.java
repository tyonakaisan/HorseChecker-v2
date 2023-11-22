package github.tyonakaisan.horsechecker.packet.util.data;

import com.comphenix.protocol.wrappers.Vector3F;
import org.bukkit.entity.Display;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.joml.Quaterniond;

@SuppressWarnings("unchecked")
@DefaultQualifier(NonNull.class)
public abstract class DisplayData<B> extends EntityData<B> {

    public B interpolationDelay(int delay) {
        addIntData(8, delay);
        return (B) this;
    }

    public B interpolationDuration(int duration) {
        addIntData(9, duration);
        return (B) this;
    }

    //仮
    public B teleportInterpolationDuration(int duration) {
        addIntData(10, duration);
        return (B) this;
    }

    public B translation(Vector3F vector3F) {
        addVectorData(11, vector3F);
        return (B) this;
    }

    public B scale(Vector3F vector3F) {
        addVectorData(12, vector3F);
        return (B) this;
    }

    public B rotationLeft(Quaterniond quaterniond) {
        addQuaternionData(13, quaterniond);
        return (B) this;
    }

    public B rotationRight(Quaterniond quaterniond) {
        addQuaternionData(14, quaterniond);
        return (B) this;
    }

    public B billboard(Display.Billboard billboard) {
        switch (billboard) {
            case FIXED -> addByteData(15, (byte) 0);
            case VERTICAL -> addByteData(15, (byte) 1);
            case HORIZONTAL -> addByteData(15, (byte) 2);
            case CENTER -> addByteData(15, (byte) 3);
        }
        return (B) this;
    }

    public B brightness(int lightLevel) {
        addIntData(16, lightLevel);
        return (B) this;
    }

    public B viewRange(float range) {
        addFloatData(17, range);
        return (B) this;
    }

    public B shadowRadius(float radius) {
        addFloatData(18, radius);
        return (B) this;
    }

    public B shadowStrength(float strength) {
        addFloatData(19, strength);
        return (B) this;
    }

    public B width(float width) {
        addFloatData(20, width);
        return (B) this;
    }

    public B height(float height) {
        addFloatData(21, height);
        return (B) this;
    }

    public B glowColor(int color) {
        addIntData(22, color);
        return (B) this;
    }
}