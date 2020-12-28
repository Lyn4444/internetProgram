package chapter10;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;
import jpcap.NetworkInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName internetProgram
 * @ClassName test
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/8 19:28
 * @Version 1.0
 * @Function
 */

public class test {

    //    获取网卡列表及相关信息
    public static void main(String[] args) throws IOException {

        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++) {
            //print out its guid information and description
            System.out.println(i+": "+devices[i].name + devices[i].description);
            //print out its MAC address
//            StringBuilder mac = new StringBuilder();
//            for (byte b : devices[i].mac_address) {
//                //mac地址6段，每段是8位，所以只保留低 8位，和0xff相与
//                mac.append(Integer.toHexString(b & 0xff)).append(":");
//            }
//            System.out.println("MAC address:" + mac.substring(0, mac.length() - 1));
//            //print out its IP address, subnet mask and broadcast address
//            for (NetworkInterfaceAddress addr : devices[i].addresses) {
//                System.out.println(" address:" + addr.address + " " + addr.subnet + " "+ addr.broadcast );
//            }
        }
//        JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(devices[2], 1514, true, 20);
//        Packet packet = jpcapCaptor.getPacket();
//        System.out.println(packet);
//        jpcapCaptor.close();
//        jpcapCaptor.loopPacket(-1, new PacketHandler());
//        jpcapCaptor.setFilter("port 443 and host www.gdufs.edu.cn", true);
//        while (jpcapCaptor != null) {
//            jpcapCaptor.processPacket(-1, new PacketHandler());
//        }
    }

    static class PacketHandler implements PacketReceiver {

        @Override
        public void receivePacket(Packet packet) {
            System.out.println(packet);
            String data = new String(packet.data, 0, packet.data.length, StandardCharsets.UTF_8);
            System.out.println(data);
        }
    }
}
