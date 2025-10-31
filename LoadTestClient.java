package tp3_2;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;

public class LoadTestClient {
    public static void main(String[] args) throws Exception {
        String host = (args.length >= 1) ? args[0] : "localhost";
        int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 5000;
        int clients = (args.length >= 3) ? Integer.parseInt(args[2]) : 10;
        int reqsPerClient = (args.length >= 4) ? Integer.parseInt(args[3]) : 20;

        ExecutorService ex = Executors.newFixedThreadPool(clients);
        CountDownLatch latch = new CountDownLatch(clients);
        for (int i = 0; i < clients; i++) {
            final int id = i;
            ex.submit(() -> {
                try (Socket s = new Socket(host, port);
                     ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                     ObjectInputStream ois = new ObjectInputStream(s.getInputStream())) {
                    Random r = new Random();
                    String clientId = "loadClient-" + id;
                    for (int j = 0; j < reqsPerClient; j++) {
                        double a = r.nextInt(100);
                        double b = r.nextInt(10); // avoid too many zeros
                        String op = new String[]{"+", "-", "*", "/"}[r.nextInt(4)];
                        CalculatorRequest req = new CalculatorRequest(a, b, op, clientId);
                        oos.writeObject(req);
                        oos.flush();
                        Object resp = ois.readObject();
                        // ignore content - for load testing
                        Thread.sleep(10);
                    }
                    oos.writeObject("quit");
                    oos.flush();
                } catch (Exception e) {
                    System.err.println("Load client " + id + " error: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        ex.shutdown();
        System.out.println("Load test finished.");
    }
}
