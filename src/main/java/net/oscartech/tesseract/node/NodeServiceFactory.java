package net.oscartech.tesseract.node;

import net.oscartech.tesseract.node.util.SequenceGenerator;
import net.oscartech.tesseract.node.util.SimpleSequenceGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tylaar on 15/4/26.
 */
public class NodeServiceFactory {

    private static List<Integer> testPorts;
    private static SequenceGenerator sequenceGenerator = new SimpleSequenceGenerator();
    private static String localhost = "127.0.0.1";

    private static List<Integer> generateSomePorts() {
        testPorts = Arrays.asList(50001,50002,50003);
        return testPorts;
    }

    private static List<String> generateSomeLocalhostPairs() {
        List<String> cluster = new ArrayList<>();
        List<Integer> ports = generateSomePorts();
        for (Integer port : ports) {
            cluster.add(localhost + ":" + port);
        }
        return cluster;
    }

    public static List<NodeService> generateSomeService() {
        List<String> map = generateSomeLocalhostPairs();
        List<NodeService> serviceList = new ArrayList<>();

        for (String addr : map) {
            String[] host = addr.split(":");
            serviceList.add(new NodeService("127.0.0.1", Integer.valueOf(host[1]), map, sequenceGenerator));
        }
        return serviceList;
    }

    public static SequenceGenerator getSequenceGenerator() {
        return sequenceGenerator;
    }
}
