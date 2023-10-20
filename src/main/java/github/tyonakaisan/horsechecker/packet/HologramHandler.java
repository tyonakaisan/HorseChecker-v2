package github.tyonakaisan.horsechecker.packet;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.manager.StateManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@DefaultQualifier(NonNull.class)
public final class HologramHandler {
    private final HorseChecker horseChecker;
    private final HologramManager hologramManager;
    private final StateManager stateManager;
    private final Converter converter;

    private final Map<Player, Optional<AbstractHorse>> targetedHorseMap = new HashMap<>();
    private int targetRange;

    @Inject
    public HologramHandler(
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
                targetedHorseMap.computeIfAbsent(player, k -> Optional.empty());

                if (player.getTargetEntity(targetRange, false) instanceof AbstractHorse horse) {
                    targetedHorseMap.get(player).ifPresentOrElse(targetedHorse -> {
                        //同じ馬のとき
                        if (targetedHorse.equals(horse)) {
                            teleportHologram(horse);
                        } else {
                            //違うウマ
                            hideHologram(player, targetedHorse);
                        }
                    }, () -> {
                        //それら以外(?)
                        createOrShowHologram(player, horse);
                        targetedHorseMap.put(player, Optional.of(horse));
                    });
                } else {
                    targetedHorseMap.get(player).ifPresent(horse -> hideHologram(player, horse));
                    if (!player.isOnline() || !stateManager.state(player, "stats") || player.isInsideVehicle()) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(horseChecker, 0, 1);
    }

    public void createOrShowHologram(Player player, AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();

        if (!hologramManager.getHologramNames().contains(horseUUID)) {
            var horseStatsData = converter.convertHorseStats(horse);
            //ホログラム作成
            hologramManager.createHologram(horseStatsData, converter.horseStatsMessage(horseStatsData));
        }
        hologramManager.showHologram(horseUUID, player);
    }

    public void changeHologramText(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        if (hologramManager.getHologramNames().contains(horseUUID)) {
            var horseStatsData = converter.convertHorseStats(horse);
            //jumpの値変わらないの許せない
            //誤差あるけどmemo
            //x = ポーションのレベル
            //Jump = Math.pow(0.0308354 * x, 2) + 0.744631 * x)
            Component component = converter.horseStatsMessage(horseStatsData);
            hologramManager.changeHologramText(horseUUID, component);
        }
    }

    public void hideHologram(Player player, AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        hologramManager.hideHologram(horseUUID, player);
        this.targetedHorseMap.put(player, Optional.empty());
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
