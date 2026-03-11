package edu.cnm.deepdive.codebreaker.app.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.codebreaker.app.R;
import edu.cnm.deepdive.codebreaker.app.databinding.FragmentInProgressBinding;
import edu.cnm.deepdive.codebreaker.app.viewmodel.SummaryViewModel;


public class InProgressFragment extends Fragment {

  private FragmentInProgressBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentInProgressBinding.inflate(inflater, container, false);
    // TODO: 3/11/2026 Attach adapter to recylcerview. 
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    SummaryViewModel viewModel = new ViewModelProvider(requireActivity()).get(SummaryViewModel.class);
    viewModel
        .getInProgressSummaries()
        .observe(getViewLifecycleOwner(), (summaries -> {
          // TODO: 3/11/2026 Populate recyclerview adapter with summaries. 
        }));
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}