import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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

    String urlString = args[0];  // URL fornecida pelo usuário
    System.out.println("URL fornecida: " + urlString);  // Imprime a URL para conferirmos

    try {
      // Estabelecendo conexão com a URL
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      // Obtendo o código de status da resposta
      int status = connection.getResponseCode();
      if (status != 200) {
        System.out.println("Erro na conexão com a URL. Código de status: " + status);
        return;
      }

      // Usando try-with-resources para ler o conteúdo HTML
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String line;
        Stack<String> tagStack = new Stack<>();  // Pilha para armazenar as tags abertas
        String deepestText = null;  // Armazenar o texto mais profundo
        int maxDepth = 0;  // Profundidade máxima encontrada
        boolean malformed = false;  // Indicador de HTML malformado

        // Criando um BufferedWriter para salvar o HTML em um arquivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("saida.html"))) {
          // Lê o conteúdo HTML linha por linha
          while ((line = reader.readLine()) != null) {
            // Salva no arquivo "saida.html"
            writer.write(line);
            writer.newLine();
            
            // Processa o conteúdo (no seu caso, você está procurando texto dentro das tags)
            line = line.trim();
            if (line.isEmpty()) {
              continue; // Ignora linhas vazias
            }

            // Verifica se é uma tag de abertura (ex: <tag>)
            if (line.matches("<[a-zA-Z]+>")) {
              String tag = line.replaceAll("[<>]", "");
              tagStack.push(tag);  // Empilha a tag de abertura
            }
            // Verifica se é uma tag de fechamento (ex: </tag>)
            else if (line.matches("</[a-zA-Z]+>")) {
              String tag = line.replaceAll("[</>]", "");
              if (tagStack.isEmpty() || !tagStack.peek().equals(tag)) {
                malformed = true;  // HTML malformado (tags de fechamento não correspondem)
                break;
              }
              tagStack.pop();  // Remove a tag de abertura correspondente
            }
            // Caso seja um trecho de texto dentro das tags
            else {
              int depth = tagStack.size();  // Profundidade atual das tags
              if (depth > maxDepth) {
                maxDepth = depth;  // Atualiza a profundidade máxima
                deepestText = line;  // Atualiza o texto mais profundo
              }
            }
          }

          // Verificações finais após analisar todo o HTML
          if (malformed || !tagStack.isEmpty()) {
            System.out.println("HTML malformado. Algumas tags não foram fechadas corretamente.");
          } else if (deepestText != null) {
            System.out.println("Texto mais profundo encontrado: ");
            System.out.println(deepestText);
          } else {
            System.out.println("Nenhum texto encontrado dentro das tags.");
          }

          System.out.println("Conteúdo HTML salvo em 'saida.html'");

        } catch (IOException e) {
          System.out.println("Erro ao salvar o conteúdo HTML em um arquivo: " + e.getMessage());
        }
      }
    } catch (MalformedURLException e) {
      System.out.println("Erro: URL malformada.");
    } catch (IOException e) {
      System.out.println("Erro ao tentar se conectar à URL ou ler o conteúdo.");
    }
  }
}
