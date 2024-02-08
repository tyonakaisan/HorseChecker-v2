package github.tyonakaisan.horsechecker.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.PropertyKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DefaultQualifier(NonNull.class)
@Singleton
public final class Messages {

    private final Path dataDirectory;
    private final ComponentLogger logger;

    private final Map<Locale, ResourceBundle> locales = new HashMap<>();
    private final Map<Locale, String> supportedLocales = Map.of(
            Locale.JAPAN, "messages_ja_JP",
            Locale.US, "messages_en_US"
    );
    private final Pattern pattern = Pattern.compile("messages_(.+)\\.properties");
    private static final String BUNDLE = "locale.messages";
    private static final String PREFIX = "<white>[<gradient:#ff2e4a:#ffd452>HorseChecker-v2</gradient>]</white>";

    @Inject
    public Messages(
            final Path dataDirectory,
            final ComponentLogger logger
    ) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;

        this.reloadMessage();
    }

    public void reloadMessage() {
        this.locales.clear();
        this.logger.info("Reloading locales...");
        try {
            this.loadMessageFile();
        } catch (IOException e) {
            this.logger.error("Failed to reload locales.", e);
        }
    }

    public void loadMessageFile() throws IOException {
        var path = this.dataDirectory.resolve("locale");

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Load default locale
        var defaultLocalePath = path.resolve("messages.properties");
        if (!Files.exists(defaultLocalePath)) {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE, Locale.US, UTF8ResourceBundleControl.get());

            // Create default locale
            this.createProperties(defaultLocalePath, bundle, "This is a sample file. Create a new locale or rewrite an existing file.");
        }

        this.load(Locale.US, defaultLocalePath);

        // Create supported locales
        this.createSupportedLocales(path);

        // Load messages_*.properties locale
        try (Stream<Path> paths = Files.list(path)) {
            paths.filter(Files::isRegularFile)
                    .forEach(this::loadMatchFile);
        } catch (IOException e) {
            this.logger.error("Failed to load locales.", e);
        }

        this.logger.info("Successfully {} locales loaded! {}", this.locales.keySet().size(), this.locales.keySet());
    }

    private void createSupportedLocales(final Path path) {
        for (Map.Entry<Locale, String> localesEntry : this.supportedLocales.entrySet()) {
            var locale = localesEntry.getKey();
            var fileName = localesEntry.getValue();
            var localePath = path.resolve(fileName + ".properties");

            if (!Files.exists(localePath)) {
                ResourceBundle bundle = ResourceBundle.getBundle("locale." + fileName, locale, UTF8ResourceBundleControl.get());
                this.createProperties(localePath, bundle, null);
            }
        }
    }

    private void createProperties(final Path path, final ResourceBundle bundle, @Nullable final String comment) {
        var properties = new Properties() {
            @Override
            public synchronized Set<Map.Entry<Object, Object>> entrySet() {
                return Collections.unmodifiableSet(
                        (Set<? extends Map.Entry<Object, Object>>) super.entrySet()
                                .stream()
                                .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                                .collect(Collectors.toCollection(LinkedHashSet::new)));
            }
        };
        try (Writer outputStream = Files.newBufferedWriter(path)) {
            properties.putAll(bundle.keySet().stream()
                    .collect(Collectors.toMap(
                            key -> key,
                            bundle::getString
                    )));
            properties.store(outputStream, comment);
            this.logger.info("successfully '{}' created!", path.getFileName());
        } catch (IOException e) {
            this.logger.error("Failed to create '{}' locales.", bundle.getLocale(), e);
        }
    }

    private void loadMatchFile(final Path path) {
        var matcher = this.pattern.matcher(path.getFileName().toString());
        if (matcher.matches()) {
            @Nullable Locale locale = Translator.parseLocale(matcher.group(1));

            if (locale == null) {
                this.logger.warn("Invalid locales {}", path.getFileName());
            } else {
                this.load(locale, path);
            }
        }
    }

    private void load(final Locale locale, final Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            this.locales.put(locale, new PropertyResourceBundle(reader));
        } catch (Exception e) {
            this.logger.error(String.format("Failed to load %s", path.getFileName()), e);
        }
    }

    public Component translatable(final Style style, final Audience audience, @PropertyKey(resourceBundle = BUNDLE) final String key) {
        return this.translatable(style, audience, key, TagResolver.empty());
    }

    public Component translatable(final Style style, final Audience audience, @PropertyKey(resourceBundle = BUNDLE) final String key, final TagResolver tagResolver) {
        var addPrefixTagResolver = TagResolver.builder()
                .resolver(tagResolver)
                .tag("prefix", Tag.selfClosingInserting(MiniMessage.miniMessage().deserialize(PREFIX)))
                .build();

        return audience instanceof Player player
                ? this.forPlayer(style, player, key, addPrefixTagResolver)
                : this.forAudience(style, key, addPrefixTagResolver);
    }

    private Component forPlayer(final Style style, final Player player, @PropertyKey(resourceBundle = BUNDLE) final String key, final TagResolver tagResolver) {
        final var component = Component.empty()
                .color(TextColor.fromCSSHexString(style.hex()))
                .decoration(TextDecoration.ITALIC, false);
        final @Nullable ResourceBundle resource = this.locales.get(player.locale());

        // localeはあるけどkeyがない場合
        if (resource != null && !resource.keySet().contains(key)) {
            return this.forAudience(style, key, tagResolver);
        }

        return resource != null
                ? component.append(MiniMessage.miniMessage().deserialize(resource.getString(key), tagResolver))
                : this.forAudience(style, key, tagResolver);
    }

    private Component forAudience(final Style style, @PropertyKey(resourceBundle = BUNDLE) final String key, final TagResolver tagResolver) {
        final var component = Component.empty()
                .color(TextColor.fromCSSHexString(style.hex()))
                .decoration(TextDecoration.ITALIC, false);
        final var resource = this.locales.get(Locale.US);

        // keyがない場合
        if (!resource.keySet().contains(key)) {
            var bundle = ResourceBundle.getBundle(BUNDLE, Locale.US, UTF8ResourceBundleControl.get());
            this.logger.warn("Message retrieved from resource bundle because '{}' does not exist in messages.properties.", key);
            return component.append(MiniMessage.miniMessage().deserialize("<hover:show_text:'<red>This message is taken from the resource bundle'>" + bundle.getString(key), tagResolver));
        }

        return component.append(MiniMessage.miniMessage().deserialize(resource.getString(key), tagResolver));
    }

    public enum Style {
        SUCCESS("#59ffa4"),
        ERROR("#ff4775"),
        INFO("#ffffff");

        private final String hex;

        Style(final String hex) {
            this.hex = hex;
        }

        public String hex() {
            return this.hex;
        }
    }
}
