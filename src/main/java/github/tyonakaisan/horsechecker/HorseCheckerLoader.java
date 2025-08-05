package github.tyonakaisan.horsechecker;

import com.google.gson.Gson;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings({"UnstableApiUsage", "unused"})
@DefaultQualifier(NonNull.class)
public final class HorseCheckerLoader implements PluginLoader {

    @Override
    public void classloader(final PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();
        final PluginLibraries pluginLibraries = load();
        pluginLibraries.asDependencies().forEach(resolver::addDependency);
        pluginLibraries.asRepositories().forEach(resolver::addRepository);
        classpathBuilder.addLibrary(resolver);
    }

    private PluginLibraries load() {
        try (final var in = Objects.requireNonNull(getClass().getResourceAsStream("/paper-libraries.json"))) {
            return new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
        } catch (final IOException exception) {
            throw new IllegalStateException("Failed to load plugin libraries.", exception);
        }
    }

    private record PluginLibraries(Map<String, String> repositories, List<String> dependencies) {
        public Stream<Dependency> asDependencies() {
            return dependencies.stream()
                    .map(d -> new Dependency(new DefaultArtifact(d), null));
        }

        public Stream<RemoteRepository> asRepositories() {
            final var mirror = this.getMavenCentralMirror();
            return repositories.entrySet().stream()
                    .map(entry -> {
                        final var url = entry.getValue().contains(".maven.org") || entry.getValue().contains(".maven.apache.org")
                                ? mirror
                                : entry.getValue();

                        return new RemoteRepository.Builder(entry.getKey(), "default", url).build();
                    });
        }

        private String getMavenCentralMirror() {
            return Optional.ofNullable(System.getenv("PAPER_DEFAULT_CENTRAL_REPOSITORY"))
                    .or(() -> Optional.ofNullable(System.getProperty("org.bukkit.plugin.java.LibraryLoader.centralURL")))
                    .orElse("https://maven-central.storage-download.googleapis.com/maven2");
        }
    }
}
