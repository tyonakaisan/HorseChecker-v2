plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")

    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")

    //ProtocolLib
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper", "paper-api", "1.20.4-R0.1-SNAPSHOT")
    // ProtocolLib
    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.2.0-SNAPSHOT")

    // Command
    paperLibrary("cloud.commandframework", "cloud-paper", "1.8.4")

    // Config
    paperLibrary("org.spongepowered", "configurate-hocon", "4.2.0-SNAPSHOT")
    paperLibrary("net.kyori", "adventure-serializer-configurate4", "4.15.0")

    // Utils
    paperLibrary("com.google.inject", "guice", "7.0.0")
    implementation("com.github.tyonakaisan", "GlowLib", "0.2.1")
    implementation("com.github.tyonakaisan", "Toast", "0.1.1")
}

version = "1.4.3-SNAPSHOT"

paper {
    val mainPackage = "github.tyonakaisan.horsechecker"
    generateLibrariesJson = true
    name = rootProject.name
    version = project.version as String
    main = "$mainPackage.HorseChecker"
    loader = "$mainPackage.HorseCheckerLoader"
    bootstrapper = "$mainPackage.HorseCheckerBootstrap"
    apiVersion = "1.20"
    author = "tyonakaisan"
    website = "https://github.com/tyonakaisan"
    serverDependencies {
        register("ProtocolLib") {
            required = true
        }
    }
}

tasks {
    val paperPlugins = runPaper.downloadPluginsSpec {
        // tabTps
        url("https://cdn.modrinth.com/data/cUhi3iB2/versions/QmxLremu/tabtps-spigot-1.3.21.jar")
        // spark
        url("https://ci.lucko.me/job/spark/396/artifact/spark-bukkit/build/libs/spark-1.10.55-bukkit.jar")
    }

    compileJava {
        this.options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        this.archiveClassifier.set(null as String?)
    }

    runServer {
        minecraftVersion("1.20.4")

        downloadPlugins {
            downloadPlugins.from(paperPlugins)
        }
    }
}
