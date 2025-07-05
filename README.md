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

## 🖼️ Скриншоты

- главный экран
  ![Снимок экрана 2025-07-05 111927](https://github.com/user-attachments/assets/e925530b-1957-47e9-a3df-5d72821d39b7)
- выбор группы
  ![Снимок экрана 2025-07-05 111824](https://github.com/user-attachments/assets/8cb31477-56cf-49a1-8c22-0624062f1c96)
- уведомление
  ![Снимок экрана 2025-07-05 121210](https://github.com/user-attachments/assets/ef61f3af-de5f-49d7-870f-520d411cb93e)
