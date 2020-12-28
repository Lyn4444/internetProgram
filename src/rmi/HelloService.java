package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * @ProjectName internetProgram
 * @InterfaceName HelloService
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/22 20:39
 * @Version 1.0
 * @Function
 */

public interface HelloService extends Remote {

    public String echo(String msg) throws RemoteException;

    public Date getTime() throws RemoteException;
}
