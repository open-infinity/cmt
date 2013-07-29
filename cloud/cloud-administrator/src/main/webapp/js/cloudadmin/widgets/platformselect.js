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
 * @version 1.2.1 
 * @since 1.2.1
 */

// Creation 

function populateAccordion(element, clusterTypes, machineTypes, identificationPrefix){
	for(var i = 0; i < clusterTypes.length; i++){
		var template = $("#clusterConfigurationTemplate");
		var header = template.find(".clusterTypeConfigurationHeader").clone();
		var body = template.find(".clusterTypeConfigurationBody").clone();	
		if(clusterTypes[i].replicated == true){
			var replicationClusterSizeRow = template.find(".clusterTypeConfigurationBody .clusterSizeRow").clone();
			var replicationMachineSizeRow = $("#clusterConfigurationTemplate .clusterTypeConfigurationBody .machineSizeRow").clone();		
			replicationClusterSizeRow.attr('class', 'replicationClusterSizeRow configRow').find("label").html("Repl. cluster size");
			replicationMachineSizeRow.attr('class', 'machineSizeRow configRow replicationMachine');
			replicationMachineSizeRow.find('.radioButton').attr('class', 'replicationRadio radioButton');	
			body.find(".machineSizeRow").after(replicationMachineSizeRow).after(replicationClusterSizeRow);
		}				
		header.find(".clusterTypeTitle").html(clusterTypes[i].title);

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
		body.attr('id', identificationPrefix + clusterTypes[i].name).find('[type="radio"]').each(function () {
			var attribute = '';
			if($(this).parent().hasClass('replicationRadio')){
				attribute = 'replication_';
			}
			setIdentificationAttributes($(this), clusterTypes[i].name, identificationPrefix, attribute);
		});
		
		// Set default value for button set display
		element.dialog.find(".radioButton").buttonset();
        if (cloudadmin.resource.machineTypes.length > 0)
        	element.dialog.find(".valueDisplayButtonSet").text(cloudadmin.resource.machineTypes[0].specification);
        
        // Set all platforms to be unselected by default - UI elements dimming 
        dimAccordionElements(element.accordion);
        
		body.data('clusterConfiguration', cloudadmin.resource.clusterTypes[i]);
		element.accordion.append(header);
		element.accordion.append(body);		
	} 
}

function setIdentificationAttributes(element, clusterName, idPrefix, optionalAttribute){
	element.attr('id', idPrefix + optionalAttribute + element.attr('id') + clusterName);
	element.attr('name', idPrefix + optionalAttribute + element.attr('name') + clusterName);
	var label = element.next("label");
	label.attr('for', idPrefix + optionalAttribute + label.attr('for') + clusterName);
}


// Event handlers

