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

    private String healthText = "MaxHP: <#ffa500><max_health></#ffa500><red>♥</red>";

    private String ownerFoundText = "owned by <#ffa500><owner></#ffa500>";

    private String ownerNotFoundText = "no owner";

    @Comment("ホログラムのステータス表示順")
    private String resultText = "<rank_score><newline><speed><newline><jump><newline><health><newline><name><newline><owner>";

    @Comment("馬のステータスを比較する際の表示順")
    private String parentText = "<dark_gray>(<#ff85f1><mother><mother_comparison></#ff85f1> / <#47b9ff><father><father_comparison></#47b9ff>)";

    private String higherText = "<red><b>↑</b></red>";

    private String equalText = "<gray><b>=</b></gray>";

    private String lowerText = "<blue><b>↓</b></blue>";

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

    public String ownerFoundText() {
        return this.ownerFoundText;
    }
    public String ownerNotFoundText() {
        return this.ownerNotFoundText;
    }

    public String parentText() {
        return this.parentText;
    }

    public String resultText() {
        return this.resultText;
    }

    public String higherText() {
        return this.higherText;
    }

    public String equalText() {
        return this.equalText;
    }

    public String lowerText() {
        return this.lowerText;
    }
}
