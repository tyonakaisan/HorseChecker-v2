package github.tyonakaisan.horsechecker.horse;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.tyonakaisan.yummytoast.Toast;
import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@DefaultQualifier(NonNull.class)
public final class Share {

    private final ConfigFactory configFactory;
    private final Server server;
    private final Messages messages;

    @Inject
    public Share(
            final ConfigFactory configFactory,
            final Messages messages,
            final Server server
    ) {
        this.configFactory = configFactory;
        this.messages = messages;
        this.server = server;
    }

    private final HashMap<UUID, Instant> intervalMap = new HashMap<>();

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
                player.sendMessage(
                        this.messages.translatable(
                                Messages.Style.ERROR,
                                player,
                                "share.error.different_owner",
                                TagResolver.builder()
                                        .tag("owner", Tag.selfClosingInserting(new WrappedHorse(horse).plainOwnerName()))
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
        var now = Instant.now();

        var coolTime = this.intervalMap.getOrDefault(uuid, now);

        if (now.isBefore(coolTime)) {
            player.sendMessage(
                    this.messages.translatable(
                            Messages.Style.ERROR,
                            player,
                            "command.error.command_interval",
                            TagResolver.builder()
                                    .tag("interval", Tag.selfClosingInserting(
                                            Component.text(Duration.between(now, coolTime).toSeconds() + 1)))
                                    .build()
                    ));
            return false;
        } else {
            this.intervalMap.put(uuid, now.plusSeconds(intervalTime));
            return true;
        }
    }

    public void broadcastShareMessage(Player sender, Collection<Player> targets) {
        if (!this.isShareable(sender)) {
            return;
        }

        if (!this.checkInterval(sender)) {
            return;
        }

        @Nullable Entity targetEntity = sender.getTargetEntity(this.configFactory.primaryConfig().horse().targetRange(), false);

        if (targetEntity instanceof AbstractHorse horse) {
            //もしものため
            if (targets.isEmpty()) {
                this.server.forEachAudience(player -> {
                    if (player instanceof Player receiver) {
                        this.sendBroadCastMessage(sender, receiver, horse);
                    }
                });
            } else {
                targets.forEach(receiver -> this.sendBroadCastMessage(sender, receiver, horse));
            }

            var icon = new ItemStack(Material.PLAYER_HEAD);

            icon.editMeta(meta -> {
                if (meta instanceof SkullMeta skullMeta) {
                    var playerProfile = Bukkit.createProfile(UUID.randomUUID(), "horsechecker");
                    var playerProperty = new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=");
                    playerProfile.setProperty(playerProperty);
                    skullMeta.setPlayerProfile(playerProfile);
                }
            });

            Toast.make()
                    .icon(icon)
                    .title(this.messages.translatable(Messages.Style.SUCCESS, sender, "share.success.broadcast_info"))
                    .frameType(Toast.FrameType.GOAL)
                    .sendTo(sender);
        }
    }

    private void sendBroadCastMessage(Player sender, Player receiver, AbstractHorse horse) {
        var wrappedHorse = new WrappedHorse(horse);
        var horseNamePrefix = this.configFactory.primaryConfig().share().horseNamePrefix();

        var broadcast = this.messages.translatable(
                Messages.Style.INFO,
                receiver,
                "share.success.broadcast",
                TagResolver.builder()
                        .tag("hover", Tag.styling(style ->
                                style.hoverEvent(HoverEvent.showText(Converter.statsMessageResolver(this.configFactory, wrappedHorse)))))
                        .tag("random_prefix",
                                Tag.selfClosingInserting(Component.text(horseNamePrefix.get(ThreadLocalRandom.current().nextInt(horseNamePrefix.size())))))
                        .tag("horse_name", Tag.selfClosingInserting(wrappedHorse.horseName()))
                        .tag("rank_color", Tag.styling(style -> style.color(wrappedHorse.getRank().textColor())))
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
