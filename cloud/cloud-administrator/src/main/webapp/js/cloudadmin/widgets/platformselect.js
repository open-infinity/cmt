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

function createPlatformSelectAccordion(container, data, machineTypes, identificationPrefix){
    container.accordion.accordion("destroy");
    container.accordion.empty();
	populateAccordion(container, data, machineTypes, identificationPrefix);
	container.accordion.accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
	$(".modulesAccordion").accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
    $(".modulesAccordionRow").fadeTo(0, ".5");

	// Events 
	
	container.accordion.find(".togglePlatformSelectionRow :radio").change(function(e){
		handlePlatformSelectionChange($(this), identificationPrefix);
	});	
	
	container.accordion.find(".toggleEbsRow :radio").change(function(e) {
		handleEbsSelectionChange($(this));
	});

	container.accordion.find(".machineSizeRow :radio").change(function(e) {
		handleMachineSizeChange($(this));
	});

	container.accordion.find(".toggleParametersRow :radio").change(function(e) {
		handleParametersSelectionChange($(this));
	});
}

function populateAccordion(container, data, machineTypes, identificationPrefix){
	for(var i = 0; i < data.length; i++){
		var template = $("#clusterConfigurationTemplate");
		var header = template.find(".accordionHeader").clone();
		var body = template.find(".elementConfigurationBody").clone();
		if(data[i].element.replicated === true){
			var replicationClusterSizeRow = template.find(".elementConfigurationBody .clusterSizeRow").clone();
			var replicationMachineSizeRow = $("#clusterConfigurationTemplate .elementConfigurationBody .machineSizeRow").clone();
			replicationClusterSizeRow.attr('class', 'replicationClusterSizeRow configRow').find("label").html("Repl. cluster size");
			replicationMachineSizeRow.attr('class', 'machineSizeRow configRow replicationMachine');
			replicationMachineSizeRow.find('.radioButton').attr('class', 'replicationRadio radioButton');	
			body.find(".machineSizeRow").after(replicationMachineSizeRow).after(replicationClusterSizeRow);
		}				
		header.find(".elementTitle").html(data[i].element.description);

        // Inserts "machine types" radio into body before element ids and names are set
		var machineTypeRadio = body.find('.machineSizeRow .radioButton');
		for (var mt = 0; mt < machineTypes.length; ++mt) {
			var machineTypeInstanceId = 'machineSizeRadio' + machineTypes[mt].name + '_';
			$('#machineTypeTemplate').children('[type="radio"]').clone().attr({id: machineTypeInstanceId, value: machineTypes[mt].id}).appendTo(machineTypeRadio);
			$('#machineTypeTemplate').children('label').clone().attr({'for': machineTypeInstanceId}).html(machineTypes[mt].name).appendTo(machineTypeRadio);
		}

        // Prepares element ids and names for accordion body radio buttons
        body.attr('id', configurationElementPrefix + data[i].element.id).find('[type="radio"]').each(function () {
            var attribute = '';
            if($(this).parent().hasClass('replicationRadio')){
                attribute = 'replication_';
            }
            setIdentificationAttributes($(this), identificationPrefix, attribute, data[i].element.id);
        });

        // For each module : insert module accordion html
        var modulesAccordion = body.find(".modulesAccordion");
        for(var j = 0; j < data[i].modules.length; j++){

            // insert accordion segment template
            var moduleAccordionHeader = $("#modulesAccordionTemplate").find(".accordionHeader").clone().appendTo(modulesAccordion);
            var moduleAccordionBody = $("#modulesAccordionTemplate").find(".modulesAccordionBody").clone().appendTo(modulesAccordion);

            // set module title
            var moduleTitle = data[j].modules[j].module.name + "-" + data[j].modules[j].module.version;
            moduleAccordionHeader.find(".moduleTitle").text(moduleTitle);

            // for each module insert parameter keys into  a radio button
            for(var k = 0; k < data[i].modules[j].parameters.length; k++){

                 // create a "Off-On" radio button for key selection
                 var parameterRow = $("#parameterTemplate").find(".parameterRow").clone();
                 //var radioButton = parameterRow.find(".radioButton").clone();
                 var radioButton = parameterRow.find(".radioButton");
                 var identifier = data[i].element.id + "_" + data[i].modules[j].module.id + "_" + data[i].modules[j].parameters[k].key.id;
                 setIdentificationAttributes(radioButton.find("#toggleKeyOn_"), "", "", identifier);
                 setIdentificationAttributes(radioButton.find("#toggleKeyOff_"), "", "", identifier);
                 //radioButton.appendTo(moduleAccordionBody);

                 // create and populate Parameter Key
                 var parameterKey = parameterRow.find(".parameterKey");
                 //parameterKey.text(data[i].modules[j].parameters[k].key.name).appendTo(moduleAccordionBody);
                 parameterKey.text(data[i].modules[j].parameters[k].key.name);

                 // create and populate Parameter Value for a Parameter Key
                 var parameterValue = parameterRow.find(".parameterValue");
                 var value = "";
                 for (var l = 0; l < data[i].modules[j].parameters[k].values.length; l++){
                    if (value === ""){
                        value = data[i].modules[j].parameters[k].values[0];
                    }
                    else{
                        value += ":" + data[i].modules[j].parameters[k].values[0];
                    }
                 }
                 //parameterValue.text(value).appendTo(moduleAccordionBody);
                 parameterValue.text(value);
                 parameterRow.appendTo(moduleAccordionBody);
            }

        }

        // Set default value for MachineType buttonset display
		//container.dialog.find(".radioButton").buttonset();
		//container.dialog.find(".radioButton", "parametersRow").buttonset();
        if (cloudadmin.resource.machineTypes.length > 0)
        	container.dialog.find(".valueDisplayButtonSet").text(cloudadmin.resource.machineTypes[0].specification);


        // Set default value for ParameterKeys buttonset display
        container.dialog.find(".parameterRadioButton").buttonset();
        //if (cloudadmin.resource.machineTypes.length > 0)
        //    container.dialog.find(".parameterValue").text(data[i].modules[j].parameters[0].key.name);

        // Hide parameter selection instruction text
        $(".parameterSelectInstruction").fadeTo(0, ".5");;

        // Set all platforms to be unselected by default - UI elements dimming
        dimAccordionElements($("#cloudTypesSelectionAccordion"));
        
		body.data('clusterConfiguration', cloudadmin.resource.elements[i]);
		container.accordion.append(header);
		container.accordion.append(body);
		dimAccordionElements(container.accordion);
	}
	container.dialog.find(".radioButton").buttonset();
}

