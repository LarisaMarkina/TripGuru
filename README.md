# TripGuru

Aplikacja mobilna na Androida służąca do planowania i organizacji podróży.  
Wersja MVP zawiera funkcjonalność zarządzania podróżami: dodawanie, edycja, usuwanie oraz przeglądanie listy i szczegółów podróży.

TripGuru to aplikacja mobilna, której celem jest ułatwienie planowania i organizacji podróży — od przygotowań, przez każdy dzień wyjazdu, aż po porządki po powrocie. 
Aplikacja ma pomóc użytkownikom uporządkować wszystkie informacje związane z podróżą w jednym miejscu: bilety, dokumenty, plan dnia, notatki, zdjęcia, wspomnienia. 
Projekt powstał również jako nauka języka Kotlin oraz nowoczesnych wzorców architektonicznych w Androidzie (Clean Architecture + MVVM) poprzez stworzenie praktycznej i użytecznej aplikacji mobilnej.

---

## Aktualny zakres funkcjonalności (Milestone 1 – Przed podróżą)

- Dodawanie nowej podróży z następującymi informacjami:
  - Nazwa podróży
  - Cel podróży
  - Data rozpoczęcia i zakończenia
  - Miejsce (lokalizacja)
  - Załączniki: linki i dokumenty
- Lista wszystkich zapisanych podróży
- Podgląd szczegółów podróży
- Edycja i usuwanie podróży
- Przechowywanie danych lokalnie za pomocą Room (baza danych)
- UI zbudowane w Jetpack Compose, responsywne i działające w orientacji pionowej
- Architektura aplikacji oparta na Clean Architecture + MVVM
- Dependency Injection za pomocą Hilt
- Asynchroniczność i obsługa danych za pomocą Kotlin Coroutines i Flow

---

## Technologie

- Kotlin  
- Jetpack Compose  
- Room (lokalna baza danych)  
- Hilt (Dependency Injection)  
- Kotlin Coroutines + Flow  
- Navigation Compose  

---

## Wymagania sprzętowe

- Android 6.0 (API 23) lub nowszy  
- Minimum 100 MB wolnej pamięci na urządzeniu  
- Połączenie internetowe nie jest wymagane do działania aplikacji

---

## Jak uruchomić projekt

Zainstaluj plik APK z katalogu app/build/outputs/apk

---

## Struktura projektu
- data/ – warstwa danych (Room, DAO, repozytoria)
- domain/ – logika biznesowa (use case, interfejsy)
- presentation/ – UI i ViewModel (Jetpack Compose)
- di/ – konfiguracja dependency injection
- MainActivity.kt – punkt startowy aplikacji

---

## Plany rozwoju
W kolejnych etapach planowane jest rozszerzenie aplikacji o:

- Rozdział „W trakcie podróży”: agenda dnia, notatki, zdjęcia, przypomnienia
- Rozdział „Po podróży”: ankieta, podsumowanie, statystyki, zarządzanie i optymalizacja zdjęć, eksport danych
- Współdzielenie podróży - możliwość dodawania członków podróży do wspólnego zarządzania podróżą, dzieleniem się zdjęciami i informacjami

---

## Licencja
Projekt udostępniony na licencji MIT.

---

## Kontakt
Jeśli masz pytania lub sugestie, skontaktuj się:
- E-mail: larisa.markina.work@gmail.com
- GitHub: LarisaMarkina
