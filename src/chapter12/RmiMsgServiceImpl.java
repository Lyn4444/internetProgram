package chapter12;

import rmi.HelloService;
import rmi.RmiMsgService;

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

public class RmiMsgServiceImpl extends UnicastRemoteObject implements RmiMsgService {

    private String name;
    private String num;

    public RmiMsgServiceImpl() throws RemoteException {}

    public RmiMsgServiceImpl(String num, String name) throws RemoteException {
        this.num = num;
        this.name = name;
    }


    @Override
    public String send(String msg) throws RemoteException {
        System.out.println("服务端完成一些send方法相关任务......");
        return "send:" + msg;
    }

    @Override
    public String send(String yourNum, String yourName) throws RemoteException {
        System.out.println("服务端完成一些send方法相关任务......");
        return yourName + "," + yourName;
    }
}
