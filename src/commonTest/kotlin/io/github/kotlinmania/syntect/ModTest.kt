// port-lint: tests syntect/src/lib.rs
package io.github.kotlinmania.syntect

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ModTest {
    @Test
    fun testModuleDescriptors() {
        assertEquals("syntect", SyntectLib.MODULE_NAME)
        assertEquals("syntect", SyntectLib.CRATE_NAME)
    }

    @Test
    fun testErrorHierarchies() {
        val badPath = LoadingError.BadPath
        assertEquals("Invalid path", badPath.message)

        val wrapped = Error.LoadingError(badPath)
        assertTrue(wrapped.message!!.contains("Invalid path"))
    }
}
