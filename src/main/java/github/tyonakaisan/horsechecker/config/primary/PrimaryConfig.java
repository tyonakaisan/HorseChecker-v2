package github.tyonakaisan.horsechecker.config.primary;

import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public final class PrimaryConfig {

    private HorseSettings horse = new HorseSettings();
    private ShareSettings share = new ShareSettings();

    public HorseSettings horse() {
        return this.horse;
    }

    public ShareSettings share() {
        return this.share;
    }

    public List<EntityType> allowedMobs() {
        return horse().allowedMobs().entrySet().stream()
                .filter(entry -> entry.getValue().equals(true))
                .map(Map.Entry::getKey)
                .map(String::toUpperCase)
                .map(EntityType::valueOf)
                .toList();
    }
}
