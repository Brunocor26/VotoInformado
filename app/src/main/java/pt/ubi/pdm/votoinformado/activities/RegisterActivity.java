package pt.ubi.pdm.votoinformado.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextName, editTextEmail, editTextPassword;
    private MaterialButton buttonRegister;
    private CircleImageView profileImageView;
    private TextView loginRedirectText;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    profileImageView.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        profileImageView = findViewById(R.id.image_profile);
        loginRedirectText = findViewById(R.id.login_redirect_text);

        setupClickListeners();
    }

    private void setupClickListeners() {
        profileImageView.setOnClickListener(v -> openGallery());
        buttonRegister.setOnClickListener(v -> registerUser());
        loginRedirectText.setOnClickListener(v -> finish());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody passwordPart = RequestBody.create(MediaType.parse("text/plain"), password);

        MultipartBody.Part photoPart = null;
        if (selectedImageUri != null) {
            try {
                File file = getFileFromUri(selectedImageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
                photoPart = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ApiClient.getInstance().getApiService().register(namePart, emailPart, passwordPart, photoPart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String responseBody = response.body().string();
                                org.json.JSONObject jsonObject = new org.json.JSONObject(responseBody);
                                String token = jsonObject.getString("token");
                                String userId = jsonObject.optString("_id", "");
                                String name = jsonObject.optString("name", "Utilizador");
                                String email = jsonObject.optString("email", "");
                                String photoUrl = jsonObject.optString("photoUrl", "");
                                
                                // Save to EncryptedSharedPreferences
                                try {
                                    MasterKey masterKey = new MasterKey.Builder(RegisterActivity.this)
                                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                                            .build();

                                    SharedPreferences prefs = EncryptedSharedPreferences.create(
                                            RegisterActivity.this,
                                            "user_session_secure",
                                            masterKey,
                                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                                    );

                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("auth_token", token);
                                    editor.putString("user_id", userId);
                                    editor.putString("user_name", name);
                                    editor.putString("user_email", email);
                                    editor.putString("user_photo_url", photoUrl);
                                    editor.apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegisterActivity.this, "Erro de segurança ao salvar dados", Toast.LENGTH_SHORT).show();
                                }
                                
                                Toast.makeText(RegisterActivity.this, "Utilizador registado com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "Erro ao processar resposta", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Erro ao registar: " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private File getFileFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(uri);
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        File file = File.createTempFile("temp_image", "." + extension, getCacheDir());
        
        try (InputStream inputStream = contentResolver.openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return file;
    }
}
