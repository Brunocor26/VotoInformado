package pt.ubi.pdm.votoinformado.parsing;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Candidato;
import pt.ubi.pdm.votoinformado.classes.ImportantDate;

public class JsonUtilsDatas {

    private static final String TAG = "JsonUtilsDatas";

    public static List<ImportantDate> carregarDatas(Context context, Map<String, Candidato> mapaCandidatos) {

        List<ImportantDate> lista = new ArrayList<>();

        try {
            InputStream is = context.getResources().openRawResource(R.raw.dates);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();
            String linha;

            while ((linha = br.readLine()) != null) sb.append(linha);

            JSONObject root = new JSONObject(sb.toString());
            JSONArray arr = root.getJSONArray("events");

            for (int i = 0; i < arr.length(); i++) {

                JSONObject obj = arr.getJSONObject(i);

                String cat = obj.getString("category");
                String title = obj.getString("title");
                String date = obj.getString("date");
                String time = obj.getString("time");

                Candidato candidato = null;
                Candidato c1 = null;
                Candidato c2 = null;

                if (cat.equals("Entrevista") && obj.has("candidate_id")) {
                    candidato = mapaCandidatos.get(obj.getString("candidate_id"));
                }

                if (cat.equals("Debate") && obj.has("candidate_ids")) {
                    JSONArray ids = obj.getJSONArray("candidate_ids");
                    c1 = mapaCandidatos.get(ids.getString(0));
                    c2 = mapaCandidatos.get(ids.getString(1));
                }

                lista.add(new ImportantDate(
                        title,
                        date,
                        time,
                        cat,
                        candidato,
                        c1,
                        c2
                ));
            }

            Log.d(TAG, "Datas importantes carregadas: " + lista.size());

        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar datas importantes", e);
            e.printStackTrace();
        }

        return lista;
    }
}
