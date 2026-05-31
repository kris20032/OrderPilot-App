#!/bin/bash
# Instalacja git hooków z `scripts/git-hooks/` do `.git/hooks/`.
#
# Użycie: uruchom raz po sklonowaniu repo (lub gdy dojdzie nowy hook):
#   ./scripts/install-hooks.sh
#
# Hooki (`.git/hooks/`) nie są wersjonowane przez gita, dlatego trzymamy ich
# źródła w `scripts/git-hooks/` i kopiujemy tym skryptem. Każdy nowy klon repo
# wymaga ponownego uruchomienia tego skryptu.
#
# Hooki instalowane:
#   post-checkout — auto-aktualizacja „AKTYWNY BRANCH" w CLAUDE.md przy checkoutach
#   pre-commit    — raport higieny pamięci (memory-check.sh) przy każdym commicie (ostrzega, nie blokuje)

set -e

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)"
if [ -z "$REPO_ROOT" ]; then
  echo "BŁĄD: uruchom z wnętrza repozytorium git" >&2
  exit 1
fi

SRC_DIR="$REPO_ROOT/scripts/git-hooks"
DST_DIR="$REPO_ROOT/.git/hooks"

if [ ! -d "$SRC_DIR" ]; then
  echo "BŁĄD: brak katalogu $SRC_DIR" >&2
  exit 1
fi

mkdir -p "$DST_DIR"

installed=0
for hook in "$SRC_DIR"/*; do
  [ -f "$hook" ] || continue
  name="$(basename "$hook")"
  cp "$hook" "$DST_DIR/$name"
  chmod +x "$DST_DIR/$name"
  echo "✓ zainstalowany hook: $name"
  installed=$((installed + 1))
done

if [ "$installed" -eq 0 ]; then
  echo "Ostrzeżenie: brak hooków do instalacji w $SRC_DIR"
else
  echo ""
  echo "Zainstalowano $installed hook(ów) w $DST_DIR"
fi
