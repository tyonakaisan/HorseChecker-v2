package github.tyonakaisan.horsechecker.horse.listener;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.manager.HorseManager;
import github.tyonakaisan.horsechecker.manager.StateManager;
import github.tyonakaisan.horsechecker.utils.Converter;
import github.tyonakaisan.horsechecker.utils.Messages;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class HorseCancelBreedListener implements Listener {

    private final HorseManager horseManager;
    private final StateManager stateManager;
    private final Converter converter;

    @Inject
    public HorseCancelBreedListener(
            final HorseManager horseManager,
            final StateManager stateManager,
            final Converter converter
    ) {
        this.horseManager = horseManager;
        this.stateManager = stateManager;
        this.converter = converter;
    }

    @EventHandler
    public void onCanceledBreed(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();

        //toggleチェック
        if (!stateManager.isState(player, "breed")) return;

        //繫殖させるためのアイテムか
        if (!horseManager.isBreedItem(itemStack)) {
            return;
        }

        if (horseManager.isAllowedHorse(event.getRightClicked().getType())) {
            AbstractHorse horse = (AbstractHorse) event.getRightClicked();
            int maxHealth, health, age, loveMode;
            Component component;

            maxHealth = (int) Objects.requireNonNull(horse.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            health = (int) horse.getHealth();
            age = horse.getAge();
            loveMode = horse.getLoveModeTicks();

            //繫殖クールタイム中&体力がMAXであればイベントキャンセル
            if (age > 0 && health == maxHealth) {
                component = MiniMessage.miniMessage().deserialize(
                        Messages.BREEDING_COOL_TIME.getMessage(),
                        Formatter.number("cooltime", converter.getBreedingCoolTime(horse)));
                player.sendActionBar(component);
                event.setCancelled(true);

                //繫殖モード中(ハートが出てる時)&体力がMAXであればイベントキャンセル
            } else if (loveMode > 0 && health == maxHealth) {
                component = MiniMessage.miniMessage().deserialize(
                        Messages.LOVE_MODE_TIME.getMessage(),
                        Formatter.number("cooltime", converter.getLoveModeTime(horse)));
                player.sendActionBar(component);
                event.setCancelled(true);
            }
        }
    }
}
