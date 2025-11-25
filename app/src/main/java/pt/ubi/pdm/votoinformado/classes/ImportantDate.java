package pt.ubi.pdm.votoinformado.classes;

import com.google.firebase.firestore.Exclude; // <--- IMPORTANTE IMPORTAR ISTO
import java.time.LocalDate;

public class ImportantDate {

    protected String title;
    protected String date;
    protected String time;
    protected String category;

    // Adiciona @Exclude aqui para o Firebase não tentar escrever este campo
    @Exclude
    protected transient LocalDate localDate;

    public ImportantDate() { }

    public ImportantDate(String title, String date, String time, String category) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getCategory() { return category; }

    // Adiciona @Exclude aqui também para o Firebase não ler isto como uma propriedade
    @Exclude
    public LocalDate getLocalDate() {
        if (localDate == null && date != null) {
            // O try-catch previne o crash se a data vier mal formatada da BD
            try {
                localDate = LocalDate.parse(date);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return localDate;
    }
}