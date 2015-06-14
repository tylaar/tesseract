package net.oscartech.tesseract.node.rpc.aop;

import freemarker.ext.beans.HashAdapter;
import net.oscartech.tesseract.node.rpc.protocol.RpcMethodUtils;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommand;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommandParam;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommandParamType;
import net.oscartech.tesseract.node.rpc.protocol.TessyProtocolException;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tylaar on 15/5/2.
 */
public class RpcServiceProcessor{

    private static final String DEFAULT_END_POINT = "DEFAULT_END_POINT";
    private static final int PARAMS_NUMBER_LIMITS = 10;
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
            extractEachClazzAnnotation(clazz);
        }
    }

    private List<MethodSig> extractEachClazzAnnotation(final Class<?> clazz) {
        List<MethodSig> methodSigs = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println("method name: " + method.getName());
            RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
            if (rpcMethod != null) {
                //Class<?>[] args = method.getParameterTypes();
                Parameter[] parameters = method.getParameters();
                MethodSig sig = new MethodSig();
                for (Parameter p : parameters) {
                    sig.addMethodParam(p);
                }
                sig.setMethodName(rpcMethod.name());
                sig.setMethodResult(method.getReturnType());
                methodSigs.add(sig);
            }
        }
        return methodSigs;
    }

    private MethodSig validateRpcCall(TessyCommand command) {

        if (!serviceMap.containsKey(command.getServiceName())) {
            throw new TessyProtocolException(TessyProtocolException.SERVICE_NAME_NOT_EXIST, command.getServiceName() + ": unexisted service name");
        }

        if (!methodSigMap.containsKey(command.getCommandName())) {
            throw new TessyProtocolException(TessyProtocolException.PROTOCOL_NAME_NOT_EXIST, command.getCommandName() + ": unexisted call name");
        }

        MethodSig signature = methodSigMap.get(command.getCommandName());

        if (command.getCommandParams().size() > PARAMS_NUMBER_LIMITS) {
            throw new TessyProtocolException(TessyProtocolException.TOO_MANY_PARAMS, "current tessy only support up to ");
        }

        if (signature.getParameters().size() != command.getCommandParams().size()) {
            throw new TessyProtocolException(TessyProtocolException.PARAM_NUM_MIS_MATCH,
                    "expecting " + signature.getParameters().size()+ ", but only found " + command.getCommandParams().size());
        }

        if (!typeValidation(signature, command)) {
            throw new TessyProtocolException(TessyProtocolException.PARAM_TYPE_ERROR, "type validation error");
        }

        return signature;
    }

    /**
     * DEPRECATED: we don't need a name checking. In any language API calling, name is only context variable for
     * compiler, but no real world binding.
     * @param signature
     * @param command
     * @return
     */
    @Deprecated
    private boolean nameValidation(final MethodSig signature, final TessyCommand command) {
        TessyCommandParam[] params = (TessyCommandParam[]) command.getCommandParams().toArray();
        int index = 0;
        for (Parameter paramName : signature.getParameters()) {
            index++;
        }
        return true;
    }

    public void callMethod(TessyCommand command) {
        MethodSig sig = validateRpcCall(command);
        Object service = serviceMap.get(command.getServiceName());

        /**
         * TODO: use the signature and service endpoint to conduct the RPC call
         * TODO: this version I can only thinking about supporting basic type in Java language.
         * TODO: as the trait for the complex type will be very complicated for me to handle
         * TODO: within a single day.
         */
    }

    private boolean typeValidation(final MethodSig signature, final TessyCommand command) {
        TessyCommandParam[] params = (TessyCommandParam[]) command.getCommandParams().toArray();
        int i = 0;
        List<Parameter> paramsInSignature = signature.getParameters();

        /**
         * Travel comparing the type information for the method calling signature.
         */
        for (Parameter p : paramsInSignature) {
            if (TessyCommandParamType.fromClazz(p.getType()).getCode()
                    != params[i].getType()) {
                return false;
            }
            i++;
        }
        /**
         * If the type list is still not ending, it shall be false
         */
        return params.length == i;
    }


    class MethodSig {
        private String methodName;
        private List<Parameter> parameters;
        private Class<?> methodResult;

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(final String methodName) {
            this.methodName = methodName;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public void setParameters(final List<Parameter> parameters) {
            this.parameters = parameters;
        }

        public void addMethodParam(final Parameter parameter) {
            if (parameters == null) {
                parameters = new ArrayList<>();
            }
            parameters.add(parameter);
        }

        public Class<?> getMethodResult() {
            return methodResult;
        }

        public void setMethodResult(final Class<?> methodResult) {
            this.methodResult = methodResult;
        }
    }
}