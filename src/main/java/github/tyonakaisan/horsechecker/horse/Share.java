package github.tyonakaisan.horsechecker.horse;

import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@DefaultQualifier(NonNull.class)
public final class Share {

    private final ConfigFactory configFactory;
    private final Server server;
    private final Converter converter;

    @Inject
    public Share(
            final ConfigFactory configFactory,
            final Converter converter,
            final Server server
    ) {
        this.configFactory = configFactory;
        this.converter = converter;
        this.server = server;
    }

    private final HashMap<UUID, Long> commandInterval = new HashMap<>();

    private boolean isShareable(Player player) {
        var targetRange = this.configFactory.primaryConfig().horse().targetRange();
        //ターゲットしてるエンティティがnullの場合
        if (player.getTargetEntity(targetRange, false) == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.TARGETED_ENTITY_IS_NULL.getMessageWithPrefix()));
            return false;
        }

        //shareが使用可能か
        if (!this.configFactory.primaryConfig().share().allowedHorseShare()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.NOT_ALLOWED_SHARE.getMessageWithPrefix()));
            return false;
        }

        //ターゲットしてる馬チェック
        if (player.getTargetEntity(targetRange, false) instanceof AbstractHorse horse) {
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
        int intervalTime = this.configFactory.primaryConfig().share().shareCommandIntervalTime();
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

        @Nullable Entity targetEntity = sender.getTargetEntity(this.configFactory.primaryConfig().horse().targetRange(), false);

        if (targetEntity instanceof AbstractHorse horse) {
            var horseStatsData = this.converter.convertHorseStats(horse);
            var horseNamePrefix = this.configFactory.primaryConfig().share().horseNamePrefix();

            Component broadcastMessage = MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE.get(),
                    Placeholder.parsed("prefix", Messages.PREFIX.get()),
                    Placeholder.styling("myhover", HoverEvent.showText(this.converter.horseStatsMessage(horseStatsData))),
                    Placeholder.parsed("random_message", horseNamePrefix.get(ThreadLocalRandom.current().nextInt(horseNamePrefix.size()))),
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
                targets.getPlayers().forEach(target -> target.sendMessage(broadcastMessage));
                sender.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BROADCAST_SHARE_SUCCESS.getMessageWithPrefix()));
            }
        }
    }

    private boolean ownerCheck(AbstractHorse horse, Player player) {
        if (horse.getOwner() == null) {
            return true;
        }

        if (this.configFactory.primaryConfig().share().ownerOnly()) {
            return Objects.requireNonNull(horse.getOwnerUniqueId()).equals(player.getUniqueId());
        } else {
            return true;
        }
    }
}