function handlePlatformSelectionChange(element, prefix) {
	var requiredClusterType = null;
	var dependentPlatformContainer = null;
	var grandpa = element.parents(".ui-accordion-content");
	var dependency = grandpa.data('clusterConfiguration').dependency;	
	
	if  (dependency != -1) {
		requiredClusterType = findClusterTypeById(dependency);
		dependentPlatformContainer = $("#" +  prefix + requiredClusterType.name);
	}
	
	if (element.attr("id").indexOf("togglePlatformRadioOn") != -1) {
		toggleGrandunclesClass(element, "select", 0);
		if  (dependency != -1) {
			var radioPlatformOn = dependentPlatformContainer.find('input[id*="togglePlatformRadioOn_"]');
			radioPlatformOn.attr('checked',true).button("refresh");
			toggleGrandunclesClass(radioPlatformOn, "select", 0);
			dependentPlatformContainer.find('.togglePlatformSelectionRow :radio').attr("disabled", true).button("refresh");
		}
	}
	else if (element.attr("id").indexOf("togglePlatformRadioOff") !=  -1) {
		toggleGrandunclesClass(element, "unselect", 0);
		if  (dependency != -1){		
			// Check if some other platform also depends on the "dependent platform"
			var myId = grandpa.data('clusterConfiguration').id;
			var found = false;
			for(var i = 0; i < cloudadmin.resource.clusterTypes.length; i++){
				if (cloudadmin.resource.clusterTypes[i].dependency == dependency  &&
					cloudadmin.resource.clusterTypes[i].id != myId){
					 if  ($('#' + prefix + 'togglePlatformRadioOn_' + cloudadmin.resource.clusterTypes[i].name).attr('checked')) {
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
	element.siblings().attr('checked',false).button("refresh");
	element.attr('checked',true).button("refresh");		
}	

function handleEbsSelectionChange(element){
	var sliderRow = element.parent().parent().next();
	var jqSlider = sliderRow.find(".jq_slider");
	
	if (element.attr("id").indexOf("toggleEbsRadioOff") !=  -1) {
		sliderRow.fadeTo(500, ".5");
		jqSlider.slider({ disabled: true});		
	}
	else{
		sliderRow.fadeTo(500, "1");
		jqSlider.slider({ disabled: false});		
	}
}

function handleMachineSizeChange(element){
	element.parent().next().text(cloudadmin.resource.machineTypes[element.attr("value")].specification);
}


// Helper functions

function dimAccordionElements(element){
	element.find(".jq_slider").slider({ disabled: true }).css("opacity", "1");
	element.find(".clusterSizeRow").css("opacity", ".5");
	element.find(".configRow").css("opacity", ".5");	
	element.find(".ebsSizeRow").css("opacity", ".5");
} 

function findClusterTypeById(clusterId) {
	var matchedTypes = $.grep(cloudadmin.resource.clusterTypes, function(obj) {
		return obj.id == clusterId;	
	});
	if (matchedTypes.length != 0)
		return matchedTypes[0];
}

function toggleGrandunclesClass(element, mode, delay){
	var grandpa = element.parents(".ui-accordion-content");
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
	var clusters = cloudadmin.resource.clusterTypes;
	for(var i = 0; i < clusters.length; i++){
		outData[clusters[i].name]				  	= "false";
		outData[clusters[i].name + "clustersize"] 	= 0;
		outData[clusters[i].name + "machinesize"] 	= 0;
		outData[clusters[i].name + "esb"] 		  	= "false";
		outData[clusters[i].name + "volumesize"]  	= 0;
	}
	outData["instanceid"] = dc.instanceId;
	for(var i = 0; i < clusters.length; i++){
		if($('#' + dc.idPrefix + "togglePlatformRadioOn_" + clusters[i].name).attr('checked')){
			outData[clusters[i].name] = "true";
			outData[clusters[i].name + "clustersize"] = 
				$('#' + dc.idPrefix + clusters[i].name + ' .clusterSizeRow .jq_slider').parent().next().text();
			outData[clusters[i].name + "machinesize"] = machineSize(dc.idPrefix, clusters[i].name, 1);
			
			if (clusters[i].replicated == true){
				outData[clusters[i].name + "replclustersize"] = $('#' + dc.idPrefix + clusters[i].name + 
					' .replicationClusterSizeRow .jq_slider').parent().next().text();
				outData[clusters[i].name + "replmachinesize"] = machineSize(dc.idPrefix, clusters[i].name, 2);
			}
			if($('#' + dc.idPrefix + "imageTypeEphemeral_" + clusters[i].name).attr('checked')){
				outData[clusters[i].name + "imagetype"] = "0";
			}
			else
				outData[clusters[i].name + "imagetype"] = "1";
			if($('#' + dc.idPrefix + "toggleEbsRadioOn_" + clusters[i].name).attr('checked')){
				outData[clusters[i].name + "esbvolumesize"] = 
					$('#' + dc.idPrefix + clusters[i].name + ' .ebsSizeRow .jq_slider').parent().next().text();
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
	
