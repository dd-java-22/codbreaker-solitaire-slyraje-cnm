# CRUD Inventory for `GameSummary`

## Create

- Create a `GameSummary` after a game is started successfully, when the completed `Game` instance comes back from the web service.
- Create a `GameSummary` when a remote game is discovered (for example, via a status check) and there is no existing summary with the same external key.

## Read

- Read all `GameSummary` records for games that are not solved yet, ordered by the most recent time a guess was submitted (or by started time if there are no guesses).
- Retrieve the most recently played unsolved `GameSummary` to resume play automatically when the user returns to the app.

## Update

- Update `guessCount`, `exactMatches`, `nearMatches`, `lastPlayed`, and `solved` on a `GameSummary` each time the user submits a guess and the service returns a new status.
- Update `lastPlayed` when the user opens or interacts with a game so that recently played games stay at the top of the list.

## Delete

- Delete a `GameSummary` automatically if a status request shows that the corresponding game no longer exists on the server (for example, it expired from inactivity).
- Delete one or more `GameSummary` records when the user chooses to abandon selected in‑progress games or wipe all stored game data on the device.
