plugins {
    id("java")
    id("application")
}

group = "com.sunit.groceryplus.backend"
version = "1.0-SNAPSHOT"




dependencies {
    implementation("com.sparkjava:spark-core:2.9.4")
    implementation("com.stripe:stripe-java:24.0.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-simple:2.0.7")
}

application {
    mainClass.set("com.sunit.groceryplus.backend.Server")
}
