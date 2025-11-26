package pt.ubi.pdm.votoinformado.classes;

public class CompassCandidate {
    private String name;
    private double x; // Economic (-10 to 10)
    private double y; // Social (-10 to 10)
    private int imageResId;

    public CompassCandidate(String name, double x, double y, int imageResId) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getImageResId() {
        return imageResId;
    }
}
