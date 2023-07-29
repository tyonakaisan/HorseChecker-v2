package github.tyonakaisan.horsechecker.packet.holograms;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface Hologram {

    String getId();

    int size();

    void addLine(Component line);

    void setLine(int index, Component line);

    Component getLine(int index);

    Location getLocation();

    void teleport(Location target);

    void showTo(Player player);

    void hideFrom(Player player);

    void removeLine(int index);
}
