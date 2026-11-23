package com.example

import com.example.ui.TeleSyncViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun viewModel_managesLoginAndSyncStatesCleanly() {
        val viewModel = TeleSyncViewModel()

        // Initial Login Card state
        assertFalse(viewModel.uiState.value.isLoggedIn)
        assertEquals("Status: Idle", viewModel.uiState.value.statusText)
        assertEquals(0, viewModel.uiState.value.totalCopied)
        assertTrue(viewModel.uiState.value.hideForwardedFrom)
        assertFalse(viewModel.uiState.value.newestFirst)
        assertEquals(100, viewModel.uiState.value.hourlySpeedLimit)

        // Toggle Demo Login
        viewModel.toggleDemoLogin()
        assertTrue(viewModel.uiState.value.isLoggedIn)

        // Update speed slider while sync switch is OFF
        viewModel.updateHourlySpeedLimit(120)
        assertEquals(120, viewModel.uiState.value.hourlySpeedLimit)

        // Reset to 100 and toggle sync switch ON
        viewModel.updateHourlySpeedLimit(100)
        viewModel.toggleSyncActive(true)
        assertTrue(viewModel.uiState.value.isSyncActive)
        assertEquals("Copying post #142... Waiting 36s", viewModel.uiState.value.statusText)

        // Speed slider updates are ignored while sync switch is ON
        viewModel.updateHourlySpeedLimit(180)
        assertEquals(100, viewModel.uiState.value.hourlySpeedLimit)

        // Toggle sync switch OFF
        viewModel.toggleSyncActive(false)
        assertFalse(viewModel.uiState.value.isSyncActive)
        assertEquals("Status: Idle", viewModel.uiState.value.statusText)
    }
}
