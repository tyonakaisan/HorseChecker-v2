package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import com.tyonakaisan.glowlib.glow.Glow;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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
    private final ConfigFactory configFactory;
    private final Messages messages;

    @Inject
    public HorseFinder(
            final HorseChecker horseChecker,
            final ConfigFactory configFactory,
            final Messages messages
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.messages = messages;
    }

    public void fromUuid(final UUID uuid, final Player showPlayer) {
        final List<Entity> entities = showPlayer.getWorld().getEntities();

        final var horseOptional = entities.stream()
                .filter(entity -> entity.getUniqueId().equals(uuid))
                .findFirst();

        horseOptional.ifPresentOrElse(entity -> {
            if (entity instanceof AbstractHorse horse) {
                this.showing(horse, showPlayer);
            }
        }, () -> showPlayer.sendMessage(this.messages.translatable(Messages.Style.ERROR, showPlayer, "breeding.notification.baby_horse_not_found")));
    }

    private void showing(final AbstractHorse horse, final Player player) {
        final Glow glow = Glow.glowing(new WrappedHorse(horse).getRank().glowColor(), horse.getUniqueId().toString());
        glow.addEntities(horse);
        glow.show(player);

        player.playSound(Sound.sound()
                .type(Key.key("minecraft:entity.experience_orb.pickup"))
                .pitch(1.5f)
                .build());

        Bukkit.getScheduler().runTaskLater(this.horseChecker, () -> glow.hide(player), this.configFactory.primaryConfig().horse().glowingTime());
    }

    public void showing(AbstractHorse horse, Player player, Glow.Color color) {
        final Glow glow = Glow.glowing(color, horse.getUniqueId().toString());
        glow.addEntities(horse);
        glow.show(player);

        player.playSound(Sound.sound()
                .type(Key.key("minecraft:entity.experience_orb.pickup"))
                .pitch(1.5f)
                .build());

        Bukkit.getScheduler().runTaskLater(this.horseChecker, () -> glow.hide(player), this.configFactory.primaryConfig().horse().glowingTime());
    }
}
