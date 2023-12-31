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
import java.util.UUID;

@DefaultQualifier(NonNull.class)
public final class HologramHandler {

    private final HorseChecker horseChecker;
    private final HologramManager hologramManager;
    private final StateManager stateManager;
    private final HorseManager horseManager;
    private final Converter converter;

    private final Map<UUID, Optional<AbstractHorse>> targetedHorseMap = new HashMap<>();

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
        var playerUuid = player.getUniqueId();
        this.targetedHorseMap.computeIfAbsent(playerUuid, k -> Optional.empty());
        var targetRange = this.horseManager.targetRange();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getTargetEntity(targetRange, false) instanceof AbstractHorse horse) {
                    operateHologram(player, horse);
                } else {
                    targetedHorseMap.get(playerUuid).ifPresent(targetedHorse -> {
                        hideHologram(player, targetedHorse);
                        if (playerStateCheck(player)) this.cancel();
                    });
                }
            }
        }.runTaskTimer(this.horseChecker, 0L, 1L);
    }

    private void operateHologram(Player player, AbstractHorse horse) {
        var playerUuid = player.getUniqueId();
        this.targetedHorseMap.get(playerUuid).ifPresentOrElse(targetedHorse -> {
            //同じ馬のとき
            if (targetedHorse.equals(horse)) {
                this.teleportHologram(horse);
            } else {
                //違うウマ
                this.hideHologram(player, targetedHorse);
                this.createHologram(player, horse);
            }
            //それら以外(?)
        }, () -> this.createHologram(player, horse));
    }

    public void createHologram(Player player, AbstractHorse horse) {
        var playerUuid = player.getUniqueId();
        var horseUuid = horse.getUniqueId().toString();
        var horseStatsData = this.converter.convertHorseStats(horse);

        //ホログラム作成
        this.hologramManager.createHologram(horseStatsData, this.converter.horseStatsMessage(horseStatsData));
        this.hologramManager.showHologram(horseUuid, player);
        this.targetedHorseMap.put(playerUuid, Optional.of(horse));
    }

    public void hideHologram(Player player, AbstractHorse horse) {
        var playerUuid = player.getUniqueId();
        var horseUuid = horse.getUniqueId().toString();
        this.hologramManager.hideHologram(horseUuid, player);
        this.targetedHorseMap.put(playerUuid, Optional.empty());
    }

    public void changeHologramText(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        var horseStatsData = this.converter.convertHorseStats(horse);
        //jumpの値変わらないの許せない
        //誤差あるけどmemo
        //x = ポーションのレベル
        //Jump = Math.pow(0.0308354 * x, 2) + 0.744631 * x)
        Component component = this.converter.horseStatsMessage(horseStatsData);
        this.hologramManager.changeHologramText(horseUUID, component);
    }

    public void teleportHologram(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        Location horseLocation = horse.isAdult() ? horse.getLocation() : horse.getLocation().subtract(0, 1, 0);

        this.hologramManager.teleportHologram(horseUUID, horseLocation);
    }

    private boolean playerStateCheck(Player player) {
        return (!player.isOnline() || !this.stateManager.state(player, "stats") || player.isInsideVehicle());
    }
}
