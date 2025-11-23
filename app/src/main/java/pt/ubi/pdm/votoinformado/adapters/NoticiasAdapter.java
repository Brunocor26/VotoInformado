package pt.ubi.pdm.votoinformado.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.activities.NoticiaDetalheActivity;
import pt.ubi.pdm.votoinformado.classes.Noticia;

import java.util.ArrayList;
import java.util.List;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.VH> {

    private List<Noticia> listaNoticias;
    private List<Noticia> listaOriginal;  // lista completa para pesquisa

    public NoticiasAdapter(List<Noticia> lista) {
        this.listaNoticias = new ArrayList<>(lista);
        this.listaOriginal = new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noticia, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Noticia n = listaNoticias.get(position);

        holder.titulo.setText(n.getTitulo());
        holder.data.setText(n.getData());

        String urlImg = n.getImagem();
        if (urlImg != null) urlImg = urlImg.replace("&amp;", "&");

        // Limpa a imagem antiga
        holder.img.setImageDrawable(null);

        // Carregamento de imagem com Picasso
        if (urlImg != null && !urlImg.isEmpty()) {
            com.squareup.picasso.Picasso.get()
                    .load(urlImg)
                    .placeholder(R.drawable.erro) // Imagem de loading ou placeholder se quiseres
                    .error(R.drawable.erro)
                    .into(holder.img);
        } else {
            holder.img.setImageResource(R.drawable.erro);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), NoticiaDetalheActivity.class);
            i.putExtra("titulo", n.getTitulo());
            i.putExtra("data", n.getData());
            i.putExtra("link", n.getLink());
            i.putExtra("imagem", n.getImagem());
            v.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return listaNoticias.size();
    }

    public void filtrar(String texto) {
        listaNoticias.clear();

        if (texto.isEmpty()) {
            listaNoticias.addAll(listaOriginal);
        } else {
            String filtro = texto.toLowerCase();
            for (Noticia n : listaOriginal) {
                if (n.getTitulo() != null && n.getTitulo().toLowerCase().contains(filtro)) {
                    listaNoticias.add(n);
                }
            }
        }

        notifyDataSetChanged();
        //metodo do RecyclerView que informa o adapter de que os dados da lista mudaram,
        //e que ele precisa atualizar toda a RecyclerView.
    }

    // Atualiza a lista original quando carregas novas notícias
    public void setListaOriginal(List<Noticia> noticias) {
        // Atualiza a lista de backup (usada na pesquisa)
        this.listaOriginal = new ArrayList<>(noticias);

        // Atualiza também a lista visível imediatamente
        this.listaNoticias = new ArrayList<>(noticias);

        // Notifica a RecyclerView que os dados mudaram
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView titulo, data;
        ImageView img;

        VH(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            data   = itemView.findViewById(R.id.txtData);
            img    = itemView.findViewById(R.id.imgNoticia);
        }
    }
}
