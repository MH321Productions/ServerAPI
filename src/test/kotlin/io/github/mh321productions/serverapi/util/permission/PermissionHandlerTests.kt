package io.github.mh321productions.serverapi.util.permission

import kotlin.test.Test
import kotlin.test.expect

class PermissionHandlerTests {

    @Test
    fun `mapping empty list returns false`() {
        val data = listOf<Data>()
        val name = "test"

        expect(false) { containsName(data, name) }
    }

    @Test
    fun `mapping list returns true`() {
        val data = listOf(Data("test"))
        val name = "test"

        expect(true) { containsName(data, name) }
    }

    private fun containsName(data: List<Data>, name: String): Boolean {
        return data
            .map { it.name }
            .contains(name)
    }
}

data class Data(val name: String)