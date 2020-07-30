package cn.cjc.dubbo.consumer.impl;

import cn.cjc.dubbo.share.HelloService;
import cn.cjc.dubbo.share.UserService;
import cn.cjc.dubbo.share.dto.UserDTO;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

/**
 * @author chenjc
 * @since 2016-07-06
 */
@DefaultProperties(groupKey = "helloServiceGroup", threadPoolProperties = {
        @HystrixProperty(name = "coreSize", value = "2"),
        @HystrixProperty(name = "maxQueueSize", value = "1")
})
public class HelloServiceImpl implements HelloService {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @HystrixCommand(fallbackMethod = "sayHiFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "60000")
    })
    @Override
    public String sayHi(String name) {
        UserDTO user = userService.getUser(1L);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread());
        return "欢迎二位，" + name + "，" + user.getName();
    }

    public String sayHiFallback(String name) {
        return "fallback " + name;
    }
}
