package org.example.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioselectorServer {


    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(9000));
        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();

        SelectionKey selectionKey = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功");
        while(true){
            selector.select();
            Set<SelectionKey> keySet =  selector.selectedKeys();
            Iterator<SelectionKey> iterator = keySet.iterator();

            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey selKey = socketChannel.register(selector,SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                }else if(key.isReadable()){
                    SocketChannel socketChannel = (SocketChannel)key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = socketChannel.read(byteBuffer);
                    if(len > 0){
                        System.out.println("接收消息:"+new String(byteBuffer.array(),"UTF-8"));
                    }else if(len == -1){
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }else if(key.isWritable()){

                }
                //从集合中删除处理过的事件
                iterator.remove();
            }
        }
    }
}
