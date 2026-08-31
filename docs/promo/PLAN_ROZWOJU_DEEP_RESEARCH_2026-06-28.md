# OrderPilot - Plan rozwoju (głęboki research biznesowy)
> **Data:** 2026-06-28 · **Cel:** sprawić, by kurierzy realnie pobierali i UŻYWALI OrderPilota.
>
> **Jak powstało:** wieloagentowy research (26 agentów, ~1,6 mln tokenów): konkurencja PL+global, YouTube, fora kurierskie, społeczności PL/UA, dystrybucja, ASO, monetyzacja, rynek, twórcy → weryfikacja kluczowych faktów → 3 gotowce → synteza. Sekcja "Głosy kurierów" dobita osobnym agentem.
>
> **Sytuacja:** apka LIVE w Google Play (v1.0.5) od 2026-05-23, ~miesiąc bez promocji = ~zero pobrań. Założyciel nietechniczny, budżet ~0, teraz ZDALNIE z Hiszpanii, w PL za ~2 tygodnie.

---
## SPIS TREŚCI
- CZĘŚĆ I - Plan rozwoju (synteza, 9 sekcji)
- CZĘŚĆ II - Gotowce do wdrożenia (ASO listing, lista grup/społeczności, lista twórców)
- CZĘŚĆ III - Głosy kurierów (fora/Reddit): bóle i wishlist
- CZĘŚĆ IV - Weryfikacja kluczowych twierdzeń
- CZĘŚĆ V - Podsumowania wątków researchu
- CZĘŚĆ VI - Źródła

---

# CZĘŚĆ I - PLAN ROZWOJU (synteza)

# Plan rozwoju OrderPilot - od zera pobrań do realnych użytkowników

Dokument gotowy do wdrożenia. Pisany prosto, ale z konkretami i liczbami. Pierwsze 2 tygodnie robisz ZDALNIE z Hiszpanii, potem dokładasz robotę w Polsce.

---

## 1. Brutalnie szczera diagnoza - dlaczego teraz zero pobrań

Twój problem to NIE produkt. Produkt jest dobry i ma realną lukę. Masz trzy różne problemy i trzeba je rozdzielić, bo każdy leczy się czym innym:

**Problem dystrybucji (90% Twojej winy za zero pobrań) - NAJWAŻNIEJSZY.**
Apka jest od miesiąca w Google Play i nikt o niej nie powiedział ani jednemu kurierowi. Zero postów w grupach, zero filmów, zero rozmów. Niszowe narzędzie samo z siebie nie zbiera pobrań - ktoś musi je pokazać grupie docelowej. To jest cała przyczyna. Apka jest niewidzialna nie dlatego, że źle wygląda w sklepie, tylko dlatego, że jej nie ma w miejscach, gdzie siedzą kurierzy (Telegram, Discord, grupy FB, YouTube).

**Problem widoczności w samym sklepie (wtórny, ale realny).**
Nawet jeśli ktoś zacznie szukać, nie znajdzie. Tytuł brzmi tylko "OrderPilot" - nie zawiera ŻADNEGO słowa, którego kurier szuka ("kurier", "Glovo", "Wolt", "zł/h"). W Google Play tytuł ma najwyższą wagę w wyszukiwarce. Do tego nie ma jeszcze osobnych listingów po ukraińsku i rosyjsku, a to ogromna część rynku. ALE uwaga: ASO Cię nie uratuje samo, bo w Polsce popyt na "asystenta kuriera" jeszcze nie istnieje - nikt nie wie, że takiej apki w ogóle można szukać. Najpierw trzeba ten popyt stworzyć (dystrybucja), a ASO ma go dopiero przechwycić.

**Problem produktu (najmniejszy, ale jest jeden krytyczny punkt).**
Sam produkt jest OK i wypełnia realną lukę. Jedyne dwa realne ryzyka produktowe to: (a) niezawodność odczytu ekranu - to zabójca nr 1 recenzji wszystkich konkurentów (Mystro, Stride: "przestało działać po aktualizacji"); (b) brak haka, który sprawia, że kurier WRACA do apki codziennie. Dziś apka tylko "pokazuje belkę" - to za mało, żeby zostać zainstalowaną na stałe. O tym w sekcji 5.

**Wniosek jednym zdaniem:** masz dobry produkt schowany w szafie. Cała robota najbliższych tygodni to wyjąć go z szafy i pokazać kurierom tam, gdzie już są - a nie czekać, aż sami znajdą.

---

## 2. Pozycjonowanie

**Jedno zdanie (co to jest i dla kogo):**
> OrderPilot to darmowa apka dla kurierów dowozu jedzenia w Polsce (Uber Eats, Wolt, Glovo, Bolt Food), która w sekundę pokazuje kolorową belką, czy zlecenie się opłaca - ile to zł/h i zł/km - bez logowania do konta i bez ryzyka bana.

**3 główne argumenty (vs konkurencja), które wbijasz wszędzie:**

1. **ZERO ryzyka bana.** To Twój najsilniejszy argument i emocjonalny przycisk u kurierów. OrderPilot tylko czyta ekran - nie loguje się do konta, nie używa API platform, nie klika za Ciebie. W USA apka Para (pokazywała ukryty napiwek) została zablokowana przez DoorDash/Uber/Lyft właśnie dlatego, że łączyła się z kontem. Mystro do dziś walczy z Uberem o nakładkę i został wykopany z Lyfta. Ty świadomie tego nie robisz - i to jest nie do podrobienia w komunikacji.

2. **Darmowa i prywatna - na zawsze.** Konkurenci za granicą biorą realne pieniądze: Gridwise 15 USD/mc, Mystro 19 USD/mc, Solo 10-20 USD/mc, GigU ~7 USD/mc. Ty dajesz to za 0 zł, bez konta, bez reklam, bez wysyłania czegokolwiek na serwer. Apka, która zarabiała na byciu darmową (Stride), dobiła do 2,6 mln użytkowników. Darmowość to Twoja dźwignia wzrostu, nie słabość.

3. **Jedna belka nad wszystkimi apkami naraz (multi-apping).** Kurierzy w Polsce standardowo jeżdżą na 2-3 apkach jednocześnie (Glovo + Bolt + Wolt) i wybierają najlepsze zlecenie. OrderPilot działa nad wszystkimi - jedna decyzja "brać czy nie" ponad całym ekranem. To naturalna przewaga nakładki.

**Czy jest realna luka na rynku PL? TAK, potwierdzona w 4 językach (PL, UA, RU, ES).**
Nie istnieje ŻADNA lokalna apka-nakładka oceniająca opłacalność pojedynczego zlecenia dla Glovo/Wolt/Bolt Food. To, co jest w Polsce, to coś zupełnie innego: kalkulatory rozliczeniowe partnerów flotowych (liczą wypłatę PO fakcie) i poradniki o zarobkach. Zagraniczne nakładki (Mystro, Gridwise) są US-only, liczą w dolarach i obsługują Uber/Lyft/DoorDash, nie polskie platformy. Jesteś pierwszy i jedyny - ale bariera wejścia jest niska, więc liczy się czas. Trzeba szybko zostać "tym znanym" wśród kurierów, zanim ktoś (np. GigU) wejdzie na PL.

---

## 3. ASO - masz gotowy listing, oto co zmienić NAJPIERW

Cały gotowy listing (tytuł, opisy, słowa kluczowe PL/UA/RU, układ zrzutów) jest już napisany i policzony w deliverables: **"Listing Google Play dla OrderPilot - gotowiec ASO"**. Nie pisz tego od nowa - wklejaj stamtąd.

**Kolejność zmian (od najmocniejszej):**

1. **Tytuł (efekt w ~48h, najsilniejszy pojedynczy ruch).** Zmień z "OrderPilot" na: `OrderPilot: kurier zł/h zł/km` (29 znaków). Łapie słowo "kurier" + dwie kluczowe jednostki. Puść to jako eksperyment (Custom Store Listing w Console), żeby porównać konwersję ze starym tytułem, a nie nadpisuj na ślepo.

2. **Krótki + pełny opis.** Wklej gotowe teksty z deliverable. Pełny opis (3223 znaki) ma już wplecione realnie wyszukiwane frazy ("czy zlecenie się opłaca", "opłacalność", "zł/h i zł/km", "multi-apping"), mocno wyeksponowane "ZERO RYZYKA BANA" i zero długich myślników.

