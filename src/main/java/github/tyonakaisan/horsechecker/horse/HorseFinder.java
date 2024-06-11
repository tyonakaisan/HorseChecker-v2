package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tyonakaisan.glowlib.glow.Glow;
import fr.skytasul.glowingentities.GlowingEntities;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.utils.GlowColorConverter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;
import java.util.UUID;

@DefaultQualifier(NonNull.class)
@Singleton
public final class HorseFinder {

    private final HorseChecker horseChecker;
    private final ConfigFactory configFactory;
    private final Messages messages;
    private final ComponentLogger logger;
    private final Server server;
    private final GlowingEntities glowingEntities;

    @Inject
    public HorseFinder(
            final HorseChecker horseChecker,
            final ConfigFactory configFactory,
            final Messages messages,
            final ComponentLogger logger,
            final Server server
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.messages = messages;
        this.logger = logger;
        this.server = server;
        this.glowingEntities = new GlowingEntities(horseChecker);
    }

    public void fromUuid(final UUID uuid, final Player showPlayer) {
        final List<Entity> entities = showPlayer.getWorld().getEntities();

        final var horseOptional = entities.stream()
                .filter(entity -> entity.getUniqueId().equals(uuid))
                .findFirst();

        horseOptional
                .filter(AbstractHorse.class::isInstance)
                .map(AbstractHorse.class::cast)
                .ifPresentOrElse(horse -> this.showing(horse, showPlayer),
                        () -> showPlayer.sendMessage(this.messages.translatable(Messages.Style.ERROR, showPlayer, "breeding.notification.baby_horse_not_found")));
    }

    private void showing(final AbstractHorse horse, final Player player) {
        this.showing(horse, player, new WrappedHorse(horse).getRank().glowColor());
    }

    public void showing(final AbstractHorse horse, final Player player, final Glow.Color color) {
        try {
            this.glowingEntities.setGlowing(horse, player, GlowColorConverter.convert(color));
        } catch (ReflectiveOperationException e) {
            this.logger.error("Failed to glowing.", e);
        }

        player.playSound(this.configFactory.primaryConfig().horse().findSound());

        this.server.getScheduler().runTaskLater(this.horseChecker, () -> {
            try {
                this.glowingEntities.unsetGlowing(horse, player);
            } catch (ReflectiveOperationException e) {
                this.logger.error("Failed to deactivate glowing.", e);
            }
        }, this.configFactory.primaryConfig().horse().glowingTime());
        /*
        final Glow glow = Glow.glowing(color, horse.getUniqueId().toString());
        glow.addEntities(horse);
        glow.show(player); // doesn't work. because optional empty?

        player.playSound(this.configFactory.primaryConfig().horse().findSound());

        Bukkit.getScheduler().runTaskLater(this.horseChecker, () -> glow.hide(player), this.configFactory.primaryConfig().horse().glowingTime());
         */
    }
}
