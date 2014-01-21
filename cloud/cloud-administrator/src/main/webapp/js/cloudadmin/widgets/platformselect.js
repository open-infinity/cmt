/**
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
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.1
 */

// TODO: Make a truly stand-alone widget for this

// Creation 

function createPlatformSelectAccordion(container, elements, machineTypes, identificationPrefix){
	populateAccordion(container, elements, machineTypes, identificationPrefix);
	$("#cloudTypesSelectionAccordion").accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
	
	// Events 
	
	$("#cloudTypesSelectionAccordion").find(".togglePlatformSelectionRow :radio").change(function(e){
		handlePlatformSelectionChange($(this), identificationPrefix);
	});	
	
	$("#cloudTypesSelectionAccordion").find(".toggleEbsRow :radio").change(function(e) {
		handleEbsSelectionChange($(this));
	});

	$("#cloudTypesSelectionAccordion").find(".machineSizeRow :radio").change(function(e) {
		handleMachineSizeChange($(this));
	});	 	
}

function populateAccordion(container, elements, machineTypes, identificationPrefix){
	for(var i = 0; i < elements.length; i++){
		var template = $("#clusterConfigurationTemplate");
		var header = template.find(".clusterTypeConfigurationHeader").clone();
		var body = template.find(".clusterTypeConfigurationBody").clone();	
		if(elements[i].replicated == true){
			var replicationClusterSizeRow = template.find(".clusterTypeConfigurationBody .clusterSizeRow").clone();
			var replicationMachineSizeRow = $("#clusterConfigurationTemplate .clusterTypeConfigurationBody .machineSizeRow").clone();		
			replicationClusterSizeRow.attr('class', 'replicationClusterSizeRow configRow').find("label").html("Repl. cluster size");
			replicationMachineSizeRow.attr('class', 'machineSizeRow configRow replicationMachine');
			replicationMachineSizeRow.find('.radioButton').attr('class', 'replicationRadio radioButton');	
			body.find(".machineSizeRow").after(replicationMachineSizeRow).after(replicationClusterSizeRow);
		}				
		header.find(".clusterTypeTitle").html(elements[i].description);

        // Inserts machine types into body before element ids and names are set
		var machineTypeRadio = body.find('.machineSizeRow .radioButton');
		for (var mt = 0; mt < machineTypes.length; ++mt) {
			var machineTypeInstanceId = 'machineSizeRadio' + machineTypes[mt].name + '_';
			$('#machineTypeTemplate').children('[type="radio"]').clone().
				attr({id: machineTypeInstanceId, value: machineTypes[mt].id}).appendTo(machineTypeRadio);
			$('#machineTypeTemplate').children('label').clone().
				attr({'for': machineTypeInstanceId}).html(machineTypes[mt].name).appendTo(machineTypeRadio);
		}
		// Prepares element ids and names
		body.attr('id', identificationPrefix + elements[i].name).find('[type="radio"]').each(function () {
			var attribute = '';
			if($(this).parent().hasClass('replicationRadio')){
				attribute = 'replication_';
			}
			setIdentificationAttributes($(this), elements[i].name, identificationPrefix, attribute);
		});
		
		// Set default value for button set display
		container.dialog.find(".radioButton").buttonset();
        if (cloudadmin.resource.machineTypes.length > 0)
        	container.dialog.find(".valueDisplayButtonSet").text(cloudadmin.resource.machineTypes[0].specification);
        
        // Set all platforms to be unselected by default - UI elements dimming 
        dimAccordionElements($("#cloudTypesSelectionAccordion"));
        
		body.data('clusterConfiguration', cloudadmin.resource.elements[i]);
		$("#cloudTypesSelectionAccordion").append(header);
		$("#cloudTypesSelectionAccordion").append(body);
		dimAccordionElements($("#cloudTypesSelectionAccordion"));
	} 
}

