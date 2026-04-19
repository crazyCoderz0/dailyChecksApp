package com.crazycoderz.dailychecks

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crazycoderz.dailychecks.ads.InterstitialAdController
import com.crazycoderz.dailychecks.ui.DailyChecksApp
import com.crazycoderz.dailychecks.ui.MainViewModel
import com.crazycoderz.dailychecks.ui.theme.DailyChecksTheme
import com.crazycoderz.dailychecks.R

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel> {
        MainViewModel.provideFactory(application)
    }

    private lateinit var interstitialAdController: InterstitialAdController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        interstitialAdController = InterstitialAdController(this)

        setContent {
            DailyChecksTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    if (!granted) {
                        viewModel.onNotificationPermissionDenied()
                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.events.collect { event ->
                        when (event) {
                            MainViewModel.UiEvent.ShowInterstitialAd -> {
                                interstitialAdController.showIfAvailable()
                            }
                            MainViewModel.UiEvent.RequestNotificationPermission -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        }
                    }
                }

                DailyChecksApp(
                    uiState = uiState,
                    onCheckItem = viewModel::checkItem,
                    onAddItem = viewModel::addItem,
                    onReminderEnabledChanged = viewModel::setReminderEnabled,
                    onReminderTimeChanged = viewModel::setReminderTime,
                    bannerAdUnitId = context.getString(R.string.admob_banner_unit_id),
                )
            }
        }
    }
}

