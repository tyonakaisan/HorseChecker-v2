package github.tyonakaisan.horsechecker.packet.util.data;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Pose;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings("unchecked")
@DefaultQualifier(NonNull.class)
public abstract class EntityData<B> extends WrappedDataManager {
    private byte bitmask = 0x00;

    public B fire() {
        this.bitmask |= 0x01;
        addByteData(0, this.bitmask);
        return (B) this;
    }

    public B crouching() {
        this.bitmask |= 0x02;
        addByteData(0, this.bitmask);
        return (B) this;
    }

    public B sprinting() {
        this.bitmask |= 0x08;
        addByteData(0, this.bitmask);
        return (B) this;
    }

    public B swimming() {
        this.bitmask |= 0x10;
        addByteData(0, this.bitmask);
        return (B) this;
    }

    public B invisible() {
        this.bitmask |= 0x20;
        addByteData(0, this.bitmask);
        return (B) this;
    }

    public B glowing() {
        this.bitmask |= 0x40;
        addByteData(0, this.bitmask);
        return (B) this;
    }

    public B flying() {
        this.bitmask |= 0x80;
        addByteData(0, this.bitmask);
        return (B) this;
    }

    public B airTick(int ticks) {
        addIntData(1, ticks);
        return (B) this;
    }

    public B name(Component name) {
        addChatData(2, name);
        return (B) this;
    }

    public B name(Component name, boolean visible) {
        addChatData(2, name);
        addBooleanData(3, visible);
        return (B) this;
    }

    public B silent(boolean silent) {
        addBooleanData(4, silent);
        return (B) this;
    }

    public B gravity(boolean gravity) {
        addBooleanData(5, gravity);
        return (B) this;
    }

    public B pose(Pose pose) {
        addPoseData(6, pose);
        return (B) this;
    }

    public B frozenTicks(int ticks) {
        addIntData(7, ticks);
        return (B) this;
    }
}
