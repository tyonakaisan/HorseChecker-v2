package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.*;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public final class HorseSettings {

    private List<String> allowedMobs = List.of(
            "horse",
            "donkey",
            "mule",
            "skeleton_horse",
            "zombie_horse"
    );

    @Comment("""
            発光エフェクトの効果時間
            """)
    private long glowingTime = 400L;

    @Comment("""
            検知可能距離
            """)
    private int targetRange = 20;

    public List<String> allowedMobs() {
        return this.allowedMobs;
    }

    public long glowingTime() {
        return this.glowingTime;
    }

    public int targetRange() {
        return this.targetRange;
    }
}
