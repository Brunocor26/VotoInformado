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

    @SerializedName("idCandidato")
    private String idCandidato;
    @SerializedName("idCandidato1")
    private String idCandidato1;
    @SerializedName("idCandidato2")
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

    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setCategory(String category) { this.category = category; }

    private Location location;

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public static class Location {
        private double latitude;
        private double longitude;
        private String address;

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public void setIdCandidato1(String idCandidato1) { this.idCandidato1 = idCandidato1; }
    public void setIdCandidato2(String idCandidato2) { this.idCandidato2 = idCandidato2; }
    public void setIdCandidato(String idCandidato) { this.idCandidato = idCandidato; }
}
