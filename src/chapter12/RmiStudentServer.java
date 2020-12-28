package chapter12;


import rmi.RmiKitService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @ProjectName internetProgram
 * @ClassName RmiStudentServer
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/23 15:36
 * @Version 1.0
 * @Function
 */

public class RmiStudentServer {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            RmiKitService rmiKitService = new RmiKitServiceImpl();
            registry.rebind("RmiKitService", rmiKitService);
            System.out.println("发布了一个RmiKitService RMI远程服务");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
