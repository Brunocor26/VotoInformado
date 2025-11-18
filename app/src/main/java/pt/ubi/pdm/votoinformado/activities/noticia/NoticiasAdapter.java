package pt.ubi.pdm.votoinformado.activities.noticia;

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

import java.net.URL;
import java.util.List;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.VH> {

    private List<Noticia> lista;

    public NoticiasAdapter(List<Noticia> lista) {
        this.lista = lista;
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
        Noticia n = lista.get(position);

        holder.titulo.setText(n.getTitulo());
        holder.data.setText(n.getData());

        String urlImg = n.getImagem();
        if (urlImg != null) urlImg = urlImg.replace("&amp;", "&");

        // Coloca placeholder enquanto carrega
        holder.img.setImageResource(R.drawable.placeholder);

        String finalUrl = urlImg;

        new Thread(() -> {
            try {
                if (finalUrl != null && !finalUrl.isEmpty()) {
                    Bitmap bmp = BitmapFactory.decodeStream(new URL(finalUrl).openStream());

                    holder.img.post(() -> holder.img.setImageBitmap(bmp));
                } else {
                    holder.img.post(() -> holder.img.setImageResource(R.drawable.erro));
                }
            } catch (Exception e) {
                holder.img.post(() -> holder.img.setImageResource(R.drawable.erro));
            }
        }).start();

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
        return lista.size();
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
