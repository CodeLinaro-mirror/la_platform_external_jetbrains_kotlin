import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import java.io.File
import java.io.FileOutputStream

fun reversion(kotlinCompilerEmbeddableDirectory: File, oldVersion: String, newVersion: String) {
    val saxBuilder = SAXBuilder()

    // Rename version directory
    File(kotlinCompilerEmbeddableDirectory, oldVersion).renameTo(File(kotlinCompilerEmbeddableDirectory, newVersion))
    val versionDirectory = File(kotlinCompilerEmbeddableDirectory, newVersion)

    // Rename files in version directory
    File(versionDirectory, "kotlin-compiler-embeddable-$oldVersion.jar")
        .renameTo(File(versionDirectory, "kotlin-compiler-embeddable-$newVersion.jar"))
    File(versionDirectory, "kotlin-compiler-embeddable-$oldVersion-sources.jar")
        .renameTo(File(versionDirectory, "kotlin-compiler-embeddable-$newVersion-sources.jar"))
    File(versionDirectory, "kotlin-compiler-embeddable-$oldVersion-javadoc.jar")
        .renameTo(File(versionDirectory, "kotlin-compiler-embeddable-$newVersion-javadoc.jar"))
    File(versionDirectory, "kotlin-compiler-embeddable-$oldVersion.pom")
        .renameTo(File(versionDirectory, "kotlin-compiler-embeddable-$newVersion.pom"))

    // Update POM file with new version number
    val pomFile = File(versionDirectory, "kotlin-compiler-embeddable-$newVersion.pom")
    val pomDocument = saxBuilder.build(pomFile)
    pomDocument.getRootElement().getChildren().single { it.name == "version" }.setText(newVersion)
    XMLOutputter(Format.getPrettyFormat()).output(pomDocument, FileOutputStream(pomFile))
}

reversion(File(args[0]), args[1], args[2])


