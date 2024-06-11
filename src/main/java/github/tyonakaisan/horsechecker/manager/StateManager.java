package github.tyonakaisan.horsechecker.manager;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.utils.LegacyUpdater;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class StateManager {

    private static final String NAMESPACE = "horsechecker-v2";

    @Inject
    public StateManager(
    ) {
    }

    public boolean toggleState(final Player player, final String stateKey) {
        final var pdc = player.getPersistentDataContainer();
        final var namespacedKey = new NamespacedKey(NAMESPACE, stateKey);
        LegacyUpdater.playerPDCUpdateIfNeeded(player, namespacedKey);

        if (!pdc.has(namespacedKey)) {
            pdc.set(namespacedKey, PersistentDataType.BOOLEAN, false);
            return false;
        }

        if (Boolean.TRUE.equals(Objects.requireNonNull(pdc.get(namespacedKey, PersistentDataType.BOOLEAN)))) {
            pdc.set(namespacedKey, PersistentDataType.BOOLEAN, false);
            return false;
        } else {
            pdc.set(namespacedKey, PersistentDataType.BOOLEAN, true);
            return true;
        }
    }

    public boolean state(final Player player, final String stateKey) {
        final var pdc = player.getPersistentDataContainer();
        final var namespacedKey = new NamespacedKey(NAMESPACE, stateKey);
        LegacyUpdater.playerPDCUpdateIfNeeded(player, namespacedKey);

        if (!pdc.has(namespacedKey)) return false;

        return Boolean.TRUE.equals(Objects.requireNonNull(pdc.get(namespacedKey, PersistentDataType.BOOLEAN)));
    }

    enum State {
        BREED,
        BREED_NOTIFICATION,
        STATS
    }
}
