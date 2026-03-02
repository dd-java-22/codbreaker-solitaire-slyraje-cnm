package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.ActivityMainBinding;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    GameViewModel viewModel = new ViewModelProvider(this).get(GameViewModel.class);
    viewModel
        .getGame()
        .observe(this, (game) -> binding.response.setText(game.toString()));
    binding.text.setOnClickListener((v) -> viewModel.startGame("ABDCEF", 6));
  }

}
