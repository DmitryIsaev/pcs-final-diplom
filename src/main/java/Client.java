import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String host = "127.0.0.1";
    private static final int port = 8989;

    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            out.println("Бизнес");
            String gson = in.readLine();
            String[] string = gson.split("(?<=(\\{|},|\",|\"page\":[0-9][0-9],|\"count\":[0-9][0-9]" +
                    "|\"page\":[0-9],|\"count\":[0-9]))");
            for (String newLine : string) {
                System.out.println(newLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
