package pt.ubi.pdm.votoinformado.classes;

public class Debate extends ImportantDate {
    private String idCandidato1;
    private String idCandidato2;

    public Debate() { } // Firebase precisa disto vazio

    public Debate(String title, String date, String time, String category, String id1, String id2) {
        super(title, date, time, category);
        this.idCandidato1 = id1;
        this.idCandidato2 = id2;
    }

    public String getIdCandidato1() { return idCandidato1; }
    public String getIdCandidato2() { return idCandidato2; }
}