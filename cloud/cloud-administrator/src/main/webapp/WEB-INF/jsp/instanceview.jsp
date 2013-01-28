<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="cloudadmin"/>

<div id="instanceviewtemplate" class="cloudadmincontent">

	<div class="servicecontainer servicenew" data-pub-id="3">
		<h3 class="ui-helper-reset ui-accordion-header ui-state-active ui-corner-top">
			<span class="ui-space"></span>
			<a href="#"><fmt:message key="cloud.instanceview.title.unpublished" /></a>
		</h3>
		
		<div id="dropnew" class="ui-helper-reset ui-corner-bottom">
			<ul id="newlist" class="ui-helper-reset ui-helper-clearfix"></ul>	
		</div>
	</div>

	<div class="servicecontainer servicepublic" data-pub-id="1">
		<h3 class="ui-helper-reset ui-accordion-header ui-state-active ui-corner-top">
			<span class="ui-space"></span>
			<a href="#"><fmt:message key="cloud.instanceview.title.public" /></a>
		</h3>
		
		<div id="droppublic" class="droppable ui-corner-bottom">
			<ul id="publiclist" class="ui-helper-reset ui-helper-clearfix"></ul>	
		</div>
	</div>

	<div class="servicecontainer serviceprivate" data-pub-id="2">
		<h3 class="ui-helper-reset ui-accordion-header ui-state-active ui-corner-top">
			<span class="ui-space"></span>
			<a href="#"><fmt:message key="cloud.instanceview.title.private" /></a>
		</h3>
		
		<div id="dropprivate" class="droppable ui-corner-bottom" >
			<ul id="privatelist" class="ui-helper-reset ui-helper-clearfix"></ul>	
		</div>
	</div>

	<div class="ui-button-bar">
		<button class="add-services"><fmt:message key="cloud.instanceview.button.add" /></button>	
		<button class="view-machines"><fmt:message key="cloud.instanceview.button.machines" /></button>	
		<button class="get-key"><fmt:message key="cloud.instanceview.button.getkey" /></button>	
	</div>

</div>


