# OrderPilot

[![Google Play](https://img.shields.io/badge/Google%20Play-LIVE%20v1.0.5-brightgreen?logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.orderpilot.app)
[![Release](https://img.shields.io/github/v/release/kris20032/OrderPilot-App)](https://github.com/kris20032/OrderPilot-App/releases/latest)

> **🚀 v1.0.5 LIVE w Google Play od 2026-05-23** — apka dostępna globalnie w 177 krajach. Zobacz [v1.0.5 release notes](https://github.com/kris20032/OrderPilot-App/releases/tag/v1.0.5).

Aplikacja Android dla kurierów (Glovo, UberEats, Wolt, Bolt Food) — ocenia opłacalność zlecenia w czasie rzeczywistym przez nakładkę (overlay): zielony/żółty/czerwony + PLN/h.

Działa przez AccessibilityService — zero ryzyka bana, bez API.

## Zanim zaczniesz

1. `git pull`
2. Przeczytaj [`RULES.md`](RULES.md) — zasady współpracy i Git workflow
3. Sprawdź [`PROGRESS.md`](PROGRESS.md) — co jest w trakcie
4. Przeczytaj [`docs/PLAN.md`](docs/PLAN.md) — plan etapów
5. Przeczytaj [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — architektura

## Zespół

- **Krzysztof** (+ Claude Code) — główna implementacja
- **Tata** — testowanie na realnych zleceniach
- **Łukasz** — UI, settings, wsparcie

## Struktura repo

```
OrderPilot-App/
├── README.md
├── RULES.md             ← zasady współpracy (PRZECZYTAJ)
├── PROGRESS.md          ← aktualny status
├── docs/
│   ├── PLAN.md          ← aktywny plan + otwarte zadania
│   ├── ARCHITECTURE.md  ← architektura modułowa
│   └── PRODUCT_SPEC.md  ← specyfikacja produktu
├── OrderPilot/       ← projekt Android (app/)
└── FakeUberDriver/     ← aplikacja testowa (symuluje popupy Uber)
```
