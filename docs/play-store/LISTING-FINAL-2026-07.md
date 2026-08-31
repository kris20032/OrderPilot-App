# OrderPilot - FINALNY listing Google Play (lipiec 2026)

> **Co to jest:** gotowy pakiet tekstów do wklejenia w Google Play Console - polski (główny), angielski, ukraiński i rosyjski. Każdy tekst napisany naturalnie w danym języku (nie tłumaczony słowo w słowo), policzony skryptem i zgodny z zasadami Google Play oraz zasadami domu (zero długich myślników, zero obietnicy "zero bana", dane zostają na telefonie).
>
> **Ważne:** listing opisuje TYLKO funkcje z wersji LIVE (v1.0.5): kolorowa belka zł/h i zł/km, 4 platformy, praca w pełni offline. Nowości z 1.1.0 (nowy kreator, podgląd belki itd.) dopisać DOPIERO po wydaniu 1.1.0.
>
> Zmiana tekstów w Play Console NIE wymaga nowej wersji apki - to tylko opis w sklepie, można podmienić w każdej chwili i cofnąć w każdej chwili.

---

## REKOMENDACJA TYTUŁU (przeczytaj przed wklejaniem)

**Rekomendowany tytuł PL: `OrderPilot: kurier zł/h zł/km` (29/30 znaków).**

Dlaczego ten: tytuł ma najwyższą wagę w wyszukiwarce Google Play, a obecny tytuł "OrderPilot" nie zawiera ŻADNEGO słowa, którego kurier szuka. Słowo "kurier" + jednostki wartości "zł/h" i "zł/km" to najsilniejszy pojedynczy ruch ASO z całego planu rozwoju. Dwukropek po marce dodatkowo pomaga na zapytanie "order pilot" pisane ze spacją.

**Jak wdrożyć (wg planu - przez eksperyment, nie na ślepo):**
1. W Play Console uruchom eksperyment strony aplikacji (Store listing experiment): stary tytuł "OrderPilot" kontra nowy `OrderPilot: kurier zł/h zł/km`, podział 50/50, tylko język polski.
2. Poczekaj minimum 7-14 dni i zobacz, co Console pokaże.
3. **Uczciwa uwaga:** przy obecnych ~0 instalacjach eksperyment może nigdy nie zebrać dość danych, żeby wskazać zwycięzcę. Jeśli po 2-3 tygodniach Console dalej mówi "za mało danych" - zakończ eksperyment i po prostu ustaw nowy tytuł na stałe w głównym listingu. Przy zerowej bazie nie ma czego zepsuć, a słowo "kurier" w tytule zaczyna pracować od razu.

**Nasycenie fraz w pełnym opisie PL** (policzone skryptem): kurier* x15, opłac* x9, zlecen* x9, zł/h x6, zł/km x3, Uber x6, Wolt x5, Glovo x5, Bolt x5, Order Pilot / OrderPilot x8. Fraza główna "kurier" = 3.2% wszystkich słów, "opłac-" = 1.9% - w widełkach 2-3% dla frazy głównej, naturalnie, bez upychania.

**Strategia językowa (jak ustawić osobne listingi):**
- **Polski = język główny listingu** (domyślny). To rynek docelowy - apka liczy w złotówkach.
- **Ukraiński i rosyjski = dodać TERAZ jako tłumaczenia listingu.** To największy nieodblokowany segment: tysiące kurierów z Ukrainy w Polsce, zerowa konkurencja w wyszukiwarce Play w tych językach. Google indeksuje tytuł i opisy OSOBNO dla każdego języka, więc ukraiński kurier wpisujący "кур'єр" znajdzie apkę tylko wtedy, gdy listing UK istnieje.
- **Angielski = przygotowany poniżej, ale z gwiazdką.** Plan rozwoju mówi: wstrzymać EN, bo apka liczy w PLN i instalacje z USA psułyby retencję (a przez to ranking). ALE: jeśli w Play Console apka jest dostępna TYLKO w Polsce (sekcja "Kraje/regiony" w wydaniu produkcyjnym), to listing EN jest bezpieczny i wręcz przydatny - zobaczą go kurierzy W POLSCE, którzy mają telefon ustawiony po angielsku. Zasada: **najpierw sprawdź kraje dystrybucji; jeśli tylko Polska - wgraj EN; jeśli cały świat - EN wstrzymaj albo ogranicz kraje do Polski.**
- Kolejność wklejania: PL -> UK -> RU -> (EN warunkowo).

