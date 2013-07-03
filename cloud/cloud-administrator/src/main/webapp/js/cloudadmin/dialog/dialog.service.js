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
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.2.0 
 * @since 1.0.0
 */

(function($) {
	console.log("initializing cloudadmin.dialog.service");
	var cloudadmin = window.cloudadmin || {};
	
	$.extend(cloudadmin.dialog, {
		initAddServiceDialog: function () {
			
			// Creation
			
			var clusterTypes = cloudadmin.resource.clusterTypes;
			var machineTypes = cloudadmin.resource.machineTypes;				
			var o = new Object();
			
			o.dialog = $("#addServicesDialog");
			o.accordion = $("#addServicesAccordion");	
			o.idPrefix = 'sa_';
			
			populateAccordion(o, clusterTypes, machineTypes, o.idPrefix);
			o.accordion.accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
			o.dialog.dialog({
				autoOpen: false,
				resizable: true,
				height: 700,
				width: 500,
				modal: true,
				buttons: {
					"Create cluster": function() {
						var outData = {};	
						clusters = cloudadmin.resource.clusterTypes;
						
						/*
						var outData = {};
						var instanceId = document.getElementById("addServiceDialog").getAttribute("data-instance-id");
						outData["service"] = $("#serviceSelect").val();
						outData["machineCount"] = $("#serviceMachineCountSlider").mbgetVal();
						outData["machineSize"] = $("#serviceMachineSizeSelect").vmenu('getValId');
						$.ajax({
							type: 'POST',
							url: portletURL.url.service.newServiceURL+"&id="+instanceId,
							data: outData,
							dataType: 'json'
						});
						
						$("#addServiceDialog .vmenu").vmenu("reset");
						$(this).dialog("close");
						*/
						
						// init outData
						for(var i = 0; i < clusters.length; i++){
							outData[clusters[i].name]				  	= "false";
							outData[clusters[i].name + "clustersize"] 	= 0;
							outData[clusters[i].name + "machinesize"] 	= 0;
							outData[clusters[i].name + "esb"] 		  	= "false";
							outData[clusters[i].name + "volumesize"]  	= 0;
						}
						for(var i = 0; i < clusters.length; i++){
							if($('#' + o.idPrefix + "togglePlatformRadioOn_" + clusters[i].name).attr('checked')){
								outData[clusters[i].name] = "true";
								outData[clusters[i].name + "clustersize"] = 
									$('#' + o.idPrefix + clusters[i].name + ' .clusterSizeRow .jq_slider').parent().next().text();
								outData[clusters[i].name + "machinesize"] = machineSize(clusters[i].name, 1);
								
								if (clusters[i].replicated == true){
									outData[clusters[i].name + "replclustersize"] = $('#' + o.idPrefix + clusters[i].name + 
										' .replicationClusterSizeRow .jq_slider').parent().next().text();
									outData[clusters[i].name + "replmachinesize"] = machineSize(clusters[i].name, 2);
								}
								if($('#' + o.idPrefix + "imageTypeEphemeral_" + clusters[i].name).attr('checked')){
									outData[clusters[i].name + "imagetype"] = "0";
								}
								else
									outData[clusters[i].name + "imagetype"] = "1";
								if($('#' + o.idPrefix + "toggleEbsRadioOn_" + clusters[i].name).attr('checked')){
									outData[clusters[i].name + "esbvolumesize"] = 
										$('#' + o.idPrefix + clusters[i].name + ' .ebsSizeRow .jq_slider').parent().next().text();
								}
							}
						}
						$.ajax({
							type: 'POST',
							url: portletURL.url.instance.addInstanceURL,
							data: outData,
							dataType: 'json'
						});
						$("#cloudTypesSelectionAccordion").accordion("option", "active", false);
					},
					"Cancel": function() {
						$(this).dialog("close");
					}
				}
			});
			
			// Events 
					
			o.dialog.find(".togglePlatformSelectionRow :radio").change(function(e){
				handlePlatformSelectionChange($(this), o.idPrefix);
			});	
			
			o.dialog.find(".toggleEbsRow :radio").change(function(e) {
				handleEbsSelectionChange($(this));
			});

			$("#addInstanceDialog .machineSizeRow :radio").change(function(e) {
				handleMachineSizeChange($(this));
			});	 
			
			cloudadmin.dialog.addServiceDialog = o;
		},
		
		addNewService: function(o) {	
			var clusters = cloudadmin.resource.clusterTypes;
			for(var i = 0; i < clusters.length; i++){
				var selectorName = '#' + o.idPrefix + clusters[i].name;
				$(selectorName + ' .clusterSizeRow .jq_slider')
					.slider({
						min: clusters[i].minMachines,
						max: clusters[i].maxMachines,
						values: [clusters[i].minMachines],
						slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
					.parent().next().text(clusters[i].minMachines);	
				$(selectorName + ' .ebsSizeRow .jq_slider')
					.slider({
						min: 1,
						max: 1000,
						values: [1],
						step: 10,
						slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
					.parent().next().text(1);
				if(clusters[i].replicated == true){
					$(selectorName + ' .replicationClusterSizeRow .jq_slider')
						.slider({
							max:clusters[i].maxReplicationMachines,
							min:clusters[i].minReplicationMachines,
							values: [clusters[i].minReplicationMachines],
							slide: function(event, ui) {$(this).parent().next().text(ui.values[0]);}})
						.parent().next().text(clusters[i].minReplicationMachines);
				}	
				// accordion header
				$(selectorName).prev().addClass("platformNotSelected").removeClass("platformSelected");
			}
			o.accordion.accordion("option", "active", false);		
			o.accordion.find('.radioButton input').attr('checked',false).button("refresh");
			o.accordion.find('.togginstanceIdlePlatformSelectionRow input[id*="togglePlatformRadioOff_"]').attr('checked',true).button("refresh");
			o.accordion.find('.imageTypeRow input[id*="imageTypeEphemeral_"]').attr('checked',true).button("refresh");
			o.accordion.find('.toggleEbsRow input[id*="toggleEbsRadioOff_"]').attr('checked',true).button("refresh");
			
			// select first machine type and reset label if there are any machine types
            if (cloudadmin.resource.machineTypes.length > 0) { 
    			o.accordion.find('.valueDisplayButtonSet').text(cloudadmin.resource.machineTypes[0].specification);
    			o.accordion.find('.machineSizeRow input:first-child').attr('checked',true).button("refresh");
            }

            dimAccordionElements(o.accordion);
            o.dialog.dialog("open");
		}
	
	});

	//cloudadmin.dialog.initAddServiceDialog();
})(jQuery);

// Initializing the dialog
// TODO: create buttons 
/*
$("#addServiceDialog").dialog({
	autoOpen: false,
	resizable: false,			$(".addInstanceDialogError").hide();

	height: 500,
	width: 510,
	modal: true,
	buttons: {
		"Create cluster": function() {
			var outData = {};
			var instanceId = document.getElementById("addServiceDialog").getAttribute("data-instance-id");
			outData["service"] = $("#serviceSelect").val();
			outData["machineCount"] = $("#serviceMachineCountSlider").mbgetVal();
			outData["machineSize"] = $("#serviceMachineSizeSelect").vmenu('getValId');
			$.ajax({
				type: 'POST',
				url: portletURL.url.service.newServiceURL+"&id="+instanceId,
				data: outData,
				dataType: 'json'
			});
			
			$("#addServiceDialog .vmenu").vmenu("reset");
			$(this).dialog("close");
		},
		"Cancel": function() {
			$("#addServiceDialog .vmenu").vmenu("reset");
			$(this).dialog("close");
		}
	}
});
*/

/*
var url = portletURL.url.service.getAvailableServicesURL +"&id="+ instanceId;
$("#addServiceDialog").attr("data-instance-id", instanceId);

// Clear the previous data
var $dialog = $("#addServiceDialog").empty();

	// Get the template
	var $template = $("#addServiceTemplate > form").clone();
	$template.find("select[name='serviceSelect']").attr("id", "serviceSelect");
	$template.find(".serviceSliders").empty();

$dialog.append($template);

// Get available services	
$.getJSON(url, function(data) {
	var options = '';
	
	// First row empty
	options += '<option value=""> </option>';
	
	// Add available services to select box
	$.each(data, function(key,val) {
		options += '<option value="'+key+'">'+val+'</option>';
	});
	$("#serviceSelect").html(options);
	
	
	// Handle the selection change
    $("#serviceSelect").change(function () {
    	var clusterName = this.text;
        var clusterType = this.value;
        
    	$("#serviceSelect option:selected").each(function () {
    		clusterName += $(this).text() + " ";
    	});
          
    	console.log("selected service " + clusterName);
    	console.log("selected service type " + clusterType);

    	// Clear the sliders
    	$("#addServiceDialog .serviceSliders").remove();
    	
    	for(var i = 0; i < clusters.length; i++) {
    		if(clusters[i].type == clusterType) {

    			// Get the slider template
		  		var $slidertemplate = $("#addServiceTemplate .serviceSliders").clone();
		  		
		  		$slidertemplate.find("div[name='serviceslider']").attr("id", "serviceslider");
		  		$slidertemplate.find("div[name='serviceMachineCountSlider']").attr("id", "serviceMachineCountSlider").addClass("{startAt: "+ clusters[i].min +"}");
		  		$slidertemplate.find("div[name='serviceMachineSizeSelect']").attr("id", "serviceMachineSizeSelect");
		  		
		  		if(clusters[i].repMin) {
		  			$slidertemplate.find("div[name='replicationslider']").attr("id", "replicationslider");
		  			$slidertemplate.find("div[name='replicationSizeSlider']").attr("id", "replicationSizeSlider").addClass("{startAt: "+ clusters[i].repMin +"}");
			  		$slidertemplate.find("div[name='replicationMachineSizeSelect']").attr("id", "replicationMachineSizeSelect");

		  		} else {
		  			$slidertemplate.find("div[name='replicationslider']").remove();
		  			$slidertemplate.find("div[name='replicationSizeSlider']").remove();
			  		$slidertemplate.find("div[name='replicationMachineSizeSelect']").remove();
		  		}
		  		
		  		$slidertemplate.appendTo("#addServiceDialog fieldset");
		  		
		  		// Add sliders and dropbox
		  		$("#serviceslider .mb_slider").mbSlider({			
		  			minVal: clusters[i].min,
		  			maxVal: clusters[i].max,
		  			grid: 1
		  		});

		  		if(clusters[i].repMin) {
		  				$("#replicationslider .mb_slider").mbSlider({			
		  				minVal: clusters[i].repMin,
		  				maxVal: clusters[i].repMax,
		  				grid: 1
		  			});
		  		}
		  		
		  		$("#addServiceDialog .vmenu").vmenu();
    		}
    	}       
          
        });
});

}*/