package com.smartwake.alarm.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.smartwake.alarm.SmartWakeApp
import com.smartwake.alarm.data.repository.AlarmRepositoryImpl
import com.smartwake.alarm.domain.model.Alarm
import com.smartwake.alarm.domain.model.MathDifficulty
import com.smartwake.alarm.domain.model.Mission
import com.smartwake.alarm.domain.model.MissionType
import com.smartwake.alarm.platform.alarm.AlarmSchedulerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class EditAlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as SmartWakeApp
    private val repository = AlarmRepositoryImpl(app.database)
    private val scheduler = AlarmSchedulerImpl(application)

    private val _hour = MutableStateFlow(7)
    val hour: StateFlow<Int> = _hour.asStateFlow()

    private val _minute = MutableStateFlow(0)
    val minute: StateFlow<Int> = _minute.asStateFlow()

    private val _daysMask = MutableStateFlow(31) // Будни по умолчанию
    val daysMask: StateFlow<Int> = _daysMask.asStateFlow()

    private val _label = MutableStateFlow("Подъем")
    val label: StateFlow<String> = _label.asStateFlow()

    private val _missionType = MutableStateFlow(MissionType.QR_CODE)
    val missionType: StateFlow<MissionType> = _missionType.asStateFlow()

    private val _barcodeHash = MutableStateFlow("SMARTWAKE_CODE")
    val barcodeHash: StateFlow<String> = _barcodeHash.asStateFlow()

    private val _barcodeLabel = MutableStateFlow("Ванная комната")
    val barcodeLabel: StateFlow<String> = _barcodeLabel.asStateFlow()

    private val _mathDifficulty = MutableStateFlow(MathDifficulty.MEDIUM)
    val mathDifficulty: StateFlow<MathDifficulty> = _mathDifficulty.asStateFlow()

    private val _mathCount = MutableStateFlow(3)
    val mathCount: StateFlow<Int> = _mathCount.asStateFlow()

    private var currentAlarmId: String? = null

    fun loadAlarm(alarmId: String?) {
        if (alarmId == null) {
            currentAlarmId = UUID.randomUUID().toString()
            return
        }
        currentAlarmId = alarmId
        viewModelScope.launch {
            val alarm = repository.getAlarmById(alarmId) ?: return@launch
            _hour.value = alarm.hour
            _minute.value = alarm.minute
            _daysMask.value = alarm.daysOfWeekMask
            _label.value = alarm.label
            _missionType.value = alarm.missionType

            val mission = repository.getMissionForAlarm(alarmId)
            if (mission != null) {
                _barcodeHash.value = mission.targetBarcodeHash
                _barcodeLabel.value = mission.barcodeLabel
                _mathDifficulty.value = mission.mathDifficulty
                _mathCount.value = mission.mathQuestionsCount
            }
        }
    }

    fun setTime(h: Int, m: Int) {
        _hour.value = h
        _minute.value = m
    }

    fun toggleDay(dayBit: Int) {
        _daysMask.value = _daysMask.value xor dayBit
    }

    fun setLabel(newLabel: String) {
        _label.value = newLabel
    }

    fun setMissionType(type: MissionType) {
        _missionType.value = type
    }

    fun setBarcodeData(hash: String, label: String) {
        _barcodeHash.value = hash
        _barcodeLabel.value = label
    }

    fun setMathDifficulty(difficulty: MathDifficulty) {
        _mathDifficulty.value = difficulty
    }

    fun saveAlarm(onSaved: () -> Unit) {
        val id = currentAlarmId ?: UUID.randomUUID().toString()
        viewModelScope.launch {
            val alarm = Alarm(
                id = id,
                hour = _hour.value,
                minute = _minute.value,
                daysOfWeekMask = _daysMask.value,
                isEnabled = true,
                label = _label.value,
                missionType = _missionType.value
            )

            val mission = Mission(
                id = UUID.randomUUID().toString(),
                alarmId = id,
                type = _missionType.value,
                targetBarcodeHash = _barcodeHash.value,
                barcodeLabel = _barcodeLabel.value,
                mathDifficulty = _mathDifficulty.value,
                mathQuestionsCount = _mathCount.value
            )

            repository.saveAlarm(alarm, mission)
            scheduler.schedule(alarm)
            onSaved()
        }
    }
}
