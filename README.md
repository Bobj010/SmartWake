# SmartWake: Умный будильник с QR-верификацией и когнитивными задачами

[![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-brightgreen.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin%202.0-blue.svg)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-orange.svg)](https://developer.android.com/jetpack/compose)
[![ML Kit](https://img.shields.io/badge/Vision-Google%20ML%20Kit-red.svg)](https://developers.google.com/ml-kit)
[![Offline](https://img.shields.io/badge/Offline-100%25%20Autonomous-success.svg)](#)

> **SmartWake** — бескомпромиссный Android-будильник, гарантирующий 100% подъем с кровати через физическое действие (сканирование QR-кода/штрихкода в другой комнате) или когнитивную активацию мозга (решение математических примеров, тест памяти, шейк-тест).

---

## 📱 Релизный APK (Быстрая установка)
Готовый скомпилированный APK-файл размещен прямо в корне репозитория:
👉 **[SmartWake.apk](SmartWake.apk)** (33.9 МБ, включает встроенные нейросетевые модели Google ML Kit для моментального сканирования без интернета).

---

## 🚀 Ключевые возможности

1. **Защита от «утреннего автопилота»**:
   - На экране звонка **нет** кнопки обычного отключения свайпом.
   - Блокировка глушения звука качелькой громкости на корпусе смартфона.
2. **Миссия 1: QR / Штрих-код капча (Физический подъем)**:
   - Привязка любого домашнего кода (штрихкод зубной пасты в ванной, QR-код на холодильнике).
   - Быстрое распознавание через `CameraX` + `Google ML Kit` с кнопкой подсветки фонариком.
3. **Миссия 2: Математический разгон (Когнитивная активация)**:
   - 3 уровня сложности (Easy, Medium, Hard).
   - Настраиваемое количество задач (от 1 до 5) и удобная сенсорная клавиатура.
4. **Миссия 3: Shake-to-Wake (Физическая активность)**:
   - Разгон пульса через 30–50 интенсивных встряхиваний через акселерометр.
5. **Системная надежность Android**:
   - `AlarmManager.setAlarmClock()` — обход Doze Mode и оптимизации батареи.
   - `FullScreenIntent` — подъем экрана поверх блокировки смартфона.
   - `BootReceiver` — автовосстановление всех сигналов при перезагрузке устройства.
   - `Safe-Exit` — аварийное отключение по 6-значному PIN-коду или таймауту 5 мин.
6. **Дизайн в стиле Industrial Functionalism (Anti-Slop)**:
   - Глубокий матовый OLED True Black (`#090A0F`), моноширинные крупные часы с коэффициентом контрастности 21:1, сигнальный оранжевый акцент (`#FF5500`) и тактильный виброотклик.

---

## 🛠️ Стек технологий и архитектура

- **Язык**: Kotlin 1.9 / 2.0 (Coroutines + StateFlow)
- **UI**: Jetpack Compose (Material 3)
- **База данных**: Room ORM (SQLite)
- **Камера и зрение**: CameraX (`camera-view`, `camera-lifecycle`) + Google ML Kit Barcode Scanning
- **Планировщик**: `AlarmManager.setAlarmClock()` + `Foreground Service`
- **Тестирование**: JUnit 4 (Unit-тесты TDD для расчетов времени и капч)

---

## 📁 Документация проекта

Вся проектная документация структурирована в каталоге `docs/`:
- [docs/PRODUCT_SPEC.md](docs/PRODUCT_SPEC.md) — Продуктовая спецификация (для человека)
- [docs/SPEC.md](docs/SPEC.md) — Техническая спецификация архитектуры
- [docs/CODE_MAP.md](docs/CODE_MAP.md) — Архитектурная карта пакетов и классов
- [docs/DECISIONS.md](docs/DECISIONS.md) — Журнал принятых решений (ADR)
- [docs/WORKFLOW_AND_VERIFICATION.md](docs/WORKFLOW_AND_VERIFICATION.md) — Регламент 3-слойной верификации
- [docs/presentation_draft.md](docs/presentation_draft.md) — Структура и текст презентации для защиты

---

## 🔨 Сборка из исходников

```bash
# Клонирование репозитория
git clone https://github.com/EduardSalikhov/SmartWake.git
cd SmartWake

# Запуск юнит-тестов
./gradlew testReleaseUnitTest

# Сборка релизного APK
./gradlew assembleRelease
```
Результат сборки: `app/build/outputs/apk/release/app-release.apk`.
