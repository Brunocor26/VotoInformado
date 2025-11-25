package pt.ubi.pdm.votoinformado.classes;

public class Entrevista extends ImportantDate {
    private String idCandidato;

    public Entrevista() { }

    public Entrevista(String title, String date, String time, String category, String idCandidato) {
        super(title, date, time, category);
        this.idCandidato = idCandidato;
    }

    public String getIdCandidato() { return idCandidato; }
}