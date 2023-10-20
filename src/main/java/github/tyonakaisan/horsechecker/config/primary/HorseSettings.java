package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.*;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public final class HorseSettings {

    @Comment("""
            検知可能距離
            """)
    private int targetRange = 20;

    //ゴリ押し
    //そもそもこれ無くてもいい説
    private Map<String, Boolean> allowedMobs = Map.of(
            "horse", true,
            "donkey", true,
            "mule", true,
            "skeleton_horse", true,
            "zombie_horse", true
        );

    public Map<String, Boolean> allowedMobs() {
        return this.allowedMobs;
    }

    public int targetRange() {
        return this.targetRange;
    }
}
