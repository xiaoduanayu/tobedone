package cn.cjc.tm.pkg2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 演示伪异步IO
 *
 * @author chenjc
 * @since 2015/8/18
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket;
        ExecutorService executor = Executors.newCachedThreadPool();
        while (true) {
            socket = serverSocket.accept();
            System.out.println("接收到新连接" + socket);
            executor.execute(new SocketHandler(socket));
        }
    }

    private static class SocketHandler implements Runnable {

        private Socket socket;

        SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                String msg;
                while ((msg = reader.readLine()) != null) {
                    writer.println("echo:" + msg);
                }
                System.out.println("退出连接" + socket);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
