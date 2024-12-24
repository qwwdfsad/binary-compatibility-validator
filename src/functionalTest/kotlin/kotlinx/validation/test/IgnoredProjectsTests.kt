/*
 * Copyright 2016-2020 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.validation.test

import kotlinx.validation.api.*
import kotlinx.validation.api.assertTaskSuccess
import kotlinx.validation.api.buildGradleKts
import kotlinx.validation.api.resolve
import kotlinx.validation.api.runner
import kotlinx.validation.api.test
import org.junit.Test

internal class IgnoredProjectsTests : BaseKotlinGradleTest() {
    /**
     * Creates a single project with 2 (Kotlin and Java Android Library) modules, applies
     * the plugin on the root project.
     */

    @Test
    fun `apiCheck should succeed when a project does not have an api file but is ignored via ignoredProjects`() {
        val runner = test {
            createProjectHierarchyWithPluginOnRoot()

            // Ignore "sub1" project
            buildGradleKts {
                resolve("examples/gradle/configuration/ignoredProjects/ignoreSub1.gradle.kts")
            }

            emptyApiFile(projectName = rootProjectDir.name)

            dir("sub1") {
                dir("subsub1") {
                    emptyApiFile(projectName = "subsub1")
                }
                dir("subsub2") {
                    emptyApiFile(projectName = "subsub2")
                }
            }

            dir("sub2") {
                emptyApiFile(projectName = "sub2")
            }

            runner {
                arguments.add("check")
            }
        }

        runner.build().apply {
            assertTaskSuccess(":apiCheck")
            assertTaskSuccess(":sub1:subsub1:apiCheck")
            assertTaskSuccess(":sub1:subsub2:apiCheck")
            assertTaskSuccess(":sub2:apiCheck")
        }
    }

    @Test
    fun `apiCheck should succeed ignoring a project and its subprojects`() {
        val runner = test {
            createProjectHierarchyWithPluginOnRoot()

            // Ignore "sub1" project and its subprojects ("subsub1" and "subsub2")
            buildGradleKts {
                resolve("examples/gradle/configuration/ignoredProjects/ignoreSub1.gradle.kts")
                resolve("examples/gradle/configuration/ignoredProjects/ignoreSubProjects.gradle.kts")
            }

            emptyApiFile(projectName = rootProjectDir.name)

            dir("sub1") {
                dir("subsub1") {}
                dir("subsub2") {}
            }

            dir("sub2") {
                emptyApiFile(projectName = "sub2")
            }

            runner {
                arguments.add("check")
            }
        }

        runner.build().apply {
            assertTaskSuccess(":apiCheck")
            assertTaskSuccess(":sub2:apiCheck")
        }
    }

    /**
     * Sets up a project hierarchy like this:
     * ```
     * build.gradle.kts (with the plugin)
     * settings.gradle.kts (including refs to 4 subprojects)
     * sub1/
     *    build.gradle.kts
     *    subsub1/build.gradle.kts
     *    subsub2/build.gradle.kts
     * sub2/build.gradle.kts
     * ```
     */
    private fun BaseKotlinScope.createProjectHierarchyWithPluginOnRoot() {
        settingsGradleKts {
            resolve("examples/gradle/settings/settings-with-hierarchy.gradle.kts")
        }
        buildGradleKts {
            resolve("examples/gradle/base/withPlugin.gradle.kts")
        }
        dir("sub1") {
            buildGradleKts {
                resolve("examples/gradle/base/withoutPlugin-noKotlinVersion.gradle.kts")
            }
            dir("subsub1") {
                buildGradleKts {
                    resolve("examples/gradle/base/withoutPlugin-noKotlinVersion.gradle.kts")
                }
            }
            dir("subsub2") {
                buildGradleKts {
                    resolve("examples/gradle/base/withoutPlugin-noKotlinVersion.gradle.kts")
                }
            }
        }
        dir("sub2") {
            buildGradleKts {
                resolve("examples/gradle/base/withoutPlugin-noKotlinVersion.gradle.kts")
            }
        }
    }
}
