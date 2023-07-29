package github.tyonakaisan.horsechecker.packet.holograms;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface HologramLine {

    Component getText();

    void showTo(Player player);

    void hideFrom(Player player);

    void teleport(Location location);

    void setText(Component text);

}
