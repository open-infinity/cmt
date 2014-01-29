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

var configurationElementPrefix = "configurationElement_";

// Creation 

function createPlatformSelectAccordion(dialog, data, machineTypes, identificationPrefix){
    dialog.accordion.accordion("destroy");
    dialog.accordion.empty();
	populateAccordion(dialog, data, machineTypes, identificationPrefix);
	dialog.accordion.accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
	$(".modulesAccordion").accordion({collapsible: true, autoHeight:false, heightStyle: "content"});
    //$(".modulesAccordionRow").fadeTo(0, ".5");
    disablePlatformRows(dialog.accordion);

	// Events 
	
	dialog.accordion.find(".togglePlatformSelectionRow :radio").change(function(e){
		handlePlatformSelectionChange($(this), data, identificationPrefix);
	});	
	
	dialog.accordion.find(".toggleEbsRow :radio").change(function(e) {
		handleEbsSelectionChange($(this));
	});

	dialog.accordion.find(".machineSizeRow :radio").change(function(e) {
		handleMachineSizeChange($(this), data);
	});

	dialog.accordion.find(".toggleParametersRow :radio").change(function(e) {
		handleParametersSelectionChange($(this));
	});
}

function populateAccordion(dialog, data, machineTypes, identificationPrefix){
	for(var i = 0; i < data.elements.length; i++){
		var template = $("#clusterConfigurationTemplate");
		var header = template.find(".accordionHeader").clone();
		var body = template.find(".elementConfigurationBody").clone();
		if(data.elements[i].element.replicated === true){
			var replicationClusterSizeRow = template.find(".elementConfigurationBody .clusterSizeRow").clone();
			var replicationMachineSizeRow = $("#clusterConfigurationTemplate .elementConfigurationBody .machineSizeRow").clone();
			replicationClusterSizeRow.attr('class', 'replicationClusterSizeRow configRow').find("label").html("Repl. cluster size");
			replicationMachineSizeRow.attr('class', 'machineSizeRow configRow replicationMachine');
			replicationMachineSizeRow.find('.radioButton').attr('class', 'replicationRadio radioButton');
			body.find(".machineSizeRow").after(replicationMachineSizeRow).after(replicationClusterSizeRow);
		}
		header.find(".elementTitle").html(data.elements[i].element.description);

        // Inserts "machine types" radio into body before element ids and names are set
		var machineTypeRadio = body.find('.machineSizeRow .radioButton');
		for (var mt = 0; mt < machineTypes.length; ++mt) {
			var machineTypeInstanceId = 'machineSizeRadio' + machineTypes[mt].name + '_';
			$('#machineTypeTemplate').children('[type="radio"]').clone().attr({id: machineTypeInstanceId, value: machineTypes[mt].id}).appendTo(machineTypeRadio);
			$('#machineTypeTemplate').children('label').clone().attr({'for': machineTypeInstanceId}).html(machineTypes[mt].name).appendTo(machineTypeRadio);
		}

        // Prepares element ids and names for accordion body radio buttons
        body.attr('id', configurationElementPrefix + data.elements[i].element.id).find('[type="radio"]').each(function () {
            var attribute = '';
            if($(this).parent().hasClass('replicationRadio')){
                attribute = 'replication_';
            }
            setIdentificationAttributes($(this), identificationPrefix, attribute, data.elements[i].element.id);
        });

        // For each module : insert module accordion html
        var modulesAccordion = body.find(".modulesAccordion");
        for(var j = 0; j < data.elements[i].modules.length; j++){

            // Insert accordion segment template
            var moduleAccordionHeader = $("#modulesAccordionTemplate").find(".accordionHeader").clone().appendTo(modulesAccordion);
            var moduleAccordionBody = $("#modulesAccordionTemplate").find(".modulesAccordionBody").clone().appendTo(modulesAccordion);

            // Set module title
            var moduleTitle = data.elements[j].modules[j].module.name + "-" + data.elements[j].modules[j].module.version;
            moduleAccordionHeader.find(".moduleTitle").text(moduleTitle);

            // For each module insert parameter keys into  a radio button
            for(var k = 0; k < data.elements[i].modules[j].parameters.length; k++){

                 // Create a "Off-On" radio button for key selection
                 var parameterRow = $("#parameterTemplate").find(".parameterRow").clone();
                 var radioButton = parameterRow.find(".radioButton");
                 var identifier = data.elements[i].element.id + "_" + data.elements[i].modules[j].module.id + "_" + data.elements[i].modules[j].parameters[k].key.id;
                 setIdentificationAttributes(radioButton.find("#toggleKeyOn_"), "", "", identifier);
                 setIdentificationAttributes(radioButton.find("#toggleKeyOff_"), "", "", identifier);

                 // Create and populate Parameter Key
                 var parameterKey = parameterRow.find(".parameterKey");
                 parameterKey.text(data.elements[i].modules[j].parameters[k].key.name);

                 // Create and populate Parameter Value for a Parameter Key
                 var parameterValue = parameterRow.find(".parameterValue");
                 var value = "";
                 for (var l = 0; l < data.elements[i].modules[j].parameters[k].values.length; l++){
                    if (value === ""){
                        value = data.elements[i].modules[j].parameters[k].values[0];
                    }
                    else{
                        value += ":" + data.elements[i].modules[j].parameters[k].values[0];
                    }
                 }
                 parameterValue.text(value);
                 parameterRow.appendTo(moduleAccordionBody);
            }

        }

        // Set default value for MachineType buttonset display
        if (data.machineTypes.length > 0)
        	dialog.dialog.find(".valueDisplayButtonSet").text(data.machineTypes[0].specification);


        // Set default initializeButtonsvalue for ParameterKeys buttonset display
        dialog.dialog.find(".parameterRadioButton").buttonset();

        // Set all platforms to be unselected by default - UI elements dimming
        dimAccordionElements($("#cloudTypesSelectionAccordion"));

		body.data('clusterConfiguration', data.elements[i]);
		dialog.accordion.append(header);
		dialog.accordion.append(body);
		dimAccordionElements(dialog.accordion);
	}
	dialog.dialog.find(".radioButton").buttonset();

	// Initialize UI elements
    initializeSliders(data.elements);
    initializeButtons(data, dialog.dialog);
}

