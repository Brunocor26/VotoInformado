package pt.ubi.pdm.votoinformado.parsing;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.Sondagem;

public class JsonUtils {

    private static final String TAG = "JsonUtils";

    // Adaptador para LocalDate que suporta valores nulos
    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(FORMATTER.format(value));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDate.parse(in.nextString(), FORMATTER);
        }
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public static List<Candidato> loadCandidatos(Context context) {
        if (context == null) {
            Log.e(TAG, "Contexto é nulo. Não é possível carregar candidatos.");
            return new ArrayList<>();
        }

        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.candidatos);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type listType = new TypeToken<ArrayList<Candidato>>() {}.getType();
            
            List<Candidato> candidatos = createGson().fromJson(reader, listType);

            if (candidatos != null) {
                for (Candidato c : candidatos) {
                    c.cacheFotoId(context);
                }
                return candidatos;
            }
            return new ArrayList<>();

        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar candidatos.json", e);
            return new ArrayList<>();
        }
    }

    public static List<Sondagem> loadSondagens(Context context) {
        if (context == null) {
            Log.e(TAG, "Contexto é nulo. Não é possível carregar sondagens.");
            return new ArrayList<>();
        }
        
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.sondagens);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type listType = new TypeToken<ArrayList<Sondagem>>() {}.getType();
            
            List<Sondagem> sondagens = createGson().fromJson(reader, listType);
            return sondagens != null ? sondagens : new ArrayList<>();

        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar sondagens.json", e);
            return new ArrayList<>();
        }
    }
}
