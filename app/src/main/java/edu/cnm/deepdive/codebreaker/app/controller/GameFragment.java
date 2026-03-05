package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentGameBinding;

@AndroidEntryPoint
public class GameFragment extends Fragment {

  private FragmentGameBinding binding;

  /**
   * Public no-arg constructor as required by {@link Fragment}.
   */
  public GameFragment() {
    // Required empty public constructor
  }

  /**
   * Static factory method for creating a new instance of this fragment.
   *
   * @return A new instance of {@link GameFragment}.
   */
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
