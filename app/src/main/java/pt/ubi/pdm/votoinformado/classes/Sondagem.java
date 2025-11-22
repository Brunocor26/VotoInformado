package pt.ubi.pdm.votoinformado.classes;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sondagem implements Serializable {

    // --- Campos (com tipos que permitem valores nulos) ---
    private String entidade;
    private Integer tam_amostra;
    private String data_inicio_recolha;
    private String data_fim_recolha;
    private String metodologia;
    private String universo;
    private Double margem_erro;
    private Double nivel_confianca;
    private Map<String, Double> resultados;
    private Map<String, Double> resultado_dist_indecisos;

    public Sondagem(String entidade, Integer tam_amostra, String data_inicio_recolha, String data_fim_recolha, String metodologia, String universo, Double margem_erro, Double nivel_confianca, Map<String, Double> resultados, Map<String, Double> resultado_dist_indecisos) {
        this.entidade = entidade;
        this.tam_amostra = tam_amostra;
        this.data_inicio_recolha = data_inicio_recolha;
        this.data_fim_recolha = data_fim_recolha;
        this.metodologia = metodologia;
        this.universo = universo;
        this.margem_erro = margem_erro;
        this.nivel_confianca = nivel_confianca;
        this.resultados = resultados;
        this.resultado_dist_indecisos = resultado_dist_indecisos;
    }

    // --- Classe interna para guardar o resultado principal ---
    public static class ResultadoPrincipal implements Serializable {
        public final String idCandidato;
        public final Double percentagem;

        public ResultadoPrincipal(String idCandidato, Double percentagem) {
            this.idCandidato = idCandidato;
            this.percentagem = percentagem;
        }
    }

    // --- Métodos Get ---
    public String getEntidade() { return entidade; }
    public Integer getTamAmostra() { return tam_amostra; }
    public String getDataInicioRecolha() { return data_inicio_recolha; }
    public String getDataFimRecolha() { return data_fim_recolha; }
    public String getMetodologia() { return metodologia; }
    public String getUniverso() { return universo; }
    public Double getMargemErro() { return margem_erro; }
    public Double getNivelConfianca() { return nivel_confianca; }
    public Map<String, Double> getResultados() { return resultados; }
    public Map<String, Double> getResultadoDistIndecisos() { return resultado_dist_indecisos; }

    // --- Método para obter o candidato em primeiro lugar ---
    public ResultadoPrincipal getResultadoPrincipal() {
        if (resultados == null || resultados.isEmpty()) {
            return new ResultadoPrincipal("N/A", 0.0);
        }

        // Filtra para encontrar apenas candidatos reais, ignorando outras entradas
        Map<String, Double> resultadosCandidatos = new HashMap<>();
        List<String> aExcluir = Arrays.asList("indeciso", "brancos_nulos", "nao_votaria", "outro");

        for (Map.Entry<String, Double> entry : resultados.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (aExcluir.stream().noneMatch(key::contains)) {
                resultadosCandidatos.put(entry.getKey(), entry.getValue());
            }
        }

        if (resultadosCandidatos.isEmpty()) {
            return new ResultadoPrincipal("N/A", 0.0);
        }

        Map.Entry<String, Double> maxEntry = Collections.max(resultadosCandidatos.entrySet(), Map.Entry.comparingByValue());
        return new ResultadoPrincipal(maxEntry.getKey(), maxEntry.getValue());
    }
}
