import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    val kotlinVersion = "1.9.22"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.openapi.generator") version "7.2.0"
    id("com.diffplug.spotless") version "6.24.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    dependsOn("openApiGenerate")
    kotlinOptions {
        jvmTarget = "17"
    }
}

val apiDirectoryPath = "$projectDir/src/main/resources/api"
val openApiGenerateOutputDir =
    "${layout.buildDirectory.get()}/generated/openapi"

openApiGenerate {
    generatorName = "kotlin-spring"
    inputSpec = "$apiDirectoryPath/spec.yml"
    outputDir = openApiGenerateOutputDir
    apiPackage = "com.yonatankarp.openapi"
    modelPackage = "com.yonatankarp.openapi.models"
    configOptions =
        mapOf(
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "implicitHeaders" to "true",
            "hideGenerationTimestamp" to "true",
            "useTags" to "true",
            "documentationProvider" to "none",
            "useSpringBoot3" to "true",
        )
}

tasks {
    register("cleanGeneratedCodeTask") {
        description = "Removes generated Open API code"

        doLast {
            File(openApiGenerateOutputDir).deleteRecursively()
        }
    }

    clean { dependsOn("cleanGeneratedCodeTask") }
}

sourceSets.main {
    kotlin {
        srcDir("$openApiGenerateOutputDir/src/main/kotlin")
    }
}

configure<SpotlessExtension> {
    kotlin {
        // see https://github.com/shyiko/ktlint#standard-rules
        ktlint()
        target(
            project.fileTree(project.rootDir) {
                include("**/*.kt")
                exclude("**/generated/**")
            },
        )
    }
    kotlinGradle {
        trimTrailingWhitespace()
        ktlint()
        target(
            project.fileTree(project.rootDir) {
                include("**/*.kts")
                exclude("**/generated/**")
            },
        )
    }
}

val tasksDependencies =
    mapOf(
        "spotlessKotlin" to listOf("spotlessKotlinGradle", "compileTestKotlin", "test"),
        "spotlessKotlinGradle" to listOf("processResources", "compileKotlin", "compileTestKotlin", "test"),
    )

tasksDependencies.forEach { task, dependsOn ->
    dependsOn.forEach {
        tasks.findByName(task)!!.dependsOn(it)
    }
}
