<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setBundle basename="cloudadmin"/>

<%-- Cluster settings template, used in instance creation dialog --%>
<div id="clusterConfigurationTemplate" class="template">
	<h3 class ="accordionHeader platformNotSelected">
		<label class ="elementTitle"></label>
	</h3>
	<div class="elementConfigurationBody">
		<div class="togglePlatformSelectionRow">
			<label class="platformLabel instanceCreationLabel">Select platform</label>
			<div class="radioButton">
				<input type="radio" id="togglePlatformRadioOn_" name="togglePlatformRadio_"/>
				<label for="togglePlatformRadioOn_"> On </label>
				<input type="radio" id="togglePlatformRadioOff_" name="togglePlatformRadio_" checked="checked" />
				<label for="togglePlatformRadioOff_"> Off </label>
			</div>
		</div>
		<div class="clusterSizeRow configRow">
			<label class="clusterSizeLabel instanceCreationLabel">Cluster size </label>
			<div class="jq_slider_wrapper">
				<div class="jq_slider"></div>
			</div>
			<div class="valueDisplay valueDisplaySlider"></div>
		</div>
		<div class="machineSizeRow configRow ordinaryMachine">
			<label class="machineSizeLabel instanceCreationLabel">Machine size </label>
			<div class="radioButton">
			</div>
			<div class="valueDisplay valueDisplayButtonSet"></div>
		</div>
		<div class="imageTypeRow configRow">
			<label class="toggleEbsLabel instanceCreationLabel">Image type</label>
			<div class="radioButton">
				<input type="radio" id="imageTypeEphemeral_" name="imageType_" checked="checked"/>
				<label for="imageTypeEphemeral_"> Ephemeral </label>
				<input type="radio" id="imageTypeEbs_" name="imageType_"/>
				<label for="imageTypeEbs_"> EBS </label>
			</div>
		</div>
		<div class="toggleEbsRow configRow">
			<label class="toggleEbsLabel instanceCreationLabel">EBS volume</label>
			<div class="radioButton">
				<input type="radio" id="toggleEbsRadioOn_" name="toggleEbsRadio_" />
				<label for="toggleEbsRadioOn_"> On </label>
				<input type="radio" id="toggleEbsRadioOff_" name="toggleEbsRadio_" checked="checked"/>
				<label for="toggleEbsRadioOff_"> Off </label>
			</div>
		</div>
		<div class="ebsSizeRow">
			<label class="ebsSizeLabel instanceCreationLabel">EBS volume size</label>
			<div class="jq_slider_wrapper">
				<div class="jq_slider ui_disabled"></div>
			</div>
			<div class="valueDisplay valueDisplaySlider"></div>
		</div>
		<div class="toggleParametersRow configRow">
            <label class="parametersLabel instanceCreationLabel">Parameters</label>
            <div class="radioButton parametersRadio">
                <input type="radio" id="toggleParametersOn_" name="toggleParameters_" />
                <label for="toggleParametersOn_"> On </label>
                <input type="radio" id="toggleParametersOff_" name="toggleParameters_" checked="checked"/>
                <label for="toggleParametersOff_"> Off </label>
            </div>
        </div>
        <div class="modulesAccordionRow">
            <div class="parameterSelectInstruction">Select parameters for installation modules</div>
            <div class="modulesAccordion"></div>
        </div>
	</div>
</div>

<%-- template for module accordion element --%>
<div id="modulesAccordionTemplate" class="template">
    <h3 class ="accordionHeader">
        <label class ="moduleTitle"></label>
    </h3>
    <div class="modulesAccordionBody"></div>
</div>

<%-- template for parameter row --%>
<div id="parameterTemplate" class="template">
    <div class="parameterRow">
        <%--<div class="parameterKeyTitle">Keys</div>
        <div class="parameterValueTitle">Values</div>--%>
        <div class="parameterKey"></div>
        <div class="radioButton parameterKeySelectRadio">
            <input type="radio" id="toggleKeyOn_" name="toggleKey_" />
            <label for="toggleKeyOn_">On</label>
            <input type="radio" id="toggleKeyOff_" name="toggleKey_" checked="checked"/>
            <label for="toggleKeyOff_">Off</label>
        </div>
        <div class="parameterValue"></div>
    </div>
</div>

<%-- template for single machine type button --%>
<div id="machineTypeTemplate" class="template">
    <input type="radio" id="machineSizeRadio" name="machineSizeRadio_" value=""/>
    <label for="machineSizeRadio"></label>
</div>

<%-- Cluster template --%>
<ul id="clusterTemplate" class="template">
	<li class="newlist ui-corner-all">
		<a href="#" class="drag-handle" style="float: left;"></a>
		<span></span>
		<div class="panelinfo_container">
			<a href="#" class="ui-icon ui-icon-circle-triangle-e" style="margin: 2px;">Cluster Options</a>
			<div class="panel_container">
				<ul class="options_panel">
					<li><a href="#">Information</a></li>
					<li><a href="#">Configure</a></li>
					<li><a href="#">Scale</a></li>
					<li><a href="#">Delete</a></li>
				</ul>
			</div>
			<div class="machineinfocontainer">
				<div class="machineinfo runningmachine"><span name="runningmachine"></span></div>
				<div class="machineinfo startingmachine"><span name="startingmachine"></span></div>
				<div class="machineinfo errormachine"><span name="errormachine"></span></div>
				<div class="machineinfo machinecount"><span name="totalmachine"></span></div>
			</div>
		</div>
	</li>
