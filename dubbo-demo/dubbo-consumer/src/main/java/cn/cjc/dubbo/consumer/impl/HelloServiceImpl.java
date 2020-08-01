package cn.cjc.dubbo.consumer.impl;

import cn.cjc.dubbo.share.HelloService;
import cn.cjc.dubbo.share.UserService;
import cn.cjc.dubbo.share.dto.UserDTO;

/**
 * @author chenjc
 * @since 2016-07-06
 */
public class HelloServiceImpl implements HelloService {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String sayHi(String name) {
        UserDTO user = userService.getUser(1L);
        System.out.println("sayHi " + Thread.currentThread());
        return "欢迎二位，" + name + "，" + user.getName();
    }
}
