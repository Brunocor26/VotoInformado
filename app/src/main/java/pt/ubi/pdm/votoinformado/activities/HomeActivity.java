package pt.ubi.pdm.votoinformado.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

public class HomeActivity extends AppCompatActivity {

    // Launcher para pedir a permissão de notificação
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Verificar se utilizador está autenticado
        SharedPreferences prefs = null;
        try {
            MasterKey masterKey = new MasterKey.Builder(this) //criar chave para aceder aos dados
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)       //tipo de encriptação
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    this,
                    "user_session_secure", //nome do ficheiro
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            // se nao conseguirmos, volta para o login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        //do ficheiro, tentar tirar authtoken
        String token = prefs.getString("auth_token", null);

        //se ele nao existe/está vazio, login
        if (token == null || token.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //correu tudo bem, funciona normal
        setContentView(R.layout.activity_home);

        //usamos o xml do bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        //caso de cada opcao que pode escolher
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_candidatos) {
                fragment = new CandidatosFragment();
            } else if (itemId == R.id.nav_eventos) {
                fragment = new pt.ubi.pdm.votoinformado.fragments.ImportantDatesHostFragment();
            } else if (itemId == R.id.nav_sondagens) {
                fragment = new SondagensFragment();
            } else if (itemId == R.id.nav_noticias) {
                fragment = new NoticiasFragment();
            } else if (itemId == R.id.nav_peticoes) {
                fragment = new PeticoesFragment();
            }

            return loadFragment(fragment);
        });

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Pede permissão de notificações
        askNotificationPermission();

        //funcao que vai tratar das notificacoes mesmo sem a necessidade de ter a aplicacao aberta
        scheduleDateSync();

        //adiciona botao de desenvolvedor, para poder adicionar um novo candidato, data, sondagem
        com.google.android.material.floatingactionbutton.FloatingActionButton fabDev = findViewById(R.id.fabDev);

        //nas prefs, tentar tirar o role
        String role = prefs.getString("user_role", "user");
        android.util.Log.d("HomeActivity", "User Role from Prefs: " + role);

        //se é admin, botao visivel
        if (role.equals("admin")) {
            fabDev.setVisibility(android.view.View.VISIBLE);
            fabDev.setOnClickListener(v -> {
                startActivity(new Intent(this, DevOptionsActivity.class));
            });
        } else {
            //se nao é admin, nao aparece o botao
            fabDev.setVisibility(android.view.View.GONE);
        }
    }

    private void askNotificationPermission() {
        // Apenas necessário para API 33+ (Android 13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Pede diretamente a permissão
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
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

    //funcao que carrega o fragmento
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
