plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0"
}

group = "me.pan_truskawka045"
version = "1.0-SNAPSHOT"

var lombokVersion: String = "1.18.42"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.pantruskawka045.me/public")
    }
    maven {
        name = "kokscraft-engine"
        url = uri("https://repo.pantruskawka045.me/Kokscraft-Engine")
        credentials {
            username = (project.findProperty("kokscraftUsername") as String?) ?: System.getenv("KOKSCRAFT_REPO_NAME")
            password = (project.findProperty("kokscraftPassword") as String?) ?: System.getenv("KOKSCRAFT_REPO_SECRET")
        }
        authentication {
            create<BasicAuthentication>("basic")
        }
    }
    mavenLocal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("me.pan_truskawka045:Injector:1.0-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("me.pan_truskawka045:Spigot-Animations:1.2.2-SNAPSHOT")

    //This project will work on basic spigot, I'm using this only for QOL features.
    paperweight.paperDevBundle(
        version = "1.21.10-R0.1-SNAPSHOT",
        group = "pl.kokscraft.strawberry-paper",
        artifactId = "dev-bundle"
    )

}

bukkitPluginYaml {
    main = "me.pan_truskawka045.Slither.SpigotSlitherPlugin"
    authors.add("PanTruskawka045")
    apiVersion = "1.21.10"
    version = project.version.toString()
    name = "Spigot-Slither"
}

tasks.test {
    useJUnitPlatform()
}