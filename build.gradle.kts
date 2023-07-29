import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.jpenilla.runpaper.task.RunServerTask

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
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
    implementation("io.papermc", "paperlib", "1.0.7")
    compileOnly("io.papermc.paper", "paper-api", "1.19.4-R0.1-SNAPSHOT")

    // Adventure
    implementation("net.kyori", "adventure-api", "4.13.0")

    // Command
    implementation("cloud.commandframework", "cloud-paper", "1.8.0")

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
    implementation("com.google.inject", "guice", "5.1.0")
    implementation("co.aikar", "taskchain-bukkit", "3.7.2")
}

version = "1.0-SNAPSHOT"
val pluginName = project.name
val mainPackage = "github.tyonakaisan.horsechecker"
val mainClass = "$mainPackage.HorseChecker"

bukkit {
    name = rootProject.name
    version = project.version as String
    main = "github.tyonakaisan.horsechecker.HorseChecker"
    apiVersion = "1.19"
    author = "tyonakaisan"
    website = "https://github.com/tyonakaisan"
    depend = listOf("ProtocolLib")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<ShadowJar> {
        this.archiveClassifier.set(null as String?)
        relocate("io.papermc.lib", "$mainPackage.paperlib")
        relocate("co.aikar.taskchain", "$mainPackage.taskchain")
    }

    withType<RunServerTask> {
        this.minecraftVersion("1.20")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}