import cn.cjc.dubbo.share.HelloService;
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
@ContextConfiguration("/dubbo-client.xml")
public class StartClient {

    @Resource
    private HelloService helloService;

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        try {
            System.out.println("开始请求");
            String msg = helloService.sayHi("debo");
            System.out.println(msg);
            System.out.println("结束请求，耗时" + (System.currentTimeMillis() - start) + "ms");
//            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
