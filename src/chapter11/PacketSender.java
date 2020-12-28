package chapter11;

import jpcap.JpcapSender;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName internetProgram
 * @ClassName PacketSender
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/15 20:49
 * @Version 1.0
 * @Function
 */

public class PacketSender {

    private static TCPPacket tcp;

    public static void sendTCPPacket(JpcapSender sender, int srcPort,
                                     int dstPort, String srcHost, String dstHost, String data,
                                     String srcMAC, String dstMAC,
                                     boolean syn, boolean ack, boolean rst, boolean fin) {
        try {
            //构造一个TCP包
            tcp = new TCPPacket(srcPort, dstPort, 56, 78,
                    false, ack, false, rst, syn, fin, true, true, 200, 10);
            //设置IPv4报头参数，ip地址可以伪造
            tcp.setIPv4Parameter(0, false, false, false,
                    0, false, false, false, 0, 1010101,
                    100, IPPacket.IPPROTO_TCP, InetAddress.getByName(srcHost), InetAddress.getByName(dstHost)
                    );

            //填充TCP包中的数据
            tcp.data = data.getBytes(StandardCharsets.UTF_8);
            //构造相应的MAC帧
            EthernetPacket ether=new EthernetPacket();
            ether.frametype = EthernetPacket.ETHERTYPE_IP;
            tcp.datalink = ether;


            ether.src_mac = convertMacFormat(srcMAC);
            ether.dst_mac = convertMacFormat(dstMAC);
            if(ether.src_mac == null || ether.dst_mac==null)
                throw new Exception("MAC地址输入错误");

            sender.sendPacket(tcp);
            System.out.println("发包成功！");
            sender.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            //重新抛出异常，调用者可以捕获处理
            throw new RuntimeException(e);
        }
    }

    public static byte[] convertMacFormat(String MAC) {
//（1）首先判断参数MAC中是否包含"-"或":";
// (2)通过split方法将MAC切分为字符串数组;
// (3)定义一个6字节的字节数组，循环将十六进制形式的字符串转为字节，赋值给字节数组;
// 提示：通过(byte)Integer.parseInt("0F",16)方式将16进制表示的字符串转为字节
        byte[] bytes = new byte[6];
        if (MAC.contains(":")) {
            String[] MACList = MAC.split(":");
            for (int i = 0; i < 6; i++ ) {
                bytes[i] = (byte) Integer.parseInt(MACList[i], 16);
            }
            System.out.println(bytes);
            return bytes;
        }
        else if (MAC.contains("-")) {
            String[] MACList = MAC.split("-");
            for (int i = 0; i < 6; i++ ) {
                bytes[i] = (byte) Integer.parseInt(MACList[i], 16);
            }
            System.out.println(bytes);
            return bytes;
        }
        return null;
    }
}
