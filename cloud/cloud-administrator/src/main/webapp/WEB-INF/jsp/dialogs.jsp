<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:setBundle basename="cloudadmin"/>

<%-- instance dialogs --%>
<div id="addInstanceDialog" title="<fmt:message key="cloud.instancedialog.title.addNew" />">
	<div class="dialogInstructions">
        <label class="dialogInstructionsLine1"><fmt:message key="cloud.dialog.info.fillIn" /></label>
        <label class="dialogInstructionsLine2"><fmt:message key="cloud.dialog.info.allForm" /></label>
	</div>
	<div class="ui-state-error ui-corner-all addInstanceDialogError">
		<p>
			<span class="ui-icon ui-icon-alert" style="float: left; margin-right: .7em;"></span> 
			<strong><fmt:message key="cloud.instancedialog.error.fieldvalue" /> </strong>
 		</p>
	</div>
	<div class="configSegment">
	    <div class="cloudConfigRow">
		    <label for="instanceName" class="subtitleLabel"><fmt:message key="cloud.instancedialog.title.name" /></label>
		    <input type="text" id="instanceName" class="instanceName text ui-corner-all"/>
		</div>
	    <div class="cloudConfigRow">
            <label for="cloudType" class="subtitleLabel"><fmt:message key="cloud.instancedialog.title.cloud" /></label>
            <select id="cloudSelect" class="instanceConfigurationSelect">
                <option selected></option>
            </select>
		</div>
	    <div class="cloudConfigRow">
		    <label for="zoneName" class="subtitleLabel"><fmt:message key="cloud.instancedialog.title.zone" /></label>
		    <select id="zoneSelect" class="instanceConfigurationSelect"></select>
		</div>
        <div>
            <label for="template" class="subtitleLabel"><fmt:message key="cloud.instancedialog.title.template" /></label>
            <select id="addInstanceTemplateSelect" class="templateSelect instanceConfigurationSelect"></select>
        </div>
	</div>
	<div class="configSegment">
        <div class="subtitleLabel"><fmt:message key = "cloud.instancedialog.title.available"/></div>
        <p class="dialogInstructionsLine1"><fmt:message key = "cloud.instancedialog.info.selectServices"/></p>
        <div id="cloudTypesSelectionAccordion"></div>
    </div>
</div>

<div id="deleteInstanceConfirmDialog" title="<fmt:message key="cloud.instancedialog.title.delete" />">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0:"></span><fmt:message key="cloud.instancedialog.info.deleteInstance" /></p>
</div>


<%-- add service dialog --%>
<div id="addServicesDialog" title="<fmt:message key="cloud.servicedialog.title.addNew" />">
    <div class="dialogInstructions">
        <label class="dialogInstructionsLine1"><fmt:message key="cloud.dialog.info.addService" /></label>
	</div>
    <div class="configSegment">
        <label for="template" class="subtitleLabel"><fmt:message key="cloud.instancedialog.title.template" /></label>
        <select id="addServicesTemplateSelect" class="templateSelect instanceConfigurationSelect"></select>
    </div>
    <div class="configSegment">
        <div class="subtitleLabel"><fmt:message key = "cloud.instancedialog.title.available"/></div>
        <p class="dialogInstructionsLine1"><fmt:message key = "cloud.instancedialog.info.selectServices"/></p>
        <div id="addServicesAccordion"></div>
    </div>
</div>


<%-- cluster dialogs --%>
<div id="clusterdialog" title="<fmt:message key="cloud.clusterdialog.title.clusterInfo" />">
	<table id="clusterdatatable" class="clusterdata">
		<colgroup>
    		<col class="info" />
    		<col class="data" />
    	</colgroup>
		<thead>
			<tr>
				<th>Information</th>
				<th></th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
	<br />
	<table id="clusterstatustable" class="clusterdata">
		<colgroup>
    		<col class="info" />
    		<col class="data" />
    	</colgroup>
		<thead>
			<tr>
				<th>Status</th>
				<th></th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>

<%-- Delete cluster dialog --%>
<div id="deleteClusterConfirmDialog" title="<fmt:message key="cloud.clusterdialog.title.deleteCluster" />">
	<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span><fmt:message key="cloud.clusterdialog.info.deleteConfirm" /></p>
</div>

<%-- Scale cluster dialog --%>
<div id="scaleClusterDialog" title="<fmt:message key="cloud.clusterdialog.title.scaleCluster" />">
</div>

<%-- Configure cluster dialog --%>
<div id="configureClusterDialog" title="<fmt:message key="cloud.clusterdialog.title.configureCluster" />">
</div>

<%-- Machine information dialog --%>
<div id="machineListDialog" title="Instance machines">
	<table id="machines"></table>
	<div id="machinepager"></div>
</div>

<div id="machineDialog" title="Machine information">
	<table id="machinedatatable" width="100%" border="1" cellpadding="5">
	</table>
</div>
