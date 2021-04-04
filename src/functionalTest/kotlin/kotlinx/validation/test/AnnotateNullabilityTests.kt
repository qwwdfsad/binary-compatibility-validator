/*
 * Copyright 2016-2021 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.validation.test

import kotlinx.validation.api.*
import kotlinx.validation.api.BaseKotlinGradleTest
import kotlinx.validation.api.buildGradleKts
import kotlinx.validation.api.resolve
import kotlinx.validation.api.test
import org.assertj.core.api.Assertions
import org.junit.Test
import kotlin.test.assertTrue

internal class AnnotateNullabilityTests : BaseKotlinGradleTest() {
    @Test
    fun `apiDump should NOT annotate nullable types by default`() {
        val runner = test {
            buildGradleKts {
                resolve("examples/gradle/base/withPlugin.gradle.kts")
            }

            kotlin("NullableClass.kt") {
                resolve("examples/classes/NullableClass.kt")
            }

            runner {
                arguments.add(":apiDump")
            }
        }

        runner.build().apply {
            assertTaskSuccess(":apiDump")

            assertTrue(rootProjectApiDump.exists(), "api dump file should exist")

            val expected = readFileList("examples/classes/NullableClass.dump")
            Assertions.assertThat(rootProjectApiDump.readText()).isEqualToIgnoringNewLines(expected)
        }
    }
    @Test
    fun `apiDump should annotate nullable types, if annotateNullability is enabled`() {
        val runner = test {
            buildGradleKts {
                resolve("examples/gradle/base/withPlugin.gradle.kts")
                resolve("examples/gradle/configuration/annotateNullability/annotateNullabilityEnabled.gradle.kts")
            }

            kotlin("NullableClass.kt") {
                resolve("examples/classes/NullableClass.kt")
            }

            runner {
                arguments.add(":apiDump")
            }
        }

        runner.build().apply {
            assertTaskSuccess(":apiDump")

            assertTrue(rootProjectApiDump.exists(), "api dump file should exist")

            val expected = readFileList("examples/classes/NullableClassAnnotated.dump")
            Assertions.assertThat(rootProjectApiDump.readText()).isEqualToIgnoringNewLines(expected)
        }
    }
}