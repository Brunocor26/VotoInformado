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
    private LinearLayout devOptionsButton;
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
        devOptionsButton = findViewById(R.id.dev_options_button);
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
            Toast.makeText(this, "Editar Perfil Clicado", Toast.LENGTH_SHORT).show();
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "ativadas" : "desativadas";
            Toast.makeText(this, "Notificações " + status, Toast.LENGTH_SHORT).show();
        });

        devOptionsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, DevActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Terminar Sessão Clicado", Toast.LENGTH_SHORT).show();
        });
    }
}
