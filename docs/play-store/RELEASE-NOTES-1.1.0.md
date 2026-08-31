# OrderPilot v1.1.0 (versionCode 7) — notki wydania

> Gotowe do WKLEJENIA w Play Console → Production → Release notes (limit 500 znaków/język).
> Wersja zawiera: wizard v2.1, redesign całej apki, sprint niezawodności #1+#2, fixy M8/M9,
> prośbę o recenzję (In-App Review) i przycisk „Poleć kumplowi".

## pl-PL
```
Wielka aktualizacja!
• Nowa, prostsza konfiguracja krok po kroku — z podglądem belki na żywo i automatycznym wykrywaniem włączonych ustawień
• Odświeżony wygląd całej aplikacji
• Podgląd belki w ustawieniach — od razu widzisz efekt zmian
• Dokładniejszy odczyt zleceń na telefonach Xiaomi
• Koniec fałszywych alertów po restarcie telefonu
• Wskazówki dla telefonów Vivo, OPPO i innych
• Poprawki stabilności i płynności
```

## en-US
```
Big update!
• New step-by-step setup with a live preview of the rate bar and automatic detection of enabled settings
• Refreshed design across the whole app
• Live bar preview in settings — see changes instantly
• More accurate offer reading on Xiaomi phones
• No more false alerts after phone restart
• Guidance for Vivo, OPPO and other phones
• Stability and smoothness fixes
```

## uk
```
Велике оновлення!
• Нове покрокове налаштування з живим переглядом панелі та автоматичним виявленням увімкнених налаштувань
• Оновлений дизайн усього додатка
• Живий перегляд панелі в налаштуваннях
• Точніше зчитування замовлень на телефонах Xiaomi
• Більше жодних хибних сповіщень після перезавантаження
• Поради для телефонів Vivo, OPPO та інших
• Виправлення стабільності та плавності
```

## ru
```
Большое обновление!
• Новая пошаговая настройка с живым предпросмотром панели и автоматическим определением включённых настроек
• Обновлённый дизайн всего приложения
• Живой предпросмотр панели в настройках
• Более точное чтение заказов на телефонах Xiaomi
• Больше никаких ложных уведомлений после перезагрузки
• Подсказки для телефонов Vivo, OPPO и других
• Исправления стабильности и плавности
```

## Checklist wydania (Krzysztof)
1. `git checkout feat/wizard-v2` → test na telefonie (świeża instalacja → wizard → Start → belka)
2. Merge do `main` (gałąź zawiera WSZYSTKO: sprint #1 + wizard v2.1 + sprint #2 + redesign + growth)
3. Android Studio: Build → Generate Signed Bundle (AAB), keystore jak zawsze (NIE ruszać keystore!)
4. Play Console → Production → nowa wersja → wgraj AAB + wklej notki ↑
5. (Przy okazji) podmień screenshoty listingu: `docs/play-store/screenshots-v1.1/`
6. (Opcjonalnie, wg planu ASO) tytuł: „OrderPilot: kurier zł/h zł/km"
