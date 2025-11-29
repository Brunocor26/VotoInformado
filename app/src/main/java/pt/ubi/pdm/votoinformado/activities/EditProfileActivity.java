package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

public class EditProfileActivity extends AppCompatActivity {
    
    private CircleImageView profileImage;
    private TextInputEditText editName, editEmail;
    private Button btnSave;
    private Uri selectedImageUri;
    
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                profileImage.setImageURI(selectedImageUri);
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profile_image);
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        btnSave = findViewById(R.id.btn_save);

        loadUserData();
        setupClickListeners();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String name = prefs.getString("user_name", "");
        String email = prefs.getString("user_email", "");
        String photoUrl = prefs.getString("user_photo_url", "");

        editName.setText(name);
        editEmail.setText(email);

        if (photoUrl != null && !photoUrl.isEmpty()) {
            if (!photoUrl.startsWith("http")) {
                photoUrl = ApiClient.getBaseUrl() + photoUrl.replaceFirst("^/", "");
            }
            Picasso.get().load(photoUrl).placeholder(R.drawable.candidato_generico).into(profileImage);
        }
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> openGallery());
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void updateProfile() {
        String name = editName.getText().toString().trim();
        
        if (name.isEmpty()) {
            Toast.makeText(this, "O nome não pode estar vazio", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        MultipartBody.Part photoPart = null;

        if (selectedImageUri != null) {
            try {
                File file = getFileFromUri(selectedImageUri);
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
                photoPart = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
            } catch (IOException e) {
                Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                return;
            }
        }

        ApiClient.getInstance().getApiService().updateProfile("Bearer " + token, namePart, photoPart)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            org.json.JSONObject jsonObject = new org.json.JSONObject(responseBody);
                            String newName = jsonObject.optString("name", name);
                            String newPhotoUrl = jsonObject.optString("photoUrl", "");
                            
                            // Update SharedPreferences
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("user_name", newName);
                            editor.putString("user_photo_url", newPhotoUrl);
                            editor.apply();
                            
                            Toast.makeText(EditProfileActivity.this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Erro ao processar resposta", Toast.LENGTH_SHORT).show();
                            btnSave.setEnabled(true);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                }
            });
    }

    private File getFileFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = new File(getCacheDir(), "temp_image.jpg");
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
        return tempFile;
    }
}
