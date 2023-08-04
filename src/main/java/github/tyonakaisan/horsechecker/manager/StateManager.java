package github.tyonakaisan.horsechecker.manager;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class StateManager {

    private final HorseChecker horseChecker;

    @Inject
    public StateManager(
            final HorseChecker horseChecker
    ) {
        this.horseChecker = horseChecker;
    }

    public boolean toggleState(Player player, String stateKey) {
        var pdc = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(horseChecker, stateKey);

        if (!pdc.has(namespacedKey)) {
            pdc.set(namespacedKey, PersistentDataType.STRING, "false");
            return false;
        }

        if (Objects.requireNonNull(pdc.get(namespacedKey, PersistentDataType.STRING)).equalsIgnoreCase("true")) {
            pdc.remove(namespacedKey);
            pdc.set(namespacedKey, PersistentDataType.STRING, "false");
            return false;
        } else {
            pdc.remove(namespacedKey);
            pdc.set(namespacedKey, PersistentDataType.STRING, "true");
            return true;
        }
    }

    public boolean state(Player player, String stateKey) {
        var pdc = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(horseChecker, stateKey);

        if (!pdc.has(namespacedKey)) return false;

        return Objects.requireNonNull(pdc.get(namespacedKey, PersistentDataType.STRING)).equalsIgnoreCase("true");
    }

}
