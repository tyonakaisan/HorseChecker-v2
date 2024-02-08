package github.tyonakaisan.horsechecker.horse;

import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.Tag;
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
    private final Messages messages;
    private final Converter converter;

    @Inject
    public Share(
            final ConfigFactory configFactory,
            final Converter converter,
            final Messages messages,
            final Server server
    ) {
        this.configFactory = configFactory;
        this.converter = converter;
        this.messages = messages;
        this.server = server;
    }

    private final HashMap<UUID, Long> commandInterval = new HashMap<>();

    private boolean isShareable(Player player) {
        var targetRange = this.configFactory.primaryConfig().horse().targetRange();
        //ターゲットしてるエンティティがnullの場合
        if (player.getTargetEntity(targetRange, false) == null) {
            player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "share.error.targeted_horse_is_null"));
            return false;
        }

        //shareが使用可能か
        if (!this.configFactory.primaryConfig().share().allowedHorseShare()) {
            player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "share.error.not_allowed_share"));
            return false;
        }

        //ターゲットしてる馬チェック
        if (player.getTargetEntity(targetRange, false) instanceof AbstractHorse horse) {
            //オーナーチェック
            if (this.ownerCheck(horse, player)) {
                return true;
            } else {
                var horseStats = this.converter.convertHorseStats(horse);
                player.sendMessage(
                        this.messages.translatable(
                                Messages.Style.ERROR,
                                player,
                                "share.error.different_owner",
                                TagResolver.builder()
                                        .tag("owner", Tag.selfClosingInserting(horseStats.plainOwnerName()))
                                        .build()));
                return false;
            }

        } else {
            player.sendMessage(this.messages.translatable(Messages.Style.ERROR, player, "share.error.un_shareable_entity"));
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
                    var interval = (intervalTime - (System.currentTimeMillis() - this.commandInterval.get(uuid))) / 1000;
                    player.sendMessage(
                            this.messages.translatable(
                                    Messages.Style.ERROR,
                                    player,
                                    "command.error.command_interval",
                                    TagResolver.builder()
                                            .tag("interval", Tag.selfClosingInserting(
                                                    Component.text(interval)))
                                            .build()
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
            //もしものため
            if (targets.getPlayers().isEmpty()) {
                this.server.forEachAudience(player -> {
                    if (player instanceof Player receiver) {
                        this.sendBroadCastMessage(sender, receiver, horse);
                    }
                });
                sender.sendActionBar(this.messages.translatable(Messages.Style.SUCCESS, sender, "share.success.broadcast_info"));
            } else {
                targets.getPlayers().forEach(receiver -> this.sendBroadCastMessage(sender, receiver, horse));
                sender.sendActionBar(this.messages.translatable(Messages.Style.SUCCESS, sender, "share.success.broadcast_info"));
            }
        }
    }

    private void sendBroadCastMessage(Player sender, Player receiver, AbstractHorse horse) {
        var horseStats = this.converter.convertHorseStats(horse);
        var horseNamePrefix = this.configFactory.primaryConfig().share().horseNamePrefix();

        var broadcast = this.messages.translatable(
                Messages.Style.INFO,
                receiver,
                "share.success.broadcast",
                TagResolver.builder()
                        .tag("hover", Tag.styling(style ->
                                style.hoverEvent(HoverEvent.showText(this.converter.statsMessageResolver(horseStats, this.configFactory)))))
                        .tag("random_prefix",
                                Tag.selfClosingInserting(Component.text(horseNamePrefix.get(ThreadLocalRandom.current().nextInt(horseNamePrefix.size())))))
                        .tag("horse_name", Tag.selfClosingInserting(horseStats.horseName()))
                        .tag("rank_color", Tag.styling(style -> style.color(horseStats.rankData().textColor())))
                        .tag("player", Tag.selfClosingInserting(sender.displayName()))
                        .build()
        );

        receiver.sendMessage(broadcast);
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
