package github.tyonakaisan.horsechecker.utils;

import com.tyonakaisan.glowlib.glow.Glow;
import org.bukkit.ChatColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class GlowColorConverter {

    public static ChatColor convert(final Glow.Color color) {
        return ChatColor.values()[color.ordinal()];
    }
}