function setIdentificationAttributes(item, prefix, optionalAttribute, identifier){
	item.attr('id', item.attr('id') + prefix + optionalAttribute + identifier);
	item.attr('name', item.attr('name') + prefix + optionalAttribute + identifier);
	var label = item.next("label");
	label.attr('for', label.attr('for') + prefix + optionalAttribute + identifier);
}

function initializeSliders(elements){
    for(var i = 0; i < elements.length; i++){
        var selector = '#' + configurationElementPrefix + elements[i].element.id;
        $(selector + ' .clusterSizeRow .jq_slider')
            .slider({
                min: elements[i].element.minMachines,
                max: elements[i].element.maxMachines,
                values: [elements[i].element.minMachines],
                slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
            .parent().next().text(elements[i].element.minMachines);
        $(selector + ' .ebsSizeRow .jq_slider')
            .slider({
                min: 1,
                max: 1000,
                values: [1],
                step: 10,
                slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
            .parent().next().text(1);
        if(elements[i].element.replicated == true){
            $(selector + ' .replicationClusterSizeRow .jq_slider')
                .slider({
                    max:elements[i].element.maxReplicationMachines,
                    min:elements[i].element.minReplicationMachines,
                    values: [elements[i].element.minReplicationMachines],
                    slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
                .parent().next().text(elements[i].element.minReplicationMachines);
        }
        // accordion header
        $(selector).prev().addClass("platformNotSelected").removeClass("platformSelected");
    }
}

function initializeButtons(data, accordion){
    accordion.find('.togglePlatformSelectionRow input[id*="togglePlatformRadioOff_"]').attr('checked',true).button("refresh");
    accordion.find('.imageTypeRow input[id*="imageTypeEphemeral_"]').attr('checked',true).button("refresh");
    accordion.find('.toggleEbsRow input[id*="toggleEbsRadioOff_"]').attr('checked',true).button("refresh");
    accordion.find('.toggleParametersRow input[id*="toggleParametersOff_"]').attr('checked',true).button("refresh");
    accordion.find('.parameterRow input[id*="toggleKeyOff_"]').attr('checked',true).button("refresh");
    if (data.machineTypes.length > 0) { // select first machine type and reset label if there are any machine types
        $("#addInstanceDialog .valueDisplayButtonSet").text(data.machineTypes[0].specification);
        $("#addInstanceDialog .machineSizeRow input:first-child").attr('checked',true).button("refresh");
    }
}

// Event handlers

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

function handleMachineSizeChange(item, data){
	item.parent().next().text(data.machineTypes[item.attr("value")].specification);
}

/*
 * Hides or shows  "parameterSelectInstruction" and module accordion
 */
function handleParametersSelectionChange(item){

    var parametersRow = item.parents(".toggleParametersRow").next();

	if (item.attr("id").indexOf("toggleParametersOff") !==  -1) {
        //item.parent().next().fadeTo(500, ".5");
        parametersRow.find(":radio").attr("disabled", true).button("refresh");
        parametersRow.fadeTo(500, ".5");
    }
    else{
        //item.parent().next().fadeTo(500, "1");
        parametersRow.fadeTo(500, "1");
        parametersRow.find(":radio").attr("disabled", false).button("refresh");
    }
}

function handlePlatformSelectionChange(item, data, prefix) {

	// Grab data from accordion segment's model (elementData)
	var elementData = item.parents(".ui-accordion-content").data('clusterConfiguration');

    // Handle selecting platform
    if (item.attr("id").indexOf("togglePlatformRadioOn") != -1) {
        doSelectPlatform(item, elementData);
    }

    // Handle deselecting platform
    else if (item.attr("id").indexOf("togglePlatformRadioOff") !=  -1) {
        doDeselectPlatform(item, elementData, data);
    }

    // Handle invalid state
    else{
        throw("Internal error. Radio button on unknown state.");
    }
}

function doSelectPlatform(item, elementData){
    togglePlatformSelection(item, "select", 0);
    for (var i = 0; i < elementData.dependees.length; i ++){
        var accordionSegment = fetchDependeeAccordionSegment(elementData.dependees[i]);

        // Make the dependee accordion segment selected
        var radioPlatformOn = accordionSegment.find('input[id*="togglePlatformRadioOn_"]');
        radioPlatformOn.attr('checked',true).button("refresh");
        togglePlatformSelection(radioPlatformOn, "select", 0);

        // Disable button, so that it is not possible to deselect the dependee platform
        accordionSegment.find('.togglePlatformSelectionRow :radio').attr("disabled", true).button("refresh");
    }
}

function doDeselectPlatform(item, elementData, data){
    togglePlatformSelection(item, "unselect", 0);
    for (var i = 0; i < elementData.dependees.length; i ++){
        var accordionSegment = fetchDependeeAccordionSegment(elementData.dependees[i]);

        // Check if some other platform also depends on the "dependent platform"
        var found = false;
        for(var j = 0; j < data.elements.length; j++){
            for(var k = 0; k < data.elements[j].dependees.length; k++){
                if (data.elements[j].element.id !== elementData.element.id && data.elements[j].dependees[k] === elementData.dependees[i]){
                     if  ($('#' + 'togglePlatformRadioOn_' + data.elements[i].element.id).attr('checked')) {
                        found  = true;
                        break;
                    } else continue;
                }
            }
            if (found === true){
                break;
            }
        }

        // Enable manual selection of dependent platform in case no other dependencies were found
        if (!found ) {
            accordionSegment.find('.togglePlatformSelectionRow :radio').attr("disabled", false).button("refresh");
        }
    }
}

function togglePlatformSelection(item, mode, delay){
    if (item.length === 0 || typeof(item) === 'undefined') return;
	var segment = item.parents(".ui-accordion-content");
	var header = segment.prev();
	if(mode === "select"){
		header.addClass("platformSelected", delay).removeClass("platformNotSelected");
		enablePlatformRows(segment);
    }
	else  if (mode === "unselect"){
		header.addClass("platformNotSelected").removeClass("platformSelected",delay);
		disablePlatformRows(segment);
	}
	return;
}

function enablePlatformRows(segment){
	segment.find(".configRow").fadeTo(500, "1");
	segment.find(".clusterSizeRow .jq_slider").slider({ disabled: false});
	segment.find(".replicationClusterSizeRow  .jq_slider").slider({ disabled: false});
	segment.find(".machineSizeRow :radio").attr("disabled", false).button("refresh");
	segment.find(".imageTypeRow :radio").attr("disabled", false).button("refresh");
	segment.find(".toggleEbsRow :radio").attr("disabled", false).button("refresh");
	if ($('#' + "toggleEbsRadioOn_" + segment.data('clusterConfiguration').element.id).attr('checked')){
		segment.find(".ebsSizeRow").fadeTo(500, "1");
		segment.find(".jq_slider").slider({ disabled: false });
	}
	if ($('#' + "toggleParametersOn_" + segment.data('clusterConfiguration').element.id).attr('checked')){
        segment.find(".modulesAccordionRow").fadeTo(500, "1");
        segment.find(".modulesAccordionRow :radio").attr("disabled", false).button("refresh");
    }
    segment.find(".toggleParametersRow :radio").attr("disabled", false).button("refresh");
}

function disablePlatformRows(segment){
	segment.find(".jq_slider").slider({ disabled: true});
	segment.find(".configRow").fadeTo(500, ".5");
	segment.find(".ebsSizeRow").fadeTo(500, ".5");
	segment.find(".modulesAccordionRow").fadeTo(500, ".5");
	segment.find(".machineSizeRow :radio").attr("disabled", true).button("refresh");
	segment.find(".imageTypeRow :radio").attr("disabled", true).button("refresh");
	segment.find(".toggleEbsRow :radio").attr("disabled", true).button("refresh");
	segment.find(".toggleParametersRow :radio").attr("disabled", true).button("refresh");
	segment.find(".modulesAccordionRow :radio").attr("disabled", true).button("refresh");
}

function fetchDependeeAccordionSegment(dependeeId){
    var dialog = $("#" +  configurationElementPrefix + dependeeId);
    if (dialog === null || typeof(dialog) === 'undefined'){
        throw("Internal error. Referred jQuery object does not exist.");
    }
    return dialog;
}


// Helper functions

function dimAccordionElements(item){
	item.find(".jq_slider").slider({ disabled: true }).css("opacity", "1");
	item.find(".clusterSizeRow").css("opacity", ".5");
	item.find(".configRow").css("opacity", ".5");
	item.find(".ebsSizeRow").css("opacity", ".5");
}

/*
function findElementById(id, data) {
	var matchedTypes = $.grep(data.elements, function(obj) {
		return obj.element.id === id;
	});
	if (matchedTypes.length !== 0)
		return matchedTypes[0];
}
*/

function getConfiguration(element, accordion){
    ret = null;
    return ret;
}

function prepareRequestParameters(elements, accordion, outData){
    for(var i = 0; i < elements.length; i++){
        var elementConfiguration = getConfiguration(elements[i], accordion);
        if (elementConfiguration !== null){
            outData.elements.push(elementConfiguration);
        }
    }
    return outData;

    /*
    var dc = cloudadmin.dialog.instance;
	var elements = cloudadmin.resource.elements;
	for(var i = 0; i < elements.length; i++){
		outData[elements[i].element.id]				  	= "false";
		outData[elements[i].element.id + "clustersize"] 	= 0;
		outData[elements[i].element.id + "machinesize"] 	= 0;
		outData[elements[i].element.id + "esb"] 		  	= "false";
		outData[elements[i].element.id + "volumesize"]  	= 0;
	}
	//outData["instanceid"] = dc.instanceId;
	for(i = 0; i < elements.length; i++){
		if($('#' + dc.idPrefix + "togglePlatformRadioOn_" + elements[i].element.id).attr('checked')){
			outData[elements[i].element.id] = "true";
			outData[elements[i].element.id + "clustersize"] =
				$('#' + dc.idPrefix + elements[i].element.id + ' .elementsizeRow .jq_slider').parent().next().text();
			outData[elements[i].element.id + "machinesize"] = machineSize(dc.idPrefix, elements[i].element.id, 1);

			if (elements[i].replicated === true){
				outData[elements[i].element.id + "replelementsize"] = $('#' + dc.idPrefix + elements[i].element.id +
					' .replicationelementsizeRow .jq_slider').parent().next().text();
				outData[elements[i].element.id + "replmachinesize"] = machineSize(dc.idPrefix, elements[i].element.id, 2);
			}
			if($('#' + dc.idPrefix + "imageTypeEphemeral_" + elements[i].element.id).attr('checked')){
				outData[elements[i].element.id + "imagetype"] = "0";
			}
			else
				outData[elements[i].element.id + "imagetype"] = "1";
			if($('#' + dc.idPrefix + "toggleEbsRadioOn_" + elements[i].element.id).attr('checked')){
				outData[elements[i].element.id + "esbvolumesize"] =
					$('#' + dc.idPrefix + elements[i].element.id + ' .ebsSizeRow .jq_slider').parent().next().text();
			}
		}
	}
	*/

}

function machineSize(prefix, clusterName, machineType){
	var ret = -1;
	var rowClass = machineType === tr1 ? ".ordinaryMachine": ".replicationMachine";
	$('#' + prefix + clusterName + ' ' +  rowClass + ' .radioButton input').each(function(){	
		if ($(this).attr('checked')){
			ret = $(this).attr("value");
			return false;
		}
	});
	if (ret === tr-1) throw "Machine size fetching failed";
	return ret;
}
	
