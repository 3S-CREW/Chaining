package com.example.chaining.data.repository

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
class NotificationRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val rootRef: DatabaseReference,
) {
    private fun uidOrThrow(): String =
        auth.currentUser?.uid ?: error("로그인이 필요합니다.")

    private fun notificationsRef(): DatabaseReference = rootRef.child("notifications")

    /** 알림 목록을 실시간으로 가져오기 */
    fun observeNotifications(): Flow<List<Notification>> = callbackFlow {
        val uid = uidOrThrow()
        val ref = notificationsRef()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = snapshot.children.mapNotNull { data ->
                    data.getValue(Notification::class.java)?.copy(id = data.key ?: "")
                }.sortedByDescending { it.createdAt }
                trySend(notifications)
                if (notifications != null) {
                    val entity = notifications.toEntity()
                    CoroutineScope(Dispatchers.IO).launch {
                        notificationsDao.insertDao(entity)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        val dbFlow = notificationsDao.getNotifications(uid)
        val job = CoroutineScope(Dispatchers.IO).launch {
            dbFlow.collect { entity ->
                val notifications = entity?.toNotifications()
                trySend(notifications).isSuccess
            }
        }
        awaitClose {
            ref.removeEventListener(listener)
            job.cancel()
        }
    }

}