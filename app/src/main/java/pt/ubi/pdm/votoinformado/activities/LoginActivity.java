package pt.ubi.pdm.votoinformado.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

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
}
