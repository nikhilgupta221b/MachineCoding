import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LoadBalancer {
    private static final int LB_PORT = 4000; // Port for the load balancer to listen on
    private static List<ServerInfo> serverList = new ArrayList<>();
    private static int currentServerIndex = 0;

    public static void main(String[] args) {
        // Add server addresses to the serverList
        serverList.add(new ServerInfo("localhost", 5001));
        serverList.add(new ServerInfo("localhost", 5002));
        serverList.add(new ServerInfo("localhost", 5003));

        System.out.println("Load Balancer started on port " + LB_PORT);
        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(LB_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    // Class to store server information
    static class ServerInfo {
        String host;
        int port;

        ServerInfo(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    // ClientHandler for the load balancer
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            ServerInfo serverInfo = getNextServer();

            try (
                Socket serverSocket = new Socket(serverInfo.host, serverInfo.port);
                BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
            ) {
                // Threads to handle bidirectional communication
                Thread clientToServer = new Thread(() -> {
                    try {
                        String line;
                        while ((line = clientIn.readLine()) != null) {
                            serverOut.println(line);
                        }
                    } catch (IOException e) {
                        // Client disconnected
                    }
                });

                Thread serverToClient = new Thread(() -> {
                    try {
                        String line;
                        while ((line = serverIn.readLine()) != null) {
                            clientOut.println(line);
                        }
                    } catch (IOException e) {
                        // Server disconnected
                    }
                });

                clientToServer.start();
                serverToClient.start();

                // Wait for both threads to finish
                clientToServer.join();
                serverToClient.join();

            } catch (IOException | InterruptedException e) {
                System.out.println("Exception in load balancer client handler: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    // Method to select the next server using Round Robin
    private static synchronized ServerInfo getNextServer() {
        ServerInfo server = serverList.get(currentServerIndex);
        currentServerIndex = (currentServerIndex + 1) % serverList.size();
        return server;
    }
}