---

## POLSKI - listing główny (pl-PL)

### Tytuł aplikacji - 29/30 znaków
```
OrderPilot: kurier zł/h zł/km
```
Wariant alternatywny (gdyby główny nie zadziałał w eksperymencie) - 29/30 znaków:
```
OrderPilot: kurier Glovo Wolt
```

### Krótki opis - 79/80 znaków
```
Czy zlecenie się opłaca? zł/h i zł/km dla kurierów Uber Eats, Wolt, Glovo, Bolt
```
Wariant bezpieczny (to, co jest dziś LIVE - gdyby Krzysztof wolał nie zmieniać) - 67/80 znaków:
```
Order Pilot dla kurierów: PLN/h zleceń Uber, Wolt, Glovo, Bolt Food
```


### Pełny opis - 3119/4000 znaków
```
Order Pilot to darmowa aplikacja dla kurierów Uber Eats, Wolt, Glovo i Bolt Food. OrderPilot pokazuje, ile zł/h i zł/km jest warte każde zlecenie, w momencie gdy wpada - zielona, żółta lub czerwona belka nad apką kuriera mówi w 5 sekund, czy oferta się opłaca. Koniec liczenia opłacalności w głowie.

CZY TO ZLECENIE SIĘ OPŁACA?
Tyle zarabiasz na godzinę, a tyle za kilometr - to jedyne, co się liczy. Order Pilot przelicza kwotę, dystans i czas na zł/h i zł/km, i od razu mówi:
🟢 zielona belka = bierz
🟡 żółta belka = zależy od dystansu
🔴 czerwona belka = odrzuć, nie marnuj czasu

JAK DZIAŁA
Aplikacja wykrywa nowe oferty zleceń z apek kurierskich (Uber Driver, Wolt, Glovo, Bolt Food), wylicza opłacalność na podstawie Twoich własnych progów i pokazuje wynik jako kolorową belkę na ekranie. Obok widzisz kwotę, dystans i czas - wszystko w jednym miejscu, żebyś zdecydował od razu.

DLA WIELU APEK NARAZ (MULTI-APPING)
Pracujesz na Uber Eats, Wolt, Glovo i Bolt Food jednocześnie? Jedna belka działa nad wszystkimi. Porównujesz zlecenia z różnych platform i bierzesz to, które realnie się opłaca.

BEZPIECZNE DLA TWOJEGO KONTA
Order Pilot tylko czyta ekran, żeby rozpoznać ofertę. NIE loguje się do Twojego konta kuriera, NIE używa API platform, NIE klika i NIE akceptuje zleceń za Ciebie. Niczego nie automatyzuje - tylko doradza, a decyzja zawsze należy do Ciebie.

DARMOWA I PRYWATNA
- Całkowicie za darmo, bez wersji płatnej i bez ukrytych opłat
- Wszystko działa lokalnie na Twoim telefonie
- Zero połączeń z internetem - możesz wyłączyć dane mobilne, a apka nadal działa
- Zero kont, zero logowania, zero rejestracji
- Zero śledzenia, zero reklam, zero analityki
- Order Pilot nie wysyła żadnych danych nigdzie

DLA KOGO
- Kurierzy Uber Eats (apka Uber Driver)
- Kurierzy Wolt
- Kurierzy Glovo
- Kurierzy Bolt Food
- Kurierzy rowerowi, na hulajnodze, skuterze i autem
- Kurierzy, którzy pracują na kilku aplikacjach naraz

JĘZYKI
Order Pilot dostępny po polsku, ukraińsku, rosyjsku i angielsku. Aplikacja przeznaczona dla kurierów pracujących w Polsce - wylicza w złotówkach (PLN).

DLACZEGO WARTO
Przy nadpodaży kurierów i spadających stawkach każda minuta na nieopłacalnym zleceniu to stracone pieniądze. Zamiast zgadywać, czy zlecenie za 9 zł i 5 km się opłaca, widzisz od razu: to 12 zł/h - odrzuć, a tamto 48 zł/h - bierz. Mniej jeżdżenia za darmo, więcej realnego zł/h na koniec dnia.

UPRAWNIENIA
- Accessibility Service: do odczytu ofert z aplikacji kurierskich
- Wyświetlanie nad innymi aplikacjami: do pokazania kolorowej belki
- Foreground Service: do działania w tle podczas pracy
- Brak dostępu do kontaktów, lokalizacji, kamery i mikrofonu

ZASTRZEŻENIA
OrderPilot nie jest afiliowany z, sponsorowany przez ani powiązany z firmami Uber, Wolt, Glovo czy Bolt. To niezależne narzędzie stworzone dla kurierów. Wyliczenia opłacalności (zł/h, zł/km) są szacunkowe i mają charakter informacyjny - nie stanowią porady finansowej. Każdy kurier sam podejmuje decyzję o przyjęciu zlecenia.

WYMAGANIA
Android 8.0 (API 26) lub nowszy.

Pobierz Order Pilot i sprawdzaj opłacalność zleceń, zanim je przyjmiesz.
```

