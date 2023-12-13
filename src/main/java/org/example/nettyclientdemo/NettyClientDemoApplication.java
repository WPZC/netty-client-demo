package org.example.nettyclientdemo;

import org.example.nettyclientdemo.netty.NettyClientStartApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyClientDemoApplication.class, args);
        //2.启动NettyClient
        new NettyClientStartApplication().start();
    }

}
