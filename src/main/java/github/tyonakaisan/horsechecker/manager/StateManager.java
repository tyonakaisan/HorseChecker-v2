package github.tyonakaisan.horsechecker.manager;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.HorseChecker;
import github.tyonakaisan.horsechecker.LegacyUpdater;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

// PersistentDataTypeにbooleanがあることを知らずStringにした過去の愚行によって地味にややこしいことになった(他も色々)
// 置き換えする場合はremove -> addでやるしかなさそう?とりあえずこのままで
@DefaultQualifier(NonNull.class)
public final class StateManager {

    private final HorseChecker horseChecker;

    @Inject
    public StateManager(
            final HorseChecker horseChecker
    ) {
        this.horseChecker = horseChecker;
    }

    public boolean toggleState(final Player player, final String stateKey) {
        final var pdc = player.getPersistentDataContainer();
        final var namespacedKey = new NamespacedKey(this.horseChecker, stateKey);
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
        final var namespacedKey = new NamespacedKey(this.horseChecker, stateKey);
        LegacyUpdater.playerPDCUpdateIfNeeded(player, namespacedKey);

        if (!pdc.has(namespacedKey)) return false;

        return Boolean.TRUE.equals(Objects.requireNonNull(pdc.get(namespacedKey, PersistentDataType.BOOLEAN)));
    }
}