---

## ANGIELSKI - patrz gwiazdka w rekomendacji (wgrywać tylko przy dystrybucji ograniczonej do Polski) (en-US)

### Tytuł aplikacji - 25/30 znaków
```
OrderPilot: courier PLN/h
```
Wariant alternatywny (gdyby główny nie zadziałał w eksperymencie) - 28/30 znaków:
```
OrderPilot: delivery courier
```

### Krótki opis - 78/80 znaków
```
Is this order worth it? PLN/h and PLN/km for Uber Eats, Wolt, Glovo, Bolt Food
```

### Pełny opis - 3101/4000 znaków
```
Order Pilot is a free app for Uber Eats, Wolt, Glovo and Bolt Food couriers working in Poland. OrderPilot shows what every order is worth in PLN per hour and PLN per km the moment it appears - a green, yellow or red bar over your courier app tells you in 5 seconds whether the offer pays off. Stop doing the maths in your head.

IS THIS ORDER WORTH IT?
What you earn per hour and per kilometre is all that matters. Order Pilot turns the payout, distance and time into PLN/h and PLN/km and tells you right away:
🟢 green bar = take it
🟡 yellow bar = depends on the distance
🔴 red bar = decline, don't waste your time

HOW IT WORKS
The app spots new order offers in your courier apps (Uber Driver, Wolt, Glovo, Bolt Food), calculates profitability against your own thresholds and shows the result as a coloured bar on top of the screen. Next to it you see the payout, distance and time - everything in one place, so you can decide instantly.

MADE FOR MULTI-APPING
Running Uber Eats, Wolt, Glovo and Bolt Food at the same time? One bar works on top of all of them. Compare orders across platforms and take the one that actually pays.

SAFE FOR YOUR ACCOUNT
Order Pilot only reads the screen to recognise the offer. It does NOT log in to your courier account, does NOT use platform APIs, does NOT tap or accept orders for you. It automates nothing - it only advises, and the decision is always yours.

FREE AND PRIVATE
- Completely free, no paid version, no hidden fees
- Everything runs locally on your phone
- No internet connection - turn mobile data off and the app keeps working
- No account, no login, no sign-up
- No tracking, no ads, no analytics
- Order Pilot sends no data anywhere

WHO IT IS FOR
- Uber Eats couriers (Uber Driver app)
- Wolt couriers
- Glovo couriers
- Bolt Food couriers
- Couriers delivering by bike, e-scooter, scooter or car
- Couriers who work on several apps at once

LANGUAGES
Order Pilot is available in English, Polish, Ukrainian and Russian. The app is built for couriers working in Poland - it calculates in Polish złoty (PLN).

WHY IT MATTERS
With more couriers on the road and falling rates, every minute spent on a bad order is money lost. Instead of guessing whether a 9 PLN order over 5 km pays off, you see it instantly: that one is 12 PLN/h - decline, this one is 48 PLN/h - take it. Less riding for free, more real earnings per hour at the end of the day.

PERMISSIONS
- Accessibility Service: to read order offers from courier apps
- Display over other apps: to show the coloured bar
- Foreground Service: to keep working in the background during your shift
- No access to contacts, location, camera or microphone

DISCLAIMER
OrderPilot is not affiliated with, sponsored by or connected to Uber, Wolt, Glovo or Bolt in any way. It is an independent tool made for couriers. Profitability figures (PLN/h, PLN/km) are estimates for information only and are not financial advice. Every courier makes their own decision about accepting an order.

REQUIREMENTS
Android 8.0 (API 26) or newer.

Get Order Pilot and check what an order is worth before you accept it.
```

