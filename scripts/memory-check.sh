#!/usr/bin/env bash
# memory-check.sh — kontrola higieny plikow pamieci projektu.
# Przenosna: warstwa git/filesystem, dziala niezaleznie od narzedzia AI (Claude Code, ChatGPT, czlowiek).
#
# Uzycie:
#   bash scripts/memory-check.sh            # raport, zawsze exit 0 (informacyjny)
#   bash scripts/memory-check.sh --strict   # exit 1 gdy ktorys prog przekroczony (do CI)
set -euo pipefail

ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
cd "$ROOT"

# Plik:prog (liczba linii). Progi standardu pamieci.
CHECKS="CLAUDE.md:120 todo.md:80 PROGRESS.md:150"

strict=0
[ "${1:-}" = "--strict" ] && strict=1
problem=0

echo "-- Higiena pamieci: $ROOT --"
for c in $CHECKS; do
  f="${c%%:*}"; limit="${c##*:}"
  if [ -f "$f" ]; then
    n=$(wc -l < "$f" | tr -d ' ')
    if [ "$n" -gt "$limit" ]; then
      echo "  [!] $f: $n linii (prog $limit) -> POSPRZATAJ przed dolozeniem tresci"
      problem=1
    else
      echo "  [ok] $f: $n/$limit"
    fi
  fi
done

# docs/ (wariant full): ostrzegaj o pojedynczych spuchnietych plikach
if [ -d docs ]; then
  while IFS= read -r f; do
    [ -f "$f" ] || continue
    n=$(wc -l < "$f" | tr -d ' ')
    if [ "$n" -gt 1500 ]; then
      echo "  [!] $f: $n linii (prog 1500) -> podziel tematycznie"
      problem=1
    fi
  done < <(find docs -maxdepth 2 -name '*.md' 2>/dev/null)
fi

if [ "$problem" -eq 0 ]; then
  echo "  Wszystko w normie."
fi
if [ "$strict" -eq 1 ] && [ "$problem" -eq 1 ]; then
  exit 1
fi
exit 0
