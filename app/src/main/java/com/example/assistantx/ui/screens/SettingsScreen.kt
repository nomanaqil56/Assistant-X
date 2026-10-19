package com.example.assistantx.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assistantx.engine.CommandEngine
import com.example.assistantx.ui.theme.DarkBackground
import com.example.assistantx.ui.theme.StatusGreen
import com.example.assistantx.ui.theme.TextPrimary
import com.example.assistantx.ui.theme.TextSecondary
import com.example.assistantx.ui.theme.TextTertiary
import java.util.Locale

/**
 * Dedicated Assistant X Settings Destination.
 * Houses Voice & Speech, App Permissions, AI Architecture, and Privacy controls
 * with a unified, restrained, borderless monochrome theme.
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    commandEngine: CommandEngine? = null,
    historyCount: Int = 0,
    onClearHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }

    // Intercept hardware and gesture back
    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // --- TOP APP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x0EFFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back to Chat",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Settings",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )
                    Text(
                        text = "ASSISTANT X PREFERENCES",
                        color = Color(0xFF38BDF8).copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // ========================================================
            // SECTION 1: VOICE & SPEECH
            // ========================================================
            SettingsSectionHeader(
                icon = Icons.Outlined.RecordVoiceOver,
                title = "VOICE & SPEECH"
            )

            SettingsCard {
                // TTS Readiness State
                val isTtsReady = commandEngine?.isTtsReady ?: false
                SettingsStatusRow(
                    label = "Text-to-Speech Engine",
                    statusText = if (isTtsReady) "Operational" else "Initializing",
                    isPositive = isTtsReady
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                // Voice Locale Info
                val currentLocale = Locale.getDefault()
                SettingsDetailRow(
                    label = "Language & Voice",
                    value = "${currentLocale.displayLanguage} (${currentLocale.country})"
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                // Speech Rate & Pitch Info
                Text(
                    text = "Speech rate, pitch, and voice profile are managed through the Android system text-to-speech synthesizer.",
                    color = TextTertiary,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Actions: Test Speech & Open System TTS Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isTtsReady) {
                        SettingsActionButton(
                            label = "Test Voice",
                            icon = Icons.AutoMirrored.Outlined.VolumeUp,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                commandEngine?.let {
                                    // Send a simple audible test phrase through existing engine
                                    val ttsField = it.javaClass.getDeclaredField("ttsManager")
                                    ttsField.isAccessible = true
                                    val tts = ttsField.get(it) as? com.example.assistantx.engine.TextToSpeechManager
                                    tts?.speak("Assistant X voice synthesizer is ready.")
                                }
                            }
                        )
                    }

                    SettingsActionButton(
                        label = "System TTS Settings",
                        icon = Icons.AutoMirrored.Outlined.OpenInNew,
                        modifier = Modifier.weight(1f),
                        onClick = { openSystemTtsSettings(context) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========================================================
            // SECTION 2: APP PERMISSIONS
            // ========================================================
            SettingsSectionHeader(
                icon = Icons.Outlined.Security,
                title = "APP PERMISSIONS"
            )

            SettingsCard {
                // 1. Installed App Querying
                SettingsPermissionItem(
                    name = "Query Installed Applications",
                    permissionCode = "QUERY_ALL_PACKAGES",
                    purpose = "Allows Assistant X to resolve and launch installed apps by voice or text command.",
                    status = "Granted (Manifest)",
                    isGranted = true
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                // 2. Microphone / Audio
                SettingsPermissionItem(
                    name = "Microphone Audio Recording",
                    permissionCode = "RECORD_AUDIO",
                    purpose = "Reserved for speech-to-text recognition and hands-free voice interaction.",
                    status = "Not Requested (Planned)",
                    isGranted = false
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                Spacer(modifier = Modifier.height(4.dp))

                SettingsActionButton(
                    label = "Manage in Android App Settings",
                    icon = Icons.AutoMirrored.Outlined.OpenInNew,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { openApplicationDetailsSettings(context) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========================================================
            // SECTION 3: AI ARCHITECTURE
            // ========================================================
            SettingsSectionHeader(
                icon = Icons.Outlined.Memory,
                title = "AI ENGINE & PIPELINE"
            )

            SettingsCard {
                SettingsDetailRow(
                    label = "Command Architecture",
                    value = "On-Device Pattern & Rule Engine"
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                SettingsDetailRow(
                    label = "Processing Location",
                    value = "100% Local / Zero Cloud Latency"
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                SettingsDetailRow(
                    label = "Generative LLM Weights",
                    value = "None Bundled (Deterministic)"
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                Text(
                    text = "Commands are matched deterministically using an on-device parser and action validator. Planned future updates will introduce quantized local SLM execution and Whisper-based offline speech processing.",
                    color = TextTertiary,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ========================================================
            // SECTION 4: PRIVACY & DATA
            // ========================================================
            SettingsSectionHeader(
                icon = Icons.Outlined.Shield,
                title = "PRIVACY & STORAGE"
            )

            SettingsCard {
                SettingsDetailRow(
                    label = "Data Storage",
                    value = "Sandboxed Local Storage"
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                SettingsDetailRow(
                    label = "External Processing",
                    value = "None (No Outbound Traffic)"
                )

                HorizontalDivider(color = Color(0x0AFFFFFF), thickness = 0.5.dp)

                SettingsDetailRow(
                    label = "Command History",
                    value = "$historyCount entries recorded"
                )

                if (historyCount > 0) {
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingsActionButton(
                        label = "Clear Command History",
                        icon = Icons.Outlined.DeleteOutline,
                        isDestructive = true,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showClearHistoryDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Device Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Assistant X // v1.0.0",
                    color = TextTertiary.copy(alpha = 0.45f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${Build.MANUFACTURER.uppercase()} ${Build.MODEL} • Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                    color = TextTertiary.copy(alpha = 0.35f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- CLEAR HISTORY CONFIRMATION DIALOG ---
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(
                    text = "Clear All History?",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "This will permanently remove all $historyCount recorded commands from Assistant X's local database. This cannot be undone.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearHistoryDialog = false
                        onClearHistory()
                    }
                ) {
                    Text(
                        text = "Clear All",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearHistoryDialog = false }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF10151E),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF38BDF8),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = title,
            color = TextTertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    val cardShape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color(0x0CFFFFFF), cardShape)
            .border(0.5.dp, Color(0x0EFFFFFF), cardShape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsStatusRow(
    label: String,
    statusText: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isPositive) StatusGreen else Color(0xFFF59E0B))
            )
            Text(
                text = statusText,
                color = if (isPositive) StatusGreen else Color(0xFFF59E0B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SettingsDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = TextSecondary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun SettingsPermissionItem(
    name: String,
    permissionCode: String,
    purpose: String,
    status: String,
    isGranted: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = permissionCode,
                    color = TextTertiary.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = status,
                color = if (isGranted) StatusGreen else TextTertiary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = purpose,
            color = TextTertiary,
            fontSize = 11.5.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun SettingsActionButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(10.dp)
    val buttonBg = if (isDestructive) Color(0x18EF4444) else Color(0x12FFFFFF)
    val contentColor = if (isDestructive) Color(0xFFEF4444) else TextPrimary

    Row(
        modifier = modifier
            .clip(buttonShape)
            .background(buttonBg, buttonShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun openSystemTtsSettings(context: Context) {
    try {
        val intent = Intent("com.android.settings.TTS_SETTINGS").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        } catch (_: Exception) {}
    }
}

private fun openApplicationDetailsSettings(context: Context) {
    try {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        } catch (_: Exception) {}
    }
}
