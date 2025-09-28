package net.hitpromo.hitpromoworkstation.domain.model

/**
 * Domain model representing a user in the system.
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val lastLoginTime: Long? = null
)

/**
 * User roles for the industrial workstation system.
 */
enum class UserRole {
    OPERATOR,
    SUPERVISOR,
    ADMIN
}