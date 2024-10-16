import java.io.*;
import java.net.*;
import java.util.Scanner;

public class KeyValueStoreClient {
    private static final String SERVER_ADDRESS = "localhost"; // Or the server's IP address
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
        ) {
            System.out.println("Connected to Key-Value Store Server at " + SERVER_ADDRESS + ":" + PORT);
            System.out.println("Available commands: GET key, POST key value, DELETE key, EXIT");

            while (true) {
                System.out.print("> ");
                String inputLine = scanner.nextLine();

                if ("EXIT".equalsIgnoreCase(inputLine.trim())) {
                    System.out.println("Exiting client.");
                    break;
                }

                out.println(inputLine);
                String response = in.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
