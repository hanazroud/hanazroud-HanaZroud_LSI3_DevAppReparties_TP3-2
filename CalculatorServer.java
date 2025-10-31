package tp3_2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CalculatorServer {
    private int port;
    private AtomicInteger globalCounter = new AtomicInteger(0);
    private ExecutorService threadPool;

    public CalculatorServer(int port, int maxThreads) {
        this.port = port;
        this.threadPool = new ThreadPoolExecutor(
                Math.max(2, Runtime.getRuntime().availableProcessors()),
                maxThreads,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] CalculatorServer started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, globalCounter);
                try {
                    threadPool.submit(handler);
                } catch (RejectedExecutionException re) {
                    System.err.println("[SERVER] Too many clients - rejecting " + clientSocket.getRemoteSocketAddress());
                    clientSocket.close();
                }
            }
        } finally {
            threadPool.shutdown();
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        int maxThreads = 50;
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        if (args.length >= 2) maxThreads = Integer.parseInt(args[1]);
        CalculatorServer server = new CalculatorServer(port, maxThreads);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
