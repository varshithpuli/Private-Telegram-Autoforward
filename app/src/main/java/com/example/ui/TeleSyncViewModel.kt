package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class SampleChannel(
    val id: String,
    val title: String,
    val handleOrType: String,
    val subscriberCount: String
) {
    val displayLabel: String
        get() = "$title ($handleOrType)"
}

data class TeleSyncUiState(
    // Auth / Login state
    val isLoggedIn: Boolean = false,
    val isDemoMode: Boolean = false,
    val apiId: String = "",
    val apiHash: String = "",
    val phoneNumber: String = "",
    val loginCode: String = "",
    val twoFactorPassword: String = "",
    val isCodeSent: Boolean = false,
    val authFeedbackMessage: String? = null,
    val isAuthError: Boolean = false,

    // Main Sync Dashboard state
    val sourceChannels: List<SampleChannel> = defaultSourceChannels,
    val destinationChannels: List<SampleChannel> = defaultDestinationChannels,
    val selectedSourceChannel: SampleChannel = defaultSourceChannels.first(),
    val selectedDestinationChannel: SampleChannel = defaultDestinationChannels.first(),
    val isSyncActive: Boolean = false,
    val hideForwardedFrom: Boolean = true,
    val newestFirst: Boolean = false,
    val hourlySpeedLimit: Int = 100,

    // Status Card state
    val currentPostNumber: Int = 142,
    val waitSecondsRemaining: Int = 36,
    val totalCopied: Int = 0
) {
    val statusText: String
        get() = if (isSyncActive) {
            "Copying post #$currentPostNumber... Waiting ${waitSecondsRemaining}s"
        } else {
            "Status: Idle"
        }

    companion object {
        val defaultSourceChannels = listOf(
            SampleChannel(
                id = "src_1",
                title = "Alpha Crypto Signals VIP",
                handleOrType = "Private • 14.2K msgs",
                subscriberCount = "4,820 members"
            ),
            SampleChannel(
                id = "src_2",
                title = "Quant Research Archive",
                handleOrType = "Private • 8.9K msgs",
                subscriberCount = "1,940 members"
            ),
            SampleChannel(
                id = "src_3",
                title = "Global Macro & Tech Wire",
                handleOrType = "Private • 31.5K msgs",
                subscriberCount = "12,300 members"
            )
        )

        val defaultDestinationChannels = listOf(
            SampleChannel(
                id = "dst_1",
                title = "BitChord Main Feed",
                handleOrType = "@bitchord_main",
                subscriberCount = "28.4K subscribers"
            ),
            SampleChannel(
                id = "dst_2",
                title = "Personal Vault Mirror",
                handleOrType = "Private Backup",
                subscriberCount = "Only you"
            ),
            SampleChannel(
                id = "dst_3",
                title = "Alpha Community Hub",
                handleOrType = "@alpha_community_hub",
                subscriberCount = "9,150 subscribers"
            )
        )
    }
}

class TeleSyncViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TeleSyncUiState())
    val uiState: StateFlow<TeleSyncUiState> = _uiState.asStateFlow()

    private var syncJob: Job? = null

    fun updateApiId(value: String) {
        _uiState.update {
            it.copy(
                apiId = value.filter { ch -> ch.isDigit() },
                authFeedbackMessage = null,
                isAuthError = false
            )
        }
    }

    fun updateApiHash(value: String) {
        _uiState.update {
            it.copy(
                apiHash = value.trim(),
                authFeedbackMessage = null,
                isAuthError = false
            )
        }
    }

    fun updatePhoneNumber(value: String) {
        _uiState.update {
            it.copy(
                phoneNumber = value,
                authFeedbackMessage = null,
                isAuthError = false
            )
        }
    }

    fun updateLoginCode(value: String) {
        _uiState.update {
            it.copy(
                loginCode = value.filter { ch -> ch.isDigit() }.take(6),
                authFeedbackMessage = null,
                isAuthError = false
            )
        }
    }

    fun updateTwoFactorPassword(value: String) {
        _uiState.update {
            it.copy(
                twoFactorPassword = value,
                authFeedbackMessage = null,
                isAuthError = false
            )
        }
    }

    fun sendCode() {
        val current = _uiState.value
        if (current.apiId.isBlank() || current.apiHash.isBlank() || current.phoneNumber.isBlank()) {
            _uiState.update {
                it.copy(
                    isCodeSent = false,
                    authFeedbackMessage = "Please enter App api_id, App api_hash, and Phone Number first.",
                    isAuthError = true
                )
            }
            return
        }
        _uiState.update {
            it.copy(
                isCodeSent = true,
                authFeedbackMessage = "Login code requested for ${current.phoneNumber}. Enter the code below.",
                isAuthError = false
            )
        }
    }

    fun verifyAndLogin() {
        val current = _uiState.value
        if (current.apiId.isBlank() || current.apiHash.isBlank() || current.phoneNumber.isBlank()) {
            _uiState.update {
                it.copy(
                    authFeedbackMessage = "Please fill in App api_id, App api_hash, and Phone Number.",
                    isAuthError = true
                )
            }
            return
        }
        if (current.loginCode.isBlank()) {
            _uiState.update {
                it.copy(
                    authFeedbackMessage = "Please enter the Telegram Login Code or tap Demo Login below.",
                    isAuthError = true
                )
            }
            return
        }
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                isDemoMode = false,
                authFeedbackMessage = null,
                isAuthError = false
            )
        }
    }

    fun toggleDemoLogin() {
        val currentlyLoggedIn = _uiState.value.isLoggedIn
        if (currentlyLoggedIn) {
            logout()
        } else {
            _uiState.update {
                it.copy(
                    apiId = it.apiId.ifBlank { "28491024" },
                    apiHash = it.apiHash.ifBlank { "9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c" },
                    phoneNumber = it.phoneNumber.ifBlank { "+91 98765 43210" },
                    loginCode = it.loginCode.ifBlank { "54829" },
                    isLoggedIn = true,
                    isDemoMode = true,
                    authFeedbackMessage = null,
                    isAuthError = false
                )
            }
        }
    }

    fun logout() {
        stopSyncJob()
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                isDemoMode = false,
                isSyncActive = false,
                waitSecondsRemaining = 36
            )
        }
    }

    fun selectSourceChannel(channel: SampleChannel) {
        _uiState.update { it.copy(selectedSourceChannel = channel) }
    }

    fun selectDestinationChannel(channel: SampleChannel) {
        _uiState.update { it.copy(selectedDestinationChannel = channel) }
    }

    fun toggleSyncActive(enabled: Boolean) {
        if (enabled) {
            val intervalSeconds = calculateIntervalSeconds(_uiState.value.hourlySpeedLimit)
            _uiState.update {
                it.copy(
                    isSyncActive = true,
                    currentPostNumber = 142,
                    waitSecondsRemaining = intervalSeconds
                )
            }
            startSyncJob()
        } else {
            stopSyncJob()
            _uiState.update {
                it.copy(
                    isSyncActive = false,
                    waitSecondsRemaining = calculateIntervalSeconds(it.hourlySpeedLimit)
                )
            }
        }
    }

    fun toggleHideForwardedFrom(enabled: Boolean) {
        _uiState.update { it.copy(hideForwardedFrom = enabled) }
    }

    fun toggleNewestFirst(enabled: Boolean) {
        _uiState.update { it.copy(newestFirst = enabled) }
    }

    fun updateHourlySpeedLimit(limit: Int) {
        if (_uiState.value.isSyncActive) return
        val coerced = limit.coerceIn(30, 200)
        _uiState.update {
            it.copy(
                hourlySpeedLimit = coerced,
                waitSecondsRemaining = calculateIntervalSeconds(coerced)
            )
        }
    }

    private fun calculateIntervalSeconds(filesPerHour: Int): Int {
        val safeRate = filesPerHour.coerceIn(30, 200)
        return (3600 / safeRate).coerceAtLeast(18)
    }

    private fun startSyncJob() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            while (isActive && _uiState.value.isSyncActive) {
                delay(1000L)
                _uiState.update { state ->
                    if (!state.isSyncActive) return@update state
                    if (state.waitSecondsRemaining > 1) {
                        state.copy(waitSecondsRemaining = state.waitSecondsRemaining - 1)
                    } else {
                        val nextInterval = calculateIntervalSeconds(state.hourlySpeedLimit)
                        val nextPost = if (state.newestFirst) {
                            (state.currentPostNumber - 1).coerceAtLeast(1)
                        } else {
                            state.currentPostNumber + 1
                        }
                        state.copy(
                            currentPostNumber = nextPost,
                            waitSecondsRemaining = nextInterval,
                            totalCopied = state.totalCopied + 1
                        )
                    }
                }
            }
        }
    }

    private fun stopSyncJob() {
        syncJob?.cancel()
        syncJob = null
    }
}
