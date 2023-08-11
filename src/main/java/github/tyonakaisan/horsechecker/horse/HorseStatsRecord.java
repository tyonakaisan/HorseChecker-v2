package github.tyonakaisan.horsechecker.horse;

import org.bukkit.Location;

import java.util.UUID;

public record HorseStatsRecord(
        double speed,
        double jump,
        int health,
        String ownerName,
        String horseName,
        UUID uuid,
        Location location,
        String rank
) {}