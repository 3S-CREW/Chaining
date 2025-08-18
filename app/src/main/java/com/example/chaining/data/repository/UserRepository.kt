package com.example.chaining.data.repository


import com.example.chaining.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val rootRef: DatabaseReference
) {
    private fun uidOrThrow(): String =
        auth.currentUser?.uid ?: error("로그인이 필요합니다.")

    private fun usersRef(): DatabaseReference = rootRef.child("users")

    /** Create (신규 유저 추가) */
    suspend fun addUser(user: User): String {
        val uid = uidOrThrow()

        val newUser = user.copy(
            id = uid,
            createdAt = System.currentTimeMillis()
        )

        usersRef().child(uid).setValue(newUser).await()
        return uid
    }

    /** Read (단건) */
    suspend fun getUser(id: String): User? {
        val snap = usersRef().child(id).get().await()
        return snap.getValue<User>()?.copy(id = id)
    }

    /** Read (내 계정 정보, 한 번만 가져오기) */
    suspend fun getMyUser(): User? {
        val uid = uidOrThrow()
        return getUser(uid)
    }

    /** Read (실시간 구독 - 내 계정, 변경사항이 있을 때마다 계속 가져오기) */
    fun observeMyUsers(): Flow<User?> = callbackFlow {
        val uid = uidOrThrow()
        val ref = usersRef().child(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue<User>()?.copy(id = uid)).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /** Update (부분 수정) */
    suspend fun updateUser(id: String, updates: Map<String, Any?>) {
        if (updates.isEmpty()) return
        usersRef().child(id).updateChildren(updates).await()
    }

    /** Delete (Soft Delete) */
    suspend fun deleteUser(id: String) {
        val updates = mapOf("isDeleted" to true)
        usersRef().child(id).updateChildren(updates).await()
    }
}