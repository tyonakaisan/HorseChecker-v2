package github.tyonakaisan.horsechecker.config.primary;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
            1秒 = 20""")
    private int glowingTime = 400;

    @Comment("検知可能距離")
    private int targetRange = 20;

    @Comment("馬が見つかった際に鳴らす音")
    private Sound findSound = Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 0.7f, 1.5f);

    public int glowingTime() {
        return this.glowingTime;
    }

    public int targetRange() {
        return this.targetRange;
    }

    public Sound findSound() {
        return this.findSound;
    }
}
