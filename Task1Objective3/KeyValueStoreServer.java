import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class KeyValueStoreServer {
    // Same as before: ConcurrentHashMap to store key-value pairs
    private static ConcurrentHashMap<String, String> keyValueStore = new ConcurrentHashMap<>();
    private static int port = 5000; // Default port

    public static void main(String[] args) {
        // Modification: Allow port to be specified as a command-line argument
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Key-Value Store Server started on port " + port);

        // Same as before: Use ExecutorService to handle multiple clients
        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                // Same as before: Accept client connections and handle them in separate threads
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    // Same as before: ClientHandler class to handle client communication
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
                );
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String response = processCommand(inputLine);
                    out.println(response);
                }
            } catch (IOException e) {
                System.out.println("Exception in client handler: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    // Same as before: Method to process commands from the client
    private static String processCommand(String inputLine) {
        if (inputLine == null || inputLine.trim().isEmpty()) {
            return "Error: Empty command.";
        }

        String[] tokens = inputLine.trim().split("\\s+", 3);
        String command = tokens[0].toUpperCase();

        try {
            switch (command) {
                case "GET":
                    return handleGet(tokens);
                case "POST":
                    return handlePost(tokens);
                case "DELETE":
                    return handleDelete(tokens);
                default:
                    return "Error: Invalid command. Available commands: GET, POST, DELETE";
            }
        } catch (Exception e) {
            return "Error processing command: " + e.getMessage();
        }
    }

    // Same as before: Handler for GET command
    private static String handleGet(String[] tokens) {
        if (tokens.length != 2) {
            return "Usage: GET key";
        }
        String key = tokens[1];
        String value = keyValueStore.get(key);
        if (value != null) {
            return "Value: " + value;
        } else {
            return "Error: Key not found.";
        }
    }

    // Same as before: Handler for POST command
    private static String handlePost(String[] tokens) {
        if (tokens.length != 3) {
            return "Usage: POST key value";
        }
        String key = tokens[1];
        String value = tokens[2];
        keyValueStore.put(key, value);
        return "Success: Key-Value pair added/updated.";
    }

    // Same as before: Handler for DELETE command
    private static String handleDelete(String[] tokens) {
        if (tokens.length != 2) {
            return "Usage: DELETE key";
        }
        String key = tokens[1];
        if (keyValueStore.remove(key) != null) {
            return "Success: Key deleted.";
        } else {
            return "Error: Key not found.";
        }
    }
}
