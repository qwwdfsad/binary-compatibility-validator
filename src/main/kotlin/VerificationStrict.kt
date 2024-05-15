/*
 * Copyright 2016-2024 JetBrains s.r.o.
 * Use of this source code is governed by the Apache 2.0 License that can be found in the LICENSE.txt file.
 */

package kotlinx.validation

internal class VerificationStrict(private val subject: String): Verification {
    override fun verify(diffSet: Collection<String>) {
        check(diffSet.isEmpty()) {
            val diffText = diffSet.joinToString("\n\n")
            "API check failed for project $subject.\n$diffText\n\n You can run :$subject:apiDump task to overwrite API declarations"
        }
    }
}