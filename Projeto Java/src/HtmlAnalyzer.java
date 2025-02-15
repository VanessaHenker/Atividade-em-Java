import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        // URL fixa para análise
        String urlString = "https://axur-internship-2025.s3.amazonaws.com/";

        try {
            // Estabelecendo conexão com a URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Adicionando o cabeçalho "User-Agent" para simular um navegador
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Referer", "https://axur-internship-2025.s3.amazonaws.com/"); // Referer
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"); // Accept

            // Verificando o status da conexão
            int status = connection.getResponseCode();
            if (status != 200) {
                System.out.println("Erro na conexão, código de status: " + status);
                return;
            }

            // Usando try-with-resources para ler o conteúdo HTML
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                Stack<String> tagStack = new Stack<>();
                String deepestText = null;
                int maxDepth = 0;
                boolean malformed = false;

                // Criando um BufferedWriter para salvar o HTML em um arquivo
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("saida.html"))) {
                    // Analisando linha por linha do HTML
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();

                        // Se a linha estiver vazia, ignora
                        if (line.isEmpty()) {
                            continue;
                        }

                        // Salvando o conteúdo no arquivo "saida.html"
                        writer.write(line);
                        writer.newLine();

                        // Regex para identificar tags HTML de forma mais robusta
                        Pattern tagPattern = Pattern.compile("<([a-zA-Z][a-zA-Z0-9]*)[^>]*>|</([a-zA-Z][a-zA-Z0-9]*)>");
                        Matcher matcher = tagPattern.matcher(line);

                        // Verifica se a linha contém uma tag
                        if (matcher.find()) {
                            // Verifica se é uma tag de abertura
                            if (matcher.group(1) != null) {
                                tagStack.push(matcher.group(1));
                            } 
                            // Verifica se é uma tag de fechamento
                            else if (matcher.group(2) != null) {
                                String tag = matcher.group(2);
                                if (tagStack.isEmpty() || !tagStack.peek().equals(tag)) {
                                    malformed = true;
                                    break;
                                }
                                tagStack.pop();
                            }
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
                } catch (IOException e) {
                    System.out.println("Erro ao salvar o conteúdo HTML em um arquivo: " + e.getMessage());
                    return;
                }

                // Verificações finais
                if (malformed || !tagStack.isEmpty()) {
                    System.out.println("HTML malformado.");
                } else if (deepestText != null) {
                    System.out.println("Texto mais profundo: " + deepestText);
                } else {
                    System.out.println("Nenhum texto encontrado.");
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("URL inválida.");
        } catch (IOException e) {
            System.out.println("Erro na conexão ou leitura da URL.");
        }
    }
}
