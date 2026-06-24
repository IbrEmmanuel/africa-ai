package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AutomatedTask
import com.example.data.local.ChatMessage
import com.example.data.local.LearningCourse
import com.example.data.local.ResearchReport
import com.example.ui.theme.*
import com.example.ui.viewmodel.OmniViewModel
import com.example.ui.viewmodel.QuizData
import kotlinx.coroutines.launch
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: OmniViewModel) {
    var currentTab by remember { mutableStateOf("Dashboard") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val activeQuiz by viewModel.activeQuiz.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                GlassyCard(
                    borderGlowColor = ElectricPurple,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MintGreen)
                            )
                            Column {
                                Text(
                                    text = "AFRICA-AI WEB4 CORE",
                                    color = CosmicTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Node: Active Gateway | Session Secure",
                                    color = CosmicTextSecondary,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "v3.5-PRO",
                                color = NeonCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 4.dp)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                NeonCyan.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CosmicSurface.copy(alpha = 0.90f),
                                        CosmicSurfaceElevated.copy(alpha = 0.75f)
                                    )
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabs = listOf(
                            NavigationTab("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "Dashboard"),
                            NavigationTab("Chat", Icons.Filled.Chat, Icons.Outlined.Chat, "Chat"),
                            NavigationTab("Sandbox", Icons.Filled.Terminal, Icons.Outlined.Terminal, "Sandbox"),
                            NavigationTab("Learn", Icons.Filled.School, Icons.Outlined.School, "Learn"),
                            NavigationTab("Hub", Icons.Filled.Analytics, Icons.Outlined.Analytics, "Hub")
                        )

                        tabs.forEach { tab ->
                            val selected = currentTab == tab.id
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { currentTab = tab.id }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
                                    contentDescription = tab.label,
                                    tint = if (selected) NeonCyan else CosmicTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = tab.label,
                                    color = if (selected) NeonCyan else CosmicTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CosmicSlateBg)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(ElectricPurple.copy(alpha = 0.12f), Color.Transparent),
                            center = Offset(size.width * 0.15f, size.height * 0.15f),
                            radius = size.width * 0.6f
                        ),
                        radius = size.width * 0.6f,
                        center = Offset(size.width * 0.15f, size.height * 0.15f)
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.10f), Color.Transparent),
                            center = Offset(size.width * 0.85f, size.height * 0.85f),
                            radius = size.width * 0.6f
                        ),
                        radius = size.width * 0.6f,
                        center = Offset(size.width * 0.85f, size.height * 0.85f)
                    )
                }
                .padding(innerPadding)
        ) {
            // Main Content Area with elegant fade transitions
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(spring()) togetherWith fadeOut(spring())
                },
                label = "MainContentTransition"
            ) { tab ->
                when (tab) {
                    "Dashboard" -> DashboardScreen(viewModel, onNavigateToTab = { currentTab = it })
                    "Chat" -> ChatScreen(viewModel, onNavigateToSandbox = { currentTab = "Sandbox" })
                    "Sandbox" -> SandboxScreen(viewModel)
                    "Learn" -> LearningScreen(viewModel)
                    "Hub" -> HubScreen(viewModel)
                }
            }

            // Interactive HUD / Quiz Popup overlay
            activeQuiz?.let { quiz ->
                QuizOverlay(quiz = quiz, onDismiss = { viewModel.dismissQuiz() })
            }
        }
    }
}

data class NavigationTab(
    val id: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val label: String
)

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(viewModel: OmniViewModel, onNavigateToTab: (String) -> Unit) {
    val courses by viewModel.learningCourses.collectAsStateWithLifecycle()
    val tasks by viewModel.automatedTasks.collectAsStateWithLifecycle()
    val reports by viewModel.researchReports.collectAsStateWithLifecycle()
    val totalMessages by viewModel.totalMessagesCount.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Header Banner
        item {
            GlassyCard(
                borderGlowColor = NeonCyan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, "OmniAgent Core", tint = NeonCyan, modifier = Modifier.size(24.dp))
                        }
                        Text(
                            text = "OmniAgent OS v3.5",
                            color = NeonCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Unified Artificial Intelligence OS",
                        color = CosmicTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Build websites, research queries, automate tasks, and teach subjects seamlessly from a single terminal console.",
                        color = CosmicTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Health Status metrics row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusMetricCard(
                    title = "Daily Usage",
                    value = "$totalMessages / 100",
                    sub = "Messages",
                    icon = Icons.Filled.Speed,
                    color = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                StatusMetricCard(
                    title = "Active Agents",
                    value = "4 Online",
                    sub = "Run Diagnostics",
                    icon = Icons.Filled.OfflineBolt,
                    color = ElectricPurple,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        Toast.makeText(context, "Running Unified Diagnostics on all 4 Agents...", Toast.LENGTH_SHORT).show()
                        viewModel.runSystemDiagnostics()
                        onNavigateToTab("Hub")
                    }
                )
            }
        }

        // Active Agent Workspace Quick links
        item {
            Text(
                text = "SPECIALIZED COGNITIVE AGENTS",
                color = CosmicTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        item {
            BentoWebsiteCard(
                onClick = {
                    viewModel.selectAgent("Website Builder")
                    onNavigateToTab("Chat")
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BentoLearningCard(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectAgent("Learning")
                        onNavigateToTab("Chat")
                    }
                )
                BentoResearchCard(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectAgent("Research")
                        onNavigateToTab("Chat")
                    }
                )
            }
        }

        item {
            BentoAutomatorCard(
                onClick = {
                    viewModel.selectAgent("Task")
                    onNavigateToTab("Chat")
                }
            )
        }

        // Recent activity counters
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActivityIndicatorBlock(label = "Courses", count = courses.size, color = ElectricPurple, modifier = Modifier.weight(1f))
                ActivityIndicatorBlock(label = "Tasks Running", count = tasks.count { it.status == "Running" }, color = MintGreen, modifier = Modifier.weight(1f))
                ActivityIndicatorBlock(label = "Saved Reports", count = reports.size, color = WarmAmber, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GlassyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderGlowColor: Color = CosmicBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick)
    } else {
        modifier
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f),
                    borderGlowColor.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.05f)
                )
            )
        ),
        modifier = cardModifier
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CosmicSurface.copy(alpha = 0.75f),
                            CosmicSurfaceElevated.copy(alpha = 0.55f)
                        )
                    )
                )
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
fun skeletonShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    return Brush.linearGradient(
        colors = listOf(
            CosmicSurfaceElevated.copy(alpha = 0.6f),
            Color(0xFF334155).copy(alpha = 0.9f),
            CosmicSurfaceElevated.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnim.value - 300f, translateAnim.value - 300f),
        end = Offset(translateAnim.value, translateAnim.value)
    )
}

