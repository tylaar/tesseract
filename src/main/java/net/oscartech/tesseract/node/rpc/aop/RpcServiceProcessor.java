package net.oscartech.tesseract.node.rpc.aop;

import com.google.inject.Guice;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommand;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommandParam;
import net.oscartech.tesseract.node.rpc.protocol.TessyCommandParamType;
import net.oscartech.tesseract.node.rpc.protocol.TessyProtocolException;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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
    private Map<String, Map<String, MethodSig>> methodSigMap = new ConcurrentHashMap<>();

    public void scanAnnotation(String classpath) {
        Reflections reflections = new Reflections(classpath);
        Set<Class<?>> result = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class<?> clazz : result) {
            RpcService serviceName = clazz.getAnnotation(RpcService.class);
            try {
                serviceMap.put(serviceName.name(), createSingletonService(clazz));
                methodSigMap.put(serviceName.name(), extractEachClazzAnnotation(clazz));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }
    }

    private Object createSingletonService(final Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> ctor = clazz.getConstructor(String.class);
        return ctor.newInstance(new Object[] {});
    }

    private Map<String, MethodSig> extractEachClazzAnnotation(final Class<?> clazz) {
        Map<String, MethodSig> methodSigs = new HashMap<>();
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
                sig.setMethod(method);
                sig.setMethodName(rpcMethod.name());
                sig.setMethodResult(method.getReturnType());
                methodSigs.put(sig.getMethodName(), sig);
            }
        }
        return methodSigs;
    }

    private MethodSig validateRpcCall(TessyCommand command) {

        if (!serviceMap.containsKey(command.getServiceName())) {
            throw new TessyProtocolException(TessyProtocolException.SERVICE_NAME_NOT_EXIST, command.getServiceName() + ": unexisted service name");
        }

        if (!methodSigMap.get(command.getServiceName()).containsKey(command.getCommandName())) {
            throw new TessyProtocolException(TessyProtocolException.PROTOCOL_NAME_NOT_EXIST, command.getCommandName() + ": unexisted call name");
        }

        MethodSig signature = methodSigMap.get(command.getServiceName()).get(command.getCommandName());

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

        try {
            sig.getMethod().invoke(service, extractArgObjects(command));
        } catch (IllegalAccessException e) {
            throw new TessyProtocolException(TessyProtocolException.PROTOCAL_INVOKING_FAILURE,
                    "Access for the service failed with the param list.",
                    e);
        } catch (InvocationTargetException e) {
            throw new TessyProtocolException(TessyProtocolException.PROTOCAL_INVOKING_FAILURE,
                    "invocation for the service failed with the param list.",
                    e);
        }
        /**
         * TODO: use the signature and service endpoint to conduct the RPC call
         * TODO: this version I can only thinking about supporting basic type in Java language.
         * TODO: as the trait for the complex type will be very complicated for me to handle
         * TODO: within a single day.
         */
    }

    private Object[] extractArgObjects(final TessyCommand command) {
        List<Object> args = new ArrayList<>();
        for (TessyCommandParam param : command.getCommandParams()) {
            args.add(param.getValue());
        }
        return args.toArray();
    }

    private boolean typeValidation(final MethodSig signature, final TessyCommand command) {
        List<TessyCommandParam> paramList = command.getCommandParams();
        TessyCommandParam[] params = extractCommandParamsFromList(paramList);
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

    private TessyCommandParam[] extractCommandParamsFromList(final List<TessyCommandParam> paramList) {
        TessyCommandParam[] params = new TessyCommandParam[paramList.size()];
        int i = 0;
        for (TessyCommandParam param : paramList) {
            params[i] = param;
            i++;
        }
        return params;
    }


    class MethodSig {
        private String methodName;
        private Method method;
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

        public Method getMethod() {
            return method;
        }

        public void setMethod(final Method method) {
            this.method = method;
        }
    }
}