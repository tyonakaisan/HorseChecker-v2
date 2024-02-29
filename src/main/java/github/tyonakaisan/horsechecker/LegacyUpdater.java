package github.tyonakaisan.horsechecker;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class LegacyUpdater {

    private LegacyUpdater() {
    }

    public static void playerPDCUpdateIfNeeded(final Player player, final NamespacedKey stateKey) {

        final var pdc = player.getPersistentDataContainer();

        if (pdc.has(stateKey, PersistentDataType.STRING)) {
            final var boolString = Objects.requireNonNull(pdc.get(stateKey, PersistentDataType.STRING));
            final var bool = Boolean.parseBoolean(boolString);
            pdc.set(stateKey, PersistentDataType.BOOLEAN, bool);
        }
    }
}
