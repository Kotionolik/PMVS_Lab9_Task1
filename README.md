# Weather App – Compose Multiplatform

Кроссплатформенное приложение погоды с единым кодом для Android, Desktop (JVM) и Web (JS).  
Данные получаются через **OpenWeatherMap API**, поддерживается кеширование для офлайн-доступа.

[![Build Status](https://github.com/Kotionolik/PMVS_Lab9_Task1/actions/workflows/build.yml/badge.svg)](https://github.com/Kotionolik/PMVS_Lab9_Task1/actions)

---

## Возможности

- **Поиск города** – ввод названия и добавление в избранное.
- **Текущая погода** – температура, описание, влажность, скорость ветра.
- **Прогноз на 5 дней** – краткий прогноз по дням.
- **Кеширование** – просмотр ранее загруженных данных без интернета.
- **Адаптивный UI** – Material 3, поддержка разных размеров экрана (одна, две или три колонки в Web).

---

## 🛠️ Технологии

- **Язык**: Kotlin
- **UI**: Compose Multiplatform (Material 3)
- **Сеть**: Ktor Client
- **Сериализация**: kotlinx.serialization
- **Корутины**: kotlinx.coroutines
- **Тестирование**: Mokkery (моки), kotlin.test
- **Сборка**: Gradle, GitHub Actions
