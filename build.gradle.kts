plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "io.github.jbock-java"

repositories {
  mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
    (options as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Version"] = project.version
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// https://vanniktech.github.io/gradle-maven-publish-plugin/central/
mavenPublishing {
    coordinates("io.github.jbock-java", "parmigiano", project.version?.toString())
    pom {
        name = "parmigiano"
        packaging = "jar"
        description = "Finite permutations for Java"
        url = "https://github.com/jbock-java/parmigiano"

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "Various"
                name = "Various"
                email = "jbock-java@gmx.de"
            }
        }
        scm {
            connection = "scm:git:https://github.com/jbock-java/parmigiano.git"
            developerConnection = "scm:git:https://github.com/jbock-java/parmigiano.git"
            url = "https://github.com/jbock-java/parmigiano"
        }
    }
    publishToMavenCentral()
    signAllPublications()
}
