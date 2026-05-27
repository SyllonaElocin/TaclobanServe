package com.example.taclobanserve

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.osmdroid.config.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.taclobanserve.ui.screens.*
import com.example.taclobanserve.ui.theme.TaclobanServeTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList

data class TaclobanEvent(
    val title: String,
    val description: String,
    val hours: String,
    val volunteers: String,
    val area: String,
    val professions: List<String> = emptyList(), // Added professions
    val tags: List<String> = emptyList(), // Now used specifically for Skills
    val imageUri: String? = null,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis() + 3600000 // Default 1 hour duration
)

data class TaclobanUser(
    val name: String,
    val email: String,
    val profession: String,
    val skills: List<String>,
    val password: String = "password123", // Added password field
    val joinedYear: String = "2024",
    val volunteerId: String = "V-${(1000..9999).random()}" // Added unique volunteer ID
)

data class CheckInRequest(
    val userName: String,
    val eventTitle: String,
    val timestamp: String,
    val isWithinRange: Boolean,
    var status: String = "PENDING", // PENDING, VERIFIED, REJECTED, REQUIRES_SECONDARY
    val id: String = "#RQ-${(1000..9999).random()}",
    // Manual Override fields
    val isManualOverride: Boolean = false,
    val referenceId: String? = null,
    val overrideReason: String? = null,
    val justification: String? = null,
    val firstAdminId: String? = null,
    val secondAdminId: String? = null,
    val earnedHours: Double = 0.0 // Added field
)

data class JoinProjectRequest(
    val userName: String,
    val userEmail: String, // Added to uniquely identify user
    val event: TaclobanEvent,
    var status: String = "PENDING",
    val id: String = "#JR-${(1000..9999).random()}"
)

data class TaclobanNotification(
    val title: String,
    val message: String,
    val timestamp: String = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date()),
    val isRead: Boolean = false,
    val type: String = "INFO" // INFO, ALERT, SUCCESS
)

