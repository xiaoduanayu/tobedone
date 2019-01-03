package cn.cjc.dubbo.share;


import cn.cjc.dubbo.share.dto.UserDTO;

/**
 * @author chenjc
 * @since 2016-07-06
 */
public interface UserService {

    Boolean saveUser(UserDTO user);

    UserDTO getUser(Long id);
}
