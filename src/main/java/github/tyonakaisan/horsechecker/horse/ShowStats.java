package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.packet.holograms.HologramManager;
import github.tyonakaisan.horsechecker.utils.Converter;
import github.tyonakaisan.horsechecker.utils.HorseRank;
import net.kyori.adventure.key.Key;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@DefaultQualifier(NonNull.class)
public final class ShowStats {
    private final HorseChecker horseChecker;
    private final HologramManager hologramManager;
    private final HorseManager horseManager;
    private final StateManager stateManager;
    private final Converter converter;
    private final ConfigFactory configFactory;

    private final Map<Player, String> horseMap = new HashMap<>();

    @Inject
    public ShowStats(
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

    public void showStatsStart(Player player) {
        int targetRange = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !stateManager.isState(player, "stats")) {
                    this.cancel();
                    return;
                }

                if (player.getTargetEntity(targetRange, false) instanceof AbstractHorse horse &&
                        horseManager.isAllowedHorse(Objects.requireNonNull(player.getTargetEntity(targetRange, false)).getType())
                ) {
                    Location location = horse.getLocation();
                    String uuid = player.getUniqueId() + "+" + horse.getUniqueId();

                    //mapに含まれているか
                    if (!horseMap.containsKey(player)) {
                        //削除
                        deleteHologram(player, uuid);
                        //作成
                        createHologram(player, location, horse, uuid);
                        //更新開始
                        updateTargetMob(player, horse);
                    }
                } else {
                    deleteHologram(player, horseMap.get(player));
                }
            }
        }.runTaskTimer(horseChecker, 0, 1);
    }

    private void createHologram(Player player, Location location, AbstractHorse horse, String uuid) {
        horseMap.put(player, uuid);

        var horseStats = converter.convertHorseStats(horse);
        //大人チェック
        if (!horse.isAdult()) {
            Location babyLocation = location.clone().add(0, -1, 0);
            hologramManager.createHologram(babyLocation, uuid, horseStats.rank());
        } else {
            hologramManager.createHologram(location, uuid, horseStats.rank());
        }
        //ホログラム作成
        Component component = MiniMessage.miniMessage().deserialize("""
                                            Score: <rankcolor><rank></rankcolor>
                                            Speed: <#ffa500><speed></#ffa500>blocks/s
                                            Jump : <#ffa500><jump></#ffa500>blocks
                                            MaxHP: <#ffa500><health></#ffa500><red>♥</red>
                                            <owner>""",
                Formatter.number("speed", horseStats.speed()),
                Formatter.number("jump", horseStats.jump()),
                Formatter.number("health", horseStats.health()),
                Placeholder.parsed("owner", horseStats.ownerName()),
                Placeholder.parsed("rank", horseStats.rank()),
                TagResolver.resolver("rankcolor", Tag.styling(HorseRank.calcEvaluateRankColor(horseStats.rank())))
        );
        addHologram(uuid, component);
        //表示するプレイヤー
        hologramManager.initPlayer(uuid, player);
    }

    private void updateTargetMob(Player player, AbstractHorse horse) {
        int targetRange = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getTargetEntity(targetRange, false) == null) {
                    deleteHologram(player, horseMap.get(player));
                    this.cancel();
                } else {
                    //ここで極まれにnullが発生するけど今のところ発生頻度は低めなのでもし高ければ直すポイント
                    if (horseManager.isAllowedHorse(Objects.requireNonNull(player.getTargetEntity(targetRange, false)).getType())) {

                        AbstractHorse focusedHorse = (AbstractHorse) Objects.requireNonNull(player.getTargetEntity(targetRange, false));
                        String focusedHorseUUID = player.getUniqueId() + "+" + focusedHorse.getUniqueId();

                        if (horseMap.getOrDefault(player, "null").equalsIgnoreCase(focusedHorseUUID)) {
                            if (!horse.isAdult()) {
                                Location babyLocation = horse.getLocation().add(0, -1, 0);
                                //ホログラムのtp
                                teleportHologram(focusedHorseUUID, babyLocation);
                            } else {
                                //ホログラムのtp
                                teleportHologram(focusedHorseUUID, horse.getLocation());
                            }
                        } else {
                            //削除
                            deleteHologram(player, horseMap.get(player));
                            //作成
                            createHologram(player, focusedHorse.getLocation(), focusedHorse, focusedHorseUUID);
                            //更新開始
                            updateTargetMob(player, focusedHorse);
                            this.cancel();
                        }
                    } else {
                        deleteHologram(player, horseMap.get(player));
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(horseChecker, 0, 1);
    }

    private void addHologram(String hologramName, Component line) {
        hologramManager.getHologram(hologramName).addLine(line);
    }

    private void deleteHologram(Player player, String name) {
        hologramManager.deleteHologram(name, player);
        horseMap.remove(player);
    }

    private void teleportHologram(String hologramName, Location target) {
        hologramManager.getHologram(hologramName).teleport(target);
    }
}
