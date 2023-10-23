package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public final class ShareSettings {

    @Comment("""
            馬のステータスの共有メッセージを使用可能にするか
            """)
    private boolean allowedHorseShare = true;

    @Comment("""
            shareコマンドのクールダウン(秒)
            """)
    private int shareCommandIntervalTime = 60;

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

    public int shareCommandIntervalTime() {
        return this.shareCommandIntervalTime;
    }

    public boolean ownerOnly() {
        return this.ownerOnly;
    }
}
