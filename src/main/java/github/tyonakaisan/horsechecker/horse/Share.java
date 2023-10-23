package github.tyonakaisan.horsechecker.horse;

import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.manager.ShareManager;
import github.tyonakaisan.horsechecker.message.Messages;
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

@DefaultQualifier(NonNull.class)
public final class Share {
    private final Server server;
    private final HorseManager horseManager;
    private final ShareManager shareManager;
    private final Converter converter;
    private final Random random = new Random();

    @Inject
    public Share(
            final HorseManager horseManager,
            final ShareManager shareManager,
            final Converter converter,
            final Server server
    ) {
        this.horseManager = horseManager;
        this.shareManager = shareManager;
        this.converter = converter;
        this.server = server;
    }

    private final HashMap<UUID, Long> commandInterval = new HashMap<>();
    private final String[] randomMessage = {"イケイケな", "イマドキな", "可愛らしい", "ホットな", "ハンパない", "バズリ狙いの"};

    private boolean isShareable(Player player) {
        //ターゲットしてるエンティティがnullの場合
        if (player.getTargetEntity(this.horseManager.targetRange(), false) == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.TARGETED_ENTITY_IS_NULL.getMessageWithPrefix()));
            return false;
        }

        //shareが使用可能か
        if (!shareManager.isAllowedHorseShare()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.NOT_ALLOWED_SHARE.getMessageWithPrefix()));
            return false;
        }

        //ターゲットしてる馬チェック
        if (player.getTargetEntity(this.horseManager.targetRange(), false) instanceof AbstractHorse horse && horseManager.isAllowedHorse(horse.getType())) {
            //オーナーチェック
            if (ownerCheck(horse, player)) {
                return true;
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.DIFFERENT_OWNER.getMessageWithPrefix()));
                return false;
            }

        } else {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.UNSHAREABLE_ENTITY.getMessageWithPrefix()));
            return false;
        }
    }

    private boolean checkInterval(Player player) {
        int intervalTime = shareManager.shareCommandIntervalTime();
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

    public void broadcastShareMessage(Player player, MultiplePlayerSelector targets) {
        if (!isShareable(player)) {
            return;
        }

        if (!checkInterval(player)) {
            return;
        }

        AbstractHorse horse = (AbstractHorse) Objects.requireNonNull(player.getTargetEntity(this.horseManager.targetRange(), false));
        var horseStatsData = converter.convertHorseStats(horse);

        Component broadcastMessage = MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE.get(),
                Placeholder.parsed("prefix", Messages.PREFIX.get()),
                TagResolver.resolver("myhover", Tag.styling(HoverEvent.showText(converter.horseStatsMessage(horseStatsData)))),
                Placeholder.parsed("random_message", randomMessage[this.random.nextInt(randomMessage.length)]),
                Placeholder.parsed("horse_name", horseStatsData.horseName()),
                TagResolver.resolver("rankcolor", Tag.styling(horseStatsData.rankData().textColor())),
                Placeholder.parsed("player", player.getName()));

        //もしものため
        if (targets.getPlayers().isEmpty()) {
            server.forEachAudience(receiver -> {
                if (receiver instanceof Player) {
                    receiver.sendMessage(broadcastMessage);
                    player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE_SUCSESS.getMessageWithPrefix()));
                }
            });
        } else {
            targets.getPlayers().forEach(target -> target.sendMessage(broadcastMessage));
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE_SUCSESS.getMessageWithPrefix()));
        }
    }

    private boolean ownerCheck(AbstractHorse horse, Player player) {

        if (horse.getOwner() == null) return true;

        if (shareManager.ownerOnly()) {
            return Objects.requireNonNull(horse.getOwnerUniqueId()).equals(player.getUniqueId());
        } else {
            return true;
        }
    }
}
