plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.0.0"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "kotlinx-serialization")

    group = "me.santio"
    version = "2.0.0"
    java.sourceCompatibility = JavaVersion.VERSION_17

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://repo.maven.apache.org/maven2/")
        maven("https://oss.sonatype.org/content/groups/public/")
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    }
}

dependencies {
    implementation(project(":glass-bungeecord"))
    implementation(project(":glass-spigot"))
    implementation(project(":glass-velocity"))
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
