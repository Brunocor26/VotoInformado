package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.UUID;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Peticao;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class CreatePeticaoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText tituloEdit, descEdit;
    private Button btnPublicar, btnSelectImage;
    private ImageView imagePreview;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_peticao);

        tituloEdit = findViewById(R.id.edit_titulo_peticao);
        descEdit = findViewById(R.id.edit_desc_peticao);
        btnPublicar = findViewById(R.id.btn_publicar_peticao);
        btnSelectImage = findViewById(R.id.btn_select_image);
        imagePreview = findViewById(R.id.image_preview);

        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnPublicar.setOnClickListener(v -> publicarPeticao());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void publicarPeticao() {
        String titulo = tituloEdit.getText().toString().trim();
        String desc = descEdit.getText().toString().trim();

        if (titulo.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Erro: Utilizador não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublicar.setEnabled(false); // Desativa o botão para evitar cliques duplos

        String nome = user.getDisplayName();
        if (nome == null || nome.isEmpty()) nome = user.getEmail();

        Peticao novaPeticao = new Peticao(titulo, desc, user.getUid(), nome);

        if (imageUri != null) {
            uploadImageAndSavePeticao(novaPeticao);
        } else {
            savePeticaoOnly(novaPeticao);
        }
    }

    private void savePeticaoOnly(Peticao peticao) {
        DatabaseHelper.savePeticao(peticao, this, new DatabaseHelper.SaveCallback() {
            @Override
            public void onSuccess() {
                finish(); // Fecha a activity após sucesso
            }
            @Override
            public void onFailure(String message) {
                btnPublicar.setEnabled(true); // Reativa o botão em caso de falha
            }
        });
    }

    private void uploadImageAndSavePeticao(Peticao peticao) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("peticao_images")
                .child(UUID.randomUUID().toString());

        storageRef.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                peticao.setImageUrl(uri.toString());
                savePeticaoOnly(peticao);
            }))
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Falha no upload da imagem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                btnPublicar.setEnabled(true); // Reativa o botão em caso de falha
            });
    }
}
