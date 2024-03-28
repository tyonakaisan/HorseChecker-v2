package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import com.tyonakaisan.glowlib.glow.Glow;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.horse.HorseFinder;
import github.tyonakaisan.horsechecker.horse.WrappedHorse;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

@DefaultQualifier(NonNull.class)
public final class HorseBreedListener implements Listener {

    private final StateManager stateManager;
    private final ConfigFactory configFactory;
    private final Messages messages;
    private final HorseFinder horseFinder;

    @Inject
    public HorseBreedListener(
            final StateManager stateManager,
            final Messages messages,
            final ConfigFactory configFactory,
            final HorseFinder horseFinder
    ) {
        this.stateManager = stateManager;
        this.messages = messages;
        this.configFactory = configFactory;
        this.horseFinder = horseFinder;
    }

    @EventHandler
    public void onCanceledBreed(final PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();

        //toggleチェック
        //繫殖させるためのアイテムか
        if (!this.stateManager.state(player, "breed") || !this.isBreedItem(itemStack)) return;
        if (!(event.getRightClicked() instanceof final AbstractHorse horse)) return;

        final var wrappedHorse = new WrappedHorse(horse);

        //繫殖クールタイム中&体力がMAXであればイベントキャンセル
        if (wrappedHorse.getAge() > 0 && wrappedHorse.getHealth() == wrappedHorse.getMaxHealth()) {
            player.sendActionBar(
                    this.messages.translatable(
                            Messages.Style.ERROR,
                            player,
                            "breeding.normal_cool_time",
                            TagResolver.builder()
                                    .tag("cool_time", Tag.selfClosingInserting(Component.text(wrappedHorse.getBreedingCoolTime())))
                                    .build()));
            event.setCancelled(true);
            //繫殖モード中(ハートが出てる時)&体力がMAXであればイベントキャンセル
        } else if (wrappedHorse.getLoveModeTicks() > 0 && wrappedHorse.getHealth() == wrappedHorse.getMaxHealth()) {
            player.sendActionBar(
                    this.messages.translatable(
                            Messages.Style.ERROR,
                            player,
                            "breeding.current_love_mode_time",
                            TagResolver.builder()
                                    .tag("cool_time", Tag.selfClosingInserting(Component.text(wrappedHorse.getLoveModeTime())))
                                    .build()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreeding(final EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player player) {

            if (!this.stateManager.state(player, "breed_notification")) {
                return;
            }

            if (event.getEntity() instanceof AbstractHorse childrenHorse
                    && event.getMother() instanceof AbstractHorse motherHorse
                    && event.getFather() instanceof AbstractHorse fatherHorse) {

                final var wrappedChildren = new WrappedHorse(childrenHorse);
                final var wrappedMother = new WrappedHorse(motherHorse);
                final var wrappedFather = new WrappedHorse(fatherHorse);

                player.sendMessage(
                        this.messages.translatable(Messages.Style.SUCCESS, player, "breeding.notification",
                                TagResolver.builder()
                                        .tag("call", Tag.styling(style ->
                                                style.clickEvent(ClickEvent.callback(audience -> {
                                                    if (audience instanceof Player callPlayer) {
                                                        this.horseFinder.fromUuid(childrenHorse.getUniqueId(), callPlayer);
                                                        this.horseFinder.showing(motherHorse, callPlayer, Glow.Color.RED);
                                                        this.horseFinder.showing(fatherHorse, callPlayer, Glow.Color.BLUE);
                                                    }
                                                }, builder -> builder.uses(3)))))
                                        .tag("hover", Tag.styling(style ->
                                                style.hoverEvent(HoverEvent.showText(Component.text()
                                                        .append(Converter.withParentsStatsMessageResolver(this.configFactory, wrappedChildren, wrappedMother, wrappedFather))))))
                                        .build()));
            }
        }
    }

    private boolean isBreedItem(final ItemStack itemStack) {
        List<Material> breedItems = this.configFactory.primaryConfig().horse().nonRepeatableItems();
        return breedItems.contains(itemStack.getType());
    }
}
