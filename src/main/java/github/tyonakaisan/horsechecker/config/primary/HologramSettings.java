package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class HologramSettings {

    private String rankScoreResultText = "Score: <rank_color><rank></rank_color>";
    private String speedResultText = "Speed: <#ffa500><speed></#ffa500> blocks/s";
    private String jumpResultText = "Jump : <#ffa500><jump></#ffa500> blocks";
    private String healthResultText = "MaxHP: <#ffa500><health></#ffa500><red>♥</red>";
    private String ownerResultText = "<owner>";
    private String parentResultText = "<dark_gray>(<color:#ff85f1><mother></color> / <color:#47b9ff><father></color>)";
    @Comment("""
            ホログラムのステータス表示順
            """)
    private String resultText = "<rank_score><newline><speed><newline><jump><newline><health><newline><owner>";

    @Comment("""
            ホログラムを表示するタスクの処理頻度
            数字を大きくするほど処理は軽くなります
            """)
    private int displayHologramTaskInterval = 2;

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

    public String parentResultText() {
        return this.parentResultText;
    }

    public String resultText() {
        return this.resultText;
    }

    public int displayHologramTaskInterval() {
        return this.displayHologramTaskInterval;
    }
}
