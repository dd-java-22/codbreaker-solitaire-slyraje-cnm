package edu.cnm.deepdive.codebreaker;

import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.service.CodebreakerService;
import edu.cnm.deepdive.codebreaker.viewmodel.GameViewModel;
import java.util.concurrent.CompletableFuture;

public class Main {

  static void main() {

    GameViewModel viewModel = new GameViewModel();
    viewModel.registerGameObserver(System.out::println);
    viewModel.startGame("ABCDE", 2);
    System.out.println("Game start requested!");
  }
}
