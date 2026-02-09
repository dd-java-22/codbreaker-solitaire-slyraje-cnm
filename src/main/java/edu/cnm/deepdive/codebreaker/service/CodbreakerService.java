package edu.cnm.deepdive.codebreaker.service;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
import java.util.concurrent.CompletableFuture;

enum CodbreakerService implements AbstractCodebreakerService {

  INSTANCE;

  private final CodebreakerApi api;


  CodbreakerService() {
    //TODO: 2026-02-09 DO initialization of Gson, Retrofit, and CodebreakerApi
  }

  @Override
  public CompletableFuture<Game> startGame(Game game) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public CompletableFuture<Game> getGame(String gameId) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public CompletableFuture<Guess> getGuess(String gameId, String guessId) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public CompletableFuture<Guess> submitGuess(String gameId, Guess guess) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public CompletableFuture<Void> deleteGame(String gameId) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
