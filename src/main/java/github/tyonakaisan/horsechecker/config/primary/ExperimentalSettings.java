package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class ExperimentalSettings {

    @Comment("乗馬した際にプレイヤーの向いていた方向に合わせるか")
    private boolean alignHorseDirectionToPlayer = true;

    public boolean alignHorseDirectionToPlayer() {
        return this.alignHorseDirectionToPlayer;
    }
}
