import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    maven
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.fromgate.sendtelegram"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    implementation("com.github.jkcclemens:khttp:-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}


tasks {
    /* build {
        dependsOn.add("shadowJar")
    } */
    named<ShadowJar>("shadowJar") {
        //configurations.add(project.configurations.compile.get())
        // archiveFileName.set("${project.name}-${project.version}-test.jar")
    }
}