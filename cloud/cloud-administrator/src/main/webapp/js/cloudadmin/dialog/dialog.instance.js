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
	console.log("initializing cloudadmin.dialog.instance");
	var cloudadmin = window.cloudadmin || {};
	$.extend(cloudadmin.dialog, {
		cid : {},
		instanceObj : {},
		instanceAddButtons : {},
		instanceDeleteButtons : {},		
		
		// This function is called at dialog creation. It creates and initializes all the html and css elements 
		// and defines event handling
		initInstanceCreationDialog: function() {
			var dc = new Object();
			dc.idPrefix = '';
			dc.dialog = $("#addInstanceDialog");
			dc.accordion = $("#cloudTypesSelectionAccordion");
			dc.dialog.dialog({		
				autoOpen : false,
				height : 745,
				width : 710,
				modal : true,
				buttons : cloudadmin.dialog.instanceAddButtons
			});	
			
			// Initialize accordion once the data from CMT DB arrives
			$.when(
				$.ajax({dataType: "json", url: portletURL.url.instance.getCloudProvidersURL}),
				$.ajax({dataType: "json", url: portletURL.url.instance.getClusterTypesURL}),
				$.ajax({dataType: "json", url: portletURL.url.instance.getMachineTypesURL}))
				.done(function(resultCloudProviders, resultClusterTypes, resultMachineTypes) {
											
					var cloudProviders = cloudadmin.resource.cloudProviders = resultCloudProviders[0];
					var clusterTypes = cloudadmin.resource.clusterTypes = resultClusterTypes[0];
					var machineTypes = cloudadmin.resource.machineTypes = resultMachineTypes[0];
						
					var cloudSelect = dc.dialog.find("#cloudSelect");
					$.each(cloudProviders, function(index, provider) {
						cloudSelect.append("<option value='" + provider.id + "'>" + provider.name + "</option>");
					});			

					createPlatformSelectAccordion(dc, clusterTypes, machineTypes, dc.idPrefix);
									
					cloudSelect.change(function() {
						var cloudId = $('#cloudSelect option:selected').val();
						if (!cloudId) { 
							$("#zoneSelect").html('<option selected></option>');
							return;
						} 
						var url = portletURL.url.instance.getCloudZonesURL+"&cloud="+cloudId+"&rnd="+Math.random();
						var options = '';
					
						$.getJSON(url, function(data) {
							$.each(data, function(index, zone) {
								options += '<option value="'+zone.name+'">'+zone.name+'</option>';
							});
							$("#zoneSelect").html(options);
						});
					});
					
					cloudadmin.dialog.initAddServiceDialog();
			});
		},
		
		// This function is called when dialog is already created. It is called from instances page, 
		// from "create new instance" button.
		// The function clears resets all dialog element values and styles to defaults,
		// and finally opens the dialog
		createNewInstance: function() {
			
			$("#instanceName").val('');
			var clusters = cloudadmin.resource.clusterTypes;
			for(var i = 0; i < clusters.length; i++){
				var selectorName = '#' + clusters[i].name;
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
			$("#cloudTypesSelectionAccordion").accordion("option", "active", false);		
			$('#addInstanceDialog .togglePlatformSelectionRow input[id*="togglePlatformRadioOff_"]').attr('checked',true).button("refresh");
			$('#addInstanceDialog .imageTypeRow input[id*="imageTypeEphemeral_"]').attr('checked',true).button("refresh");
			$('#addInstanceDialog .toggleEbsRow input[id*="toggleEbsRadioOff_"]').attr('checked',true).button("refresh");
            if (cloudadmin.resource.machineTypes.length > 0) { // select first machine type and reset label if there are any machine types
                $("#addInstanceDialog .valueDisplayButtonSet").text(cloudadmin.resource.machineTypes[0].specification);
                $("#addInstanceDialog .machineSizeRow input:first-child").attr('checked',true).button("refresh");
            }
            // reset cloud selection, fire also change event to update zone selection
            $("#cloudSelect option").eq(0).attr("selected", "selected").trigger("change");
            dimAccordionElements($("#cloudTypesSelectionAccordion"));
			$(".addInstanceDialogError").hide();
			$("#addInstanceDialog").dialog("open");
		},
		
		initAddInstanceDialogButtons: function() {
			// Create instance-button
			this.instanceAddButtons[dialogRes.resource.instance.create] = function() {
				var outData = {};	
				clusters = cloudadmin.resource.clusterTypes;
				if (!validateField($("#instanceName"))) return;
				if (!validateField($("#cloudSelect"))) return;
				if (!validateField($("#zoneSelect"))) return;
				
				outData["instancename"] = $("#instanceName").val();
				outData["cloudtype"] = $("#cloudSelect").val();
				outData["zone"] = $("#zoneSelect").val();
				
				// init outData
				for(var i = 0; i < clusters.length; i++){
					outData[clusters[i].name]				  	= "false";
					outData[clusters[i].name + "clustersize"] 	= 0;
					outData[clusters[i].name + "machinesize"] 	= 0;
					outData[clusters[i].name + "esb"] 		  	= "false";
					outData[clusters[i].name + "volumesize"]  	= 0;
				}
				for(var i = 0; i < clusters.length; i++){
					if($('#' + "togglePlatformRadioOn_" + clusters[i].name).attr('checked')){
						outData[clusters[i].name] = "true";
						// TODO: There is a bug somewhere, values can't be read from slider correctly as documented in jQuery API docs, 
						// workaround is used
						// should be like this: 
						// outData[clusters[i].name + "clustersize"] 	= $('#' + clusters[i].name + ' .clusterSizeRow .jq_slider').slider("value");
						outData[clusters[i].name + "clustersize"] = $('#' + clusters[i].name + ' .clusterSizeRow .jq_slider').parent().next().text();
						outData[clusters[i].name + "machinesize"] = machineSize("", clusters[i].name, 1);
						if (clusters[i].replicated == true){
							outData[clusters[i].name + "replclustersize"] = $('#' + clusters[i].name + ' .replicationClusterSizeRow .jq_slider').parent().next().text();
							outData[clusters[i].name + "replmachinesize"] = machineSize("", clusters[i].name, 2);
						}
						if($('#' + "imageTypeEphemeral_" + clusters[i].name).attr('checked')){
							outData[clusters[i].name + "imagetype"] = "0";
						}
						else
							outData[clusters[i].name + "imagetype"] = "1";
						if($('#' + "toggleEbsRadioOn_" + clusters[i].name).attr('checked')){
							outData[clusters[i].name + "esbvolumesize"] = $('#' + clusters[i].name + ' .ebsSizeRow .jq_slider').parent().next().text();
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
				$(this).trigger("instancetable.refresh").dialog("close");
			};

			this.instanceAddButtons[dialogRes.resource.dialog.cancel] = function() {
				$("#cloudTypesSelectionAccordion").accordion("option", "active", false);
				$(this).trigger("instancetable.refresh").dialog("close");
			};
		},
	
		// Delete instance-button
		initInstanceDeleteButtons: function() {
			this.instanceDeleteButtons[dialogRes.resource.instance.del] = function() {
				var instanceId = this.getAttribute("data-instance-id");
				$.get(portletURL.url.instance.deleteInstanceURL+"&id="+instanceId+"&rnd="+Math.random());
				$(this).trigger("instance.delete", instanceId).dialog("close");
			},
			
			// Cancel-button
			this.instanceDeleteButtons[dialogRes.resource.dialog.cancel] = function() {
				$(this).trigger("instancetable.refresh").dialog("close");
			};
		}
	});
		
	cloudadmin.dialog.initAddInstanceDialogButtons();
	cloudadmin.dialog.initInstanceDeleteButtons();
	cloudadmin.dialog.initInstanceCreationDialog();
	
	$("#deleteInstanceConfirmDialog").dialog({
		autoOpen: false,
		resizable: false,
		height: 140,
		modal: true,
		buttons: cloudadmin.dialog.instanceDeleteButtons
	});

	// Helper functions
	function validateField(dialogField){
		if (dialogField.val() == null || dialogField.val().length < 1) {
			$(".addInstanceDialogError").show();
			dialogField.focus();
			return false;
		}
		else return true;
	}
		
})(jQuery);