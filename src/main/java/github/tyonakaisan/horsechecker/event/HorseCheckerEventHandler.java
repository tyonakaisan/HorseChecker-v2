package github.tyonakaisan.horsechecker.event;


import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import net.kyori.event.EventSubscription;
import net.kyori.event.PostResult;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.function.Consumer;

@DefaultQualifier(NonNull.class)
public final class HorseCheckerEventHandler {

    private final EventBus<HorseCheckerEvent> eventBus = EventBus.create(HorseCheckerEvent.class, (type, event, subscriber) -> {
        if (event instanceof ResultedHorseCheckerEvent<@NonNull ?> rce) {
            return !rce.result().cancelled();
        }

        return true;
    });

    public <T extends HorseCheckerEvent> EventSubscription subscribe(
            final Class<T> eventClass,
            final EventSubscriber<T> subscriber
    ) {
        return this.eventBus.subscribe(eventClass, subscriber);
    }

    public <T extends HorseCheckerEvent> EventSubscription subscribe(
            final Class<T> eventClass,
            final int priority,
            final boolean acceptsCancelled,
            final Consumer<T> consumer
    ) {
        return this.eventBus.subscribe(eventClass, new EventSubscriberImpl<>(consumer, priority, acceptsCancelled));
    }

    public PostResult emit(final HorseCheckerEvent event) {
        return this.eventBus.post(event);
    }
}
