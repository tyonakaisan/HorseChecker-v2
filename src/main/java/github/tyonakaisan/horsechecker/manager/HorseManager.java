package github.tyonakaisan.horsechecker.manager;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class HorseManager {

    private final ConfigFactory configFactory;

    @Inject
    public HorseManager(
            ConfigFactory configFactory
    ) {
        this.configFactory = configFactory;
    }

    public boolean isBreedItem(ItemStack itemStack) {
        return itemStack.getType() == Material.GOLDEN_CARROT ||
                itemStack.getType() == Material.GOLDEN_APPLE ||
                itemStack.getType() == Material.ENCHANTED_GOLDEN_APPLE ||
                itemStack.getType() == Material.HAY_BLOCK;
    }

    public boolean isAllowedHorse(EntityType entityType) {
        return Objects.requireNonNull(this.configFactory.primaryConfig()).allowedMOBs().contains(entityType);
    }

    public boolean ownerCheck(AbstractHorse horse, Player player) {

        if (horse.getOwner() == null) return true;

        if (Objects.requireNonNull(configFactory.primaryConfig()).share().ownerOnly()) {
            return Objects.requireNonNull(horse.getOwnerUniqueId()).equals(player.getUniqueId());
        } else {
            return true;
        }
    }
}
