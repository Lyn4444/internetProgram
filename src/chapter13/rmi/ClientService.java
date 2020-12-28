package chapter13.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @ProjectName internetProgram
 * @InterfaceName ClientService
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/30 14:55
 * @Version 1.0
 * @Function
 */

public interface ClientService extends Remote {
    public void showMsgToClient(String msg) throws RemoteException;
}
