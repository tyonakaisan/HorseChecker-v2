package github.tyonakaisan.horsechecker.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface HorseCheckerCommand {
    ArgumentBuilder<CommandSourceStack, ?> init();
}