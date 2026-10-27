plugins {
    id("java")
    id("application")
}

group = "retea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("Main")
}

tasks.test {
    useJUnitPlatform()
}

// data/users.txt si data/friendships.txt sunt cautate relativ la directorul proiectului;
// fortam explicit directorul de lucru, ca sa nu conteze cum/de unde e pornit run-ul
tasks.named<JavaExec>("run") {
    workingDir = projectDir
    standardInput = System.`in`
}