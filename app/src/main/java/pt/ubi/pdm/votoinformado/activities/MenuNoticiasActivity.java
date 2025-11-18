package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.noticia.NoticiasAdapter;
import pt.ubi.pdm.votoinformado.activities.noticia.NoticiasFetcher;
import pt.ubi.pdm.votoinformado.classes.Noticia;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta é a Activity. A sua responsabilidade é gerir o ecrã,
 * ir buscar os dados e "contratar" o Adapter para os mostrar.
 */
public class MenuNoticiasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoticiasAdapter adapter; // <- O nosso adapter externo
    private List<Noticia> listaNoticias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_noticias);

        recyclerView = findViewById(R.id.recyclerViewNoticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaNoticias = new ArrayList<>();
        adapter = new NoticiasAdapter(listaNoticias); // entregamos a lista ao adapter

        recyclerView.setAdapter(adapter);

        buscarNoticias();
    }

    /**
     * Vai buscar as notícias numa Thread de background
     * e atualiza o adapter na Main Thread.
     */
    private void buscarNoticias() {
        new Thread(() -> {
            // Corre em Background
            List<Noticia> noticias = NoticiasFetcher.buscarNoticias();

            runOnUiThread(() -> {
                if (noticias != null) {
                    listaNoticias.clear();
                    listaNoticias.addAll(noticias);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Erro ao carregar notícias. Tenta novamente.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}