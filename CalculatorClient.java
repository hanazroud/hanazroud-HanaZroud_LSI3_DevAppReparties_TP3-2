package tp3_2;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class CalculatorClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        String clientId = "client-" + System.currentTimeMillis();
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);
        if (args.length >= 3) clientId = args[2];

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("[CLIENT] Connected to " + host + ":" + port + " as " + clientId);
            System.out.println("Enter: a op b (e.g. 3 + 4). Type 'quit' to exit.");

            while (true) {
                System.out.print("> ");
                String line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    oos.writeObject("quit");
                    oos.flush();
                    break;
                }
                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    try {
                        double a = Double.parseDouble(parts[0]);
                        String op = parts[1];
                        double b = Double.parseDouble(parts[2]);
                        CalculatorRequest req = new CalculatorRequest(a, b, op, clientId);
                        oos.writeObject(req);
                        oos.flush();

                        Object respObj = ois.readObject();
                        if (respObj instanceof CalculatorResponse) {
                            CalculatorResponse resp = (CalculatorResponse) respObj;
                            if (resp.isOk()) System.out.println("= " + resp.getResult());
                            else System.out.println("Error: " + resp.getMessage());
                        } else {
                            System.out.println("Unknown response type");
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid numbers.");
                    }
                } else {
                    System.out.println("Invalid input. Format: a op b");
                }
            }
        } catch (Exception e) {
            System.err.println("[CLIENT] Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[CLIENT] Exiting.");
    }
}
