package github.tyonakaisan.horsechecker.packet;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.manager.HorseManager;
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
import java.util.Optional;

@DefaultQualifier(NonNull.class)
public final class HologramHandler {

    private final HorseChecker horseChecker;
    private final HologramManager hologramManager;
    private final StateManager stateManager;
    private final HorseManager horseManager;
    private final Converter converter;

    private final Map<Player, Optional<AbstractHorse>> targetedHorseMap = new HashMap<>();

    @Inject
    public HologramHandler(
            final HorseChecker horseChecker,
            final HologramManager hologramManager,
            final StateManager stateManager,
            final HorseManager horseManager,
            final Converter converter
    ) {
        this.horseChecker = horseChecker;
        this.hologramManager = hologramManager;
        this.stateManager = stateManager;
        this.horseManager = horseManager;
        this.converter = converter;
    }

    public void show(Player player) {
        targetedHorseMap.computeIfAbsent(player, k -> Optional.empty());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getTargetEntity(horseManager.targetRange(), false) instanceof AbstractHorse horse) {
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
                    targetedHorseMap.get(player).ifPresent(horse -> {
                        hideHologram(player, horse);
                        if (playerStateCheck(player)) this.cancel();
                    });
                }
            }
        }.runTaskTimer(this.horseChecker, 0, 1);
    }

    public void createOrShowHologram(Player player, AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();

        if (!this.hologramManager.getHologramNames().contains(horseUUID)) {
            var horseStatsData = this.converter.convertHorseStats(horse);
            //ホログラム作成
            this.hologramManager.createHologram(horseStatsData, this.converter.horseStatsMessage(horseStatsData));
        }
        this.hologramManager.showHologram(horseUUID, player);
    }

    public void hideHologram(Player player, AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        if (this.hologramManager.getHologramNames().contains(horseUUID)) {
            this.hologramManager.hideHologram(horseUUID, player);
            this.targetedHorseMap.put(player, Optional.empty());
        }
    }

    public void changeHologramText(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        if (this.hologramManager.getHologramNames().contains(horseUUID)) {
            var horseStatsData = this.converter.convertHorseStats(horse);
            //jumpの値変わらないの許せない
            //誤差あるけどmemo
            //x = ポーションのレベル
            //Jump = Math.pow(0.0308354 * x, 2) + 0.744631 * x)
            Component component = this.converter.horseStatsMessage(horseStatsData);
            this.hologramManager.changeHologramText(horseUUID, component);
        }
    }

    public void teleportHologram(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();

        if (this.hologramManager.getHologramData(horseUUID).location().equals(horse.getLocation())) return;

        if (this.hologramManager.getHologramNames().contains(horseUUID)) {
            Location horseLocation = horse.isAdult() ? horse.getLocation() : horse.getLocation().subtract(0, 1, 0);

            this.hologramManager.teleportHologram(horseUUID, horseLocation);
        }
    }

    private boolean playerStateCheck(Player player) {
        return (!player.isOnline() || !this.stateManager.state(player, "stats") || player.isInsideVehicle());
    }
}
