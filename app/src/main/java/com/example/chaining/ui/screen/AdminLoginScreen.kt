package com.example.chaining.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaining.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Suppress("FunctionName")
@Composable
fun AdminLoginScreen(
    onBackClick: () -> Unit = {},
    // 로그인 성공 시 호출될 콜백 함수 추가
    onAdminLoginSuccess: () -> Unit,
) {
    // 아이디와 비밀번호 입력을 기억하기 위한 상태 변수
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 로딩 상태를 관리하기 위한 상태 변수
    var isLoading by remember { mutableStateOf(false) }

    // Toast 메시지를 위한 Context
    val context = LocalContext.current

    Scaffold(
        //  topBar에 로그인 제목을 넣습니다.
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black,
                    )
                }
                Text(
                    text = "관리자 로그인",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier =
                    Modifier
                        // 가로 꽉 채움
                        .fillMaxWidth()
                        // 좌우 패딩은 유지
                        .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chain),
                    contentDescription = "Chain",
                    modifier =
                        Modifier
                            // 필요하면 조절
                            .size(90.dp)
                            .padding(bottom = 18.dp),
                )

                Text(
                    text = "Chaining",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )
            }
            // 로고와 입력창 사이 여백
            Spacer(modifier = Modifier.height(48.dp))
            Column(
                modifier =
                    Modifier
                        // 가로 꽉 채움
                        .fillMaxWidth()
                        // 좌우 패딩은 유지
                        .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 아이디 입력창
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("아이디") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    // 로딩 중에는 입력 비활성화
                    enabled = !isLoading,
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F2F5),
                            unfocusedContainerColor = Color(0xFFF0F2F5),
                            // 포커스 시 밑줄 제거
                            focusedIndicatorColor = Color.Transparent,
                            // 포커스 없을 때 밑줄 제거
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 입력창
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("비밀번호") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    // 입력된 글자를 '*'로 보이게 함
                    visualTransformation = PasswordVisualTransformation(),
                    // 키보드 타입을 비밀번호용으로 설정
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    // 로딩 중에는 입력 비활성화
                    enabled = !isLoading,
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F2F5),
                            unfocusedContainerColor = Color(0xFFF0F2F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                )
            }
            // 입력창과 버튼 사이 여백
            Spacer(modifier = Modifier.height(80.dp))
            Column(
                modifier =
                    Modifier
                        // 가로 꽉 채움
                        .fillMaxWidth()
                        // 좌우 패딩은 유지
                        .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 4. 로그인 버튼
                Button(
                    onClick = {
                        // 입력값 검증
                        if (id.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT)
                                .show()
                            return@Button
                        }
                        // 로딩 시작
                        isLoading = true

                        Firebase.auth.signInWithEmailAndPassword(id.trim(), password)
                            .addOnCompleteListener { task ->
                                // 로딩 종료
                                isLoading = false
                                if (task.isSuccessful) {
                                    // 로그인 성공
                                    Toast.makeText(context, "관리자 로그인 성공", Toast.LENGTH_SHORT).show()
                                    // 성공 콜백 호출
                                    onAdminLoginSuccess()
                                } else {
                                    // 로그인 실패
                                    Toast.makeText(
                                        context,
                                        "로그인 실패: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                    // 로딩 중에는 버튼 비활성화
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4285F4),
                            // 비활성화 시 색상
                            disabledContainerColor = Color.Gray,
                        ),
                ) {
                    // 로딩 상태에 따라 텍스트 변경
                    Text(
                        text = if (isLoading) "로그인 중..." else "관리자 로그인",
                        fontSize = 18.sp,
                    )
                }
            }
        }
    }
}
