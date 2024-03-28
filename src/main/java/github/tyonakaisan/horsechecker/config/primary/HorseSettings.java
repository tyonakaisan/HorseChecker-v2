package github.tyonakaisan.horsechecker.config.primary;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.List;

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

    @Comment("特定の条件下で連続で与えることが出来てしまうアイテムのリスト")
    private List<Material> nonRepeatableItems = List.of(
            Material.GOLDEN_CARROT,
            Material.GOLDEN_APPLE,
            Material.ENCHANTED_GOLDEN_APPLE,
            Material.HAY_BLOCK
    );

    public int glowingTime() {
        return this.glowingTime;
    }

    public int targetRange() {
        return this.targetRange;
    }

    public Sound findSound() {
        return this.findSound;
    }

    public List<Material> nonRepeatableItems() {
        return this.nonRepeatableItems;
    }
}
