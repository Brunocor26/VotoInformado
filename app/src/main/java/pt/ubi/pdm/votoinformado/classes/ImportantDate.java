package pt.ubi.pdm.votoinformado.classes;

import java.time.LocalDate;

public class ImportantDate {

    private String title;
    private String date;
    private String time;
    private String category;

    private Candidato candidato;
    private Candidato candidato1;
    private Candidato candidato2;

    // NOVO: data convertida
    private LocalDate localDate;

    public ImportantDate(String title, String date, String time, String category,
                         Candidato candidato, Candidato candidato1, Candidato candidato2) {

        this.title = title;
        this.date = date;
        this.time = time;
        this.category = category;

        this.candidato = candidato;
        this.candidato1 = candidato1;
        this.candidato2 = candidato2;

        // Conversão automática YYYY-MM-DD → LocalDate
        this.localDate = LocalDate.parse(date);
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getCategory() { return category; }
    public Candidato getCandidato() { return candidato; }
    public Candidato getCandidato1() { return candidato1; }
    public Candidato getCandidato2() { return candidato2; }

    public LocalDate getLocalDate() { return localDate; }
}
