package github.tyonakaisan.horsechecker.packet.holograms;

import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface HologramFactory {

    Hologram createHologram(Location location, String hologramName, String rank);

}
