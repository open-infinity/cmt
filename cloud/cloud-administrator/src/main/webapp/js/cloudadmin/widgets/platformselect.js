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


function createPlatformSelectAccordion(dialog, data){

    if($.isEmptyObject(data) || $.isEmptyObject(data.elements) || $.isEmptyObject(data.machineTypes)) {
        console.log("Unable to populate UI - configuration elements not available");
        return;
    }

    dialog.accordion.accordion("destroy");
    dialog.accordion.empty();
	populateAccordion(dialog, data);
	dialog.accordion.accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
	$(".modulesAccordion").accordion({collapsible: true, autoHeight:false, heightStyle: "content"});
    //$(".modulesAccordionRow").fadeTo(0, ".5");
    disablePlatformRows(dialog.accordion);

	// Events 
	
	dialog.accordion.find(".togglePlatformSelectionRow :radio").change(function(e){
		handlePlatformSelectionChange($(this), data, dialog.identificationPrefix);
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

function deletePlatformSelectAccordion(accordion){
     accordion.accordion("destroy");
     accordion.empty();
}

function populateAccordion(dialog, data){
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
		for (var mt = 0; mt < data.machineTypes.length; ++mt) {
			var machineTypeInstanceId = 'machineSizeRadio' + data.machineTypes[mt].name + '_';
			$('#machineTypeTemplate').children('[type="radio"]').clone().attr({id: machineTypeInstanceId, value: data.machineTypes[mt].id}).appendTo(machineTypeRadio);
			$('#machineTypeTemplate').children('label').clone().attr({'for': machineTypeInstanceId}).html(data.machineTypes[mt].name).appendTo(machineTypeRadio);
		}

        // Prepares element ids and names for accordion body radio buttons
        body.attr('id', configurationElementPrefix + dialog.idPrefix + data.elements[i].element.id).find('[type="radio"]').each(function () {
            var attribute = '';
            if($(this).parent().hasClass('replicationRadio')){
                attribute = 'replication_';
            }
            //setIdentificationAttributes($(this), dialog.identificationPrefix, attribute, data.elements[i].element.id);
            setIdentificationAttributes($(this), dialog.idPrefix, attribute, data.elements[i].element.id);
        });

        // For each module : insert module accordion html
        var modulesAccordion = body.find(".modulesAccordion");
        for(var j = 0; j < data.elements[i].modules.length; j++){

            // Insert accordion segment template
            var moduleAccordionHeader = $("#modulesAccordionTemplate").find(".accordionHeader").clone().appendTo(modulesAccordion);
            var moduleAccordionBody = $("#modulesAccordionTemplate").find(".modulesAccordionBody").clone().appendTo(modulesAccordion);

            // Set module title
            var moduleTitle = data.elements[i].modules[j].module.name + "-" + data.elements[i].modules[j].module.version;
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
    initializeSliders(data, configurationElementPrefix + dialog.idPrefix);
    initializeButtons(data, dialog.dialog);
}

function setIdentificationAttributes(item, prefix, optionalAttribute, identifier){
	item.attr('id', item.attr('id') + prefix + optionalAttribute + identifier);
	item.attr('name', item.attr('name') + prefix + optionalAttribute + identifier);
	var label = item.next("label");
	label.attr('for', label.attr('for') + prefix + optionalAttribute + identifier);
}

function initializeSliders(data, identifier){
    for(var i = 0; i < data.elements.length; i++){
        var selector = '#' + identifier + data.elements[i].element.id;
        $(selector + ' .clusterSizeRow .jq_slider')
            .slider({
                min: data.elements[i].element.minMachines,
                max: data.elements[i].element.maxMachines,
                values: [data.elements[i].element.minMachines],
                slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
            .parent().next().text(data.elements[i].element.minMachines);
        $(selector + ' .ebsSizeRow .jq_slider')
            .slider({
                min: 1,
                max: 1000,
                values: [1],
                step: 10,
                slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
            .parent().next().text(1);
        if(data.elements[i].element.replicated === true){
            $(selector + ' .replicationClusterSizeRow .jq_slider')
                .slider({
                    max:data.elements[i].element.maxReplicationMachines,
                    min:data.elements[i].element.minReplicationMachines,
                    values: [data.elements[i].element.minReplicationMachines],
                    slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
                .parent().next().text(data.elements[i].element.minReplicationMachines);
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
        accordion.find(".valueDisplayButtonSet").text(data.machineTypes[0].specification);
        accordion.find(".machineSizeRow input:first-child").attr('checked',true).button("refresh");
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
        var accordionSegment = fetchDependeeAccordionSegment(elementData.dependees[i], elementData.idPrefix);

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
        var accordionSegment = fetchDependeeAccordionSegment(elementData.dependees[i], elementData.idPrefix);

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

function fetchDependeeAccordionSegment(dependeeId, prefix){
    var dialog = $("#" +  configurationElementPrefix + prefix + dependeeId);
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

function prepareRequestData(elements, outData, prefix, accordion){
    for(i = 0; i < elements.length; i++){
        if($('#' + "togglePlatformRadioOn_" + prefix + elements[i].element.id).attr('checked')){

            // Configuration Element settings chosen by user
            var configuration = {
                element : {id: elements[i].element.id, name: elements[i].element.name, type: elements[i].element.type},
                cluster: {size: 0},
                machine: {size: 0},
                replication: {on: false, cluster: {size: 0}, machine: {size: 0}},
                imageType: 1,
                ebs: {on: false, size: 0},
                parameters: {on: false, keys: []}
            };

            // Cluster and machine size
            configuration.cluster.size = $('#' + configurationElementPrefix + prefix +elements[i].element.id + ' .clusterSizeRow .jq_slider').parent().next().text();
            configuration.machine.size = getMachineSize(configurationElementPrefix + prefix + elements[i].element.id, 1);

            // Replication
            if (elements[i].element.replicated === true){
                configuration.replication.on = true;
                configuration.replication.cluster.size = $('#' + configurationElementPrefix + prefix + elements[i].element.id +' .replicationClusterSizeRow .jq_slider').parent().next().text();
                configuration.replication.machine.size = getMachineSize(configurationElementPrefix + prefix + elements[i].element.id, 2);
            }

            // Image type
            if($('#' + "imageTypeEphemeral_" + prefix + elements[i].element.id).attr('checked')){
                configuration.imageType = 0;
            }

            // EBS volume
            if($('#' + "toggleEbsRadioOn_" + prefix + elements[i].element.id).attr('checked')){
                configuration.ebs.on = true;
                configuration.ebs.size = $('#' + configurationElementPrefix + prefix + elements[i].element.id + ' .ebsSizeRow .jq_slider').parent().next().text();
            }

            // Parameters
            if($('#' + "toggleParametersOn_" + prefix + elements[i].element.id).attr('checked')){
                configuration.parameters.on = true;

                // For each element.module
                for(j = 0; j < elements[i].modules.length; j++){

                    // For each element.module.parameter
                    for(k = 0; k < elements[i].modules[j].parameters.length; k++){

                        // Read key selection
                        var checked = ($('#' + "toggleKeyOn_" + prefix + elements[i].element.id + "_"  + elements[i].modules[j].module.id + "_" + elements[i].modules[j].parameters[k].key.id).attr('checked'));

                        // Store key id,
                        if (checked){
                            configuration.parameters.keys.push(elements[i].modules[j].parameters[k].key.id);
                        }
                    }
                }

                // Push placeholder into empty array to make Spring Jackson parser happy
                if (configuration.parameters.keys.length === 0){
                    configuration.parameters.keys.push("-1");
                }
            }
            else{

                // Push placeholder into empty array to make Spring Jackson parser happy
                configuration.parameters.keys.push("0");
            }
            outData.configurations.push(configuration);
        }
    }
}

function getMachineSize(identifier, machineType){
	var ret = -1;
	var rowClass = machineType === 1 ? ".ordinaryMachine": ".replicationMachine";
	$('#' + identifier + ' ' +  rowClass + ' .radioButton input').each(function(){
		if ($(this).attr('checked')){
			ret = $(this).attr("value");

			// returning false from each() breaks the $.each() loop
			return false;
		}
	});
	if (ret === -1) throw "Machine size fetching failed";
	return ret;
}
	
