package pt.ubi.pdm.votoinformado.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.notificacoes.SyncDatesWorker;
import pt.ubi.pdm.votoinformado.fragments.CandidatosFragment;
import pt.ubi.pdm.votoinformado.fragments.ChooseEventTypeFragment;
import pt.ubi.pdm.votoinformado.fragments.HomeFragment;
import pt.ubi.pdm.votoinformado.fragments.NoticiasFragment;
import pt.ubi.pdm.votoinformado.fragments.PeticoesFragment;
import pt.ubi.pdm.votoinformado.fragments.SondagensFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in via shared preferences
        android.content.SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null || token.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this);

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        //funcao que vai tratar das notificacoes mesmo sem a necessidade de ter a aplicacao aberta
        scheduleDateSync();
    }

    private void scheduleDateSync() {
        //criamos um pedido de trabalho periódico
        PeriodicWorkRequest syncDatesRequest =
                new PeriodicWorkRequest.Builder(SyncDatesWorker.class, 24, TimeUnit.HOURS)
                        .build();               //executar a logica do SyncDatesWorker.class a cada 24h

        //entregamos o pedido de trabalho ao WorkManager
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "syncDatesWork",
                ExistingPeriodicWorkPolicy.KEEP,
                syncDatesRequest);
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
        } else if (itemId == R.id.nav_peticoes) {
            fragment = new PeticoesFragment();
        }

        return loadFragment(fragment);
    }
}
