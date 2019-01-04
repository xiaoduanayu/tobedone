package cn.cjc.tm.pkg3.pkg31;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * 演示NIO
 *
 * @author chenjc
 * @since 2015/8/18
 */
public class Server {

    private ServerSocketChannel ssc;

    private Selector selector;

    private Charset charset = Charset.defaultCharset();

    public static void main(String[] args) throws IOException {
        new Server().startup();
    }

    public Server() throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(8888));
        ssc.configureBlocking(false);
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void startup() {
        while (true) {
            try {
                selector.select();//此处阻塞
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(SelectionKey selectedKey) throws IOException {
        if (selectedKey.isAcceptable()) {
            System.out.println("accept");
            ServerSocketChannel ssc = (ServerSocketChannel) selectedKey.channel();
            SocketChannel socketChannel = ssc.accept();
            System.out.println("开始连接" + socketChannel);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
        if (selectedKey.isReadable()) {
            System.out.println("read");
            handleRead(selectedKey);
        }
    }

    private void handleRead(SelectionKey selectedKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len = socketChannel.read(buffer);
        if (len > 0) {
            buffer.flip();
            String ori = charset.decode(buffer).toString();
            String resp = "echo:" + ori;
            ByteBuffer buf = charset.encode(resp);
            socketChannel.write(buf);
        } else if (len < 0) {
            System.out.println("关闭连接" + socketChannel);
            selectedKey.cancel();
            socketChannel.close();
        }
    }

}
