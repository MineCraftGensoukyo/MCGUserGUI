val coroutines_version = "1.4.2"

plugins {
    java
    id("io.izzel.taboolib") version "1.26"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    install("common")
    install("module-chat")
    install("platform-bukkit")
    install("module-database")
    install("module-configuration")
    install("module-lang")
    classifier = null
    version = "6.0.0-53"
    description {
        desc("本插件用与为MC幻想乡RPG内容提供图形用户界面的支持")
        contributors {
            name("ZakeArias")
            name("DavidWang19")
        }
        dependencies {
            name("WakeShow").with("bukkit")
        }
    }
}

repositories {
    mavenCentral()
    flatDir {
        dir("lib")
    }
}

dependencies {
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    implementation("org.jetbrains.kotlinx","kotlinx-coroutines-core",coroutines_version)
    implementation("org.jetbrains.kotlinx","kotlinx-coroutines-jdk8",coroutines_version)
    implementation(fileTree("lib"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
