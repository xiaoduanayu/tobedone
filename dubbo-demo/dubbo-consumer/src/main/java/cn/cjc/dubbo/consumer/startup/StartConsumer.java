package cn.cjc.dubbo.consumer.startup;

import cn.cjc.dubbo.share.UserService;
import cn.cjc.dubbo.share.dto.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author chenjc
 * @since 2016-07-07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/dubbo-consumer.xml")
public class StartConsumer {

    @Resource
    private UserService userService;

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        try {
            System.out.println("开始请求");
            UserDTO user = userService.getUser(1L);
            System.out.println("user:" + user.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("结束请求，耗时" + (System.currentTimeMillis() - start) + "ms");
    }
}
