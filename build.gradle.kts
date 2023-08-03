plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.1.0"
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

    // Inventory
    // TaskChain
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Paper
    compileOnly("io.papermc.paper", "paper-api", "1.20.1-R0.1-SNAPSHOT")

    // Command
    implementation("cloud.commandframework", "cloud-paper", "1.8.3")

    // Config
    implementation("org.spongepowered", "configurate-hocon", "4.1.2")
    implementation("net.kyori", "adventure-serializer-configurate4", "4.12.0")

    // Messages
    implementation("net.kyori.moonshine", "moonshine-standard", "2.0.4")

    // Event
    implementation("net.kyori", "event-api","5.0.0-SNAPSHOT")

    // ProtocolLib
    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.0.0")

    // Inventory
    implementation("org.incendo.interfaces", "interfaces-paper", "1.0.0-SNAPSHOT")

    // Utils
    implementation("com.google.inject", "guice", "7.0.0")
    implementation("co.aikar", "taskchain-bukkit", "3.7.2")
}

version = "1.0-SNAPSHOT"
val pluginName = project.name
val mainPackage = "github.tyonakaisan.horsechecker"
val mainClass = "$mainPackage.HorseChecker"

paper {
    name = rootProject.name
    version = project.version as String
    main = "github.tyonakaisan.horsechecker.HorseChecker"
    apiVersion = "1.19"
    author = "tyonakaisan"
    website = "https://github.com/tyonakaisan"
    // depend = listOf("ProtocolLib")
}

tasks {
    compileJava {
        this.options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        this.archiveClassifier.set(null as String?)
    }

    runServer {
        minecraftVersion("1.20.1")
    }
}
