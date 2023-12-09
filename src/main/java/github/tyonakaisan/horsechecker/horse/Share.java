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
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@DefaultQualifier(NonNull.class)
public final class Share {

    private final Server server;
    private final HorseManager horseManager;
    private final ShareManager shareManager;
    private final Converter converter;

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

    private boolean isShareable(Player player) {
        //ターゲットしてるエンティティがnullの場合
        if (player.getTargetEntity(this.horseManager.targetRange(), false) == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.TARGETED_ENTITY_IS_NULL.getMessageWithPrefix()));
            return false;
        }

        //shareが使用可能か
        if (!this.shareManager.isAllowedHorseShare()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.NOT_ALLOWED_SHARE.getMessageWithPrefix()));
            return false;
        }

        //ターゲットしてる馬チェック
        if (player.getTargetEntity(this.horseManager.targetRange(), false) instanceof AbstractHorse horse && this.horseManager.isAllowedHorse(horse.getType())) {
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
        int intervalTime = this.shareManager.shareCommandIntervalTime();
        var uuid = player.getUniqueId();

        if (this.commandInterval.containsKey(uuid)) {
            if (this.commandInterval.get(uuid) == -1L) {
                return true;
            } else {
                long timeElapsed = System.currentTimeMillis() - this.commandInterval.get(uuid);
                if (timeElapsed >= intervalTime) {
                    this.commandInterval.put(uuid, System.currentTimeMillis());
                    return true;
                } else {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                            Messages.COMMAND_INTERVAL.getMessageWithPrefix(),
                            Formatter.number("interval", ((intervalTime - (System.currentTimeMillis() - this.commandInterval.get(uuid))) / 1000))
                    ));
                }
            }
        } else {
            this.commandInterval.put(uuid, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public void broadcastShareMessage(Player sender, MultiplePlayerSelector targets) {
        if (!this.isShareable(sender)) {
            return;
        }

        if (!this.checkInterval(sender)) {
            return;
        }

        AbstractHorse horse = (AbstractHorse) Objects.requireNonNull(sender.getTargetEntity(this.horseManager.targetRange(), false));
        var horseStatsData = this.converter.convertHorseStats(horse);

        Component broadcastMessage = MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE.get(),
                Placeholder.parsed("prefix", Messages.PREFIX.get()),
                Placeholder.styling("myhover", HoverEvent.showText(this.converter.horseStatsMessage(horseStatsData))),
                Placeholder.parsed("random_message", this.shareManager.getHorseNamePrefix().get(ThreadLocalRandom.current().nextInt(this.shareManager.getHorseNamePrefix().size()))),
                Placeholder.parsed("horse_name", horseStatsData.horseName()),
                TagResolver.resolver("rankcolor", Tag.styling(horseStatsData.rankData().textColor())),
                Placeholder.parsed("player", sender.getName()));

        //もしものため
        if (targets.getPlayers().isEmpty()) {
            this.server.forEachAudience(receiver -> {
                if (receiver instanceof Player) {
                    receiver.sendMessage(broadcastMessage);
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE_SUCCESS.getMessageWithPrefix()));
                }
            });
        } else {
            targets.getPlayers().forEach(target -> {
                target.sendMessage(broadcastMessage);
            });
            sender.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE_SUCCESS.getMessageWithPrefix()));
        }
    }

    private boolean ownerCheck(AbstractHorse horse, Player player) {

        if (horse.getOwner() == null) return true;

        if (this.shareManager.ownerOnly()) {
            return Objects.requireNonNull(horse.getOwnerUniqueId()).equals(player.getUniqueId());
        } else {
            return true;
        }
    }
}