@Composable
fun SkeletonItem(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 20.dp,
    widthFraction: Float = 1f,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(shape)
            .background(skeletonShimmerBrush())
    )
}

@Composable
fun SkeletonCard() {
    GlassyCard(borderGlowColor = CosmicBorder) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(skeletonShimmerBrush())
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                    SkeletonItem(widthFraction = 0.4f, height = 14.dp)
                    SkeletonItem(widthFraction = 0.2f, height = 10.dp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonItem(widthFraction = 0.9f, height = 12.dp)
            SkeletonItem(widthFraction = 0.7f, height = 12.dp)
        }
    }
}

@Composable
fun StatusMetricCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    GlassyCard(
        onClick = onClick,
        borderGlowColor = color,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = CosmicTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = value, color = CosmicTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(text = sub, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun AgentLinkCard(
    name: String,
    desc: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val tag = when (name) {
        "Website Builder" -> "WEB RENDER"
        "Learning Agent" -> "NEURAL TUTOR"
        "Research Agent" -> "WEB CRAWLER"
        else -> "CRON WORKER"
    }

    GlassyCard(
        onClick = onClick,
        borderGlowColor = accentColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1.5f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = name, color = CosmicTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            color = accentColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = desc, color = CosmicTextSecondary, fontSize = 12.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
            }

            Icon(Icons.Filled.ArrowForwardIos, null, tint = CosmicTextSecondary, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun ActivityIndicatorBlock(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    GlassyCard(
        borderGlowColor = color,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, color = CosmicTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun PulsingStatusIndicator(
    status: String, // "online" | "busy" | "idle"
    modifier: Modifier = Modifier
) {
    val color = when (status.lowercase()) {
        "online" -> MintGreen
        "busy" -> Color(0xFFEF4444)
        "idle" -> WarmAmber
        else -> CosmicTextSecondary
    }

    val label = status.uppercase()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(12.dp)
        ) {
            // Pulsing ring
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .drawBehind {
                        drawCircle(
                            color = color,
                            radius = (size.minDimension / 2f) * pulseScale,
                            alpha = pulseAlpha
                        )
                    }
            )
            // Core static dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
        Text(
            text = label,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun BentoWebsiteCard(onClick: () -> Unit) {
    GlassyCard(
        onClick = onClick,
        borderGlowColor = NeonCyan,
        modifier = Modifier.fillMaxWidth().testTag("bento_website_builder")
    ) {
        Column(modifier = Modifier.padding(2.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PulsingStatusIndicator(status = "online")
                    Text(
                        text = "WEB RENDER ENGINE",
                        color = NeonCyan.copy(alpha = 0.70f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "v2.0-HTML5",
                        color = NeonCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Website Builder",
                color = CosmicTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Designs interactive frontend views & database schemas",
                color = CosmicTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            // High-fidelity Editor Wireframe Mockup
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CosmicSurface.copy(alpha = 0.70f))
                    .border(BorderStroke(1.dp, CosmicBorder.copy(alpha = 0.40f)), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Browser Top Bar Mockup
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(CosmicSurfaceElevated.copy(alpha = 0.50f))
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("https://web4-render.node/canvas", color = CosmicTextSecondary.copy(alpha = 0.70f), fontSize = 6.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    // Content layout
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Sidebar element
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(30.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(CosmicSurfaceElevated.copy(alpha = 0.60f))
                                .padding(4.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(NeonCyan.copy(alpha = 0.40f)))
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(CosmicTextSecondary.copy(alpha = 0.20f)))
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(CosmicTextSecondary.copy(alpha = 0.20f)))
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(CosmicTextSecondary.copy(alpha = 0.20f)))
                            }
                        }

                        // Main Content area
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(CosmicSlateBg.copy(alpha = 0.80f))
                                .border(BorderStroke(0.5.dp, NeonCyan.copy(alpha = 0.20f)), RoundedCornerShape(4.dp))
                                .padding(6.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.width(40.dp).height(4.dp).background(NeonCyan.copy(alpha = 0.60f)))
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonCyan))
                                }
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(CosmicTextSecondary.copy(alpha = 0.15f)))
                                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(CosmicTextSecondary.copy(alpha = 0.15f)))
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(ElectricPurple.copy(alpha = 0.30f)))
                                    Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(WarmAmber.copy(alpha = 0.30f)))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BentoLearningCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    GlassyCard(
        onClick = onClick,
        borderGlowColor = ElectricPurple,
        modifier = modifier.height(180.dp).testTag("bento_learning_agent")
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(2.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PulsingStatusIndicator(status = "idle")
                        Text(
                            text = "TUTOR",
                            color = ElectricPurple,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Icon(Icons.Filled.MenuBook, null, tint = ElectricPurple, modifier = Modifier.size(14.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Learning",
                    color = CosmicTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Generates customized lessons & dynamic quizzes",
                    color = CosmicTextSecondary,
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Visual Progress Mockup
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CosmicSurface.copy(alpha = 0.60f))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AI Curriculum Progress", color = CosmicTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("84%", color = ElectricPurple, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                LinearProgressIndicator(
                    progress = { 0.84f },
                    color = ElectricPurple,
                    trackColor = CosmicSurfaceElevated.copy(alpha = 0.40f),
                    modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape)
                )
            }
        }
    }
}

@Composable
fun BentoResearchCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    GlassyCard(
        onClick = onClick,
        borderGlowColor = WarmAmber,
        modifier = modifier.height(180.dp).testTag("bento_research_agent")
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(2.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PulsingStatusIndicator(status = "busy")
                        Text(
                            text = "CRAWLER",
                            color = WarmAmber,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Icon(Icons.Filled.TravelExplore, null, tint = WarmAmber, modifier = Modifier.size(14.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Research",
                    color = CosmicTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Compiles structured summaries & cited sources",
                    color = CosmicTextSecondary,
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Visual Simulation Query Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CosmicSurface.copy(alpha = 0.60f))
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(WarmAmber)
                        )
                        Text(
                            text = "deepsearch_spider.log",
                            color = CosmicTextSecondary,
                            fontSize = 7.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "> crawling: \"Web4 standards\"",
                        color = WarmAmber,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun BentoAutomatorCard(onClick: () -> Unit) {
    GlassyCard(
        onClick = onClick,
        borderGlowColor = MintGreen,
        modifier = Modifier.fillMaxWidth().testTag("bento_task_automator")
    ) {
        Column(modifier = Modifier.padding(2.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PulsingStatusIndicator(status = "online")
                    Text(
                        text = "CRON WORKER PIPELINE",
                        color = MintGreen.copy(alpha = 0.70f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MintGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "WORKFLOW",
                        color = MintGreen,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Task Automator",
                color = CosmicTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Coordinates complex multi-agent workflows & scheduled triggers",
                color = CosmicTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            // Horizontal Node Pipeline visualization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CosmicSurface.copy(alpha = 0.60f))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MintGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Trigger", color = MintGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Icon(Icons.Filled.ArrowForward, null, tint = CosmicTextSecondary.copy(alpha = 0.40f), modifier = Modifier.size(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Process", color = ElectricPurple, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Icon(Icons.Filled.ArrowForward, null, tint = CosmicTextSecondary.copy(alpha = 0.40f), modifier = Modifier.size(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Deploy", color = NeonCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PulsingStatusIndicator(status = "online")
                }
            }
        }
    }
}

// ==========================================
// 2. AGENT WORKSPACE CHAT SCREEN
// ==========================================
@Composable
fun ChatScreen(viewModel: OmniViewModel, onNavigateToSandbox: () -> Unit) {
    val selectedAgent by viewModel.selectedAgent.collectAsStateWithLifecycle()
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to latest message on reload
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Agent selection bar (modern glassy capsules)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(CosmicSurface.copy(alpha = 0.45f))
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val agents = listOf("General", "Website Builder", "Learning", "Research", "Task")
            items(agents) { agent ->
                val selected = selectedAgent == agent
                val color = when (agent) {
                    "Website Builder" -> NeonCyan
                    "Learning" -> ElectricPurple
                    "Research" -> WarmAmber
                    "Task" -> MintGreen
                    else -> CosmicTextPrimary
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) color.copy(alpha = 0.20f) else CosmicSurfaceElevated.copy(alpha = 0.60f))
                        .border(
                            BorderStroke(
                                1.dp,
                                if (selected) color else CosmicBorder.copy(alpha = 0.50f)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.selectAgent(agent) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("agent_chip_${agent.lowercase().replace(" ", "_")}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (selected) color else CosmicTextSecondary.copy(alpha = 0.50f))
                        )
                        Text(
                            text = agent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (selected) color else CosmicTextSecondary
                        )
                    }
                }
            }
        }

        // Messages list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty()) {
                // Friendly system instructions / empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = "Bot",
                        tint = CosmicTextSecondary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "OmniAgent Terminal Interface",
                        color = CosmicTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You are currently chatting with the $selectedAgent Agent. Type a query below to execute real-time commands.",
                        color = CosmicTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(messages) { msg ->
                        MessageBubble(message = msg, onNavigateToSandbox = onNavigateToSandbox, viewModel = viewModel)
                    }

                    if (isLoading) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan)
                                    )
                                    Text(
                                        text = "$selectedAgent is compiling full-stack results...",
                                        color = CosmicTextSecondary,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                SkeletonCard()
                            }
                        }
                    }
                }
            }
        }

        // Input bottom field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            NeonCyan.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    CosmicSurface.copy(alpha = 0.85f),
                                    CosmicSurfaceElevated.copy(alpha = 0.70f)
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(Icons.Filled.DeleteSweep, "Clear Session", tint = RoseRed)
                    }

                    TextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = {
                            Text(
                                text = "Execute $selectedAgent agent action...",
                                color = CosmicTextSecondary,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (textInput.trim().isNotEmpty()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        }),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CosmicSurfaceElevated.copy(alpha = 0.6f),
                            unfocusedContainerColor = CosmicSurfaceElevated.copy(alpha = 0.4f),
                            disabledContainerColor = CosmicSurfaceElevated.copy(alpha = 0.4f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = CosmicTextPrimary,
                            unfocusedTextColor = CosmicTextPrimary
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    IconButton(
                        onClick = {
                            if (textInput.trim().isNotEmpty()) {
                                viewModel.sendMessage(textInput)
                                textInput = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = NeonCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .size(46.dp)
                            .testTag("chat_send_button")
                    ) {
                        Icon(Icons.Filled.Send, "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onNavigateToSandbox: () -> Unit,
    viewModel: OmniViewModel
) {
    val isUser = message.sender == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start

    val accentColor = if (isUser) {
        NeonCyan
    } else {
        when (message.agentName) {
            "Website Builder" -> NeonCyan
            "Learning" -> ElectricPurple
            "Research" -> WarmAmber
            "Task" -> MintGreen
            else -> ElectricPurple
        }
    }

    val bubbleBackground = if (isUser) {
        Brush.verticalGradient(
            colors = listOf(
                CosmicSurfaceElevated.copy(alpha = 0.85f),
                CosmicSurface.copy(alpha = 0.65f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                CosmicSurface.copy(alpha = 0.85f),
                CosmicSurfaceElevated.copy(alpha = 0.55f)
            )
        )
    }

    val bubbleBorderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            accentColor.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.05f)
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (isUser) Icons.Filled.Person else Icons.Filled.SmartToy,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (isUser) "Operator" else "${message.agentName} Agent",
                color = CosmicTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .background(bubbleBackground)
                .border(
                    BorderStroke(1.dp, bubbleBorderBrush),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                // If it contains markers, let's clean the output message slightly for display
                val cleanText = message.message
                    .replace("[QUIZ_START]", "")
                    .replace("[QUIZ_END]", "")
                    .replace("[SOURCES_START]", "")
                    .replace("[SOURCES_END]", "")
                    .replace("[TASK_START]", "")
                    .replace("[TASK_END]", "")
                    .substringBefore("{\"question\":")
                    .substringBefore("[{\"title\":")
                    .substringBefore("{\"title\":")
                    .trim()

                Text(
                    text = cleanText,
                    color = CosmicTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Render dynamic rich actions in the chat card
                if (message.type == "website_code") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onNavigateToSandbox,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_sandbox_chat_btn")
                    ) {
                        Icon(Icons.Filled.Terminal, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Code in Sandbox", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (message.type == "quiz") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            // Already set by viewModel when parsed, let's prompt quiz popup if cleared
                            viewModel.sendMessage("Play quiz again")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("play_quiz_chat_btn")
                    ) {
                        Icon(Icons.Filled.Quiz, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Launch Interactive Quiz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (message.type == "research_report") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarmAmber.copy(alpha = 0.12f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Research report parsed and compiled successfully. View references inside the Hub tab.",
                            color = WarmAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (message.type == "task") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MintGreen.copy(alpha = 0.12f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Task structure verified. Active runner launched in the Automator panel.",
                            color = MintGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. WEBSITE BUILDER SANDBOX SCREEN
// ==========================================
@Composable
fun SandboxScreen(viewModel: OmniViewModel) {
    val code by viewModel.sandboxCode.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var isCodeTab by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toggle view top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CosmicSurface)
                .border(BorderStroke(1.dp, CosmicBorder))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { isCodeTab = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isCodeTab) NeonCyan else CosmicSurfaceElevated,
                        contentColor = if (!isCodeTab) Color.Black else CosmicTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Live Preview", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { isCodeTab = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCodeTab) NeonCyan else CosmicSurfaceElevated,
                        contentColor = if (isCodeTab) Color.Black else CosmicTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Code, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Source Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (isCodeTab) {
                IconButton(
                    onClick = {
                        code?.let {
                            clipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(context, "Copied source to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.background(CosmicSurfaceElevated, CircleShape)
                ) {
                    Icon(Icons.Filled.ContentCopy, "Copy Source", tint = NeonCyan, modifier = Modifier.size(18.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (!isCodeTab) {
                if (code.isNullOrEmpty()) {
                    if (isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Website Builder is assembling responsive design components...",
                                color = CosmicTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            SkeletonCard()
                            SkeletonCard()
                            SkeletonCard()
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.Web, null, tint = CosmicTextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "No Live Website Renders Yet", color = CosmicTextSecondary)
                        }
                    }
                } else {
                    // Sandbox Isolated Native Android WebView
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webChromeClient = WebChromeClient()
                                webViewClient = WebViewClient()
                            }
                        },
                        update = { webView ->
                            webView.loadDataWithBaseURL(null, code ?: "", "text/html", "UTF-8", null)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("sandbox_webview")
                    )
                }
            } else {
                // Code view terminal
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF010409))
                        .padding(16.dp)
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = code ?: "// No code parsed.",
                                color = Color(0xFF7EE787), // terminal green
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. LEARNING CENTER SCREEN
// ==========================================
@Composable
fun LearningScreen(viewModel: OmniViewModel) {
    val courses by viewModel.learningCourses.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var showSubjectCreator by remember { mutableStateOf(false) }

    var subjectText by remember { mutableStateOf("") }
    var difficultySelected by remember { mutableStateOf("Beginner") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LEARNING CENTER",
                color = CosmicTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Button(
                onClick = { showSubjectCreator = !showSubjectCreator },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Course", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Subject generator panel
        if (showSubjectCreator) {
            GlassyCard(
                borderGlowColor = ElectricPurple,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Initialize Learning Stream", color = CosmicTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    TextField(
                        value = subjectText,
                        onValueChange = { subjectText = it },
                        placeholder = { Text("Topic (e.g. Deep Learning, Greek Philosophy)", color = CosmicTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CosmicSurfaceElevated,
                            unfocusedContainerColor = CosmicSurfaceElevated,
                            focusedIndicatorColor = ElectricPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = CosmicTextPrimary,
                            unfocusedTextColor = CosmicTextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Difficulty selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val difficulties = listOf("Beginner", "Intermediate", "Advanced")
                        difficulties.forEach { diff ->
                            val selected = difficultySelected == diff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) ElectricPurple else CosmicSurfaceElevated)
                                    .clickable { difficultySelected = diff }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = diff,
                                    fontSize = 12.sp,
                                    color = if (selected) Color.Black else CosmicTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (subjectText.trim().isNotEmpty()) {
                                viewModel.createCourse(subjectText, difficultySelected)
                                subjectText = ""
                                showSubjectCreator = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Course Syllabus", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (isLoading) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Synthesizing customized interactive curriculum...",
                    color = CosmicTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                SkeletonCard()
                SkeletonCard()
                SkeletonCard()
            }
        } else if (courses.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.School, "Syllabus", tint = CosmicTextSecondary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No courses generated yet", color = CosmicTextSecondary)
                Text("Tap 'New Course' above to generate an instant AI syllabus on any topic.", color = CosmicTextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(courses) { course ->
                    CourseCard(course = course, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CourseCard(course: LearningCourse, viewModel: OmniViewModel) {
    var expanded by remember { mutableStateOf(false) }

    GlassyCard(
        borderGlowColor = ElectricPurple,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = course.subjectName, color = CosmicTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "Difficulty: ${course.difficulty}", color = ElectricPurple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            IconButton(onClick = { viewModel.deleteCourse(course.id) }) {
                Icon(Icons.Filled.Delete, "Delete course", tint = RoseRed, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LinearProgressIndicator(
                progress = { course.progress },
                color = ElectricPurple,
                trackColor = CosmicSurfaceElevated,
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(CircleShape)
            )
            Text(
                text = "${(course.progress * 100).toInt()}%",
                color = CosmicTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Expand modules lists
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceElevated, contentColor = CosmicTextPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (expanded) "Hide Syllabus Plan" else "View Syllabus Plan", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                null,
                modifier = Modifier.size(16.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val syllabus = remember(course.curriculumJson) {
                    try {
                        val arr = JSONArray(course.curriculumJson)
                        val list = mutableListOf<SyllabusLesson>()
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            list.add(
                                SyllabusLesson(
                                    title = obj.getString("title"),
                                    desc = obj.getString("description")
                                )
                            )
                        }
                        list
                    } catch (e: Exception) {
                        emptyList()
                    }
                }

                syllabus.forEachIndexed { index, lesson ->
                    val completed = index < course.currentLessonIndex
                    val active = index == course.currentLessonIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) ElectricPurple.copy(alpha = 0.08f) else Color.Transparent)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (completed) Icons.Filled.CheckCircle else if (active) Icons.Filled.PlayCircle else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (completed) MintGreen else if (active) ElectricPurple else CosmicTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lesson.title,
                                color = if (active) ElectricPurple else CosmicTextPrimary,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                            Text(text = lesson.desc, color = CosmicTextSecondary, fontSize = 11.sp)
                        }

                        if (active) {
                            Button(
                                onClick = {
                                    viewModel.advanceCourseLesson(course)
                                    // Auto suggest taking quiz or chat
                                    viewModel.sendMessage("I finished lesson: ${lesson.title}. Give me a test quiz please!")
                                    viewModel.selectAgent("Learning")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text("Complete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SyllabusLesson(
    val title: String,
    val desc: String
)

// ==========================================
// 5. HUB (RESEARCH REPORTS & TASKS) SCREEN
// ==========================================
@Composable
fun HubScreen(viewModel: OmniViewModel) {
    val reports by viewModel.researchReports.collectAsStateWithLifecycle()
    val tasks by viewModel.automatedTasks.collectAsStateWithLifecycle()
    var selectedSubTab by remember { mutableStateOf("Reports") } // "Reports", "Tasks", "Security"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Selector Header Sub-Tabs (gorgeous Web4 sub-dock)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CosmicSurface.copy(alpha = 0.45f))
                .border(BorderStroke(1.dp, CosmicBorder.copy(alpha = 0.50f)), shape = RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            val subTabs = listOf("Reports", "Tasks", "Security")
            subTabs.forEach { tab ->
                val active = selectedSubTab == tab
                val color = when (tab) {
                    "Reports" -> WarmAmber
                    "Tasks" -> MintGreen
                    else -> ElectricPurple
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) color.copy(alpha = 0.20f) else Color.Transparent)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (active) color else Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedSubTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (active) color else CosmicTextSecondary.copy(alpha = 0.50f))
                        )
                        Text(
                            text = when (tab) {
                                "Reports" -> "Research"
                                "Tasks" -> "Automator"
                                else -> "Security"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (active) color else CosmicTextSecondary
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedSubTab) {
                "Reports" -> {
                    if (reports.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.MenuBook, "Book", tint = CosmicTextSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No research reports saved yet", color = CosmicTextSecondary)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(reports) { report ->
                                ReportCard(report = report, viewModel = viewModel)
                            }
                        }
                    }
                }
                "Tasks" -> {
                    TaskAutomatorPanel(tasks = tasks, viewModel = viewModel)
                }
                "Security" -> {
                    ApiGatewaySecurityScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ReportCard(report: ResearchReport, viewModel: OmniViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    GlassyCard(
        borderGlowColor = WarmAmber,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.query, color = CosmicTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Report ID: #${report.id}",
                    color = WarmAmber,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = { viewModel.deleteReportById(report.id) }) {
                Icon(Icons.Filled.Delete, "Delete", tint = RoseRed, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = report.summary, color = CosmicTextSecondary, fontSize = 12.sp, maxLines = if (expanded) Int.MAX_VALUE else 2)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceElevated, contentColor = CosmicTextPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (expanded) "Collapse Content" else "Read Full report", fontSize = 12.sp)
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = CosmicBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report.content,
                color = CosmicTextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Sources citations clickable list
            val sources = remember(report.sourcesJson) {
                try {
                    val arr = JSONArray(report.sourcesJson)
                    val list = mutableListOf<ReportSource>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        list.add(
                            ReportSource(
                                title = obj.getString("title"),
                                url = obj.getString("url")
                            )
                        )
                    }
                    list
                } catch (e: Exception) {
                    emptyList()
                }
            }

            if (sources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "CITED SOURCES", color = WarmAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sources.forEach { src ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicSurfaceElevated)
                                .clickable {
                                    try {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(src.url))
                                        context.startActivity(browserIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Cannot open: ${src.url}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Public, null, tint = WarmAmber, modifier = Modifier.size(16.dp))
                            Text(
                                text = src.title,
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Filled.Launch, null, tint = CosmicTextSecondary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}

data class ReportSource(
    val title: String,
    val url: String
)

// ==========================================
// 6. TASK AUTOMATOR VIEW PANEL
// ==========================================
@Composable
fun TaskAutomatorPanel(tasks: List<AutomatedTask>, viewModel: OmniViewModel) {
    var showTaskCreator by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var triggerTypeSelected by remember { mutableStateOf("Manual") }
    var actionDetailsText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Active task runner queues", color = CosmicTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

            IconButton(
                onClick = { showTaskCreator = !showTaskCreator },
                modifier = Modifier.background(CosmicSurfaceElevated, CircleShape)
            ) {
                Icon(if (showTaskCreator) Icons.Filled.Close else Icons.Filled.Add, "Toggle", tint = MintGreen)
            }
        }

        if (showTaskCreator) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                border = BorderStroke(1.dp, CosmicBorder),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Deploy Automated Worker", color = CosmicTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    TextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        placeholder = { Text("Task Title (e.g. Email server check)", color = CosmicTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = CosmicSurfaceElevated, unfocusedContainerColor = CosmicSurfaceElevated, focusedIndicatorColor = MintGreen, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = CosmicTextPrimary, unfocusedTextColor = CosmicTextPrimary),
                        shape = RoundedCornerShape(8.dp)
                    )

                    TextField(
                        value = taskDesc,
                        onValueChange = { taskDesc = it },
                        placeholder = { Text("Short Description", color = CosmicTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = CosmicSurfaceElevated, unfocusedContainerColor = CosmicSurfaceElevated, focusedIndicatorColor = MintGreen, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = CosmicTextPrimary, unfocusedTextColor = CosmicTextPrimary),
                        shape = RoundedCornerShape(8.dp)
                    )

                    TextField(
                        value = actionDetailsText,
                        onValueChange = { actionDetailsText = it },
                        placeholder = { Text("Endpoint / Action payload details", color = CosmicTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = CosmicSurfaceElevated, unfocusedContainerColor = CosmicSurfaceElevated, focusedIndicatorColor = MintGreen, unfocusedIndicatorColor = Color.Transparent, focusedTextColor = CosmicTextPrimary, unfocusedTextColor = CosmicTextPrimary),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val triggers = listOf("Manual", "Schedule", "API")
                        triggers.forEach { trig ->
                            val selected = triggerTypeSelected == trig
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) MintGreen else CosmicSurfaceElevated)
                                    .clickable { triggerTypeSelected = trig }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = trig,
                                    fontSize = 12.sp,
                                    color = if (selected) Color.Black else CosmicTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (taskTitle.trim().isNotEmpty()) {
                                viewModel.createTask(
                                    title = taskTitle,
                                    desc = taskDesc,
                                    triggerType = triggerTypeSelected,
                                    action = actionDetailsText
                                )
                                taskTitle = ""
                                taskDesc = ""
                                actionDetailsText = ""
                                showTaskCreator = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MintGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verify & Deploy Worker", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No active background workflows", color = CosmicTextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(task = task, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: AutomatedTask, viewModel: OmniViewModel) {
    val statusColor = when (task.status) {
        "Completed" -> MintGreen
        "Running" -> NeonCyan
        "Failed" -> RoseRed
        else -> CosmicTextSecondary
    }

    GlassyCard(
        borderGlowColor = statusColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = task.title, color = CosmicTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Trigger: ${task.triggerType}",
                    color = CosmicTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = task.status,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                    Icon(Icons.Filled.Delete, "Delete", tint = RoseRed, modifier = Modifier.size(16.dp))
                }
            }
        }

        if (!task.description.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description, color = CosmicTextSecondary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CosmicSurfaceElevated)
                .padding(8.dp)
        ) {
            Text(
                text = "Action: ${task.actionDetails}",
                color = CosmicTextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        if (task.status == "Pending" || task.status == "Failed") {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { viewModel.executeTask(task) },
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Trigger Action Run", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 7. QUIZ HUB OVERLAY WIDGET
// ==========================================
@Composable
fun QuizOverlay(quiz: QuizData, onDismiss: () -> Unit) {
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {}
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurfaceElevated),
            border = BorderStroke(1.dp, ElectricPurple),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().testTag("quiz_card_popup")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Quiz, null, tint = ElectricPurple, modifier = Modifier.size(18.dp))
                        Text(text = "INTERACTIVE LEARNING EVAL", color = ElectricPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, "Close", tint = CosmicTextSecondary)
                    }
                }

                Text(
                    text = quiz.question,
                    color = CosmicTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quiz.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrectOption = index == quiz.correctIndex

                        val itemBg = when {
                            hasAnswered && isCorrectOption -> MintGreen.copy(alpha = 0.15f)
                            hasAnswered && isSelected && !isCorrectOption -> RoseRed.copy(alpha = 0.15f)
                            isSelected -> ElectricPurple.copy(alpha = 0.12f)
                            else -> CosmicSurface
                        }

                        val itemBorder = when {
                            hasAnswered && isCorrectOption -> MintGreen
                            hasAnswered && isSelected && !isCorrectOption -> RoseRed
                            isSelected -> ElectricPurple
                            else -> CosmicBorder
                        }

                        val itemTextColor = when {
                            hasAnswered && isCorrectOption -> MintGreen
                            hasAnswered && isSelected && !isCorrectOption -> RoseRed
                            isSelected -> ElectricPurple
                            else -> CosmicTextPrimary
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(itemBg)
                                .border(BorderStroke(1.dp, itemBorder), RoundedCornerShape(12.dp))
                                .clickable(enabled = !hasAnswered) {
                                    selectedOptionIndex = index
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                color = itemTextColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            if (hasAnswered) {
                                Icon(
                                    imageVector = if (isCorrectOption) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                    contentDescription = null,
                                    tint = if (isCorrectOption) MintGreen else RoseRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                if (!hasAnswered) {
                    Button(
                        onClick = {
                            if (selectedOptionIndex != null) {
                                hasAnswered = true
                            }
                        },
                        enabled = selectedOptionIndex != null,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Verify Answer", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CosmicSurface)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Feedback Evaluation:",
                                color = if (selectedOptionIndex == quiz.correctIndex) MintGreen else RoseRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = quiz.explanation,
                                color = CosmicTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicSurface, contentColor = CosmicTextPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dismiss Terminal Evaluation")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiGatewaySecurityScreen(viewModel: OmniViewModel) {
    val isProxyEnabled by viewModel.isProxyEnabled.collectAsStateWithLifecycle()
    val activeToken by viewModel.activeToken.collectAsStateWithLifecycle()
    val securityLogs by viewModel.securityLogs.collectAsStateWithLifecycle()

    var customEmail by remember { mutableStateOf("habu23585@gmail.com") }
    var customRole by remember { mutableStateOf("researcher") }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Section 1: API Proxy Gateway status
        item {
            GlassyCard(
                borderGlowColor = if (isProxyEnabled) ElectricPurple else CosmicBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                imageVector = if (isProxyEnabled) Icons.Filled.Shield else Icons.Filled.LockOpen,
                                contentDescription = null,
                                tint = if (isProxyEnabled) ElectricPurple else CosmicTextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text("API Proxy Authorization", color = CosmicTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = if (isProxyEnabled) "Next.js Secure Proxy Enabled" else "Direct Connection (Unsecured)",
                                    color = if (isProxyEnabled) ElectricPurple else CosmicTextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Switch(
                            checked = isProxyEnabled,
                            onCheckedChange = { viewModel.toggleProxy(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = ElectricPurple,
                                uncheckedThumbColor = CosmicTextSecondary,
                                uncheckedTrackColor = CosmicSurfaceElevated
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Validates client sessions via signed HMAC-SHA256 JWT tokens. Prevents unauthorized database mutations or external model abuse.",
                        color = CosmicTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section 2: Decoded Token Visualization
        item {
            GlassyCard(
                borderGlowColor = ElectricPurple,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ACTIVE SESSION JSON WEB TOKEN (JWT)", color = ElectricPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    if (activeToken.isNotEmpty()) {
                        val tokenParts = activeToken.split(".")
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicSurfaceElevated)
                                .padding(12.dp)
                        ) {
                            if (tokenParts.size == 3) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text("JWT Decoder Visualizer", color = CosmicTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(activeToken))
                                            Toast.makeText(context, "JWT Token Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.ContentCopy, "Copy", tint = ElectricPurple, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                // Header (Red)
                                Text(text = tokenParts[0], color = Color(0xFFEF5350), fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(text = ".", color = CosmicTextSecondary, fontSize = 11.sp)
                                // Payload (Blue)
                                Text(text = tokenParts[1], color = Color(0xFF42A5F5), fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 4, overflow = TextOverflow.Ellipsis)
                                Text(text = ".", color = CosmicTextSecondary, fontSize = 11.sp)
                                // Signature (Green)
                                Text(text = tokenParts[2], color = Color(0xFF66BB6A), fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            } else {
                                Text(text = activeToken, color = CosmicTextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Parse and show claims
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("DECODED PAYLOAD CLAIMS", color = CosmicTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            
                            val decodedPayload = remember(activeToken) {
                                try {
                                    val payloadB64 = activeToken.split(".")[1]
                                    val bytes = android.util.Base64.decode(payloadB64, android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE)
                                    String(bytes, java.nio.charset.StandardCharsets.UTF_8)
                                } catch (e: Exception) {
                                    "Error parsing"
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CosmicSurfaceElevated)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = decodedPayload,
                                    color = Color(0xFF42A5F5),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    } else {
                        Text("No active session JWT token found.", color = CosmicTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 3: Token Manipulation & Security Controls
        item {
            GlassyCard(
                borderGlowColor = CosmicBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("SIMULATE MITM & SIGNATURE COMPROMISE", color = CosmicTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.tamperJwt() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350).copy(alpha = 0.15f), contentColor = Color(0xFFEF5350)),
                            border = BorderStroke(1.dp, Color(0xFFEF5350)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.SecurityUpdateWarning, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Tamper Claims", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.invalidateSignature() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100).copy(alpha = 0.15f), contentColor = Color(0xFFFF9100)),
                            border = BorderStroke(1.dp, Color(0xFFFF9100)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.HeartBroken, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Break Signature", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    HorizontalDivider(color = CosmicBorder)

                    Text("RE-GENERATE AUTHENTIC JWT SESSION", color = CosmicTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    TextField(
                        value = customEmail,
                        onValueChange = { customEmail = it },
                        placeholder = { Text("User Email", color = CosmicTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CosmicSurfaceElevated,
                            unfocusedContainerColor = CosmicSurfaceElevated,
                            focusedIndicatorColor = ElectricPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = CosmicTextPrimary,
                            unfocusedTextColor = CosmicTextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    TextField(
                        value = customRole,
                        onValueChange = { customRole = it },
                        placeholder = { Text("User Role (e.g. researcher, admin)", color = CosmicTextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = CosmicSurfaceElevated,
                            unfocusedContainerColor = CosmicSurfaceElevated,
                            focusedIndicatorColor = ElectricPurple,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = CosmicTextPrimary,
                            unfocusedTextColor = CosmicTextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            if (customEmail.isNotEmpty() && customRole.isNotEmpty()) {
                                viewModel.generateJwt(customEmail, customRole)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sign & Issue Authenticated Session", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section 4: Live Security Logs Terminal
        item {
            GlassyCard(
                borderGlowColor = ElectricPurple,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.ListAlt, null, tint = ElectricPurple, modifier = Modifier.size(16.dp))
                            Text("API GATEWAY LIVE AUDIT FEED", color = CosmicTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.clearSecurityLogs() },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicSurfaceElevated, contentColor = CosmicTextSecondary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Clear", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .padding(8.dp)
                    ) {
                        if (securityLogs.isEmpty()) {
                            Text("No access requests intercepted yet.", color = CosmicTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(securityLogs) { log ->
                                    val color = when {
                                        log.contains("FAILURE") || log.contains("WARNING") -> Color(0xFFEF5350)
                                        log.contains("SUCCESS") -> Color(0xFF66BB6A)
                                        log.contains("JWT Generated") -> Color(0xFF42A5F5)
                                        else -> CosmicTextSecondary
                                    }
                                    Text(
                                        text = log,
                                        color = color,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

