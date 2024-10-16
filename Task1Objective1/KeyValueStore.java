import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStore {

    private static ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Key-Value Store Program");
        System.out.println("Available commands: GET key, POST key value, DELETE key, EXIT");

        while (true) {
            System.out.print("> ");
            String inputLine = scanner.nextLine();
            if (inputLine == null || inputLine.trim().isEmpty()) {
                continue;
            }

            String[] tokens = inputLine.trim().split("\\s+", 3); // Split into at most 3 parts
            String command = tokens[0].toUpperCase();

            try {
                switch (command) {
                    case "GET":
                        handleGet(tokens);
                        break;
                    case "POST":
                        handlePost(tokens);
                        break;
                    case "DELETE":
                        handleDelete(tokens);
                        break;
                    case "EXIT":
                        System.out.println("Exiting program.");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid command. Available commands: GET, POST, DELETE, EXIT");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        }
    }

    private static void handleGet(String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Usage: GET key");
            return;
        }
        String key = tokens[1];
        String value = keyValueStore.get(key);
        if (value != null) {
            System.out.println("Value: " + value);
        } else {
            System.out.println("Key not found.");
        }
    }

    private static void handlePost(String[] tokens) {
        if (tokens.length != 3) {
            System.out.println("Usage: POST key value");
            return;
        }
        String key = tokens[1];
        String value = tokens[2];
        keyValueStore.put(key, value);
        System.out.println("Key-Value pair added/updated.");
    }

    private static void handleDelete(String[] tokens) {
        if (tokens.length != 2) {
            System.out.println("Usage: DELETE key");
            return;
        }
        String key = tokens[1];
        if (keyValueStore.remove(key) != null) {
            System.out.println("Key deleted.");
        } else {
            System.out.println("Key not found.");
        }
    }
}
