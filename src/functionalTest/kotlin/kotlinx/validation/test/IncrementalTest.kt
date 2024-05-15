/*
 * Copyright 2016-2024 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.validation.test

import kotlinx.validation.api.*
import org.junit.*

class IncrementalTest : BaseKotlinGradleTest() {

    @Test
    fun `fails when removing source lines`() {
        val runner = test {
            buildGradleKts {
                resolve("/examples/gradle/base/withPlugin.gradle.kts")
                resolve("/examples/gradle/configuration/incremental/incremental.gradle.kts")
            }
            kotlin("IncrementalBase.kt") {
                resolve("/examples/classes/IncrementalRemoval.kt")
            }
            apiFile(rootProjectDir.name) {
                resolve("/examples/classes/Incremental.dump")
            }
            runner {
                arguments.add(":apiCheck")
            }
        }
        runner.buildAndFail().apply {
            assertTaskFailure(":apiCheck")
        }
    }

    @Test
    fun `fails when modifying source lines`() {
        val runner = test {
            buildGradleKts {
                resolve("/examples/gradle/base/withPlugin.gradle.kts")
                resolve("/examples/gradle/configuration/incremental/incremental.gradle.kts")
            }
            kotlin("IncrementalBase.kt") {
                resolve("/examples/classes/IncrementalModification.kt")
            }
            apiFile(rootProjectDir.name) {
                resolve("/examples/classes/Incremental.dump")
            }
            runner {
                arguments.add(":apiCheck")
            }
        }
        runner.buildAndFail().apply {
            assertTaskFailure(":apiCheck")
        }
    }

    @Test
    fun `succeeds when adding source lines`() {
        val runner = test {
            buildGradleKts {
                resolve("/examples/gradle/base/withPlugin.gradle.kts")
                resolve("/examples/gradle/configuration/incremental/incremental.gradle.kts")
            }
            kotlin("IncrementalBase.kt") {
                resolve("/examples/classes/IncrementalAddition.kt")
            }
            apiFile(rootProjectDir.name) {
                resolve("/examples/classes/Incremental.dump")
            }
            runner {
                arguments.add(":apiCheck")
            }
        }
        runner.build().apply {
            assertTaskSuccess(":apiCheck")
        }
    }

    @Test
    fun `does not dump when removing source lines`() {
        val runner = test {
            buildGradleKts {
                resolve("/examples/gradle/base/withPlugin.gradle.kts")
                resolve("/examples/gradle/configuration/incremental/incremental.gradle.kts")
            }
            kotlin("IncrementalBase.kt") {
                resolve("/examples/classes/IncrementalRemoval.kt")
            }
            apiFile(rootProjectDir.name) {
                resolve("/examples/classes/Incremental.dump")
            }
            runner {
                arguments.add(":apiCheck")
            }
        }
        runner.buildAndFail().apply {
            assertTaskFailure(":apiCheck")
            assertTaskNotRun(":apiDump")
        }
    }

    @Test
    fun `does not dump when modifying source lines`() {
        val runner = test {
            buildGradleKts {
                resolve("/examples/gradle/base/withPlugin.gradle.kts")
                resolve("/examples/gradle/configuration/incremental/incremental.gradle.kts")
            }
            kotlin("IncrementalBase.kt") {
                resolve("/examples/classes/IncrementalModification.kt")
            }
            apiFile(rootProjectDir.name) {
                resolve("/examples/classes/Incremental.dump")
            }
            runner {
                arguments.add(":apiCheck")
            }
        }
        runner.buildAndFail().apply {
            assertTaskFailure(":apiCheck")
            assertTaskNotRun(":apiDump")
        }
    }

    @Ignore
    @Test
    fun `updates dump when adding source lines`() {
        val runner = test {
            buildGradleKts {
                resolve("/examples/gradle/base/withPlugin.gradle.kts")
                resolve("/examples/gradle/configuration/incremental/incremental.gradle.kts")
            }
            kotlin("IncrementalBase.kt") {
                resolve("/examples/classes/IncrementalAddition.kt")
            }
            apiFile(rootProjectDir.name) {
                resolve("/examples/classes/Incremental.dump")
            }
            runner {
                arguments.add(":apiCheck")
            }
        }
        runner.build().apply {
            assertTaskSuccess(":apiCheck")
            assertTaskSuccess(":apiDump")
        }
    }

}