package github.tyonakaisan.horsechecker.horse;

public record HorseStatsRecord(
        double speed,
        double jump,
        int health,
        String ownerName,
        String rank
) {}