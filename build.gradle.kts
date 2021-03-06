plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.spring") version "1.7.10"
    id("org.openapi.generator") version "6.0.1"
}

group = "com.yonatankarp"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

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

val apiDirectoryPath = projectDir.absolutePath + File.separator + "api"
val generatedCodeDirectoryPath = buildDir.path + File.separator +
        "generated" + File.separator + "open-api"

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set(apiDirectoryPath + File.separator + "spec.yml")
    outputDir.set(generatedCodeDirectoryPath)
    configFile.set(apiDirectoryPath + File.separator + "config.json")
}

tasks {
    register("cleanGeneratedCodeTask") {
        description = "Removes generated Open API code"

        doLast {
            File(generatedCodeDirectoryPath).deleteRecursively()
        }
    }

    clean { dependsOn("cleanGeneratedCodeTask"); finalizedBy(openApiGenerate) }
    compileJava { dependsOn(openApiGenerate) }
}

sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].java {
    srcDir(generatedCodeDirectoryPath + File.separator +
            "src" + File.separator + "main" + File.separator + "kotlin")
}
