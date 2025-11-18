package pt.ubi.pdm.votoinformado.activities;

import android.os.Bundle;
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

        // 1. Encontra o RecyclerView no layout
        recyclerView = findViewById(R.id.recyclerViewNoticias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Prepara a lista e o adapter
        listaNoticias = new ArrayList<>();
        adapter = new NoticiasAdapter(listaNoticias); // <- Entrega a lista ao adapter

        // 3. Liga o RecyclerView ao Adapter
        recyclerView.setAdapter(adapter);

        // 4. Manda buscar as notícias
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
                    adapter.notifyDataSetChanged(); // Avisa o adapter que os dados mudaram
                } else {
                    // Opcional: Mostrar uma mensagem de erro
                }
            });
        }).start();
    }
}