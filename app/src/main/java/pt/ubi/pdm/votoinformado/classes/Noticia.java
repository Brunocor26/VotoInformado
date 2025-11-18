package pt.ubi.pdm.votoinformado.classes;

public class Noticia {
    private String titulo;
    private String link;
    private String data;
    private String imagem;

    public Noticia(String titulo, String link, String data, String imagem) {
        this.titulo = titulo;
        this.link = link;
        this.data = data;
        this.imagem = imagem;
    }

    public String getTitulo() { return titulo; }
    public String getLink() { return link; }
    public String getData() { return data; }
    public String getImagem() { return imagem; }
}
