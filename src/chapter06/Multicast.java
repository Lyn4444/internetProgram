package chapter06;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName internetProgram
 * @ClassName Multicast
 * @Description TODO
 * @Author Lyn
 * @Date 2020/10/12 16:23
 * @Version 1.0
 */
public class Multicast {
    InetAddress groupIP;
    int port = 8900;
    MulticastSocket ms = null; //组播套接字
    byte[] inBuff = new byte[1024];
    byte[] outBuff = new byte[1024];

    public Multicast() throws IOException {

        groupIP = InetAddress.getByName("225.0.0.1");
        //开启一个组播端口(UDP端口)
        ms = new MulticastSocket(port);
        //告诉网卡这样的IP地址数据包要接收
        ms.joinGroup(groupIP);
    }

    public void send(String msg) {
        try {
            outBuff = ("20181002837 罗杰鸿: " + msg).getBytes(StandardCharsets.UTF_8);
            DatagramPacket outPacket = new DatagramPacket(outBuff, outBuff.length,
                    groupIP, port);
            ms.send(outPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() {
        try {
            DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
            ms.receive(inPacket);
            String msg = new String(inPacket.getData(), 0, inPacket.getLength(), StandardCharsets.UTF_8);
            return "From " + inPacket.getAddress() + " " + msg;
        } catch (IOException e) {
            return null;
        }
    }

    public void close() {
        try {
            ms.leaveGroup(groupIP);
            ms.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
