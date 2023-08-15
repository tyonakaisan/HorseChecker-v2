package github.tyonakaisan.horsechecker.packet.holograms;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@DefaultQualifier(NonNull.class)
public abstract class AbstractHologram implements Hologram {

    private static final double LINE_SPACE = 0.25;

    private final List<HologramLine> hologramLines = new ArrayList<>();
    private final String name;
    private Location location;

    private final String rank;

    protected AbstractHologram(Location location, String name, String rank) {
        this.name = name;
        this.location = location;
        this.rank = rank;
    }

    private Location getRelativeLocationForIndex(int index) {
        return location.clone().add(0, -LINE_SPACE * index, 0);
    }

    @Override
    public @NotNull String getId() {
        return name;
    }

    @Override
    public int size() {
        return hologramLines.size();
    }

    @Override
    public void addLine(@NotNull Component line) {
        int nextIndex = size();
        Location lineLocation = getRelativeLocationForIndex(nextIndex);
        HologramLine hologramLine = createLine(lineLocation, rank);
        hologramLine.setText(line);
        hologramLines.add(hologramLine);
    }

    @Override
    public void setLine(int index, @NotNull Component line) {
        Preconditions.checkArgument(index < size());
        HologramLine hologramLine = hologramLines.get(index);
        hologramLine.setText(line);
    }

    @Override
    public void setRank(int index, String rank) {
        HologramLine hologramLine = hologramLines.get(index);
        hologramLine.setRank(rank);
    }

    @Override
    public @NotNull Component getLine(int index) {
        Preconditions.checkArgument(index < size());
        return hologramLines.get(index).getText();
    }

    @Override
    public @NotNull Location getLocation() {
        return location;
    }

    @Override
    public void teleport(@NotNull Location target) {
        this.location = target;
        for(int index = 0; index < size(); index++) {
            Location lineLoc = getRelativeLocationForIndex(index);
            HologramLine hologramLine = hologramLines.get(index);
            hologramLine.teleport(lineLoc);
        }
    }

    @Override
    public void showTo(@NotNull Player player) {
        hologramLines.forEach(line -> line.showTo(player));
    }

    @Override
    public void hideFrom(@NotNull Player player) {
        hologramLines.forEach(line -> line.hideFrom(player));
    }

    @Override
    public void removeLine(int index) {
        Preconditions.checkArgument(index < size());
        HologramLine line = hologramLines.remove(index);
        Bukkit.getOnlinePlayers().forEach(line::hideFrom);
        teleport(this.location);
    }

    protected abstract HologramLine createLine(Location location, String rank);

}
