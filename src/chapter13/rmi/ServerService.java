package chapter13.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @ProjectName internetProgram
 * @InterfaceName ServerService
 * @Description TODO
 * @Author Lyn
 * @Date 2020/11/30 14:57
 * @Version 1.0
 * @Function
 */

public interface ServerService extends Remote {
    /**
     * 客户加入群组的远程方法
     * @param client 格式为学号-姓名的字符串
     * @param clientService 用于将客户端的远程对象注入在线列表
     * @return 返回相关信息
     * @throws RemoteException
     */
    public String addClientToOnlineGroup(String client, ClientService clientService) throws RemoteException;

    /**
     * 客户退出群组的远程方法
     */
    public String removeClientFromOnlineGroup(String client,ClientService clientService) throws RemoteException;

    /**
     * 客户发送群聊信息的远程方法
     * @param client  格式为学号-姓名的字符串
     * @param msg 要发送的信息
     * @throws RemoteException
     */
    public void sendPublicMsgToServer(String client,String msg) throws RemoteException;

}
