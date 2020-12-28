package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @ProjectName internetProgram
 * @InterfaceName RmiMsgService
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/23 15:04
 * @Version 1.0
 * @Function
 */

public interface RmiMsgService extends Remote {

    public String send(String msg) throws RemoteException;

    public String send(String yourNum, String yourName) throws
            RemoteException;
}
