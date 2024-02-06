package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public final class HorseSettings {

    @Comment("""
            発光エフェクトの効果時間
            """)
    private long glowingTime = 400L;

    @Comment("""
            検知可能距離
            """)
    private int targetRange = 20;

    public long glowingTime() {
        return this.glowingTime;
    }

    public int targetRange() {
        return this.targetRange;
    }
}
