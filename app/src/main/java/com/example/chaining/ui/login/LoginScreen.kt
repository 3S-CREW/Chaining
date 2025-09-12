package com.example.chaining.ui.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.BuildConfig
import com.example.chaining.R
import com.example.chaining.domain.model.User
import com.example.chaining.ui.screen.generateRandomNickname
import com.example.chaining.ui.screen.validateNickname
import com.example.chaining.viewmodel.UserViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // 구글 로그인
    onAdminLoginClick: () -> Unit, // 관리자 로그인
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val signInClient = Identity.getSignInClient(context)
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        isLoading = false
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val credential = signInClient.getSignInCredentialFromIntent(intent)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    isLoading = true
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    Firebase.auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                val firebaseUser = Firebase.auth.currentUser
                                if (firebaseUser != null) {
                                    val uid = firebaseUser.uid

                                    userViewModel.checkUserExists(uid) { exists ->
                                        if (!exists) {
                                            val googleNickname = firebaseUser.displayName ?: ""

                                            val isGoogleNicknameValid =
                                                validateNickname(googleNickname) == null

                                            val finalNickname = if (isGoogleNicknameValid) {
                                                googleNickname
                                            } else {
                                                generateRandomNickname()
                                            }

                                            // 3. 최종 결정된 닉네임으로 사용자 추가
                                            userViewModel.addUser(
                                                User(
                                                    id = uid,
                                                    nickname = finalNickname
                                                )
                                            )
                                        }
                                        onLoginSuccess()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "ID 토큰 없음", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "로그인 데이터 없음", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "로그인 취소", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        // 1) 체인 이미지
        Image(
            painter = painterResource(id = R.drawable.chain),
            contentDescription = "Chain",
            modifier = Modifier
                .size(90.dp) // 필요하면 조절
                .padding(bottom = 18.dp)
        )

        // 2) 체이닝 텍스트
        Text(
            text = "Chaining",
            fontSize = 50.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 150.dp)
        )

        // 3) 구글 로그인 버튼
        Button(
            onClick = {
                isLoading = true
                val signInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(BuildConfig.GOOGLE_API_WEB_CLIENT_ID)
                            .setFilterByAuthorizedAccounts(false) // 모든 계정 노출
                            .build()
                    )
                    .setAutoSelectEnabled(false) // 자동 선택 비활성화 → 계정 선택 보장
                    .build()

                signInClient.beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent).build()
                        launcher.launch(intentSenderRequest)
                    }
                    .addOnFailureListener {
                        isLoading = false
                        Toast.makeText(context, "로그인 요청 실패", Toast.LENGTH_SHORT).show()
                    }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.Black
            ),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .border(0.5.dp, Color.Gray, RoundedCornerShape(12.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                runCatching {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = if (isLoading) "로그인 중..." else "Google 계정으로 로그인",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp)
                )
            }
        }


        Button(
            onClick = onAdminLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4285F4),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "관리자 로그인",
                fontSize = 18.sp
            )
        }
    }
}

