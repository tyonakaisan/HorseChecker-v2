package github.tyonakaisan.horsechecker.event;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public interface ResultedHorseCheckerEvent<R extends ResultedHorseCheckerEvent.Result> extends HorseCheckerEvent{

    R result();

    void result(final R result);

    interface Result {

        boolean cancelled();

        Component reason();

    }
}
