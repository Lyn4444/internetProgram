package lookupscore;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName LookUpScore
 * @Description TODO
 * @Author Lyn
 * @Date 2020/9/21 18:13
 * @Version 1.0
 */
public class LookUpScore {
    private final Socket socket; //定义套接字
    private final PrintWriter pw;
    private final BufferedReader br;

    public LookUpScore(String ip, String port) throws IOException {

        socket = new Socket(ip, Integer.parseInt(port));

        OutputStream socketOut = socket.getOutputStream();
        pw = new PrintWriter( // 设置最后一个参数为true，表示自动flush数据
                new OutputStreamWriter(//设置utf-8编码
                        socketOut, StandardCharsets.UTF_8), true);

        InputStream socketIn = socket.getInputStream();
        br = new BufferedReader(
                new InputStreamReader(socketIn, StandardCharsets.UTF_8));
    }


    public void send(String msg) {
        pw.println(msg);
    }


    public String receive() {
        String msg = null;
        try {
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }


    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
