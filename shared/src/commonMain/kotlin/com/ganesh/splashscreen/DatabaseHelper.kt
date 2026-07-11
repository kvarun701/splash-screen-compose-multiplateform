package com.ganesh.splashscreen

import app.cash.sqldelight.db.SqlDriver
import com.ganesh.splashscreen.database.AppDatabase
import com.ganesh.splashscreen.database.User

class DatabaseHelper(driver: SqlDriver) {
    private val database = AppDatabase(driver)
    private val dbQueries = database.appDatabaseQueries

    fun insertUser(user: User) {
        dbQueries.insertUser(user.username, user.email, user.password, user.mobile)
    }

    fun getUser(username: String): User? {
        return dbQueries.getUser(username).executeAsOneOrNull()
    }

    fun deleteUserByUsername(username: String) {
        dbQueries.deleteUserByUsername(username)
    }
}
