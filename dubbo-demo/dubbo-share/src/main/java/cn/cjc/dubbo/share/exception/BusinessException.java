package cn.cjc.dubbo.share.exception;

/**
 * @author chenjc
 * @since 2016-07-07
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
