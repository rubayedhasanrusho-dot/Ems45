package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleContainer
import com.example.ui.theme.TextPrimary

@Composable
fun EduRankTopBar(
    currentUser: UserEntity?,
    onOpenAdmin: () -> Unit,
    onSwitchUser: (Long) -> Unit,
    onLogout: () -> Unit
) {
    var showUserMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .testTag("top_bar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding Logo Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EduRankPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "EduRank Logo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "EduRank",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EduRankPurple
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EduRankPurpleContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (currentUser?.role == "ADMIN") "ADMIN" else "BD EXAM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EduRankPurple
                        )
                    }
                }
                Text(
                    text = currentUser?.institution ?: "Bangladesh National Education",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            // Admin Portal Shortcut Button
            IconButton(
                onClick = onOpenAdmin,
                modifier = Modifier.testTag("admin_portal_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Panel",
                    tint = if (currentUser?.role == "ADMIN") EduRankAccent else EduRankPurple
                )
            }

            // Notification Bell
            BadgedBox(
                badge = {
                    Badge(containerColor = EduRankAccent) {
                        Text("2", color = Color.Black, fontSize = 9.sp)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // User Avatar & Role Switch Menu
            Box {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EduRankPurpleContainer)
                        .clickable { showUserMenu = true }
                        .testTag("user_avatar_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (currentUser?.fullName?.take(1) ?: "T").uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = EduRankPurple,
                        fontSize = 14.sp
                    )
                }

                DropdownMenu(
                    expanded = showUserMenu,
                    onDismissRequest = { showUserMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = currentUser?.fullName ?: "User",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${currentUser?.educationLevel} • ${currentUser?.role}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        },
                        onClick = { showUserMenu = false }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = EduRankPurple)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Switch to Tanvir (Student - HSC)")
                            }
                        },
                        onClick = {
                            showUserMenu = false
                            onSwitchUser(1L)
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = EduRankPurple)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Switch to Prof. Rahman (Admin)")
                            }
                        },
                        onClick = {
                            showUserMenu = false
                            onSwitchUser(99L)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showUserMenu = false
                            onLogout()
                        }
                    )
                }
            }
        }
    }
}
