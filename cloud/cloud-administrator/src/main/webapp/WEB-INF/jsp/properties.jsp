<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setBundle basename="cloudadmin"/>

<script type="text/javascript">

var portletURL = new urlContainer("portletURL");
var dialogRes = new resourceContainer("dialogRes");

portletURL.initialize({
    instance: {
    	instanceTableURL : 				'<portlet:resourceURL id="instanceTable"/>',
    	instanceURL : 					'<portlet:resourceURL id="instance"/>',
    	instanceStatusURL : 			'<portlet:resourceURL id="instanceStatus"/>',
    	instanceStatusListURL : 		'<portlet:resourceURL id="instanceStatusList"/>',
    	addInstanceURL : 				'<portlet:resourceURL id="addInstance"/>',
    	getCloudZonesURL : 				'<portlet:resourceURL id="getCloudZones"/>',
    	applyInstanceURL : 				'<portlet:resourceURL id="applyInstance"/>',
    	deleteInstanceURL : 			'<portlet:resourceURL id="deleteInstance"/>',
	    errorResponseURL : 				'<portlet:resourceURL id="errorResponse"/>',
	    getInstanceKeyURL : 			'<portlet:resourceURL id="getInstanceKey"/>',
	    getClusterTypesURL : 			'<portlet:resourceURL id="getClusterTypes"/>',
	    getMachineTypesURL : 			'<portlet:resourceURL id="getMachineTypes"/>',
	    getCloudProvidersURL : 			'<portlet:resourceURL id="getCloudProviders"/>',
      	getTemplatesURL : 				'<portlet:resourceURL id="getTemplates"/>',
      	getElementsForTemplateURL : 	'<portlet:resourceURL id="getElementsForTemplate"/>'

    },
    cluster: {
    	availableClustersURL : 			'<portlet:resourceURL id="availableClusters"/>',    	
    	updatePublishedURL : 			'<portlet:resourceURL id="updatePublished"/>',
    	getClusterInfoURL : 			'<portlet:resourceURL id="getClusterInfo"/>',
    	getClusterStatusURL : 			'<portlet:resourceURL id="getClusterStatus"/>',	    	
    	deleteClusterURL : 				'<portlet:resourceURL id="deleteCluster"/>',
    	scaleClusterURL : 				'<portlet:resourceURL id="scaleCluster"/>',
    	getElasticIPListURL : 			'<portlet:resourceURL id="getElasticIPList"/>',
    	setElasticIPURL : 				'<portlet:resourceURL id="setElasticIP"/>',
    	getElasticIPForClusterURL:		'<portlet:resourceURL id="getElasticIPForCluster"/>',
    	removeElasticIPURL :			'<portlet:resourceURL id="removeElasticIP"/>',
    	getClusterScalingRuleURL : 		'<portlet:resourceURL id="getClusterScalingRule"/>',
    	getUserAuthorizedIPsURL :		'<portlet:resourceURL id="getUserAuthorizedIPsForCluster"/>',
    	deleteUserAuthorizedIPURL :		'<portlet:resourceURL id="deleteUserAuthorizedIP"/>',
    	addUserAuthorizedIPURL :		'<portlet:resourceURL id="addUserAuthorizedIP"/>'

    },
    machine: {
    	machineURL : 				'<portlet:resourceURL id="machine"/>',
    	terminateMachineURL : 		'<portlet:resourceURL id="terminateMachine"/>',
    	machineListURL : 			'<portlet:resourceURL id="machineList"/>'
    },
    service: {
    	newServiceURL : 			'<portlet:resourceURL id="newService"/>',
    	getAvailableServicesURL : 	'<portlet:resourceURL id="getAvailableServices"/>'
    }
});

dialogRes.setResource({
	tabs: {
		removeTab : 				'<fmt:message key="cloud.tabs.title.removeTab" />'
	},
	instance: {
		ok : 						'<fmt:message key="cloud.instancedialog.info.ok" />',
		create : 					'<fmt:message key="cloud.instancedialog.button.create" />',
		del :						'<fmt:message key="cloud.instancedialog.button.delete" />'			
	},
	dialog: {
		applyingChanges : 			'<fmt:message key="cloud.dialog.info.applyingChanges" />',		
		cancel : 					'<fmt:message key="cloud.dialog.button.cancel" />'
	}			
});
	
</script>
