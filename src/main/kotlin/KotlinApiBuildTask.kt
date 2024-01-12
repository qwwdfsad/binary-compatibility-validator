/*
 * Copyright 2016-2020 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.validation

import kotlinx.validation.api.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.tasks.*
import java.util.jar.JarFile
import javax.inject.Inject

open class KotlinApiBuildTask @Inject constructor(
) : BuildTaskBase() {

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var inputClassesDirs: FileCollection? = null

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    val inputJar: RegularFileProperty = this.project.objects.fileProperty()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    lateinit var inputDependencies: FileCollection

    @TaskAction
    fun generate() {
        outputApiFile.delete()
        outputApiFile.parentFile.mkdirs()

        val inputClassesDirs = inputClassesDirs
        val signatures = when {
            // inputJar takes precedence if specified
            inputJar.isPresent ->
                JarFile(inputJar.get().asFile).use { it.loadApiFromJvmClasses() }
            inputClassesDirs != null ->
                inputClassesDirs.asFileTree.asSequence()
                    .filter {
                        !it.isDirectory && it.name.endsWith(".class") && !it.name.startsWith("META-INF/")
                    }
                    .map { it.inputStream() }
                    .loadApiFromJvmClasses()
            else ->
                throw GradleException("KotlinApiBuildTask should have either inputClassesDirs, or inputJar property set")
        }


        val filteredSignatures = signatures
            .retainExplicitlyIncludedIfDeclared(publicPackages, publicClasses, publicMarkers)
            .filterOutNonPublic(ignoredPackages, ignoredClasses)
            .filterOutAnnotated(nonPublicMarkers.map(::replaceDots).toSet())

        outputApiFile.bufferedWriter().use { writer ->
            filteredSignatures
                .sortedBy { it.name }
                .forEach { api ->
                    writer.append(api.signature).appendLine(" {")
                    api.memberSignatures
                        .sortedWith(MEMBER_SORT_ORDER)
                        .forEach { writer.append("\t").appendLine(it.signature) }
                    writer.appendLine("}\n")
                }
        }
    }
}

