package github.tyonakaisan.horsechecker.packet;

import github.tyonakaisan.horsechecker.packet.holograms.Hologram;
import github.tyonakaisan.horsechecker.packet.holograms.HologramFactory;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibHologramFactory implements HologramFactory {

    @Override
    public @NotNull Hologram createHologram(@NotNull Location location, @NotNull String hologramName, @NotNull String rank) {
        return new ProtocolLibHologram(location, hologramName, rank);
    }
}
