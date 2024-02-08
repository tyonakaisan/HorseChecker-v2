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
            """)
    private long glowingTime = 400L;

    @Comment("""
            検知可能距離
            """)
    private int targetRange = 20;

    private String rankScoreResultText = "Score: <rank_color><rank></rank_color><newline>";
    private String speedResultText = "Speed: <#ffa500><speed></#ffa500> blocks/s<newline>";
    private String jumpResultText = "Jump : <#ffa500><jump></#ffa500> blocks<newline>";
    private String healthResultText = "MaxHP: <#ffa500><health></#ffa500><red>♥</red><newline>";
    private String ownerResultText = "<owner>";
    private String resultText = "<rank_score><speed><jump><health><owner>";

    public long glowingTime() {
        return this.glowingTime;
    }

    public int targetRange() {
        return this.targetRange;
    }

    public String rankScoreResultText() {
        return this.rankScoreResultText;
    }

    public String speedResultText() {
        return this.speedResultText;
    }

    public String jumpResultText() {
        return this.jumpResultText;
    }

    public String healthResultText() {
        return this.healthResultText;
    }

    public String ownerResultText() {
        return this.ownerResultText;
    }

    public String resultText() {
        return this.resultText;
    }
}
