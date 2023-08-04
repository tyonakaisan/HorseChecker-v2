package github.tyonakaisan.horsechecker.packet;

import github.tyonakaisan.horsechecker.packet.holograms.AbstractHologram;
import github.tyonakaisan.horsechecker.packet.holograms.HologramLine;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class ProtocolLibHologram extends AbstractHologram {
    public ProtocolLibHologram(Location location, String name, String rank) {
        super(location, name, rank);
    }

    @Override
    protected HologramLine createLine(Location location, String rank) {
        return new ProtocolLibHologramLine(location, rank);
    }
}
