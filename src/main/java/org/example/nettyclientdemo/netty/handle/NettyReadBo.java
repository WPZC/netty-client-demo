package org.example.nettyclientdemo.netty.handle;

/**
 * @author WQY
 * @version 1.0
 * @date 2023/12/2 10:43
 */
public class NettyReadBo {

    /**
     * 增加空格
     */
    public String addSpace(String msg){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0;i<(msg.length());i=i+2){
            if((i+2) != msg.length()){
                buffer.append(msg.substring(i,i+2)).append(" ");
            }else {
                buffer.append(msg.substring(i,i+2));
            }
        }
        return buffer.toString();
    }

}
