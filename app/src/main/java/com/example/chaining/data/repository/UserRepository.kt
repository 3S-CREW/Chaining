package com.example.chaining.data.repository


import com.example.chaining.data.local.dao.UserDao
import com.example.chaining.data.local.entity.UserEntity
import com.example.chaining.domain.model.Application
import com.example.chaining.domain.model.LanguagePref
import com.example.chaining.domain.model.Notification
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.User
import com.example.chaining.domain.model.UserSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val rootRef: DatabaseReference,
    private val userDao: UserDao
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
        // Room에도 저장
        userDao.insertUser(newUser.toEntity())
        return uid
    }

    suspend fun checkUserExists(uid: String): Boolean {
        val snapshot = usersRef().child(uid).get().await()
        return snapshot.exists()
    }

    /** 1. Firebase → Room 동기화 후 Flow 제공 */
    /** Read (실시간 구독 - 내 계정, 변경사항이 있을 때마다 계속 가져오기) */
    fun observeMyUser(): Flow<User?> = callbackFlow {
        val uid = uidOrThrow()
        val ref = usersRef().child(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)?.copy(id = uid)
                if (user != null) {
                    // Firebase → Room DB에 저장
                    val entity = user.toEntity()
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(entity)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        // Room DB Flow 구독 → UI에 전달
        val dbFlow = userDao.getUser(uid)
        val job = CoroutineScope(Dispatchers.IO).launch {
            dbFlow.collect { entity ->
                val user = entity?.toUser() // UserEntity → User 변환
                trySend(user).isSuccess
            }
        }

        awaitClose {
            ref.removeEventListener(listener)
            job.cancel()
        }
    }

    /** Update (관심글 추가 / 삭제) */
    suspend fun toggleLikedPost(uid: String, postId: String) {
        val likedRef = usersRef().child(uid).child("likedPosts").child(postId)
        val snapshot = likedRef.get().await()
        val isCurrentlyLiked = snapshot.exists()

        val updates = hashMapOf<String, Any?>()

        if (isCurrentlyLiked) {
            // 좋아요 해제
            updates["/users/$uid/likedPosts/$postId"] = null
            updates["/posts/$postId/whoLiked/$uid"] = null
        } else {
            // 좋아요 추가
            updates["/users/$uid/likedPosts/$postId"] = true
            updates["/posts/$postId/whoLiked/$uid"] = true
        }

        // 원자적 업데이트 수행
        rootRef.updateChildren(updates).await()

        // Room DB에도 반영 (copyWith 사용)
        val current = userDao.getUser(uid).firstOrNull() ?: return
        val newLikedPosts = current.likedPosts.toMutableMap()
        if (isCurrentlyLiked) newLikedPosts.remove(postId) else newLikedPosts[postId] = true

        val updatedEntity = current.copyWith(mapOf("likedPosts" to newLikedPosts))
        userDao.updateUser(updatedEntity)
    }

    /** 프로필 사진 변경 */
    suspend fun updateProfileImage(newUrl: String) {
        val uid = uidOrThrow()
        usersRef().child(uid).child("profileImageUrl").setValue(newUrl).await()

        val current = userDao.getUser(uid).firstOrNull() ?: return
        val updatedEntity = current.copy(profileImageUrl = newUrl)
        userDao.updateUser(updatedEntity)
    }

    /** Update (팔로우 추가 / 삭제) */
    suspend fun toggleFollow(
        myInfo: UserSummary,
        otherInfo: UserSummary
    ) {
        val followedRef = usersRef().child(myInfo.id).child("following").child(otherInfo.id)
        val snapshot = followedRef.get().await()
        val isCurrentlyFollowed = snapshot.exists()

        val updates = hashMapOf<String, Any?>()

        if (isCurrentlyFollowed) {
            // 팔로우 해제
            updates["/users/${myInfo.id}/following/${otherInfo.id}"] = null
            updates["/users/${otherInfo.id}/follower/${myInfo.id}"] = null
        } else {
            // 팔로우 추가
            updates["/users/${myInfo.id}/following/${otherInfo.id}"] = otherInfo
            updates["/users/${otherInfo.id}/follower/${myInfo.id}"] = myInfo


            val newNotificationKey = rootRef.child("notifications")
                .child(otherInfo.id).push().key ?: error("알림 ID 생성 실패")
            val notification = Notification(
                id = newNotificationKey,
                type = "follow",
                sender = myInfo,
                createdAt = System.currentTimeMillis(),
                isRead = false,
                uid = otherInfo.id
            )
            println("피기" + notification)
            updates["/notifications/${otherInfo.id}/$newNotificationKey"] = notification
        }

        // 원자적 업데이트 수행
        rootRef.updateChildren(updates).await()

        // Room DB에도 반영 (copyWith 사용)
        val current = userDao.getUser(myInfo.id).firstOrNull() ?: return
        val newFollowing = current.following.toMutableMap()
        if (isCurrentlyFollowed) newFollowing.remove(otherInfo.id) else newFollowing[otherInfo.id] =
            otherInfo

        val updatedEntity = current.copyWith(mapOf("following" to newFollowing))
        userDao.updateUser(updatedEntity)
    }

    /** 전체 User 객체 저장 */
    suspend fun updateMyUser(user: User) {
        val uid = uidOrThrow()

        // Firebase에 전체 User 저장
        usersRef().child(uid).setValue(user).await()

        // Room에도 전체 User 저장
        userDao.insertUser(user.toEntity())
    }

    /** Delete (Soft Delete) */
    suspend fun deleteMyUser() {
        val uid = uidOrThrow()
        val updates = mapOf("isDeleted" to true)
        usersRef().child(uid).updateChildren(updates).await()
        // Room에서도 Soft Delete 반영
        val current = userDao.getUser(uid).firstOrNull() ?: return
        userDao.updateUser(current.copy(isDeleted = true))
    }

    /** 3. User → UserEntity 변환 함수 */
    private fun User.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            country = country,
            residence = residence,
            preferredDestinations = preferredDestinations,
            preferredLanguages = preferredLanguages,
            isPublic = isPublic,
            recruitPosts = recruitPosts,
            applications = applications,
            createdAt = createdAt,
            isDeleted = isDeleted,
            likedPosts = likedPosts,
            following = following,
            follower = follower
        )
    }

    /** 4. UserEntity → User 변환 함수 */
    private fun UserEntity.toUser(): User {
        return User(
            id = id,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            country = country,
            residence = residence,
            preferredDestinations = preferredDestinations,
            preferredLanguages = preferredLanguages,
            isPublic = isPublic,
            recruitPosts = recruitPosts,
            applications = applications,
            createdAt = createdAt,
            isDeleted = isDeleted,
            likedPosts = likedPosts,
            following = following,
            follower = follower
        )
    }

    /** 5. Room UserEntity를 업데이트할 수 있는 복사 함수 */
    private fun UserEntity.copyWith(updates: Map<String, Any?>): UserEntity {
        return this.copy(
            nickname = updates["nickname"] as? String ?: nickname,
            profileImageUrl = updates["profileImageUrl"] as? String ?: profileImageUrl,
            country = updates["country"] as? String ?: country,
            residence = updates["residence"] as? String ?: residence,
            preferredDestinations = updates["preferredDestinations"] as? String
                ?: preferredDestinations,
            isPublic = updates["isPublic"] as? Boolean ?: isPublic,
            // 필요시 나머지 필드도 추가
            likedPosts = updates["likedPosts"] as? Map<String, Boolean> ?: likedPosts,
            preferredLanguages = updates["preferredLanguages"] as? List<LanguagePref>
                ?: preferredLanguages,
            recruitPosts = updates["recruitPosts"] as? Map<String, RecruitPost> ?: recruitPosts,
            applications = updates["applications"] as? Map<String, Application> ?: applications,
            following = updates["following"] as? Map<String, UserSummary> ?: following,
            follower = updates["follower"] as? Map<String, UserSummary> ?: follower
        )
    }
}