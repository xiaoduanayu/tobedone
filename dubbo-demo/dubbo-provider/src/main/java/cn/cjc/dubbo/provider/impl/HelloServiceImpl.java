package cn.cjc.dubbo.provider.impl;

import cn.cjc.dubbo.share.HelloService;

/**
 * @author chenjc
 * @since 2016-07-06
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHi(String name) {
        return name + "欢迎您！";
    }
}
