package github.tyonakaisan.horsechecker.horse;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.utils.Converter;
import github.tyonakaisan.horsechecker.utils.HorseRank;
import github.tyonakaisan.horsechecker.utils.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Server;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@DefaultQualifier(NonNull.class)
public final class Share {
    private final Server server;
    private final ConfigFactory configFactory;
    private final HorseManager horseManager;
    private final Converter converter;
    private final Random random = new Random();

    @Inject
    public Share(
            final ConfigFactory configFactory,
            final HorseManager horseManager,
            final Converter converter,
            final Server server
    ) {
        this.configFactory = configFactory;
        this.horseManager = horseManager;
        this.converter = converter;
        this.server = server;
    }

    private final HashMap<UUID, Long> commandInterval = new HashMap<>();
    private final String[] randomMessage = {"イケイケな", "イマドキな", "可愛らしい", "ホットな", "ハンパない", "バズリ狙いの"};

    private boolean isShareable(Player player) {
        int targetRange = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();

        if (player.getTargetEntity(targetRange, false) == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.TARGETED_ENTITY_IS_NULL.getMessageWithPrefix()));
            return false;
        }
        if (!Objects.requireNonNull(configFactory.primaryConfig()).horse().allowedHorseShare()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.NOT_ALLOWED_SHARE.getMessageWithPrefix()));
            return false;
        }
        if (horseManager.isAllowedHorse(Objects.requireNonNull(player.getTargetEntity(targetRange, false)).getType())) {
            return true;
        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.UNSHAREABLE_ENTITY.getMessageWithPrefix()));
            return false;
        }
    }

    private boolean checkInterval(Player player) {
        int intervalTime = Objects.requireNonNull(configFactory.primaryConfig()).horse().horseShareInterval() * 1000;
        var uuid = player.getUniqueId();

        if (commandInterval.containsKey(uuid)) {
            if (commandInterval.get(uuid) == -1L) {
                return true;
            } else {
                long timeElapsed = System.currentTimeMillis() - commandInterval.get(uuid);
                if (timeElapsed >= intervalTime) {
                    commandInterval.put(uuid, System.currentTimeMillis());
                    return true;
                } else {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                            Messages.COMMAND_INTERVAL.getMessageWithPrefix(),
                            Formatter.number("interval", ((intervalTime - (System.currentTimeMillis() - commandInterval.get(uuid))) / 1000))
                    ));
                }
            }
        } else {
            commandInterval.put(uuid, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public void broadcastShareMessage(Player player) {
        if (!isShareable(player)) {
            return;
        }

        if (!checkInterval(player)) {
            return;
        }

        int targetRange = Objects.requireNonNull(configFactory.primaryConfig()).horse().targetRange();
        AbstractHorse horse = (AbstractHorse) Objects.requireNonNull(player.getTargetEntity(targetRange, false));
        var horseStats = converter.convertHorseStats(horse);

        String shareString = "<myhover>" +
                Messages.PREFIX.getMessage() +
                "<color:#5cb8ff><player></color><white>が" +
                randomMessage[this.random.nextInt(randomMessage.length)] +
                "<rankcolor>馬</rankcolor>を共有しました！</white><newline>" +
                "<b><gray>[カーソルを合わせて表示]</gray></b></myhover>";

        Component hoverMiniMessage = MiniMessage.miniMessage().deserialize("""
                        Score: <rankcolor><rank></rankcolor>
                        Speed: <#ffa500><speed></#ffa500>blocks/s
                        Jump: <#ffa500><jump></#ffa500>blocks
                        MaxHP: <#ffa500><health></#ffa500><red>♥</red>
                        <owner>""",
                Formatter.number("speed", horseStats.speed()),
                Formatter.number("jump", horseStats.jump()),
                Formatter.number("health", horseStats.health()),
                Placeholder.parsed("owner", horseStats.ownerName()),
                Placeholder.parsed("rank", horseStats.rank()),
                TagResolver.resolver("rankcolor", Tag.styling(HorseRank.calcEvaluateRankColor(horseStats.rank())))
        );

        server.forEachAudience(receiver -> {
            if (receiver instanceof Player) {
                receiver.sendMessage(MiniMessage.miniMessage().deserialize(shareString,
                        TagResolver.resolver("myhover", Tag.styling(HoverEvent.showText(hoverMiniMessage))),
                        TagResolver.resolver("rankcolor", Tag.styling(HorseRank.calcEvaluateRankColor(horseStats.rank()))),
                        Placeholder.parsed("player", player.getName()))
                );
            }
        });

    }
}
