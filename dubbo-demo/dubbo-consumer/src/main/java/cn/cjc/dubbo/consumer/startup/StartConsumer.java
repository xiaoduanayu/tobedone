package cn.cjc.dubbo.consumer.startup;

import com.alibaba.dubbo.container.Main;

/**
 * @author chenjc
 * @since 2016-07-06
 */
public class StartConsumer {

    public static void main(String[] args) {
        System.setProperty("dubbo.spring.config", "spring/dubbo-consumer.xml");
        Main.main(new String[]{"spring"});
    }
}
