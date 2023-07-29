package github.tyonakaisan.horsechecker.event;


import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;


record EventSubscriberImpl<T extends HorseCheckerEvent>(Consumer<T> consumer, int postOrder, boolean acceptsCancelled) implements EventSubscriber<T> {

    @Override
    public void on(final @NonNull T event) {
            this.consumer.accept(event);
        }
}
