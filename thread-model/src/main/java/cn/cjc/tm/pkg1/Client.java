package cn.cjc.tm.pkg1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author chenjc
 * @since 2015/8/18
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8888);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        String msg;
        while ((msg = console.readLine()) != null) {
            if (msg.equals("exit")) {
                socket.close();
                break;
            }
            writer.println(msg);
            System.out.println(reader.readLine());
        }
    }
}
