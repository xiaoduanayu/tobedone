package cn.cjc.dubbo.provider.impl;

import cn.cjc.dubbo.share.UserService;
import cn.cjc.dubbo.share.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenjc
 * @since 2016-07-06
 */
public class UserServiceImpl implements UserService {

    private final List<UserDTO> userList = new ArrayList<>();

    @Override
    public Boolean saveUser(UserDTO user) {
        userList.add(user);
        return true;
    }

    @Override
    public UserDTO getUser(Long id) {
        System.out.println("请求开始：" + Thread.currentThread().getName());
        UserDTO userDTO = new UserDTO();
        userDTO.setName("陈文羽");
        System.out.println("请求结束：" + Thread.currentThread().getName());
        return userDTO;
    }
}
