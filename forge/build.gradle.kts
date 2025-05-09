@file:Suppress("UnstableApiUsage")


plugins {
	`maven-publish`
	id("dev.architectury.loom")
	id("architectury-plugin")
	id("com.gradleup.shadow")
}

val loader = property("loom.platform")!!
val minecraftVersion: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
	"No common project for $project"
}.project

val ci = System.getenv("CI")?.toBoolean() ?: false
val release = System.getenv("RELEASE")?.toBoolean() ?: false
val nightly = ci && !release
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()
version = "${mod.version}${if (release) "" else "-dev"}+mc.${minecraftVersion}-${loader}${if (nightly) "-build.${buildNumber}" else ""}"
group = "${mod.group}.$loader"
base.archivesName.set(mod.id)

architectury {
	platformSetupLoomIde()
	forge()
}

val commonBundle: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

configurations {
	compileClasspath.get().extendsFrom(commonBundle)
	runtimeClasspath.get().extendsFrom(commonBundle)
	get("developmentForge").extendsFrom(commonBundle)
}

loom {
	silentMojangMappingsLicense()
	accessWidenerPath = common.loom.accessWidenerPath
	forge.convertAccessWideners = true
	forge.mixinConfigs(
		"escalated-common.mixins.json",
		"escalated.mixins.json",
	)

	runConfigs.all {
		isIdeConfigGenerated = true
		runDir = "../../../run"
		vmArgs("-Dmixin.debug.export=true")
	}
}

repositories {
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings(loom.layered {
		officialMojangMappings { nameSyntheticMembers = false }
		parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${mod.dep("parchment_version")}@zip")
	})
	forge("net.minecraftforge:forge:$minecraftVersion-${common.mod.dep("forge_loader_version")}")

	"io.github.llamalad7:mixinextras-forge:${mod.dep("mixinextras_version")}".let {
		annotationProcessor(it)
		implementation(it)
	}

	// Create and its dependencies
	modImplementation("com.simibubi.create:create-${minecraftVersion}:${mod.dep("create_forge_version")}:slim") { isTransitive = false }
	modImplementation("net.createmod.ponder:Ponder-Forge-${minecraftVersion}:${mod.dep("ponder_forge_version")}")
	modCompileOnly("dev.engine-room.flywheel:flywheel-forge-api-${minecraftVersion}:${mod.dep("flywheel_forge_version")}")
    modRuntimeOnly("dev.engine-room.flywheel:flywheel-forge-${minecraftVersion}:${mod.dep("flywheel_forge_version")}")
	modImplementation("com.tterrag.registrate:Registrate:${mod.dep("registrate_forge_version")}")

	// Development QOL
	modLocalRuntime("mezz.jei:jei-${minecraftVersion}-forge:${mod.dep("jei_version")}") { isTransitive = false }

	// if you would like to add integration with JEI, uncomment this line.
	modCompileOnly("mezz.jei:jei-${minecraftVersion}-forge-api:${mod.dep("jei_version")}")

	//modImplementation("curse.maven:spark-361579:${mod.dep("spark_forge_file")}") // Spark

	compileOnly("io.github.llamalad7:mixinextras-common:${mod.dep("mixinextras_version")}")
	annotationProcessor(include("io.github.llamalad7:mixinextras-forge:${mod.dep("mixinextras_version")}"){})

	commonBundle(project(common.path, "namedElements")) { isTransitive = false }
	shadowBundle(project(common.path, "transformProductionForge")) { isTransitive = false }
}

java {
	withSourcesJar()
	val java = if (stonecutter.eval(minecraftVersion, ">=1.20.5"))
		JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	targetCompatibility = java
	sourceCompatibility = java
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifact(tasks.remapJar)
			artifact(tasks.remapSourcesJar)
			group = mod.group
			artifactId = mod.id
		}
	}
}

tasks.jar {
	archiveClassifier = "dev"
}

tasks.remapJar {
	injectAccessWidener = true
	input = tasks.shadowJar.get().archiveFile
	archiveClassifier = null
	dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
	configurations = listOf(shadowBundle)
	archiveClassifier = "dev-shadow"
	exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
	properties(listOf("META-INF/mods.toml"),
		"id" to mod.id,
		"name" to mod.id,
		"version" to mod.version,
		"forge_version" to common.mod.dep("forge_loader_version").substringBefore("."), // only specify major version of forge
		"minecraft_version" to minecraftVersion,
		"create_version" to mod.dep("create_forge_version").substringBefore("-"),
	)
}

sourceSets.main {
	resources { // include generated resources in resources
		srcDir("src/generated/resources")
		exclude("src/generated/resources/.cache")
	}
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
	dependsOn("build")
}
