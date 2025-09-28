package net.hitpromo.hitpromoworkstation.data.remote.dto

import net.hitpromo.hitpromoworkstation.domain.model.User
import net.hitpromo.hitpromoworkstation.domain.model.UserRole

/**
 * Data Transfer Object for AWS Cognito user data.
 */
data class CognitoUserDto(
    val username: String,
    val userId: String,
    val email: String,
    val attributes: Map<String, String> = emptyMap()
) {
    /**
     * Convert DTO to domain User model.
     */
    fun toDomainModel(): User {
        val role = when (attributes["custom:role"]?.uppercase()) {
            "ADMIN" -> UserRole.ADMIN
            "SUPERVISOR" -> UserRole.SUPERVISOR
            else -> UserRole.OPERATOR
        }

        return User(
            id = userId,
            username = username,
            email = email,
            role = role,
            isActive = attributes["custom:is_active"]?.toBoolean() ?: true,
            lastLoginTime = attributes["custom:last_login"]?.toLongOrNull()
        )
    }
}