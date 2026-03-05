Review the attached Markdown specification of the Android Codebreaker main screen and your project guidelines, then implement the described UI as an Android fragment.

**Goals**

- Implement a `GameFragment` class in the `edu.cnm.deepdive.codebreaker.app.controller` package (Java).
- Implement a corresponding layout resource `fragment_game.xml` with a `ConstraintLayout` root.
- The fragment should realize the layout and behavior described in the Markdown spec I provide.

**Requirements**

1. **Fragment class**
    - Language: Java.
    - Package: `edu.cnm.deepdive.codebreaker.app.controller`.
    - Class name: `GameFragment`, extending `Fragment`.
    - Use view binding with `FragmentGameBinding`.
    - Provide:
        - A public no‑arg constructor (required by Fragment).
        - A static `newInstance()` factory method.
        - Overrides of `onCreateView` and `onViewCreated`.
    - In `onCreateView`, inflate the view using view binding.
    - In `onViewCreated`, just set up any placeholder wiring for now (e.g., TODO comments for connecting to `GameViewModel`, recycler adapter, click listeners). Do not implement full game logic yet.
2. **Layout: `fragment_game.xml`**

Root: `ConstraintLayout`, matching the written spec:
    - **App bar and header region**
        - Assume the app bar / toolbar is provided by the hosting activity.
        - Directly under the top, create:
            - A `Button` in the upper‑right for “New Game”.
            - A message/status `TextView` to the left of the “New Game” button, horizontally constrained so it stretches toward the start side of the screen. This will show short status messages; use placeholder text.
    - **Guess history list**
        - Below the message/status row, add a vertically scrolling list for past guesses, using a `RecyclerView`.
        - Constrain it:
            - Top: below the message/status area.
            - Bottom: above the current guess area.
            - Start/end: to the parent.
        - For now, define a simple item layout (you may create a separate XML) with:
            - A left‑aligned attempt number (e.g., `TextView`).
            - A center region for the guess representation (can be placeholder text or simple colored views).
            - A right‑aligned `TextView` showing feedback in the form `E/N`, with the “E” number emphasized (e.g., bold).
    - **Current guess area**
        - Directly under the guess history list, add a horizontal row of empty square slots representing positions of the current guess.
        - Use a container such as a `LinearLayout` or another `ConstraintLayout` anchored above the palette.
        - Each slot should be a square `View` or `TextView` with:
            - Border (e.g., using background drawable) and empty interior.
            - IDs and layout parameters that make them evenly spaced across the width.
        - One slot should be visually identifiable as the “active” slot (for now, use a different background to indicate this; add a TODO in the fragment to control it programmatically later).
        - This row should support the future behaviors described in the spec: tapping to change the active slot, drag‑and‑drop from the palette, and swipe‑to‑clear (for now, just ensure IDs and structure support adding those listeners later).
    - **Symbol palette and submit button**
        - Below the current guess row, add the symbol palette as one or more horizontal rows of large, colorful symbol views.
            - For now, you can represent each symbol as a `Button` or `ImageButton` with placeholder colors/text; arrange them using a `Flow` helper or nested `LinearLayout`s so they wrap across multiple rows while filling the width from start to just before the submit button.
        - At the bottom‑end corner (right side), add a “Submit Guess” button.
            - Initially disabled (e.g., `android:enabled="false"`).
            - Constrain it to the bottom and end of the parent.
            - Constrain the palette container so it spans from the start side to the start edge of this button.
3. **Behavior hooks (stubs only)**
    - In `GameFragment`, declare placeholders for:
        - A `RecyclerView` adapter for the guess history.
        - Click listeners for:
            - “New Game” button.
            - “Submit Guess” button.
            - Palette items.
            - Current guess slots.
        - Comments or TODOs indicating:
            - Submit button will be enabled only when all slots are filled.
            - On submit, the list should scroll to the bottom.
            - On game solve, palette and submit button will be disabled and a success message shown in the message/status area.
    - Do **not** implement drag‑and‑drop, gestures, or real game logic yet; just structure the layout and code so those behaviors can be added later.
4. **Style and documentation**
    - Follow our existing project guidelines for:
        - Package naming and class structure.
        - Javadoc on public classes and methods (one‑sentence summary plus tags as needed).
        - Consistent ID naming (lower_snake_case) and constant naming where applicable.
    - Keep the code and XML idiomatic for Android and readable.

Output:

- The complete `GameFragment.java` source.
- The complete `fragment_game.xml` layout, plus any additional minimal item layout XML you create for the guess history rows.

