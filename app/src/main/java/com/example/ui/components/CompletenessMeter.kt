package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClinicalSummary
import com.example.data.model.ExtractedDocumentData
import com.example.data.model.SocratesData
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.SuccessGreen

data class CompletenessItem(
    val title: String,
    val isComplete: Boolean,
    val description: String
)

data class CompletenessScore(
    val percentage: Int,
    val items: List<CompletenessItem>,
    val missingCount: Int
)

object CompletenessCalculator {
    fun calculate(
        socratesData: SocratesData,
        extractedData: ExtractedDocumentData,
        clinicalSummary: ClinicalSummary?
    ): CompletenessScore {
        val hasChiefComplaint = socratesData.site.isNotBlank() || !clinicalSummary?.chiefComplaint.isNullOrBlank()
        val hasOnset = socratesData.onset.isNotBlank() || socratesData.timing.isNotBlank()
        val hasSeverity = socratesData.severity.isNotBlank() || socratesData.character.isNotBlank()
        val hasMedications = extractedData.medications.isNotEmpty() || !clinicalSummary?.drugAndAllergy.isNullOrBlank()
        val hasAllergies = !clinicalSummary?.drugAndAllergy.isNullOrBlank() && (clinicalSummary?.drugAndAllergy?.contains("allerg", ignoreCase = true) == true || clinicalSummary?.drugAndAllergy?.contains("none", ignoreCase = true) == true)
        val hasFamilyHistory = !clinicalSummary?.familyHistory.isNullOrBlank() && !clinicalSummary?.familyHistory.equals("Non-contributory", ignoreCase = true)
        val hasLabDocs = extractedData.labValues.isNotEmpty() || !clinicalSummary?.priorInvestigations.isNullOrBlank()

        val items = listOf(
            CompletenessItem("Chief Complaint", hasChiefComplaint, if (hasChiefComplaint) "Documented" else "Needs detail"),
            CompletenessItem("Onset & Timing", hasOnset, if (hasOnset) "Documented" else "Duration pending"),
            CompletenessItem("Severity & Character", hasSeverity, if (hasSeverity) "Recorded" else "Pain scale pending"),
            CompletenessItem("Medication History", hasMedications, if (hasMedications) "Recorded" else "Prescription or meds"),
            CompletenessItem("Allergy History", hasAllergies, if (hasAllergies) "Checked" else "Incomplete"),
            CompletenessItem("Family & Medical History", hasFamilyHistory, if (hasFamilyHistory) "Recorded" else "Optional/Incomplete")
        )

        val completeCount = items.count { it.isComplete }
        val percentage = ((completeCount.toFloat() / items.size) * 100).toInt().coerceIn(30, 100)
        val missingCount = items.size - completeCount

        return CompletenessScore(
            percentage = percentage,
            items = items,
            missingCount = missingCount
        )
    }
}

@Composable
fun CompletenessMeter(
    score: CompletenessScore,
    modifier: Modifier = Modifier,
    onCompleteMissingClick: (() -> Unit)? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score.percentage / 100f,
        animationSpec = tween(600),
        label = "completeness_progress"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_completeness_meter"),
        shape = RoundedCornerShape(20.dp),
        color = HealthCardBg,
        border = BorderStroke(1.dp, HealthCardBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (score.percentage >= 80) SuccessGreen.copy(alpha = 0.12f) else AlertOrange.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (score.percentage >= 80) "✓" else "ℹ",
                            fontWeight = FontWeight.Bold,
                            color = if (score.percentage >= 80) SuccessGreen else AlertOrange,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "History Completeness",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavyHeader,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = if (score.missingCount == 0) "Comprehensive clinical record" else "${score.missingCount} sections pending review",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary, fontSize = 11.sp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (score.percentage >= 80) SuccessGreen.copy(alpha = 0.12f) else AlertOrange.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${score.percentage}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (score.percentage >= 80) SuccessGreen else AlertOrange,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (score.percentage >= 80) SuccessGreen else HealthBlueAccent,
                trackColor = HealthBackground,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                score.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (item.isComplete) Icons.Default.Check else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (item.isComplete) SuccessGreen else AlertOrange,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (item.isComplete) FontWeight.Normal else FontWeight.SemiBold,
                                    color = if (item.isComplete) HealthTextPrimary else HealthNavy,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (item.isComplete) SuccessGreen else AlertOrange,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Text(
                text = "⚡ Guides clinicians during consult — not a medical diagnosis.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = HealthTextSecondary,
                    fontSize = 10.sp
                ),
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
