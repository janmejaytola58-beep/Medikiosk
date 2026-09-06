package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextMuted
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelMintIcon
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachIcon

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val isRead: Boolean = false
)

@Composable
fun NotificationCenterDialog(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onClearAll: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("notification_center_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = HealthCardBg,
        tonalElevation = 6.dp,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(HealthBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = HealthNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "${notifications.size} updates",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = HealthTextSecondary
                    )
                }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No notifications yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavy
                            )
                        )
                        Text(
                            text = "Queue updates and room calls will appear here",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (notif.isRead) HealthBackground else HealthBlueSoft.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, HealthCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(notif.iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = notif.icon,
                                        contentDescription = null,
                                        tint = notif.iconTint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = notif.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = HealthNavy,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = notif.timestamp,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = HealthTextMuted,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = HealthTextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 15.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                modifier = Modifier.testTag("close_notifications_button")
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
