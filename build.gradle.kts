import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.0.20"
    // kotlin("jvm") version "2.1.255"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    // implementation("com.google.code.gson:gson:2.11.0")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
    // implementation("org.apache.commons:commons-collections4:4.4")

    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:33.3.0-jre")

    // implementation("org.eclipse.collections:eclipse-collections-api:11.1.0")
    // implementation("org.eclipse.collections:eclipse-collections:11.1.0")

    // implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")

    testImplementation(kotlin("test"))
    // implementation(project(":lib"))
    // implementation("com.google.guava:guava:33.2.1-jre")
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
    compilerOptions {
        // freeCompilerArgs.add("-Xnon-local-break-continue")
        freeCompilerArgs.add("-XXLanguage:+BreakContinueInInlineLambdas")
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    // compilerOptions.progressiveMode.set(true)
    compilerOptions.freeCompilerArgs.add("-Xuse-fir-extended-checkers")
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
    compilerOptions.freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
    // compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_1)
    // compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_2_0)
    // compilerOptions.languageVersion.set(KotlinVersion.KOTLIN_1_9)
}