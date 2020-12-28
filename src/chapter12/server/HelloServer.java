package chapter12.server;

import rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.zip.ZipEntry;

/**
 * @ProjectName internetProgram
 * @ClassName HelloServer
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/22 20:45
 * @Version 1.0
 * @Function
 */

public class HelloServer {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            HelloService helloService = new HelloServiceImpl("远程服务");
            registry.rebind("HelloService", helloService);
            System.out.println("发布了一个HelloService RMI远程服务");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
