plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

val copyJar by tasks.registering(Copy::class) {
    from(tasks.named<Jar>("jar").get().archiveFile)
    into(file("D:/ILeZzoV Server/plugins"))
    outputs.upToDateWhen { false }
}

tasks.named<Jar>("jar") {
    finalizedBy(copyJar)
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
            "command" to command,
        )

        val propsProperty = mapOf(
            "version" to version,
            "command" to command
        )

        filesMatching("plugin.yml") {
            expand(propsYml)
        }

        filesMatching("plugin.properties") {
            expand(propsProperty)
        }
    }
}
