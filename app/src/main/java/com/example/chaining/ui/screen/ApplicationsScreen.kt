package com.example.chaining.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.domain.model.Application
import com.example.chaining.ui.component.CardItem
import com.example.chaining.ui.component.formatRemainingTime
import com.example.chaining.viewmodel.ApplicationViewModel
import com.example.chaining.viewmodel.RecruitPostViewModel
import com.example.chaining.viewmodel.UserViewModel

@Suppress("FunctionName")
@Composable
fun ApplicationsScreen(
    onBackClick: () -> Unit = {},
    userViewModel: UserViewModel = hiltViewModel(),
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    applicationViewModel: ApplicationViewModel = hiltViewModel(),
    postId: String?,
    // "My" or "Owner"
    type: String,
    onViewApplyClick: (applicationId: String) -> Unit,
) {
    val userState by userViewModel.user.collectAsState()
    val myApplications = userState?.applications.orEmpty()
    val post by postViewModel.post.collectAsState()
    val context = LocalContext.current

    val ownerApplications: Map<String, Application> =
        if (type == "Owner") {
            post?.applications ?: emptyMap()
        } else {
            emptyMap()
        }

    var showOnlyFinishedApplications by remember { mutableStateOf(false) }

    val applications: List<Application> =
        if (type == "Owner") {
            ownerApplications.values.toList()
        } else {
            myApplications.values.toList()
        }

    val filteredApplications: List<Application> =
        if (showOnlyFinishedApplications) {
            applications.filter { application -> application.status != "PENDING" }
        } else {
            applications
        }

    Scaffold(
        topBar = {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                }

                // 제목
                Text(
                    text =
                    if (type == "Owner") {
                        stringResource(id = R.string.post_application)
                    } else {
                        stringResource(
                            id = R.string.myapply_title,
                        )
                    },
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        containerColor = Color(0xFFF3F6FF),
    ) { innerPadding ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // 새로 만든 CommunityActionButton 호출
                ActionButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.post,
                    text =
                    if (showOnlyFinishedApplications) {
                        stringResource(id = R.string.myapply_all_post)
                    } else {
                        stringResource(
                            id = R.string.myapply_filter_open,
                        )
                    },
                    onClick = {
                        showOnlyFinishedApplications = !showOnlyFinishedApplications
                    },
                )
            }
            if (filteredApplications.isEmpty()) {
                // 데이터가 없을 때
                Text(
                    text = stringResource(id = R.string.myapply_nothing),
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            } else {
                // 모집글 목록 표시
                filteredApplications.forEach { application ->
                    val hasStatus =
                        application.status != "PENDING"
                    CardItem(
                        hasStatus = hasStatus,
                        remainingTime = formatRemainingTime(
                            context,
                            application.closeAt.minus(System.currentTimeMillis())
                        ),
                        onClick = {
                            onViewApplyClick(application.applicationId)
                        },
                        type = "지원서",
                        currentUserId = userState?.id,
                        application = application,
                        onLeftButtonClick = {
                            application.let { apply ->
                                applicationViewModel.updateStatus(
                                    application = apply,
                                    value = "APPROVED",
                                )
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_approved),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onRightButtonClick = {
                            application.let { apply ->
                                applicationViewModel.updateStatus(
                                    application = apply,
                                    value = "REJECTED",
                                )
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_rejected),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    )
                }
            }
        }
    }
}
