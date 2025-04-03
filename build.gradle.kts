import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")

    //ProtocolLib
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper", "paper-api", "1.21.4-R0.1-SNAPSHOT")
    // ProtocolLib
    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.3.0")

    // Config
    paperLibrary("org.spongepowered", "configurate-hocon", "4.2.0")
    paperLibrary("net.kyori", "adventure-serializer-configurate4", "4.19.0")

    // Utils
    paperLibrary("com.google.inject", "guice", "7.0.0")
    paperLibrary("io.github.skytasul", "glowingentities", "1.3.4")
    // implementation("com.github.tyonakaisan", "GlowLib", "0.2.1")
    // implementation("com.github.tyonakaisan", "Toast", "0.1.1")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

paper {
    generateLibrariesJson = true
    name = rootProject.name
    version = "1.7.0"
    apiVersion = "1.21"
    author = "tyonakaisan"
    website = "https://github.com/tyonakaisan"

    val mainPackage = "github.tyonakaisan.horsechecker"
    main = "$mainPackage.HorseChecker"
    loader = "$mainPackage.HorseCheckerLoader"
    bootstrapper = "$mainPackage.HorseCheckerBootstrap"

    serverDependencies {
        register("ProtocolLib") {
            required = true
        }
    }

    permissions {
        register("horsechecker.command") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("horsechecker.command.reload") {
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("horsechecker.command.share") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("horsechecker.command.toggle") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("horsechecker.command.toggle.stats") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("horsechecker.command.toggle.breed") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("horsechecker.command.toggle.notification") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
    }
}

tasks {
    val paperPlugins = runPaper.downloadPluginsSpec {
        // ProtocolLib
        url("https://ci.dmulloy2.net/job/ProtocolLib/lastStableBuild/artifact/build/libs/ProtocolLib.jar")
        // LuckPerms
        modrinth("LuckPerms", "cfNN7sys")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    shadowJar {
        archiveClassifier.set(null as String?)
        archiveVersion.set(paper.version)
        mergeServiceFiles()
    }

    runServer {
        minecraftVersion("1.21.4")

        downloadPlugins {
            downloadPlugins.from(paperPlugins)
        }
    }
}
