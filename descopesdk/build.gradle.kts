plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
    id("signing")
}

kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget {
        compilations.configureEach { 
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting
        val androidMain by getting
        val androidUnitTest by getting
        val appleMain by getting
        val iosMain by getting
        val nativeMain by getting
        
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.androidx.annotations)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)   
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.browser)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.androidx.lifecycle.common)
            implementation(libs.androidx.lifecycle.process)
            implementation(libs.androidx.security.crypto)
            implementation(libs.google.id)
        }

        androidUnitTest.dependencies {
            implementation(libs.json.jvm)
        }
    }
    
    targets.configureEach { 
        compilations.configureEach { 
            compilerOptions.configure { 
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}

android {
    namespace = "com.descope"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testOptions.targetSdk = 34
        lint.targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        register("release", MavenPublication::class) {
            groupId = "com.descope"
            artifactId = "descope-kotlin"
            version = System.getenv("DESCOPESDK_VERSION")
            pom {
                name = project.name
                description = project.name
                url = "https://github.com/descope/descope-kotlin"
                inceptionYear = "2023"
                licenses {
                    license {
                        name = "MIT License"
                        url = "http://www.opensource.org/licenses/mit-license.php"
                    }
                }
                developers {
                    developer {
                        id = "descope"
                        name = "Descope Inc"
                    }
                }
                scm {
                    connection = "scm:https://github.com/descope/descope-kotlin.git"
                    developerConnection = "scm:git@github.com:descope/descope-kotlin.git"
                    url = "https://github.com/descope/descope-kotlin"
                }
            }
            afterEvaluate {
                from(components.getByName("release"))
            }
        }
    }
}

signing {
    val signingKey = System.getenv("PGP_KEY")
    val signingPassword = System.getenv("PGP_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications.getByName("release")) 
}