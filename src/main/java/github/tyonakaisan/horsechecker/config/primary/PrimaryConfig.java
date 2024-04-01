package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
@SuppressWarnings("FieldMayBeFinal")
public final class PrimaryConfig {

    @Comment("実験機能")
    private ExperimentalSettings experimental = new ExperimentalSettings();
    private HorseSettings horse = new HorseSettings();
    private ShareSettings share = new ShareSettings();
    private HologramSettings hologram = new HologramSettings();
    private StatsSettings stats = new StatsSettings();

    public ExperimentalSettings experimental() {
        return this.experimental;
    }

    public HorseSettings horse() {
        return this.horse;
    }

    public ShareSettings share() {
        return this.share;
    }

    public HologramSettings hologram() {
        return this.hologram;
    }

    public StatsSettings stats() {
        return this.stats;
    }
}
