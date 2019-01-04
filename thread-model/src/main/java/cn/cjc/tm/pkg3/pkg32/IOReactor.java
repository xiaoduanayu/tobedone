package cn.cjc.tm.pkg3.pkg32;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author chenjc
 * @since 2019-01-03
 */
public class IOReactor implements Runnable {

    private Selector selector;

    private Charset charset = Charset.defaultCharset();

    private Queue<SocketChannel> newChannels = new ConcurrentLinkedQueue<>();

    public IOReactor() throws Exception {
        selector = Selector.open();
    }

    @Override
    public void run() {
        while (true) {
            try {
                int readyCount = selector.select(1000);
                if (readyCount > 0) {
                    // 处理读写事件
                    processEvents(selector.selectedKeys());
                }
                // 处理新的连接
                processNewChannels();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processNewChannels() throws Exception {
        SocketChannel channel;
        while ((channel = newChannels.poll()) != null) {
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        }
    }

    private void processEvents(Set<SelectionKey> selectionKeys) throws Exception {
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey selectedKey = iterator.next();
            iterator.remove();
            try {
                handle(selectedKey);
            } catch (Exception e) {
                selectedKey.cancel();
                selectedKey.channel().close();
            }
        }
    }

    private void handle(SelectionKey selectedKey) throws Exception {
        if (selectedKey.isReadable()) {
            System.out.println("read");
            handleRead(selectedKey);
        }
        if (selectedKey.isWritable()) {
            System.out.println("write");
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

    public void addChannel(SocketChannel socketChannel) {
        newChannels.add(socketChannel);
        selector.wakeup();
    }
}
