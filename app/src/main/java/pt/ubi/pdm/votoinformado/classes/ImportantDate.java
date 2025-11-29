package pt.ubi.pdm.votoinformado.classes;

import java.time.LocalDate;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ImportantDate {

    @SerializedName("_id")
    private String id;
    private String title;
    private String date;
    private String time;
    private String category;

    private String idCandidato;
    private String idCandidato1;
    private String idCandidato2;

    private transient LocalDate localDate;

    public ImportantDate() {
        // Required for Firebase/Gson
    }

    public ImportantDate(String title, String date, String time, String category,
                         String idCandidato, String idCandidato1, String idCandidato2) {

        this.title = title;
        this.date = date;
        this.time = time;
        this.category = category;

        this.idCandidato = idCandidato;
        this.idCandidato1 = idCandidato1;
        this.idCandidato2 = idCandidato2;

        if (date != null) {
            this.localDate = LocalDate.parse(date);
        }
    }

    public ImportantDate(String title, String date, String time, String category) {
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getCategory() { return category; }
    public Candidato getCandidato() { return null; } // Needs to be updated to fetch from DB
    public Candidato getCandidato1() { return null; } // Needs to be updated to fetch from DB
    public Candidato getCandidato2() { return null; } // Needs to be updated to fetch from DB

    public String getIdCandidato() { return idCandidato; }
    public String getIdCandidato1() { return idCandidato1; }
    public String getIdCandidato2() { return idCandidato2; }

    private List<Vote> votes;

    public List<Vote> getVotes() {
        return votes;
    }

    public static class Vote {
        private String userId;
        private String candidateId;

        public String getUserId() { return userId; }
        public String getCandidateId() { return candidateId; }
    }

    public LocalDate getLocalDate() {
        if (localDate == null && date != null) {
            localDate = LocalDate.parse(date);
        }
        return localDate;
    }
}
