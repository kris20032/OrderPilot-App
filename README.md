# OrderPilot

[![Google Play](https://img.shields.io/badge/Google%20Play-LIVE%20v1.0.5-brightgreen?logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.orderpilot.app)
[![Release](https://img.shields.io/github/v/release/kris20032/OrderPilot-App)](https://github.com/kris20032/OrderPilot-App/releases/latest)

> **🚀 v1.0.5 LIVE w Google Play od 2026-05-23** — apka dostępna globalnie w 177 krajach. Zobacz [v1.0.5 release notes](https://github.com/kris20032/OrderPilot-App/releases/tag/v1.0.5).

Aplikacja Android dla kurierów (Glovo, UberEats, Wolt, Bolt Food) — ocenia opłacalność zlecenia w czasie rzeczywistym przez nakładkę (overlay): zielony/żółty/czerwony + PLN/h.

Działa przez AccessibilityService — zero ryzyka bana, bez API.

## Zanim zaczniesz

1. `git pull`
2. Przeczytaj [`CLAUDE.md`](CLAUDE.md) — boot + zasady + sekwencja startowa (**punkt wejścia**)
3. Sprawdź [`todo.md`](todo.md) — stan „teraz" + otwarte zadania
4. Reszta wg potrzeby: [`RULES.md`](RULES.md) (szczegółowe zasady, Git workflow), [`PROGRESS.md`](PROGRESS.md) (log sesji), [`DECISIONS.md`](DECISIONS.md) (trwałe decyzje), [`docs/PLAN.md`](docs/PLAN.md) / [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)

## Zespół

- **Krzysztof** (+ Claude Code) — główna implementacja
- **Tata** — testowanie na realnych zleceniach
- **Łukasz** — UI, settings, wsparcie

## Struktura repo

```
OrderPilot-App/
├── README.md
├── CLAUDE.md            ← boot + zasady + sekwencja startowa (punkt wejścia)
├── todo.md              ← stan „teraz" + otwarte zadania
├── PROGRESS.md          ← log sesji (newest-first) + PROGRESS_ARCHIVE.md
├── DECISIONS.md         ← trwałe decyzje (ADR-lite)
├── RULES.md             ← szczegółowe zasady współpracy + Git workflow
├── docs/
│   ├── PLAN.md          ← plan etapów (14 epiców)
│   ├── ARCHITECTURE.md  ← architektura modułowa
│   ├── PRODUCT_SPEC.md  ← specyfikacja produktu
│   └── future_polish_fixes.md ← backlog drobnych bugów/pomysłów (#N)
├── OrderPilot/          ← projekt Android (app/)
└── FakeUberDriver/      ← aplikacja testowa (symuluje popupy Uber)
```
