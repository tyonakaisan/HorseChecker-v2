package github.tyonakaisan.horsechecker.manager;

import com.google.inject.Inject;
import github.tyonakaisan.horsechecker.config.ConfigFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;
import java.util.Objects;

@DefaultQualifier(NonNull.class)
public final class ShareManager {

    private final ConfigFactory configFactory;

    @Inject
    public ShareManager(
            ConfigFactory configFactory
    ) {
        this.configFactory = configFactory;
    }

    public boolean isAllowedHorseShare() {
        return Objects.requireNonNull(this.configFactory.primaryConfig()).share().allowedHorseShare();
    }

    public int shareCommandIntervalTime() {
        return Objects.requireNonNull(this.configFactory.primaryConfig()).share().shareCommandIntervalTime() * 1000;
    }

    public boolean ownerOnly() {
        return Objects.requireNonNull(this.configFactory.primaryConfig()).share().ownerOnly();
    }

    public List<String> getHorseNamePrefix() {
        return Objects.requireNonNull(this.configFactory.primaryConfig()).share().horseNamePrefix();
    }
}
