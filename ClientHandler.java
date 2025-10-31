package tp3_2;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable {
    private Socket socket;
    private AtomicInteger globalCounter;

    public ClientHandler(Socket socket, AtomicInteger globalCounter) {
        this.socket = socket;
        this.globalCounter = globalCounter;
    }

    @Override
    public void run() {
        String clientAddr = socket.getRemoteSocketAddress().toString();
        System.out.println("[SERVER] Connected: " + clientAddr);
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            Object obj;
            while ((obj = ois.readObject()) != null) {
                if (obj instanceof CalculatorRequest) {
                    CalculatorRequest req = (CalculatorRequest) obj;
                    CalculatorResponse resp = process(req);
                    int count = globalCounter.incrementAndGet(); // synchronisé grâce à AtomicInteger
                    System.out.printf("[SERVER] #%d from %s clientId=%s : %s %s %s => %s%n",
                            count, clientAddr, req.getClientId(),
                            req.getA(), req.getOp(), req.getB(),
                            resp.isOk() ? resp.getResult() : resp.getMessage());
                    oos.writeObject(resp);
                    oos.flush();
                } else if (obj instanceof String) {
                    String cmd = (String) obj;
                    if ("quit".equalsIgnoreCase(cmd) || "exit".equalsIgnoreCase(cmd)) break;
                    else {
                        oos.writeObject(new CalculatorResponse(false, 0, "Unknown command"));
                        oos.flush();
                    }
                } else {
                    oos.writeObject(new CalculatorResponse(false, 0, "Invalid request type"));
                    oos.flush();
                }
            }
        } catch (EOFException eof) {
            // client closed normally
        } catch (Exception e) {
            System.err.println("[SERVER] Error with client " + clientAddr + " : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println("[SERVER] Disconnected: " + clientAddr);
        }
    }

    private CalculatorResponse process(CalculatorRequest req) {
        double a = req.getA();
        double b = req.getB();
        String op = req.getOp();
        try {
            switch (op) {
                case "+": return new CalculatorResponse(true, a + b, "OK");
                case "-": return new CalculatorResponse(true, a - b, "OK");
                case "*":
                case "x": return new CalculatorResponse(true, a * b, "OK");
                case "/":
                    if (b == 0) return new CalculatorResponse(false, 0, "Division by zero");
                    return new CalculatorResponse(true, a / b, "OK");
                case "pow": return new CalculatorResponse(true, Math.pow(a, b), "OK");
                default: return new CalculatorResponse(false, 0, "Unsupported op: " + op);
            }
        } catch (Exception e) {
            return new CalculatorResponse(false, 0, "Error: " + e.getMessage());
        }
    }
}
