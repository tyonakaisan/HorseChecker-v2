package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import github.tyonakaisan.horsechecker.packet.holograms.HologramManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.Objects;

@Singleton
@DefaultQualifier(NonNull.class)
public final class StatsHologram {

    private final HorseChecker horseChecker;
    private final HologramManager hologramManager;
    private final HorseManager horseManager;
    private final StateManager stateManager;
    private final Converter converter;
    private final ConfigFactory configFactory;

    private final String stats = Messages.STATS_RESULT_SCORE.get()
            + Messages.STATS_RESULT_SPEED.get()
            + Messages.STATS_RESULT_JUMP.get()
            + Messages.STATS_RESULT_HP.get()
            + Messages.STATS_RESULT_OWNER.get();

    private final HashMap<Player, AbstractHorse> horseMap = new HashMap<>();

    @Inject
    public StatsHologram(
            final HorseChecker horseChecker,
            final HologramManager hologramManager,
            final HorseManager horseManager,
            final StateManager stateManager,
            final Converter converter,
            final ConfigFactory configFactory
    ) {
        this.horseChecker = horseChecker;
        this.hologramManager = hologramManager;
        this.horseManager = horseManager;
        this.stateManager = stateManager;
        this.converter = converter;
        this.configFactory = configFactory;
    }

    public void show(Player player) {
        int targetRange = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !stateManager.state(player, "stats") || player.isInsideVehicle()) {
                    this.cancel();
                    if (horseMap.get(player) == null) return;
                    hideHologram(player, horseMap.get(player));
                    horseMap.remove(player);
                    return;
                }

                if (player.getTargetEntity(targetRange, false) instanceof AbstractHorse horse) {
                    if (!horseMap.containsKey(player)) {
                        createHologram(player, horse);
                        horseMap.put(player, horse);
                    }
                    //違うウマみた時
                    if (!horseMap.get(player).equals(horse)) {
                        hideHologram(player, horseMap.get(player));
                        horseMap.remove(player);
                    } else {
                        //同じ馬
                        teleportHologram(horse);
                    }
                } else {
                    if (horseMap.get(player) == null) return;
                    hideHologram(player, horseMap.get(player));
                    horseMap.remove(player);
                }
            }
        }.runTaskTimer(horseChecker, 0, 1);
    }

    public void createHologram(Player player, AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();

        if (hologramManager.getHologramNames().contains(horse.getUniqueId().toString())) {
            //表示するプレイヤー
            hologramManager.initPlayer(horseUUID, player);
        } else {
            var horseStats = converter.convertHorseStats(horse);
            hologramManager.createHologram(horseStats.location(), horseStats.uuid().toString(), horseStats.rank());

            //ホログラム作成
            Component component = MiniMessage.miniMessage().deserialize(this.stats,
                    Formatter.number("speed", horseStats.speed()),
                    Formatter.number("jump", horseStats.jump()),
                    Formatter.number("health", horseStats.health()),
                    Placeholder.parsed("owner", horseStats.ownerName()),
                    Placeholder.parsed("rank", horseStats.rank()),
                    TagResolver.resolver("rankcolor", Tag.styling(HorseRank.calcEvaluateRankColor(horseStats.rank())))
            );
            hologramManager.getHologram(horseStats.uuid().toString()).addLine(component);
            //表示するプレイヤー
            hologramManager.initPlayer(horseUUID, player);
        }
    }

    public void hideHologram(Player player, AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        hologramManager.hideHologram(horseUUID, player);
    }

    public void teleportHologram(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();

        if (hologramManager.getHologramNames().contains(horseUUID)) {
            Location horseLocation = horse.getLocation();
            if (!horse.isAdult()) horseLocation = horseLocation.add(0, -1, 0);
            hologramManager.getHologram(horseUUID).teleport(horseLocation);
        }
    }
}
