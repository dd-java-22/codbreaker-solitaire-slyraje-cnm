# Phase 2 – Separation of Concerns and Technical Specifications (Draft)

## 1. Overview

Phase 2 focuses on **where** responsibilities live in the Codebreaker Solitaire client, independent of UI technology (console, JavaFX, Android). It covers layered design, HTTP/JSON library choices (with Android in mind), DTO usage, and a UI-agnostic `CodebreakerService` interface.

---

## 2. Layered architecture (where responsibilities go)

We divide responsibilities into four main layers.

### 2.1 Layers and responsibilities

- **Domain layer**
  - Core concepts and rules: `Game`, `Guess`, `ErrorInfo`, optional `GameStats`.
  - No knowledge of HTTP, JSON, DTOs, or UI frameworks.

- **Application / service layer**
  - Coordinates use cases: start game, submit guess, refresh game state, delete game.
  - Depends only on domain types and the `CodebreakerService` interface.

- **Infrastructure layer**
  - Handles external details: HTTP clients, JSON mapping, API DTOs.
  - Implements `CodebreakerService` using specific HTTP + JSON libraries.
  - Contains DTO classes that match the Codebreaker API JSON.

- **UI layers** (console, JavaFX, Android)
  - Present domain data and collect user input.
  - Depend only on `CodebreakerService` and domain types; never perform HTTP calls or parse JSON.

### 2.2 Conceptual packages

- `codebreaker.domain`
  - `Game`, `Guess`, `ErrorInfo`, `GameStats`

- `codebreaker.application`
  - `CodebreakerService` (UI- and HTTP-agnostic interface)

- `codebreaker.infrastructure.dto`
  - `GameDto`, `GuessDto`, `ErrorDto`

- `codebreaker.infrastructure.http`
  - HTTP client adapter(s) using OkHttp / Retrofit / `java.net.http.HttpClient`
  - Mapper between DTOs and domain

- `codebreaker.ui.console`, `codebreaker.ui.javafx`, `codebreaker.ui.android`
  - Controllers / view models and views per platform

---

## 3. Library strategy (HTTP and JSON)

### 3.1 HTTP client choices

- **`java.net.http.HttpClient`**
  - Standard in Java 11+ and good for pure desktop use.
  - Not available in typical Android runtimes; not suitable as the shared HTTP foundation.

- **OkHttp**
  - Modern HTTP client for JVM and Android; widely adopted on Android.
  - Works on desktop and Android with the same API; ideal as the common low-level HTTP engine.

- **Retrofit 2**
  - High-level REST client built on OkHttp; annotation-based interfaces, automatic JSON mapping.
  - De facto standard for REST APIs in Android apps.

### 3.2 HTTP strategy for this project

- **Desktop (console / JavaFX)**
  - Prefer an `OkHttp`-based implementation of `CodebreakerService`.
  - Optionally, a `java.net.http.HttpClient` implementation may exist but remains desktop-only.

- **Android**
  - Use `Retrofit 2 + OkHttp` in the infrastructure layer as the `CodebreakerService` implementation.

- **Common rule**
  - All HTTP choices are hidden behind `CodebreakerService`; domain and UI never depend on OkHttp, Retrofit, or `java.net.http.HttpClient`.

### 3.3 JSON mapping with Moshi

- **Moshi**
  - Modern JSON library for Kotlin and Java; designed for Android and JVM.
  - Integrates directly with OkHttp and has official Retrofit support via a Moshi converter.

- **Strategy**
  - Use Moshi in `codebreaker.infrastructure` to serialize/deserialize DTOs:
    - Desktop: OkHttp + Moshi.
    - Android: Retrofit + Moshi + OkHttp.
  - Domain and application layers see only domain objects, not JSON or Moshi types.

---

## 4. JSON mapping and DTOs

### 4.1 Domain models vs DTOs

- **Domain models** (`Game`, `Guess`, `ErrorInfo`)
  - Reflect game concepts (state, guesses, error info) independently of JSON layout.
  - Used by `CodebreakerService` and all UI layers.

- **DTOs** (`GameDto`, `GuessDto`, `ErrorDto`)
  - Mirror the Codebreaker API JSON structure and field names.
  - Annotated/configured for Moshi in the infrastructure layer.

### 4.2 Mapping responsibilities

- Dedicated mapping functions in the infrastructure layer:
  - API → DTO → domain (for responses).
  - Domain → DTO → API (for requests, where needed).

- Only the infrastructure layer knows about JSON and Moshi; domain and UI remain insulated from API shape changes.

---

## 5. CodebreakerService interface (UI-agnostic service)

### 5.1 Essential methods

- Game lifecycle
  - `Game startNewGame(String pool, int length)`
  - `Game getGame(String gameId)`
  - `void deleteGame(String gameId)`

- Guessing
  - `Guess submitGuess(String gameId, String guessText)`
  - `List<Guess> getGuesses(String gameId)`

- Convenience / status
  - `boolean isSolved(String gameId)`

### 5.2 Design properties

- UI-agnostic:
  - No references to console, JavaFX, Android, or UI classes.

- Transport-agnostic:
  - No HTTP or JSON types; usable regardless of whether the implementation uses OkHttp, Retrofit, or `java.net.http.HttpClient`.

- Domain-oriented:
  - Returns domain `Game` and `Guess` objects that align with the API’s `Game` and `Guess` state (including `solved` and guess history).

---
