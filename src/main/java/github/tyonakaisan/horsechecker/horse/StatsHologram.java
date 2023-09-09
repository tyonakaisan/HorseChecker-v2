package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.packet.HologramManager;
import net.kyori.adventure.text.Component;
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
    private final StateManager stateManager;
    private final Converter converter;

    private final HashMap<Player, AbstractHorse> targetedHorseMap = new HashMap<>();
    private final int targetRange;

    @Inject
    public StatsHologram(
            final HorseChecker horseChecker,
            final HologramManager hologramManager,
            final StateManager stateManager,
            final Converter converter,
            final ConfigFactory configFactory
    ) {
        this.horseChecker = horseChecker;
        this.hologramManager = hologramManager;
        this.stateManager = stateManager;
        this.converter = converter;

        this.targetRange = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();
    }

    public void show(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !stateManager.state(player, "stats") || player.isInsideVehicle()) {
                    this.cancel();
                    if (targetedHorseMap.get(player) == null) return;
                    hideHologram(player, targetedHorseMap.get(player));
                    targetedHorseMap.remove(player);
                    return;
                }

                if (player.getTargetEntity(targetRange, false) instanceof AbstractHorse horse) {
                    if (!targetedHorseMap.containsKey(player)) {
                        createOrShowHologram(player, horse);
                        targetedHorseMap.put(player, horse);
                    }
                    //同じウマみた時
                    if (targetedHorseMap.get(player).equals(horse)) {
                        //1.20.2でteleport_durationというのが来るらしいからそれでワンチャン滑らかにできる？
                        teleportHologram(horse);
                    } else {
                        //違うウマ
                        hideHologram(player, targetedHorseMap.get(player));
                        targetedHorseMap.remove(player);
                    }
                } else {
                    if (targetedHorseMap.get(player) == null) return;
                    hideHologram(player, targetedHorseMap.get(player));
                    targetedHorseMap.remove(player);
                }
            }
        }.runTaskTimer(horseChecker, 0, 1);
    }

    public void createOrShowHologram(Player player, AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();

        if (hologramManager.getHologramNames().contains(horse.getUniqueId().toString())) {
            //表示するプレイヤー
            hologramManager.showHologram(horseUUID, player);
        } else {
            var horseStatsData = converter.convertHorseStats(horse);

            //ホログラム作成
            hologramManager.createHologram(horseStatsData, converter.horseStatsMessage(horseStatsData));
            //表示するプレイヤー
            hologramManager.showHologram(horseUUID, player);
        }
    }

    public void changeHologramText(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        if (hologramManager.getHologramNames().contains(horseUUID)) {
            var horseStatsData = converter.convertHorseStats(horse);
            //jumpの値変わらないの許せない
            //誤差あるけどmemo x = ポーションのレベル
            //Jump = Math.pow(0.0308354 * x, 2) + 0.744631 * x)
            Component component = converter.horseStatsMessage(horseStatsData);
            hologramManager.changeHologramText(horseUUID, component);
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

            hologramManager.teleportHologram(horseUUID, horseLocation);
        }
    }
}
