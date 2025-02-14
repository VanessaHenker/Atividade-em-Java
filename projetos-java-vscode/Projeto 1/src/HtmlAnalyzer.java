import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        System.out.println("URL connection error");
        return;
      }

      // Lê o conteúdo HTML linha por linha
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        // Por enquanto, apenas exibe o HTML no console
        System.out.println(line);
      }
      reader.close();

    } catch (IOException e) {
      // Trata erros de conexão
      System.out.println("URL connection error");
    }
  }
}
