package chapter06;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @ProjectName internetProgram
 * @ClassName UDPServer
 * @Description TODO
 * @Author Lyn
 * @Date 2020/10/12 15:26
 * @Version 1.0
 */
public class UDPServer {

    //用于接收数据的报文字节数组缓存最大容量，字节为单位
    private static final int MAX_PACKET_SIZE = 512;

    public static void main(String[] args) {
        int port = 8008;
        try {
            byte[] buffer = new byte[MAX_PACKET_SIZE];
            DatagramSocket datagramSocket = new DatagramSocket(port);
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            System.out.println("UDP Server port: " + port);

            while (true) {
                datagramSocket.receive(datagramPacket);
                String s = new String(datagramPacket.getData(), 0, datagramPacket.getLength(), StandardCharsets.UTF_8);
                String userId = "20181002837";
                String userName = "罗杰鸿";
                s = userId + "&" + userName + "&" + new Date().toString() + "&" + s;
                System.out.println(s);
                byte[] outPut = s.getBytes(StandardCharsets.UTF_8);
                DatagramPacket outPacket = new DatagramPacket(
                        outPut, outPut.length, datagramPacket.getAddress(), datagramPacket.getPort()
                );
                datagramSocket.send(outPacket);
                datagramPacket.setLength(buffer.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
