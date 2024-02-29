package github.tyonakaisan.horsechecker.packet.util.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Pose;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@SuppressWarnings({"unchecked","unused"})
@DefaultQualifier(NonNull.class)
public abstract class EntityData<B> extends WrappedDataManager {

    private byte bitmask = 0x00;

    public B fire() {
        this.bitmask |= 0x01;
        this.addByteData(0, this.bitmask);
        return (B) this;
    }

    public B crouching() {
        this.bitmask |= 0x02;
        this.addByteData(0, this.bitmask);
        return (B) this;
    }

    public B sprinting() {
        this.bitmask |= 0x08;
        this.addByteData(0, this.bitmask);
        return (B) this;
    }

    public B swimming() {
        this.bitmask |= 0x10;
        this.addByteData(0, this.bitmask);
        return (B) this;
    }

    public B invisible() {
        this.bitmask |= 0x20;
        this.addByteData(0, this.bitmask);
        return (B) this;
    }

    public B glowing() {
        this.bitmask |= 0x40;
        this.addByteData(0, this.bitmask);
        return (B) this;
    }

    public B flying() {
        this.bitmask |= 0x80;
        this.addByteData(0, this.bitmask);
        return (B) this;
    }

    public B airTick(final int ticks) {
        this.addIntData(1, ticks);
        return (B) this;
    }

    public B name(final Component name) {
        this.addChatData(2, name);
        return (B) this;
    }

    public B name(final Component name, final boolean visible) {
        this.addChatData(2, name);
        this.addBooleanData(3, visible);
        return (B) this;
    }

    public B silent(final boolean silent) {
        this.addBooleanData(4, silent);
        return (B) this;
    }

    public B gravity(final boolean gravity) {
        this.addBooleanData(5, gravity);
        return (B) this;
    }

    public B pose(final Pose pose) {
        this.addPoseData(6, pose);
        return (B) this;
    }

    public B frozenTicks(final int ticks) {
        this.addIntData(7, ticks);
        return (B) this;
    }
}
