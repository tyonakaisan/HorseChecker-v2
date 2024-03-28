package github.tyonakaisan.horsechecker.config.primary;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
@SuppressWarnings("FieldMayBeFinal")
public final class PrimaryConfig {

    private HologramSettings hologram = new HologramSettings();
    private HorseSettings horse = new HorseSettings();
    private ShareSettings share = new ShareSettings();
    private StatsSettings stats = new StatsSettings();

    public HologramSettings hologram() {
        return this.hologram;
    }

    public HorseSettings horse() {
        return this.horse;
    }

    public ShareSettings share() {
        return this.share;
    }

    public StatsSettings stats() {
        return this.stats;
    }
}
