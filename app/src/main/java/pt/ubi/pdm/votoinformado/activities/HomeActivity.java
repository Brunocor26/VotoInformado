package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.notificacoes.NotificationScheduler;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;
import pt.ubi.pdm.votoinformado.fragments.CandidatosFragment;
import pt.ubi.pdm.votoinformado.fragments.ChooseEventTypeFragment;
import pt.ubi.pdm.votoinformado.fragments.HomeFragment;
import pt.ubi.pdm.votoinformado.fragments.NoticiasFragment;
import pt.ubi.pdm.votoinformado.fragments.SondagensFragment;
import pt.ubi.pdm.votoinformado.utils.DatabaseHelper;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this);

        //permissao para notificacoes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001 // request code
                );
            }
        }

        // CARREGAMOS OS EVENTOS E AGENDAMOS NOTIFICAÇÕES ---
        DatabaseHelper.getImportantDates(new DatabaseHelper.DataCallback<List<ImportantDate>>() {
            @Override
            public void onCallback(List<ImportantDate> dates) {
                for (ImportantDate event : dates) {
                    //agendamos a notificacao
                    NotificationScheduler.scheduleEventNotifications(getApplicationContext(), event);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("Firebase", "Erro ao carregar eventos: " + error);
            }
        });
        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.nav_candidatos) {
            fragment = new CandidatosFragment();
        } else if (itemId == R.id.nav_eventos) {
            fragment = new ChooseEventTypeFragment();
        } else if (itemId == R.id.nav_sondagens) {
            fragment = new SondagensFragment();
        } else if (itemId == R.id.nav_noticias) {
            fragment = new NoticiasFragment();
        }

        return loadFragment(fragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Notifications", "Permissão concedida!");
            } else {
                Log.d("Notifications", "Permissão negada!");
            }
        }
    }

}
