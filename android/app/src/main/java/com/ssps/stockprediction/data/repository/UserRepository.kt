package com.ssps.stockprediction.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.ssps.stockprediction.data.local.dao.UserDao
import com.ssps.stockprediction.data.local.entity.UserEntity
import java.security.MessageDigest

/**
 * Repository for user-related operations.
 * Handles password hashing and duplicate-email detection.
 */
class UserRepository(private val userDao: UserDao) {

    /**
     * Registers a new user.
     * Returns Result.success(userId) on success, or Result.failure on error.
     */
    suspend fun registerUser(name: String, email: String, password: String): Result<Long> {
        return try {
            val hashedPassword = hashPassword(password)
            val user = UserEntity(
                name = name,
                email = email.lowercase().trim(),
                passwordHash = hashedPassword
            )
            val id = userDao.insertUser(user)
            Result.success(id)
        } catch (e: SQLiteConstraintException) {
            Result.failure(Exception("An account with this email already exists."))
        } catch (e: Exception) {
            Result.failure(Exception("Registration failed: ${e.message}"))
        }
    }

    /**
     * Authenticates a user by email and password.
     * Returns the UserEntity on success, null on failure.
     */
    suspend fun loginUser(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email.lowercase().trim()) ?: return null
        val hashedPassword = hashPassword(password)
        return if (user.passwordHash == hashedPassword) user else null
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return userDao.getUserById(id)
    }

    /**
     * Simple SHA-256 hashing suitable for a local student project.
     * Not intended for production security.
     */
    companion object {
        fun hashPassword(password: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            return hashBytes.joinToString("") { "%02x".format(it) }
        }
    }
}
