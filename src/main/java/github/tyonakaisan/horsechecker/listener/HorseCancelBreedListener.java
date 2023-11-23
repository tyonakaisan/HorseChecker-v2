package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.horse.HorseFinder;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class HorseCancelBreedListener implements Listener {

    private final HorseManager horseManager;
    private final StateManager stateManager;
    private final HorseFinder horseFinder;
    private final Converter converter;

    @Inject
    public HorseCancelBreedListener(
            final HorseManager horseManager,
            final StateManager stateManager,
            final HorseFinder horseFinder,
            final Converter converter
    ) {
        this.horseManager = horseManager;
        this.stateManager = stateManager;
        this.horseFinder = horseFinder;
        this.converter = converter;
    }

    @EventHandler
    public void onCanceledBreed(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();

        //toggleチェック
        //繫殖させるためのアイテムか
        if (!this.stateManager.state(player, "breed") || !this.horseManager.isBreedItem(itemStack)) return;

        if (this.horseManager.isAllowedHorse(event.getRightClicked().getType())) {
            AbstractHorse horse = (AbstractHorse) event.getRightClicked();
            int maxHealth = (int) Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            int health = (int) horse.getHealth();
            int age = horse.getAge();
            int loveMode = horse.getLoveModeTicks();
            Component component;

            //繫殖クールタイム中&体力がMAXであればイベントキャンセル
            if (age > 0 && health == maxHealth) {
                component = MiniMessage.miniMessage().deserialize(Messages.BREEDING_COOL_TIME.get(),
                        Formatter.number("cooltime", this.converter.getBreedingCoolTime(horse)));
                player.sendActionBar(component);
                event.setCancelled(true);
                //繫殖モード中(ハートが出てる時)&体力がMAXであればイベントキャンセル
            } else if (loveMode > 0 && health == maxHealth) {
                component = MiniMessage.miniMessage().deserialize(Messages.LOVE_MODE_TIME.get(),
                        Formatter.number("cooltime", this.converter.getLoveModeTime(horse)));
                player.sendActionBar(component);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreeding(EntityBreedEvent event) {
        if (this.horseManager.isAllowedHorse(event.getEntity().getType())
                && event.getBreeder() instanceof Player player) {

            if (!this.stateManager.state(player, "breed_notification")) return;

            AbstractHorse childrenHorse = (AbstractHorse) event.getEntity();
            var horseData = this.converter.convertHorseStats(childrenHorse);

            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BREEDING_NOTIFICATION.get(),
                    Placeholder.parsed("prefix", Messages.PREFIX.get()),
                    Placeholder.styling("call", ClickEvent.callback(audience -> {
                        if (audience instanceof Player callPlayer) {
                            this.horseFinder.fromUuid(childrenHorse.getUniqueId(), callPlayer);
                        }
                    }, builder -> builder.uses(3))),
                    Placeholder.styling("myhover", HoverEvent.showText(this.converter.horseStatsMessage(horseData)))));
        }
    }
}
