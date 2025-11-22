package pt.ubi.pdm.votoinformado.activities.noticia;

import pt.ubi.pdm.votoinformado.classes.Noticia;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NoticiasFetcher {

    // Função auxiliar segura para ler tags sem crash
    private static String read(Element e, String tag) {
        NodeList list = e.getElementsByTagName(tag);
        if (list.getLength() == 0) return "";
        Node n = list.item(0);
        return n != null ? n.getTextContent() : "";
    }

    private static String extrairImagem(String html) {
        if (html == null) return null;
        String url = null;
        // Regex para encontrar src="URL"
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']").matcher(html);
        if (m.find()) {
            url = m.group(1);
        }

        return url;
    }
    public static List<Noticia> buscarNoticias() {
        List<Noticia> noticias = new ArrayList<>();

        try {
            String rssURL = "https://www.rtp.pt/noticias/rss/politica";
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new URL(rssURL).openStream());

            doc.getDocumentElement().normalize();
            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String titulo = read(item, "title");
                String link   = read(item, "link");
                String dataRaw = read(item, "pubDate");
                String data    = formatarData(dataRaw);
                String desc   = read(item, "description");

                String imagem = extrairImagem(desc);
                System.out.println("IMG = " + imagem);

                noticias.add(new Noticia(titulo, link, data, imagem));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return noticias;
    }

    // Função para converter a data feia do RSS numa bonita
    private static String formatarData(String dataOriginal) {
        try {
            // Locale.ENGLISH porque o RSS vem em inglês (Tue, Wed, etc)
            java.text.SimpleDateFormat formatoEntrada = new java.text.SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.ENGLISH);

            // meter o mês em português
            java.text.SimpleDateFormat formatoSaida = new java.text.SimpleDateFormat(
                    "dd 'de' MMM yyyy", new java.util.Locale("pt", "PT"));

            java.util.Date dataObj = formatoEntrada.parse(dataOriginal);
            return formatoSaida.format(dataObj);

        } catch (Exception e) {
            return dataOriginal;
        }
    }
}
