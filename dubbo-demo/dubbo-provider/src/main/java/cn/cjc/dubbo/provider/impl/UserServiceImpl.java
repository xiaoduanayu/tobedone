package cn.cjc.dubbo.provider.impl;

import cn.cjc.dubbo.share.UserService;
import cn.cjc.dubbo.share.dto.UserDTO;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjc
 * @since 2016-07-06
 */
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class);

    private final List<UserDTO> userList = new ArrayList<>();

    @Override
    public Boolean saveUser(UserDTO user) {
        userList.add(user);
        return true;
    }

    @Override
    public UserDTO getUser(Long id) {
        LOGGER.info("请求开始：" + Thread.currentThread().getName());
        UserDTO userDTO = new UserDTO();
        userDTO.setName("陈文羽");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("请求结束：" + Thread.currentThread().getName());
        return userDTO;
    }
}