function setIdentificationAttributes(item, clusterName, idPrefix, optionalAttribute){
	item.attr('id', idPrefix + optionalAttribute + item.attr('id') + clusterName);
	item.attr('name', idPrefix + optionalAttribute + item.attr('name') + clusterName);
	var label = item.next("label");
	label.attr('for', idPrefix + optionalAttribute + label.attr('for') + clusterName);
}


// Event handlers

function handlePlatformSelectionChange(item, prefix) {
	var requiredClusterType = null;
	var dependentPlatformContainer = null;
	var grandpa = item.parents(".ui-accordion-content");
	var dependency = grandpa.data('clusterConfiguration').dependency;	
	
	if  (dependency != -1) {
		requiredClusterType = findClusterTypeById(dependency);
		dependentPlatformContainer = $("#" +  prefix + requiredClusterType.name);
	}
	
	if (item.attr("id").indexOf("togglePlatformRadioOn") != -1) {
		toggleGrandunclesClass(item, "select", 0);
		if  (dependency != -1) {
			var radioPlatformOn = dependentPlatformContainer.find('input[id*="togglePlatformRadioOn_"]');
			radioPlatformOn.attr('checked',true).button("refresh");
			toggleGrandunclesClass(radioPlatformOn, "select", 0);
			dependentPlatformContainer.find('.togglePlatformSelectionRow :radio').attr("disabled", true).button("refresh");
		}
	}
	else if (item.attr("id").indexOf("togglePlatformRadioOff") !=  -1) {
		toggleGrandunclesClass(item, "unselect", 0);
		if  (dependency != -1){		
			// Check if some other platform also depends on the "dependent platform"
			var myId = grandpa.data('clusterConfiguration').id;
			var found = false;
			for(var i = 0; i < cloudadmin.resource.elements.length; i++){
				if (cloudadmin.resource.elements[i].dependency == dependency  &&
					cloudadmin.resource.elements[i].id != myId){
					 if  ($('#' + prefix + 'togglePlatformRadioOn_' + cloudadmin.resource.elements[i].name).attr('checked')) {
						found  = true;
						break;
					} else continue;
				}
			}
			// Enable manual selection of dependent platform in case no other dependencies were found
			if (!found ) dependentPlatformContainer.find('.togglePlatformSelectionRow :radio').
				attr("disabled", false).button("refresh");
		}
	}
	item.siblings().attr('checked',false).button("refresh");
	item.attr('checked',true).button("refresh");
}	

function handleEbsSelectionChange(item){
	var sliderRow = item.parent().parent().next();
	var jqSlider = sliderRow.find(".jq_slider");
	
	if (item.attr("id").indexOf("toggleEbsRadioOff") !=  -1) {
		sliderRow.fadeTo(500, ".5");
		jqSlider.slider({ disabled: true});		
	}
	else{
		sliderRow.fadeTo(500, "1");
		jqSlider.slider({ disabled: false});		
	}
}

function handleMachineSizeChange(item){
	item.parent().next().text(cloudadmin.resource.machineTypes[item.attr("value")].specification);
}


// Helper functions

function dimAccordionElements(item){
	item.find(".jq_slider").slider({ disabled: true }).css("opacity", "1");
	item.find(".clusterSizeRow").css("opacity", ".5");
	item.find(".configRow").css("opacity", ".5");
	item.find(".ebsSizeRow").css("opacity", ".5");
} 

function findClusterTypeById(clusterId) {
	var matchedTypes = $.grep(cloudadmin.resource.elements, function(obj) {
		return obj.id == clusterId;	
	});
	if (matchedTypes.length != 0)
		return matchedTypes[0];
}

function toggleGrandunclesClass(item, mode, delay){
	var grandpa = item.parents(".ui-accordion-content");
	var granduncle = grandpa.prev();
	if(mode === "select"){
		granduncle.addClass("platformSelected",delay).removeClass("platformNotSelected");
		enableGrandpaElements(grandpa);}
		
	else  if (mode === "unselect"){
		granduncle.addClass("platformNotSelected").removeClass("platformSelected",delay);
		disableGrandpaElements(grandpa);
	}
	return;
}

