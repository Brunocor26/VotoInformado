package pt.ubi.pdm.votoinformado.classes;

public class Question {
    private String text;
    private double economicWeight; // -1.0 (Left) to 1.0 (Right)
    private double socialWeight;   // -1.0 (Libertarian) to 1.0 (Authoritarian)

    public Question(String text, double economicWeight, double socialWeight) {
        this.text = text;
        this.economicWeight = economicWeight;
        this.socialWeight = socialWeight;
    }

    public String getText() {
        return text;
    }

    public double getEconomicWeight() {
        return economicWeight;
    }

    public double getSocialWeight() {
        return socialWeight;
    }
}
