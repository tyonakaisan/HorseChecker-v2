package github.tyonakaisan.horsechecker.packet;

import github.tyonakaisan.horsechecker.packet.holograms.Hologram;
import github.tyonakaisan.horsechecker.packet.holograms.HologramFactory;
import org.bukkit.Location;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

@DefaultQualifier(NonNull.class)
public final class ProtocolLibHologramFactory implements HologramFactory {

    @Override
    public @NotNull Hologram createHologram(@NotNull Location location, @NotNull String hologramName, @NotNull String rank) {
        return new ProtocolLibHologram(location, hologramName, rank);
    }
}
