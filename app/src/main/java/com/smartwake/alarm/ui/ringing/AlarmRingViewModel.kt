package com.smartwake.alarm.ui.ringing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartwake.alarm.SmartWakeApp
import com.smartwake.alarm.data.repository.AlarmRepositoryImpl
import com.smartwake.alarm.domain.model.Alarm
import com.smartwake.alarm.domain.model.AlarmLog
import com.smartwake.alarm.domain.model.Mission
import com.smartwake.alarm.domain.model.MissionType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface RingUiState {
    data object Ringing : RingUiState
    data object MissionScreen : RingUiState
    data object Success : RingUiState
}

class AlarmRingViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SmartWakeApp
    private val repository = AlarmRepositoryImpl(app.database)

    private val _uiState = MutableStateFlow<RingUiState>(RingUiState.Ringing)
    val uiState: StateFlow<RingUiState> = _uiState.asStateFlow()

    private val _alarm = MutableStateFlow<Alarm?>(null)
    val alarm: StateFlow<Alarm?> = _alarm.asStateFlow()

    private val _mission = MutableStateFlow<Mission?>(null)
    val mission: StateFlow<Mission?> = _mission.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private var timerJob: Job? = null
    private val startTimestamp = System.currentTimeMillis()

    fun initAlarm(alarmId: String) {
        viewModelScope.launch {
            val loadedAlarm = repository.getAlarmById(alarmId)
            _alarm.value = loadedAlarm
            _mission.value = repository.getMissionForAlarm(alarmId)
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _elapsedSeconds.value += 1

                // Safe-Exit: автоотключение через 5 минут непрерывного звонка
                if (_elapsedSeconds.value >= 300) {
                    onMissionCompleted(wasSuccess = false)
                    break
                }
            }
        }
    }

    fun startMission() {
        _uiState.value = RingUiState.MissionScreen
    }

    fun onMissionCompleted(wasSuccess: Boolean = true) {
        timerJob?.cancel()
        _uiState.value = RingUiState.Success

        viewModelScope.launch {
            val currentAlarm = _alarm.value
            val log = AlarmLog(
                id = UUID.randomUUID().toString(),
                alarmId = currentAlarm?.id ?: "unknown",
                scheduledTimestamp = startTimestamp,
                dismissedTimestamp = System.currentTimeMillis(),
                snoozeCountUsed = 0,
                durationSeconds = _elapsedSeconds.value,
                wasSuccess = wasSuccess
            )
            repository.logAlarmEvent(log)
        }
    }

    fun unlockViaEmergencyPin(pin: String): Boolean {
        val expected = _mission.value?.emergencyPin ?: "123456"
        if (pin.trim() == expected) {
            onMissionCompleted(wasSuccess = false)
            return true
        }
        return false
    }
}
