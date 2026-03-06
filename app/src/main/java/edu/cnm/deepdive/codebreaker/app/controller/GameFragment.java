package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;
import edu.cnm.deepdive.codebreaker.app.util.SymbolMap;
import edu.cnm.deepdive.codebreaker.app.viewmodel.GameViewModel;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  @Inject
  SymbolMap symbolMap;

  private FragmentGameBinding binding;
  private GameViewModel gameViewModel;

  public GameFragment() {
    // Required empty public constructor
  }

  public static GameFragment newInstance() {
    return new GameFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentGameBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
    LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
    gameViewModel.getGame()
        .observe(lifecycleOwner, game -> {
          // TODO: 3/6/2026 Handle updates to the game.
          //  1. Clear all children from binding.palette.
          //  2. Add new child for every symbol in the game.getPoll().
          //     a. Inflate a layout.
          //     b. Set the symbol text (contentDescription and tooltip).
          //     c. Set the symbol drawable.
          //     d. Set the symbol drawable tint.
          //     e. Add the symbol widget to binding.palette children.
          binding.palette.removeAllViews();
          game.getPool()
              .codePoints()
              .mapToObj(codePoint -> {
                // TODO: 3/6/2026 Inflate a layout and return the inflated widget.
                return (ImageButton) null;
              })
              .map((symbolWidget) -> {
                // TODO: 3/6/2026 Set the symbol text.
                return symbolWidget;
              })
              .map((symbolWidget) -> {
                // TODO: 3/6/2026 Set the symbol drawable
                return symbolWidget;
              })
              .map((symbolWidget) -> {
                // TODO: 3/6/2026 Set drawable tint.
                return symbolWidget;
              })
              .forEach(binding.palette::addView);
        });
    gameViewModel
        .getSolved()
            .observe(lifecycleOwner, solved -> {
              // TODO: 3/6/2026 Handle changes to the solved state of the game.
            });
    gameViewModel
        .getGuess()
        .observe(lifecycleOwner, guess -> {
          // TODO: 3/6/2026 Handle updates to the most recent guess.
        });
    gameViewModel.
        getError()
            .observe(lifecycleOwner, error -> {
              // TODO: 3/6/2026 Handle error.
            });
    setupViews();
    setupViewModel();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void setupViews() {
    // TODO: Initialize RecyclerView adapter for guess history.
    // TODO: Set click listener for "New Game" button.
    // TODO: Set click listener for "Submit Guess" button.
    // TODO: Set click listeners for palette items.
    // TODO: Set click listeners for current guess slots.
    // TODO: Submit button will be enabled only when all slots are filled.
    // TODO: On submit, the list should scroll to the bottom.
    // TODO: On game solve, palette and submit button will be disabled and a success message shown in the status area.
  }

  private void setupViewModel() {
    // TODO: Connect to GameViewModel and observe LiveData.
  }

}
