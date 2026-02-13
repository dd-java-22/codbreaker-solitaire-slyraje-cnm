package edu.cnm.deepdive.codebreaker.viewmodel;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import edu.cnm.deepdive.codebreaker.service.CodebreakerService;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Platform;

/**
 * Maintains the client-side state and presentation logic for a single {@link Game},
 * coordinating asynchronous interactions with the {@link CodebreakerService}.
 */
@SuppressWarnings({"UnusedReturnValue", "CallToPrintStackTrace", "unused"})
public class GameViewModel {

  private final CodebreakerService service;
  private final List<Consumer<Game>> gameObservers;
  private final List<Consumer<Guess>> guessObservers;
  private final List<Consumer<Throwable>> errorObservers;
  private final List<Consumer<Boolean>> solvedObservers;

  private Game game;
  private Guess guess;
  private Boolean solved;
  private Throwable error;

  private GameViewModel() {
    service = CodebreakerService.getInstance();
    gameObservers = new LinkedList<>();
    guessObservers = new LinkedList<>();
    errorObservers = new LinkedList<>();
    solvedObservers = new LinkedList<>();
  }

  /**
   * Returns a reference to an instance of the class.
   *
   * <p>This class follows the singleton design
   * pattern; that is, repeated or concurrent calls to this method will all return the same
   * reference.
   *
   * @return
   */
  public static GameViewModel getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * Starts a new game using the specified pool and code length.
   *
   * <p>On success, the newly created {@link Game} is stored as the current game, the
   * solved state is updated, and observers are notified on the JavaFX application thread.
   * On failure, the error is recorded and error observers are notified.
   *
   * @param pool character pool from which the secret code is drawn.
   * @param length length of the secret code.
   */
  public void startGame(String pool, int length) {
    Game game = new Game()
        .pool(pool)
        .length(length);
    service
        .startGame(game)
        .thenApply((startedGame) -> setGame(startedGame).getSolved())
        .thenAccept(this::setSolved)
        .exceptionally(this::logError);
  }

  /**
   * Loads an existing game by identifier.
   *
   * <p>On success, the retrieved {@link Game} becomes the current game, the solved state
   * is updated, and observers are notified on the JavaFX application thread. On failure,
   * the error is recorded and error observers are notified.
   *
   * @param gameId unique identifier of the game to load.
   */
  public void getGame(String gameId) {
    service
        .getGame(gameId)
        .thenApply((game) -> setGame(game).getSolved())
        .thenAccept(this::setSolved)
        .exceptionally(this::logError);
  }

  /**
   * Requests deletion of the specified game without altering the current view-model state.
   *
   * <p>Any error encountered is recorded and reported to error observers.
   *
   * @param gameId unique identifier of the game to delete.
   */
  public void deleteGame(String gameId) {
    service
        .deleteGame(gameId)
        .exceptionally(this::logError);
  }

  /**
   * Deletes the current game, if present, and clears the stored game reference.
   *
   * <p>On successful deletion, the current {@link Game} is set to {@code null} and game
   * observers are notified on the JavaFX application thread. Errors are recorded and
   * reported to error observers.
   */
  public void deleteGame() {
    service
        .deleteGame(game.getId())
        .thenRun(() -> setGame(null))
        .exceptionally(this::logError);
  }

  /**
   * Submits a new guess for the current game.
   *
   * <p>On success, the returned {@link Guess} is stored as the current guess, guess
   * observers are notified, and either the game is reloaded (if the solution was found) or
   * the guess is appended to the local game history and game observers are notified.
   * Errors are recorded and reported to error observers.
   *
   * @param text guess text to submit for evaluation.
   */
  public void submitGuess(String text) {
    Guess guess = new Guess()
        .text(text);
    service
        .submitGuess(game, guess)
        .thenApply(this::setGuess)
        .thenAccept((guessResponse) -> {
          if (Boolean.TRUE.equals(guessResponse.getSolution())) {
            getGame(game.getId());
          } else {
            //noinspection DataFlowIssue
            game.getGuesses().add(guessResponse);
            setGame(game);
          }
        })
        .exceptionally(this::logError);
  }

  /**
   * Loads a specific guess for the current game.
   *
   * <p>On success, the retrieved {@link Guess} is stored as the current guess and guess
   * observers are notified on the JavaFX application thread. Errors are recorded and
   * reported to error observers.
   *
   * @param guessId unique identifier of the guess to retrieve.
   */
  public void getGuess(String guessId) {
    service
        .getGuess(game.getId(), guessId)
        .thenAccept(this::setGuess)
        .exceptionally(this::logError);
  }

  /**
   * Initiates shutdown of the underlying {@link CodebreakerService}.
   *
   * <p>This method does not clear local state or unregister observers.
   */
  public void shutdown() {
    service.shutdown();
  }

  /**
   * Registers an observer to receive updates to the current {@link Game}.
   *
   * <p>If a game is already available when this method is called, the observer is invoked
   * immediately with the current game.
   *
   * @param observer consumer invoked whenever the current game reference changes.
   */
  public void registerGameObserver(Consumer<Game> observer) {
    gameObservers.add(observer);
    if (game != null) {
      observer.accept(game);
    }
  }

  /**
   * Registers an observer to receive updates to the current {@link Guess}.
   *
   * <p>If a guess is already available when this method is called, the observer is invoked
   * immediately with the current guess.
   *
   * @param observer consumer invoked whenever the current guess reference changes.
   */
  public void registerGuessObserver(Consumer<Guess> observer) {
    guessObservers.add(observer);
    if (guess != null) {
      observer.accept(guess);
    }
  }

  /**
   * Registers an observer to receive updates to the solved state of the current game.
   *
   * <p>If the solved state is already known when this method is called, the observer is
   * invoked immediately with the current flag value.
   *
   * @param observer consumer invoked whenever the solved flag changes.
   */
  public void registerSolvedObserver(Consumer<Boolean> observer) {
    solvedObservers.add(observer);
    if (solved != null) {
      observer.accept(solved);
    }
  }

  /**
   * Registers an observer to receive error notifications.
   *
   * <p>If an error has already been recorded when this method is called, the observer is
   * invoked immediately with the most recent error.
   *
   * @param observer consumer invoked whenever an error is recorded.
   */
  public void registerErrorObserver(Consumer<Throwable> observer) {
    errorObservers.add(observer);
    if (error != null) {
      observer.accept(error);
    }
  }

  private Game setGame(Game game) {
    this.game = game;
    Platform.runLater(() -> gameObservers
        .forEach((consumer) -> consumer.accept(game)));
    return game;
  }

  private Guess setGuess(Guess guess) {
    this.guess = guess;
    Platform.runLater(() -> guessObservers
        .forEach((consumer) -> consumer.accept(guess)));
    return guess;
  }

  private Boolean setSolved(Boolean solved) {
    this.solved = solved;
    Platform.runLater(() -> solvedObservers
        .forEach((consumer) -> consumer.accept(solved)));
    return solved;
  }

  private Throwable setError(Throwable error) {
    this.error = error;
    Platform.runLater(() -> errorObservers
        .forEach((consumer) -> consumer.accept(error)));
    return error;
  }

  private Void logError(Throwable error) {
    //noinspection ThrowableNotThrown
    setError(error.getCause() != null ? error.getCause() : error);
//    this.error.printStackTrace();
    return null;
  }

  private static class Holder {

    static final GameViewModel INSTANCE = new GameViewModel();

  }

}
