package github.tyonakaisan.horsechecker.packet.holograms;

import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DefaultQualifier(NonNull.class)
public class HologramManager {

    public final Map<String, Hologram> hologramMap = new HashMap<>();
    private final HologramFactory hologramFactory;

    @Inject
    public HologramManager(HologramFactory hologramFactory) {
        this.hologramFactory = hologramFactory;
    }

    public Hologram getHologram(String hologramID) {
        return hologramMap.get(hologramID);
    }

    public Hologram createHologram(Location location, String name, String rank) {
        if(hologramMap.containsKey(name)) {
            return null;
        }
        Hologram hologram = hologramFactory.createHologram(location, name, rank);
        hologramMap.put(name, hologram);
        return hologram;
    }

    public void deleteHologram(String hologramID, Player player) {
        Hologram hologram = hologramMap.remove(hologramID);
        if (hologram == null) {
            return;
        }
        hologram.hideFrom(player);
        //Bukkit.getOnlinePlayers().forEach(hologram::hideFrom);
    }

    public void initPlayer(String hologramID, Player player) {
        // This could be optimized by mapping holograms to Worlds or even Chunks
        // But for simplicity's sake we will just show a player all holograms
        //hologramMap.values().forEach(hologram -> hologram.showTo(player));
        hologramMap.get(hologramID).showTo(player);
    }

    public List<String> getHologramNames() {
        return List.copyOf(hologramMap.keySet());
    }
}
