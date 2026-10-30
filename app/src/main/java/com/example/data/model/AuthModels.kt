package com.example.data.model

data class FirebaseStudentProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val institution: String = "",
    val educationLevel: String = "",
    val role: String = "STUDENT",
    val points: Int = 1000,
    val avatarUrl: String = "",
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class AuthFormValidation(
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val phoneError: String? = null,
    val institutionError: String? = null
) {
    val isValidLogin: Boolean
        get() = emailError == null && passwordError == null

    val isValidRegistration: Boolean
        get() = emailError == null && passwordError == null && nameError == null && phoneError == null && institutionError == null
}
