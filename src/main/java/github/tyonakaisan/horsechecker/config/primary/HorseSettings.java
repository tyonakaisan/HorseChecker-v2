package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public class HorseSettings {

    @Comment("""
            検知可能距離
            """)
    private int targetRange = 20;

    @Comment("""
            馬のステータスを共有するかどうか
            共有メッセージは全体メッセージとして送信されます
            """)
    private boolean allowedHorseShare = true;

    @Comment("""
            shareコマンドのクールダウン(秒)
            連投防止
            """)
    private int horseShareInterval = 60;

    private List<String> allowedMobs = List.of(
            "horse",
            "donkey",
            "mule",
            "skeleton_horse",
            "zombie_horse"
    );

    public List<String> getAllowedMobs() {
        return this.allowedMobs;
    }

    public int targetRange() {
        return this.targetRange;
    }

    public boolean allowedHorseShare() {
        return this.allowedHorseShare;
    }

    public int horseShareInterval() {
        return this.horseShareInterval;
    }

}
