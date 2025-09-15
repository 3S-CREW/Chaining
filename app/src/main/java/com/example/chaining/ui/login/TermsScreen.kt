package com.example.chaining.ui.login

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.chaining.R
import com.example.chaining.domain.model.User
import com.example.chaining.viewmodel.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

// Custom Colors
private val PrimaryBlue = Color(0xFF3387E5)
private val LightGrayBackground = Color(0xFFF3F6FF)
private val BorderColor = Color(0xFFE0E0E0)

@Composable
fun TermsScreen(
    uid: String,
    nickname: String,
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var allChecked by remember { mutableStateOf(false) }
    var termsOfServiceChecked by remember { mutableStateOf(false) }
    var privacyPolicyChecked by remember { mutableStateOf(false) }

    LaunchedEffect(termsOfServiceChecked, privacyPolicyChecked) {
        if (allChecked != (termsOfServiceChecked && privacyPolicyChecked)) {
            allChecked = termsOfServiceChecked && privacyPolicyChecked
        }
    }

    TermsScreenLifecycleHandler(onCancel = onCancel)

    // 뒤로가기 버튼 처리 (비동의)
    BackHandler {
        val user = Firebase.auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    context,
                    context.getString(R.string.terms_agreement_required),
                    Toast.LENGTH_SHORT
                ).show()
                onCancel()
            } else {
                Firebase.auth.signOut()
                Toast.makeText(
                    context,
                    context.getString(R.string.fail_to_sign_up),
                    Toast.LENGTH_SHORT
                ).show()
                onCancel()
            }
        } ?: onCancel()
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    userViewModel.addUser(User(id = uid, nickname = nickname))
                    onSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                enabled = termsOfServiceChecked && privacyPolicyChecked,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text(stringResource(id = R.string.agree_and_start), fontSize = 18.sp)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.terms_of_service_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = allChecked,
                    onCheckedChange = { isChecked ->
                        allChecked = isChecked
                        termsOfServiceChecked = isChecked
                        privacyPolicyChecked = isChecked
                    },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                )
                Text(
                    text = stringResource(id = R.string.agree_all),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Divider()

            CheckboxWithDetails(
                checked = termsOfServiceChecked,
                onCheckedChange = { termsOfServiceChecked = it },
                title = stringResource(id = R.string.agree_terms_of_service_required),
                content = stringResource(id = R.string.terms_of_service_content)
            )

            CheckboxWithDetails(
                checked = privacyPolicyChecked,
                onCheckedChange = { privacyPolicyChecked = it },
                title = stringResource(id = R.string.agree_privacy_policy_required),
                content = stringResource(id = R.string.privacy_policy_content)
            )
        }
    }
}

@Composable
private fun CheckboxWithDetails(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
            )
            Text(title, modifier = Modifier.weight(1f))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(LightGrayBackground, RoundedCornerShape(8.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun TermsScreenLifecycleHandler(onCancel: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                val user = Firebase.auth.currentUser
                user?.delete()?.addOnCompleteListener {
                    onCancel()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}