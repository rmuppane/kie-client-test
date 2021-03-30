package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kie.server.api.model.instance.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkAbort extends AbstractKieServerConnector {

	private static final Long DELAY = 1000L; // 1 sec
	private static final Logger log = LoggerFactory.getLogger(BulkAbort.class);

	public static void main(String[] args) {
		BulkAbort client = new BulkAbort();

		// Possible statuses for abort
		List<Integer> statuses = new ArrayList<Integer>();
		statuses.add(new Integer("1"));
		
		if (args.length > 0) {
			String containerIds = args[0];
			List<String> listOfContainers = Arrays.asList(containerIds.split(",", -1));
			for (String containerId : listOfContainers) {
				try {
					List<ProcessInstance> instances = client.getQueryClient().findProcessInstancesByContainerId(containerId, statuses, 0, 1000);
					if(!instances.isEmpty()) {
						for (ProcessInstance pi : instances) {
							try {
								log.info("For Containerid  {}, Aborting  the {} process instance", containerId, pi.getId());
								client.getProcessClient().abortProcessInstance(containerId, pi.getId());
								Thread.sleep(DELAY);
							} catch (Exception exe) {
								log.error("Unable to Abort for Containerid  {} and the {} process instance; exception is {}", containerId, pi.getId(), exe);
							}
						}
					}
					else {
						log.info("For Containerid  {}, there are no process instances active to be abort", containerId);
					}
				} 
				catch (Exception exe) {
					log.error("Unable to retrive the process instances  for Containerid  {} and exception is {}", containerId, exe);
				}
			}
		} else {
			log.info("Provide the comma separated containerids to Abort process instances. Example containerid1,containerid1");
		}
	}
}
