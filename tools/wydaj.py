#!/usr/bin/env python3
"""Wydanie OrderPilota do Google Play przez API (bez klikania w konsoli).

Użycie:
    .venv-play/bin/python tools/wydaj.py --aab <plik.aab> [--wydaj] [--procent 100]

Domyślnie tworzy SZKIC wersji (status draft) — w Play Console zostaje jeden przycisk
"Rozpocznij wdrażanie". Z flagą --wydaj publikuje od razu.

Klucz konta technicznego: keystore/play-api.json (poza gitem).
"""
import argparse
import json
import pathlib
import re
import sys

import requests
from google.oauth2 import service_account
from google.auth.transport.requests import Request

PACKAGE = "com.orderpilot.app"
API = "https://androidpublisher.googleapis.com/androidpublisher/v3"
UPLOAD = "https://androidpublisher.googleapis.com/upload/androidpublisher/v3"
ROOT = pathlib.Path(__file__).resolve().parent.parent
KLUCZ = ROOT / "keystore" / "play-api.json"
# notki leżą w docs/play-store — historycznie raz w roocie repo, raz w module
KATALOGI_NOTEK = [ROOT / "docs" / "play-store", ROOT / "OrderPilot" / "docs" / "play-store"]


def token():
    creds = service_account.Credentials.from_service_account_file(
        str(KLUCZ), scopes=["https://www.googleapis.com/auth/androidpublisher"]
    )
    creds.refresh(Request())
    return creds.token


def czytaj_notki(wersja):
    """Wyciąga notki wydania z RELEASE-NOTES-<wersja>.md (bloki ``` pod nagłówkami ## <język>)."""
    for katalog in KATALOGI_NOTEK:
        plik = katalog / f"RELEASE-NOTES-{wersja}.md"
        if plik.exists():
            break
    else:
        return []
    tekst = plik.read_text(encoding="utf-8")
    notki = []
    for jezyk, tresc in re.findall(r"^## ([a-zA-Z\-]+)\s*\n```\n(.*?)\n```", tekst, re.M | re.S):
        notki.append({"language": jezyk, "text": tresc.strip()})
    return notki


def main():
    p = argparse.ArgumentParser()
    p.add_argument("--aab", help="plik .aab do wgrania")
    p.add_argument("--wersja-kodu", type=int, help="pomiń wgrywanie, użyj pakietu już obecnego w Play")
    p.add_argument("--wersja", default="1.1.0", help="do znalezienia pliku z notkami")
    p.add_argument("--wydaj", action="store_true", help="publikuj od razu zamiast szkicu")
    p.add_argument("--procent", type=float, default=100.0)
    a = p.parse_args()

    if not a.aab and not a.wersja_kodu:
        sys.exit("Podaj --aab albo --wersja-kodu")

    h = {"Authorization": f"Bearer {token()}"}

    r = requests.post(f"{API}/applications/{PACKAGE}/edits", headers=h, timeout=60)
    r.raise_for_status()
    edit = r.json()["id"]
    print(f"edycja: {edit}")

    if a.wersja_kodu:
        vc = a.wersja_kodu
        print(f"bez wgrywania, używam pakietu versionCode: {vc}")
    else:
        aab = pathlib.Path(a.aab).expanduser()
        if not aab.exists():
            sys.exit(f"Nie ma pliku: {aab}")
        with aab.open("rb") as f:
            r = requests.post(
                f"{UPLOAD}/applications/{PACKAGE}/edits/{edit}/bundles?uploadType=media",
                headers={**h, "Content-Type": "application/octet-stream"},
                data=f,
                timeout=1800,
            )
        if not r.ok:
            sys.exit(f"Wgrywanie nie przeszło: {r.status_code} {r.text[:800]}")
        vc = r.json()["versionCode"]
        print(f"wgrany pakiet, versionCode: {vc}")

    release = {
        "name": f"{vc} ({a.wersja})",
        "versionCodes": [str(vc)],
        "releaseNotes": czytaj_notki(a.wersja),
    }
    if a.wydaj:
        if a.procent >= 100:
            release["status"] = "completed"
        else:
            release["status"] = "inProgress"
            release["userFraction"] = a.procent / 100.0
    else:
        release["status"] = "draft"

    r = requests.put(
        f"{API}/applications/{PACKAGE}/edits/{edit}/tracks/production",
        headers={**h, "Content-Type": "application/json"},
        data=json.dumps({"track": "production", "releases": [release]}),
        timeout=120,
    )
    if not r.ok:
        sys.exit(f"Ustawienie ścieżki nie przeszło: {r.status_code} {r.text[:800]}")
    print(f"ścieżka produkcyjna ustawiona, status: {release['status']}, notki: {len(release['releaseNotes'])} języków")

    r = requests.post(f"{API}/applications/{PACKAGE}/edits/{edit}:commit", headers=h, timeout=300)
    if not r.ok:
        sys.exit(f"Zatwierdzenie nie przeszło: {r.status_code} {r.text[:800]}")
    print("ZATWIERDZONE w Google Play")


if __name__ == "__main__":
    main()
