# CourierAssist

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
CourierAssist-App/
├── README.md
├── RULES.md             ← zasady współpracy (PRZECZYTAJ)
├── PROGRESS.md          ← aktualny status
├── docs/
│   ├── PLAN.md          ← aktywny plan + otwarte zadania
│   ├── ARCHITECTURE.md  ← architektura modułowa
│   └── PRODUCT_SPEC.md  ← specyfikacja produktu
├── CourierAssist/       ← projekt Android (app/)
└── FakeUberDriver/     ← aplikacja testowa (symuluje popupy Uber)
```
