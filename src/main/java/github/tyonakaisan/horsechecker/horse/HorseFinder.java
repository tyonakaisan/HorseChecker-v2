package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import com.tyonakaisan.glowlib.glow.Glow;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    private final HorseManager horseManager;

    @Inject
    public HorseFinder(
            final HorseChecker horseChecker,
            final Converter converter,
            final HorseManager horseManager
    ) {
        this.horseChecker = horseChecker;
        this.converter = converter;
        this.horseManager = horseManager;
    }

    public void fromUuid(UUID uuid, Player showPlayer) {
        List<Entity> entities = showPlayer.getWorld().getEntities();

        var horseOptional = entities.stream()
                .filter(entity -> entity.getUniqueId().equals(uuid))
                .findFirst();

        horseOptional.ifPresentOrElse(horse -> {
            if (horse instanceof AbstractHorse abstractHorse) {
                this.showing(abstractHorse, showPlayer);
                showPlayer.playSound(Sound.sound()
                        .type(Key.key("minecraft:entity.experience_orb.pickup"))
                        .pitch(1.5f)
                        .build());
            }
        }, () -> showPlayer.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BABY_HORSE_NOT_FOUND.getMessageWithShortPrefix())));
    }

    private void showing(AbstractHorse horse, Player player) {
        var horseData = this.converter.convertHorseStats(horse);
        Glow glow = Glow.glowing(horseData.rankData().glowColor(), horse.getUniqueId().toString());
        glow.addEntities(horse);
        glow.show(player);

        Bukkit.getScheduler().runTaskLater(this.horseChecker, () -> glow.hide(player), this.horseManager.glowingTime());
    }
}
