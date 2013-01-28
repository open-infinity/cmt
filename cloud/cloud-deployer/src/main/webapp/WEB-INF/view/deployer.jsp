<%--

/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 */

@author Ilkka Leinonen

@version 1.0.0
@since 1.0.0

 --%>

<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<portlet:defineObjects />
<portlet:actionURL var="action" />

<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>Cloud Deployer</title>
<style>
label {
	background: white;
	width: 300px;
}
</style>
</head>
<body>
	<form:form modelAttribute="deploymentModel" action="${action}" method="post" enctype="multipart/form-data">
		<input id="instanceId" name="instanceId" type="hidden" />
		<input id="organizationId" name="organizationId" type="hidden" />
		<input id="clusterId" name="clusterId" type="hidden" />
		<p id="phase">Choose organization for deployment (1/6 steps).</p>
		<p id="organizations">
		<b>Choose organization</b>
		<br /><br />
		<c:forEach items="${organizationMap}" var="organization">
			<input id="${organization.key}" name="${organization.key}" path="organizationSelection" type="checkbox" style="display:none;" value="1"/>
			<label for="${organization.key}" class="selectable">${organization.value}</label>
		</c:forEach>
		</p>
		<p id="instances">
			<b>Choose instance</b>
			<p id="instancesForOrganization"></p>
        <p id="clusters">
           	<b>Choose cluster</b>
           	<p id="clustersForInstance">
		<p id="deployment">
			<form:label for="name" path="name"><b>Define name for the deployment</b>
			</form:label>
			<br />
			<form:input path="name" />
		</p>
		<p id="deployable">
			<form:label for="fileData" path="fileData"><b>Choose file to be uploaded</b></form:label>
			<br />
			<form:input path="fileData" type="file" />
		</p>
		<p id="submitButton">
			<input name="Deploy" type="submit"/>
		</p>		
	</form:form>

	<br /><br />

	<table id="deploymentTable"></table>
	<div id="deploymentPager"></div>
	
	<script type="text/javascript">
	
	var wizardStates = {init:0, organization:1, instance:2, cluster:3, deployment:4};
	Object.freeze(wizardStates);
	var currentState = wizardStates.init;
	
	$(document).ready(function() { 
		switchState(wizardStates.init);

		<%
		Map<Long, String> organizationMap = (Map)request.getAttribute("organizationMap");
		for (Map.Entry<Long,String> organization : organizationMap.entrySet()) {
		%>
		 $('#<%= organization.getKey()%>').click(function(){
			   $("#organizationId").attr("value","<%= organization.getKey()%>");
			   var url = '<portlet:resourceURL id="loadInstances"/>&organizationId=<%= organization.getKey()%>';
	 		   $.getJSON(url, function(data) {
	 			  	var clusters = '';
	 		   		$.each(data, function(key,val) {
	 		   			("#"+key);
	 		   			clusters += '<input id="'+key+'" name="'+key+'" type="checkbox" style="display:none;" value="1"/>';
	 					clusters += '<label for="'+key+'" class="selectable">'+val+'</label> ';
	 				});

	 		   		$("#instancesForOrganization").html(clusters);
	 		   		switchState(wizardStates.organization);
	 				$("#organizationId").css({backgroundColor: 'green'});
					$("#phase").html("Choose instance for deployment (2/6 steps).");
			   });		  	
			});
		<%
		}
		%>		
			
		$("#instancesForOrganization").on('click', function(event) {
			var target = $(event.target);
			if (target.is("#instancesForOrganization label")){
				var instanceId = target.prev().attr("name");
				$("#instanceId").attr("value", instanceId);
				var url = '<portlet:resourceURL id="loadClusters"/>&instanceId='+instanceId;
		 		$.getJSON(url, function(data) {
		 			var clusters = '';   
		 			$.each(data, function(key,val) {
	 		   			clusters += '<input id="'+key+'" name="'+key+'" value="0" style="display:none;"/>';
	 					clusters += '<input id="'+key+'" name="'+key+'" type="checkbox" style="display:none;" value="1"/>';
	 					clusters += '<label for="'+key+'" class="selectable">'+val+'</label>';
	 				});
	 		   		$("#clustersForInstance").html(clusters);
	 		   		switchState(wizardStates.instance);
	 			$("#phase").html("Choose cluster for deployment (3/6 steps).");
			   });	
			}
		});
		
		$("#clustersForInstance").on('click', function(event) { 
			var target = $(event.target);
			if (target.is("#clustersForInstance label")){
				var clusterId = target.prev().attr("name");
				$("#clusterId").attr("value", clusterId);
				$("#phase").html("Choose name for deployment (4/6 steps).");
	 		   	switchState(wizardStates.cluster);
			}
		});
		
		$("#deployment").on('change', function(event) { 
			$('#deployable').show("1000");
			$('#fileData').show("1000");
			$("#phase").html("Choose package for deployment (5/6 steps).");
		});
		
		$("#fileData").on('change', function(event) { 
			$("#phase").html("Deploy package (6/6 steps).");
			$('#submitButton').show("1000");
		});
			
		$(document).on("click", "#deploymentModel label.selectable",function() {
			$(this).siblings("label").removeClass("selected").addClass("selectable");
			$(this).removeClass("selectable").addClass("selected");
		});

		var deploymentColModel =[
        	{name:'organization',index:'organization', width:120, align:"center"},
        	{name:'organizationId',index:'organization', width:90, align:"center"},
        	{name:'instance',index:'instance', width:100, align:"center"},
        	{name:'instanceId',index:'instanceId', width:80, align:"center"},
        	{name:'cluster',index:'cluster', width:120, align:"center"},
        	{name:'clusterId',index:'clusterId', width:80, align:"center"},
        	{name:'name',index:'name', width:120, align:"center"},
        	{name:'state',index:'state', width:70, align:"center"},
        	{name:'formattedTime',index:'formattedTime', width:120, align:"center"}  
        ];
		var deploymentColNames = ['organization', 'organizationId','instance', 'instanceId', 'cluster', 'clusterId', 'name', 'state', 'time'];
		
		function loadTable(){
			jQuery("#deploymentTable").jqGrid({
				url:'<portlet:resourceURL id="loadDeploymentTable"/>',
				datatype: "json",
				jsonReader : {
					repeatitems : 	false,
					id: 			"organizationId",
					root : 			function(obj) {return obj.rows;},
					page : 			function(obj) {return obj.page;},
					total : 		function(obj) {return obj.total;},
					records : 		function(obj) {return obj.records;}
					},
				height: 250,
				width: 945,
			   	colNames : deploymentColNames,
			   	colModel : deploymentColModel,
			   	rowNum:10,
				pager: '#deploymentPager',
				sortname: 'id',
				viewrecords: true,
				sortorder: 'desc',
				shrinkToFit:false,
			   	caption: "Existing deployments"
			});
		$("#deploymentTable").jqGrid('navGrid','#deploymentPager',{edit:false,add:false,del:false});
		}
		
		loadTable();
	});
	
	function switchState(toState){
		switch (toState){
		case wizardStates.init:{
			hideAll();
			break;
		}
		case wizardStates.organization: {
			if (currentState == wizardStates.init){
				$("#instancesForOrganization").show("1000");
 				$('#instances').show("1000");
			}
			else clearOrganizations();
			break;
		}
		case wizardStates.instance: {
			if (currentState == wizardStates.organization){
				$("#clustersForInstance").show("1000");
 				$('#clusters').show("1000");
			}
			else clearClusters();
			break;
		}
		case wizardStates.cluster:{
			if (currentState == wizardStates.instance){
				$('#deployment').show("1000");
			}
			else clearClusters();
			break;
		}
		case wizardStates.deployment:
		default:
		}	
		currentState = toState;
	}

	function clearOrganizations(){
		clearClusters();
		$('#clusters').hide();
		$('#clustersForInstance').hide();	   		
	}

	function clearClusters(){
		$('#deployment').hide();
		$('#deployment input').val("");
		$('#deployable').hide();
		$('#fileData').val("");
		$('#submitButton').hide();		
	}
	
	function hideAll(){
		$('#instances').hide();
	    $('#clusters').hide();
	    $('#deployment').hide();
	   	$('#deployable').hide();
	   	$('#fileData').hide();
	    $('#submitButton').hide();
	    $('#confirmationDialog').hide();
	}	
	
</script>
</body>
</html>