function enableGrandpaElements(grandpa){
	grandpa.find(".configRow").fadeTo(500, "1");
	grandpa.find(".clusterSizeRow .jq_slider").slider({ disabled: false});	
	grandpa.find(".replicationClusterSizeRow  .jq_slider").slider({ disabled: false});	
	grandpa.find(".machineSizeRow :radio").attr("disabled", false).button("refresh");
	grandpa.find(".imageTypeRow :radio").attr("disabled", false).button("refresh");
	grandpa.find(".toggleEbsRow :radio").attr("disabled", false).button("refresh");

	if ($('#' + "toggleEbsRadioOn_" + grandpa.data('clusterConfiguration').name).attr('checked')){
		grandpa.find(".ebsSizeRow").fadeTo(500, "1");	
		grandpa.find(".jq_slider").slider({ disabled: false });	
	}
}

function disableGrandpaElements(grandpa){
	grandpa.find(".jq_slider").slider({ disabled: true});		
	grandpa.find(".configRow").fadeTo(500, ".5");
	grandpa.find(".ebsSizeRow").fadeTo(500, ".5");	
	grandpa.find(".machineSizeRow :radio").attr("disabled", true).button("refresh");
	grandpa.find(".imageTypeRow :radio").attr("disabled", true).button("refresh");
	grandpa.find(".toggleEbsRow :radio").attr("disabled", true).button("refresh");
}

function prepareRequestParameters(outData, dc){
	var elements = cloudadmin.resource.elements;
	for(var i = 0; i < elements.length; i++){
		outData[elements[i].name]				  	= "false";
		outData[elements[i].name + "elementsize"] 	= 0;
		outData[elements[i].name + "machinesize"] 	= 0;
		outData[elements[i].name + "esb"] 		  	= "false";
		outData[elements[i].name + "volumesize"]  	= 0;
	}
	outData["instanceid"] = dc.instanceId;
	for(var i = 0; i < elements.length; i++){
		if($('#' + dc.idPrefix + "togglePlatformRadioOn_" + elements[i].name).attr('checked')){
			outData[elements[i].name] = "true";
			outData[elements[i].name + "elementsize"] = 
				$('#' + dc.idPrefix + elements[i].name + ' .elementsizeRow .jq_slider').parent().next().text();
			outData[elements[i].name + "machinesize"] = machineSize(dc.idPrefix, elements[i].name, 1);
			
			if (elements[i].replicated == true){
				outData[elements[i].name + "replelementsize"] = $('#' + dc.idPrefix + elements[i].name + 
					' .replicationelementsizeRow .jq_slider').parent().next().text();
				outData[elements[i].name + "replmachinesize"] = machineSize(dc.idPrefix, elements[i].name, 2);
			}
			if($('#' + dc.idPrefix + "imageTypeEphemeral_" + elements[i].name).attr('checked')){
				outData[elements[i].name + "imagetype"] = "0";
			}
			else
				outData[elements[i].name + "imagetype"] = "1";
			if($('#' + dc.idPrefix + "toggleEbsRadioOn_" + elements[i].name).attr('checked')){
				outData[elements[i].name + "esbvolumesize"] = 
					$('#' + dc.idPrefix + elements[i].name + ' .ebsSizeRow .jq_slider').parent().next().text();
			}
		}
	}
}

function machineSize(prefix, clusterName, machineType){
	var ret = -1;
	var rowClass = machineType == 1 ? ".ordinaryMachine": ".replicationMachine";
	$('#' + prefix + clusterName + ' ' +  rowClass + ' .radioButton input').each(function(){	
		if ($(this).attr('checked')){
			ret = $(this).attr("value");
			return false;
		}
	});
	if (ret == -1) throw "Machine size fetching failed";
	return ret;
}
	
