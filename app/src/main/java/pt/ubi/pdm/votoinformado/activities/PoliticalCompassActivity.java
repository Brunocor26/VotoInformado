package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Question;
import pt.ubi.pdm.votoinformado.classes.QuizManager;

public class PoliticalCompassActivity extends AppCompatActivity {

    private QuizManager quizManager;
    private TextView questionText;
    private TextView questionCounter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_political_compass);

        Toolbar toolbar = findViewById(R.id.toolbar_quiz);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        quizManager = new QuizManager();

        questionText = findViewById(R.id.question_text);
        questionCounter = findViewById(R.id.question_counter);
        progressBar = findViewById(R.id.quiz_progress);
        progressBar.setMax(quizManager.getTotalQuestions());

        setupButtons();
        updateUI();
    }

    private void setupButtons() {
        Button btnStronglyAgree = findViewById(R.id.btn_strongly_agree);
        Button btnAgree = findViewById(R.id.btn_agree);
        Button btnDisagree = findViewById(R.id.btn_disagree);
        Button btnStronglyDisagree = findViewById(R.id.btn_strongly_disagree);

        btnStronglyAgree.setOnClickListener(v -> handleAnswer(1.0));
        btnAgree.setOnClickListener(v -> handleAnswer(0.5));
        btnDisagree.setOnClickListener(v -> handleAnswer(-0.5));
        btnStronglyDisagree.setOnClickListener(v -> handleAnswer(-1.0));
    }

    private void handleAnswer(double multiplier) {
        quizManager.answerQuestion(multiplier);

        if (quizManager.isFinished()) {
            showResults();
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        Question currentQuestion = quizManager.getCurrentQuestion();
        if (currentQuestion != null) {
            questionText.setText(currentQuestion.getText());
            int currentIndex = quizManager.getCurrentQuestionIndex() + 1;
            int total = quizManager.getTotalQuestions();
            questionCounter.setText(String.format("Questão %d de %d", currentIndex, total));
            progressBar.setProgress(currentIndex);
        }
    }

    private void showResults() {
        Intent intent = new Intent(this, PoliticalCompassResultActivity.class);
        intent.putExtra("SCORE_X", quizManager.getFinalEconomicScore());
        intent.putExtra("SCORE_Y", quizManager.getFinalSocialScore());
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