3. **Listingi UA i RU (priorytet #2 i #3).** Zrób OSOBNE listingi po ukraińsku i rosyjsku z ręcznie dobranymi słowami (nie tłumaczenie maszynowe) - frazy кур'єр/курьер, доставка, Glovo заробіток/заработок. To otwiera ogromną grupę kurierów z Ukrainy w PL, gdzie konkurencja jest zerowa. Google indeksuje metadane osobno per język.

4. **Zrzuty ekranu + feature graphic.** Pierwsze 2-3 zrzuty decydują o instalacji (90% userów nie przewija dalej). #1 = realna ZIELONA belka nad apką kuriera + duże "48 zł/h" + podpis "Wiesz w 5 sekund, czy brać zlecenie". Dalej wg deliverable. Dodaj feature graphic 1024x500 (wcześniej go brakowało).

5. **WSTRZYMAJ EN i ES.** Apka liczy w PLN - instalacje z USA/Hiszpanii i tak natychmiast odpadną (zła retencja = niższy ranking w całym sklepie). Trzymaj listingi tylko PL/UA/RU, dopóki apka nie przelicza walut.

**Recenzje na start:** wdróż In-App Review API i pokazuj prośbę o ocenę PO pierwszym zielonym (opłacalnym) zleceniu - kurier jest wtedy najbardziej zadowolony. Poproś imiennie realnych testerów (Marcin, Dominik, Andrij) o 5 gwiazdek + 1-2 zdania po polsku/ukraińsku. Odpowiadaj na KAŻDĄ recenzję w 24h - Google nagradza zaangażowanie i retencję D7.

---

## 4. Dystrybucja - kanały w kolejności, z podziałem ZDALNIE vs W POLSCE

Masz dwie gotowe listy w deliverables, korzystaj z nich:
- **"Lista celów do zasiania apki"** (grupy FB/Telegram/Discord + gotowe szablony postów PL/UA/RU)
- **"Lista twórców kurierskich do outreachu barterowego"** (twórcy + szablony wiadomości PL/EN)

### CO ROBISZ ZDALNIE TERAZ (z Hiszpanii) - 100% online, nie wymaga obecności w PL

**Priorytet 1: Dexterowski (najtańszy strzał o największym zasięgu).**
YouTube ~189 tys., TikTok ~124 tys., Discord "Dexterawka" ~10 tys. kurierów. Robi dokładnie content "ile zarabia kurier / co się opłaca" - to 1:1 Twoja apka. Napisz DM (szablon 3 z deliverable) na kontakt@dexterowski.pl. Równolegle wejdź organicznie na jego Discord jako kurier, kilka dni pomagaj, potem pokaż apkę w wątku porad. To może załatwić Ci pierwszą falę pobrań jednym filmem.

**Priorytet 2: Telegram @glovo_uber_wolt (~3,9-4,7 tys., PL/UA/RU) + czaty miejskie.**
Najważniejszy ogólnopolski czat kurierów. Wejdź jako członek, 2-3 dni tylko pomagaj, potem JEDEN wartościowy post w 3 językach pod sobą (szablon 1). Znajdź też czaty miejskie ("Glovo Wrocław", "Wolt Kraków" itd.) - tam mniej moderacji. Pułapka: @glovoinform (~14 tys.) to Ukraina, nie Polska - tam nie pisać.

**Priorytet 3: Mniejsi twórcy YouTube/TikTok na barter (apka za darmo do testu).**
Zacznij od TIER 2/3 (Deliverka ~1 tys., SwojąDrogą, Reysowaty, Michał Górka, Kurier z Holywood) oraz twórców ukraińskojęzycznych (RazeDen, Pokatun, Serhii Marchenko). Mniejsze kanały szybciej odpiszą i dadzą pierwszy dowód społeczny, którym potem skusisz Dexterowskiego i Yellowboxa (~500 tys.). Format dla nich: "testuję apkę, która mówi, czy zlecenie się opłaca - sprawdzam, czy ma rację".

**Priorytet 4: Grupy FB (PYTAJ admina, FB ostro tępi reklamę).**
Wolna Grupa Niebieskich Kurierów (Wolt), Glovo Polska, Wolt Polska, grupy multi-app. Z prywatnego konta (nie firmowego), najpierw post wartościowy (poradnik "jak w 5 sekund ocenić, czy zlecenie się opłaca"), apka jako narzędzie na końcu. Reguła 90/9/1.

**Priorytet 5: Własny kanał TikTok/Reels/Shorts (faceless, nagranie ekranu).**
3-5 krótkich filmików tygodniowo: belka zmienia kolor nad Glovo/Wolt, hook "bierzesz to za 6 zł i 4 km?". Po PL/UA/EN, każdy z linkiem do Play. Krótkie wideo to dziś najszybsza darmowa dźwignia instalacji apek. To robisz w pełni zdalnie - nagrywasz ekran telefonu.

**Priorytet 6 (zrób raz, działa długo): katalogi i pasywne źródła.**
Dodaj apkę do Kurierpedia (kurierpedia.pl), forum uber-bolt.net, portal dostawca-jedzenia.pl. Plus jednorazowy "launch tydzień" w katalogach apek (BetaList, Indie Hackers, AlternativeTo) dla backlinków i SEO.

### CO ROBISZ DOPIERO W POLSCE (za ~2 tyg.) - wymaga fizycznej obecności

**A. Ulotki/naklejki z kodem QR w strefach kurierskich.**
Wydrukuj i rozłóż pod popularnymi restauracjami Glovo/Wolt, McDonald's/KFC, w strefach odbioru, gdzie kurierzy czekają na zlecenia. Hasło: "Sprawdź, czy zlecenie się opłaca - darmowa apka, nic nie wysyła, zero ryzyka bana". To kanał czysto offline - bez sensu robić go z Hiszpanii.

**B. Rozmowy 1:1 z kurierami na mieście.**
Podejdź do czekających kurierów, pokaż belkę na żywo na swoim telefonie, daj zeskanować QR. Przy okazji zbierasz feedback produktowy - bezcenny. To buduje pierwszych ambasadorów.

**C. Partnerzy flotowi (Flow Apps, MB Partner, City Drive, Eternis).**
Mają pod sobą setki kurierów. Apka im nie zagraża (podnosi zarobki ich ludzi), więc mogą ją polecić. Spotkanie/telefon na żywo działa tu dużo lepiej niż zimny mail z zagranicy.

---

## 5. Roadmapa produktu

Priorytetyzacja na podstawie realnych boli kurierów z researchu. Buduje to Claude/AI, Ty decydujesz "idź / nie idź".

**TERAZ - przed każdą promocją (fundament, bez tego marketing leje wodę w dziurawe wiadro):**

1. **Niezawodność odczytu ekranu - priorytet #1.** To zabójca nr 1 recenzji WSZYSTKICH konkurentów ("przestało działać po aktualizacji"). Zbuduj szybki proces: gdy Glovo/Wolt/Bolt/Uber Eats zmienią wygląd ekranu, wypuszczasz patch w dni, nie tygodnie. Dorzuć prosty mechanizm w apce: jeśli odczyt zawiedzie, kurier może jednym tapnięciem zgłosić zrzut nieczytanego ekranu (lokalnie, do Twojej skrzynki - bez łamania zero-network, bo to świadoma akcja użytkownika).

2. **Niskie zużycie baterii.** Druga najczęstsza skarga na nakładki (Gridwise). Kurier jeździ 6-10h - apka żrąca baterię wyleci po dniu.

**ZARAZ POTEM - hak retencyjny (zamienia "spojrzę raz" w "wracam codziennie") - to też napędza wiralność:**

3. **Lokalny licznik "ile dziś zarobiłeś / zł/h dzisiaj".** Liczony lokalnie z odczytanych zlecen, bez konta, bez wysyłania. To wzór Gridwise/Solo, ale za darmo i prywatnie. Dzięki temu kurier otwiera apkę co dzień, nie tylko raz. Najmocniejsza pojedyncza funkcja retencyjna.

4. **Uwzględnienie godzin szczytu/mnożników w ocenie.** Godziny szczytu i mnożniki stref (x1.3-x1.5) to obsesja kurierów - to samo zlecenie jest warte więcej w porze obiadowej/wieczornej/przy złej pogodzie. Jeśli belka to uwzględni, ocena będzie trafniejsza i kurier bardziej zaufa.

5. **Przycisk "Poleć kumplowi-kurierowi".** Generuje gotowy link do Play + krótki tekst (PL/UA/RU) do wklejenia na czacie. Skoro brak konta uniemożliwia klasyczny system poleceń z nagrodą, postaw na maksymalnie prostą mechanikę dzielenia się. To Twoja wiralność.

6. **Język rosyjski w interfejsie.** Apka ma PL/UA/EN - dużo kurierów pisze i myśli po rosyjsku. RU domyka rynek.

### Polska czy Hiszpania? Zostań przy Polsce. Hiszpanii NIE ruszaj w tym roku.

To nie jest bliska decyzja - dane są jednoznaczne, mimo że fizycznie jesteś w Hiszpanii:

- **Hiszpania zabija rdzeń produktu.** Od 1 lipca 2025 Glovo (dom firmy = Barcelona) przeszedł W PEŁNI na model etatowy (Ley Rider). Kurier-etatowiec nie wybiera swobodnie zlecen - więc cała funkcja "czy wziąć to zlecenie" traci sens. To nie jest problem tłumaczenia, to problem strukturalny.
- **Do tego trzeba lokalizacji w EUR + hiszpańskiego** - duża praca dla rynku, który właśnie zamknął okno na ten typ narzędzia.
- **Polska jest idealnie dopasowana:** apka liczy w PLN (gotowa), rynek dostaw rośnie ~8%/rok (~10 mld zł), model gig (swobodny wybór zleceń) dominuje co najmniej do 2026/2027, ogromna baza kurierów z Ukrainy z silnym word-of-mouth w zamkniętych grupach.

**Co zrobić z pobytem w Hiszpanii:** wykorzystaj go do OBSERWACJI i researchu (pogadaj z repartidores, popatrz jak pracują), ale NIE buduj tam rynku. Cała energia idzie w Polskę.

---

## 6. Monetyzacja

**Decyzja: zostań w 100% darmowy przez najbliższe miesiące. Świadomie.** To nie jest unik - to strategia poparta liczbami.

**Dlaczego teraz zero monetyzacji:**
- Masz zero użytkowników. Monetyzacja czegokolwiek przy zerze to liczenie procentów od zera. Najpierw skala, potem pieniądze.
- Darmowość to Twoja największa dźwignia wzrostu i argument marketingowy. Stride doszedł do 2,6 mln userów właśnie dlatego, że był 100% free. Konkurenci biorą 7-19 USD/mc - Ty bijesz ich samą ceną.
- Prywatność (zero-network, brak reklam, brak sprzedaży danych) jest Twoim wyróżnikiem. Wrzucenie reklam czy sprzedaż danych zniszczyłoby zaufanie i całą narrację "bezpieczna, prywatna apka dla nas kurierów". Tego NIE rób nigdy.

**Kiedy w ogóle wrócić do tematu:** dopiero po przekroczeniu kilku-kilkunastu tysięcy realnych aktywnych użytkowników i ugruntowaniu marki. Wcześniej szkoda energii.

**Jak ewentualnie kiedyś (bez łamania obietnicy zero-network):**
- Opcjonalny "Pro" z funkcjami premium liczonymi LOKALNIE: rozbudowany tracking zarobków, statystyki tygodniowe, podpowiedź najlepszych godzin/stref. Wszystko na telefonie, nic na serwer. To wzór Gridwise/Solo, ale uczciwy.
- NIGDY: reklamy, sprzedaż danych, wymuszanie konta. To są czerwone linie, bo zabijają to, co Cię wyróżnia.
- Ewentualnie później: model jak Stride (zarabiasz na czymś obok, np. afiliacja z partnerem flotowym/ubezpieczeniem, a apka zostaje darmowa). Ale to muzyka przyszłości.

**Dodatkowy argument za "najpierw skala":** dyrektywa UE 2024/2831 (domniemanie zatrudnienia) wdrażana w PL do grudnia 2026 może z czasem ograniczyć model gig. Okno na zbudowanie bazy userów jest TERAZ, do ~2027. Najpierw zgarnij użytkowników i nawyk, dopiero potem myśl o pieniądzach.

---

## 7. Metryki sukcesu + jak mierzyć bez serwera

**Napięcie, które masz na stole:** obiecałeś zero-network (apka nic nie wysyła) - to Twój wyróżnik. Ale jednocześnie chcesz wiedzieć, ile osób realnie używa apki. To się gryzie i trzeba świadomie wybrać, co poświęcasz.

**Co dostajesz ZA DARMO z Google Play Console (bez łamania zero-network):**
- **Instalacje i odinstalowania** - ile osób pobrało, ile usunęło.
- **Retencja D1/D7/D30** - ilu wraca po dniu/tygodniu/miesiącu (Console to liczy z systemu, nie z Twojej apki). To jest Twoja NAJWAŻNIEJSZA metryka - Google nagradza nią ranking, a ona mówi, czy apka jest realnie używana, czy odinstalowywana po dniu.
- **Search terms** (Acquisition) - na jakie frazy ludzie Cię znajdują. Po 7 dniach od zmiany ASO sprawdź, czy "kurier" i "order pilot" zaczynają wpadać.
- **Oceny i recenzje** - jakościowy sygnał, co działa, a co się psuje.

**Czego Console NIE da, a chciałbyś wiedzieć:** ile zleceń apka oceniła, ile razy belka się pokazała, czy kurier faktycznie z niej korzysta w trasie (aktywne użycie wewnątrz apki). Tego nie zmierzysz bez telemetrii, a telemetria łamie zero-network.

**Rekomendacja (jak rozwiązać napięcie):**
1. **Domyślnie trzymaj zero-network** - to Twój wyróżnik, nie poświęcaj go dla wygody pomiaru.
2. **Retencja D7 z Console + tempo recenzji to Twój kompas.** Jeśli ludzie wracają po tygodniu i piszą dobre recenzje - apka działa, nie potrzebujesz więcej.
3. **Jakościowy feedback zamiast telemetrii:** rozmowy z kurierami, komentarze w grupach, odpowiedzi twórców. Przy Twojej skali to powie więcej niż wykresy.
4. **Jeśli kiedyś koniecznie będziesz chciał liczyć aktywne użycie** - zrób to UCZCIWIE: jeden ekran przy pierwszym uruchomieniu "zgadzasz się na anonimową, opcjonalną statystykę użycia? (możesz odmówić, apka działa tak samo)". Domyślnie WYŁĄCZONE. To jedyny sposób, który nie łamie obietnicy - bo user świadomie wybiera. Ale na teraz: nie warto, skup się na Console.

**Konkretne cele (kamienie milowe):**
- Tydzień 4: pierwsze ~100-300 instalacji + ≥5 prawdziwych recenzji (4-5 gwiazdek).
- Miesiąc 2-3: ~1000+ instalacji, retencja D7 powyżej 20%, min. 1 film u twórcy.
- Sygnał "działa": retencja D7 rośnie, a nie spada (to znaczy, że produkt zostaje na telefonach).

---

## 8. Konkretny plan 4 tygodni

Podział: **[AI]** = robi Claude/AI (Ty tylko zatwierdzasz/wklejasz), **[K]** = robi Krzysztof osobiście.

### TYDZIEŃ 1 - fundament ASO + cichy wjazd do społeczności (ZDALNIE)
- **[AI]** Finalizacja całego listingu PL z gotowca (tytuł, opisy, słowa kluczowe). Przygotowanie tekstów listingów UA i RU.
- **[K]** Wklejenie listingu do Google Play Console, ustawienie nowego tytułu jako eksperyment, wysłanie do review. (5 kroków z instrukcji w deliverable.)
- **[AI]** Przygotowanie podpisów do zrzutów + treści na feature graphic. Wygenerowanie/złożenie grafik (jeśli dasz materiały - zrzut belki).
- **[K]** Założenie/dopucowanie kont: wejście jako kurier na Discord Dexterawka i Telegram @glovo_uber_wolt. Przez ten tydzień TYLKO obserwuj i pomagaj, nie reklamuj.
- **[K]** Dołączenie do 3-4 grup FB i znalezienie 2-3 czatów miejskich Telegram.
- **[AI]** Przygotowanie wszystkich szablonów postów spersonalizowanych pod konkretne miejsca (PL/UA/RU).

### TYDZIEŃ 2 - pierwsze uderzenie outreach + własny content (ZDALNIE)
- **[K]** DM do Dexterowskiego (szablon 3) - najważniejszy strzał. Plus 10-15 mniejszych twórców (TIER 2/3 + ukraińscy) z gotowych szablonów.
- **[AI]** Personalizacja każdej wiadomości do twórcy pod jego konkretny film (to drastycznie podnosi odsetek odpowiedzi).
- **[K]** Po kilku dniach członkostwa: JEDEN wartościowy post w każdym miejscu (Telegram, Discord, grupy FB) w odpowiednim języku. Na czatach mieszanych PL+UA+RU pod sobą.
- **[K]** Nagranie 3-5 krótkich filmików (ekran telefonu, belka w akcji). **[AI]** montaż/podpisy/hooki + opublikowanie na własnym TikTok/Reels/Shorts z linkiem.
- **[AI]** Dodanie apki do katalogów (Kurierpedia, dostawca-jedzenia.pl, uber-bolt.net, BetaList, AlternativeTo).
- **[K]** Poproszenie testerów (Marcin, Dominik, Andrij) o pierwsze recenzje.
- **[K+AI]** Odpowiadanie na KAŻDĄ recenzję i komentarz w 24h.

### TYDZIEŃ 3 - wjazd do Polski: offline + flota (W POLSCE)
- **[AI]** Zaprojektowanie ulotki/naklejki z kodem QR (PL po jednej stronie, UA/RU po drugiej) gotowej do druku.
- **[K]** Druk + rozłożenie ulotek/naklejek w strefach kurierskich (pod McDonald's/KFC, popularne restauracje Glovo/Wolt, strefy odbioru) w swoim mieście.
- **[K]** Rozmowy 1:1 z czekającymi kurierami - pokaz belki na żywo, QR do zeskanowania. Zbieranie feedbacku.
- **[K]** Kontakt z 2-3 partnerami flotowymi (Flow Apps, MB Partner, City Drive, Eternis) - na żywo/telefon, propozycja polecenia apki ich kurierom.
- **[AI]** Wdrożenie poprawek produktowych z feedbacku z trasy (drobne fixy odczytu, baterii).

### TYDZIEŃ 4 - dokręcenie, pomiar, iteracja (W POLSCE)
- **[K]** Druga fala outreach do twórców - tym razem z pierwszym dowodem społecznym (recenzje, screeny belki). Ponowny strzał do Yellowboxa (~500 tys.) i Dexterowskiego, jeśli nie odpisali.
- **[AI]** Analiza Console (instalacje, retencja D7, Search terms) + rekomendacja, co iterować w ASO i produkcie.
- **[K]** Kolejna seria własnych filmików + posty w nowych czatach miejskich.
- **[AI]** Przygotowanie funkcji retencyjnej (lokalny licznik "ile dziś zarobiłeś") do wdrożenia w miesiącu 2.
- **[K]** Decyzja "idź / nie idź" co do kolejnego miesiąca na podstawie liczb.

---

## 9. Największe ryzyka i co z nimi

1. **Aktualizacja Glovo/Wolt/Bolt/Uber Eats lub Androida łamie odczyt ekranu (NAJWIĘKSZE ryzyko).**
To zabiło recenzje wszystkim konkurentom. Reakcja: traktuj niezawodność jako priorytet #1 produktowy. Zbuduj szybki proces łapania zmian ekranów i wypuszczania patcha w DNI, nie tygodnie. Dorzuć w apce jednoprzyciskowe zgłaszanie nieczytanego ekranu. Jeśli to zaniedbasz, każda fala pobrań zamieni się w falę 1-gwiazdkowych recenzji.

2. **Ban / wyrzucenie z grup za reklamę.**
Grupy FB i część Telegramów ostro tępią reklamę; niektórzy admini Telegrama każą płacić za post. Reakcja: zawsze najpierw bądź członkiem i pomagaj (reguła 90/9/1), pytaj admina o zgodę, mów "zrobiłem to dla nas, nie sprzedaję". Postuj z prywatnego konta-kuriera, nie firmowego. Korzystaj z gotowych szablonów, które brzmią jak realny kurier. Influencer (Dexterowski) jest bezpieczniejszy niż masowe postowanie.

3. **Sezonowość i nadpodaż kurierów.**
Stawki spadają (lato 2024 dzienne zarobki schodziły poniżej 100 zł przez napływ kurierów). To paradoksalnie działa NA Twoją korzyść: im trudniej zarobić, tym bardziej kurier chce wiedzieć, które zlecenie się opłaca. Przekuj to w komunikat: "przy nadpodaży nie marnuj czasu na nieopłacalne kursy". Ale pamiętaj, że zimą/przy gorszej pogodzie aktywność rośnie - to dobry moment na promocję.

4. **Zmiana prawa UE (dyrektywa 2024/2831, wdrożenie w PL do grudnia 2026).**
Może z czasem ograniczyć model gig (swobodny wybór zleceń), na którym stoi cała funkcja "czy wziąć zlecenie". Reakcja: działaj szybko, okno jest do ~2027. Równolegle zaplanuj wariant wartości na świat po zmianie (statystyki zarobków, optymalizacja godzin/tras), żeby produkt przetrwał, nawet gdy "akceptuj/odrzuć" straci sens.

5. **Konkurent wchodzi na PL (np. GigU) - bariera wejścia jest niska.**
Twoja jedyna obrona to czas i rozpoznawalność: zostań "tym znanym darmowym narzędziem" wśród kurierów PL/UA, zanim ktokolwiek się pojawi. Społeczność i dobre recenzje to fosa, której nowy gracz nie skopiuje z dnia na dzień.

6. **Google Play zawiesza apki używające Accessibility Service do "nietypowych" celów.**
Google bywa restrykcyjny wobec Accessibility. Reakcja: pilnuj, by opis uprawnień w Console był precyzyjny i zgodny z polityką (deklaracja, że służy do odczytu ofert dla użytkownika), i miej zawsze aktualny backup APK + sposób na szybką reakcję, gdyby przyszło ostrzeżenie. To Twoja apka żyje na jednym uprawnieniu - traktuj jego zgodność z polityką poważnie.

---

**Jedna rzecz, którą musisz zapamiętać:** masz dobry produkt w pustej niszy. Twój problem to wyłącznie to, że nikt o nim nie wie. Najbliższe 2 tygodnie (zdalnie) to ASO + wejście do społeczności + outreach do twórców. Kolejne 2 (w Polsce) to ulotki QR, rozmowy na mieście i flota. Nie buduj nowych funkcji, dopóki nie zaczniesz mieć użytkowników - jedyny wyjątek to niezawodność odczytu, bo bez niej promocja leje wodę w dziurawe wiadro.

---

# CZĘŚĆ II - GOTOWCE DO WDROŻENIA

## II.1 Listing Google Play dla OrderPilot - gotowiec ASO (PL + UA/RU)

# LISTING GOOGLE PLAY - OrderPilot (gotowy do wklejenia)

Wszystko poniżej jest policzone skryptem (limity znaków się zgadzają) i BEZ długich myślników (tylko `-`). Belki kolorowe (🟢🟡🔴) zostawiam, bo to one sprzedają produkt na ekranie sklepu.

---

## 1) TYTUŁ APLIKACJI (max 30 znaków) - warianty

Problem dziś: tytuł brzmi tylko „OrderPilot" - nie ma w nim ŻADNEGO słowa, którego kurier szuka. Tytuł ma najwyższą wagę w wyszukiwarce Google Play, więc dodanie słowa „kurier" to pojedynczo najsilniejszy ruch ASO. Dodatkowo spacja w „Order Pilot" / słowo „kurier" pomaga, gdy ktoś wpisuje „order pilot" ze spacją.

| # | Tytuł | Znaki | Kiedy ten |
|---|-------|------|-----------|
| A (REKOMENDOWANY) | `OrderPilot: kurier zł/h zł/km` | 29 | Łapie „kurier" + dwie kluczowe jednostki wartości. Najmocniejszy mix brand + kategoria. |
| B | `OrderPilot: Kurier Uber Wolt` | 28 | Gdy chcesz łapać wyszukiwania nazw platform (Uber, Wolt). |
| C | `OrderPilot: kurier Glovo Wolt` | 29 | Wariant pod 2 najpopularniejsze platformy dostaw w PL. |
| D | `OrderPilot - kurier zł/h` | 24 | Wersja minimalistyczna, czysta. |

Rekomendacja: wariant A. Zmianę tytułu warto puścić jako eksperyment (Custom Store Listing / store listing experiment w Console), żeby porównać konwersję ze starym „OrderPilot".

---

## 2) KRÓTKI OPIS (max 80 znaków)

REKOMENDOWANY (67 znaków, zostaje przy sprawdzonej linii „Order Pilot dla kurierów"):
```
Order Pilot dla kurierów: PLN/h zleceń Uber, Wolt, Glovo, Bolt Food
```

Warianty alternatywne:
```
Order Pilot: czy zlecenie się opłaca? zł/h i zł/km dla kuriera
```
(62 znaki - mocniejszy benefit „czy się opłaca")
```
Order Pilot: zł/h i zł/km zlecenia. Darmowa, bez konta, zero bana
```
(65 znaków - eksponuje 3 przewagi naraz)

---

## 3) PEŁNY OPIS (3223 / 4000 znaków) - gotowy do wklejenia

```
Order Pilot to darmowa aplikacja dla kurierów Uber Eats, Wolt, Glovo i Bolt Food. OrderPilot pokazuje, ile zł/h i zł/km jest warte każde zlecenie, w momencie gdy wpada - zielona, żółta lub czerwona belka nad ekranem apki kuriera mówi w 5 sekund, czy oferta się opłaca. Koniec liczenia opłacalności w głowie.

CZY TO ZLECENIE SIĘ OPŁACA?
Tyle zarabiasz na godzinę, a tyle za kilometr - to jedyne, co się liczy. Order Pilot przelicza kwotę, dystans i czas na zł/h i zł/km, i od razu mówi:
🟢 zielona belka = bierz
🟡 żółta belka = zależy od dystansu
🔴 czerwona belka = odrzuć, nie marnuj czasu

JAK DZIAŁA
Aplikacja wykrywa nowe oferty zleceń z apek kurierskich (Uber Driver, Wolt, Glovo, Bolt Food), wylicza opłacalność na podstawie Twoich własnych progów i pokazuje wynik jako kolorową belkę nad ekranem. Obok widzisz kwotę, dystans i czas - wszystko w jednym miejscu, żebyś zdecydował od razu.

DLA WIELU APEK NARAZ (MULTI-APPING)
Pracujesz na Uber Eats, Wolt, Glovo i Bolt Food jednocześnie? Jedna belka działa nad wszystkimi. Porównujesz zlecenia z różnych platform i bierzesz to, które realnie się opłaca.

ZERO RYZYKA BANA
To najważniejsze. Order Pilot tylko czyta ekran przez Android Accessibility Service. NIE loguje się do Twojego konta, NIE używa API platform, NIE klika i NIE akceptuje zleceń za Ciebie. Nie automatyzuje niczego - tylko doradza, decyzja zawsze należy do Ciebie. Dlatego nie ma ryzyka bana ani naruszenia regulaminu Uber, Wolt, Glovo czy Bolt Food.

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

Co poprawia ten opis względem aktualnego (z 2026-05-23):
- Dodane realnie wyszukiwane frazy: „czy zlecenie się opłaca", „opłacalność", „zł/h i zł/km", „multi-apping", „nadpodaż", „stawki".
- Mocniej wyeksponowane „ZERO RYZYKA BANA" (główny argument, który zabija konkurenta Para w USA) + „tylko doradza, nie automatyzuje" (odróżnia od Mystro/Maxymo).
- Usunięte WSZYSTKIE długie myślniki (w starym opisie było ich pełno - to anty-AI tell i Twoja zasada).
- „Order Pilot" 6x (w tym w pierwszych 250 znakach) - domyka problem widoczności na frazę „order pilot" ze spacją.
- Gęstość słów kluczowych: kurier 14x, opłac- 9x, Uber 7x, zł/h 6x, Wolt/Glovo/Bolt po 6x - naturalnie, bez upychania.

---

## 4) SŁOWA KLUCZOWE (PL / UA / RU / EN)

W Google Play nie ma osobnego pola „keywords" - poniższe frazy wplata się w tytuł, krótki i pełny opis OSOBNEGO listingu per język (Custom store listing). Najpierw PL, potem zrób ręczne (nie tłumaczone maszynowo) listingi UA i RU - to ogromna grupa kurierów z Ukrainy w Polsce, a konkurencja tam zerowa.

POLSKI (główny rynek):
kurier, kurier jedzenia, dostawca jedzenia, praca kuriera, aplikacja dla kuriera, zarobki kuriera, ile zarabia kurier, Glovo zarobki, Wolt zarobki, Uber Eats zarobki, Bolt Food, czy zlecenie się opłaca, opłacalność zlecenia, kalkulator kuriera, kalkulator opłacalności, zł/h, zł/km, stawka kuriera, nakładka na ekran, order pilot

UKRAIŃSKI (priorytet #2 - kurierzy z UA w Polsce):
кур'єр, кур'єр у Польщі, робота кур'єром, доставка їжі, заробіток кур'єра, скільки заробляє кур'єр, Glovo заробіток, Wolt заробіток, Bolt Food, вигідність замовлення, чи вигідне замовлення, зл/год, зл/км, калькулятор кур'єра

ROSYJSKI (priorytet #3 - duża część kurierów RU-języcznych; warto dodać interfejs/listing RU):
курьер, курьер в Польше, работа курьером, доставка еды, заработок курьера, сколько зарабатывает курьер, Glovo заработок, Wolt заработок, Bolt Food, выгодность заказа, выгодно ли брать заказ, зл/час, зл/км, калькулятор курьера

ANGIELSKI (drugorzędny - patrz uwaga niżej):
courier, food delivery, delivery driver, gig driver assistant, delivery driver helper, is this order worth it, per hour per km, Uber Eats Wolt Glovo Bolt helper, order pilot

UWAGA strategiczna do EN/ES: apka liczy w PLN, więc instalacje z USA/ES/EN i tak odpadną (zła retencja = niższy ranking). Listingi EN/ES wstrzymaj, dopóki apka nie przelicza walut. Rynek realny = PL + kurierzy UA/RU w Polsce. Sam ASO nie wygeneruje ruchu - w PL popyt na „asystenta kuriera" jeszcze nie istnieje, trzeba go wytworzyć (grupy FB/Telegram, demo wideo, Dexterowski), a ASO ma go przechwycić.

---

## 5) ZRZUTY EKRANU + PIERWSZE RECENZJE

ZRZUTY (pionowe 1080x1920; pierwsze 2-3 decydują o instalacji - ~90% userów nie przewija dalej niż 3.):
1. HERO (najważniejszy): realna ZIELONA belka NAD ekranem apki kuriera + duże „48 zł/h". Podpis: „Wiesz w 5 sekund, czy brać zlecenie".
2. Trzy kolory belki obok siebie (zielony/żółty/czerwony). Podpis: „Bierz - Zależy - Odrzuć".
3. Belka nad kilkoma platformami. Podpis: „Jedna belka nad Uber, Wolt, Glovo i Bolt Food".
4. Ekran prywatności/bezpieczeństwa. Podpis: „Zero ryzyka bana - tylko czyta ekran, nie używa API".
5. Podpis: „Darmowa. Bez konta. Działa offline.".
Dodatkowo: feature graphic 1024x500 (wymagany; wg repo wcześniej go brakowało) - ZIELONA belka „48 zł/h" + claim „Darmowa apka dla kurierów. Zero ryzyka bana.".
Zasada: każdy zrzut z krótkim podpisem (mniej niż 5 słów na grafice), korzyść nie technologia. NIE pisz o OCR/Accessibility na grafice - pisz o pieniądzach i decyzji.

PIERWSZE RECENZJE (start jest pusty - to zabija konwersję):
- Wdroż In-App Review API i pokaż prośbę o ocenę PO pierwszym opłacalnym (zielonym) zleceniu - kurier jest wtedy najbardziej zadowolony.
- Poproś imiennie realnych testerów (Marcin, Dominik, Andrij) o 5 gwiazdek + 1-2 zdania PO POLSKU, w języku kuriera. Przykładowe szczere recenzje do wzoru (nie kopiuj 1:1, niech każdy napisze po swojemu):
  „Od razu widać czy zlecenie się opłaca. Przestałem brać kursy za 8 zł na drugi koniec miasta. zł/h realnie w górę."
  „Działa nad Glovo i Bolt naraz, nic nie wysyła, konta nie ruszyłem - zero stresu o bana. I za darmo."
  „Простий додаток, показує скільки зл/год за замовлення. Беру тільки вигідні."
- Odpowiadaj na KAŻDĄ recenzję w ciągu 24h (Google nagradza zaangażowanie i retencję D7, nie samą liczbę instalacji). Nigdy farmowanych instalacji.

---

## SZYBKA INSTRUKCJA WDROŻENIA
1. Console -> Grow -> Store presence -> Main store listing (PL): wklej krótki + pełny opis powyżej, zapisz, wyślij do review.
2. Tytuł: ustaw wariant A jako eksperyment store listing (nie nadpisuj od razu na ślepo).
3. Dodaj custom store listing dla UA i RU (ręczne teksty z sekcji 4).
4. Wymień zrzuty + dodaj feature graphic.
5. Po 7 dniach: Console -> Acquisition -> Search terms - sprawdź, czy „order pilot" i „kurier" zaczynają wpadać, i iteruj.

## II.2 OrderPilot - lista celów do zasiania apki (grupy FB / Telegram / Discord / TikTok-YouTube) + gotowe szablony PL/UA/RU

# OrderPilot - gdzie zasiać apkę wśród kurierów (PL + ukraińskojęzyczni w Polsce)

To gotowa lista miejsc do "posiania" apki za 0 zł + gotowe szablony postów/wiadomości (PL, UA, RU), które wyglądają jak realny kurier dzielący się narzędziem, a nie jak reklama. Najpierw 3 zasady, potem cele wg priorytetu, na końcu szablony.

---

## 3 zasady, żeby NIE dostać bana / nie wylecieć z grupy
1. **Najpierw bądź członkiem, potem napisz.** Wejdź, przez kilka dni komentuj/pomagaj normalnie (odpowiedz komuś na pytanie o zarobki, trasę). Dopiero potem JEDEN post o narzędziu. Reguła 90/9/1 (90% wartość, 9% miękka wzmianka, 1% "tu link").
2. **Pytaj admina o zgodę** tam, gdzie regulamin zakazuje reklam (większość grup FB i część Telegramów). Krótko: "Cześć, zrobiłem darmową apkę dla nas kurierów, zero reklam, mogę wrzucić jeden post i zapytać o opinie?". Część adminów Telegrama każe płacić za post - wtedy odpuść i idź organicznie.
3. **Mów "zrobiłem to dla siebie/dla nas", nie "polecam aplikację".** Eksponuj 3 rzeczy: (1) ZA DARMO, bez konta, bez reklam, nic nie wysyła; (2) ZERO ryzyka bana - tylko czyta ekran, nie loguje się do Glovo/Wolt; (3) działa nad Uber Eats / Wolt / Glovo / Bolt Food naraz. Proś o feedback - to wygląda autentycznie i daje ci poprawki.

**Kolejność uderzeń (od najtańszego o największym zasięgu):**
1) Discord Dexterawka + napisać do Dexterowskiego (1 osoba = ~100-200 tys. zasięgu)
2) Telegram @glovo_uber_wolt + lokalne czaty miejskie
3) Grupy FB (Wolna Grupa Niebieskich Kurierów, Glovo Polska, multi-app)
4) Mali youtuberzy/tiktokerzy na barter (apka za darmo do testu)
5) Własne krótkie wideo (TikTok/Reels/Shorts) z belką w akcji

---

## A. TELEGRAM (najgęstsza grupa docelowa, dużo UA/RU)

| Nazwa | Link | Wielkość (ok.) | Język | Uwaga |
|---|---|---|---|---|
| Glovo \| Uber \| Wolt 🇵🇱 Polska | https://t.me/glovo_uber_wolt | ~3,9-4,7 tys. | PL / UA / RU | **Najważniejszy ogólnopolski czat.** Tu pisać w 3 językach. |
| Glovo PL 🇵🇱 (@glovopl) | https://t.me/glovopl | ~1,2 tys. | PL / UA / EN | Prowadzony przez partnera flotowego Flow Apps. Najpierw zapytaj admina. |
| Wolt Gdynia | https://t.me/woltgdynia | lokalny, publiczny | PL | Czat miejski, łatwy do wejścia. |
| Wolt Gdańsk | https://t.me/+us_WIPVNL-VjZjBk | lokalny, prywatny | PL | Wejście przez link zaproszeniowy. |
| Bolt Food (oficjalny kanał kurierów) | szukaj w apce Bolt Food / przez partnera | duży | PL | Oficjalny - tam się NIE reklamuje; to do obserwacji, nie do postów. |

**Jak znaleźć czaty miejskie:** w wyszukiwarce Telegrama wpisz "Glovo Wrocław", "Wolt Kraków", "kurier Warszawa", "доставка Варшава" itd. Prawie każde duże miasto ma swój czat (Wrocław, Trójmiasto, Poznań, Łódź, Katowice). Te lokalne są najłatwiejsze - mniej moderacji.
**Pułapka:** kanał "GLOVO INFOrmation" (@glovoinform, ~14 tys.) to UKRAINA (Kijów/Odessa), NIE Polska - tam nie pisać.

---

## B. DISCORD (najmocniejszy pojedynczy punkt)

| Nazwa | Link | Wielkość (ok.) | Język |
|---|---|---|---|
| Dexterawka (społeczność Dexterowskiego) | https://discord.gg/dexterawka | ~10 tys. | PL (głównie) |

Wejdź jako kurier, kilka dni normalnie pogadaj, potem wrzuć narzędzie w kanale "pomoc/ogólne" jako "zrobiłem to dla siebie, może się przyda". Discord ma sekcje (rowery, smartfony, ogólne) - wybierz pasującą.

---

## C. FACEBOOK (duży zasięg, ale ostro tępią reklamę - PYTAJ ADMINA)

| Nazwa | Link | Wielkość | Język |
|---|---|---|---|
| Wolna Grupa Niebieskich Kurierów (Wolt, oddolna) | https://www.facebook.com/groups/278530996963355 | b.d. (FB ukrywa) | PL |
| Glovo Polska - dostawcy, kurierzy | https://www.facebook.com/groups/polskaglovo | b.d. | PL / UA |
| Wolt Polska - dostawcy, kurierzy | https://www.facebook.com/groups/woltpolska | b.d. | PL |
| Kurierzy Dostawcy Eats Glovo Wolt Xpress Delivery (multi-app) | https://www.facebook.com/groups/881810492167387 | b.d. | PL |

FB jest najbardziej wrażliwy na "reklamę" - tu absolutnie najpierw post wartościowy (poradnik "jak w 5 sekund ocenić czy zlecenie się opłaca"), a apka jako narzędzie na końcu. Najlepiej z prywatnego konta-kuriera, nie firmowego.

---

## D. YOUTUBE / TIKTOK - twórcy kurierscy (barter: apka za darmo do testu)

Napisz krótko, zaproponuj DARMOWY test (bez płacenia): "testuję apkę, która mówi czy zlecenie się opłaca - sprawdź na nagraniu czy ma rację". Format "jakie zlecenia brać" jest u nich sprawdzony i viralowy.

| Twórca | Link | Wielkość (ok.) | Język | Kontakt |
|---|---|---|---|---|
| **Dexterowski** (CEL #1) | https://www.youtube.com/@Dexterowskii | YT ~100-200 tys., TikTok ~100 tys. | PL | kontakt@dexterowski.pl, TikTok @dexterowski2137, IG @dexterowskii, Discord |
| Yellowbox (duży reach) | https://www.youtube.com/@yellowbox | ~500 tys. | PL | yellowbox@lifetube.pl |
| Michał Górka / ShyneDeux | https://www.youtube.com/@ShyneDeux | 170+ odcinków | PL | przez kanał |
| Reysowaty (Glovo, Olsztyn) | https://www.youtube.com/@reysowaty3952 | mały/niszowy | PL | przez kanał |
| SwojąDrogą (Uber, Rzeszów) | https://www.youtube.com/@swojadroga599 | mały | PL | przez kanał |
| Deliverka (Wolt skuter) | https://www.youtube.com/@deliverka | ~1 tys. | PL | przez kanał |
| Kurier z Holywood (Dolny Śląsk) | https://www.youtube.com/@KURIERZHOLLYWOOD | niszowy | PL | przez kanał |
| Eternis - Glovo/Uber/Bolt/FreeNow | (kanał "Eternis") | niszowy | PL | przez kanał |

Twórcy ukraińskojęzyczni o pracy kuriera W POLSCE (świetny dowód popytu - ich widzowie to dokładnie nasi userzy): **RazeDen, Serhii Marchenko, Pokatun, ЩОЯК**. Tu pisać po ukraińsku/rosyjsku.

---

## E. FORA / KATALOGI / pasywne źródła (zrób raz, działa długo)

| Miejsce | Link | Po co |
|---|---|---|
| Kurierpedia (projekt Dexterowskiego) | https://kurierpedia.pl | Dodać apkę jako pozycję w "narzędzia/porady" - pasywne pobrania. |
| Forum Uber-Bolt (dział Dostawcy/Kurierzy) | https://uber-bolt.net/forum/dostawcy-kurierzy/ | Wątek "narzędzie które zrobiłem". |
| Portal dostawca-jedzenia.pl | https://dostawca-jedzenia.pl | Poprosić o wzmiankę / dodanie do listy narzędzi. |
| Katalog czatów TG | https://tgstat.com / tg-cat.com | Znajdziesz tu kolejne czaty miejskie kurierów. |

---

# GOTOWE SZABLONY (kopiuj-wklej)

> Wskazówka: w każdym poście dodaj swój link do Google Play. Nie wklejaj samego linku - link zawsze na końcu, po wartości.

## Szablon 1 - krótki, do Telegrama / Discorda (PL)
```
Cześć, jeżdżę jak Wy i wkurzało mnie liczenie w głowie czy zlecenie się opłaca, więc zrobiłem sobie apkę i daję ją za darmo.

Pokazuje belkę nad Glovo/Wolt/Uber Eats/Bolt Food (zielony/żółty/czerwony) i od razu zł/h i zł/km - widzisz w sekundę czy brać.

Ważne: nie loguje się do żadnej apki, tylko czyta ekran, więc nie ma ryzyka bana. Za darmo, bez konta, bez reklam, nic nie wysyła na żaden serwer.

Dajcie znać co poprawić - robię to po godzinach dla nas, a nie na sprzedaż.
[link do Google Play]
```

## Szablon 2 - dłuższy, do grupy FB (PL, "wartość najpierw")
```
Mała rada dla początkujących: nie patrzcie na samą kwotę zlecenia, tylko na zł/km i zł/h. 8 zł za 1 km to świetnie, 8 zł za 5 km z powrotem pod prąd to strata czasu. Dobre zlecenie liczę z grubsza tak: kwota / (czas dojazdu + dostawa + powrót).

Robienie tego w głowie przy każdym pingu było męczące, więc zrobiłem sobie małą apkę, która liczy to automatycznie i pokazuje kolorową belkę nad Glovo/Wolt/Uber Eats/Bolt Food. Daję ją za darmo - nie loguje się do konta (czyta tylko ekran, zero ryzyka bana), bez reklam, bez konta, nic nie wysyła.

Jak ktoś chce przetestować i powiedzieć co dodać - byłbym wdzięczny, dopiero ją rozwijam.
[link]
(Admini - jak coś nie tak z postem, dajcie znać, skasuję)
```

## Szablon 3 - DM do twórcy YouTube/TikTok (PL, barter)
```
Hej, oglądam Twoje filmy o kurierce. Zrobiłem darmową apkę, która w sekundę pokazuje czy zlecenie się opłaca - belka nad Glovo/Wolt/Uber Eats/Bolt Food z zł/h i zł/km (zielony=bierz, czerwony=odpuść). Nie rusza konta platformy, tylko czyta ekran, więc bez ryzyka bana.

Nie sprzedaję - jest darmowa, bez reklam. Pomyślałem, że to dobry materiał na film "czy ta apka ma rację co do zleceń" - wyzwanie idealne pod Twój format. Mogę dać wszystko czego potrzebujesz do testu. Wrzucisz, sprawdzisz na realnych zleceniach? Ciekaw jestem Twojej szczerej oceny.
```

---

## Szablon 1 - Telegram/Discord (UA - ukraiński)
```
Привіт! Сам розвожу, як і ви, і набридло рахувати в голові, чи вигідне замовлення - тож зробив собі застосунок і віддаю безкоштовно.

Показує смужку поверх Glovo/Wolt/Uber Eats/Bolt Food (зелений/жовтий/червоний) і одразу zł/год та zł/км - за секунду видно, брати чи ні.

Важливо: не заходить у ваш акаунт, лише читає екран, тому НЕ буде бана. Безкоштовно, без реєстрації, без реклами, нічого нікуди не надсилає.

Напишіть, що покращити - роблю це для нас, а не на продаж.
[посилання на Google Play]
```

## Szablon 2 - grupa FB / dłuższy (UA)
```
Порада новачкам: дивіться не на саму суму замовлення, а на zł/км і zł/год. 8 zł за 1 км - супер, 8 zł за 5 км туди й назад - марна трата часу.

Рахувати це в голові на кожному замовленні втомлювало, тож я зробив маленький застосунок, який рахує автоматично і показує кольорову смужку поверх Glovo/Wolt/Uber Eats/Bolt Food. Віддаю безкоштовно - не заходить в акаунт (лише читає екран, тому без ризику бана), без реклами, без реєстрації, нічого не надсилає.

Хто хоче протестувати і сказати, що додати - буду вдячний.
[посилання]
```

---

## Szablon 1 - Telegram (RU - rosyjski, bo duża część kurierów pisze po rosyjsku)
```
Привет! Сам развожу, как и вы, надоело в уме считать, выгодный ли заказ - сделал себе приложение и отдаю бесплатно.

Показывает полоску поверх Glovo/Wolt/Uber Eats/Bolt Food (зелёный/жёлтый/красный) и сразу zł/час и zł/км - за секунду видно, брать или нет.

Важно: не заходит в ваш аккаунт, только читает экран, поэтому бана не будет. Бесплатно, без регистрации, без рекламы, ничего никуда не отправляет.

Напишите, что улучшить - делаю это для нас, а не на продажу.
[ссылка на Google Play]
```

---

## Co zrobić w tym tygodniu (konkret)
1. Wejdź na **Discord Dexterawka** i do **Telegrama @glovo_uber_wolt** - tylko obserwuj/pomagaj 2-3 dni.
2. Napisz DM do **Dexterowskiego** (szablon 3) - to najtańszy strzał o największym zasięgu.
3. Dołącz do 3-4 grup FB i znajdź 2-3 czaty miejskie Telegram swojego regionu.
4. Po 2-3 dniach wrzuć po JEDNYM poście (szablon 1 lub 2) w każdym miejscu, w odpowiednim języku - na czatach mieszanych wrzuć od razu PL+UA+RU pod sobą.
5. Odpowiadaj na KAŻDY komentarz i każdą recenzję w Google Play w 24h - to buduje zaufanie i podbija ranking.

## II.3 Lista twórców kurierskich (PL/UA) do outreachu barterowego + szablony wiadomości (PL/EN)

# Outreach barterowy OrderPilot - twórcy kurierscy + szablony

Cel: darmowa recenzja/wzmianka apki w zamian za nic (apka i tak jest darmowa). Budżet 0 zł. Kolejność wg priorytetu - od największego dopasowania i zasięgu.

## TIER 1 - zacznij od tych dwóch (najwyższy zasięg + idealne dopasowanie)

**1. Dexterowski** - NAJWAŻNIEJSZY punkt dotarcia w PL
- YouTube: https://www.youtube.com/@Dexterowskii - ~189 tys. subskrybentów (40 mln wyświetleń łącznie)
- TikTok: https://www.tiktok.com/@dexterowski2137 - ~124 tys. obserwujących, 3,6 mln polubień
- Discord (jego społeczność "Dexterawka"): https://discord.gg/dexterawka - ~10 tys. kurierów
- Profil: kurier Glovo/Uber/Wolt z Krakowa, robi dokładnie content "ile zarabia kurier / co się opłaca" - 1:1 z tematem apki
- KONTAKT: kontakt@dexterowski.pl (ogólny), arreek.business@gmail.com (współprace biznesowe)
- Dlaczego on: największa pojedyncza dźwignia w PL. Jego widzowie to wprost grupa docelowa. Możesz też wejść organicznie na jego Discord jako kurier i pokazać apkę w wątku porad.

**2. Yellowbox (Maciek Pudełko)** - największy zasięg ogółem
- YouTube: https://www.youtube.com/channel/UCdO3rGYAaOggrrWz9PIbggg - ~500 tys. subskrybentów (największy warszawski kanał rowerowy)
- Instagram: https://www.instagram.com/yellowboxpl/ - ~78 tys. obserwujących
- Profil: kurier rowerowy Warszawa (szerzej rower/miasto, nie tylko delivery - ale rdzeń to kurierka)
- KONTAKT: yellowbox@lifetube.pl
- Uwaga: duży kanał, może być trudniej o darmową wzmiankę - warto, ale traktuj jako "reach play", nie pierwszy strzał.

## TIER 2 - mniejsze/średnie kanały PL (łatwiej o darmowy test, wyższy współczynnik odpowiedzi)

**3. Deliverka** - https://www.youtube.com/@deliverka - blisko 1 tys. subskrybentów, rosnący, bardzo wysoka jakość nagrań. Kurier na hulajnodze/e-bike (Wrocław), dostawa Wolt/Glovo/Bolt/Uber Eats. Idealny do nakręcenia demo belki w akcji.
**4. SwojąDrogą** - https://www.youtube.com/@swojadroga599 - nisza kurierska PL.
**5. Reysowaty** - https://www.youtube.com/@reysowaty3952 - kurierka PL.
**6. Michał Górka (ShyneDeux)** - https://www.youtube.com/@ShyneDeux - 170+ odcinków o kurierce.
**7. Kurier z Holywood** - https://www.youtube.com/@KURIERZHOLLYWOOD - content kurierski PL.
**8. Luters** - kanał kurierski PL (przykładowy film: https://www.youtube.com/watch?v=Ui1CYb3Wz8c).

## TIER 3 - twórcy ukraińskojęzyczni (kurierzy z UA w Polsce = kluczowy, niedoceniony segment)

Bardzo duża część kurierów w PL to migranci z Ukrainy. Apka ma już UA/EN. To najmniej obsłużona, a bardzo zaangażowana grupa. (Uwaga: poniżej linki do filmów-kotwic; pełne adresy kanałów warto domknąć klikając w autora - confidence średni co do dokładnych nazw kanałów.)

**9. RazeDen** - film "Я працював місяць курʼєром в Glovo у Польщі" (~18 tys. wyświetleń): https://www.youtube.com/watch?v=_nzI9wY7hx0 - wprost o pracy kuriera w Polsce, porusza specyfikę PL (partnerzy rozliczeniowi, umowy).
**10. Pokatun** - poradnik "8 порад" dla kurierów (~37 tys. wyświetleń): https://www.youtube.com/watch?v=q_9n5ed6lc0 - uczy liczyć opłacalność zlecenia i multi-appingu (Glovo+Bolt naraz) - idealny kontekst pod apkę.
**11. Serhii Marchenko** - "Сколько можно заработать за час в glovo на велосипеде? Работа в Польше" (~18 tys. wyświetleń): https://www.youtube.com/watch?v=q_9n5ed6lc0 (RU/UA, praca w PL).

## BONUS - partner flotowy z własnym kanałem (inny typ, ale realny kanał dystrybucji do setek kurierów)

**12. Eternis** - https://www.youtube.com/@EternisJobPartner + IG https://www.instagram.com/eternis_partner/ - partner rozliczeniowy (12 tys.+ kierowców, 10 biur w PL), robi content "gdzie zarobisz więcej - Glovo czy Bolt". To nie influencer, ale apka im NIE zagraża (podnosi zarobki ich ludzi) - mogą ją polecić swoim kurierom. Kontakt na późniejszą fazę (gdy będziesz w PL).

---

# SZABLON WIADOMOŚCI (PL) - do skopiowania

```
Cześć [Imię],

oglądam Twoje materiały o kurierce ([Glovo/Wolt/Bolt - dopasuj]) i widzę, że często rozkminiasz, czy dane zlecenie się w ogóle opłaca. Zrobiłem darmową apkę na Androida, która robi to za kuriera automatycznie - OrderPilot.

W skrócie: gdy wpada zlecenie, apka pokazuje belkę NAD apką platformy (Uber Eats / Wolt / Glovo / Bolt Food) - kolor zielony/żółty/czerwony + ile to zł/h i zł/km. W sekundę wiesz: brać czy odrzucić.

Czemu może Cię to zainteresować:
- ZERO ryzyka bana - tylko czyta ekran, nie loguje się do konta, nie używa API platform, niczego nie automatyzuje. (W USA podobne apki łączące się z kontem - Para - dostały bana; ta świadomie tego nie robi.)
- Darmowa, prywatna - nic nie wysyła na serwer, bez konta, bez reklam. Działa po polsku, ukraińsku i angielsku.
- Działa nad wszystkimi apkami naraz - jedna belka dla Glovo, Wolt, Bolt Food i Uber Eats.

Nie chcę nic sprzedawać (apka jest i będzie darmowa) ani Cię opłacać. Pomyślałem, że to dobry materiał na film/wzmiankę typu "testuję apkę, która mówi, czy zlecenie się opłaca - sprawdzam, czy ma rację". Jeśli chcesz dać jej szansę, podeślę link do Google Play i pomogę z czymkolwiek (np. nagram dla Ciebie demo belki w akcji).

Dzięki i powodzenia na trasie,
[Imię] - twórca OrderPilot
[link do Google Play]
```

# SZABLON WIADOMOŚCI (EN) - do skopiowania

```
Hi [Name],

I follow your courier content and noticed you often break down whether an order is actually worth taking. I built a free Android app that does exactly that, automatically - it's called OrderPilot.

In short: when an order pops up, the app shows a bar ON TOP of the platform app (Uber Eats / Wolt / Glovo / Bolt Food) - green/yellow/red color + how much it is per hour and per km. In one second you know: take it or skip it.

Why it might interest you:
- ZERO ban risk - it only reads the screen, never logs into your account, never uses the platforms' API, automates nothing. (In the US, similar apps that connect to your account - like Para - got blocked; this one deliberately avoids that.)
- Free and private - nothing is sent to any server, no account, no ads. Works in Polish, Ukrainian and English.
- Works over all apps at once - one bar for Glovo, Wolt, Bolt Food and Uber Eats.

I'm not selling anything (the app is and stays free) and I'm not paying for placement. I just thought it'd make a solid video/mention like "I'm testing an app that tells you if an order is worth it - let's see if it's right." If you'd like to give it a try, I'll send the Google Play link and help with anything (e.g. I can record a demo of the bar in action for you).

Thanks and ride safe,
[Name] - creator of OrderPilot
[Google Play link]
```

## Wskazówki przy wysyłce
- Personalizuj pierwsze zdanie pod konkretny film twórcy (pokaż, że naprawdę oglądałeś) - to drastycznie podnosi odsetek odpowiedzi.
- Do twórców UA/RU - wyślij ten sam szablon przetłumaczony na ukraiński/rosyjski (mogę przygotować).
- Zacznij od Tier 2 i Tier 3 (mniejsze kanały) - szybciej odpiszą i dadzą pierwsze recenzje/dowód społeczny, którym potem skusisz Dexterowskiego i Yellowboxa.
- Miej gotowy 30-sekundowy filmik demo belki (zielony/żółty/czerwony + zł/h) - to najmocniejszy załącznik.
```

---

# CZĘŚĆ III - GŁOSY KURIERÓW (fora/Reddit) - bóle i wishlist

## Zastrzeżenie metodyczne (ważne)
Reddit był w całości zablokowany dla narzędzi w tej sesji (wyszukiwarka filtruje linki `reddit.com`, WebFetch/przeglądarka odmawiają wejścia). Dlatego **brak bezpośrednich linków do pojedynczych wątków** - i świadomie ich nie zmyślono. Sentyment pochodzi z: (1) streszczeń wyszukiwarki generowanych z realnych dyskusji kierowców, (2) czytelnych artykułów branżowych (EntreCourier, The Rideshare Guy, Gridwise, strony apek) - te linki są prawdziwe i faktycznie otwarte. Na końcu jest lista subredditów + gotowych URL-i wyszukiwania do samodzielnej weryfikacji.

## 1. TOP powtarzających się BÓLI kurierów
1. **Niska / nieopłacalna stawka za zlecenie** - progi odrzucania community to "poniżej ~$1,50-2 za milę" i "poniżej ~$6-8 bazy". (EntreCourier, ShiftTracker, Para)
2. **Zlecenia bez napiwku ("no-tip")** - "if the customer didn't tip, just don't take the trip"; w USA ~56% zarobku to napiwki. (Para)
3. **Długie dojazdy za grosze / na pusto** - 10-20 mil z minimalnym napiwkiem zjada godzinę i paliwo. (Para)
4. **Acceptance rate jako narzędzie nacisku** - dylemat "cherry-picking vs. status (Top Dasher)". (EntreCourier, The Rideshare Guy)
5. **Ukryte napiwki / brak transparentności realnej wypłaty** - nie widać pełnej kwoty przed akceptacją (stąd cały fenomen "tip predictorów" / Para). (Para, ShiftTracker)
6. **Chaos multi-appingu** - żonglowanie 2-3 apkami naraz, ryzyko spadku ratingu, dwa telefony. (The Rideshare Guy)
7. **Zmęczenie ciągłym liczeniem w głowie ($/milę, $/h) pod presją czasu** - to dokładnie nisza, którą produktyzują Maxymo/Mystro ("instant heuristics", "color-coded decision indicators"). (Maxymo, Mystro)
8. **Stacked orders w przeciwnych kierunkach** - "eat into your time and efficiency". (Para)
9. **Niepewność, czy w danej godzinie/strefie warto wyjeżdżać** - cała wartość Solo "Smart Schedule". (Solo, EntreCourier)
10. **Strach przed deaktywacją bez procesu ("manage by algorithm")** - bany bez ostrzeżenia i odwołania. (EntreCourier)
11. **Niedoszacowane przebiegi = przepłacony podatek** (USA) - bazowy ból mileage trackerów. (ShiftTracker/Hurdlr)
12. **Regulacyjny zamęt / wahania stawek** - eksperyment Seattle z gwarantowaną stawką "nie zadziałał jak miał". (The Conversation)

## 2. WISHLIST - czego kurierzy chcą, a w pełni nie mają
- **Natychmiastowy werdykt "brać / nie brać" na ekranie** - kolor + $/milę i $/h liczone za nich (rdzeń Maxymo).
- **Auto-decline wg własnych progów** ($/milę, $/h, dystans, typ) - żeby śmieci same znikały (Mystro, DUH, Maxymo).
- **Pełna realna wypłata przed akceptacją** (w tym ukryty napiwek). Dane: transparentność pay-upfront podnosi retencję kierowców o ~41%.
- **Hands-free / głos** - decyzja bez dotykania telefonu w jeździe (Maxymo).
- **Jeden pulpit na multi-apping** - agregacja kilku platform (Mystro, Gridwise).
- **Podpowiedź "gdzie i kiedy warto wyjechać"** (smart schedule / heatmapy) (Solo, Gridwise).
- **Automatyczny licznik km + raport podatkowy** (USA-centryczne, mniej istotne dla PL) (Hurdlr, Stride).

## 3. Za co kurierzy FAKTYCZNIE płacą (apki + ceny)
| Apka | Co robi | Cena | Link |
|---|---|---|---|
| **Maxymo** | **Najbliższy OrderPilotowi**: snapshot oferty z $/milę i $/h, kolorowe wskaźniki, auto-accept/decline, głos (Android) | Freemium (cennik nie podany) | https://maxymoapp.com/ |
| **Mystro** | Auto-accept/decline wg fare/dystans/$/h, przełączanie apek | $6,99/tydz., **$18,99/mc**, $139,99/rok | https://mystrodriver.com/ |
| **Driver Utility Helper (DUH)** | Auto-decline niskich ofert; kultowe wśród multi-apperów | Android/Windows | EntreCourier |
| **Solo** | Smart Schedule + gwarancja wypłaty | $3,99-$19,99/mc + kredyty | https://www.worksolo.com/ |
| **Para** | Tip predictor (ukryte napiwki) | Darmowa | https://www.withpara.com/ |
| **Gridwise** | Tracker zarobków/mil, 150k+ kierowców, 22 platformy | Freemium | https://gridwise.io/ |
| **Hurdlr / TripLog** | Mileage + podatki | Free / **$5,99/mc** | ShiftTracker |
| **Stride** | Mileage + podatki | **Darmowa** | ShiftTracker |

**Wniosek:** istnieje płacąca kategoria ($6-20/mc), ale prawie każdy konkurent albo wymaga logowania do konta (ryzyko bana), albo skupia się na podatkach/mileage (USA). Nisza **darmowy + zero-network + PL/UA/EN + sam werdykt-overlay bez konta** jest realnie nieobsadzona w Europie/Polsce.

## 4. Reakcje na "order screener / overlay / tip predictor" - co chwalą, czego się boją
**Chwalą:** "najbardziej innowacyjny koncept" (o liczeniu $/h Solo); Mystro 4.x gwiazdki, lojalna baza ("nie trzeba dłubać w telefonie, apka odsiewa śmieci"); sam pomysł "instant $/milę na ekranie" = dokładnie ich wishlist.

**Boją się / zniechęca ich (KLUCZOWE):**
- **BAN/deaktywacja za apki czytające dane platformy.** Umowa DoorDash (sekcja XIII) zakazuje "scraping... automated process and/or device to scrape, copy, index, frame, **monitor**" danych. Pod to podpadają stare wersje pokazujące wypłatę, DUH oraz Para/Solo/Gridwise (logowanie do konta).
- **Nieufność do oddawania dostępu do konta** obcej firmie (główny hamulec Solo/Para/Gridwise).
- **"Feature umiera po aktualizacji platformy"** - udokumentowany przypadek: DoorDash zmienił format danych i **zabił tip-predictor Para**, userów ubyło "substantially". To dokładnie ryzyko apki czytającej cudzy ekran.
- **Zawodność trackingu** - Solo łapał tylko ~51% mil w teście; źle liczy = traci zaufanie.

> **NAJWAŻNIEJSZY insight produktowo-marketingowy:** hasło "bez bana, bo bez API/konta" jest **częściowo** prawdziwe - eliminuje wektor "powiązanie konta" (ten, co straszy userów Solo/Para/Gridwise). ALE czytanie ekranu przez Accessibility **może podpadać pod tę samą klauzulę anty-"monitoring"**. Rekomendacja: **nie obiecywać twardo "zero ban"**; pozycjonować jako "**nie logujemy się do Twojego konta, nie ruszamy API, dane zostają na telefonie (zero-network)**" + uczciwie o Accessibility ("apka tylko odczytuje to, co i tak widzisz na ekranie; nic nie wysyła"). To rozróżnienie jest mocniejsze i bezpieczniejsze niż gołe "no ban".

## 5. Język / argumenty, które do nich trafiają
- "$/milę i $/h liczone za Ciebie - w sekundę wiesz: brać czy odpuścić."
- "Przestań wozić śmieciowe zlecenia." (odsiewanie no-tip / long-distance for peanuts)
- "Nie logujemy się do Twojego konta. Nic nie wysyłamy. Dane zostają na telefonie." (bije w strach #1 + prywatność - mocniejsze niż "no ban")
- "Twój próg, Twoje zasady" - ustaw min. stawkę, resztę apka oznacza na czerwono.
- "Za darmo. Bez subskrypcji." (konkurenci $6-20/mc)
- "Bez liczenia w głowie po 10 godzinach jazdy."
- Unikać obietnic "guaranteed pay" (Seattle pokazał, że gwarancje bywają zawodne i regulacyjnie grząskie).

## 6. Źródła (działające, faktycznie otwarte)
- EntreCourier - deaktywacje za Dash Utility / DUH / Para: https://entrecourier.com/delivery/gig-delivery-platforms/doordash/doordash-strategies/deactivation-dash-utility-5-63-6-dash-utility-doordash/
- EntreCourier - czy tip-transparency Para umarło: https://entrecourier.com/uncategorized/is-para-tip-transparency-dead/
- EntreCourier - recenzja Solo (nieufność do dostępu do konta): https://entrecourier.com/delivery/delivery-strategies/delivery-tools/solo-app-review-mileage-tracking/
- Para - "ALWAYS decline these orders": https://www.withpara.com/blog/always-decline-these-orders
- Maxymo (najbliższy konkurent): https://maxymoapp.com/
- Mystro + cennik: https://mystrodriver.com/ · https://help.mystrodriver.com/article/121-what-does-mystro-cost
- Solo: https://www.worksolo.com/ · recenzja: https://therideshareguy.com/solo-app-review/
- The Rideshare Guy - multi-apping: https://therideshareguy.com/how-to-drive-for-multiple-delivery-apps/
- ShiftTracker - porównanie apek 2026: https://shifttrackerapp.com/blog/best-apps-for-delivery-drivers-in-2026-mileage-taxes-earnings-compared
- Gridwise: https://gridwise.io/
- Zarobki kurierów PL: https://www.mycycle.pl/en/blog/ile-zarabia-kurier-w-dostawie · https://avalon-logistics.pl/en/news/glovo-uber-eats-or-wolt/
- Seattle - gwarantowana stawka nie zadziałała: https://theconversation.com/seattle-tried-to-guarantee-higher-pay-for-delivery-drivers-heres-why-it-didnt-work-as-intended-276576

## Subreddity do samodzielnej weryfikacji (otworzyć w przeglądarce)
`r/doordash_drivers`, `r/UberEATS`, `r/grubhubdrivers`, `r/couriersofreddit`, `r/AmazonFlexDrivers`, `r/Deliveroos`, `r/Wolt`, `r/Glovo`, `r/BoltFood`, `r/Spark`.
Przykład wyszukiwania: `reddit.com/r/doordash_drivers/search?q=per%20mile%20decline&restrict_sr=1`

## 7. Czego NIE potwierdzono (uczciwie)
- Bezpośrednich linków do pojedynczych wątków Reddit (reddit zablokowany; sentyment = synteza, nie cytat z posta).
- Polskich/EU wątków Wolt/Glovo/Bolt na Reddicie (wyszukiwarka US-only; głosy EU słabo wychodzą) - luka do dobicia ręcznie na PL grupach FB.
- **"Żre baterię" / "psuje się po każdej aktualizacji systemu"** jako *cytowanego* bólu - NIE potwierdzono w źródłach. Logicznie dotyczy każdej apki overlay+Accessibility (a przypadek Para potwierdza "śmierć po aktualizacji platformy"), ale to **hipoteza do walidacji**, nie potwierdzony głos kuriera.


---

# CZĘŚĆ IV - WERYFIKACJA KLUCZOWYCH TWIERDZEŃ

Werdykty: CONFIRMED = potwierdzone, PARTLY = w większości tak (z doprecyzowaniem), REFUTED = obalone.

- **PARTLY** - Gridwise to lider kategorii (asystent kierowcy gig) - ~1 mln instalacji, 4.6 gwiazdki, ~12 tys. ocen, ~650 tys. aktywnych userow. Tracker zarobkow/przebiegu/podatkow + analityka. Freemium: Gridwise Plus 9,99 USD/mc lub 71,99 USD/rok. NIE ocenia pojedynczego zlecenia na zywo.
  - *Doprecyzowanie:* Gridwise to czolowa apka w kategorii asystentow kierowcy gig (najwyzsze oceny w kategorii wg ich bloga): ~1 mln pobran (Google Play "500k+", szacunki ~990 tys.), 4.6 gwiazdki na Androidzie (4.9 na iOS), ~12 tys. ocen, spolecznosc 650 tys.+ kierowcow (liczba laczna, nie scisle "aktywni miesiecznie"). Tracker zarobkow/przebiegu/podatkow + analityka (zl/h, eventy w miescie, dane lotnisk, benchmark). Freemium: Gridwise Plus wg aktualnego cennika 15 USD/mc lub ~9 USD/mc przy platnosci rocznej (~108 USD/rok) - dawne 9,99/71,99 USD nieaktualne. Analizuje dane po fakcie, nie ocenia pojedynczego zlecenia na zywo.
- **PARTLY** - Mystro (automat overlay/accessibility, auto-accept/decline) - 100K+ instalacji, ~5,5 tys. ocen, droga subskrypcja ~18-20 USD/mc i fala skarg na psucie się po aktualizacjach platform. Szczegół: overlay + auto-akceptacja wg filtrów, Android Accessibility Services; skargi 2025 (nie filtruje od października, walka o overlay z Uberem, czarny ekran/reinstall, offline przy odrzuceniu, drogo, support tylko na Discordzie); ocena 3.33 (AppBrain) vs 4.8 (Play).
  - *Doprecyzowanie:* Mystro (automat: overlay + Android Accessibility Services, auto-accept/decline wg filtrów stawka/dystans, auto-offline po przyjęciu) - 100K+ instalacji na Google Play. Subskrypcja droga, ~19 USD/mc (lub 140 USD/rok). Realna fala skarg: walka o overlay z Uberem, filtry działają zawodnie, Lyft zablokował apkę (raporty 2025), "drogo za efekt". Oceny się rozjeżdżają między platformami: Android nisko (Google Play ~3.63 z ~4,4 tys. ocen; AppBrain 3.33 z ~5,5 tys.), iOS wysoko (App Store ~4.5 z ~7,4 tys. ocen). UWAGA: oryginalne "4.8 na Play" jest błędne - Play ma ~3.63; wysokie noty są na iOS. Szczegółowe skargi typu "czarny ekran/reinstall", "offline przy odrzuceniu", "support tylko na Discordzie", "nie filtruje od października" - prawdopodobne, ale niezweryfikowane (podane źródło justuseapp niedostępne).
- **PARTLY** - Maxymo i DUH od tego samego studia (Middleton Tech) - najblizsi konkurenci: overlay + accessibility, tanio. Maxymo: trip optimizer overlay, auto-accept/decline, Uber/Lyft/DoorDash/UberEats/Grubhub/Instacart/Spark, Android 8+, trial+subskrypcja (ceny nieujawnione). DUH: tylko Android, accessibility+overlay, filtry accept/decline, darmowy + premium ~0,33 USD/dzien (~10 USD/mc), DoorDash, dystrybucja spoza Google Play (acceptordecline.com).
  - *Doprecyzowanie:* Maxymo i Driver's Utility Helper (DUH) - oba od studia Middleton Technologies LLC - najblizsi funkcjonalnie konkurenci: overlay + accessibility czytajacy oferte, tanio. Maxymo: trip optimizer overlay, auto-accept/decline (automatyzacja tylko Android), wspiera Uber/Lyft/DoorDash/UberEats/Grubhub/Instacart/Spark (oraz Didi/Ola/Curri), Android 8.0+, darmowy trial + subskrypcja 9,99 USD/mies. lub 99,99 USD/rok (ceny jawne w supporcie, nie na stronie glownej). DUH: tylko Android, accessibility + overlay, filtry accept/decline bez dotykania telefonu, darmowy z reklamami + premium ~0,33 USD/dzien (~10 USD/mc) lub wersja bez reklam 2,99 USD/mc, skupiony na DoorDash, dystrybuowany spoza Google Play (acceptordecline.com).
- **PARTLY** - Solo (predykcje + GWARANCJA zarobku) - subskrypcja 12-24 USD/mc, łączy konta gig, wypłaca różnicę jeśli zarobisz mniej niż prognoza. Smart Schedule, auto-tracking przebiegu, gwarancja w ~100 metropoliach US. Skargi: gwarancja podchwytliwa (aktywne godziny, min. 1 zlecenie/h), problemy z trackingiem i supportem. Wypłacił ponad 14 mln USD gwarancji.
  - *Doprecyzowanie:* Solo (predykcje + gwarancja zarobku) - subskrypcja faktycznie 10-20 USD/mc (Basic 10, Pro 15, Pro Plus 20; taniej przy planie rocznym, ok. 8-15 USD/mc), nie 12-24 USD/mc. Łączy konta gig i wypłaca różnicę, jeśli w zaplanowanej (przez Smart Schedule) godzinie zarobisz mniej niż prognoza. Auto-tracking przebiegu, gwarancja w ~100 metropoliach US. Uwaga: zablokowanie gwarantowanej godziny kosztuje dodatkowe kredyty (Pro Plus: 60 darmowych/mc, potem ~0,40 USD/szt.). Skargi potwierdzone: gwarancja podchwytliwa (aktywne godziny, min. 1 zlecenie/h, brak multi-appingu, pozostanie w mieście min. 70% czasu), zawodny tracking i problemy z supportem. Wypłacono ponad 14 mln USD gwarancji.
- **CONFIRMED** - Brak lokalnego (PL/UA) konkurenta robiącego ocenę opłacalności zlecenia; wyszukiwanie PL/RU zwraca tylko artykuły o zarobkach i nabór do flot. US-owe apki (Gridwise/Mystro/Maxymo/Para/Solo) liczą w USD i wspierają DoorDash/UberEats/Grubhub/Lyft, nie Wolt/Glovo/Bolt Food.
  - *Doprecyzowanie:* Brak lokalnego (PL/UA) konkurenta robiącego zewnętrzną ocenę opłacalności zlecenia dla Wolt/Glovo/Bolt Food - wyszukiwanie po polsku, rosyjsku i ukraińsku zwraca tylko poradniki o zarobkach i strony partnerów flotowych rekrutujących kurierów (część UA-języczna). Zarobki wg źródeł: Wolt ~35-55 zł/h (śr. ~40, Śląsk 50+), Bolt Food ~25-45 zł/h. US-owe apki (Gridwise/Solo/Mystro/Para) liczą w USD i wspierają DoorDash/UberEats/Grubhub/Lyft/Instacart, nie Wolt/Glovo/Bolt Food. Jedyny analogiczny mechanizm 'podpowiadania drogich zleceń' to wbudowana funkcja Yandex Доставка w Rosji - natywna w platformie, nie nakładka, poza rynkiem PL/UA.
- **PARTLY** - Wspólny mianownik skarg na apki overlay dla kurierów/kierowców: psują się po aktualizacjach platform, walka o overlay, zużycie baterii, drogie subskrypcje. SZCZEGÓŁ: Mystro (czarny ekran / nie może wejść online, walka o overlay z Uberem, ~18-20 USD/mc), Gridwise (bateria), Stride (stabilność po update), Para (błędy połączenia), support słaby (Mystro - tylko Discord).
  - *Doprecyzowanie:* Wspólny mianownik skarg na apki-nakładki (overlay) dla kierowców/kurierów: psują się po aktualizacjach systemu/platform (Stride, Mystro), walka o nakładkę i przełączanie online/offline z apkami platform (Mystro vs Uber/Lyft), zużycie baterii przez śledzenie w tle (Gridwise), oraz koszt subskrypcji. Szczegóły potwierdzone: Mystro - "can't go online"/konflikt z Uberem, ~19 USD/mc (139,99 USD/rok, też tygodniowo 6,99 USD i 0,20 USD/przejazd); Gridwise - drenaż baterii; Stride - utrata stabilności i danych po aktualizacjach; Para - błędy połączenia/wylogowywanie. POPRAWKA: Mystro NIE ma "tylko Discord" - oferuje live chat, Discord i e-mail (support@mystrodriver.com), więc akurat support Mystro nie jest tu trafnym przykładem słabości.
- **CONFIRMED** - Brak bezposredniego konkurenta-nakladki na rynku polskim - OrderPilot wypelnia realna luke.
  - *Doprecyzowanie:* Na polskim rynku brak bezposredniego konkurenta-nakladki, ktora czyta ekran i ocenia oplacalnosc pojedynczego zlecenia (Glovo/Wolt/Bolt) w czasie rzeczywistym - OrderPilot wypelnia realna luke. Zastrzezenia: (1) globalnie istnieja analogiczne nakladki dla kierowcow gig (Mystro, Gridwise, Para), ale dla USA i platform Uber/Lyft/DoorDash, nie dla polskich apek dostawczych; (2) w Polsce byly narzedzia trzecich stron dla kurierow (bot do rezerwacji godzin, spoofing GPS), o innej funkcji - nie oceniaja oplacalnosci zlecenia. Zadne nie jest bezposrednim konkurentem OrderPilota.
- **PARTLY** - W Hiszpanii (rynek startowy założyciela) też brak nakładki oceniającej opłacalność dla repartidores. Wyszukiwanie ES zwróciło tylko porównywarki cen jedzenia, oficjalne apki i artykuły o zarobkach (10-80 EUR/dzień). Glovo ma korzenie w Barcelonie - silny rynek, brak narzędzia tego typu.
  - *Doprecyzowanie:* W Hiszpanii brak dedykowanej nakladki oceniajacej oplacalnosc zlecen dla kurierow DOSTAWCZYCH (Glovo/Uber Eats) - to sie potwierdza. Ale: (a) niemal identyczne narzedzie typu overlay ISTNIEJE dla kierowcow przewozu osob (np. Ruta Rentable dla Uber/DiDi) w Ameryce Lacinskiej, wiec koncept jest juz zwalidowany rynkowo; (b) hiszpanski rynek dostaw zmienil sie strukturalnie - od 1 lipca 2025 Glovo dziala wylacznie na kurierach ETATOWYCH (laboralizacja wg Ley Rider), co obniza popyt na narzedzie typu "czy oplaca sie przyjac to zlecenie". Wniosek "Barcelona/Glovo = silny otwarty rynek na takie narzedzie" nalezy zlagodzic.
- **CONFIRMED** - Godziny szczytu i mnożniki/bonusy to obsesja kurierów - decyzja brać czy nie zależy od pory i współczynnika (Pokatun: w szczycie zlecenie za 80 zamiast 50 hrywien; UA/PL blogi: stawki rosną do 40-60 zł/h w szczycie, weekendy, zła pogoda).
  - *Doprecyzowanie:* Godziny szczytu, mnożniki stref (x1.3-x1.5) i bonusy to kluczowy czynnik decyzji kurierów - w szczycie/weekend/złej pogodzie to samo zlecenie jest warte więcej. W Polsce stawki rosną z ~30-40 zł/h do 50-60 zł/h w szczycie (potwierdzone: Glovo, Uber). W Ukrainie baza za dostawę to 50-150 hrywien i rośnie z współczynnikiem w szczycie (Glovo UA; mechanizm "podbijania współczynnika" jak w Yandex/Kuper). Konkretny serwis "Pokatun" i para 50->80 hrywien to wiarygodny, ale niezweryfikowany bezpośrednio przykład - mieści się w potwierdzonym zakresie.
- **PARTLY** - Polskie kanały YT już robią treści Glovo vs Bolt - gdzie zarobisz więcej. Kanał Eternis publikuje krótkie filmy typu "Gdzie zarobisz więcej jako kurier - Glovo czy Bolt Food?" (mniejsze zasięgi). Kanał Alpha (zarobki Wolt/Glovo) ma 10 224 wyświetleń.
  - *Doprecyzowanie:* Istnieją polskojęzyczne treści YouTube porównujące zarobki kurierów (Glovo vs Bolt Food vs Wolt) - potwierdzony przykład: film "Gdzie zarobisz więcej jako kurier - Glovo czy Bolt Food?" (URL _0C6XyGl6Dw). UWAGA: publikuje go Eternis, czyli firma rekrutacyjno-rozliczeniowa flot kurierskich (nie niezależny twórca), więc to content marketingowy konkurenta/pośrednika, nie neutralna nisza twórcza. Szczegół o kanale "Alpha" i 10 224 wyświetleniach pozostaje niepotwierdzony - nie znaleziono takiego kanału ani liczby; potraktować jako niezweryfikowany.

---

# CZĘŚĆ V - PODSUMOWANIA WĄTKÓW RESEARCHU

### konkurencja-global
Globalna/US konkurencja dzieli sie na 3 grupy: (1) trackery zarobkow/przebiegu/podatkow (Gridwise, Solo, Stride) - licza ile zarobiles po fakcie, NIE oceniaja zlecenia w czasie rzeczywistym; (2) predyktory napiwkow (Para) - pokazuja ukryty napiwek PRZED przyjeciem, ale LACZA SIE z kontem kuriera przez API i przez to zostaly zablokowane przez DoorDash/Uber/Lyft; (3) automaty overlay/accessibility (Mystro, Maxymo, Driver's Utility Helper) - czytaja ekran i auto-akceptuja/odrzucaja zlecenia. OrderPilot jest najblizej grupy 3, ale z kluczowa roznica: tylko DORADZA (kolor + zl/h + zl/km), nie automatyzuje klikania i nie dotyka konta - czyli ma jeszcze nizsze ryzyko bana niz Mystro/Maxymo. NAJWAZNIEJSZA LUKA: wszystkie te apki sa 100% US-centryczne (USD, DoorDash/Uber Eats/Grubhub/Lyft); zadna nie liczy w PLN ani nie wspiera Wolt/Glovo/Bolt Food. Wyszukiwanie po polsku i rosyjsku nie wykazalo ZADNEGO lokalnego konkurenta robiacego ocene oplacalnosci zlecenia - tylko artykuly o zarobkach i nabor do flot. To wolna nisza dla OrderPilota (PL+UA+EN, PLN, brak konta, zero-network, darmowy).
Rekomendacje: Uczyn zero ryzyka bana glownym przekazem (ASO, opis w Play, posty na forach): pokaz konkretnie ze - inaczej niz Para/Mystro/Solo - OrderPilot NIE loguje sie do konta, NIE uzywa API, NIE automatyzuje klikania, tylko czyta ekran lokalnie i nic nie wysyla. To jedyna w kategorii kombinacja, potwierdzona przez ekspertow i przez fakt blokad Para/Uber/Lyft.; Zajmij wolna nisze PL/UA: brak lokalnego konkurenta. Zoptymalizuj ASO pod PL i UA/RU slowa kluczowe (kurier, oplacalnosc, Wolt, Glovo, Bolt Food, ile zl na godzine, kurier UA/RU). Promuj w ukrainskojezycznych grupach Telegram/Facebook kurierow w Polsce - tam najwieksza gestosc grupy docelowej i zero kosztu.; Pozycjonuj jako DORADCA, nie AUTOMAT - to odroznia od Mystro/Maxymo/DUH (auto-accept) i jest bezpieczniejsze. Komunikat: pokazuje czy brac, decyzja Twoja - nizsze ryzyko, zgodne z regulaminami, mniej walki o overlay.; Potraktuj niezawodnosc overlay/OCR jako priorytet #1 produktowy: #1 zabojca recenzji konkurentow to przestalo dzialac po aktualizacji platformy. Zbuduj szybki proces lapania zmian ekranow Wolt/Glovo/Bolt Food/Uber Eats i wypuszczania patcha w dni, nie tygodnie. Zadbaj o niskie zuzycie baterii (czesta skarga na Gridwise/overlaye).; Wykorzystaj darmowy jako dzwignie wzrostu (Stride: 2,6 mln userow dzieki free; konkurenci biora 10-24 USD/mc). Na teraz zero monetyzacji = maksymalna adopcja. Pomysl o pozniejszym modelu bez psucia zaufania (np. opcjonalny pro z trackingiem zarobkow, NIGDY reklamy ani sprzedaz danych - prywatnosc jest wyroznikiem).; Rozwaz pozniej lekki hak retencyjny wzorem Gridwise/Solo: prosty licznik ile dzis zarobiles / zl/h dzis liczony lokalnie z odczytanych zlecen (bez konta). Zamienia apke z patrze raz na wracam codziennie, bez naruszania modelu zero-network.; Zbieraj i pokazuj dowod spoleczny w jezyku kurierow: screenshot belki z zl/h i zl/km na realnym zleceniu Wolt/Glovo to material na posty/recenzje. Konkurenci chwala sie +30% zarobkow - OrderPilot moze pokazac proste: to zlecenie = 12 zl/h, odrzuc; tamto = 55 zl/h, bierz.

### konkurencja-pl-eu
OrderPilot ma realną, dużą lukę na rynku polskim (i szerzej w Europie kontynentalnej). W żadnym z przeszukanych języków (PL, UA, RU, ES, EN) nie znalazłem aplikacji-nakładki (overlay), która w czasie rzeczywistym czyta ekran kuriera i ocenia opłacalność konkretnego zlecenia w zł/h i zł/km dla Glovo/Wolt/Bolt Food/Uber Eats. Koncept jest sprawdzony za granicą: w USA istnieją GigU (czyta ekran, kolory zielony/czerwony, Android, ~6,95 USD/mc), Para, Mystro, Gridwise, Solo - ale wszystkie celują w rynek amerykański i głównie w przewóz osób (Uber/Lyft/DoorDash), nie obsługują polskich/europejskich platform dostaw jedzenia ani waluty PLN, a większość jest płatna. W Polsce "konkurencja" to coś zupełnie innego niż OrderPilot: kalkulatory rozliczeniowe partnerów flotowych (CABIO, MyCycle) działające z tyłu (back-office, po fakcie), same apki platform (pokazują kwotę brutto przed przyjęciem, ale nie liczą znormalizowanego zł/h ani zł/km z uwzględnieniem dystansu/kosztów) oraz blogi o "łączeniu aplikacji" (multi-apping). Wniosek: OrderPilot jest prawdopodobnie pierwszym/jedynym narzędziem tego typu na rynku PL - to mocny fundament pod pozycjonowanie ("jedyna darmowa nakładka, która mówi czy zlecenie się opłaca"). Ryzyko: niska bariera wejścia - ktoś (np. GigU) może wejść na PL, więc trzeba szybko budować rozpoznawalność i społeczność.
Rekomendacje: Pozycjonuj OrderPilota jako 'pierwsza/jedyna darmowa nakładka, która w czasie rzeczywistym mówi czy zlecenie się opłaca (zł/h, zł/km)' - przewaga 'first mover' na PL jest realna i potwierdzona brakiem konkurencji.; Główny komunikat bezpieczeństwa: 'czyta ekran, nic nie automatyzuje, nie używa API platform = zero ryzyka bana' - to wprost odróżnia od modelu Mystro (auto-accept) i rozwiewa największą obawę kuriera.; Wykorzystaj USA jako dowód popytu w komunikacji ('w USA kurierzy płacą za podobne narzędzia ~7-19 USD/mc - u nas masz to za darmo'), powołując się na GigU/Para/Mystro jako kategorię, nie kopiując nazw.; Celuj mocno w ukraińskich kurierów w PL: ASO i materiały po ukraińsku/rosyjsku, bo tam konkurencja zerowa, a grupa ogromna - apka już ma UA/EN.; Nie konkuruj z 'platforma pokazuje kwotę' tylko z 'czy warto' - w marketingu i na ekranie pokazuj wprost przeliczenie brutto -> zł/h i zł/km z dystansem powrotnym, bo tego apki platform nie robią.; Rozważ partnerów flotowych (CABIO, MyCycle, Evelstar, Flow Apps, MB Partner) jako kanał dystrybucji do setek kurierów naraz - nie są konkurentami, tylko sąsiednią kategorią back-office.; Działaj szybko nad rozpoznawalnością/społecznością - bariera wejścia jest niska (GigU mógłby wejść na PL), więc przewaga to czas i bycie 'pierwszym znanym' wśród kurierów (grupy FB/Telegram kurierów PL i UA).; Przy okazji pobytu założyciela w Hiszpanii: zebrać feedback od repartidores i rozważyć wersję ES/EUR jako drugi rynek po ugruntowaniu PL.

### youtube-50
Przejrzałem realny sygnał z YouTube o zarobkach/optymalizacji pracy kuriera w 3 językach (EN globalnie, PL i UA/RU dla Polski). Kluczowy wniosek: najpopularniejsze filmy (najlepszy 1,6 mln wyświetleń) UCZĄ kurierów RĘCZNIE robić dokładnie to, co OrderPilot robi automatycznie - liczyć czy zlecenie się opłaca (zł/h i zł/km, dobre vs złe zlecenia, patrz na kwotę i kilometry, nie na napiwek). To mocna walidacja produktu. W USA istnieje już apka Para (pokazuje ukryty napiwek/realną wypłatę PRZED akceptacją) - dowód popytu, ale Para grozi banem/dezaktywacją, bo rusza dane API. Tu OrderPilot ma przewagę nie do podrobienia: czyta tylko ekran -> zero ryzyka bana, i to argument, który u kurierów elektryzuje. Rynek PL to ogromna baza kurierów z Ukrainy (vlogi UA mają dziesiątki tysięcy wyświetleń), którzy multi-appują (Glovo+Bolt naraz), gonią za zł/h i godzinami szczytu, porównują kurierkę do fabryki. Najlepsze kanały dotarcia to ukraińscy i polscy vlogerzy-kurierzy (Pokatun, ЩОЯК, RazeDen, Eternis).
Rekomendacje: Ustaw zero ryzyka bana jako GŁÓWNY komunikat (czyta tylko ekran, nie rusza API/danych platformy) - bezpośrednia odpowiedź na realny strach przed dezaktywacją, który zabija konkurenta Para w USA. Para = dowód popytu, OrderPilot = bezpieczna wersja.; Komunikuj wartość w jednostkach kuriera: zł/godzinę i zł/km, nie trać czasu na nieopłacalne kursy, belka mówi w sekundę: brać czy odrzucić - zamiast mówić o technologii/OCR. Hook: Przestań liczyć opłacalność w głowie.; Priorytet językowy: UA/RU najpierw, potem PL. Najbardziej zaangażowani odbiorcy kurierki w Polsce to migranci z Ukrainy oglądający vlogi UA/RU. Materiały, opis w Google Play i treści organiczne rób w tych językach.; Dotarcie barterem (budżet ~0): odezwij się do twórców w niszy (ЩОЯК, Pokatun, RazeDen, Serhii Marchenko, Eternis) z propozycją pierwsza recenzja/demo darmowej belki w ich kolejnym poradniku jak zarabiać więcej jako kurier.; Podkreślaj multi-apping: jedna belka nad Uber Eats, Wolt, Glovo i Bolt naraz - kurierzy pracują na kilku apkach jednocześnie i potrzebują szybkiej decyzji ponad wszystkimi.; Nakręć/zleć krótki film-demo pokazujący belkę w akcji na realnym zleceniu (zielony/żółty/czerwony + zł/h + zł/km). Format which orders to accept / jakie zlecenia brać jest sprawdzony i masowo oglądany - to naturalny viral hook.; Na później rozważ funkcje premium widoczne w sygnale (podsumowanie zł/h sesji, podpowiedź godzin szczytu) - jako bezpłatny dodatek utrzymujący przewagę darmowy, a nie kopię płatnego Gridwise.; Jeśli kiedyś wejście na EN/rynki globalne (apka widoczna w 177 krajach): najpierw dodać obsługę walut (apka liczy w PLN) - bez tego globalny zasięg nie przełoży się na użycie.

### reddit-forums-en
(brak wyniku)

### spolecznosci-pl-ua
Kurierzy dowozu jedzenia w Polsce zbieraja sie glownie w trzech miejscach: (1) grupy na Facebooku (ogolnopolskie Glovo Polska, Wolt Polska, multi-app oraz miejskie/dwujezyczne PL-RU/UA), (2) chaty/kanaly na Telegramie - najwazniejszy to wielojezyczny @glovo_uber_wolt (PL/UA/RU, ~3,9 tys. realnych kurierow w PL), (3) ekosystem influencera Dexterowski (YouTube 200 tys.+, TikTok 100 tys.+, Discord Dexterawka ~10 tys.) - najsilniejszy pojedynczy punkt dotarcia. Bardzo duzo kurierow to Ukraincy/Rosjanie - tresci ida po polsku, ukrainsku i rosyjsku, wiec wielojezycznosc OrderPilota (PL/UA/EN) to atut, ale warto dodac RU. Uwaga: wiekszosc grup FB ostro tepi reklame (ban/usuwanie), a admini Telegrama czesto kaza placic za posty - dlatego najtansza i najbezpieczniejsza droga to influencer Dexterowski oraz wejscie jako prawdziwy czlonek/kurier z darmowym narzedziem, nie jako spam.
Rekomendacje: CEL #1 - influencer Dexterowski: napisac do niego (YouTube/TikTok/Discord) z darmowa apka - liczy zl/h i zl/km na belce nad apka platformy, zero bana bo nie rusza API. Zaproponowac test i ew. pokazanie w filmie. Najtansza droga o najwiekszym zasiegu.; Wejsc organicznie na Discord Dexterawka (discord.gg/dexterawka) jako kurier i podzielic sie narzedziem w watku porad - nie jako reklama, lecz zrobilem to dla siebie, moze sie przyda.; Telegram @glovo_uber_wolt (3,9 tys., PL/UA/RU): napisac do admina o zgode/koszt posta; przygotowac krotki komunikat w 3 jezykach (PL, UA, RU).; Facebook: postowac jako prawdziwy czlonek (nie firma) w Wolna Grupa Niebieskich Kurierow, Glovo Polska, Wolt Polska i grupach multi-app - format darmowe, bez konta, nic nie wysyla, sprawdza czy zlecenie sie oplaca. Najpierw przeczytac regulamin / zapytac admina, bo reklama = ban.; Dodac wersje rosyjska (RU) do interfejsu/komunikacji - duza czesc kurierow to osoby RU/UA-jezyczne; obecne PL/UA/EN to baza, RU domyka rynek.; W kazdym poscie eksponowac 3 przewagi: (1) ZERO ryzyka bana (nie rusza API platform), (2) DARMOWA i prywatna (zero-network, bez konta, bez reklam), (3) dziala dla Uber Eats/Wolt/Glovo/Bolt Food NARAZ - jedna belka.; Dodac narzedzie do Kurierpedia (kurierpedia.pl) jako pozycje w sekcji porad/narzedzi - pasywne, dlugoterminowe zrodlo pobran.; Na pozniej (gdy zalozyciel bedzie w PL za ~2 tyg.): kontakt z partnerami flotowymi (Flow Apps, MB Partner, City Drive) - moga polecic darmowa apke kurierom; apka im nie zagraza, a podnosi zarobki ich ludzi.

### dystrybucja-zero-budzet
Dla darmowej, niszowej apki-narzędzia robionej solo z budżetem ~0 najskuteczniejsza kolejność to: (1) ASO po polsku + ukraińsku + angielsku jako fundament za 0 zł, (2) seeding w istniejących społecznościach kurierów (Telegram, Discord, grupy FB) z regułą wartość-najpierw, (3) mikroinfluencerzy-kurierzy na YouTube/TikTok, którzy dostają apkę za darmo i ją recenzują. Rynek kurierów dowozu w Polsce ma gotowe, gęste i bardzo aktywne kanały: czaty Telegram po 4-5 tys. osób, Discord Dexterawka ~10 tys. osób, kanały YouTube od 1 tys. do 500 tys. subskrybentów - dotarcie dokładnie do grupy docelowej praktycznie za darmo. Krótkie wideo (TikTok/Shorts/Reels) z formatem "czy to zlecenie się opłaca?" to dziś najszybsza dźwignia organicznego wzrostu apek. Programy poleceń i ulotki QR pod restauracjami/strefami kurierów to dobre wzmacniacze, ale wtórne; ulotki dopiero gdy założyciel będzie w PL. UWAGA: model zero-network/brak konta utrudnia klasyczny referral (brak jak śledzić poleceń bez konta) - to trzeba obejść projektowo.
Rekomendacje: TYDZIEŃ 1 (0 zł, najwyższy zwrot): Przepisz listing Google Play pod ASO. Tytuł z głównym słowem kluczowym (np. 'OrderPilot: kurier zł/h zł/km' do 30 znaków), krótki opis z benefitem (≤80 znaków), długi opis z frazami long-tail (kurier, dostawca jedzenia, Glovo/Wolt/Bolt/Uber Eats, czy zlecenie się opłaca, zarobki kuriera). Pierwsze 3 zrzuty muszą pokazać belkę zielony/czerwony nad apką platformy.; Zrób PEŁNĄ lokalizację listingu po ukraińsku i rosyjsku (osobne słowa kluczowe, nie tłumaczenie) - otwiera ogromną grupę ukraińskich kurierów w PL bez kosztu.; Wejdź jako PRAWDZIWY członek do: czatu Telegram 'КУРЬЕРЫ GLOVO WOLT UBER BOLT POLAND' (~4,7 tys.), kanału Glovo PL, czatów miejskich (Wrocław, Trójmiasto), Discorda 'Dexterawka' (~10 tys.) i grup FB ('Wolna Grupa Niebieskich Kurierów', 'Kurierzy Dostawcy Eats Glovo Wolt'). Przez 1-2 tyg. tylko pomagaj, potem wrzuć JEDEN wartościowy post: poradnik 'jak szybko ocenić, czy zlecenie się opłaca' + apka jako narzędzie. Reguła 90/9/1.; Napisz do 10-15 mniejszych/średnich kurierskich kanałów YouTube/TikTok (Dexterowski, Deliverka, SwojąDrogą, Luters, Reysowaty, Michał Górka, Kurier z Holywood; Yellowbox jako duży reach play). Zaoferuj apkę za darmo do szczerego testu - bez płacenia. Format: 'testuję apkę, która mówi, czy zlecenie się opłaca - sprawdzam, czy ma rację'.; Uruchom własny kanał TikTok/Reels/Shorts z treścią FACELESS: nagranie ekranu, belka zmienia kolor nad Glovo/Wolt, hook 'bierzesz to za 6 zł i 4 km?'. 3-5 krótkich wideo tygodniowo, po PL/UA/EN, każde z linkiem do Play. Cel: pętla Content→Conversation→Install.; Dodaj w apce przycisk 'Poleć kumplowi-kurierowi', generujący gotowy link do Google Play + krótki tekst do wklejenia (PL/UA/EN). Skoro brak kont uniemożliwia śledzenie referrali z nagrodą, postaw na maksymalnie prostą mechanikę dzielenia się.; Po powrocie do PL (~2 tyg.): wydrukuj ulotki/naklejki z trackowanym kodem QR i rozłóż w strefach oczekiwania kurierów - pod popularnymi restauracjami Glovo/Wolt, McDonald's/KFC, strefy odbioru. Hasło: 'Sprawdź, czy zlecenie się opłaca - darmowa apka, nic nie wysyła, zero ryzyka bana'.; Zrób jednorazowy 'launch tydzień' w katalogach (BetaList, Indie Hackers, AlternativeTo, Capterra + lista z launchdirectories.com) dla backlinków i SEO. Niski wysiłek, robisz raz; licz na widoczność w Google, nie na instalacje.; Pisz build in public: krótkie posty/artykuł o historii apki (solo, nietechniczny founder, zero-network, brak ryzyka bana, darmowa). Rozważ pitch do PL mediów techno (np. Antyweb) - jeden artykuł = duży darmowy zasięg.; Mierz to, co Google teraz nagradza: D7 retencję i stabilność, nie samą liczbę instalacji. Wdrażaj features zgłaszane przez kurierów i często aktualizuj - to podbija ranking ASO. Reaguj na KAŻDĄ recenzję w Play.

### aso-keywords
Najważniejszy wniosek strategiczny: w Polsce (PL + kurierzy z UA/RU) praktycznie NIE ISTNIEJE wyszukiwana w Google Play kategoria "pomocnik kuriera / skaner opłacalności zleceń". Światowe odpowiedniki (Gridwise ~1 mln instalacji, 4.6 gwiazdki; Para; Maxymo) są wyłącznie anglojęzyczne i nieobecne w polskim/ukraińskim wyszukiwaniu. Sam ASO nie wygeneruje dużego ruchu, bo kurierzy nie wiedzą, że taka apka istnieje i czego szukać - popyt trzeba NAJPIERW wytworzyć (grupy FB, demo wideo), a ASO ma go przechwycić (brand search "order pilot" + zapytania platformowe). Dwie pilne dźwignie: (1) tytuł - obecnie "OrderPilot" nie zawiera ŻADNEGO słowa kluczowego; dodanie "kurier" do tytułu to najsilniejszy pojedynczy ruch (tytuł ma najwyższą wagę indeksowania); (2) problem "order pilot" ze spacją jest już częściowo załatany w short/full description (zrobione 2026-05-23), ale najmocniej rozwiąże go słowo "kurier"/"Order Pilot" w tytule. Realny target to PL + rosyjsko/ukraińskojęzyczni kurierzy W POLSCE - rynek EN/ES jest pozorny, bo apka liczy w PLN. Recenzje na starcie: poproś realnych testerów (Marcin, Dominik, Andrij) o ocenę po pierwszym opłacalnym zleceniu i odpowiadaj na każdą recenzję w 24h.
Rekomendacje: TYTUŁ (najwyższy priorytet, ~48h efekt): zmień z 'OrderPilot' na 'OrderPilot - kurier zł/h' (24 zn.) lub 'OrderPilot: Kurier Uber Wolt' (28 zn.). Łapie frazę kategoryjną i wzmacnia rozpoznanie 'order pilot'. Przetestuj przez Custom Store Listing experiments w Console.; Skup ASO na DWÓCH rynkach: polski (główny listing) + osobne, ręcznie przetłumaczone listingi UA i RU z frazami кур'єр/курьер, доставка, Glovo/Wolt заробіток/заработок. Wstrzymaj EN/ES dopóki apka nie przelicza walut - chroni retencję i ranking.; Dorzuć do long description frazy, których realnie szukają kurierzy: 'ile zarabia kurier', 'zarobki Glovo/Wolt/Uber Eats', 'kalkulator opłacalności zlecenia', 'stawka zł/h i zł/km' - w pierwszych 250 znakach i raz na ~250 znaków dalej, bez upychania.; Przerób zrzuty ekranu: #1 = realna kolorowa belka NAD apką kuriera (zielona, '42 zł/h') z podpisem 'Wiesz w 5 sekund, czy brać zlecenie'; #2 = trzy kolory belki (bierz/zależy/odrzuć); #3 = 'Zero ryzyka bana - tylko czyta ekran, nie używa API'; #4 = 'Darmowa, bez konta, działa offline'. Dodaj feature graphic 1024x500.; Recenzje na starcie: wdroż In-App Review API i pokaż prośbę o ocenę po pierwszym opłacalnym (zielonym) zleceniu. Poproś imiennie Marcina, Dominika i Andrija o 5 gwiazdek + krótki opis po polsku. Odpowiadaj na KAŻDĄ recenzję w ciągu 24h. Nigdy farmowych instalacji.; Zweryfikuj realne wolumeny fraz dla PL w darmowym ASOMobile lub Sonar (trysonar.app) zanim ostatecznie zdecydujesz o tytule - sprawdź kurier, dostawca jedzenia, Glovo, Wolt, order pilot. Po 7 dniach od zmian czytaj Console - Acquisition - Search terms i iteruj.; Pamiętaj o realiach: ASO przechwytuje popyt, ale w PL popyt na 'asystenta kuriera' jeszcze nie istnieje - musisz go tworzyć (grupy FB kurierów PL/UA, demo wideo, krótkie TikToki z belką w akcji). ASO + dystrybucja w grupach działają tylko razem.

### monetyzacja
test summary minimal
Rekomendacje: one; two

### rynek-trendy
Polska jest zdecydowanie najlepszym i najlepiej dopasowanym rynkiem dla OrderPilot: rynek dostaw rosnie szybko (~10 mld zl w 2024-2025, +15,7% przychodow wg Statista w 2025, penetracja ~27% w segmencie posilkow), apka liczy w PLN, a model gig (kurier sam decyduje czy wziac zlecenie) wciaz dominuje. Twarda liczba kurierow to slaby punkt danych - jedyne wiarygodne badanie (Beresewicz/Selectivv, 2021) dawalo ~34 tys. kurierow jedzenia (Pyszne 15,2 tys., Glovo 7,9 tys., Wolt 7,2 tys.); od wojny 2022 rynek i liczba zagranicznych kurierow mocno urosly, wiec realnie dzis to prawdopodobnie kilkadziesiat-100+ tys. (niska pewnosc). Udzial Ukraincow/obcokrajowcow: badanie 2021 mowilo ~65% Polakow (cudzoziemcy w mniejszosci), ale dane sprzed wojny - po 2022 obserwacje wskazuja na duzy naplyw zagranicznych kurierow w wielkich miastach (Warszawa), nadpodaz i spadek stawek od 2024. Hiszpania jako drugi rynek jest RYZYKOWNA: to dom Glovo, ale ustawa Ley Rider przeksztalcila kurierow w etatowcow (Glovo ~14 tys. na etacie, ~21 tys. lacznie), wiec kluczowa funkcja "czy wziac to zlecenie" traci sens, gdy kurier nie wybiera swobodnie zlecen - plus trzeba by lokalizacji w EUR. Dyrektywa UE 2024/2831 (domniemanie zatrudnienia, wdrozenie w PL do XII 2026) to srednioterminowe zagrozenie dla modelu akceptuj/odrzuc - okno na ekspansje jest teraz do ~2027.
Rekomendacje: Skup sie na Polsce jako rynku #1: apka liczy w PLN, model gig (swobodny wybor zlecen) dominuje co najmniej do 2026/2027, a rynek dostaw rosnie ~8%/rok. To najwiekszy realnie adresowalny rynek dla tego produktu bez przerobek.; NIE traktuj Hiszpanii jako drugiego rynku w tym roku, mimo ze zalozyciel tam jest. Powod merytoryczny: Ley Rider przeksztalcila kurierow w etatowcow (Glovo), wiec rdzen produktu czy wziac to zlecenie traci sens; do tego trzeba lokalizacji w EUR. Obecnosc w ES wykorzystaj do obserwacji/researchu, nie do budowy rynku.; Celuj mocno w zagranicznych kurierow (Ukraincy, ale tez inni obcokrajowcy) w duzych miastach - to segment wzrostu, slabiej zna dobre stawki, ma silny word-of-mouth w zamknietych grupach (Telegram, FB). Apka ma juz PL/UA/EN, wiec bariera wejscia jest niska - to przewaga do wyeksponowania.; Dystrybucja przez kurierskie grupy i blogi: grupy Telegram/Facebook kurierow (PL i UA), portale typu dostawca-jedzenia.pl, mycycle.pl, avalon-logistics - tam siedzi cala grupa docelowa za 0 zl. Komunikat: zobacz w 1 sekunde ile to zl/h i zl/km, zanim przyjmiesz zlecenie - przy nadpodazy nie marnuj czasu na nieoplacalne kursy.; Dzialaj szybko, bo okno modelu gig sie zamyka: dyrektywa UE wdrazana w PL do konca 2026. Zbuduj baze uzytkownikow i nawyk korzystania teraz; jednoczesnie zaplanuj wariant wartosci na swiat po-etatyzacji (statystyki zarobkow/godzin, optymalizacja tras), zeby produkt przetrwal zmiane prawa.; Priorytetyzuj jakosc czytania overlayow platform wg realnego wolumenu kurierow: Glovo i Uber Eats (duze), potem Wolt i Bolt Food. Pyszne.pl ma najwiekszy udzial zamowien, ale to inny model - sprawdz czy jego kurierzy sa w grupie docelowej.

### tworcy-influencerzy
(brak wyniku)


---

# CZĘŚĆ VI - ŹRÓDŁA

1. [Gridwise vs Solo - porownanie asystentow gig](https://gridwise.io/blog/gridwise-vs-solo)
2. [Gridwise - Apps on Google Play](https://play.google.com/store/apps/details?id=com.gridwise.app&hl=en_US)
3. [Para App Review 2026 - The Rideshare Guy (blokada DoorDash/Uber/Lyft)](https://therideshareguy.com/para-review/)
4. [Para - Gig Drivers Earn More (App Store)](https://apps.apple.com/us/app/para-gig-drivers-earn-more/id1548322258)
5. [Mystro Driver Reviews 2025 - JustUseApp](https://justuseapp.com/en/app/1524407919/mystro-driver-drive-deliver/reviews)
6. [Mystro Driver - AppBrain (instalacje, oceny)](https://www.appbrain.com/app/mystro-driver-drive-deliver/com.mystrodriver)
7. [Mystro - oficjalna strona](https://mystrodriver.com/)
8. [Maxymo - Gig Driver App](https://maxymoapp.com/)
9. [Driver's Utility Helper (DUH) - acceptordecline.com](https://acceptordecline.com/)
10. [Solo Gig App Review 2026 - The Rideshare Guy](https://therideshareguy.com/solo-app-review/)
11. [Stride - One free app (tax/mileage)](https://www.stridehealth.com/tax)
12. [EntreCourier - ryzyko deaktywacji za apki third-party](https://entrecourier.com/delivery/gig-delivery-platforms/doordash/doordash-strategies/deactivation-dash-utility-5-63-6-dash-utility-doordash/)
13. [Najlepsze aplikacje dla kuriera w Polsce](https://prominpartner.com/blog/best-apps-for-couriers-in-poland/)
14. [Ile zarabia kurier - Glovo/Wolt/Bolt Food (stawki PL)](https://www.mycycle.pl/blog/ile-zarabia-kurier-w-dostawie)
15. [Buckle - ubezpieczenia dla kierowcow gig](https://www.prnewswire.com/news-releases/buckle-selects-ccc-to-digitize-the-auto-claims-experience-for-gig-economy-drivers-301327548.html)
16. [Mystro vs GigU - The Rideshare Guy (GigU czyta ekran, kolory, Android, USA)](https://therideshareguy.com/mystro-vs-gigu/)
17. [Para App Review - The Rideshare Guy (przewidywanie napiwków, USA)](https://therideshareguy.com/drivers-experience-using-para/)
18. [CABIO - jak rozliczać kurierów Glovo/Wolt/Stuart/Uber Eats/Bolt (kalkulator flotowy back-office)](https://cabio.pl/pl/jak-rozliczac-glovo-wolt-stuart/)
19. [Łączenie dostaw - ranking aplikacji (multi-apping PL)](https://dostawca-jedzenia.pl/laczenie-dostaw/)
20. [Glovo kurier - realne stawki i zarobki netto 2025 (jak liczona kwota zlecenia)](https://glovokurier.pl/ile-zarabia-kurier-glovo-2025/)
21. [Skільки заробляє кур'єр Glovo в Україні 2025 (UA, brak narzędzi-nakładek)](https://wem.ua/skilky-zaroblyaye-kuryer-glovo-v-ukrayini-u-2025-roczi/)
22. [Cuanto gana un repartidor de Glovo en España (ES, brak nakładki opłacalności)](https://www.adslzone.net/noticias/internet/cuanto-gana-repartidor-glovo-como-trabajar/)
23. [DoorDash Driver: Best Tips for Beginners ($35/hr) - Best Dasher Alive (1,619,261 wyświetleń)](https://www.youtube.com/watch?v=Q7COkq2iZcY)
24. [Все про роботу кур'єром BOLT FOOD - ЩОЯК (83,433 wyświetleń, UA)](https://www.youtube.com/watch?v=rX4RAInJK2Y)
25. [8 ПОРАД ЯКІ ТОБІ ДОПОМОЖУТЬ ЗАРОБЛЯТИ ВДВІЧІ БІЛЬШЕ В ДОСТАВЦІ! - Pokatun (37,101 wyświetleń, UA)](https://www.youtube.com/watch?v=q_9n5ed6lc0)
26. [Я працював місяць курʼєром в Glovo у Польщі - RazeDen (18,381 wyświetleń, UA/Polska)](https://www.youtube.com/watch?v=_nzI9wY7hx0)
27. [Сколько можно заработать за час в glovo на велосипеде? Работа в Польше - Serhii Marchenko (17,954 wyświetleń, RU/Polska)](https://www.youtube.com/watch?v=0oG5aTpSO8g)
28. [8 Top Courier Apps - Make $10k+ per month as a Delivery Driver - Steve Orenstein (19,341 wyświetleń, EN)](https://www.youtube.com/watch?v=4Lo0uiyCYgI)
29. [5 High-Paying Delivery Apps You Must Try in 2025 - Sam J Drives (13,165 wyświetleń, EN)](https://www.youtube.com/watch?v=0w-0OF4cfI4)
30. [Ile tak naprawdę zarabiają kierowcy korzystający z aplikacji Wolt i Glovo? - Alpha (10,224 wyświetleń, PL)](https://www.youtube.com/watch?v=HKJMNLLnv68)
31. [Gdzie zarobisz więcej jako kurier - Glovo czy Bolt Food? - Eternis (kanał PL)](https://www.youtube.com/watch?v=_0C6XyGl6Dw)
32. [Will Para Tip Transparency Make More Money or Get You Deactivated? - EntreCourier](https://entrecourier.com/delivery/delivery-strategies/delivery-tools/will-para-tip-transparency-make-more-money-or-get-you-deactivated/)
33. [Para App - tip predictor / ujawnianie pełnej wypłaty przed akceptacją (USA)](https://www.withpara.com/drivers)
34. [Gridwise - Gig Driver Assistant (tracker zarobków/przebiegu; plan Plus 9,99$/mc)](https://gridwise.io/)
35. [How to Get the BIGGEST Orders on DoorDash and Uber Eats (2024) - YouTube](https://www.youtube.com/watch?v=0Ev8wmGu7GE)
36. [Top YouTube Influencers for DoorDash and Uber Eats Drivers (Rideshare Guy, Your Driver Mike, Pedro DoorDash Santiago)](https://hiddenhotspots.com/top-youtube-influencers-for-doordash-and-uber-eats-drivers-a-guide-to-maximizing-your-earnings-2023/)
37. [Praca kuriera na rowerze: ile można zarobić - Strefa Biznesu (elastyczny czas pracy)](https://strefabiznesu.pl/praca-kuriera-na-rowerze-ile-mozna-zarobic-stawki-nie-powalaja-ale-dla-wielu-najbardziej-liczy-sie-elastyczny-czas-pracy/ar/c3-16794267)
38. [Dexterowski - kanal YouTube (kurier Glovo/Wolt/Uber, 200 tys.+)](https://www.youtube.com/@Dexterowskii)
39. [Dostawca-jedzenia.pl - Warto rozmawiac - dostawcy jedzenia w sieci](https://dostawca-jedzenia.pl/dostawcy-jedzenia-w-sieci/)
40. [Telegram @glovo_uber_wolt - Glovo Uber Wolt PL Polska (3,9 tys., PL/UA/RU)](https://t.me/s/glovo_uber_wolt)
41. [Telegram @glovopl - Glovo PL (~1,19 tys., partner Flow Apps)](https://t.me/s/glovopl)
42. [Telegram @glovoinform - GLOVO INFOrmation (14 tys., UKRAINA nie PL)](https://t.me/s/glovoinform)
43. [FB grupa Glovo Polska - dostawcy, kurierzy](https://www.facebook.com/groups/polskaglovo/)
44. [FB grupa Wolt Polska - dostawcy, kurierzy](https://www.facebook.com/groups/woltpolska/)
45. [FB grupa Wolna Grupa Niebieskich Kurierow (oddolna, Wolt)](https://www.facebook.com/groups/278530996963355/)
46. [FB grupa Kurierzy Dostawcy Eats Glovo Wolt Xpress Delivery (multi-app)](https://www.facebook.com/groups/881810492167387/)
47. [FB grupa Kurierzy-Dostawcy grupa ogolna dpd glovo uber](https://www.facebook.com/groups/1026736401505373/)
48. [FB grupa kierowcy i kurierzy Uber Bolt Freenow Glovo Wolt Stuart](https://www.facebook.com/groups/1067076023697799/)
49. [FB grupa Dostawcy Glovo Wolt Xpress Delivery Praca](https://www.facebook.com/groups/2285887084814435/)
50. [FB grupa Kurierzy Glovo Trojmiasto (PL/RU, Gdansk)](https://www.facebook.com/groups/223133805746763/)
51. [Kurierpedia - Kompendium Wiedzy Delivery (powiazane z Dexterawka)](https://kurierpedia.pl/docs/10-porady-od-innych/)
52. [Flow Apps - partner flotowy, obsluga RU](https://flowapps.pl/)
53. [Google Play Store ASO: Proven Strategies for Android Apps (Asolytics)](https://asolytics.pro/blog/post/google-play-app-optimization/)
54. [App Store Optimization in 2026: ASO Strategy, Trends, and Best Practices (ASO Mobile)](https://asomobile.net/en/blog/aso-in-2026-the-complete-guide-to-app-optimization/)
55. [Курьеры - katalog czatów Telegram (tg-cat)](https://www.tg-cat.com/?search=%D0%BA%D1%83%D1%80%D1%8C%D0%B5%D1%80%D1%8B&type=supergroup)
56. [yellowbox - YouTube (warszawski kanał rowerowy)](https://www.youtube.com/channel/UCdO3rGYAaOggrrWz9PIbggg)
57. [2025 TikTok Organic Growth Report (Social Growth Engineers)](https://www.socialgrowthengineers.com/2025-tiktok-organic-growth-report-lessons-trends-and-the-road-to-2026)
58. [How I got my first 100 users via Reddit (Indie Hackers)](https://www.indiehackers.com/post/how-i-got-my-first-100-users-via-reddit-8639b39777)
59. [r/couriersofreddit - Subreddit Stats & Analysis (GummySearch)](https://gummysearch.com/r/couriersofreddit/)
60. [Supercharge Your Mobile App Referral Program (Viral Loops)](https://viral-loops.com/blog/mobile-app-referral-program/)
