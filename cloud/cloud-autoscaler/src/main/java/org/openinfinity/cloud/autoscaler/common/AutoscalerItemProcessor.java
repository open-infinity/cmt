package org.openinfinity.cloud.autoscaler.common;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.springframework.beans.factory.annotation.Autowired;

public class AutoscalerItemProcessor {

    @Autowired
    protected InstanceService instanceService;

    @Autowired
    protected ScalingRuleService scalingRuleService;

    @Autowired
    protected ClusterService clusterService;

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
}
