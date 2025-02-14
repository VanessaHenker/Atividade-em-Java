import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        // Verifica se a URL foi passada como argumento
        if (args.length != 1) {
            System.out.println("Usage: java HtmlAnalyzer <URL>");
            return;
        }

        String urlString = args[0];
        
        try {
            // Cria a conexão com a URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Verifica se a conexão foi bem-sucedida
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("URL connection error. Response code: " + responseCode);
                return;
            }

            // Lê o conteúdo HTML e armazena em StringBuilder
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
            }
            reader.close();
            
            // Usa Jsoup para parsear o HTML
            Document document = Jsoup.parse(htmlContent.toString());

            // Encontra e exibe o texto nas tags mais profundas
            System.out.println("Texto no nível mais profundo:");
            Elements allElements = document.getAllElements();
            for (Element element : allElements) {
                // Verifica se o elemento é uma "folha" (não tem filhos com texto próprio)
                if (element.children().isEmpty() && !element.ownText().isEmpty()) {
                    System.out.println(element.ownText());
                }
            }

        } catch (IOException e) {
            // Trata erros de conexão
            System.out.println("URL connection error: " + e.getMessage());
        }
    }
}
