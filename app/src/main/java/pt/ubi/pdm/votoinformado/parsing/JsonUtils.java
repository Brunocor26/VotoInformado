package pt.ubi.pdm.votoinformado.parsing; // Verifique se o seu package name está correto

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import pt.ubi.pdm.votoinformado.R;
import pt.ubi.pdm.votoinformado.classes.Candidato;

public class JsonUtils {

    private static final String TAG = "JsonUtils";

    /**
     * Lê o ficheiro candidatos.json de res/raw e converte-o numa lista de objetos Candidato.
     * Este método também trata de chamar a função de cache para os IDs das fotos.
     *
     * @param context Contexto da aplicação (ex: a sua Activity) para aceder aos Recursos.
     * @return Uma List<Candidato> preenchida, ou uma lista vazia se falhar.
     */
    public static List<Candidato> carregarCandidatos(Context context) {

        // 1. Validação de segurança
        if (context == null) {
            Log.e(TAG, "Contexto é nulo. Não é possível carregar candidatos.");
            return new ArrayList<>();
        }

        // 2. Inicializar o Gson
        Gson gson = new Gson();

        // 3. Definir o tipo de dados que esperamos
        // O Gson precisa disto para saber que queremos uma Lista<Candidato>
        Type tipoListaCandidatos = new TypeToken<List<Candidato>>() {}.getType();

        // 4. Abrir e ler o ficheiro
        // Usar try-with-resources garante que o InputStream e o Reader são fechados
        // automaticamente, mesmo que ocorra um erro.
        try (
                InputStream inputStream = context.getResources().openRawResource(R.raw.candidatos);
                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        ) {

            // 5. Fazer o Parsing: Converter o JSON na nossa Lista
            List<Candidato> candidatos = gson.fromJson(reader, tipoListaCandidatos);

            // 6. OTIMIZAÇÃO PÓS-LEITURA
            // Se a lista foi carregada, percorrer e fazer o cache dos IDs das fotos.
            if (candidatos != null && !candidatos.isEmpty()) {
                Log.d(TAG, "Parsing do JSON com sucesso. A fazer cache dos " + candidatos.size() + " IDs de fotos...");

                // Contexto da aplicação é mais seguro para operações longas
                Context appContext = context.getApplicationContext();

                for (Candidato candidato : candidatos) {
                    candidato.cacheFotoId(appContext);
                }

                Log.d(TAG, "Cache de fotos concluído.");
                return candidatos;

            } else {
                // Ficheiro JSON estava vazio ou era inválido
                Log.w(TAG, "Ficheiro JSON carregado, mas a lista de candidatos está vazia ou nula.");
                return new ArrayList<>();
            }

        } catch (Exception e) {
            // Se algo falhar (ficheiro não encontrado, JSON mal formatado, etc.)
            Log.e(TAG, "ERRO CRÍTICO ao ler ou fazer parsing do candidatos.json", e);
            e.printStackTrace();
            // Retorna uma lista vazia para a app não "crashar"
            return new ArrayList<>();
        }
    }
}