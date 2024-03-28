package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class StatsSettings {
    private String nameText = "Name: <name>";
    private String rankScoreText = "Score: <rank_color><rank></rank_color>";
    private String speedText = "Speed: <#ffa500><speed></#ffa500> blocks/s";
    private String jumpText = "Jump : <#ffa500><jump></#ffa500> blocks";
    private String healthText = "MaxHP: <#ffa500><health></#ffa500><red>♥</red>";
    private String ownerText = "<owner>";
    private String parentText = "<dark_gray>(<color:#ff85f1><mother></color> / <color:#47b9ff><father></color>)";
    @Comment("ホログラムのステータス表示順")
    private String resultText = "<rank_score><newline><speed><newline><jump><newline><health><newline><owner><newline><name>";

    public String nameText() {
        return this.nameText;
    }

    public String rankScoreText() {
        return this.rankScoreText;
    }

    public String speedText() {
        return this.speedText;
    }

    public String jumpText() {
        return this.jumpText;
    }

    public String healthText() {
        return this.healthText;
    }

    public String ownerText() {
        return this.ownerText;
    }

    public String parentText() {
        return this.parentText;
    }

    public String resultText() {
        return this.resultText;
    }
}
