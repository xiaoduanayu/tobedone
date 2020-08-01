package cn.cjc.dubbo.consumer.filter;

import cn.cjc.dubbo.share.dto.UserDTO;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;

/**
 * @author chenjc
 * @since 2020-08-01
 */
@Activate(group = Constants.CONSUMER, value = "hystrix")
public class HystrixFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 将请求包装成命令
        DubboHystrixCommand command = new DubboHystrixCommand(invoker, invocation);
        return command.execute();
    }

    private static class DubboHystrixCommand extends HystrixCommand<Result> {
        private Invoker<?> invoker;
        private Invocation invocation;

        public DubboHystrixCommand(Invoker<?> invoker, Invocation invocation) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d", invocation.getMethodName(), invocation
                            .getArguments() == null ? 0 : invocation.getArguments().length)))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            .withCircuitBreakerRequestVolumeThreshold(20)
                            .withCircuitBreakerSleepWindowInMilliseconds(3000)
                            .withCircuitBreakerErrorThresholdPercentage(50)
                            // 使用dubbo的超时，禁用这里的超时
                            .withExecutionTimeoutEnabled(false))
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                            .withCoreSize(2)
                            // 使用同步队列
                            .withMaxQueueSize(-1))
            );
            this.invoker = invoker;
            this.invocation = invocation;
        }

        @Override
        protected Result run() throws Exception {
            System.out.println("hystrix filter run " + Thread.currentThread());
            Result res = invoker.invoke(invocation);
            if (res.hasException()) {
                throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.COMMAND_EXCEPTION,
                        DubboHystrixCommand.class,
                        res.getException().getMessage(),
                        res.getException(), null);
            }
            return res;
        }

        @Override
        protected Result getFallback() {
            System.out.println("hystrix filter fallback " + Thread.currentThread());
            UserDTO userDTO = new UserDTO();
            userDTO.setName("the dubbo fallback.");
            return new RpcResult(userDTO);
        }
    }
}