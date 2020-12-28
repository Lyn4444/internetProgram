package chapter04.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileDataClient {
    private final Socket dataSocket; //定义套接字
    //定义字符输入流和输出流
    private final PrintWriter pw;
    private final BufferedReader br;

    public FileDataClient(String ip, String port) throws IOException {
        //主动向服务器发起连接，实现TCP的三次握手过程
        //如果不成功，则抛出错误信息，其错误信息交由调用者处理

        dataSocket = new Socket(ip, Integer.parseInt(port));

        //得到网络输出字节流地址，并封装成网络输出字符流
        OutputStream socketOut = dataSocket.getOutputStream();
        pw = new PrintWriter( // 设置最后一个参数为true，表示自动flush数据
                new OutputStreamWriter(//设置utf-8编码
                        socketOut, StandardCharsets.UTF_8), true);

        //得到网络输入字节流地址，并封装成网络输入字符流
        InputStream socketIn = dataSocket.getInputStream();
        br = new BufferedReader(
                new InputStreamReader(socketIn, StandardCharsets.UTF_8));
    }


    public void getFile(File saveFile) throws IOException {

        if (dataSocket != null) { // dataSocket是Socket类型的成员变量


            FileOutputStream fileOut = new FileOutputStream(saveFile);//新建本地空文件
            byte[] buf = new byte[1024]; // 用来缓存接收的字节数据
            //网络字节输入流
            InputStream socketIn = dataSocket.getInputStream();
            //网络字节输出流
            OutputStream socketOut = dataSocket.getOutputStream();

            //(2)向服务器发送请求的文件名，字符串读写功能
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socketOut, StandardCharsets.UTF_8), true);
            pw.println(saveFile.getName());

            //(3)接收服务器的数据文件，字节读写功能
            int size = 0;
            while ((size = socketIn.read(buf)) != -1) {//读一块到缓存，读取结束返回-1
                fileOut.write(buf, 0, size); //写一块到文件
            }
            fileOut.flush();//关闭前将缓存的数据全部推出
            //文件传输完毕，关闭流
            fileOut.close();
            if (dataSocket != null) {
                dataSocket.close();
            }

        } else {
            System.err.println("连接ftp数据服务器失败");
        }
    }

    public void send(String msg) {
        //输出字符流，由Socket调用系统底层函数，经网卡发送字节流
        pw.println(msg);
    }


    public String receive() {
        String msg = null;
        try {
            //从网络输入字符流中读信息，每次只能接受一行信息
            //如果不够一行（无行结束符），则该语句阻塞，
            // 直到条件满足，程序才往下运行
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }


    public void close() {
        try {
            if (dataSocket != null) {
                //关闭socket连接及相关的输入输出流,实现四次握手断开
                dataSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