sealed class Screen {
    data object Login : Screen()
    data object SignUp : Screen()
    data object VolunteerDashboard : Screen()
    data object AdminDashboard : Screen()
    data object VolunteerHistory : Screen()
    data object BayanihanTranscript : Screen()
    data class Profile(val user: TaclobanUser? = null) : Screen()
    data class ResilientMap(val event: TaclobanEvent) : Screen()
    data class AdminEventMap(val event: TaclobanEvent) : Screen()
    data class AdminEditEvent(val event: TaclobanEvent) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        enableEdgeToEdge()
        setContent {
            TaclobanServeTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                var currentUser by remember { mutableStateOf<TaclobanUser?>(null) }
                var isAdminUser by remember { mutableStateOf(false) }
                
                // Shared State: Source of Truth
                val liveEvents = remember { mutableStateListOf<TaclobanEvent>() }
                
                val joinedMissions = remember { mutableStateListOf<Pair<String, TaclobanEvent>>() } // UserEmail to Event mapping
                
                val appUsers = remember { mutableStateListOf<TaclobanUser>() }
                
                val checkInRequests = remember { mutableStateListOf<CheckInRequest>() }

                val joinProjectRequests = remember { mutableStateListOf<JoinProjectRequest>() }
                
                // Global GPS Simulation State for Testing (User Name -> Is In Range)
                val userGpsSim = remember { mutableStateMapOf<String, Boolean>() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            is Screen.Login -> LoginScreen(
                                users = appUsers,
                                onLoginSuccess = { user, isAdmin ->
                                    currentUser = user
                                    isAdminUser = isAdmin
                                    currentScreen = if (isAdmin) Screen.AdminDashboard else Screen.VolunteerDashboard
                                },
                                onNavigateToSignUp = { currentScreen = Screen.SignUp }
                            )
                            is Screen.SignUp -> SignUpScreen(
                                onSignUpSuccess = { user ->
                                    currentUser = user
                                    isAdminUser = false
                                    appUsers.add(user)
                                    currentScreen = Screen.VolunteerDashboard
                                },
                                onNavigateToLogin = { currentScreen = Screen.Login }
                            )
                            is Screen.VolunteerDashboard -> VolunteerDashboard(
                                skills = currentUser?.skills ?: emptyList(),
                                events = liveEvents,
                                joinedEvents = joinedMissions.filter { it.first == currentUser?.email }.map { it.second },
                                pendingJoinRequests = joinProjectRequests.filter { it.userEmail == currentUser?.email },
                                joinedMissions = joinedMissions,
                                onJoinEvent = { event -> 
                                    if (joinedMissions.none { it.first == currentUser?.email && it.second == event } && 
                                        joinProjectRequests.none { it.event == event && it.userEmail == currentUser?.email }) {
                                        joinProjectRequests.add(JoinProjectRequest(currentUser?.name ?: "Unknown User", currentUser?.email ?: "", event))
                                    }
                                },
                                onLogout = { 
                                    currentUser = null
                                    isAdminUser = false
                                    currentScreen = Screen.Login 
                                },
                                onNavigateToHistory = { currentScreen = Screen.VolunteerHistory },
                                onNavigateToMap = { event -> currentScreen = Screen.ResilientMap(event) },
                                onNavigateToProfile = { currentScreen = Screen.Profile(currentUser) }
                            )
                            is Screen.AdminDashboard -> AdminDashboard(
                                events = liveEvents,
                                users = appUsers,
                                checkIns = checkInRequests,
                                joinRequests = joinProjectRequests,
                                joinedMissions = joinedMissions,
                                userGpsSim = userGpsSim, // Pass the global sim state
                                onApproveJoin = { request ->
                                    joinedMissions.add(request.userEmail to request.event)
                                    joinProjectRequests.remove(request)
                                },
                                onLogout = { 
                                    currentUser = null
                                    isAdminUser = false
                                    currentScreen = Screen.Login 
                                },
                                onNavigateToUserProfile = { user -> currentScreen = Screen.Profile(user) },
                                onViewEventMap = { event -> currentScreen = Screen.AdminEventMap(event) },
                                onEditEvent = { event -> currentScreen = Screen.AdminEditEvent(event) },
                                onQuickUpdateEvent = { updatedEvent ->
                                    val index = liveEvents.indexOfFirst { it.title == updatedEvent.title } // Find by title for simplicity
                                    if (index != -1) {
                                        liveEvents[index] = updatedEvent
                                    }
                                }
                            )
                            is Screen.VolunteerHistory -> VolunteerHistoryScreen(
                                user = currentUser,
                                checkIns = checkInRequests,
                                onNavigateToHome = { currentScreen = Screen.VolunteerDashboard },
                                onNavigateToTranscript = { currentScreen = Screen.BayanihanTranscript },
                                onNavigateToProfile = { currentScreen = Screen.Profile(currentUser) }
                            )
                            is Screen.BayanihanTranscript -> BayanihanTranscriptScreen(
                                onNavigateBack = { currentScreen = Screen.VolunteerHistory }
                            )
                            is Screen.Profile -> {
                                val profile = currentScreen as Screen.Profile
                                ProfileScreen(
                                    user = profile.user,
                                    checkIns = checkInRequests,
                                    onNavigateBack = { currentScreen = if (isAdminUser) Screen.AdminDashboard else Screen.VolunteerDashboard }
                                )
                            }
                            is Screen.ResilientMap -> {
                                val mapScreen = currentScreen as Screen.ResilientMap
                                ResilientMapScreen(
                                    userName = currentUser?.name ?: "Unknown Volunteer",
                                    event = mapScreen.event,
                                    isSimulatingInRange = userGpsSim[currentUser?.name] ?: false,
                                    onGpsSimToggle = { inRange -> userGpsSim[currentUser?.name ?: "Unknown Volunteer"] = inRange },
                                    onCheckIn = { request -> checkInRequests.add(0, request) },
                                    onNavigateBack = { currentScreen = Screen.VolunteerDashboard }
                                )
                            }
                            is Screen.AdminEventMap -> {
                                val mapScreen = currentScreen as Screen.AdminEventMap
                                AdminEventMapScreen(
                                    event = mapScreen.event,
                                    joinedMissions = joinedMissions,
                                    checkIns = checkInRequests,
                                    onNavigateBack = { currentScreen = Screen.AdminDashboard }
                                )
                            }
                            is Screen.AdminEditEvent -> {
                                val editScreen = currentScreen as Screen.AdminEditEvent
                                EditEventScreen(
                                    event = editScreen.event,
                                    onSave = { updatedEvent ->
                                        val index = liveEvents.indexOf(editScreen.event)
                                        if (index != -1) {
                                            liveEvents[index] = updatedEvent
                                        }
                                        currentScreen = Screen.AdminDashboard
                                    },
                                    onNavigateBack = { currentScreen = Screen.AdminDashboard }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
