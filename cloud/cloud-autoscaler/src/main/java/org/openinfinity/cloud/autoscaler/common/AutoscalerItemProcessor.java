package org.openinfinity.cloud.autoscaler.common;

import org.openinfinity.cloud.autoscaler.notifier.Notifier;
import org.openinfinity.cloud.autoscaler.periodicautoscaler.Failures;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class AutoscalerItemProcessor {

    /**
     * Maps cluster id (group id) to Failures data
     */

    protected Map<Integer, Failures> failuresMap;

    protected int clusterId;

    @Autowired
    protected InstanceService instanceService;

    @Autowired
    protected ScalingRuleService scalingRuleService;

    @Autowired
    protected ClusterService clusterService;

    @Autowired
    protected Notifier notifier;

    protected AutoscalerItemProcessor(){
        failuresMap = new HashMap<Integer, Failures>();
    }

    public Map<Integer, Failures> getFailuresMap() {
        return failuresMap;
    }

    public Job createJob(Cluster cluster, int newClusterSize) {
        Instance instance = instanceService.getInstance(cluster.getInstanceId());
        return new Job("scale_cluster",
                cluster.getInstanceId(),
                instance.getCloudType(),
                JobService.CLOUD_JOB_CREATED,
                instance.getZone(),
                Integer.toString(cluster.getId()),
                newClusterSize);
    }

    protected Failures initializeFailures(){
        if (!failuresMap.containsKey(clusterId)){
            failuresMap.put(clusterId, new Failures(0, false));
        }
        return failuresMap.get(clusterId);
    }
}
