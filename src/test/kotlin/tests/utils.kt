/*
 * Copyright 2016-2023 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.validation.api.tests

import kotlinx.validation.api.*
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.fail

private val OVERWRITE_EXPECTED_OUTPUT = System.getProperty("overwrite.output")?.toBoolean() ?: false // use -Doverwrite.output=true

fun List<ClassBinarySignature>.dumpAndCompareWith(to: File) {
    if (!to.exists()) {
        to.parentFile?.mkdirs()
        to.bufferedWriter().use { dump(to = it) }
        fail("Expected data file did not exist. Generating: $to")
    } else {
        val actual = dump(to = StringBuilder()).toString()
        assertEqualsToFile(to, actual)
    }
}

private fun assertEqualsToFile(expectedFile: File, actualText: String) {
    val expectedText = expectedFile.readText()

    if (OVERWRITE_EXPECTED_OUTPUT && expectedText != actualText) {
        expectedFile.writeText(actualText)
        assertEquals(expectedText, actualText, "Actual data differs from file content: ${expectedFile.name}, rewriting")
    }

    assertEquals(expectedText, actualText, "Actual data differs from file content: ${expectedFile.name}\nTo overwrite the expected API rerun with -Doverwrite.output=true parameter\n")
}
