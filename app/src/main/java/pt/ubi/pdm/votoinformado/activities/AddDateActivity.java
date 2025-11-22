package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class AddDateActivity extends AppCompatActivity {

    private EditText editTitle, editDate, editTime, editCategory, editCandidate, editCandidate1, editCandidate2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);

        editTitle = findViewById(R.id.edit_date_title);
        editDate = findViewById(R.id.edit_date_date);
        editTime = findViewById(R.id.edit_date_time);
        editCategory = findViewById(R.id.edit_date_category);
        editCandidate = findViewById(R.id.edit_date_candidate);
        editCandidate1 = findViewById(R.id.edit_date_candidate1);
        editCandidate2 = findViewById(R.id.edit_date_candidate2);

        Button btnSave = findViewById(R.id.btn_save_date);
        btnSave.setOnClickListener(v -> saveDate());
    }

    private void saveDate() {
        String title = getStringOrNull(editTitle);
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        ImportantDate importantDate = new ImportantDate(
                title,
                getStringOrNull(editDate),
                getStringOrNull(editTime),
                getStringOrNull(editCategory),
                getStringOrNull(editCandidate),
                getStringOrNull(editCandidate1),
                getStringOrNull(editCandidate2)
        );

        DatabaseHelper.saveDate(importantDate, AddDateActivity.this);
    }

    private String getStringOrNull(EditText editText) {
        String text = editText.getText().toString().trim();
        return TextUtils.isEmpty(text) ? null : text;
    }
}
