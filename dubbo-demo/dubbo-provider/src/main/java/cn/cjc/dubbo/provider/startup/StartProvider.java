package cn.cjc.dubbo.provider.startup;

import com.alibaba.dubbo.container.Main;

/**
 * @author chenjc
 * @since 2016-07-06
 */
public class StartProvider {

    public static void main(String[] args) {
        System.setProperty("dubbo.spring.config", "spring/dubbo-provider.xml");
        Main.main(new String[]{"spring"});
    }
}
