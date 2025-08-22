//package com.example.chaining.data.repository
//
//import android.net.Uri
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage
//import kotlinx.coroutines.tasks.await
//
//class ProfileRepository {
//
//    private val storage = Firebase.storage
//    private val database = Firebase.database.reference
//    private val userId get() = Firebase.auth.currentUser?.uid ?: ""
//
//    // 1. 이미지 업로드
//    suspend fun uploadProfileImage(imageUri: Uri): String? {
//        return try {
//            val ref = storage.reference.child("profileImages/$userId.jpg")
//            ref.putFile(imageUri).await()
//            ref.downloadUrl.await().toString()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    // 2. 다운로드 URL DB 저장
//    suspend fun saveProfileImageUrl(url: String) {
//        database.child("users").child(userId).child("profileImageUrl").setValue(url).await()
//    }
//
//    // 3. DB에서 URL 가져오기
//    suspend fun getProfileImageUrl(): String? {
//        return try {
//            val snapshot =
//                database.child("users").child(userId).child("profileImageUrl").get().await()
//            snapshot.getValue(String::class.java)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//}
