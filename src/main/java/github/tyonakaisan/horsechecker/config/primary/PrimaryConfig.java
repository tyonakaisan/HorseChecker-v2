package github.tyonakaisan.horsechecker.config.primary;

import org.bukkit.entity.EntityType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigSerializable
@DefaultQualifier(NonNull.class)
public final class PrimaryConfig {

    private HorseSettings horse = new HorseSettings();

    private ShareSettings share = new ShareSettings();

    private Map<String, String> customPlaceholders = Map.of();

    public HorseSettings horse() {
        return this.horse;
    }

    public ShareSettings share() {
        return this.share;
    }

    public Set<EntityType> allowedMOBs() {
        return horse().getAllowedMobs().stream()
                .map(String::toUpperCase)
                .map(EntityType::valueOf)
                .collect(Collectors.toSet());
    }
}
