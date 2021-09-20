val kotlinVersion = "1.5.30"
val ktorVersion = "1.6.3"
val serializationVersion = "1.2.2"
val logbackVersion = "1.2.3"

plugins {
    kotlin("multiplatform") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
}


group = "me.artemij"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(IR) {
        useCommonJs()
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("io.ktor:ktor-client-java:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
            }
        }
        val jsMain by getting
    }
}
