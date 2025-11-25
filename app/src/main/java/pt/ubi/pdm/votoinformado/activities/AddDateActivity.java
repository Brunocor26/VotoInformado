package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Debate;
import pt.ubi.pdm.votoinformado.classes.Entrevista;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.utils.FirebaseUtils;

public class AddDateActivity extends AppCompatActivity {

    private EditText editTitle, editDate, editTime, editCategory, editCandidate, editCandidate1, editCandidate2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);

        editTitle = findViewById(R.id.edit_date_title);
        editDate = findViewById(R.id.edit_date_date);
        editTime = findViewById(R.id.edit_date_time);
        editCategory = findViewById(R.id.edit_date_category); // Ex: "Debate", "Entrevista", "Eleições"

        // IDs dos candidatos (Strings)
        editCandidate = findViewById(R.id.edit_date_candidate);
        editCandidate1 = findViewById(R.id.edit_date_candidate1);
        editCandidate2 = findViewById(R.id.edit_date_candidate2);

        Button btnSave = findViewById(R.id.btn_save_date);

        // Removido o listener duplicado que tinhas no código original
        btnSave.setOnClickListener(v -> saveDate());
    }

    private void saveDate() {
        String title = getStringOrNull(editTitle);
        String date = getStringOrNull(editDate);
        String time = getStringOrNull(editTime);
        String category = getStringOrNull(editCategory);

        // Validação básica
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Title and Category are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        ImportantDate objectToSave;

        // --- LÓGICA DE DECISÃO ---
        // Dependendo do que escreveste na categoria, instanciamos classes diferentes

        if (category.equalsIgnoreCase("Debate")) {
            String id1 = getStringOrNull(editCandidate1);
            String id2 = getStringOrNull(editCandidate2);

            // Cria um objeto Debate (que herda de ImportantDate)
            objectToSave = new Debate(title, date, time, category, id1, id2);

        } else if (category.equalsIgnoreCase("Entrevista")) {
            String id = getStringOrNull(editCandidate);

            // Cria um objeto Entrevista (que herda de ImportantDate)
            objectToSave = new Entrevista(title, date, time, category, id);

        } else {
            // Para "Eleições", "Voto antecipado" ou outros
            objectToSave = new ImportantDate(title, date, time, category);
        }

        // O Firebase aceita objectToSave porque todas herdam de ImportantDate.
        // Ele vai guardar automaticamente os campos extras se for Debate ou Entrevista.
        FirebaseUtils.saveDate(objectToSave, AddDateActivity.this);

        // Opcional: Fechar a activity após guardar
        finish();
    }

    private String getStringOrNull(EditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text) ? null : text;
    }
}