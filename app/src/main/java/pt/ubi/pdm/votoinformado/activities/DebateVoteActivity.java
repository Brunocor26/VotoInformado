package pt.ubi.pdm.votoinformado.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DebateVoteActivity extends AppCompatActivity {

    private com.github.mikephil.charting.charts.PieChart pieChart;
    private String cand1Name, cand2Name;
    private boolean isVotingClosed = false;

    private String debateId, cand1Id, cand2Id;
    private TextView txtResult1, txtResult2;
    private Button btnVote1, btnVote2;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debate_vote);

        debateId = getIntent().getStringExtra("debateId");
        cand1Id = getIntent().getStringExtra("cand1Id");
        cand2Id = getIntent().getStringExtra("cand2Id");
        String title = getIntent().getStringExtra("title");
        cand1Name = getIntent().getStringExtra("cand1Name");
        cand2Name = getIntent().getStringExtra("cand2Name");
        String cand1Photo = getIntent().getStringExtra("cand1Photo");
        String cand2Photo = getIntent().getStringExtra("cand2Photo");
        String eventDateTimeStr = getIntent().getStringExtra("eventDateTime");

        ((TextView) findViewById(R.id.txtDebateTitle)).setText(title);
        ((TextView) findViewById(R.id.txtCand1Name)).setText(cand1Name);
        ((TextView) findViewById(R.id.txtCand2Name)).setText(cand2Name);

        CircleImageView img1 = findViewById(R.id.imgCand1);
        CircleImageView img2 = findViewById(R.id.imgCand2);

        loadImage(cand1Photo, img1);
        loadImage(cand2Photo, img2);

        btnVote1 = findViewById(R.id.btnVote1);
        btnVote2 = findViewById(R.id.btnVote2);
        txtResult1 = findViewById(R.id.txtResult1);
        txtResult2 = findViewById(R.id.txtResult2);
        progressBar = findViewById(R.id.progressBarVoting);
        pieChart = findViewById(R.id.pieChart);
        setupPieChart();

        // Check 24h rule
        if (eventDateTimeStr != null) {
            java.time.LocalDateTime eventDateTime = java.time.LocalDateTime.parse(eventDateTimeStr);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (now.isAfter(eventDateTime.plusHours(24))) {
                isVotingClosed = true;
                btnVote1.setEnabled(false);
                btnVote2.setEnabled(false);
                btnVote1.setText("Encerrado");
                btnVote2.setText("Encerrado");
                Toast.makeText(this, "A votação para este debate já encerrou.", Toast.LENGTH_SHORT).show();
            }
        }

        btnVote1.setOnClickListener(v -> {
            if (!isVotingClosed) vote(cand1Id);
        });
        btnVote2.setOnClickListener(v -> {
            if (!isVotingClosed) vote(cand2Id);
        });

        checkIfVoted(); // This will also load results if voting is closed
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.graphics.Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK);
    }

    private void loadImage(String url, CircleImageView img) {
        if (url != null && !url.isEmpty()) {
            if (url.contains("localhost") || url.contains("127.0.0.1")) {
                String relativePath = url.replaceAll("http://localhost:\\d+", "")
                                              .replaceAll("http://127.0.0.1:\\d+", "")
                                              .replace('\\', '/');
                if (!relativePath.startsWith("/")) relativePath = "/" + relativePath;
                url = ApiClient.getBaseUrl() + relativePath.replaceFirst("^/", "");
            } else if (!url.startsWith("http")) {
                 url = ApiClient.getBaseUrl() + url.replaceFirst("^/", "");
            }
            Picasso.get().load(url).placeholder(R.drawable.candidato_generico).into(img);
        } else {
            img.setImageResource(R.drawable.candidato_generico);
        }
    }

    private void checkIfVoted() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance().getApiService().getDates().enqueue(new Callback<List<ImportantDate>>() {
            @Override
            public void onResponse(Call<List<ImportantDate>> call, Response<List<ImportantDate>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    for (ImportantDate d : response.body()) {
                        if (d.getId() != null && d.getId().equals(debateId)) {
                            processVotes(d);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ImportantDate>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void processVotes(ImportantDate debate) {
        List<ImportantDate.Vote> votes = debate.getVotes();
        if (votes == null) votes = new java.util.ArrayList<>(); // Handle null votes

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        boolean userVoted = false;
        int count1 = 0;
        int count2 = 0;

        for (ImportantDate.Vote v : votes) {
            if (userId != null && v.getUserId().equals(userId)) {
                userVoted = true;
            }
            if (v.getCandidateId().equals(cand1Id)) count1++;
            else if (v.getCandidateId().equals(cand2Id)) count2++;
        }

        if (userVoted || isVotingClosed) {
            showResults(count1, count2);
        }
    }

    private void vote(String candidateId) {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "Erro: Utilizador não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("candidateId", candidateId);

        ApiClient.getInstance().getApiService().voteDebate(debateId, body).enqueue(new Callback<ImportantDate>() {
            @Override
            public void onResponse(Call<ImportantDate> call, Response<ImportantDate> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    processVotes(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        android.util.Log.e("DebateVoteActivity", "Erro API: " + errorBody);
                        Toast.makeText(DebateVoteActivity.this, "Erro ao votar", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        android.util.Log.e("DebateVoteActivity", "Erro: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ImportantDate> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DebateVoteActivity.this, "Erro de rede", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResults(int c1, int c2) {
        btnVote1.setVisibility(View.GONE);
        btnVote2.setVisibility(View.GONE);
        txtResult1.setVisibility(View.VISIBLE);
        txtResult2.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.VISIBLE);

        int total = c1 + c2;
        if (total == 0) {
            txtResult1.setText("0%");
            txtResult2.setText("0%");
            pieChart.setNoDataText("Sem votos ainda.");
            pieChart.invalidate();
        } else {
            int p1 = (c1 * 100) / total;
            int p2 = (c2 * 100) / total;
            txtResult1.setText(p1 + "% (" + c1 + ")");
            txtResult2.setText(p2 + "% (" + c2 + ")");

            java.util.ArrayList<com.github.mikephil.charting.data.PieEntry> entries = new java.util.ArrayList<>();
            if (c1 > 0) entries.add(new com.github.mikephil.charting.data.PieEntry((float) c1, cand1Name));
            if (c2 > 0) entries.add(new com.github.mikephil.charting.data.PieEntry((float) c2, cand2Name));

            com.github.mikephil.charting.data.PieDataSet dataSet = new com.github.mikephil.charting.data.PieDataSet(entries, "Resultados");
            dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(android.graphics.Color.BLACK);

            com.github.mikephil.charting.data.PieData data = new com.github.mikephil.charting.data.PieData(dataSet);
            pieChart.setData(data);
            pieChart.invalidate();
            pieChart.animateY(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad);
        }
    }
    
    // ImportantDate needs getId() which maps to _id
}
