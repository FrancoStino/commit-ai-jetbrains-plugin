package com.davideladisa.commitai

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CommitAIUtilsTest {

    @Test
    fun `test matchesGlobs with matching glob`() {
        assertTrue(CommitAIUtils.matchesGlobs("foo.txt", setOf("*.txt")))
    }

    @Test
    fun `test matchesGlobs with non-matching glob`() {
        assertFalse(CommitAIUtils.matchesGlobs("foo.md", setOf("*.txt")))
    }

    @Test
    fun `test matchesGlobs with multiple globs`() {
        assertTrue(CommitAIUtils.matchesGlobs("foo.txt", setOf("*.md", "*.txt")))
    }

    @Test
    fun `test matchesGlobs with no globs`() {
        assertFalse(CommitAIUtils.matchesGlobs("foo.txt", emptySet()))
    }

    @Test
    fun `test matchesGlobs with complex glob`() {
        assertTrue(CommitAIUtils.matchesGlobs("src/main/kotlin/com/davideladisa/commitai/AICommitsUtils.kt", setOf("**/commitai/**")))
    }

    @Test
    fun `test matchesGlobs with full path`() {
        assertTrue(CommitAIUtils.matchesGlobs("/home/user/project/src/main.kt", setOf("**/src/**.kt")))
    }

    @Test
    fun `test extractSvnBranchName with branch url`() {
        assertEquals("feature-branch", CommitAIUtils.extractSvnBranchName("https://svn.example.com/repo/branches/feature-branch/src"))
    }

    @Test
    fun `test extractSvnBranchName with tag url`() {
        assertEquals("tag: 1.0.0", CommitAIUtils.extractSvnBranchName("https://svn.example.com/repo/tags/1.0.0/src"))
    }

    @Test
    fun `test extractSvnBranchName with trunk url`() {
        assertEquals("trunk", CommitAIUtils.extractSvnBranchName("https://svn.example.com/repo/trunk/src"))
    }

    @Test
    fun `test extractSvnBranchName with root url`() {
        assertNull(CommitAIUtils.extractSvnBranchName("https://svn.example.com/repo/"))
    }
}