plugins {
    kotlin("jvm") version "1.9.22"
    id("com.google.protobuf") version "0.9.4"
    `maven-publish`
    `java-library`
}

group = "com.concert"
version = "1.0.3"

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.protobuf:protobuf-kotlin:3.24.0")
    api("io.grpc:grpc-kotlin-stub:1.4.0")
    api("io.grpc:grpc-protobuf:1.59.0")
    api("com.google.protobuf:protobuf-java-util:3.24.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.0"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.59.0"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
                create("grpckt")
            }
            task.builtins {
                create("kotlin")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "build/generated/source/proto/main/java",
                "build/generated/source/proto/main/kotlin",
                "build/generated/source/proto/main/grpc",
                "build/generated/source/proto/main/grpckt"
            )
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn("generateProto")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            groupId = "com.concert"
            artifactId = "concert-proto"
            version = version

            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ByeonJuHwan/concert-proto")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}