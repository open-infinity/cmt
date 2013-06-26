<%--

/*
 * Copyright (c) 2013 the original author or authors.
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
@since 1.2.0

 --%>

<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<portlet:defineObjects />
<portlet:actionURL var="action" />

<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>


<html>
<head>
<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>Cloud Properties</title>
<style>
label {
	background: white;
	width: 300px;
}
</style>
</head>
<body>
	<form:form modelAttribute="sharedPropertyModel" action="${action}" method="post">
		<input id="instanceId" name="instanceId" type="hidden" />
		<input id="organizationId" name="organizationId" type="hidden" />
		<input id="clusterId" name="clusterId" type="hidden" />
		<p id="phase">Choose organization for property (1/6 steps).</p>
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
		<p id="key">
			<form:label for="key" path="key"><b>Define the shared key</b>
			</form:label>
			<br />
			<form:input path="key" />
		</p>           	
		<p id="value">
			<form:label for="value" path="value"><b>Define the shared value for the key</b>
			</form:label>
			<br />
			<form:input path="value" />
		</p>
		<p id="submitButton">
			<input name="Submit" type="submit"/>
		</p>		
	</form:form>

	<br /><br />

	<table id="propertyTable"></table>
	<div id="propertyPager"></div>
<!-- 	<br><br/> <a href="#" id="undeploy_property">Undeploy property</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="#" id="redeploy_property">Redeploy property</a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="#" id="delete_property">Delete property</a> <br />	 -->
	<fmt:setBundle basename="clouddeployer"/>
	<div class="ui-button-bar">
		<button id="deleteSharedProperty"><spring:message code="cloudproperties.mainview.button.delete.property"/></button>
	</div>
	
<script type="text/javascript">
	
	var wizardStates = {init:0, organization:1, instance:2, cluster:3, key:4, value:5};
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
					$("#phase").html("Choose instance for property (2/6 steps).");
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
	 			$("#phase").html("Choose cluster for property (3/6 steps).");
			   });	
			}
		});
		
		$("#clustersForInstance").on('click', function(event) { 
			var target = $(event.target);
			if (target.is("#clustersForInstance label")){
				var clusterId = target.prev().attr("name");
				$("#clusterId").attr("value", clusterId);
				$("#phase").html("Choose type of property (4/6 steps).");
	 		   	switchState(wizardStates.cluster);
			}
		});

		$("#key").on('change', function(event) { 
			$('#value').show("1000");
			$("#phase").html("Choose key for property (5/6 steps).");
		});
		
		$("#value").on('change', function(event) { 
			$('#submitButton').show("1000");
			$("#phase").html("Choose value for property (6/6 steps).");
		});
			
		$(document).on("click", "#sharedPropertyModel label.selectable", function() {
			$(this).siblings("label").removeClass("selected").addClass("selectable");
			$(this).removeClass("selectable").addClass("selected");
		});
		
		var propertyColModel =[
		    {name:'id',index:'id', width:40, align:"center"},
        	{name:'organization',index:'organization', width:120, align:"center"},
        	{name:'organizationId',index:'organizationId', width:60, align:"center"},
        	{name:'instance',index:'instance', width:100, align:"center"},
        	{name:'instanceId',index:'instanceId', width:60, align:"center"},
        	{name:'cluster',index:'cluster', width:120, align:"center"},
        	{name:'clusterId',index:'clusterId', width:60, align:"center"},
        	{name:'key',index:'key', width:90, align:"center"},
        	{name:'value',index:'key', width:120, align:"center"},
        	{name:'formattedTime',index:'formattedTime', width:120, align:"center"}  
        ];
		var propertyColNames = ['id', 'organization', 'organizationId','instance', 'instanceId', 'cluster', 'clusterId', 'key', 'value', 'formattedTime'];
		
		function loadTable() {
			jQuery("#propertyTable").jqGrid({
				url:'<portlet:resourceURL id="loadPropertiesTable"/>',
				datatype: "json",
				jsonReader : {
					repeatitems : 	false,
					id: 			"id",
					root : 			function(obj) {return obj.rows;},
					page : 			function(obj) {return obj.page;},
					total : 		function(obj) {return obj.total;},
					records : 		function(obj) {return obj.records;}
					},
				height: 250,
				width: 945,
			   	colNames : propertyColNames,
			   	colModel : propertyColModel,
			   	rowNum:10,
				pager: '#propertyPager',
				sortname: 'id',
				viewrecords: true,
				sortorder: 'desc',
				shrinkToFit:false,
				ondblClickRow: function(id){ alert("You double click row with id: "+id);},				
			   	caption: "Existing properties"
			});
			$("#propertyTable").jqGrid('navGrid','#propertyPager',{edit:false,add:false,del:false});
		}
		
		jQuery("#deleteSharedProperty").button().click( function(){ 
			var id = jQuery("#propertyTable").jqGrid('getGridParam','selrow'); 
			if (id) { 	
			    var url = '<portlet:resourceURL id="deleteProperty"/>&propertyId='+id;
			    $.getJSON(url, function(data) {
			    	loadTable();
			    });
			    $('#propertyTable').trigger( 'reloadGrid' );
			}
		}); 
		
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
				$('#key').show("1000");
			}
			else clearClusters();
			break;
		}
		case wizardStates.key:
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
		$('#property').hide();
		$('#property input').val("");
		$('#submitButton').hide();		
	}
	
	function hideAll(){
		$('#instances').hide();
	    $('#clusters').hide();
	    $('#key').hide();
	    $('#value').hide();
	    $('#submitButton').hide();
	    $('#confirmationDialog').hide();
	}	
	
</script>
</body>
</html>