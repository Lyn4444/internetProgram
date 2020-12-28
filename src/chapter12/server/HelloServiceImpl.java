package chapter12.server;

import rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 * @ProjectName internetProgram
 * @ClassName HelloServiceImpl
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/22 20:42
 * @Version 1.0
 * @Function
 */

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

    private String name;

    public HelloServiceImpl() throws RemoteException {}

    public HelloServiceImpl(String name) throws RemoteException {
        this.name = name;
    }

    @Override
    public String echo(String msg) throws RemoteException {
        System.out.println("服务端完成一些echo方法相关任务......");
        return "echo: " + msg + " from " + name;
    }

    @Override
    public Date getTime() throws RemoteException {
        System.out.println("服务端完成一些getTime方法相关任务......");
        return new Date();
    }
}
