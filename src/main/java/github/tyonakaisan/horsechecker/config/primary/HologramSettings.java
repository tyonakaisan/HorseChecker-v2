package github.tyonakaisan.horsechecker.config.primary;

import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class HologramSettings {
    @Comment("""
            ホログラムを表示するタスクの処理頻度
            数字を大きくするほど処理は軽くなります""")
    private int taskInterval = 2;

    @Comment("ホログラムの大きさ")
    private Vector scale = new Vector(1, 1, 1);
    @Comment("ホログラムの位置")
    private Vector translation = new Vector(0, 0.7, 0);
    @Comment("ホログラムの表示範囲")
    private float viewRange = 2f;
    @Comment("""
            ホログラムのテキストの配置方向
            LEFT,CENTER,RIGHT のどれか""")
    private String alignment = "LEFT";
    @Comment("""
            ホログラムの回転方向
            FIXED,VERTICAL,HORIZONTAL,CENTER のどれか""")
    private String billboard = "CENTER";

    public int taskInterval() {
        return this.taskInterval;
    }

    public Vector scale() {
        return this.scale;
    }

    public Vector translation() {
        return this.translation;
    }

    public float viewRange() {
        return this.viewRange;
    }

    public TextDisplay.TextAlignment alignment() {
        switch (this.alignment) {
            case "LEFT" -> {
                return TextDisplay.TextAlignment.LEFT;
            }
            case "RIGHT" -> {
                return TextDisplay.TextAlignment.RIGHT;
            }
            default -> {
                return TextDisplay.TextAlignment.CENTER;
            }
        }
    }

    public Display.Billboard billboard() {
        switch (this.billboard) {
            case "VERTICAL" -> {
                return Display.Billboard.VERTICAL;
            }
            case "HORIZONTAL" -> {
                return Display.Billboard.HORIZONTAL;
            }
            case "CENTER" -> {
                return Display.Billboard.CENTER;
            }
            default -> {
                return Display.Billboard.FIXED;
            }
        }
    }
}
