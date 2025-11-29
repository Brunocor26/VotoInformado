package pt.ubi.pdm.votoinformado.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import org.json.JSONObject;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText emailEditText, passwordEditText;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check for existing session (e.g., SharedPreferences) - To be implemented
        // For now, we always show login screen

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.edittext_email);
        passwordEditText = findViewById(R.id.edittext_password);
        Button loginButton = findViewById(R.id.button_login);
        TextView registerTextView = findViewById(R.id.textview_register);
        TextView forgotPasswordTextView = findViewById(R.id.textview_forgot_password);

        loginButton.setOnClickListener(v -> loginUser());

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidade de recuperar senha a ser implementada.", Toast.LENGTH_SHORT).show();
        });

        credentialManager = CredentialManager.create(this);

        findViewById(R.id.sign_in_button).setOnClickListener(v -> signInWithGoogle());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha email e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        ApiClient.getInstance().getApiService().login(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String token = jsonObject.getString("token");
                        String userId = jsonObject.optString("_id", "");
                        String name = jsonObject.optString("name", "Utilizador");
                        String email = jsonObject.optString("email", "");
                        String photoUrl = jsonObject.optString("photoUrl", "");
                        
                        // Save to SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("auth_token", token);
                        editor.putString("user_id", userId);
                        editor.putString("user_name", name);
                        editor.putString("user_email", email);
                        editor.putString("user_photo_url", photoUrl);
                        editor.apply();
                        
                        Toast.makeText(LoginActivity.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing login response", e);
                        Toast.makeText(LoginActivity.this, "Erro ao processar resposta", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Falha na autenticação", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signInWithGoogle() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        try {
                            GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                            backendAuthWithGoogle(credential.getIdToken());
                        } catch (Exception e) {
                            Log.e(TAG, "Error creating GoogleIdTokenCredential", e);
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Erro ao processar credenciais.", Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        runOnUiThread(() -> {
                            if (e instanceof NoCredentialException) {
                                Log.w(TAG, "No Google accounts found on the device.", e);
                                Toast.makeText(LoginActivity.this, "Nenhuma conta Google encontrada.", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "GetCredentialException", e);
                                Toast.makeText(LoginActivity.this, "Falha no login com Google.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        );
    }

    private void backendAuthWithGoogle(String idToken) {
        Map<String, String> body = new HashMap<>();
        body.put("idToken", idToken);

        ApiClient.getInstance().getApiService().googleLogin(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Erro na autenticação com Google no backend", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
