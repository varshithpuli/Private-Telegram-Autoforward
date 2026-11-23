package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.theme.CarbonSurfaceCard
import com.example.ui.theme.DividerStroke
import com.example.ui.theme.ElevatedCarbonSurface
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.NoticeBorderGreen
import com.example.ui.theme.NoticeSurfaceBg
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.OnTeleSyncGreenContainer
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.TeleSyncGreen
import com.example.ui.theme.TeleSyncGreenContainer
import com.example.ui.theme.TextPrimaryWhite
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.TextTertiarySubtle
import kotlin.math.roundToInt

@Composable
fun TeleSyncApp(
    viewModel: TeleSyncViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoggedIn) {
        BackHandler {
            viewModel.logout()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedContent(
                targetState = uiState.isLoggedIn,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith
                        fadeOut(animationSpec = tween(180))
                },
                label = "auth_dashboard_transition"
            ) { isLoggedIn ->
                if (!isLoggedIn) {
                    LoginScreen(
                        uiState = uiState,
                        onApiIdChange = viewModel::updateApiId,
                        onApiHashChange = viewModel::updateApiHash,
                        onPhoneNumberChange = viewModel::updatePhoneNumber,
                        onLoginCodeChange = viewModel::updateLoginCode,
                        onTwoFactorPasswordChange = viewModel::updateTwoFactorPassword,
                        onSendCodeClick = viewModel::sendCode,
                        onVerifyLoginClick = viewModel::verifyAndLogin,
                        onDemoLoginClick = viewModel::toggleDemoLogin
                    )
                } else {
                    MainSyncDashboardScreen(
                        uiState = uiState,
                        onSelectSourceChannel = viewModel::selectSourceChannel,
                        onSelectDestinationChannel = viewModel::selectDestinationChannel,
                        onToggleSyncActive = viewModel::toggleSyncActive,
                        onToggleHideForwarded = viewModel::toggleHideForwardedFrom,
                        onToggleNewestFirst = viewModel::toggleNewestFirst,
                        onSpeedLimitChange = viewModel::updateHourlySpeedLimit,
                        onLogoutClick = viewModel::logout
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    uiState: TeleSyncUiState,
    onApiIdChange: (String) -> Unit,
    onApiHashChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onLoginCodeChange: (String) -> Unit,
    onTwoFactorPasswordChange: (String) -> Unit,
    onSendCodeClick: () -> Unit,
    onVerifyLoginClick: () -> Unit,
    onDemoLoginClick: () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 520.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Branding Pill & Hero Visual
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CarbonSurfaceCard),
                border = BorderStroke(1.dp, SubtleBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_sync_header_banner_1795419508591),
                        contentDescription = stringResource(R.string.content_desc_header_banner),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        ObsidianBackground.copy(alpha = 0.88f),
                                        ObsidianBackground.copy(alpha = 0.45f)
                                    )
                                )
                            )
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TeleSyncGreenContainer,
                                border = BorderStroke(1.dp, TeleSyncGreen.copy(alpha = 0.45f)),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = TeleSyncGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.app_name),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimaryWhite
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = TeleSyncGreenContainer
                                    ) {
                                        Text(
                                            text = "MTProto v2",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TeleSyncGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Direct Device-to-Telegram Relay Engine",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }
                }
            }

            // Main Login Card (#16181A, rounded 16dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CarbonSurfaceCard),
                border = BorderStroke(1.dp, SubtleBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header: "Connect Telegram Account" with subtitle "Credentials stay locally on this device."
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = TeleSyncGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.login_header_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimaryWhite,
                                modifier = Modifier.testTag("login_header_title")
                            )
                        }
                        Text(
                            text = stringResource(R.string.login_header_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryMuted,
                            modifier = Modifier.testTag("login_header_subtitle")
                        )
                    }

                    HorizontalDivider(color = DividerStroke, thickness = 1.dp)

                    // Outlined text fields
                    TeleSyncOutlinedTextField(
                        value = uiState.apiId,
                        onValueChange = onApiIdChange,
                        label = stringResource(R.string.label_api_id),
                        placeholder = "e.g. 28491024",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Key,
                                contentDescription = null,
                                tint = TextSecondaryMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        testTag = "input_api_id"
                    )

                    TeleSyncOutlinedTextField(
                        value = uiState.apiHash,
                        onValueChange = onApiHashChange,
                        label = stringResource(R.string.label_api_hash),
                        placeholder = "32-character hexadecimal hash",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Hub,
                                contentDescription = null,
                                tint = TextSecondaryMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Next
                        ),
                        testTag = "input_api_hash"
                    )

                    TeleSyncOutlinedTextField(
                        value = uiState.phoneNumber,
                        onValueChange = onPhoneNumberChange,
                        label = stringResource(R.string.label_phone_number),
                        placeholder = "+91 98765 43210",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.PhoneAndroid,
                                contentDescription = null,
                                tint = TextSecondaryMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        testTag = "input_phone_number"
                    )

                    TeleSyncOutlinedTextField(
                        value = uiState.loginCode,
                        onValueChange = onLoginCodeChange,
                        label = stringResource(R.string.label_login_code),
                        placeholder = "5-digit code from Telegram chat",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Pin,
                                contentDescription = null,
                                tint = TextSecondaryMuted
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Next
                        ),
                        testTag = "input_login_code"
                    )

                    TeleSyncOutlinedTextField(
                        value = uiState.twoFactorPassword,
                        onValueChange = onTwoFactorPasswordChange,
                        label = stringResource(R.string.label_2fa_password),
                        placeholder = stringResource(R.string.hint_optional_2fa),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Password,
                                contentDescription = null,
                                tint = TextSecondaryMuted
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.testTag("toggle_2fa_visibility")
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Outlined.VisibilityOff
                                    } else {
                                        Icons.Outlined.Visibility
                                    },
                                    contentDescription = stringResource(R.string.content_desc_toggle_password),
                                    tint = TextSecondaryMuted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        testTag = "input_2fa_password"
                    )

                    // Optional Feedback Notice Banner
                    AnimatedVisibility(visible = uiState.authFeedbackMessage != null) {
                        val message = uiState.authFeedbackMessage ?: ""
                        val borderColor = if (uiState.isAuthError) ErrorCrimson.copy(alpha = 0.6f) else NoticeBorderGreen
                        val bgColor = if (uiState.isAuthError) ErrorCrimson.copy(alpha = 0.12f) else NoticeSurfaceBg
                        val textColor = if (uiState.isAuthError) ErrorCrimson else OnTeleSyncGreenContainer

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_feedback_banner"),
                            shape = RoundedCornerShape(10.dp),
                            color = bgColor,
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.isAuthError) Icons.Outlined.Info else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Action Buttons Row: "Send Code" and "Verify & Login"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onSendCodeClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("send_code_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, TeleSyncGreen.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TeleSyncGreen
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.btn_send_code),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Button(
                            onClick = onVerifyLoginClick,
                            modifier = Modifier
                                .weight(1.15f)
                                .height(48.dp)
                                .testTag("verify_login_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TeleSyncGreen,
                                contentColor = Color(0xFF041206)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.btn_verify_login),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(
                        color = DividerStroke,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Mock "Demo Login" button at the bottom so user can preview the main screen in the emulator
                    OutlinedButton(
                        onClick = onDemoLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("demo_login_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SubtleBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ElevatedCarbonSurface,
                            contentColor = TextPrimaryWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = TeleSyncGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.btn_demo_login),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimaryWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(Preview Main Screen)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeleSyncOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    testTag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = {
            Text(
                text = placeholder,
                color = TextTertiarySubtle,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimaryWhite,
            unfocusedTextColor = TextPrimaryWhite,
            focusedContainerColor = ElevatedCarbonSurface.copy(alpha = 0.55f),
            unfocusedContainerColor = ElevatedCarbonSurface.copy(alpha = 0.35f),
            focusedBorderColor = TeleSyncGreen,
            unfocusedBorderColor = SubtleBorder,
            focusedLabelColor = TeleSyncGreen,
            unfocusedLabelColor = TextSecondaryMuted,
            cursorColor = TeleSyncGreen
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

@Composable
fun MainSyncDashboardScreen(
    uiState: TeleSyncUiState,
    onSelectSourceChannel: (SampleChannel) -> Unit,
    onSelectDestinationChannel: (SampleChannel) -> Unit,
    onToggleSyncActive: (Boolean) -> Unit,
    onToggleHideForwarded: (Boolean) -> Unit,
    onToggleNewestFirst: (Boolean) -> Unit,
    onSpeedLimitChange: (Int) -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("main_sync_dashboard"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.widthIn(max = 540.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar title: "Unofficial Telegram Sync" with subtitle "An unofficial client using the Telegram API."
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimaryWhite,
                            modifier = Modifier.testTag("dashboard_top_title")
                        )
                    }
                    Text(
                        text = stringResource(R.string.dashboard_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted,
                        modifier = Modifier.testTag("dashboard_top_subtitle")
                    )
                }

                // Disconnect / Demo toggle button to return to Login state
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("disconnect_button"),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    border = BorderStroke(1.dp, SubtleBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CarbonSurfaceCard,
                        contentColor = TextSecondaryMuted
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = stringResource(R.string.content_desc_disconnect),
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondaryMuted
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_disconnect),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimaryWhite
                    )
                }
            }

            // Two dropdown selectors: "Source Private Channel" and "Destination Channel"
            ChannelDropdownSelector(
                label = stringResource(R.string.label_source_channel),
                selectedChannel = uiState.selectedSourceChannel,
                channels = uiState.sourceChannels,
                onChannelSelected = onSelectSourceChannel,
                testTag = "dropdown_source_channel"
            )

            ChannelDropdownSelector(
                label = stringResource(R.string.label_destination_channel),
                selectedChannel = uiState.selectedDestinationChannel,
                channels = uiState.destinationChannels,
                onChannelSelected = onSelectDestinationChannel,
                testTag = "dropdown_destination_channel"
            )

            // Main Control Card (#16181A, rounded 16dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_control_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CarbonSurfaceCard),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (uiState.isSyncActive) TeleSyncGreen.copy(alpha = 0.55f) else SubtleBorder
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Row with bold title "Copy into main channel", subtitle "Copies old history & watches new posts", and a green Material 3 Switch.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleSyncActive(!uiState.isSyncActive) }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.copy_into_main_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryWhite,
                                modifier = Modifier.testTag("copy_into_main_title")
                            )
                            Text(
                                text = stringResource(R.string.copy_into_main_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryMuted,
                                modifier = Modifier.testTag("copy_into_main_subtitle")
                            )
                        }

                        TeleSyncGreenSwitch(
                            checked = uiState.isSyncActive,
                            onCheckedChange = onToggleSyncActive,
                            testTag = "switch_copy_into_main"
                        )
                    }

                    // Info notice text: "Telegram watches for accounts that copy many messages. The pace below never exceeds 100 files an hour, and copying pauses whenever Telegram asks."
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sync_info_notice_box"),
                        shape = RoundedCornerShape(12.dp),
                        color = NoticeSurfaceBg,
                        border = BorderStroke(1.dp, NoticeBorderGreen)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = TeleSyncGreen,
                                modifier = Modifier
                                    .padding(top = 1.dp)
                                    .size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.sync_info_notice),
                                style = MaterialTheme.typography.bodySmall,
                                color = OnTeleSyncGreenContainer,
                                modifier = Modifier.testTag("sync_info_notice_text")
                            )
                        }
                    }

                    HorizontalDivider(color = DividerStroke, thickness = 1.dp)

                    // Toggle row: "Hide 'Forwarded from'" ("Copies show as your own posts", default ON)
                    SyncOptionToggleRow(
                        title = stringResource(R.string.toggle_hide_forwarded_title),
                        subtitle = stringResource(R.string.toggle_hide_forwarded_subtitle),
                        checked = uiState.hideForwardedFrom,
                        onCheckedChange = onToggleHideForwarded,
                        switchTestTag = "switch_hide_forwarded"
                    )

                    // Toggle row: "Newest first" ("Starts from latest post instead of oldest", default OFF)
                    SyncOptionToggleRow(
                        title = stringResource(R.string.toggle_newest_first_title),
                        subtitle = stringResource(R.string.toggle_newest_first_subtitle),
                        checked = uiState.newestFirst,
                        onCheckedChange = onToggleNewestFirst,
                        switchTestTag = "switch_newest_first"
                    )

                    HorizontalDivider(color = DividerStroke, thickness = 1.dp)

                    // Speed slider labeled "Hourly speed limit: 100 files/hour" (range 30 to 200, default 100, disabled while sync switch is ON).
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Speed,
                                    contentDescription = null,
                                    tint = if (uiState.isSyncActive) TextTertiarySubtle else TeleSyncGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(
                                        R.string.hourly_speed_limit_format,
                                        uiState.hourlySpeedLimit
                                    ),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (uiState.isSyncActive) TextSecondaryMuted else TextPrimaryWhite,
                                    modifier = Modifier.testTag("hourly_speed_limit_label")
                                )
                            }

                            if (uiState.isSyncActive) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ElevatedCarbonSurface
                                ) {
                                    Text(
                                        text = "Locked while syncing",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiarySubtle,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Slider(
                            value = uiState.hourlySpeedLimit.toFloat(),
                            onValueChange = { newValue ->
                                onSpeedLimitChange(newValue.roundToInt())
                            },
                            valueRange = 30f..200f,
                            enabled = !uiState.isSyncActive,
                            colors = SliderDefaults.colors(
                                thumbColor = TeleSyncGreen,
                                activeTrackColor = TeleSyncGreen,
                                inactiveTrackColor = ElevatedCarbonSurface,
                                disabledThumbColor = TextTertiarySubtle,
                                disabledActiveTrackColor = TeleSyncGreen.copy(alpha = 0.35f),
                                disabledInactiveTrackColor = ElevatedCarbonSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("slider_hourly_speed_limit")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "30 files/hr (Safe)",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextTertiarySubtle
                            )
                            Text(
                                text = "100 files/hr",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondaryMuted
                            )
                            Text(
                                text = "200 files/hr (Max)",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextTertiarySubtle
                            )
                        }
                    }
                }
            }

            // Bottom Status Card showing "Status: Idle" (or "Copying post #142... Waiting 36s" when toggled ON) and "Total copied: 0".
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CarbonSurfaceCard),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (uiState.isSyncActive) TeleSyncGreen.copy(alpha = 0.45f) else SubtleBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Status pulse dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    if (uiState.isSyncActive) TeleSyncGreen else TextTertiarySubtle
                                )
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = uiState.statusText,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily
                                ),
                                color = if (uiState.isSyncActive) TeleSyncGreen else TextPrimaryWhite,
                                modifier = Modifier.testTag("status_text")
                            )
                            Text(
                                text = stringResource(R.string.total_copied_format, uiState.totalCopied),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryMuted,
                                modifier = Modifier.testTag("total_copied_text")
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (uiState.isSyncActive) TeleSyncGreenContainer else ElevatedCarbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (uiState.isSyncActive) TeleSyncGreen.copy(alpha = 0.4f) else SubtleBorder
                        )
                    ) {
                        Text(
                            text = if (uiState.isSyncActive) "ACTIVE" else "STANDBY",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (uiState.isSyncActive) TeleSyncGreen else TextSecondaryMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelDropdownSelector(
    label: String,
    selectedChannel: SampleChannel,
    channels: List<SampleChannel>,
    onChannelSelected: (SampleChannel) -> Unit,
    testTag: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = TextSecondaryMuted
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { expanded = true }
                    .testTag(testTag),
                shape = RoundedCornerShape(14.dp),
                color = CarbonSurfaceCard,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (expanded) TeleSyncGreen else SubtleBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = selectedChannel.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimaryWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${selectedChannel.handleOrType} • ${selectedChannel.subscriberCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = if (expanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = label,
                        tint = if (expanded) TeleSyncGreen else TextSecondaryMuted
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(ElevatedCarbonSurface)
                    .border(1.dp, SubtleBorder, RoundedCornerShape(12.dp))
            ) {
                channels.forEach { channel ->
                    val isSelected = channel.id == selectedChannel.id
                    DropdownMenuItem(
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = channel.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isSelected) TeleSyncGreen else TextPrimaryWhite
                                )
                                Text(
                                    text = "${channel.handleOrType} • ${channel.subscriberCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        },
                        onClick = {
                            onChannelSelected(channel)
                            expanded = false
                        },
                        modifier = Modifier.testTag("${testTag}_item_${channel.id}")
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncOptionToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchTestTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimaryWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )
        }

        TeleSyncGreenSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            testTag = switchTestTag
        )
    }
}

@Composable
private fun TeleSyncGreenSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFF041206),
            checkedTrackColor = TeleSyncGreen,
            checkedBorderColor = TeleSyncGreen,
            uncheckedThumbColor = TextSecondaryMuted,
            uncheckedTrackColor = ElevatedCarbonSurface,
            uncheckedBorderColor = SubtleBorder
        ),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    )
}
