package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;
import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.prefs.DarkModePrefManager;

public class SettingsActivity extends AppCompatActivity {
    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial notificationsSwitch;
    private LinearLayout profileSection;
    private LinearLayout logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_settings);
        
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        profileSection = findViewById(R.id.profile_section);
        logoutButton = findViewById(R.id.logout_button);
        
        setDarkModeSwitch();
        setClickListeners();
    }

    private void setDarkModeSwitch() {
        darkModeSwitch.setChecked(new DarkModePrefManager(this).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            new DarkModePrefManager(this).setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        });
    }
    
    private void setClickListeners() {
        profileSection.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "ativadas" : "desativadas";
            Toast.makeText(this, "Notificações " + status, Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            // Clear SharedPreferences
            android.content.SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            prefs.edit().clear().apply();
            
            Toast.makeText(this, "Sessão terminada", Toast.LENGTH_SHORT).show();
            
            // Redirect to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
