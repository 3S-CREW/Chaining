package com.example.chaining.data.repository

import com.example.chaining.domain.model.Application
import com.example.chaining.domain.model.Notification
import com.example.chaining.domain.model.UserSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationRepository
@Inject
constructor(
    private val auth: FirebaseAuth,
    private val rootRef: DatabaseReference,
) {
    private fun uidOrThrow(): String = auth.currentUser?.uid ?: error("로그인이 필요합니다.")

    private fun applicationsRef(): DatabaseReference = rootRef.child("applications")

    /** Create (지원서 제출) */
    suspend fun submitApplication(application: Application): Result<String> {
        return try {
            val uid = uidOrThrow()

            val applicationRef = applicationsRef().push()
            val applicationId = applicationRef.key ?: error("지원서 ID 생성 실패")
            val finalApplicant = application.applicant.copy(id = uid)

            val newApplication =
                application.copy(
                    applicationId = applicationId,
                    createdAt = System.currentTimeMillis(),
                    applicant = finalApplicant,
                )

            // 멀티패스 업데이트 경로 구성
            val updates =
                hashMapOf<String, Any?>(
                    // 1. applications 노드에 지원서 저장
                    "/applications/$applicationId" to newApplication,

                    // 2. posts/{postId}/applications/{applicationId} = true
                    "/posts/${application.postId}/applications/$applicationId" to newApplication,

                    // 3. users/{uid}/myApplications/{applicationId} = true
                    "/users/$uid/applications/$applicationId" to newApplication,
                )

            val postOwnerId = application.owner.id

            val newNotificationKey = rootRef.child("notifications")
                .child(postOwnerId).push().key ?: error("알림 ID 생성 실패")

            val notification = Notification(
                id = newNotificationKey,
                type = "application",
                sender = UserSummary(
                    id = application.applicant.id,
                    nickname = application.applicant.nickname,
                    profileImageUrl = application.applicant.profileImageUrl,
                    country = application.applicant.country,
                ),
                postId = application.postId,
                applicationId = applicationId,
                createdAt = System.currentTimeMillis(),
                isRead = false,
                uid = postOwnerId
            )

            updates["/notifications/$postOwnerId/$newNotificationKey"] = notification

            // 원자적 업데이트 수행
            rootRef.updateChildren(updates).await()

            Result.success(applicationId)
        } catch (e: DatabaseException) {
            if (e.message?.contains("Permission denied", ignoreCase = true) == true) {
                Result.failure(Exception("먼저 마이페이지에서 프로필 정보를 모두 입력하세요."))
            } else {
                Result.failure(Exception("데이터베이스 오류가 발생했습니다: ${e.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeMyApplicationStatus(): Flow<List<Application>> =
        callbackFlow {
            val uid = uidOrThrow()
            val ref = applicationsRef().orderByChild("applicant/id").equalTo(uid)

            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val applications =
                            snapshot.children.mapNotNull { snap ->
                                snap.getValue(Application::class.java)?.let { app ->
                                    app.copy(applicationId = snap.key ?: "")
                                }
                            }
                        trySend(applications).isSuccess
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                }

            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }

    /** Read (지원서 보기) */
    suspend fun getApplication(id: String): Application? {
        val snap = applicationsRef().child(id).get().await()

        return snap.getValue(Application::class.java)?.copy(
            applicationId = id,
        )
    }

    /** Read (내가 쓴 지원서 전체 정보, 한 번만 가져오기) */
    suspend fun getMyApplications(): List<Application> {
        val uid = uidOrThrow()
        val snap = applicationsRef().orderByChild("applicant/id").equalTo(uid).get().await()

        return snap.children.mapNotNull { it.getValue(Application::class.java) }
    }

    /** Read (실시간 구독 - 내 계정, 변경사항이 있을 때마다 계속 가져오기) */
    fun observeMyApplications(): Flow<List<Application>> =
        callbackFlow {
            val uid = uidOrThrow()
            val ref = applicationsRef().orderByChild("applicant/id").equalTo(uid)

            val listener =
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val applications =
                            snapshot.children.mapNotNull { it.getValue(Application::class.java) }
                        trySend(applications).isSuccess
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                }

            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }

    /** Update (지원서 승인) */
    suspend fun updateStatus(
        application: Application,
        value: String,
    ): Result<Unit> {
        return try {
            val applicationRef = applicationsRef().child(application.applicationId)
            val currentAppSnapshot = applicationRef.get().await()
            val currentStatus = currentAppSnapshot.child("status").getValue(String::class.java)

            if (currentStatus != "PENDING") {
                return Result.failure(Exception("이미 처리된 지원서입니다."))
            }
            // 멀티패스 업데이트 경로 구성
            println("호시기" + application.applicant.id + application.applicationId)
            val updates =
                hashMapOf<String, Any?>(
                    // 1. applications 노드에 지원서 저장
                    "/applications/${application.applicationId}/status" to value,

                    // 2. posts/{postId}/applications/{applicationId}
                    "/posts/${application.postId}/applications/${application.applicationId}/status" to value,

                    // 3. users/{uid}/myApplications/{applicationId}
                    "/users/${application.applicant.id}/applications/${application.applicationId}/status" to value,
                )

            val newNotificationKey = rootRef.child("notifications")
                .child(application.applicant.id).push().key ?: error("알림 ID 생성 실패")
            val notification = Notification(
                id = newNotificationKey,
                type = "status_update",
                sender = UserSummary(
                    id = application.applicant.id,
                    nickname = application.applicant.nickname,
                    profileImageUrl = application.applicant.profileImageUrl,
                    country = application.applicant.country,
                ),
                postId = application.postId,
                applicationId = application.applicationId,
                status = value,
                createdAt = System.currentTimeMillis(),
                isRead = false
            )
            updates["/notifications/${application.applicant.id}/$newNotificationKey"] = notification
            // 원자적 업데이트 수행
            rootRef.updateChildren(updates).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    /** Delete (Soft Delete) */
    suspend fun deleteApplication(id: String) {
        val updates = mapOf("isDeleted" to true)
        applicationsRef().child(id).updateChildren(updates).await()
    }
}
