package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public final class ShareSettings {

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

    @Comment("""
            所有者関係なくShareコマンドを使えるかどうか
            所有者が居ない場合はこの設定は関係ありません
            true: 自分の所有してる馬のみ使用可能
            false: 所有者が違っても使用可能
            """)
    private boolean ownerOnly = false;

    public boolean allowedHorseShare() {
        return this.allowedHorseShare;
    }

    public int horseShareInterval() {
        return this.horseShareInterval;
    }

    public boolean ownerOnly() {
        return  this.ownerOnly;
    }
}