function setIdentificationAttributes(item, prefix, optionalAttribute, identifier){
	item.attr('id', item.attr('id') + prefix + optionalAttribute + identifier);
	item.attr('name', item.attr('name') + prefix + optionalAttribute + identifier);
	var label = item.next("label");
	label.attr('for', label.attr('for') + prefix + optionalAttribute + identifier);
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

function handleMachineSizeChange(item){
	item.parent().next().text(cloudadmin.resource.machineTypes[item.attr("value")].specification);
}

/*
 * Hides or shows  "parameterSelectInstruction" and module accordion
 */
function handleParametersSelectionChange(item){

    var parametersRow = item.parents(".toggleParametersRow").next();

	if (item.attr("id").indexOf("toggleParametersOff") !==  -1) {
        item.parent().next().fadeTo(500, ".5");
        parametersRow.find(":radio").attr("disabled", true).button("refresh");
        parametersRow.fadeTo(500, ".5");
    }
    else{
        item.parent().next().fadeTo(500, "1");
        parametersRow.fadeTo(500, "1");
        parametersRow.find(":radio").attr("disabled", false).button("refresh");
    }
}

function handlePlatformSelectionChange(item, prefix) {

	// grab data from accordion segment's model (data)
	var data = item.parents(".ui-accordion-content").data('clusterConfiguration');

    // handle selecting platform
    if (item.attr("id").indexOf("togglePlatformRadioOn") != -1) {
        doSelectPlatform(item, data);
    }

    // handle deselecting platform
    else if (item.attr("id").indexOf("togglePlatformRadioOff") !=  -1) {
        doDeselectPlatform(item, data);
    }

    // handle invalid state
    else{
        throw("Internal error. Radio button on unknown state.");
    }
}

function doSelectPlatform(item, data){
    togglePlatformSelection(item, "select", 0);
    for (var i = 0; i < data.dependees.length; i ++){
        var accordionSegment = fetchDependeeAccordionSegment(data.dependees[i]);

        // Make the dependee accordion segment selected
        var radioPlatformOn = accordionSegment.find('input[id*="togglePlatformRadioOn_"]');
        radioPlatformOn.attr('checked',true).button("refresh");
        togglePlatformSelection(radioPlatformOn, "select", 0);

        // Disable button, so that it is not possible to deselect the dependee platform
        accordionSegment.find('.togglePlatformSelectionRow :radio').attr("disabled", true).button("refresh");
    }
}

function doDeselectPlatform(item, data){
    togglePlatformSelection(item, "unselect", 0);
    for (var i = 0; i < data.dependees.length; i ++){
        var accordionSegment = fetchDependeeAccordionSegment(data.dependees[i]);

        // check if some other platform also depends on the "dependent platform"
        var found = false;
        for(var j = 0; j < cloudadmin.resource.elements.length; j++){
            for(var k = 0; k < cloudadmin.resource.elements[j].dependees.length; k++){
                if (cloudadmin.resource.elements[j].element.id !== data.element.id && cloudadmin.resource.elements[j].dependees[k] === data.dependees[i]){
                     if  ($('#' + prefix + 'togglePlatformRadioOn_' + cloudadmin.resource.elements[i].element.id).attr('checked')) {
                        found  = true;
                        break;
                    } else continue;
                }
            }
            if (found === true){
                break;
            }
        }

        // enable manual selection of dependent platform in case no other dependencies were found
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
    var container = $("#" +  configurationElementPrefix + dependeeId);
    if (container === null || typeof(container) === 'undefined'){
        throw("Internal error. Referred jQuery object does not exist.");
    }
    return container;
}


// Helper functions

function dimAccordionElements(item){
	item.find(".jq_slider").slider({ disabled: true }).css("opacity", "1");
	item.find(".clusterSizeRow").css("opacity", ".5");
	item.find(".configRow").css("opacity", ".5");
	item.find(".ebsSizeRow").css("opacity", ".5");
}

function findElementById(id) {
	var matchedTypes = $.grep(cloudadmin.resource.elements, function(obj) {
		return obj.element.id === id;
	});
	if (matchedTypes.length !== 0)
		return matchedTypes[0];
}

function prepareRequestParameters(outData){
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
	
