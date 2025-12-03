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

        // Get user data from SharedPreferences
        // Get user data from EncryptedSharedPreferences
        android.content.SharedPreferences prefs = null;
        try {
            String masterKey = androidx.security.crypto.MasterKeys.getOrCreate(androidx.security.crypto.MasterKeys.AES256_GCM_SPEC);
            prefs = androidx.security.crypto.EncryptedSharedPreferences.create(
                    "user_session_secure",
                    masterKey,
                    this,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String userId = prefs.getString("user_id", null);
        String userName = prefs.getString("user_name", "Utilizador");

        if (userId == null) {
            Toast.makeText(this, "Erro: Utilizador não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublicar.setEnabled(false); // Desativa o botão para evitar cliques duplos

        String nome = userName;

        Peticao novaPeticao = new Peticao(titulo, desc, userId, nome);

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
        try {
            java.io.File file = getFileFromUri(imageUri);
            okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse(getContentResolver().getType(imageUri)), file);
            okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            pt.ubi.pdm.votoinformado.api.ApiClient.getInstance().getApiService().uploadPetitionImage(body).enqueue(new retrofit2.Callback<java.util.Map<String, String>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.Map<String, String>> call, retrofit2.Response<java.util.Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String imageUrl = response.body().get("imageUrl");
                        peticao.setImageUrl(imageUrl);
                        savePeticaoOnly(peticao);
                    } else {
                        Toast.makeText(CreatePeticaoActivity.this, "Falha no upload: " + response.message(), Toast.LENGTH_SHORT).show();
                        btnPublicar.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<java.util.Map<String, String>> call, Throwable t) {
                    Toast.makeText(CreatePeticaoActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnPublicar.setEnabled(true);
                }
            });
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
            btnPublicar.setEnabled(true);
        }
    }

    private java.io.File getFileFromUri(Uri uri) throws java.io.IOException {
        android.content.ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(uri);
        String extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        java.io.File file = java.io.File.createTempFile("temp_image", "." + extension, getCacheDir());
        
        try (java.io.InputStream inputStream = contentResolver.openInputStream(uri);
             java.io.OutputStream outputStream = new java.io.FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return file;
    }
}
