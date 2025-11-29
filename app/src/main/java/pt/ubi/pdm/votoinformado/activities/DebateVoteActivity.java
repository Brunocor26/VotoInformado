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
        String cand1Name = getIntent().getStringExtra("cand1Name");
        String cand2Name = getIntent().getStringExtra("cand2Name");
        String cand1Photo = getIntent().getStringExtra("cand1Photo");
        String cand2Photo = getIntent().getStringExtra("cand2Photo");

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

        btnVote1.setOnClickListener(v -> vote(cand1Id));
        btnVote2.setOnClickListener(v -> vote(cand2Id));

        checkIfVoted();
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
        // We need to fetch the debate details to check votes
        // Since we don't have a single debate endpoint, we fetch all and filter (not ideal but works for now)
        // Or we can just try to vote and handle "already voted" error, but we want to show results if already voted.
        // Let's fetch all dates.
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance().getApiService().getDates().enqueue(new Callback<List<ImportantDate>>() {
            @Override
            public void onResponse(Call<List<ImportantDate>> call, Response<List<ImportantDate>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    for (ImportantDate d : response.body()) {
                        if (d.getId() != null && d.getId().equals(debateId)) { // Assuming ImportantDate has getId() mapped to _id
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
        if (votes == null) return;

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null); // Assuming user_id is saved

        boolean userVoted = false;
        int count1 = 0;
        int count2 = 0;

        for (ImportantDate.Vote v : votes) {
            if (v.getUserId().equals(userId)) {
                userVoted = true;
            }
            if (v.getCandidateId().equals(cand1Id)) count1++;
            else if (v.getCandidateId().equals(cand2Id)) count2++;
        }

        if (userVoted) {
            showResults(count1, count2);
        }
    }

    private void vote(String candidateId) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
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
                    Toast.makeText(DebateVoteActivity.this, "Erro ao votar: " + response.message(), Toast.LENGTH_SHORT).show();
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

        int total = c1 + c2;
        if (total == 0) {
            txtResult1.setText("0%");
            txtResult2.setText("0%");
        } else {
            int p1 = (c1 * 100) / total;
            int p2 = (c2 * 100) / total;
            txtResult1.setText(p1 + "% (" + c1 + ")");
            txtResult2.setText(p2 + "% (" + c2 + ")");
        }
    }
    
    // ImportantDate needs getId() which maps to _id
}
