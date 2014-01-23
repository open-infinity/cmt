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

function createPlatformSelectAccordion(container, data, machineTypes, identificationPrefix){
    container.accordion.accordion("destroy");
    container.accordion.empty();
	populateAccordion(container, data, machineTypes, identificationPrefix);
	container.accordion.accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
	$(".modulesAccordion").accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});

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
}

function populateAccordion(container, data, machineTypes, identificationPrefix){
	for(var i = 0; i < data.length; i++){
		var template = $("#clusterConfigurationTemplate");
		var header = template.find(".clusterTypeConfigurationHeader").clone();
		var body = template.find(".clusterTypeConfigurationBody").clone();
		if(data[i].element.replicated === true){
			var replicationClusterSizeRow = template.find(".clusterTypeConfigurationBody .clusterSizeRow").clone();
			var replicationMachineSizeRow = $("#clusterConfigurationTemplate .clusterTypeConfigurationBody .machineSizeRow").clone();		
			replicationClusterSizeRow.attr('class', 'replicationClusterSizeRow configRow').find("label").html("Repl. cluster size");
			replicationMachineSizeRow.attr('class', 'machineSizeRow configRow replicationMachine');
			replicationMachineSizeRow.find('.radioButton').attr('class', 'replicationRadio radioButton');	
			body.find(".machineSizeRow").after(replicationMachineSizeRow).after(replicationClusterSizeRow);
		}				
		header.find(".clusterTypeTitle").html(data[i].element.description);

        // Inserts machine types radio into body before element ids and names are set
		var machineTypeRadio = body.find('.machineSizeRow .radioButton');
		for (var mt = 0; mt < machineTypes.length; ++mt) {
			var machineTypeInstanceId = 'machineSizeRadio' + machineTypes[mt].name + '_';
			$('#machineTypeTemplate').children('[type="radio"]').clone().
				attr({id: machineTypeInstanceId, value: machineTypes[mt].id}).appendTo(machineTypeRadio);
			$('#machineTypeTemplate').children('label').clone().
				attr({'for': machineTypeInstanceId}).html(machineTypes[mt].name).appendTo(machineTypeRadio);
		}
        var modulesAccordion = body.find(".modulesAccordion");
        // For each module : insert module accordion html

        /*
        <div id="modulesAccordionTemplate" class="template">
            <h3 class ="modulesAccordionHeader">
                <label class ="moduleTitle"></label>
            </h3>
            <div class="modulesAccordionRow">
                <label class="parameterTitleLabel">Keys</label>
                <div class="parameterRadioButton"></div>
                <label class="parameterTitleLabel">Value for key</label>
                <div class="parameterValue"></div>
            </div>
        </div>
        */

        for(var j = 0; j < data[i].modules.length; j++){

            // insert accordion segment template
            // var segment = $("#modulesAccordionTemplate").clone().appendTo(modulesAccordion);

            var moduleAccordionHeader = $("#modulesAccordionTemplate").find(".modulesAccordionHeader").clone().appendTo(modulesAccordion);
            var moduleAccordionBody = $("#modulesAccordionTemplate").find(".modulesAccordionRow").clone().appendTo(modulesAccordion);

            // populate template with modules
            var moduleTitle = data[j].modules[j].module.name + "-" + data[j].modules[j].module.version;
            moduleAccordionHeader.find(".moduleTitle").text(moduleTitle);

            // for each module insert parameter keys into  a radio button
            var parameterRadioButton = moduleAccordionBody.find(".parameterRadioButton");
            var parameterTemplate = $('#parameterKeyTemplate');
            for(var k = 0; k < data[i].modules[j].parameters.length; k++){
                var keyId = 'keySelectRadio' + data[i].modules[j].parameters[k].key.name + '_';
                parameterTemplate.children('[type="radio"]').clone().attr({id: keyId, value: data[i].modules[j].parameters[k].key.id}).appendTo(parameterRadioButton);
                parameterTemplate.children('label').clone().attr({'for': keyId}).html(data[i].modules[j].parameters[k].key.name).appendTo(parameterRadioButton);
            }
        }

        // Prepares element ids and names
		body.attr('id', identificationPrefix + data[i].element.id).find('[type="radio"]').each(function () {
			var attribute = '';
			if($(this).parent().hasClass('replicationRadio')){
				attribute = 'replication_';
			}
			setIdentificationAttributes($(this), data[i].element.name, identificationPrefix, attribute);
		});

        // Set default value for MachineType buttonset display
		container.dialog.find(".radioButton").buttonset();
        if (cloudadmin.resource.machineTypes.length > 0)
        	container.dialog.find(".valueDisplayButtonSet").text(cloudadmin.resource.machineTypes[0].specification);


        // Set default value for ParameterKeys buttonset display
        container.dialog.find(".parameterRadioButton").buttonset();
        //if (cloudadmin.resource.machineTypes.length > 0)
        //    container.dialog.find(".parameterValue").text(data[i].modules[j].parameters[0].key.name);

        // Set all platforms to be unselected by default - UI elements dimming
        dimAccordionElements($("#cloudTypesSelectionAccordion"));
        
		body.data('clusterConfiguration', cloudadmin.resource.elements[i]);
		container.accordion.append(header);
		container.accordion.append(body);
		dimAccordionElements(container.accordion);
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
	var requiredElement = null;
	var dependentPlatformContainer = null;
	var grandpa = item.parents(".ui-accordion-content");
	var dependees = grandpa.data('clusterConfiguration').dependees;

	for (var = 0; i < dependees.length; i ++){
            dependeeElement = findElementById(dependees);
            dependeeElementContainer = $("#" +  prefix + requiredElement.element.name);

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
                    if (cloudadmin.resource.elements[i].element.dependency === dependency  &&
                        cloudadmin.resource.elements[i].element.id != myId){
                         if  ($('#' + prefix + 'togglePlatformRadioOn_' + cloudadmin.resource.elements[i].element.name).attr('checked')) {
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
    }
	item.siblings().attr('checked',false).button("refresh");
	item.attr('checked',true).button("refresh");

	/*
	if  (typeof dependees != 'undefined') {
    		requiredElement = findElementById(dependees);
    		dependentPlatformContainer = $("#" +  prefix + requiredElement.element.name);
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
    				if (cloudadmin.resource.elements[i].element.dependency === dependency  &&
    					cloudadmin.resource.elements[i].element.id != myId){
    					 if  ($('#' + prefix + 'togglePlatformRadioOn_' + cloudadmin.resource.elements[i].element.name).attr('checked')) {
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
    	*/

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

function findElementById(id) {
	var matchedTypes = $.grep(cloudadmin.resource.elements, function(obj) {
		return obj.element.id === id;
	});
	if (matchedTypes.length !== 0)
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

function prepareRequestParameters(outData){
    var dc = cloudadmin.dialog.instance;
	var elements = cloudadmin.resource.elements;
	for(var i = 0; i < elements.length; i++){
		outData[elements[i].element.name]				  	= "false";
		outData[elements[i].element.name + "clustersize"] 	= 0;
		outData[elements[i].element.name + "machinesize"] 	= 0;
		outData[elements[i].element.name + "esb"] 		  	= "false";
		outData[elements[i].element.name + "volumesize"]  	= 0;
	}
	//outData["instanceid"] = dc.instanceId;
	for(i = 0; i < elements.length; i++){
		if($('#' + dc.idPrefix + "togglePlatformRadioOn_" + elements[i].element.name).attr('checked')){
			outData[elements[i].element.name] = "true";
			outData[elements[i].element.name + "clustersize"] =
				$('#' + dc.idPrefix + elements[i].element.name + ' .elementsizeRow .jq_slider').parent().next().text();
			outData[elements[i].element.name + "machinesize"] = machineSize(dc.idPrefix, elements[i].element.name, 1);

			if (elements[i].replicated === true){
				outData[elements[i].element.name + "replelementsize"] = $('#' + dc.idPrefix + elements[i].element.name +
					' .replicationelementsizeRow .jq_slider').parent().next().text();
				outData[elements[i].element.name + "replmachinesize"] = machineSize(dc.idPrefix, elements[i].element.name, 2);
			}
			if($('#' + dc.idPrefix + "imageTypeEphemeral_" + elements[i].element.name).attr('checked')){
				outData[elements[i].element.name + "imagetype"] = "0";
			}
			else
				outData[elements[i].element.name + "imagetype"] = "1";
			if($('#' + dc.idPrefix + "toggleEbsRadioOn_" + elements[i].element.name).attr('checked')){
				outData[elements[i].element.name + "esbvolumesize"] =
					$('#' + dc.idPrefix + elements[i].element.name + ' .ebsSizeRow .jq_slider').parent().next().text();
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
	
