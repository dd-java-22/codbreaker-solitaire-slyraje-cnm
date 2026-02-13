package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import java.util.concurrent.CompletableFuture;

/**
 * Defines asynchronous operations for creating, retrieving, evaluating, and deleting
 * {@link Game} and {@link Guess} resources in the Codebreaker Solitaire service.
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
   * <p>The {@code game} argument is validated locally before any request is sent. The code
   * length must be between the implementation-defined minimum and maximum (inclusive),
   * the pool length must be within its allowed bounds, and every pool character must be
   * defined, non-whitespace, and non-control. If validation fails, the returned future
   * completes exceptionally with {@code InvalidPayloadException} and no HTTP call is made.
   *
   * <p>On a successful service response, the returned {@link Game} includes the
   * server-assigned identifier and initial state that callers should treat as canonical.
   * On an error response, the future completes exceptionally with one of:
   * {@code InvalidPayloadException} (HTTP 400), {@code UnknownServiceException} (HTTP 500),
   * or another unchecked exception if the status code is unmapped or an I/O error occurs.
   *
   * @param game game configuration and initial state to validate and persist.
   * @return future completing with the persisted {@link Game}, or completing exceptionally
   *     if validation fails, the game cannot be created, or a service error occurs.
   */
  CompletableFuture<Game> startGame(Game game);

  /**
   * Retrieves the {@link Game} with the specified identifier.
   *
   * <p>If the service returns a successful response, the future completes with the
   * corresponding {@link Game}. If the game does not exist, the future completes
   * exceptionally with {@code ResourceNotFoundException} (HTTP 404). Other service
   * errors (including HTTP 500 and I/O failures) also cause exceptional completion,
   * typically with {@code UnknownServiceException} or a lower-level runtime exception.
   *
   * @param gameId unique identifier of the game to retrieve; must be a valid game id
   *     recognized by the service.
   * @return future completing with the matching {@link Game}, or completing exceptionally
   *     if the game does not exist or a service error occurs.
   */
  CompletableFuture<Game> getGame(String gameId);

  /**
   * Deletes the {@link Game} with the specified identifier.
   *
   * <p>If the deletion succeeds, the returned future completes normally. If the game
   * does not exist, the future completes exceptionally with
   * {@code ResourceNotFoundException} (HTTP 404). Other service errors (including HTTP 400,
   * HTTP 500, and I/O failures) also cause exceptional completion, typically with
   * {@code InvalidPayloadException}, {@code UnknownServiceException}, or another runtime
   * exception.
   *
   * @param gameId unique identifier of the game to delete; must be a valid game id
   *     recognized by the service.
   * @return future that completes when the game has been deleted, or completes
   *     exceptionally if the game does not exist or a service error occurs.
   */
  CompletableFuture<Void> deleteGame(String gameId);

  /**
   * Submits a {@link Guess} for evaluation against the specified {@link Game}.
   *
   * <p>The {@code game} and {@code guess} arguments are validated locally before any
   * request is sent. The guess text length must exactly match the game code length, and
   * every character of the guess must be a member of the game’s pool. If validation
   * fails, the returned future completes exceptionally with {@code InvalidPayloadException}
   * and no HTTP call is made.
   *
   * <p>On a successful service response, the returned {@link Guess} includes evaluation
   * metadata (for example, feedback and any solution flag) that callers can use to update
   * local game state. If the game does not exist, the game has already been solved, or
   * another service error occurs, the future completes exceptionally with
   * {@code ResourceNotFoundException} (HTTP 404), {@code GameSolvedException} (HTTP 409),
   * {@code UnknownServiceException} (HTTP 500), or another runtime exception for I/O and
   * unmapped errors.
   *
   * @param game game against which the guess is to be validated and evaluated; must
   *     reference a game recognized by the service.
   * @param guess guess to validate, evaluate, and persist.
   * @return future completing with the persisted {@link Guess}, or completing
   *     exceptionally if validation fails, the game is not in a valid state to accept
   *     guesses, or a service error occurs.
   */
  CompletableFuture<Guess> submitGuess(Game game, Guess guess);

  /**
   * Retrieves the {@link Guess} with the specified identifiers.
   *
   * <p>If the service returns a successful response, the future completes with the
   * corresponding {@link Guess}. If the game or guess does not exist, the future
   * completes exceptionally with {@code ResourceNotFoundException} (HTTP 404). Other
   * service errors (including HTTP 400, HTTP 500, and I/O failures) also cause exceptional
   * completion, typically with {@code InvalidPayloadException}, {@code UnknownServiceException},
   * or another runtime exception.
   *
   * @param gameId unique identifier of the {@link Game} that owns the guess; must
   *     reference a game recognized by the service.
   * @param guessId unique identifier of the guess to retrieve; must reference a guess
   *     associated with the specified game.
   * @return future completing with the matching {@link Guess}, or completing exceptionally
   *     if the game or guess does not exist or a service error occurs.
   */
  CompletableFuture<Guess> getGuess(String gameId, String guessId);

  /**
   * Initiates an orderly shutdown of the underlying HTTP client resources.
   *
   * <p>This method shuts down the internal executor service used for dispatching HTTP
   * requests and evicts all connections from the client connection pool. After this
   * method returns, further calls to the service methods are not guaranteed to succeed
   * and may fail immediately.
   */
  void shutdown();

}
