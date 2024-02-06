package github.tyonakaisan.horsechecker.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import github.tyonakaisan.horsechecker.horse.Converter;
import github.tyonakaisan.horsechecker.horse.HorseFinder;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.message.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
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
public final class HorseBreedListener implements Listener {

    private final ConfigFactory configFactory;
    private final StateManager stateManager;
    private final HorseFinder horseFinder;
    private final Converter converter;

    @Inject
    public HorseBreedListener(
            final ConfigFactory configFactory,
            final StateManager stateManager,
            final HorseFinder horseFinder,
            final Converter converter
    ) {
        this.configFactory = configFactory;
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
        if (!this.stateManager.state(player, "breed") || !this.isBreedItem(itemStack)) return;

        if (this.configFactory.primaryConfig().horse().allowedMobs().contains(event.getRightClicked().getType())) {
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
        if (this.configFactory.primaryConfig().horse().allowedMobs().contains(event.getEntity().getType())
                && event.getBreeder() instanceof Player player) {

            if (!this.stateManager.state(player, "breed_notification")) return;

            AbstractHorse childrenHorse = (AbstractHorse) event.getEntity();
            var horseData = this.converter.convertHorseStats(childrenHorse);

            var locationMessage = MiniMessage.miniMessage().deserialize(Messages.BABY_HORSE_LOCATION.get(),
                    Placeholder.parsed("world", horseData.location().getWorld().getName()),
                    Formatter.number("x", (int) horseData.location().getX()),
                    Formatter.number("y", (int) horseData.location().getY()),
                    Formatter.number("z", (int) horseData.location().getZ()));

            player.sendMessage(MiniMessage.miniMessage().deserialize(Messages.BREEDING_NOTIFICATION.get(),
                    Placeholder.parsed("prefix", Messages.PREFIX.get()),
                    Placeholder.styling("call", ClickEvent.callback(audience -> {
                        if (audience instanceof Player callPlayer) {
                            this.horseFinder.fromUuid(childrenHorse.getUniqueId(), callPlayer);
                        }
                    }, builder -> builder.uses(3))),
                    Placeholder.styling("myhover", HoverEvent.showText(Component.text()
                            .append(this.converter.horseStatsMessage(horseData))
                            .appendNewline()
                            .append(locationMessage)))));
        }
    }

    private boolean isBreedItem(ItemStack itemStack) {
        return itemStack.getType() == Material.GOLDEN_CARROT ||
                itemStack.getType() == Material.GOLDEN_APPLE ||
                itemStack.getType() == Material.ENCHANTED_GOLDEN_APPLE ||
                itemStack.getType() == Material.HAY_BLOCK;
    }
}