---

## UKRAIŃSKI - priorytet #2 (uk)

### Tytuł aplikacji - 25/30 znaków
```
OrderPilot: кур'єр зл/год
```
Wariant alternatywny (gdyby główny nie zadziałał w eksperymencie) - 29/30 znaków:
```
OrderPilot: кур'єр Glovo Wolt
```

### Krótki opis - 78/80 znaków
```
Чи вигідне замовлення? зл/год і зл/км для кур'єра Uber Eats, Wolt, Glovo, Bolt
```

### Pełny opis - 3141/4000 znaków
```
Order Pilot - безкоштовний застосунок для кур'єрів Uber Eats, Wolt, Glovo та Bolt Food у Польщі. OrderPilot показує, скільки зл/год і зл/км варте кожне замовлення, у момент, коли воно з'являється - зелена, жовта або червона панель поверх кур'єрського застосунку за 5 секунд підказує, чи вигідна пропозиція. Досить рахувати в голові.

ЧИ ВИГІДНЕ ЦЕ ЗАМОВЛЕННЯ?
Скільки ти заробляєш за годину і скільки за кілометр - це єдине, що має значення. Order Pilot переводить суму, відстань і час у зл/год та зл/км і одразу підказує:
🟢 зелена панель = бери
🟡 жовта панель = залежить від відстані
🔴 червона панель = відмовляйся, не витрачай час

ЯК ЦЕ ПРАЦЮЄ
Застосунок помічає нові пропозиції замовлень у кур'єрських застосунках (Uber Driver, Wolt, Glovo, Bolt Food), рахує вигідність за твоїми власними порогами і показує результат кольоровою панеллю поверх екрана. Поруч бачиш суму, відстань і час - усе в одному місці, щоб вирішити одразу.

КІЛЬКА ЗАСТОСУНКІВ ОДНОЧАСНО (МУЛЬТИАПІНГ)
Працюєш на Uber Eats, Wolt, Glovo і Bolt Food водночас? Одна панель працює поверх усіх. Порівнюєш замовлення з різних платформ і береш те, що справді вигідне.

БЕЗПЕЧНО ДЛЯ ТВОГО АКАУНТА
Order Pilot лише читає екран, щоб розпізнати пропозицію. НЕ входить у твій акаунт кур'єра, НЕ використовує API платформ, НЕ натискає і НЕ приймає замовлення за тебе. Нічого не автоматизує - лише підказує, а рішення завжди за тобою.

БЕЗКОШТОВНО І ПРИВАТНО
- Повністю безкоштовно, без платної версії і прихованих оплат
- Усе працює локально на твоєму телефоні
- Жодних з'єднань з інтернетом - можеш вимкнути мобільні дані, застосунок далі працює
- Без акаунта, без логіна, без реєстрації
- Без стеження, без реклами, без аналітики
- Order Pilot нікуди не надсилає жодних даних

ДЛЯ КОГО
- Кур'єри Uber Eats (застосунок Uber Driver)
- Кур'єри Wolt
- Кур'єри Glovo
- Кур'єри Bolt Food
- Кур'єри на велосипеді, самокаті, скутері та авто
- Кур'єри, які працюють на кількох платформах одночасно

МОВИ
Order Pilot доступний українською, польською, російською та англійською. Застосунок створений для кур'єрів, які працюють у Польщі - рахує у злотих (PLN).

ЧОМУ ЦЕ ВАЖЛИВО
Коли кур'єрів багато, а ставки падають, кожна хвилина на невигідному замовленні - це втрачені гроші. Замість вгадувати, чи вигідне замовлення за 9 зл і 5 км, одразу бачиш: це 12 зл/год - відмовляйся, а те 48 зл/год - бери. Менше їзди задарма, більше реальних злотих за годину наприкінці дня.

ДОЗВОЛИ
- Accessibility Service: для зчитування пропозицій з кур'єрських застосунків
- Показ поверх інших застосунків: для кольорової панелі
- Foreground Service: для роботи у фоні під час зміни
- Без доступу до контактів, геолокації, камери та мікрофона

ЗАСТЕРЕЖЕННЯ
OrderPilot не афілійований, не спонсорований і жодним чином не пов'язаний з компаніями Uber, Wolt, Glovo чи Bolt. Це незалежний інструмент, створений для кур'єрів. Розрахунки вигідності (зл/год, зл/км) є орієнтовними та мають інформаційний характер - це не фінансова порада. Рішення про прийняття замовлення завжди ухвалює кур'єр.

ВИМОГИ
Android 8.0 (API 26) або новіший.

Завантаж Order Pilot і перевіряй вигідність замовлень, перш ніж їх приймати.
```

