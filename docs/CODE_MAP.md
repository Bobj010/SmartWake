# Архитектурная карта проекта SmartWake (CODE_MAP)

> **Назначение**: Полный справочник пакетов, модулей и файлов проекта. Каждый файл имеет строго очерченную зону ответственности. Разработчик обязан сверяться с картой перед созданием новых классов.

---

## Структура пакетов (`com.smartwake.alarm`)

```
app/src/main/java/com/smartwake/alarm/
├── SmartWakeApp.kt                     # Базовый Application класс (инициализация Hilt/DI, каналов уведомлений)
│
├── core/                               # Общие утилиты, темы, расширения и константы
│   ├── designsystem/                   # Дизайн-система (Material 3 + кастомная OLED Dark тема)
│   │   ├── Color.kt                    # Палитра цветов (Electric Indigo, Cyber Teal, Slate Black)
│   │   ├── Theme.kt                    # Настройка SmartWakeTheme и темной темы
│   │   ├── Type.kt                     # Типографика (шрифты, крупные цифры часов)
│   │   └── Shape.kt                    # Закругления кнопок и карточек
│   ├── notification/                   # Менеджер каналов системных уведомлений
│   │   └── AlarmNotificationHelper.kt  # Создание High Priority Channel и FullScreen Notification
│   └── util/
│       ├── DateTimeUtils.kt            # Форматирование времени, расчет дельты до звонка
│       └── AudioUtils.kt               # Управление громкостью, фокусом звука и вибрацией
│
├── data/                               # Слой данных (Room Database, репозитории, сущности)
│   ├── local/
│   │   ├── AppDatabase.kt              # Точка входа Room Database (таблицы: alarms, missions, logs)
│   │   ├── dao/
│   │   │   ├── AlarmDao.kt             # CRUD операции со списком будильников
│   │   │   ├── MissionDao.kt           # CRUD для параметров миссий/кодов
│   │   │   └── AlarmLogDao.kt          # Запись и чтение логов пробуждений
│   │   └── entity/
│   │       ├── AlarmEntity.kt          # Сущность таблицы alarms
│   │       ├── MissionEntity.kt        # Сущность таблицы missions
│   │       └── AlarmLogEntity.kt       # Сущность таблицы alarm_logs
│   └── repository/
│       └── AlarmRepositoryImpl.kt      # Реализация доменного репозитория AlarmRepository
│
├── domain/                             # Слой бизнес-логики (Entity, UseCases, репозиторные интерфейсы)
│   ├── model/
│   │   ├── Alarm.kt                    # Чистая доменная модель будильника
│   │   ├── Mission.kt                  # Модель миссии (QR, Math, Shake, Memory)
│   │   └── AlarmLog.kt                 # Модель записи в журнале
│   ├── repository/
│   │   └── AlarmRepository.kt          # Интерфейс доступа к данным
│   └── usecase/
│       ├── GetAlarmsUseCase.kt         # Получение реактивного списка активных будильников
│       ├── ScheduleAlarmUseCase.kt     # Расчет времени и постановка в AlarmManager
│       ├── CancelAlarmUseCase.kt       # Отмена в AlarmManager при выключении
│       └── ValidateMissionUseCase.kt   # Проверка корректности ответа капчи
│
├── platform/                           # Низкоуровневые сервисы и системные интеграции Android
│   ├── alarm/
│   │   ├── AlarmSchedulerImpl.kt       # Точная регистрация в AlarmManager.setAlarmClock
│   │   ├── AlarmReceiver.kt            # BroadcastReceiver системного триггера
│   │   └── BootReceiver.kt             # Восстановление будильников при BOOT_COMPLETED
│   ├── service/
│   │   └── AlarmForegroundService.kt   # Foreground служба звонка (звук, вибро, WakeLock)
│   ├── camera/
│   │   └── QrCodeAnalyzer.kt           # Обработчик кадров CameraX с Google ML Kit
│   └── sensor/
│       └── ShakeDetector.kt            # Детектор встряхивания через SensorManager
│
└── ui/                                 # Слой пользовательского интерфейса (Jetpack Compose)
    ├── main/                           # Главный экран (Список будильников)
    │   ├── MainScreen.kt               # Компоновка списка карточек, переключатели, FAB
    │   ├── MainViewModel.kt            # Стейт списка будильников
    │   └── components/
    │       ├── AlarmCard.kt            # Карточка будильника
    │       └── TimeRemainingBanner.kt  # Плашка «До звонка осталось 6 ч 40 мин»
    │
    ├── edit/                           # Экран создания и редактирования будильника
    │   ├── EditAlarmScreen.kt          # Выбор времени, дней недели, звука и капчи
    │   ├── EditAlarmViewModel.kt       # Управление стейтом формы редактирования
    │   └── components/
    │       ├── TimePickerView.kt       # Барабанный/дисковый пикер времени
    │       └── MissionSelector.kt      # Выбор типа испытания
    │
    ├── ringing/                        # Экран активного будильника (Lock Screen)
    │   ├── AlarmRingActivity.kt        # Полноэкранная активность поверх экрана блокировки
    │   ├── AlarmRingScreen.kt          # UI экрана звонка (крупные часы, кнопка Миссии)
    │   └── AlarmRingViewModel.kt       # Управление стейтом звонка и таймаутами
    │
    ├── missions/                       # Экраны прохождения капч
    │   ├── qr/
    │   │   ├── QrScannerScreen.kt      # Видоискатель CameraX с зеленой рамкой и фонариком
    │   │   └── QrScannerViewModel.kt   # Сверка считанного кода с эталоном
    │   ├── math/
    │   │   ├── MathMissionScreen.kt    # Математические задачи с калькуляторным блоком
    │   │   └── MathMissionViewModel.kt # Генерация примеров и проверка ответов
    │   ├── memory/
    │   │   └── MemoryMissionScreen.kt  # Сетка запоминания плиток
    │   └── shake/
    │       └── ShakeMissionScreen.kt   # Анимированный экран встряхивания с прогресс-баром
    │
    └── history/                        # Экран статистики и логов
        ├── HistoryScreen.kt            # Список истории подъемов
        └── HistoryViewModel.kt         # Статистика эффективности
```

---

## Правила взаимодействия слоев:
1. `ui` общается только с `ViewModel`.
2. `ViewModel` вызывает `UseCase` из слоя `domain`.
3. `domain` независим от Android SDK (за исключением базовых типов времени).
4. `data` реализует интерфейсы репозиториев из `domain` и изолирует Room/SQLite.
5. `platform` инкапсулирует системные сервисы Android (`AlarmManager`, `Service`, `CameraX`).
