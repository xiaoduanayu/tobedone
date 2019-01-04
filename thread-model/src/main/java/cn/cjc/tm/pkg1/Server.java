package cn.cjc.tm.pkg1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 演示BIO
 *
 * @author chenjc
 * @since 2015/8/18
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket;
        while (true) {
            socket = serverSocket.accept();
            System.out.println("接收到新连接" + socket);
            new Thread(new SocketHandler(socket)).start();
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
