package com.example.chaining.data.repository

import com.example.chaining.data.local.dao.NotificationDao
import com.example.chaining.data.local.entity.NotificationEntity
import com.example.chaining.domain.model.Notification
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
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        private val rootRef: DatabaseReference,
        private val notificationDao: NotificationDao,
    ) {
        private fun uidOrThrow(): String = auth.currentUser?.uid ?: error("로그인이 필요합니다.")

        private fun notificationsRef(uid: String): DatabaseReference = rootRef.child("notifications").child(uid)

        /** 알림 목록을 실시간으로 가져오기 */
        fun observeNotifications(): Flow<List<Notification>> =
            callbackFlow {
                val uid = uidOrThrow()
                val ref =
                    notificationsRef(uid)
                        .orderByChild("createdAt")
                        .limitToLast(50)

                val listener =
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val notifications =
                                snapshot.children.mapNotNull { data ->
                                    data.getValue(Notification::class.java)?.copy(id = data.key ?: "")
                                }
                            CoroutineScope(Dispatchers.IO).launch {
                                notificationDao.insertDao(notifications.toEntity(uid))
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            close(error.toException())
                        }
                    }

                ref.addValueEventListener(listener)

                val job =
                    launch {
                        notificationDao.getNotifications(uid)
                            .collect { entities ->
                                trySend(entities.toNotifications()).isSuccess
                            }
                    }
                awaitClose {
                    ref.removeEventListener(listener)
                    job.cancel()
                }
            }

        /** 3. Notification → NotificationEntity 변환 함수 */
        private fun List<Notification>.toEntity(uid: String): List<NotificationEntity> {
            return this.map {
                NotificationEntity(
                    id = it.id,
                    type = it.type,
                    postId = it.postId,
                    applicationId = it.applicationId,
                    sender = it.sender,
                    status = it.status,
                    createdAt = it.createdAt,
                    isRead = it.isRead,
                    uid = uid,
                    closeAt = it.closeAt,
                )
            }
        }

        private fun List<NotificationEntity>.toNotifications(): List<Notification> {
            return this.map {
                Notification(
                    id = it.id,
                    type = it.type,
                    postId = it.postId,
                    applicationId = it.applicationId,
                    sender = it.sender,
                    status = it.status,
                    createdAt = it.createdAt,
                    isRead = it.isRead,
                    uid = it.uid,
                    closeAt = it.closeAt,
                )
            }
        }
    }
