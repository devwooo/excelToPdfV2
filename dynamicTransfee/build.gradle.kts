plugins {
    id("java")
}

group = "hpy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // POI
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    // PDF
    implementation("com.itextpdf:itextpdf:5.5.13.6")

    //FONT
    implementation("com.itextpdf:itext-asian:5.2.0");
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}