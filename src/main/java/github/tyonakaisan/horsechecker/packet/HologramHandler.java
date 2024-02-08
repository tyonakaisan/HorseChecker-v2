package github.tyonakaisan.horsechecker.packet;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.manager.StateManager;
import net.kyori.adventure.text.Component;
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
    private final ConfigFactory configFactory;
    private final HologramManager hologramManager;
    private final StateManager stateManager;
    private final Converter converter;

    private final Map<UUID, Optional<AbstractHorse>> targetedHorseMap = new HashMap<>();

    @Inject
    public HologramHandler(
            final HorseChecker horseChecker,
            final ConfigFactory configFactory,
            final HologramManager hologramManager,
            final StateManager stateManager,
            final Converter converter
    ) {
        this.horseChecker = horseChecker;
        this.configFactory = configFactory;
        this.hologramManager = hologramManager;
        this.stateManager = stateManager;
        this.converter = converter;
    }

    public void show(Player player) {
        var playerUuid = player.getUniqueId();
        this.targetedHorseMap.computeIfAbsent(playerUuid, k -> Optional.empty());
        var targetRange = this.configFactory.primaryConfig().horse().targetRange();
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
        }.runTaskTimer(this.horseChecker, 0, 2);
    }

    private void operateHologram(Player player, AbstractHorse horse) {
        var playerUuid = player.getUniqueId();
        this.targetedHorseMap.get(playerUuid).ifPresentOrElse(targetedHorse -> {
            //違う馬のとき
            if (!targetedHorse.equals(horse)) {
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
        var vehicleId = horse.getEntityId();

        //ホログラム作成
        this.hologramManager.createHologram(horseStatsData, this.converter.statsMessageResolver(horseStatsData, this.configFactory));
        this.hologramManager.showHologram(horseUuid, player, vehicleId);
        this.targetedHorseMap.put(playerUuid, Optional.of(horse));
    }

    public void hideHologram(Player player, AbstractHorse horse) {
        var playerUuid = player.getUniqueId();
        var horseStatsData = this.converter.convertHorseStats(horse);
        this.hologramManager.hideHologram(horseStatsData, player);
        this.targetedHorseMap.put(playerUuid, Optional.empty());
    }

    public void changeHologramText(AbstractHorse horse) {
        var horseUUID = horse.getUniqueId().toString();
        var horseStatsData = this.converter.convertHorseStats(horse);

        Component component = this.converter.statsMessageResolver(horseStatsData, this.configFactory);
        this.hologramManager.changeHologramText(horseUUID, component);
    }

    private boolean playerStateCheck(Player player) {
        return (!player.isOnline() || !this.stateManager.state(player, "stats") || player.isInsideVehicle());
    }
}
