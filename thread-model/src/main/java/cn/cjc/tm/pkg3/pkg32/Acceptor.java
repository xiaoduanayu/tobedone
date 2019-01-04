package cn.cjc.tm.pkg3.pkg32;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.Set;

/**
 * 主从Reactor
 *
 * @author chenjc
 * @since 2015/8/18
 */
public class Acceptor {

    private Selector selector;

    private Thread[] threads = new Thread[4];

    private IOReactor[] dispatchers = new IOReactor[4];

    public Acceptor() throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(8888));
        ssc.configureBlocking(false);
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void startup() throws Exception {
        for (int i = 0; i < 4; i++) {
            dispatchers[i] = new IOReactor();
        }
        for (int i = 0; i < 4; i++) {
            threads[i] = new Thread(dispatchers[i]);
            threads[i].start();
        }
        while (true) {
            try {
                // 此处阻塞
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    handle(selectionKey);
                }
                selectionKeys.clear();
            } catch (Exception e) {
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
            // 随机挑选一个IO线程
            int i = new Random().nextInt(4);
            dispatchers[i].addChannel(socketChannel);
        }
    }
}
