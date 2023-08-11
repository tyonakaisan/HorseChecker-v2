package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
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
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class NewStatsHologram {

    private final HorseChecker horseChecker;
    private final HologramManager hologramManager;
    private final HorseManager horseManager;
    private final StateManager stateManager;
    private final Converter converter;
    private final ConfigFactory configFactory;

    @Inject
    public NewStatsHologram(
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

    public void createHologram(Player player, AbstractHorse horse) {
        var horseStats = converter.convertHorseStats(horse);

        if (!hologramManager.getHologramNames().contains(horseStats.uuid().toString())) {
            hologramManager.createHologram(horseStats.location(), horseStats.uuid().toString(), horseStats.rank());

            String stats = Messages.STATS_RESULT_SCORE.get()
                    + Messages.STATS_RESULT_SPEED.get()
                    + Messages.STATS_RESULT_JUMP.get()
                    + Messages.STATS_RESULT_HP.get()
                    + Messages.STATS_RESULT_OWNER.get();

            //ホログラム作成
            Component component = MiniMessage.miniMessage().deserialize(stats,
                    Formatter.number("speed", horseStats.speed()),
                    Formatter.number("jump", horseStats.jump()),
                    Formatter.number("health", horseStats.health()),
                    Placeholder.parsed("owner", horseStats.ownerName()),
                    Placeholder.parsed("rank", horseStats.rank()),
                    TagResolver.resolver("rankcolor", Tag.styling(HorseRank.calcEvaluateRankColor(horseStats.rank())))
            );
            hologramManager.getHologram(horseStats.uuid().toString()).addLine(component);
        }
        //表示するプレイヤー
        hologramManager.initPlayer(horseStats.uuid().toString(), player);
    }

    public void hideHologram(Player player, AbstractHorse horse) {
        var horseStats = converter.convertHorseStats(horse);
        hologramManager.hideHologram(horseStats.uuid().toString(), player);
    }

    public void teleportHologram(AbstractHorse horse) {
        var horseStats = converter.convertHorseStats(horse);

        if (hologramManager.getHologramNames().contains(horseStats.uuid().toString())) {
            hologramManager.getHologram(horseStats.uuid().toString()).teleport(horseStats.location());
        }
    }
}
