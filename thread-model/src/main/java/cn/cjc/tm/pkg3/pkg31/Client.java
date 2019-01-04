package cn.cjc.tm.pkg3.pkg31;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author chenjc
 * @since 2015/8/18
 */
public class Client {

    private SocketChannel socketChannel;

    private Selector selector;

    private Charset charset;

    private volatile boolean stop = false;


    public static void main(String[] args) throws IOException {
        new Client().startup();
    }

    public Client() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        charset = Charset.defaultCharset();
    }

    private void startup() {
        try {
            boolean flag = socketChannel.connect(new InetSocketAddress("localhost", 8888));
            if (flag) {
                System.out.println("立即连上");
                socketChannel.register(selector, SelectionKey.OP_READ);
                new Thread(new ConsoleHandler()).start();
            } else {
                System.out.println("稍后连上");
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
            while (!stop) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectedKey = iterator.next();
                    iterator.remove();
                    try {
                        handle(selectedKey);
                    } catch (IOException e) {
                        selectedKey.cancel();
                        selectedKey.channel().close();
                    }
                }
            }
            selector.close();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(SelectionKey selectedKey) throws IOException {
        if (selectedKey.isConnectable()) {
            System.out.println("当前信号：connect");
            SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
            if (socketChannel.finishConnect()) {
                selectedKey.interestOps(SelectionKey.OP_READ);
                new Thread(new ConsoleHandler()).start();
            }
        }
        if (selectedKey.isReadable()) {
            System.out.println("当前信号：read");
            handleRead(selectedKey);
        }
    }

    private void handleRead(SelectionKey selectedKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        socketChannel.read(buf);
        buf.flip();
        System.out.println(charset.decode(buf));
    }

    private class ConsoleHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
                String msg;
                while ((msg = console.readLine()) != null) {
                    if (msg.equals("exit")) {
                        stop = true;
                        selector.wakeup();
                        break;
                    }
                    ByteBuffer buffer = charset.encode(msg);
                    socketChannel.write(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
