package org.example.nettyclientdemo.netty.handle.main_device_status;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.nettyclientdemo.netty.handle.NettyReadBo;

/**
 * 设备状态消息体
 * @author WQY
 * @version 1.0
 * @date 2023/11/30 14:18
 */
@Data
@AllArgsConstructor
public class MainDeviceStatusC3InBo extends NettyReadBo {

    //消息体
    private String msg;

}
