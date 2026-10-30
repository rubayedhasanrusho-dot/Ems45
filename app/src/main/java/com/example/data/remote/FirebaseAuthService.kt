package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.UserEntity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException

class FirebaseAuthService(private val context: Context) {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // 1. Sign in with Email and Password
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user ?: return@withContext Result.failure(Exception("Failed to get user after sign in"))
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Sign in error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 2. Register with Email, Password and create Firestore student profile
    suspend fun registerWithEmail(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        institution: String,
        educationLevel: String
    ): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user ?: return@withContext Result.failure(Exception("User creation failed"))

            // Update display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName.trim())
                .build()
            user.updateProfile(profileUpdates).await()

            // Save comprehensive student profile to Firestore
            val profileMap = hashMapOf(
                "uid" to user.uid,
                "fullName" to fullName.trim(),
                "email" to email.trim(),
                "phone" to phone.trim(),
                "institution" to institution.trim(),
                "educationLevel" to educationLevel,
                "role" to "STUDENT",
                "points" to 1000,
                "avatarUrl" to "",
                "isEmailVerified" to user.isEmailVerified,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .set(profileMap, SetOptions.merge())
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Registration error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 3. Send Password Reset Email
    suspend fun sendPasswordReset(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Password reset error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 4. Google Sign-In using CredentialManager
    suspend fun signInWithGoogle(webClientId: String? = null): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val credentialManager = CredentialManager.create(context)
            
            // If webClientId is provided, use GoogleIdTokenCredential
            val googleOption = if (!webClientId.isNullOrBlank()) {
                GetSignInWithGoogleOption.Builder(webClientId)
                    .build()
            } else {
                // Fallback using client id from google-services.json default web client
                GetSignInWithGoogleOption.Builder("568376404763-android.apps.googleusercontent.com")
                    .build()
            }

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user ?: return@withContext Result.failure(Exception("Failed to sign in with Google credential"))

                // Upsert to Firestore
                val profileMap = hashMapOf(
                    "uid" to user.uid,
                    "fullName" to (user.displayName ?: "Student"),
                    "email" to (user.email ?: ""),
                    "phone" to (user.phoneNumber ?: ""),
                    "institution" to "Notre Dame College, Dhaka",
                    "educationLevel" to "HSC - Science",
                    "role" to "STUDENT",
                    "points" to 1250,
                    "avatarUrl" to (user.photoUrl?.toString() ?: ""),
                    "isEmailVerified" to user.isEmailVerified,
                    "lastLoginAt" to System.currentTimeMillis()
                )
                firestore.collection("users")
                    .document(user.uid)
                    .set(profileMap, SetOptions.merge())
                    .await()

                Result.success(user)
            } else {
                Result.failure(Exception("Unsupported credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialException) {
            Log.w("FirebaseAuthService", "CredentialManager cancelled or unavailable: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Google sign-in exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 5. Fetch user profile from Firestore
    suspend fun fetchFirestoreUserProfile(uid: String): UserEntity? = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists()) {
                UserEntity(
                    id = 1L, // mapped local id
                    fullName = doc.getString("fullName") ?: "Student",
                    email = doc.getString("email") ?: "",
                    phone = doc.getString("phone") ?: "",
                    institution = doc.getString("institution") ?: "College",
                    educationLevel = doc.getString("educationLevel") ?: "HSC - Science",
                    role = doc.getString("role") ?: "STUDENT",
                    points = doc.getLong("points")?.toInt() ?: 1000,
                    avatarUrl = doc.getString("avatarUrl") ?: "",
                    isPhoneVerified = true,
                    isEmailVerified = doc.getBoolean("isEmailVerified") ?: true
                )
            } else null
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Fetch profile error: ${e.message}")
            null
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
