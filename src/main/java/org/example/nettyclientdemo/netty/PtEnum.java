package org.example.nettyclientdemo.netty;

/**
 * @author WQY
 * @version 1.0
 * @date 2023/12/3 10:52
 */
public enum PtEnum {

    MB("主控板协议"),
    PB("PLC协议");

    private String describe;

    PtEnum(String describe){
        this.describe = describe;
    }

}
