# Phase 4 – Future-proofing for GUI and Mobile (Draft)

## 1. Overview

Phase 4 focuses on **tomorrow**: how this design survives a platform change from console to JavaFX and Android. It covers how we structure service methods for non-blocking UI environments, how our separation of concerns minimizes changes when replacing `TerminalUI` with other views, and which Android-specific networking and security requirements we should consider now so `RestCodebreakerService` can move to mobile with minimal redesign.

***

## 2. Threading and async (service method structure)

In a console app, it is acceptable to block the calling thread while waiting for HTTP responses. In JavaFX and Android, blocking the UI thread is not acceptable; network calls must be offloaded and results delivered asynchronously to the UI.

### 2.1 Asynchronous service interface

To support both blocking console flows and non-blocking GUI/mobile UIs, the **application-level service** should expose asynchronous variants of its methods. A simple, standard-library-friendly option is `CompletableFuture`.

```text
public interface AsyncCodebreakerService {

    // Game lifecycle
    CompletableFuture<Game> startNewGameAsync(String pool, int length);
    CompletableFuture<Game> getGameAsync(String gameId);
    CompletableFuture<Void> deleteGameAsync(String gameId);

    // Guessing
    CompletableFuture<Guess> submitGuessAsync(String gameId, String guessText);
    CompletableFuture<List<Guess>> getGuessesAsync(String gameId);

    // Status
    CompletableFuture<Boolean> isSolvedAsync(String gameId);
}
```

Key points:

- **Console**:
    - `TerminalUI` can continue to block when appropriate by calling `.get()` or `.join()` on the `CompletableFuture` results in a background or main loop, where blocking is acceptable.
- **JavaFX and Android**:
    - Controllers or view models call asynchronous methods, attach callbacks (`thenAccept`, `whenComplete`), and marshal results back to the UI thread using JavaFX application thread utilities or Android’s main thread mechanisms.
- **Extensibility**:
    - If a project later adopts RxJava3 or another reactive library, `AsyncCodebreakerService` can be implemented using those types internally, while still presenting a `CompletableFuture`-based interface externally, or vice versa.

### 2.2 Implementation considerations

- `RestCodebreakerService` can implement both synchronous (`CodebreakerService`) and asynchronous (`AsyncCodebreakerService`) variants, where sync methods delegate to async versions and block only in contexts where that is safe (e.g., non-UI threads).
- All HTTP and JSON work remains in the infrastructure layer; threading concerns at the service level are about **return types and callback structure**, not about exposing any particular HTTP client API to the caller.

***

## 3. Decoupling and platform changes (replacing TerminalUI)

### 3.1 What stays the same when moving to JavaFX

If `TerminalUI` is replaced with a JavaFX view (e.g., `JavaFxGameView` plus a controller or view model), the following parts of the current design should require **zero changes**:

- **Domain layer**
    - `Game`, `Guess`, `Score` (or `GameStats`) and any domain logic remain unchanged; they model the game, not the UI.
- **Application / service layer**
    - `CodebreakerService` and (if used) `AsyncCodebreakerService` interfaces, plus `GameController`, keep the same signatures and behavior.
- **Infrastructure layer**
    - `RestCodebreakerService`, DTOs, HTTP/JSON wiring, and mapping remain unchanged; they do not depend on the UI type.
- **Dependency injection / wiring contracts**
    - The fact that `TerminalUI` depends on `GameController`, and `GameController` depends on `CodebreakerService`, remains valid. A JavaFX view or controller can take the place of `TerminalUI` using the same constructor or setter contracts.

### 3.2 What actually changes

Only the **UI layer** changes:

- `TerminalUI` is removed or sidelined.
- New JavaFX components are introduced (e.g., `JavaFxGameController`, FXML view, or a view model).
- These new components depend on the same `GameController` and service interfaces through composition, using the same methods (`startNewGame`, `submitGuess`, `getCurrentGame`, etc.).

**Why zero changes elsewhere?**

- The UI is isolated behind the controller/service interfaces, and those interfaces are defined solely in terms of domain types and simple parameters.
- No HTTP, JSON, or thread-management details leak into domain or UI-facing interfaces; only the infrastructure and application layers deal with those concerns.
- The domain and application layers are free of any console- or JavaFX-specific types, so replacing `TerminalUI` does not ripple through the rest of the design.

***

## 4. Android constraints (preparing RestCodebreakerService for mobile)

`RestCodebreakerService` should be designed with Android networking and security requirements in mind, even before an Android module exists.

### 4.1 Network and threading constraints

- **No network on the main thread**
    - Android strictly prohibits network operations on the main (UI) thread; doing so throws a runtime exception.
    - `RestCodebreakerService` must be usable from background threads or expose asynchronous methods (`CompletableFuture`, or later RxJava/Flows) so Android callers do not block the UI thread.
- **Lifecycle-aware usage**
    - Android components (Activities, Fragments, ViewModels) can be destroyed or recreated; service calls should be cancelable or at least safe if results arrive after a UI component is gone.
    - Keeping `RestCodebreakerService` stateless (no hard ties to Activity/Context) makes it easier to use in lifecycle-aware patterns.

### 4.2 Security and transport concerns

- **HTTPS by default**
    - Android requires cleartext (HTTP) traffic to be explicitly allowed; planning for HTTPS endpoints from the start avoids future configuration headaches.
- **Network Security Config**
    - If debugging or using self-signed certificates, a network security config may be required; designing `RestCodebreakerService` to accept configurable base URLs and TLS settings makes this easier later.
- **No hard dependency on Android types**
    - `RestCodebreakerService` should avoid direct dependencies on Android classes (`Context`, `Activity`, etc.).
    - Keeping it pure Java (with HTTP/JSON libraries) allows reuse in desktop and Android modules, with Android-specific configuration layered on top.

### 4.3 Authentication and permissions (future-ready)

Even if the initial Codebreaker API requires no authentication:

- Plan for adding headers or tokens:
    - `RestCodebreakerService` should be able to accept or inject authentication headers (e.g., via constructor parameters, interceptors, or configuration objects).
- Permissions:
    - Android requires the `INTERNET` permission; this is an app-level concern, but designing `RestCodebreakerService` to fail clearly when connectivity is absent (and to surface meaningful errors to the application layer) simplifies integration.

***

## 5. Summary

- Asynchronous service interfaces (e.g., using `CompletableFuture`) allow the same `CodebreakerService` semantics to be used safely in console, JavaFX, and Android environments where UI threads must not block.
- The domain, application, and infrastructure layers are UI-agnostic, so replacing `TerminalUI` with a JavaFX view should require no changes outside the UI package.
- Designing `RestCodebreakerService` as a pure Java, HTTP/JSON-focused component, with async APIs and no Android-specific dependencies, positions it to be reused directly in an Android app that meets mobile networking and security constraints.