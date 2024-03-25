package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
@SuppressWarnings({"FieldMayBeFinal","FieldCanBeLocal"})
public final class HorseSettings {

    @Comment("""
            発光エフェクトの効果時間
            1秒 = 20""")
    private int glowingTime = 400;

    @Comment("検知可能距離")
    private int targetRange = 20;

    public int glowingTime() {
        return this.glowingTime;
    }

    public int targetRange() {
        return this.targetRange;
    }
}
