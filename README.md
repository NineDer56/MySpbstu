# University Schedule App

📅 Android-приложение для студентов Политеха, позволяющее просматривать расписание по выбранной учебной группе.

## 📌 Возможности

- Выбор учебной группы
- Просмотр расписания на любой день недели
- Горизонтальный скроллинг по дням
- Подсветка выбранного дня
- Уведомления о зачетах и экзаменах
- Сохранение группы и пропуск экрана выбора
- Splash screen
- Только тёмная тема

## 🧱 Архитектура

- MVVM + Clean Architecture
- LiveData, ViewModel
- Retrofit (загрузка расписания)
- SafeArgs + Navigation
- RecyclerView + SnapHelper
- SharedPreferences
- WorkManager (уведомления)

## 📸 Скриншоты

<p float="left" align="center">

  <div style="display: inline-block; margin: 10px; text-align: center;">
    <img src="Screenshots/Снимок экрана 2025-07-05 111847.png" width="250"/>
    <div>🔔 Разрешение на уведомления</div>
  </div>

  <div style="display: inline-block; margin: 10px; text-align: center;">
    <img src="Screenshots/Снимок экрана 2025-07-05 111927.png" width="250"/>
    <div>🔍 Расписание дня</div>
  </div>

  <div style="display: inline-block; margin: 10px; text-align: center;">
    <img src="Screenshots/Снимок экрана 2025-07-05 111954.png" width="250"/>
    <div>📅 Выбор новой группы</div>
  </div>

  <div style="display: inline-block; margin: 10px; text-align: center;">
    <img src="Screenshots/Снимок экрана 2025-07-05 121117.png" width="250"/>
    <div>🔔 Уведомление о зачете</div>
  </div>


</p>


