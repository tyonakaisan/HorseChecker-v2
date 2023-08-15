package github.tyonakaisan.horsechecker.packet.holograms;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@DefaultQualifier(NonNull.class)
public final class HologramManager {

    private final Map<String, Hologram> hologramMap = new HashMap<>();
    private final HologramFactory hologramFactory;

    @Inject
    public HologramManager(HologramFactory hologramFactory) {
        this.hologramFactory = hologramFactory;
    }

    public List<String> getHologramNames() {
        return List.copyOf(hologramMap.keySet());
    }

    public Hologram getHologram(String hologramID) {
        return hologramMap.get(hologramID);
    }

    public void createHologram(Location location, String name, String rank) {
        if(hologramMap.containsKey(name)) {
            return;
        }
        Hologram hologram = hologramFactory.createHologram(location, name, rank);
        hologramMap.put(name, hologram);
    }

    public void deleteHologram(String hologramID, Player player) {
        Hologram hologram = hologramMap.remove(hologramID);
        if (hologram == null) {
            return;
        }
        hologram.hideFrom(player);
    }

    public void hideHologram(String hologramID, Player player) {
        Hologram hologram = hologramMap.get(hologramID);
        hologram.hideFrom(player);
    }

    public void initPlayer(String hologramID, Player player) {
        hologramMap.get(hologramID).showTo(player);
    }
}
