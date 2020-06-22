package com.luxinfeng.cloudclipboard.WebSocket.Util;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class SslContextFactory {
    private static final String PROTOCOL = "TLS";

    private static SSLContext sslContext;

    public static SSLContext getServerContext(String pkPath, String storepass, String keypass){
        if(sslContext !=null) {
            System.out.println("sslcontext is null");
            return sslContext;
        }
        InputStream in =null;

        try{
            //密钥管理器
            KeyManagerFactory kmf = null;
            if(pkPath!=null){
                //密钥库KeyStore
                KeyStore ks = KeyStore.getInstance("JKS");
//                Enumeration<String> enumeration = ks.aliases();
                //加载服务端证书
                in = new FileInputStream(pkPath);
                //加载服务端的KeyStore,  该密钥库的密码"storepass,storepass指定密钥库的密码(获取keystore信息所需的密码)
                ks.load(in, storepass.toCharArray());
                log.info("keystore加载成功！");

                kmf = KeyManagerFactory.getInstance("sunx509");
                //初始化密钥管理器, keypass 指定别名条目的密码(私钥的密码)
                kmf.init(ks, keypass.toCharArray());
            }

            //获取安全套接字协议（TLS协议）的对象
            sslContext = SSLContext.getInstance(PROTOCOL);
            log.info("获取sslcontext成功！");
            //初始化此上下文
            //参数一：认证的密钥    参数二：对等信任认证，如果双向认证就写成tf.getTrustManagers()
        // 参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
            sslContext.init(kmf.getKeyManagers(), null, null);
        }catch(Exception e){
            throw new Error("Failed to init the server-side SSLContext", e);
        }finally{
            if(in !=null){
                try {
                    in.close();
                } catch (IOException e) {
                    log.info("close InputStream.", e);
                }
            }
        }
        return sslContext;
    }
}
