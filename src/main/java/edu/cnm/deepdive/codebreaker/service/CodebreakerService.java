package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import java.util.concurrent.CompletableFuture;

/**
 * Defines asynchronous operations for creating, retrieving, updating, and deleting {@link Game} and
 * {@link Guess} resources in the Codebreaker application.
 */
public interface CodebreakerService {

  /**
   * Returns the singleton implementation instance of this service.
   *
   * @return singleton {@link CodebreakerService} implementation.
   */
  static CodebreakerService getInstance() {
    return CodebreakerServiceImpl.getInstance();
  }

  /**
   * Starts a new game using the supplied configuration.
   *
   * <p>The {@code game} argument typically specifies the pool and length for the game; the
   * returned {@link Game} includes the server-assigned identifier and initial state that callers
   * should treat as canonical.
   *
   * @param game game configuration and initial state to persist.
   * @return future completing with the persisted {@link Game}, including its identifier and any
   * server-assigned properties.
   */
  CompletableFuture<Game> startGame(Game game);

  /**
   * Retrieves the {@link Game} with the specified identifier.
   *
   * <p>This is commonly used to refresh game state, including the solved flag and
   * associated guesses, after a winning guess has been processed.
   *
   * @param gameId unique identifier of the game to retrieve.
   * @return future completing with the matching {@link Game}, or completing exceptionally if no
   * such game exists or cannot be retrieved.
   */
  CompletableFuture<Game> getGame(String gameId);

  /**
   * Deletes the {@link Game} with the specified identifier.
   *
   * <p>Clients typically treat deletion as effectively idempotent: failures are surfaced
   * via the returned future but do not restore the deleted state.
   *
   * @param gameId unique identifier of the game to delete.
   * @return future that completes when the game has been deleted or completes exceptionally if the
   * game does not exist or cannot be deleted.
   */
  CompletableFuture<Void> deleteGame(String gameId);

  /**
   * Submits a {@link Guess} for evaluation against the specified {@link Game}.
   *
   * <p>The returned {@link Guess} includes evaluation metadata, such as feedback and a
   * solution flag, that callers can merge into the owning game’s guess history or use to trigger a
   * game-state refresh.
   *
   * @param game  game against which the guess is to be evaluated.
   * @param guess guess to evaluate and persist.
   * @return future completing with the persisted {@link Guess}, including any feedback or scoring
   * produced by evaluation.
   */
  CompletableFuture<Guess> submitGuess(Game game, Guess guess);

  /**
   * Retrieves the {@link Guess} with the specified identifiers.
   *
   * <p>This is intended for direct access to a single guess for display or inspection,
   * independent of any locally cached guess list.
   *
   * @param gameId unique identifier of the {@link Game} that owns the guess.
   * @param guessId unique identifier of the guess to retrieve.
   * @return future completing with the matching {@link Guess}, or completing exceptionally
   *     if no such guess exists or cannot be retrieved.
   */
  CompletableFuture<Guess> getGuess(String gameId, String guessId);

  void shutdown();

}
