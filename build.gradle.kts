import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.okaeri.cloud/releases")
    maven("https://repo.kyngs.xyz/public/")
}

dependencies {
    implementation("xyz.kyngs.libby:libby-bukkit:1.5.0")

    compileOnly("org.bstats:bstats-bukkit:3.2.1")

    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:6.1.0-beta.4")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:6.1.0-beta.4")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

val copyJar by tasks.registering(Copy::class) {
    from(tasks.named<ShadowJar>("shadowJar").get().archiveFile)
    into(file("D:/ILeZzoV Server/plugins"))
    outputs.upToDateWhen { false }
}

tasks.named<ShadowJar>("shadowJar") {
    finalizedBy(copyJar)
}

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { setOf(it) }

    relocate("net.byteflux.libby", "${project.group}.libs.libby")
    relocate("org.bstats", "${project.group}.libs.stats")
    relocate("eu.okaeri", "${project.group}.libs.okaeri")
    relocate("net.kyori", "${project.group}.libs.kyori")
}

tasks {
    runServer {
        minecraftVersion("1.18.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val name: String by project
        val prefix: String by project
        val authors: String by project
        val website: String by project
        val command: String by project

        val propsYml = mapOf(
            "version" to version,
            "description" to project.description,
            "authors" to authors,
            "website" to website,
            "name" to name,
            "prefix" to prefix,
            "command" to command
        )

        val propsProperty = mapOf(
            "version" to version,
            "command" to command,
            "website" to website
        )

        val propsLang = mapOf("command" to command)

        filesMatching("plugin.yml") {
            expand(propsYml)
        }

        filesMatching("plugin.properties") {
            expand(propsProperty)
        }
        filesMatching("messages/") {
            expand(propsLang)
        }
    }
}
