package pt.ubi.pdm.votoinformado.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import de.hdodenhof.circleimageview.CircleImageView;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.utils.FirebaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextName, editTextEmail, editTextPassword;
    private MaterialButton buttonRegister;
    private CircleImageView profileImageView;
    private TextView loginRedirectText;

    private FirebaseHelper firebaseHelper;
    private Uri selectedImageUri;

    // ActivityResultLauncher para a seleção de imagem
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

        firebaseHelper = new FirebaseHelper();

        // Inicializa os componentes do layout
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        profileImageView = findViewById(R.id.image_profile);
        loginRedirectText = findViewById(R.id.login_redirect_text);

        // Define os listeners de clique
        setupClickListeners();
    }

    private void setupClickListeners() {
        profileImageView.setOnClickListener(v -> openGallery());
        buttonRegister.setOnClickListener(v -> registerUser());
        loginRedirectText.setOnClickListener(v -> finish()); // Volta para o Login
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

        if (selectedImageUri == null) {
            Toast.makeText(this, "Por favor, selecione uma foto de perfil", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostra um indicador de progresso (a implementar)
        // ProgressBar progressBar = findViewById(R.id.progressBar);
        // progressBar.setVisibility(View.VISIBLE);

        firebaseHelper.registerUser(name, email, password, selectedImageUri, new FirebaseHelper.RegistrationCallback() {
            @Override
            public void onSuccess() {
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Utilizador registado com sucesso!", Toast.LENGTH_SHORT).show();
                
                // Redireciona para a tela de login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Erro ao registar: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
