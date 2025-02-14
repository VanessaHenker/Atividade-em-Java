import java.io.IOException;
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
      // Usa Jsoup para pegar o HTML diretamente da URL
      Document document = Jsoup.connect(urlString).get();

      // Encontra e exibe o texto nas tags mais profundas
      System.out.println("Texto no nível mais profundo:");
      Elements allElements = document.getAllElements();
      for (Element element : allElements) {
        String text = element.ownText().trim();
        if (element.children().isEmpty() && !text.isEmpty()) {
          System.out.println(text);
        }
      }

    } catch (IOException e) {
      // Trata erros de conexão
      System.out.println("URL connection error: " + e.getMessage());
    }
  }
}
