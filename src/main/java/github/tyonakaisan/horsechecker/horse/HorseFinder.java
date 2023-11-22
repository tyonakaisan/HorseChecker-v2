package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import com.tyonakaisan.glowlib.glow.Glow;
import github.tyonakaisan.horsechecker.HorseChecker;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;
import java.util.UUID;

@DefaultQualifier(NonNull.class)
public final class HorseFinder {

    private final HorseChecker horseChecker;
    private final Converter converter;

    @Inject
    public HorseFinder(
            final HorseChecker horseChecker,
            final Converter converter
    ) {
        this.horseChecker = horseChecker;
        this.converter = converter;
    }

    public void fromUuid(UUID uuid, Player player) {
        List<Entity> entities = player.getWorld().getEntities();

        var horseOptional = entities.stream()
                .filter(entity -> entity.getUniqueId().equals(uuid))
                .findFirst();

        horseOptional.ifPresent(horse -> {
            if (horse instanceof AbstractHorse abstractHorse) {
                this.glowing(abstractHorse, player);
            }
        });
    }

    private void glowing(AbstractHorse horse, Player player) {
        var horseData = this.converter.convertHorseStats(horse);
        Glow glow = Glow.glowing(horseData.rankData().glowColor(), horse.getUniqueId().toString());
        glow.entities(horse);
        glow.show(player);

        Bukkit.getScheduler().runTaskLater(this.horseChecker, () -> glow.hide(player), 400L);
    }
}
