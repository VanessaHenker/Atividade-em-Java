
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HtmlAnalyzer <url>");
            return;
        }

        String urlString = args[0];
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            if (status != 200) {
                System.out.println("URL connection error");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            Stack<String> tagStack = new Stack<>();
            String deepestText = null;
            int maxDepth = 0;
            boolean malformed = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.matches("<[a-zA-Z]+>")) {
                    String tag = line.replaceAll("[<>]", "");
                    tagStack.push(tag);
                } else if (line.matches("</[a-zA-Z]+>")) {
                    String tag = line.replaceAll("[</>]", "");
                    if (tagStack.isEmpty() || !tagStack.peek().equals(tag)) {
                        malformed = true;
                        break;
                    }
                    tagStack.pop();
                } else {
                    int depth = tagStack.size();
                    if (depth > maxDepth) {
                        maxDepth = depth;
                        deepestText = line;
                    }
                }
            }
            reader.close();

            if (malformed || !tagStack.isEmpty()) {
                System.out.println("malformed HTML");
            } else if (deepestText != null) {
                System.out.println(deepestText);
            } else {
                System.out.println("No text found");
            }
        } catch (Exception e) {
            System.out.println("URL connection error");
        }
    }
}
