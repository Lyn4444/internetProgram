package chapter12;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @ProjectName internetProgram
 * @ClassName RmiKitService
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/23 15:10
 * @Version 1.0
 * @Function
 */

public class RmiKitServiceImpl extends UnicastRemoteObject implements rmi.RmiKitService {


    public RmiKitServiceImpl() throws RemoteException {}


    @Override
    public long ipToLong(String ip) throws RemoteException {
        long ips = 0L;
        String[] numbers = ip.split("\\.");
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(numbers[i]);
        }
        return ips;
    }

    @Override
    public String longToIp(long ipNum) throws RemoteException {
        String ip = "";
        for (int i = 3; i >= 0; i--) {
            ip += String.valueOf((ipNum & 0xff));
            if(i != 0){
                ip += ".";
            }
            ipNum = ipNum >> 8;
        }

        return ip;
    }

    @Override
    public byte[] macStringToBytes(String macStr) throws RemoteException {
        byte[] bytes = new byte[6];
        if (macStr.contains("-")) {
            String[] MACList = macStr.split("-");
            for (int i = 0; i < 6; i++ ) {
                bytes[i] = (byte) Integer.parseInt(MACList[i], 16);
            }
            return bytes;
        }
        return null;
    }

    @Override
    public String bytesToMACString(byte[] macBytes) throws RemoteException {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte macByte : macBytes) {
            String s = Integer.toHexString(macByte & 0xFF);
            stringBuffer.append(s);
            stringBuffer.append("-");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }
}
