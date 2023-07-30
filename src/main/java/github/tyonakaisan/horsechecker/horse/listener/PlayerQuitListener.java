package github.tyonakaisan.horsechecker.horse.listener;

import github.tyonakaisan.horsechecker.HorseChecker;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public class PlayerQuitListener implements Listener {

    private final HorseChecker horseChecker;

    public HashMap<Player, String> horseMap = new HashMap<>();

    @Inject
    public PlayerQuitListener(
            final HorseChecker horseChecker
            ) {
        this.horseChecker = horseChecker;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var pdc = player.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(horseChecker, "stats");

        if (!pdc.has(namespacedKey)) return;

        if (Objects.requireNonNull(pdc.get(namespacedKey, PersistentDataType.STRING)).equalsIgnoreCase("true")) {
            pdc.remove(namespacedKey);
            pdc.set(namespacedKey, PersistentDataType.STRING, "false");
        }
    }
}
