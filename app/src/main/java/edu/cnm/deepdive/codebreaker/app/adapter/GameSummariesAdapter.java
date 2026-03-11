package edu.cnm.deepdive.codebreaker.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import dagger.hilt.android.qualifiers.ActivityContext;
import edu.cnm.deepdive.codebreaker.app.databinding.ItemGameSummaryBinding;
import edu.cnm.deepdive.codebreaker.app.model.GameSummary;
import jakarta.inject.Inject;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class GameSummariesAdapter extends RecyclerView.Adapter<ViewHolder> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter
      .ofLocalizedDateTime(FormatStyle.SHORT)
      .withZone(ZoneId.systemDefault());

  private final LayoutInflater inflater;
  private final List<GameSummary> gameSummaries;

  @Inject
  public GameSummariesAdapter(@ActivityContext Context context) {
    inflater = LayoutInflater.from(context);
    gameSummaries = new ArrayList<>();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new GameSummaryHolder(ItemGameSummaryBinding.inflate(inflater, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ((GameSummaryHolder) holder).bind(position);
  }

  @Override
  public int getItemCount() {
    return gameSummaries.size();
  }

  public List<GameSummary> getGameSummaries() {
    return gameSummaries;
  }

  private class GameSummaryHolder extends ViewHolder {

    private final ItemGameSummaryBinding binding;

    public GameSummaryHolder(@NonNull ItemGameSummaryBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    private void bind(int position) {
      GameSummary summary = gameSummaries.get(position);
      binding.lastPlayed.setText(FORMATTER.format(summary.getLastPlayed()));
      binding.poolSize.setText(String.valueOf(summary.getPoolSize()));
      binding.codeLength.setText(String.valueOf(summary.getCodeLength()));
      binding.guessCount.setText(String.valueOf(summary.getGuessCount()));
    }
  }
}
