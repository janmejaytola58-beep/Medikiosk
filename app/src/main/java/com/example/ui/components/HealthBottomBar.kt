package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HealthCardBorderSubtle
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.viewmodel.KioskScreen

enum class HealthBottomTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Outlined.Home, "bottom_nav_home"),
    RECORDS("History", Icons.Outlined.History, "bottom_nav_records"),
    DOCUMENTS("Documents", Icons.Outlined.Description, "bottom_nav_documents"),
    STAFF("Staff", Icons.Outlined.LocalHospital, "bottom_nav_staff"),
    PROFILE("Profile", Icons.Outlined.Person, "bottom_nav_profile")
}

@Composable
fun HealthBottomBar(
    currentScreen: KioskScreen,
    onTabSelected: (HealthBottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("health_persistent_bottom_bar"),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, HealthCardBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HealthBottomTab.entries.forEach { tab ->
                val isSelected = when (tab) {
                    HealthBottomTab.HOME -> currentScreen == KioskScreen.WELCOME
                    HealthBottomTab.RECORDS -> currentScreen == KioskScreen.MY_RECORDS
                    HealthBottomTab.DOCUMENTS -> currentScreen == KioskScreen.DOCUMENT_SCAN
                    HealthBottomTab.STAFF -> currentScreen == KioskScreen.STAFF_VIEW
                    HealthBottomTab.PROFILE -> currentScreen == KioskScreen.PATIENT_PROFILE || currentScreen == KioskScreen.PATIENT_IDENTIFY
                }

                val pillBgColor by animateColorAsState(
                    targetValue = if (isSelected) HealthNavy else Color.Transparent,
                    animationSpec = tween(200),
                    label = "pill_bg"
                )
                val pillContentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else HealthTextSecondary,
                    animationSpec = tween(200),
                    label = "pill_content"
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onTabSelected(tab) }
                        .testTag(tab.testTag),
                    color = pillBgColor,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = if (isSelected) 10.dp else 6.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = pillContentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = pillContentColor
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
