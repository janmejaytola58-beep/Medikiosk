package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.FirestorePatient
import com.example.data.firebase.SyncStatus
import com.example.data.model.AssistanceRequestItem
import com.example.data.model.DoctorProfile
import com.example.data.model.StaffRole
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertRed
import com.example.ui.theme.HealthBackground
import com.example.ui.theme.HealthBlueAccent
import com.example.ui.theme.HealthBlueSoft
import com.example.ui.theme.HealthCardBg
import com.example.ui.theme.HealthCardBorder
import com.example.ui.theme.HealthCardBorderSubtle
import com.example.ui.theme.HealthNavy
import com.example.ui.theme.HealthNavyHeader
import com.example.ui.theme.HealthTextMuted
import com.example.ui.theme.HealthTextPrimary
import com.example.ui.theme.HealthTextSecondary
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderIcon
import com.example.ui.theme.PastelLightBlue
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.RedFlagBg
import com.example.ui.theme.RedFlagBorder
import com.example.ui.theme.RedFlagText
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StaffViewScreen(
    patients: List<FirestorePatient>,
    syncStatus: SyncStatus = SyncStatus.SYNCED,
    assistanceRequests: List<AssistanceRequestItem> = emptyList(),
    currentRole: StaffRole = StaffRole.DOCTOR,
    selectedDoctor: DoctorProfile = DoctorProfile.DOCTORS.first(),
    onSelectRole: (StaffRole) -> Unit = {},
    onSelectDoctor: (DoctorProfile) -> Unit = {},
    onUpdateStatus: (patientId: String, newStatus: String) -> Unit = { _, _ -> },
    onAssignDoctor: (patientId: String, doctorName: String) -> Unit = { _, _ -> },
    onUpdatePriority: (patientId: String, priority: String) -> Unit = { _, _ -> },
    onSaveClinicianNotes: (patientId: String, notes: String, assessment: String, plan: String, doctor: String) -> Unit = { _, _, _, _, _ -> },
    onRecordVitals: (patientId: String, sys: String, dia: String, hr: String, spo2: String, temp: String, rr: String, wt: String, nurse: String) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onAcknowledgeAssistance: (String) -> Unit = {},
    onResolveAssistance: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var activeTab by remember { mutableStateOf(if (currentRole == StaffRole.DOCTOR) 0 else 1) }
    var selectedDoctorState by remember { mutableStateOf(selectedDoctor) }
    var selectedPatientForDetail by remember { mutableStateOf<FirestorePatient?>(null) }

    // Search and Filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedQueueFilter by remember { mutableStateOf("ALL") } // ALL, WAITING, URGENT, IN_CONSULTATION, COMPLETED
    var selectedSortBy by remember { mutableStateOf("QUEUE_NUMBER") } // QUEUE_NUMBER, WAITING_TIME, PRIORITY, ARRIVAL_TIME
    var showDoctorDropdown by remember { mutableStateOf(false) }

    val pendingAssistanceCount = assistanceRequests.count { it.status == "PENDING" }

    // If detail view is open, render detailed clinical view
    if (selectedPatientForDetail != null) {
        val currentDetailPatient = patients.find {
            it.id == selectedPatientForDetail!!.id || it.tokenNumber == selectedPatientForDetail!!.tokenNumber
        } ?: selectedPatientForDetail!!

        PatientClinicalDetailView(
            patient = currentDetailPatient,
            currentDoctor = selectedDoctorState,
            onClose = { selectedPatientForDetail = null },
            onCallPatient = { id ->
                onUpdateStatus(id, "CALLED")
            },
            onStartConsultation = { id, docName ->
                onUpdateStatus(id, "IN_CONSULTATION")
                onAssignDoctor(id, docName)
            },
            onCompleteVisit = { id, docName ->
                onUpdateStatus(id, "COMPLETED")
            },
            onAssignDoctor = { id, docName ->
                onAssignDoctor(id, docName)
            },
            onUpdatePriority = { id, p ->
                onUpdatePriority(id, p)
            },
            onSaveClinicianNotes = onSaveClinicianNotes,
            onRecordVitals = onRecordVitals
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HealthBackground)
            .testTag("staff_view_screen")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = HealthNavy,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Kiosk",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "City Care Super Specialty Hospital",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date()),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Firestore Live Connection Status Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (syncStatus) {
                            SyncStatus.SYNCED -> Color(0xFF10B981).copy(alpha = 0.25f)
                            SyncStatus.SYNCING -> Color(0xFF3B82F6).copy(alpha = 0.25f)
                            SyncStatus.OFFLINE -> Color(0xFFF59E0B).copy(alpha = 0.25f)
                        },
                        border = BorderStroke(
                            1.dp,
                            when (syncStatus) {
                                SyncStatus.SYNCED -> Color(0xFF10B981).copy(alpha = 0.6f)
                                SyncStatus.SYNCING -> Color(0xFF3B82F6).copy(alpha = 0.6f)
                                SyncStatus.OFFLINE -> Color(0xFFF59E0B).copy(alpha = 0.6f)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when (syncStatus) {
                                SyncStatus.SYNCED -> {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Firestore Live",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                SyncStatus.SYNCING -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(10.dp),
                                        strokeWidth = 1.5.dp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Syncing...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                SyncStatus.OFFLINE -> {
                                    Icon(
                                        imageVector = Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = Color(0xFFFCD34D),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Offline Mode",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFFCD34D),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Doctor selector dropdown when in doctor mode
                if (activeTab == 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDoctorDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Active Clinician: ${selectedDoctorState.name} (${selectedDoctorState.specialty} • ${selectedDoctorState.roomNumber})",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Text(
                                text = "Switch ▾",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = showDoctorDropdown,
                            onDismissRequest = { showDoctorDropdown = false }
                        ) {
                            DoctorProfile.DOCTORS.forEach { doc ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(doc.name, fontWeight = FontWeight.Bold)
                                            Text("${doc.specialty} • ${doc.roomNumber}", fontSize = 11.sp, color = HealthTextSecondary)
                                        }
                                    },
                                    onClick = {
                                        selectedDoctorState = doc
                                        onSelectDoctor(doc)
                                        showDoctorDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Operational Navigation Tabs
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = HealthNavy,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = HealthNavy,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0; onSelectRole(StaffRole.DOCTOR) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Doctor", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1; onSelectRole(StaffRole.RECEPTION) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Queue & Triage", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Help, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Assistance", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        if (pendingAssistanceCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = CircleShape,
                                color = AlertRed
                            ) {
                                Text(
                                    text = "$pendingAssistanceCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeviceHub, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kiosks", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
            Tab(
                selected = activeTab == 4,
                onClick = { activeTab = 4 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
        }

        // Tab Content
        when (activeTab) {
            0 -> DoctorConsoleTab(
                patients = patients,
                currentDoctor = selectedDoctorState,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onSelectPatient = { selectedPatientForDetail = it },
                onCallPatient = { id -> onUpdateStatus(id, "CALLED") },
                onStartConsult = { id -> onUpdateStatus(id, "IN_CONSULTATION") },
                onCompleteVisit = { id -> onUpdateStatus(id, "COMPLETED") }
            )
            1 -> StaffQueueTriageTab(
                patients = patients,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedFilter = selectedQueueFilter,
                onFilterChange = { selectedQueueFilter = it },
                selectedSortBy = selectedSortBy,
                onSortByChange = { selectedSortBy = it },
                onSelectPatient = { selectedPatientForDetail = it },
                onUpdateStatus = onUpdateStatus,
                onAssignDoctor = onAssignDoctor,
                onUpdatePriority = onUpdatePriority
            )
            2 -> AssistanceConsoleTab(
                requests = assistanceRequests,
                onAcknowledge = onAcknowledgeAssistance,
                onResolve = onResolveAssistance
            )
            3 -> DepartmentOverviewTab(patients = patients)
            4 -> StaffProfileTab(
                currentDoctor = selectedDoctorState,
                onSwitchDoctor = { doc ->
                    selectedDoctorState = doc
                    onSelectDoctor(doc)
                }
            )
        }
    }
}

// ---------------------------------------------------------
// 1. DOCTOR CONSOLE TAB
// ---------------------------------------------------------
@Composable
private fun DoctorConsoleTab(
    patients: List<FirestorePatient>,
    currentDoctor: DoctorProfile,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectPatient: (FirestorePatient) -> Unit,
    onCallPatient: (String) -> Unit,
    onStartConsult: (String) -> Unit,
    onCompleteVisit: (String) -> Unit
) {
    val doctorPatients = remember(patients, currentDoctor, searchQuery) {
        val assigned = patients.filter {
            it.assignedDoctor.contains(currentDoctor.name, ignoreCase = true) || it.assignedDoctor.isBlank()
        }
        if (searchQuery.isBlank()) assigned
        else assigned.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.tokenNumber.contains(searchQuery, ignoreCase = true) ||
            it.chiefComplaint.contains(searchQuery, ignoreCase = true)
        }
    }

    val waitingCount = doctorPatients.count { it.normalizedStatus == "WAITING" || it.normalizedStatus == "CALLED" }
    val inConsultCount = doctorPatients.count { it.normalizedStatus == "IN_CONSULTATION" }
    val completedCount = doctorPatients.count { it.normalizedStatus == "COMPLETED" || it.normalizedStatus == "SEEN" }
    val urgentCount = doctorPatients.count { it.normalizedPriority == "Urgent" && it.normalizedStatus != "COMPLETED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Doctor Welcome & Overview Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Today's Clinical Overview — ${currentDoctor.name}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )
                    Text(
                        text = "Specialty: ${currentDoctor.specialty} • ${currentDoctor.roomNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox(title = "Waiting", count = "$waitingCount", sub = "In OPD Queue", col = HealthNavy, modifier = Modifier.weight(1f))
                        MetricBox(title = "Urgent", count = "$urgentCount", sub = "Priority 1", col = AlertRed, modifier = Modifier.weight(1f))
                        MetricBox(title = "In Room", count = "$inConsultCount", sub = "Active Consult", col = HealthBlueAccent, modifier = Modifier.weight(1f))
                        MetricBox(title = "Completed", count = "$completedCount", sub = "Done Today", col = SuccessGreen, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by name, token #, complaint...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = HealthTextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HealthNavy,
                    unfocusedBorderColor = HealthCardBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        item {
            Text(
                text = "Assigned Patients Queue (${doctorPatients.size})",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = HealthNavyHeader
                )
            )
        }

        if (doctorPatients.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HealthCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No patients waiting in queue",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                        )
                        Text(
                            text = "New kiosk check-ins will sync automatically in real-time.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                        )
                    }
                }
            }
        } else {
            items(doctorPatients, key = { it.id.ifBlank { it.tokenNumber } }) { patient ->
                DoctorPatientCard(
                    patient = patient,
                    onClick = { onSelectPatient(patient) },
                    onCall = { onCallPatient(patient.id.ifBlank { patient.tokenNumber }) },
                    onStartConsult = { onStartConsult(patient.id.ifBlank { patient.tokenNumber }) },
                    onComplete = { onCompleteVisit(patient.id.ifBlank { patient.tokenNumber }) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------
// 2. STAFF QUEUE & TRIAGE TAB
// ---------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StaffQueueTriageTab(
    patients: List<FirestorePatient>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    selectedSortBy: String,
    onSortByChange: (String) -> Unit,
    onSelectPatient: (FirestorePatient) -> Unit,
    onUpdateStatus: (String, String) -> Unit,
    onAssignDoctor: (String, String) -> Unit,
    onUpdatePriority: (String, String) -> Unit
) {
    val context = LocalContext.current
    var showSortMenu by remember { mutableStateOf(false) }

    val filteredList = remember(patients, searchQuery, selectedFilter, selectedSortBy) {
        var list = when (selectedFilter) {
            "WAITING" -> patients.filter { it.normalizedStatus == "WAITING" || it.normalizedStatus == "CALLED" }
            "URGENT" -> patients.filter { it.normalizedPriority == "Urgent" }
            "IN_CONSULTATION" -> patients.filter { it.normalizedStatus == "IN_CONSULTATION" }
            "COMPLETED" -> patients.filter { it.normalizedStatus == "COMPLETED" || it.normalizedStatus == "SEEN" }
            else -> patients
        }

        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.tokenNumber.contains(searchQuery, ignoreCase = true) ||
                it.abhaId.contains(searchQuery, ignoreCase = true) ||
                it.chiefComplaint.contains(searchQuery, ignoreCase = true)
            }
        }

        when (selectedSortBy) {
            "WAITING_TIME" -> list.sortedByDescending { it.calculatedWaitMinutes }
            "PRIORITY" -> list.sortedByDescending { if (it.normalizedPriority == "Urgent") 2 else if (it.normalizedPriority == "Priority") 1 else 0 }
            "ARRIVAL_TIME" -> list.sortedBy { it.createdAt }
            else -> list.sortedBy { it.tokenNumber }
        }
    }

    val waitingCount = patients.count { it.normalizedStatus == "WAITING" || it.normalizedStatus == "CALLED" }
    val urgentCount = patients.count { it.normalizedPriority == "Urgent" }
    val consultCount = patients.count { it.normalizedStatus == "IN_CONSULTATION" }
    val completedCount = patients.count { it.normalizedStatus == "COMPLETED" || it.normalizedStatus == "SEEN" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Search and Sort controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search patient, token, ABHA...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = HealthTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthNavy,
                        unfocusedBorderColor = HealthCardBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, HealthCardBorder),
                        modifier = Modifier.clickable { showSortMenu = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort", tint = HealthNavy, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sort", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = HealthNavy)
                        }
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Token Number") },
                            onClick = { onSortByChange("QUEUE_NUMBER"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Longest Waiting Time") },
                            onClick = { onSortByChange("WAITING_TIME"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Urgency / Priority") },
                            onClick = { onSortByChange("PRIORITY"); showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Check-in Time") },
                            onClick = { onSortByChange("ARRIVAL_TIME"); showSortMenu = false }
                        )
                    }
                }
            }
        }

        // Filter Pills
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterTabChip("All (${patients.size})", selectedFilter == "ALL") { onFilterChange("ALL") }
                FilterTabChip("⏳ Waiting ($waitingCount)", selectedFilter == "WAITING") { onFilterChange("WAITING") }
                FilterTabChip("🚨 Urgent ($urgentCount)", selectedFilter == "URGENT") { onFilterChange("URGENT") }
                FilterTabChip("🩺 Consulting ($consultCount)", selectedFilter == "IN_CONSULTATION") { onFilterChange("IN_CONSULTATION") }
                FilterTabChip("✅ Completed ($completedCount)", selectedFilter == "COMPLETED") { onFilterChange("COMPLETED") }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HealthCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No patients matching selected criteria", style = MaterialTheme.typography.bodyMedium.copy(color = HealthTextSecondary))
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id.ifBlank { it.tokenNumber } }) { patient ->
                StaffPatientCard(
                    patient = patient,
                    onClick = { onSelectPatient(patient) },
                    onCall = { onUpdateStatus(patient.id.ifBlank { patient.tokenNumber }, "CALLED") },
                    onStartConsult = { onUpdateStatus(patient.id.ifBlank { patient.tokenNumber }, "IN_CONSULTATION") },
                    onComplete = { onUpdateStatus(patient.id.ifBlank { patient.tokenNumber }, "COMPLETED") },
                    onReopen = { onUpdateStatus(patient.id.ifBlank { patient.tokenNumber }, "WAITING") }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------
// 3. ASSISTANCE CONSOLE TAB
// ---------------------------------------------------------
@Composable
private fun AssistanceConsoleTab(
    requests: List<AssistanceRequestItem>,
    onAcknowledge: (String) -> Unit,
    onResolve: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = PastelPeach,
                border = BorderStroke(1.dp, Color(0xFFFDBA74))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Help, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Kiosk Patient Assistance Console",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                        )
                        Text(
                            text = "Real-time alerts triggered by patients requesting on-site staff help at kiosk terminals.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        if (requests.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, HealthCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No pending assistance requests",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                        )
                        Text(
                            text = "All kiosk stations operating normally.",
                            style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                        )
                    }
                }
            }
        } else {
            items(requests, key = { it.id }) { req ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(
                        1.dp,
                        if (req.status == "PENDING") Color(0xFFFDBA74) else HealthCardBorder
                    ),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (req.status == "PENDING") AlertRed.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = req.status,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (req.status == "PENDING") AlertRed else SuccessGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = req.kioskId,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                                )
                            }
                            Text(
                                text = req.formattedTime,
                                style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary, fontSize = 11.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reason: ${req.reason}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = HealthNavyHeader)
                        )
                        if (req.patientName.isNotBlank() || req.tokenNumber.isNotBlank()) {
                            Text(
                                text = "Patient: ${req.patientName} (${req.tokenNumber})",
                                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = HealthCardBorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (req.status == "PENDING") {
                                Button(
                                    onClick = {
                                        onAcknowledge(req.id)
                                        Toast.makeText(context, "Acknowledged assistance request", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HealthNavy),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Acknowledge", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            if (req.status != "RESOLVED") {
                                Button(
                                    onClick = {
                                        onResolve(req.id)
                                        Toast.makeText(context, "Request resolved", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Resolve", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------
// 4. DEPARTMENT OVERVIEW TAB
// ---------------------------------------------------------
@Composable
private fun DepartmentOverviewTab(patients: List<FirestorePatient>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Department Operations & Kiosk Nodes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = HealthNavyHeader)
            )
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Live Kiosk Statuses", fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                    Spacer(modifier = Modifier.height(10.dp))
                    KioskNodeItem("Kiosk A", "Outpatient Wing B", "Online", 18, "4m avg")
                    KioskNodeItem("Kiosk B", "Ground Floor Triage", "Online", 24, "3m avg")
                    KioskNodeItem("Kiosk C", "Emergency Care Entrance", "Online", 9, "2m avg")
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Doctor Consultation Rooms", fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                    Spacer(modifier = Modifier.height(10.dp))
                    DoctorProfile.DOCTORS.forEach { doc ->
                        val count = patients.count { it.assignedDoctor.contains(doc.name) && (it.normalizedStatus == "WAITING" || it.normalizedStatus == "IN_CONSULTATION") }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(doc.name, fontWeight = FontWeight.Bold, color = HealthNavyHeader, fontSize = 13.sp)
                                Text("${doc.specialty} • ${doc.roomNumber}", fontSize = 11.sp, color = HealthTextSecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (count > 0) HealthBlueSoft else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = "$count Patients Queued",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (count > 0) HealthNavy else HealthTextSecondary,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = HealthCardBorderSubtle)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun KioskNodeItem(name: String, location: String, status: String, completedToday: Int, waitAvg: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(name, fontWeight = FontWeight.Bold, color = HealthNavyHeader, fontSize = 13.sp)
                Text(location, fontSize = 11.sp, color = HealthTextSecondary)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("$completedToday Intakes Today", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = HealthNavy)
            Text("Avg Intake: $waitAvg", fontSize = 10.sp, color = HealthTextSecondary)
        }
    }
    HorizontalDivider(color = HealthCardBorderSubtle)
}

// ---------------------------------------------------------
// 5. STAFF PROFILE TAB
// ---------------------------------------------------------
@Composable
private fun StaffProfileTab(
    currentDoctor: DoctorProfile,
    onSwitchDoctor: (DoctorProfile) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, HealthCardBorder),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = HealthNavy,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentDoctor.name.take(4),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentDoctor.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                            )
                            Text(
                                text = "${currentDoctor.specialty} • ${currentDoctor.roomNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextSecondary)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreen.copy(alpha = 0.15f),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "Active Duty — Morning Shift A",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SuccessGreen, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = HealthCardBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Switch Current Clinician Profile", fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                    Spacer(modifier = Modifier.height(8.dp))

                    DoctorProfile.DOCTORS.forEach { doc ->
                        val isSelected = doc.id == currentDoctor.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSwitchDoctor(doc) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) HealthBlueSoft else HealthBackground,
                            border = BorderStroke(1.dp, if (isSelected) HealthNavy else HealthCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(doc.name, fontWeight = FontWeight.Bold, color = HealthNavyHeader)
                                    Text("${doc.specialty} • ${doc.roomNumber}", fontSize = 11.sp, color = HealthTextSecondary)
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = HealthNavy)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------
// Helper Card Components
// ---------------------------------------------------------
@Composable
private fun DoctorPatientCard(
    patient: FirestorePatient,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onStartConsult: () -> Unit,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("doctor_patient_card_${patient.tokenNumber}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(
            if (patient.normalizedPriority == "Urgent") 1.5.dp else 1.dp,
            if (patient.normalizedPriority == "Urgent") AlertRed.copy(alpha = 0.5f) else HealthCardBorder
        ),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HealthBlueSoft
                    ) {
                        Text(
                            text = "#${patient.tokenNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavy
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${patient.name}, ${patient.age}y (${patient.gender.take(1)})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusPill(status = patient.normalizedStatus)
                    PriorityPill(priority = patient.normalizedPriority)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = patient.chiefComplaint,
                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextPrimary),
                maxLines = 2
            )

            if (patient.vitals.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = HealthBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = patient.vitals,
                        style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary, fontSize = 10.5.sp),
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = HealthCardBorderSubtle)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = HealthTextSecondary, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${patient.calculatedWaitMinutes}m wait",
                        style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary, fontSize = 11.sp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    when (patient.normalizedStatus) {
                        "WAITING" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthNavy,
                                modifier = Modifier.clickable(onClick = onCall)
                            ) {
                                Text(
                                    text = "Call Patient",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthBlueAccent,
                                modifier = Modifier.clickable(onClick = onStartConsult)
                            ) {
                                Text(
                                    text = "Start Consult",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        "CALLED" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthBlueAccent,
                                modifier = Modifier.clickable(onClick = onStartConsult)
                            ) {
                                Text(
                                    text = "Start Consult",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        "IN_CONSULTATION" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuccessGreen,
                                modifier = Modifier.clickable(onClick = onComplete)
                            ) {
                                Text(
                                    text = "Complete Visit",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Clinical View →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavy,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
private fun StaffPatientCard(
    patient: FirestorePatient,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onStartConsult: () -> Unit,
    onComplete: () -> Unit,
    onReopen: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("staff_patient_card_${patient.tokenNumber}"),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(
            if (patient.normalizedPriority == "Urgent") 1.5.dp else 1.dp,
            if (patient.normalizedPriority == "Urgent") AlertRed.copy(alpha = 0.5f) else HealthCardBorder
        ),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HealthBlueSoft
                    ) {
                        Text(
                            text = "#${patient.tokenNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = HealthNavy
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${patient.name}, ${patient.age}y",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavyHeader
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusPill(status = patient.normalizedStatus)
                    PriorityPill(priority = patient.normalizedPriority)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = patient.chiefComplaint,
                style = MaterialTheme.typography.bodySmall.copy(color = HealthTextPrimary),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Doctor: ${patient.assignedDoctor.ifBlank { "Unassigned" }}",
                    style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary, fontSize = 11.sp)
                )
                Text(
                    text = "${patient.calculatedWaitMinutes}m wait",
                    style = MaterialTheme.typography.labelSmall.copy(color = HealthTextSecondary, fontSize = 11.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = HealthCardBorderSubtle)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "From ${patient.kioskId}",
                    style = MaterialTheme.typography.labelSmall.copy(color = HealthTextMuted, fontSize = 10.sp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    when (patient.normalizedStatus) {
                        "WAITING" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthNavy,
                                modifier = Modifier.clickable(onClick = onCall)
                            ) {
                                Text("Call", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthBlueAccent,
                                modifier = Modifier.clickable(onClick = onStartConsult)
                            ) {
                                Text("Consult", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        "CALLED" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HealthBlueAccent,
                                modifier = Modifier.clickable(onClick = onStartConsult)
                            ) {
                                Text("Start Consult", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        "IN_CONSULTATION" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuccessGreen,
                                modifier = Modifier.clickable(onClick = onComplete)
                            ) {
                                Text("Complete", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        "COMPLETED", "SEEN" -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.clickable(onClick = onReopen)
                            ) {
                                Text("Reopen", color = HealthNavy, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                    }

                    Text(
                        text = "Review →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HealthNavy,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val bg = when (status) {
        "COMPLETED", "SEEN" -> SuccessGreen.copy(alpha = 0.15f)
        "IN_CONSULTATION" -> HealthBlueSoft
        "CALLED" -> PastelPeach
        else -> Color(0xFFF1F5F9)
    }
    val textCol = when (status) {
        "COMPLETED", "SEEN" -> SuccessGreen
        "IN_CONSULTATION" -> HealthNavy
        "CALLED" -> Color(0xFFC2410C)
        else -> HealthTextSecondary
    }
    val label = when (status) {
        "COMPLETED", "SEEN" -> "✅ Done"
        "IN_CONSULTATION" -> "🩺 In Consult"
        "CALLED" -> "📞 Called"
        else -> "⏳ Waiting"
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = textCol, fontSize = 10.sp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun PriorityPill(priority: String) {
    val bg = when (priority) {
        "Urgent" -> AlertRed
        "Priority" -> PastelPeach
        else -> Color(0xFFE2E8F0)
    }
    val textCol = when (priority) {
        "Urgent" -> Color.White
        "Priority" -> Color(0xFFC2410C)
        else -> HealthNavy
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
        Text(
            text = priority.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = textCol, fontSize = 10.sp),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun FilterTabChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) HealthNavy else Color.White,
        border = BorderStroke(1.dp, if (isSelected) HealthNavy else HealthCardBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else HealthNavy,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun MetricBox(
    title: String,
    count: String,
    sub: String,
    col: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = HealthBackground,
        border = BorderStroke(1.dp, HealthCardBorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, color = HealthTextSecondary)
            Text(count, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = col)
            Text(sub, fontSize = 8.5.sp, color = HealthTextMuted)
        }
    }
}
