# Codebreaker Android Project Summary (Updated)

**Base Package:** `edu.cnm.deepdive.codebreaker.app`

**Project Type:** Android application using Hilt for dependency injection, Navigation Components, Room database (fully integrated), and ViewBinding.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                        │
├─────────────────────────────────────────────────────────────────┤
│  CodebreakerApplication (@HiltAndroidApp)                       │
│    - Entry point for Hilt DI                                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                         UI LAYER                                │
├─────────────────────────────────────────────────────────────────┤
│  MainActivity (@AndroidEntryPoint)                              │
│    - Hosts NavHostFragment                                      │
│    - Manages navigation setup                                   │
│                                                                 │
│  GameFragment (@AndroidEntryPoint) [implements MenuProvider]    │
│    ├─ observes → GameViewModel.game                             │
│    ├─ observes → GameViewModel.solved                           │
│    ├─ observes → GameViewModel.guess                            │
│    ├─ observes → GameViewModel.error                            │
│    ├─ uses → GuessesAdapter (injected)                          │
│    └─ uses → SymbolMap (injected)                               │
│                                                                 │
│  SettingsFragment (extends PreferenceFragmentCompat)            │
│    - Manages user preferences                                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                      VIEWMODEL LAYER                            │
├─────────────────────────────────────────────────────────────────┤
│  GameViewModel (@HiltViewModel extends ViewModel)               │
│    ├─ injects → CodebreakerService (from :client)               │
│    ├─ injects → Context (@ApplicationContext)                   │
│    ├─ exposes → LiveData<Game>                                  │
│    ├─ exposes → LiveData<Guess>                                 │
│    ├─ exposes → LiveData<Boolean> (solved)                      │
│    ├─ exposes → LiveData<Throwable> (error)                     │
│    └─ reads → SharedPreferences (code length, pool settings)    │
│                                                                 │
│    NOTE: Could be refactored to use GameService instead         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       ADAPTER LAYER                             │
├─────────────────────────────────────────────────────────────────┤
│  GuessesAdapter (RecyclerView.Adapter)                          │
│    ├─ injects → Context (@ActivityContext)                      │
│    ├─ injects → SymbolMap                                       │
│    └─ displays → List<Guess>                                    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     CUSTOM VIEW LAYER                           │
├─────────────────────────────────────────────────────────────────┤
│  SquareRadioButton (extends AppCompatRadioButton)               │
│    - Custom view for square-shaped radio buttons                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER (NEW)                       │
├─────────────────────────────────────────────────────────────────┤
│  GameService (interface)                                        │
│    - App-level abstraction for game operations                  │
│    - Mirrors CodebreakerService API                             │
│                                                                 │
│  GameServiceImpl (@Singleton)                                   │
│    ├─ injects → CodebreakerService (from :client)               │
│    ├─ injects → GameSummaryRepository                           │
│    ├─ delegates to → CodebreakerService for remote ops          │
│    └─ automatically updates → GameSummaryRepository             │
│                                                                 │
│  ServiceModule (@Module, @InstallIn(SingletonComponent))        │
│    ├─ provides → CodebreakerService (singleton)                 │
│    └─ binds → GameService to GameServiceImpl                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                     REPOSITORY LAYER (NEW)                      │
├─────────────────────────────────────────────────────────────────┤
│  GameSummaryRepository (interface)                              │
│    - Abstract repository for game summary persistence           │
│    - Methods:                                                   │
│      • summarize(Game) → CompletableFuture<Void>                │
│      • getByExternalKey(String) → CF<GameSummary>               │
│      • remove(GameSummary) → CompletableFuture<Integer>         │
│      • removeAll(Collection) → CompletableFuture<Integer>       │
│      • selectInProgress() → LiveData<List<GameSummary>>         │
│      • selectCompleted(int, int) → LiveData<List<GameSummary>>  │
│                                                                 │
│  GameSummaryRepositoryImpl (@Singleton)                         │
│    ├─ injects → GameSummaryDao                                  │
│    ├─ implements async operations with CompletableFuture        │
│    └─ exposes LiveData for reactive UI updates                  │
│                                                                 │
│  RepositoryModule (@Module, @InstallIn(SingletonComponent))     │
│    └─ binds → GameSummaryRepository to implementation           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     DATA/DATABASE LAYER (NEW)                   │
├─────────────────────────────────────────────────────────────────┤
│  CodebreakerDatabase (RoomDatabase) [FULLY INTEGRATED]          │
│    ├─ @Database(entities = {GameSummary}, version = 1)          │
│    ├─ provides → GameSummaryDao                                 │
│    └─ includes → Converters (Instant ↔ Long)                    │
│                                                                 │
│  GameSummaryDao (@Dao interface)                                │
│    - CRUD operations for GameSummary                            │
│    - Queries:                                                   │
│      • selectByExternalKey(String) → GameSummary                │
│      • selectInProgress() → LiveData<List<GameSummary>>         │
│      • selectCompleted(int, int) → LiveData<List<GameSummary>>  │
│                                                                 │
│  GameSummary (@Entity)                                          │
│    - Room entity for local game persistence                     │
│    - Fields: id, externalKey, pool, poolSize, codeLength,       │
│      started, guessCount, solved, lastPlayed, exactMatches,     │
│      nearMatches                                                │
│    - Indices on: externalKey (unique), solved/timestamps,       │
│      ranking columns                                            │
│                                                                 │
│  DatabaseModule (@Module, @InstallIn(SingletonComponent))       │
│    ├─ provides → CodebreakerDatabase (singleton)                │
│    └─ provides → GameSummaryDao                                 │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        UTILITY LAYER                            │
├─────────────────────────────────────────────────────────────────┤
│  SymbolMap (Kotlin class, injected)                             │
│    - Maps code points to symbol attributes (drawable, color)    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      EXTERNAL MODULES                           │
├─────────────────────────────────────────────────────────────────┤
│  :api module                                                    │
│    └─ Game, Guess, Error models (OpenAPI generated)             │
│                                                                 │
│  :client module                                                 │
│    └─ CodebreakerService, custom exceptions                     │
└─────────────────────────────────────────────────────────────────┘
```


## Key Relationships & Data Flow

1. **CodebreakerApplication** → Initializes Hilt DI for entire app

2. **MainActivity** → Hosts GameFragment via Navigation Component

3. **GameFragment** → Observes GameViewModel, uses GuessesAdapter & SymbolMap

4. **GameViewModel** → **Still uses CodebreakerService directly** (not yet refactored to use GameService)

5. **GameService (NEW)** → Wraps CodebreakerService + automatically persists to GameSummaryRepository
   - When `startGame()` is called: remote call → auto-persist summary
   - When `submitGuess()` is called: remote call → auto-persist summary
   - When `getGame()` is called: remote call → auto-persist summary
   - When `deleteGame()` is called: remote call → remove from local DB

6. **GameSummaryRepository** → Abstracts persistence operations, delegates to GameSummaryDao

7. **GameSummaryDao** → Direct Room database access

8. **CodebreakerDatabase** → Room database with Instant↔Long type converters

## Recent Changes (Since Last Summary)

### ✅ Fully Implemented
- **Room Database Integration**: CodebreakerDatabase class created with proper @Database annotation
- **DatabaseModule**: Hilt module providing database and DAO singletons
- **Repository Pattern**: GameSummaryRepository interface + implementation with CompletableFuture async ops
- **RepositoryModule**: Hilt module for repository bindings
- **Service Abstraction Layer**: GameService interface + GameServiceImpl that wraps remote service with automatic local persistence
- **Type Converters**: Instant↔Long converters for Room database

### 🔄 Integration Points
- **GameViewModel** currently injects `CodebreakerService` directly (from :client module)
- **GameService** is available but not yet used by ViewModels
- When refactored, GameViewModel should inject `GameService` instead for automatic persistence

## Architecture Pattern

The project now follows a **clean architecture** with clear separation:
- **UI Layer**: Fragments, Activities, Adapters
- **ViewModel Layer**: MVVM with LiveData
- **Service Layer**: GameService (app-level abstraction)
- **Repository Layer**: GameSummaryRepository (data abstraction)
- **Data Layer**: Room database with DAOs and entities
- **External Services**: CodebreakerService (remote API client)