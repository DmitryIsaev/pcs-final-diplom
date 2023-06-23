import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        System.out.println("Запуск сервера");

        try (ServerSocket serverSocket = new ServerSocket(8989);) {
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    System.out.println("Новое подключение");
                    String searchWord = in.readLine();
                    Gson gson = new Gson();
                    List<PageEntry> searchResult = engine.search(searchWord);
                    if (searchResult.size() != 0) {
                        out.println(gson.toJson(searchResult));
                    } else {
                        out.println("Данного слова нет в pdf-файлах");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}