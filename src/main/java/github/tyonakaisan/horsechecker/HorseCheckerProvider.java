package github.tyonakaisan.horsechecker;

import com.google.inject.Singleton;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
@Singleton
public final class HorseCheckerProvider {

    private static @Nullable HorseChecker instance;

    private HorseCheckerProvider() {

    }

    static void register(final HorseChecker instance) {
        HorseCheckerProvider.instance = instance;
    }

    public static HorseChecker instance() {
        if (instance == null) {
            throw new IllegalStateException("HorseChecker not initialized!");
        }

        return instance;
    }
}