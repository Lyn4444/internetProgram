package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @ProjectName internetProgram
 * @InterfaceName RmiKitService
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/23 15:05
 * @Version 1.0
 * @Function
 */

public interface RmiKitService extends Remote {

    public long ipToLong(String ip) throws RemoteException;

    public String longToIp(long ipNum) throws RemoteException;

    public byte[] macStringToBytes(String macStr) throws
            RemoteException;

    public String bytesToMACString(byte[] macBytes) throws
            RemoteException;
}
