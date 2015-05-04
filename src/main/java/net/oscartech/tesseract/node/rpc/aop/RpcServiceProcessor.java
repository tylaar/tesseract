package net.oscartech.tesseract.node.rpc.aop;

import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tylaar on 15/5/2.
 */
public class RpcServiceProcessor{

    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private Map<String, MethodSig> methodSigMap = new ConcurrentHashMap<>();

    public RpcServiceProcessor() {
        super();
    }

    public void scanAnnotation(String classpath) {
        Reflections reflections = new Reflections(classpath);
        Set<Class<?>> result = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> clazz : result) {
            RpcService serviceName = clazz.getAnnotation(RpcService.class);
            serviceMap.put(serviceName.name(), clazz);
        }
    }

    private List<MethodSig> extractEachClazzAnnotation(final Class<?> clazz) {
        List<MethodSig> methodSigs = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println("method name: " + method.getName());
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if (rpcMethod != null) {
                Class<?>[] args = method.getParameterTypes();
                MethodSig sig = new MethodSig();
                sig.setMethodArgs(new ArrayList<Class<?>>(Arrays.asList(args)));
                sig.setMethodName(rpcMethod.name());
                sig.setMethodResult(method.getReturnType());
                methodSigs.add(sig);
            }
        }
        return methodSigs;
    }


    class MethodSig {
        private String methodName;
        private List<Class<?>> methodArgs;
        private Class<?> methodResult;

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(final String methodName) {
            this.methodName = methodName;
        }

        public List<Class<?>> getMethodArgs() {
            return methodArgs;
        }

        public void setMethodArgs(final List<Class<?>> methodArgs) {
            this.methodArgs = methodArgs;
        }

        public Class<?> getMethodResult() {
            return methodResult;
        }

        public void setMethodResult(final Class<?> methodResult) {
            this.methodResult = methodResult;
        }
    }
}