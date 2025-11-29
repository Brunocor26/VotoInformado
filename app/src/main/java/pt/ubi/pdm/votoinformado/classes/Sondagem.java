package pt.ubi.pdm.votoinformado.classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sondagem implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("entidade")
    private String entidade;

    @SerializedName("tam_amostra")
    private Integer tamAmostra;

    @SerializedName("data_inicio_recolha")
    private String dataInicioRecolha;

    @SerializedName("data_fim_recolha")
    private String dataFimRecolha;

    @SerializedName("metodologia")
    private String metodologia;

    @SerializedName("universo")
    private String universo;

    @SerializedName("margem_erro")
    private Double margemErro;

    @SerializedName("nivel_confianca")
    private Double nivelConfianca;

    @SerializedName("resultados")
    private Map<String, Double> resultados;

    @SerializedName("resultado_dist_indecisos")
    private Map<String, Double> resultadoDistIndecisos;

    @SerializedName("resultadoPrincipal")
    private ResultadoPrincipal resultadoPrincipal;

    public Sondagem() {
        // Construtor vazio necessário para o Firestore
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(String entidade) {
        this.entidade = entidade;
    }

    public Integer getTamAmostra() {
        return tamAmostra;
    }

    public void setTamAmostra(Integer tamAmostra) {
        this.tamAmostra = tamAmostra;
    }

    public String getDataInicioRecolha() {
        return dataInicioRecolha;
    }

    public void setDataInicioRecolha(String dataInicioRecolha) {
        this.dataInicioRecolha = dataInicioRecolha;
    }

    public String getDataFimRecolha() {
        return dataFimRecolha;
    }

    public void setDataFimRecolha(String dataFimRecolha) {
        this.dataFimRecolha = dataFimRecolha;
    }

    public String getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(String metodologia) {
        this.metodologia = metodologia;
    }

    public String getUniverso() {
        return universo;
    }

    public void setUniverso(String universo) {
        this.universo = universo;
    }

    public Double getMargemErro() {
        return margemErro;
    }

    public void setMargemErro(Double margemErro) {
        this.margemErro = margemErro;
    }

    public Double getNivelConfianca() {
        return nivelConfianca;
    }

    public void setNivelConfianca(Double nivelConfianca) {
        this.nivelConfianca = nivelConfianca;
    }

    public Map<String, Double> getResultados() {
        return resultados;
    }

    public void setResultados(Map<String, Double> resultados) {
        this.resultados = resultados;
    }

    public Map<String, Double> getResultadoDistIndecisos() {
        return resultadoDistIndecisos;
    }

    public void setResultadoDistIndecisos(Map<String, Double> resultadoDistIndecisos) {
        this.resultadoDistIndecisos = resultadoDistIndecisos;
    }

    public ResultadoPrincipal getResultadoPrincipal() {
        return resultadoPrincipal;
    }

    public void setResultadoPrincipal(ResultadoPrincipal resultadoPrincipal) {
        this.resultadoPrincipal = resultadoPrincipal;
    }

    // --- Business Logic ---

    public static class ResultadoPrincipal implements Serializable {
        public String idCandidato;
        public Double percentagem;

        public ResultadoPrincipal() {
            // No-arg constructor for Firestore
        }

        public ResultadoPrincipal(String idCandidato, Double percentagem) {
            this.idCandidato = idCandidato;
            this.percentagem = percentagem;
        }
    }

    // Calculate the leader from resultados if not available
    public ResultadoPrincipal getCalculatedResultadoPrincipal() {
        // First check if we have a stored resultadoPrincipal
        if (this.resultadoPrincipal != null) {
            return this.resultadoPrincipal;
        }

        // Otherwise calculate from resultados
        if (resultados == null || resultados.isEmpty()) {
            return new ResultadoPrincipal("N/A", 0.0);
        }

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
