package net.oscartech.tesseract.common;

import net.oscartech.tesseract.node.NodeService;
import net.oscartech.tesseract.node.NodeServiceFactory;
import net.oscartech.tesseract.node.pojo.NodeProposal;
import net.oscartech.tesseract.node.rpc.aop.RpcServiceProcessor;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by tylaar on 15/4/26.
 */
public class NodeServiceTest {
    @Test
    public void testNodeStartUp() throws InterruptedException, IOException {
        List<NodeService> services =  NodeServiceFactory.generateSomeService();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //ThreadPoolExecutor executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory);
        for (NodeService service : services) {
            service.init();
        }
        NodeService service = services.get(0);
        Thread.sleep(2000);
        service.sendMasterProposal();
        service = services.get(1);
        //Thread.sleep(2000);
        service.sendMasterProposal();

        while(true) {
            Thread.yield();
        }
    }

}