---

## ROSYJSKI - priorytet #3 (ru)

### Tytuł aplikacji - 29/30 znaków
```
OrderPilot: курьер зл/ч зл/км
```
Wariant alternatywny (gdyby główny nie zadziałał w eksperymencie) - 29/30 znaków:
```
OrderPilot: курьер Glovo Wolt
```

### Krótki opis - 76/80 znaków
```
Выгоден ли заказ? зл/ч и зл/км для курьера Uber Eats, Wolt, Glovo, Bolt Food
```

### Pełny opis - 3191/4000 znaków
```
Order Pilot - бесплатное приложение для курьеров Uber Eats, Wolt, Glovo и Bolt Food в Польше. OrderPilot показывает, сколько зл/ч и зл/км стоит каждый заказ, в момент, когда он приходит - зелёная, жёлтая или красная панель поверх экрана курьерского приложения за 5 секунд подсказывает, выгодно ли предложение. Хватит считать в уме.

ВЫГОДЕН ЛИ ЭТОТ ЗАКАЗ?
Сколько ты зарабатываешь в час и сколько за километр - это единственное, что имеет значение. Order Pilot переводит сумму, расстояние и время в зл/ч и зл/км и сразу подсказывает:
🟢 зелёная панель = бери
🟡 жёлтая панель = зависит от расстояния
🔴 красная панель = отказывайся, не трать время

КАК ЭТО РАБОТАЕТ
Приложение замечает новые предложения заказов в курьерских приложениях (Uber Driver, Wolt, Glovo, Bolt Food), считает выгодность по твоим собственным порогам и показывает результат цветной панелью поверх экрана. Рядом видишь сумму, расстояние и время - всё в одном месте, чтобы решить сразу.

НЕСКОЛЬКО ПРИЛОЖЕНИЙ ОДНОВРЕМЕННО (МУЛЬТИАППИНГ)
Работаешь на Uber Eats, Wolt, Glovo и Bolt Food одновременно? Одна панель работает поверх всех. Сравниваешь заказы с разных платформ и берёшь тот, который действительно выгоден.

БЕЗОПАСНО ДЛЯ ТВОЕГО АККАУНТА
Order Pilot только читает экран, чтобы распознать предложение. НЕ входит в твой аккаунт курьера, НЕ использует API платформ, НЕ нажимает и НЕ принимает заказы за тебя. Ничего не автоматизирует - только подсказывает, а решение всегда за тобой.

БЕСПЛАТНО И ПРИВАТНО
- Полностью бесплатно, без платной версии и скрытых платежей
- Всё работает локально на твоём телефоне
- Никаких соединений с интернетом - можешь выключить мобильные данные, приложение продолжит работать
- Без аккаунта, без логина, без регистрации
- Без слежки, без рекламы, без аналитики
- Order Pilot никуда не отправляет никакие данные

ДЛЯ КОГО
- Курьеры Uber Eats (приложение Uber Driver)
- Курьеры Wolt
- Курьеры Glovo
- Курьеры Bolt Food
- Курьеры на велосипеде, самокате, скутере и авто
- Курьеры, работающие на нескольких платформах сразу

ЯЗЫКИ
Order Pilot доступен на русском, украинском, польском и английском. Приложение создано для курьеров, работающих в Польше - считает в злотых (PLN).

ПОЧЕМУ ЭТО ВАЖНО
Когда курьеров много, а ставки падают, каждая минута на невыгодном заказе - потерянные деньги. Вместо того чтобы гадать, выгоден ли заказ за 9 зл и 5 км, сразу видишь: этот 12 зл/ч - отказывайся, а тот 48 зл/ч - бери. Меньше поездок даром, больше реальных злотых в час в конце дня.

РАЗРЕШЕНИЯ
- Accessibility Service: для чтения предложений из курьерских приложений
- Показ поверх других приложений: для цветной панели
- Foreground Service: для работы в фоне во время смены
- Нет доступа к контактам, геолокации, камере и микрофону

ОГОВОРКИ
OrderPilot не аффилирован, не спонсируется и никак не связан с компаниями Uber, Wolt, Glovo или Bolt. Это независимый инструмент, созданный для курьеров. Расчёты выгодности (зл/ч, зл/км) являются ориентировочными и носят информационный характер - это не финансовая консультация. Решение о принятии заказа всегда принимает курьер.

ТРЕБОВАНИЯ
Android 8.0 (API 26) или новее.

Скачай Order Pilot и проверяй выгодность заказов, прежде чем их принимать.
```

