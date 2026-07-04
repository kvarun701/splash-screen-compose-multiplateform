package com.ganesh.splashscreen

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import com.ganesh.composepref.InMemoryKeyValueStorage

class SharedLogicAndroidHostTest {

    @Test
    fun testSessionStorageFlow() {
        val storage = InMemoryKeyValueStorage()
        
        // 1. Initial State: No session active
        val initialUser = storage.getString("username", "")
        assertTrue(initialUser.isNullOrBlank())
        
        // 2. Login: Put username in storage
        storage.putString("username", "testUser")
        val savedUser = storage.getString("username", "")
        assertEquals("testUser", savedUser)
        assertFalse(savedUser.isNullOrBlank())
        
        // 3. Logout: Clear storage and verify session is gone
        storage.clear()
        val clearedUser = storage.getString("username", "")
        assertTrue(clearedUser.isNullOrBlank())
    }
}