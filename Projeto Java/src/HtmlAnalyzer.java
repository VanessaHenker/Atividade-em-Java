import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        // Verificação de argumento de linha de comando
        if (args.length != 1) {
            System.out.println("Usage: java HtmlAnalyzer <url>");
            return;
        }

        String urlString = args[0];
        try {
            // Estabelecendo conexão com a URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            if (status != 200) {
                System.out.println("URL connection error");
                return;
            }

            // Usando try-with-resources para ler o conteúdo HTML
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                Stack<String> tagStack = new Stack<>();
                String deepestText = null;
                int maxDepth = 0;
                boolean malformed = false;

                // Analisando linha por linha do HTML
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue; // Ignora linhas vazias
                    }

                    // Verifica se é uma tag de abertura
                    if (line.matches("<[a-zA-Z]+>")) {
                        String tag = line.replaceAll("[<>]", "");
                        tagStack.push(tag);
                    }
                    // Verifica se é uma tag de fechamento
                    else if (line.matches("</[a-zA-Z]+>")) {
                        String tag = line.replaceAll("[</>]", "");
                        if (tagStack.isEmpty() || !tagStack.peek().equals(tag)) {
                            malformed = true;
                            break;
                        }
                        tagStack.pop();
                    }
                    // Caso seja um trecho de texto
                    else {
                        int depth = tagStack.size();
                        if (depth > maxDepth) {
                            maxDepth = depth;
                            deepestText = line;
                        }
                    }
                }

                // Verificações finais
                if (malformed || !tagStack.isEmpty()) {
                    System.out.println("malformed HTML");
                } else if (deepestText != null) {
                    System.out.println(deepestText);
                } else {
                    System.out.println("No text found");
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("URL connection error");
        } catch (IOException e) {
            System.out.println("URL connection error");
        }
    }
}
