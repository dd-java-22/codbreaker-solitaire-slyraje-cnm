/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    jacoco
    id("org.openapi.generator") version "7.2.0"
}

val javaVersion = libs.versions.java.get()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation(libs.junit.aggregator)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform)
}

openApiGenerate {
    inputSpec.set("$projectDir/openapi.yaml")
    generatorName.set("java")
    apiPackage.set("edu.cnm.deepdive.codebreaker.service")
    modelPackage.set("edu.cnm.deepdive.codebreaker.model")
    invokerPackage.set("edu.cnm.deepdive.codebreaker")

    configOptions.set(
        mapOf(
            "library" to "retrofit2",
            "useRecords" to "true",
            "generateBuilders" to "true",
            "dateLibrary" to "java8",
            "openApiNullable" to "false",
            "generateModelTests" to "true",
            "generateApiTests" to "true",
        )
    )

    globalProperties.set(mapOf(
        "models" to "",
        "apis" to ""
    ))
}

tasks.withType<JavaCompile> {
    options.release = javaVersion.toInt()
}

tasks.javadoc {
    with(options as StandardJavadocDocletOptions) {
        links("https://docs.oracle.com/en/java/javase/${javaVersion}/docs/api/")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events.addAll(setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED))
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

sourceSets {
    main {
        java {
            // This tells Gradle: "Look in both places for source code"
            srcDirs("${project.buildDir}/generate-resources/main/src/main")
        }
    }
}