---

## CO WKLEIĆ GDZIE W PLAY CONSOLE - krok po kroku (po ludzku)

Całość zajmie około 20-30 minut. Nie da się nic popsuć - każdą zmianę można cofnąć, a apka sama w ogóle się nie zmienia (to tylko opis w sklepie).

**Krok 1. Wejdź do konsoli.**
Otwórz play.google.com/console w przeglądarce, zaloguj się i kliknij aplikację **OrderPilot**.

**Krok 2. Otwórz główny listing.**
W menu po lewej znajdź **"Rozwój"** (Grow) -> **"Obecność w sklepie"** (Store presence) -> **"Główne informacje o aplikacji"** (Main store listing). Nazwy mogą się minimalnie różnić zależnie od wersji konsoli - szukaj strony, na której widzisz pola "Nazwa aplikacji", "Krótki opis", "Pełny opis".

**Krok 3. Wklej polską wersję.**
Upewnij się, że u góry wybrany język to **polski (pl-PL)**. Potem:
- pole **"Nazwa aplikacji"** -> na razie ZOSTAW "OrderPilot" (tytuł zmieniamy eksperymentem - krok 7); jeśli świadomie pomijasz eksperyment, wklej tu tytuł PL z tego pliku
- pole **"Krótki opis"** -> wklej krótki opis PL (ten z sekcji POLSKI)
- pole **"Pełny opis"** -> wklej pełny opis PL (cały blok, od "Order Pilot to darmowa aplikacja..." do "...zanim je przyjmiesz.")
Kliknij **"Zapisz"** na dole.

