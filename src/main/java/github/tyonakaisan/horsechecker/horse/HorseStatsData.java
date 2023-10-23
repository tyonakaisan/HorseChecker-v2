package github.tyonakaisan.horsechecker.horse;

import org.bukkit.Location;

import java.util.UUID;

public record HorseStatsData(
        double speed,
        double jump,
        int health,
        String ownerName,
        String horseName,
        UUID uuid,
        Location location,
        HorseRank.HorseRankData rankData
) {}