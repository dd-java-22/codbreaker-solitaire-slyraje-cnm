# Phase 2 – Separation of Concerns and Technical Specifications (Draft)

## 1. Overview

Phase 2 focuses on **where** responsibilities live in the Codebreaker Solitaire client, independent of UI technology (console, JavaFX, Android).[web:30][web:35] It covers layered design, HTTP/JSON library choices (with Android in mind), DTO usage, and a UI-agnostic `CodebreakerService` interface.[web:18][web:19][web:22][web:23][web:25][web:30][web:36]

---

## 2. Layered architecture (where responsibilities go)

We divide responsibilities into four main layers.[web:30][web:35]

### 2.1 Layers and responsibilities

- **Domain layer**
    - Core concepts and rules: `Game`, `Guess`, `ErrorInfo`, optional `GameStats`.
    - No knowledge of HTTP, JSON, DTOs, or UI frameworks.

- **Application / service layer**
    - Coordinates use cases: start game, submit guess, refresh game state, delete game.
    - Depends only on domain types and the `CodebreakerService` interface.

- **Infrastructure layer**
    - Handles external details: HTTP clients, JSON mapping, API DTOs.[page:8][web:18][web:19]
    - Implements `CodebreakerService` using specific HTTP + JSON libraries.
    - Contains DTO classes that match the Codebreaker API JSON.[page:8][web:30][web:32][web:36]

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
    - Standard in Java 11+ and good for pure desktop use.[web:21][web:43]
    - Not available in typical Android runtimes; not suitable as the shared HTTP foundation.[web:11][web:18]

- **OkHttp**
    - Modern HTTP client for JVM and Android; widely adopted on Android.[web:25][web:43][web:51]
    - Works on desktop and Android with the same API; ideal as the common low-level HTTP engine.[web:25][web:43][web:51]

- **Retrofit 2**
    - High-level REST client built on OkHttp; annotation-based interfaces, automatic JSON mapping.[web:46][web:23]
    - De facto standard for REST APIs in Android apps.[web:22][web:23][web:52]

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
    - Modern JSON library for Kotlin and Java; designed for Android and JVM.[web:56][web:57][web:60][web:63]
    - Integrates directly with OkHttp and has official Retrofit support via a Moshi converter.[web:56][web:59][web:63]

- **Strategy**
    - Use Moshi in `codebreaker.infrastructure` to serialize/deserialize DTOs:
        - Desktop: OkHttp + Moshi.
        - Android: Retrofit + Moshi + OkHttp.[web:25][web:56][web:59][web:63]
    - Domain and application layers see only domain objects, not JSON or Moshi types.

---

## 4. JSON mapping and DTOs

### 4.1 Domain models vs DTOs

- **Domain models** (`Game`, `Guess`, `ErrorInfo`)
    - Reflect game concepts (state, guesses, error info) independently of JSON layout.[web:31][web:35]
    - Used by `CodebreakerService` and all UI layers.

- **DTOs** (`GameDto`, `GuessDto`, `ErrorDto`)
    - Mirror the Codebreaker API JSON structure and field names.[page:8]
    - Annotated/configured for Moshi in the infrastructure layer.[web:56][web:60][web:63]

### 4.2 Mapping responsibilities

- Dedicated mapping functions in the infrastructure layer:
    - API → DTO → domain (for responses).
    - Domain → DTO → API (for requests, where needed).

- Only the infrastructure layer knows about JSON and Moshi; domain and UI remain insulated from API shape changes.[web:30][web:32][web:36][web:56][web:63]

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
    - No HTTP or JSON types; usable regardless of whether the implementation uses OkHttp, Retrofit, or `java.net.http.HttpClient`.[web:18][web:19][web:22][web:23][web:25]

- Domain-oriented:
    - Returns domain `Game` and `Guess` objects that align with the API’s `Game` and `Guess` state (including `solved` and guess history).[page:8]

---