</ul>

<%-- Cluster scale template --%>
<div id="scaleClusterTemplate" class="template">
	<form>
		<div class="dialog_fieldset">
			<%-- Manual provisioning settings --%>
			<div class="manual_provisioning">
				<input class="manual_provisioning_checkbox scale_cluster_checkbox" type="checkbox"/>
				<div class="scale_cluster_title">Manual provisioning</div>
			</div>
			<div class="manual_provisioning_settings">
				<div class="manual_scale_slider">
					<div class="scale_cluster_label">Cluster size</div>
					<div class="mb_slider"></div>
				</div>
			</div>

			<%-- Automatic provisioning settings --%>
			<div class="automatic_provisioning">
				<input class="automatic_provisioning_checkbox scale_cluster_checkbox" type="checkbox"/>
				<div class="scale_cluster_title">Automatic provisioning</div>
			</div>
			<div class="automatic_provisioning_settings">
				<div class="cluster_size_range_select_slider">
					<div class="scale_cluster_label scale_cluster_range_label">Cluster size range:</div>
					<div class="range_select_display"></div>
					<div class="jq_slider"></div>
				</div>
				<div class="cpu_load_range_select_slider">
					<div class="scale_cluster_label scale_cluster_range_label">System load range:</div>
					<div class="range_select_display"></div>
					<div class="jq_slider"></div>
				</div>
			</div>

		<%-- Scheduled scaling settings --%>
			<input class="scehduled_scale_checkbox scale_cluster_checkbox" type="checkbox">
			<div class="scale_cluster_title"> Schedule cluster scaling</div>
			<br />
			<div class="scale_scheduler_datetime_picker_elements">
				<div class="scale_cluster_range_label_period"> Period</div>
				<div class="scale_cluster_range_picker">
					<div class="scale_cluster_picker_label"> From </div>
					<input class="scale_scheduler_datetime_picker_from scale_scheduler_datetime_picker">
					<div class="scale_cluster_picker_label"> To </div>
					<input class="scale_scheduler_datetime_picker_to scale_scheduler_datetime_picker">
				</div>
				<div class="scheduled_size_slider">
					<div class="scale_cluster_label"> Cluster size  </div>
					<div class="mb_slider"> </div>
				</div>
			</div>
		</div>
	</form>
</div>

<%-- Add service template --%>
<div id="addServiceTemplate" class="template">
	<p class="validatetips"><fmt:message key="cloud.dialog.info.allFields" /></p>
	<form>
		<fieldset class="dialogFieldset">
			<select name="serviceSelect"></select>

			<div class="serviceSliders">
				<div name="serviceslider" style="display:inline-block;position:relative;">
					<div name="serviceMachineCountSlider" class="mb_slider" style="display:inline-block;float:left"></div>
					<div name="serviceMachineSizeSelect" class="vmenu" style="display:inline-block;"></div>
				</div>

				<div name="replicationslider" style="display:inline-block;position:relative;">
					<div name="replicationSizeSlider" class="mb_slider" style="display:inline-block;float:left"></div>
					<div name="replicationMachineSizeSelect" class="vmenu" style="display:inline-block;"></div>
				</div>
			</div>
		</fieldset>
	</form>
</div>


<%-- Cluster config template --%>
<div id="configClusterTemplate" class="template">
	<div class="clusterConfigureContent">
		<div class="elasticIpConfiguration">
				<label class="elasticIpSelectLabel clusterConfigureTitle"></label>
				<select class="elasticIpSelect"></select>
				<label class="elasticIpLabel"></label>
				<div class="elasticIpStatusBar"> Working...</div>

				<div class="elasticIpFiller"></div>
				<a href="#" class="elasticIpUseButton">+</a>
				<a href="#" class="elasticIpDropButton">-</a>


				<!--input class="dropElasticIpCheckBox" type="checkbox"></input-->
				<!--label class="dropElasticIpLabel clusterConfigureTitle">Drop elastic IP from cluster</label-->
				<!--p class= elasticIpLabel></p-->
			<!--p class="stickySession">
				<input class="useStickySessionsCheckBox" type="checkbox"></input>
				<label class="useStickySessionsLabel clusterConfigureTitle"> Use sticky sessions</label>
			</p-->
		</div>
		<div class="newConnectionOpening">
			<label class="connectionsOpenTitle clusterConfigureTitle" >Add ingress rule </label>
			<div class = labelsRow>
				<label class="ipCidrConfig">IPv4 CIDR</label>
				<label class="portsConfig">Ports</label>
				<label class="protocolConfig">Protocol</label>
			</div>
			<div class="inputRow">
				<input type="text" class="ipAddressInput" placeholder="IP address">
				<select class="cidrPrefixSelect">
					 <option selected>32</option>
				</select>
				<input type="text" class="portFromInput" placeholder="From">
				<div style ="display: inline-block;">-</div>
				<input type="text" class="portToInput" placeholder="To">
				<select class="protocolSelect">
					 <!--option value='' disabled selected style='display:none;'>Protocol</option-->
					 <option selected>tcp</option>
					 <option>udp</option>
					 <option>icmp</option>
				</select>
				<a href="#" class="addConnectionButton ipButton">+</a>
			</div>
		</div>
		<div class="openConnectionsList">
			<div class = "openConnectionsListLabel">Existing ingress rules:</div>
		</div>
	</div>
</div>

