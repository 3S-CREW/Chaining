package com.example.chaining.data.repository

import android.util.Log
import com.example.chaining.domain.model.RecruitPost
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
class RecruitPostRepository
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val rootRef: DatabaseReference,
    ) {
        private fun uidOrThrow(): String = auth.currentUser?.uid ?: error("로그인이 필요합니다.")

        private fun postsRef(): DatabaseReference = rootRef.child("posts")

        /** Create (신규 모집글 생성) */
        suspend fun createPost(post: RecruitPost): Result<String> {
            return try {
                val uid = uidOrThrow()

                val postRef = postsRef().push()
                val postId = postRef.key ?: error("게시글 ID 생성 실패")

                val finalOwner = post.owner.copy(id = uid)

                val newPost =
                    post.copy(
                        postId = postId,
                        createdAt = System.currentTimeMillis(),
                        owner = finalOwner,
                    )

                val updates =
                    hashMapOf<String, Any?>(
                        // 1. posts 노드에 모집글 저장
                        "/posts/$postId" to newPost,
                        // 2. user의 recruitPosts 노드에 모집글 저장
                        "/users/$uid/recruitPosts/$postId" to newPost,
                    )

                rootRef.updateChildren(updates).await()

                Result.success(postId)
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

        /** Read (단건) */
        suspend fun getPost(id: String): RecruitPost? {
            val snap = postsRef().child(id).get().await()

            return snap.getValue(RecruitPost::class.java)?.copy(
                postId = id,
            )
        }

        /** Read (전체) */
        suspend fun getAllPosts(): List<RecruitPost> {
            val snap = postsRef().get().await()
            val posts = mutableListOf<RecruitPost>()
            Log.d("RecruitPostRepository", "postsRef path = ${postsRef()}")

            for (child in snap.children) {
                val post = child.getValue(RecruitPost::class.java)
                if (post != null) {
                    posts.add(post.copy(postId = child.key ?: ""))
                }
            }

            return posts
        }

        /** Read (내가 쓴글 전체 정보, 한 번만 가져오기) */
        suspend fun getMyPosts(): List<RecruitPost> {
            val uid = uidOrThrow()
            val snap = postsRef().orderByChild("owner/id").equalTo(uid).get().await()

            return snap.children.mapNotNull { it.getValue(RecruitPost::class.java) }
        }

        /** Read (실시간 구독 - 내 계정, 변경사항이 있을 때마다 계속 가져오기) */
        fun observeMyPosts(): Flow<List<RecruitPost>> =
            callbackFlow {
                val uid = uidOrThrow()
                val ref = postsRef().orderByChild("owner/id").equalTo(uid)

                val listener =
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val posts = snapshot.children.mapNotNull { it.getValue(RecruitPost::class.java) }
                            trySend(posts).isSuccess
                        }

                        override fun onCancelled(error: DatabaseError) {
                            close(error.toException())
                        }
                    }

                ref.addValueEventListener(listener)
                awaitClose { ref.removeEventListener(listener) }
            }

        /** 전체 RecruitPost 객체 저장 */
        suspend fun savePost(post: RecruitPost) {
            val uid = uidOrThrow()

            val updates =
                hashMapOf<String, Any?>(
                    // 1. posts 노드에 모집글 저장
                    "/posts/${post.postId}" to post,
                    // 2. user의 recruitPosts 노드에 모집글 저장
                    "/users/$uid/recruitPosts/${post.postId}" to post,
                )

            rootRef.updateChildren(updates).await()
        }

        /** Delete (Soft Delete) */
        suspend fun deletePost(postId: String) {
            val uid = uidOrThrow()

            // 멀티패스 업데이트 경로 구성
            val updates =
                hashMapOf<String, Any?>(
                    // 1. posts 노드에서 해당 모집글을 찾아 isDeleted를 true로 수정
                    "/posts/$postId/isDeleted" to true,
                    // 2. user의 recruitPosts 노드에서 해당 모집글을 찾아 isDeleted를 true로 수정
                    "/users/$uid/recruitPosts/$postId/isDeleted" to true,
                )

            // 원자적 업데이트 수행
            rootRef.updateChildren(updates).await()
        }
    }
