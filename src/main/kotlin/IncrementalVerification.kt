/*
 * Copyright 2016-2024 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.validation

import org.gradle.api.logging.Logger

internal class IncrementalVerification(private val subject: String, private val logger: Logger): Verification {
    override fun verify(diffSet: Collection<String>) {
        var containsAdditions = false
        var containsRemovals = false
        out@ for (diff in diffSet) {
            for (line in diff.split("\n")) {
                when {
                    line.startsWith("+++") || line.startsWith("---") -> continue
                    line.startsWith("-") -> containsRemovals = true
                    line.startsWith("+") -> containsAdditions = true
                }
                if (containsRemovals) break@out
            }
        }
        check(!containsRemovals) {
            val diffText = diffSet.joinToString("\n\n")
            "Incremental API check failed for project $subject.\n$diffText\n\n You can run :$subject:apiDump task to overwrite API declarations. These changes likely break compatibility with existing consumers using library '$subject', consider incrementing major version code for your next release"
        }
        if (containsAdditions) {
            logger.warn("API is incrementally compatible with previous version, however is not identical to the API file provided.")
        }
    }
}