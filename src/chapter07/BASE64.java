/* -*- coding: utf-8 -*-
 * @Author: Jewfer
 * @Date: 2020/10/18 16:35
 */
package chapter07;


public class BASE64 {
    public static void main(String[] args) {
        String userName = "2128706929@qq.com";
        String authCode = "xsemauhwadbwdcia";
        //显示邮箱名的base64编码结果
        System.out.println(encode(userName));
        //显示授权码的base64编码结果
        System.out.println(encode(authCode));
    }

    public static String encode(String str) {
        return new sun.misc.BASE64Encoder().encode(str.getBytes());
    }
}