**Krok 4. Dodaj języki: ukraiński i rosyjski (i warunkowo angielski).**
Na tej samej stronie u góry jest przycisk **"Zarządzaj tłumaczeniami"** (Manage translations) -> **"Dodaj własne teksty tłumaczeń"**. Zaznacz: **українська (uk)** i **русский (ru)**. Angielski (en-US) dodaj tylko, jeśli w kroku 6 potwierdzisz, że apka jest dystrybuowana tylko w Polsce.

**Krok 5. Wklej wersje językowe.**
U góry strony przełącz język na **ukraiński** i wklej z sekcji UKRAIŃSKI: nazwę aplikacji, krótki opis, pełny opis. Zapisz. Przełącz na **rosyjski** - to samo z sekcji ROSYJSKI. Zapisz. (Ewentualnie angielski - sekcja ANGIELSKI.) Uwaga: w tłumaczeniach pole "Nazwa aplikacji" MOŻNA ustawić inne per język - wklej tytuły UK/RU z tego pliku.

**Krok 6. Sprawdź dwie rzeczy przy okazji (checklista, nie treść):**
- **Kraje dystrybucji:** "Wydania" -> "Produkcja" -> zakładka "Kraje/regiony". Jeśli jest tam tylko Polska - możesz spokojnie wgrać też listing EN. Jeśli cały świat - albo ogranicz do Polski, albo wstrzymaj EN.
- **Grafika promocyjna (feature graphic) 1024x500 px:** na stronie głównego listingu, sekcja grafik. Wg planu jej brakowało - bez niej listing wygląda ubogo w części miejsc w sklepie. Pamiętaj: na grafikach i zrzutach ekranu NIE może być logo ani prawdziwego interfejsu Uber/Wolt/Glovo/Bolt - tylko belka OrderPilot na neutralnym tle.

**Krok 7. Eksperyment z tytułem (zalecane).**
W menu po lewej znajdź **"Eksperymenty z informacjami o aplikacji"** (Store listing experiments) -> "Utwórz eksperyment" -> wybierz główny listing, język polski, atrybut **"Nazwa aplikacji"**. Wariant B = tytuł PL z tego pliku. Podział ruchu 50/50. Uruchom. Po 7-14 dniach zajrzyj: jeśli Console wskaże zwycięzcę - kliknij "Zastosuj". Jeśli mówi "za mało danych" (przy ~0 instalacjach bardzo możliwe) - zakończ eksperyment i ustaw nowy tytuł ręcznie w kroku 3.

**Krok 8. Wyślij do sprawdzenia.**
Po zapisaniu wszystkiego Console pokaże baner / stronę **"Omówienie publikowania"** (Publishing overview) -> kliknij **"Wyślij zmiany do sprawdzenia"**. Recenzja Google trwa zwykle od kilku godzin do kilku dni. Teksty pojawią się w sklepie po zatwierdzeniu.

**Krok 9. Po 7 dniach od publikacji.**
W Console wejdź w statystyki pozyskiwania (Acquisition -> Search terms / Wyszukiwane hasła) i sprawdź, czy zaczynają wpadać hasła "kurier" i "order pilot". To znak, że nowy listing pracuje.

**Czego NIE robić przy tej okazji:**
- NIE zmieniaj kategorii aplikacji (dziś: Business; w analizie była propozycja Productivity) - to osobna decyzja, nie mieszać jej z podmianą tekstów, żeby wiedzieć, co zadziałało.
- NIE wpisuj nigdzie obietnic typu "zero ryzyka bana", ocen, liczby pobrań ani funkcji z wersji 1.1.0, dopóki nie wyjdzie.
- NIE wklejaj tekstów z innych plików/starych wersji - tam są długie myślniki i stare obietnice; jedyne źródło = ten plik.

---

*Plik wygenerowany skryptem (build_listing.py) 2026-07-05. Wszystkie długości policzone automatycznie (len w Pythonie, znaki unicode). Walidacja: limity pól, zero długich myślników, zakazane frazy w tytułach, brak obietnicy "zero bana" - PASS.*
