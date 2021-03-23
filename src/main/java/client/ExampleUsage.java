package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleUsage extends AbstractKieServerConnector {

    private static final String CONTAINER_ID = "AsyncCheck_1.0.3-SNAPSHOT";
    // private static final String PROCESS_ID = "AsyncCheck.async-check";
    private static final String PROCESS_ID = "AsyncCheck.async-wih-test";
    private static final String USER = "rhpamAdmin";
    private static final String VAR_NAME = "delayCompleted";
    //private static final Long DELAY = 10000L; // 10sec
    private static final Logger log = LoggerFactory.getLogger(ExampleUsage.class);

    public static void main(String[] args) {
        long pid = 0L;
        ExampleUsage client = new ExampleUsage();
        Boolean pvar;

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(VAR_NAME, false);
            pid = client.getProcessClient().startProcess(CONTAINER_ID, PROCESS_ID, params);

            log.info("Process {} started ", pid);
            long tid = client.getTaskClient().findTasksByStatusByProcessInstanceId(pid, new ArrayList<String>(), 0, 10).get(0).getId();
            log.info("Task {} started", tid);
            log.info("1. Retrieving process variable {} from process instance {}", VAR_NAME, pid);

            Long start = System.nanoTime();
            pvar = (Boolean) client.getProcessClient().getProcessInstanceVariable(CONTAINER_ID, pid, VAR_NAME);
            Long end = System.nanoTime();
            double tmp = end - start;

            double duration = (double) tmp / 1_000_000_000;
            log.info("{} value is {} and the operation took {} seconds", VAR_NAME, pvar, duration);

            log.info("Completing task {} with user {}", tid, client.getUsername());
            start = System.nanoTime();
            client.getTaskClient().completeAutoProgress(CONTAINER_ID, tid, client.getUsername(), new HashMap<String, Object>());
            end = System.nanoTime();
            tmp = end - start;
            duration = (double) tmp / 1_000_000_000;
            log.info("User Task Complete operation took {} seconds", duration);
            

            log.info("2. Retrieving process variable {} from process instance {}", VAR_NAME, pid);
            start = System.nanoTime();
            pvar = (Boolean) client.getProcessClient().getProcessInstanceVariable(CONTAINER_ID, pid, VAR_NAME);
            end = System.nanoTime();
            tmp = end - start;

            duration = (double) tmp / 1_000_000_000;
            log.info("{} value is {} and the operation took {} seconds", VAR_NAME, pvar, duration);

            //log.info("Waiting for {} ms", DELAY);
            //Thread.sleep(DELAY);
            log.info("About to Retrieve process variable");

            log.info("3. Retrieving process variable {} from process instance {}", VAR_NAME, pid);
            start = System.nanoTime();
            pvar = (Boolean) client.getProcessClient().getProcessInstanceVariable(CONTAINER_ID, pid, VAR_NAME);
            end = System.nanoTime();
            tmp = end - start;
            
            duration = (double) tmp / 1_000_000_000;
            log.info("{} value is {} and the operation took {} seconds", VAR_NAME, pvar, duration);
            
            
            Thread.sleep(10000L);
            log.info("After 10 Seconds sleep completed");
            log.info("4. Retrieving process variable {} from process instance {}", VAR_NAME, pid);
            start = System.nanoTime();
            pvar = (Boolean) client.getProcessClient().getProcessInstanceVariable(CONTAINER_ID, pid, VAR_NAME);
            end = System.nanoTime();
            tmp = end - start;
            
            duration = (double) tmp / 1_000_000_000;
            log.info("{} value is {} and the operation took {} seconds", VAR_NAME, pvar, duration);
            //client.getProcessClient().setProcessVariable(CONTAINER_ID, pid, VAR_NAME, new Boolean(false));


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
        	try {
				Thread.sleep(10000L); // Waiting for the process to complete the delay before abort
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            log.info("Aborting {} process instance", pid);
            client.getProcessClient().abortProcessInstance(CONTAINER_ID, pid);
        }
    }

}
