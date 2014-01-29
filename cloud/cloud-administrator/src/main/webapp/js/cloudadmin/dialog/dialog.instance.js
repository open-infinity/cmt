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
 * @version 1.2.2
 * @since 1.0.0
 */

(function($) {
	console.log("initializing cloudadmin.dialog.instance");
	var cloudadmin = window.cloudadmin || {};
	cloudadmin.resource.elements = {};
	cloudadmin.dialog.instance = {};

	$.extend(cloudadmin.dialog, {
		cid : {},
		instanceAddButtons : {},
		instanceDeleteButtons : {},		
		
		// This function is called at dialog creation. It creates and initializes all the html and css elements 
		// and defines event handling
		initInstanceCreationDialog: function() {
             var instDlg = cloudadmin.dialog.instance = new Object();
             instDlg.idPrefix = '';
             instDlg.dialog = $("#addInstanceDialog");
             instDlg.accordion = $("#cloudTypesSelectionAccordion");

			// Initialize accordion once the data from CMT DB arrives
			$.when(
				$.ajax({dataType: "json", url: portletURL.url.instance.getCloudProvidersURL}),
				$.ajax({dataType: "json", url: portletURL.url.instance.getMachineTypesURL}),
				$.ajax({dataType: "json", url: portletURL.url.instance.getTemplatesURL}))
				.done(function(resultCloudProviders, resultMachineTypes, resultTemplates) {

					var cloudProviders = cloudadmin.resource.cloudProviders = resultCloudProviders[0];
					var machineTypes = cloudadmin.resource.machineTypes = resultMachineTypes[0];
					var templates = cloudadmin.resource.templates = resultTemplates[0];

					var cloudSelect = $("#cloudSelect");
					$.each(cloudProviders, function(index, provider) {
						cloudSelect.append("<option value='" + provider.id + "'>" + provider.name + "</option>");
					});

                    // Get templates and populate the templates combo box
                    var templateSelect = $("#templateSelect");
                    $.each(templates, function(index, t) {
                        templateSelect.append("<option value='" + t.id + "'>" + t.name + "</option>");
                    });

                    // Get elements for selected template
                    var url = portletURL.url.instance.getElementsForTemplateURL + "&templateId=" + templates[0].id;
                    $.getJSON(url, function(data) {
                        cloudadmin.resource.elements = data;
                        if ($.isEmptyObject(data)){
                            console.log("Unable to populate UI - configuration elements not available");
                        }
                        else{
                            createPlatformSelectAccordion(instDlg, cloudadmin.resource, machineTypes, instDlg.idPrefix);
                        }
                    });


                    // Events

                    // Update zones on cloudSelect change
					cloudSelect.change(function() {
						var cloudId = $('#cloudSelect option:selected').val();
						if (!cloudId) { 
							$("#zoneSelect").html('<option selected></option>');
							return;
						} 
						var url = portletURL.url.instance.getCloudZonesURL+"&cloud="+cloudId;
						$.getJSON(url, function(data) {
							$.each(data, function(index, zone) {
							    $("#zoneSelect").empty();
                                $("#zoneSelect").append("<option value='" + zone.name + "'>" + zone.name + "</option>");
							});
						});
					});

                    // Update elements on template change
                    templateSelect.change(function() {
                        var templateId = $('#templateSelect option:selected').val();
                        if (!templateId) {
                            return;
                        }
                        var url = portletURL.url.instance.getElementsForTemplateURL + "&templateId=" + templateId;
                        $.getJSON(url, function(data) {
                            cloudadmin.resource.elements = data;
                            if ($.isEmptyObject(data)){
                                console.log("Unable to populate UI - configuration elements not available");
                                $("#cloudTypesSelectionAccordion").fadeOut(500);
                            }
                            else{
                                $("#cloudTypesSelectionAccordion").fadeOut(500);
                                createPlatformSelectAccordion(instDlg, cloudadmin.resource, machineTypes, instDlg.idPrefix);
                               	$("#cloudTypesSelectionAccordion").fadeIn(500);
                            }
                        });
                    });

					// TODO: check this out
					//cloudadmin.dialog.initAddServiceDialog();
			    });
		},
		
		// This function is called when dialog is already created. It is called from instances page, 
		// from "create new instance" button.
		// The function clears resets all dialog element values and styles to defaults,
		// and finally opens the dialog
		createNewInstance: function() {
			$("#instanceName").val('');
			$("#cloudTypesSelectionAccordion").accordion("option", "active", false);

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
				elements = cloudadmin.resource.elements;
				if (!validateField($("#instanceName"))) return;
				if (!validateField($("#cloudSelect"))) return;
				if (!validateField($("#zoneSelect"))) return;
				/*
				outData["instancename"] = $("#instanceName").val();
				outData["cloudtype"] = $("#cloudSelect").val();
				outData["zone"] = $("#zoneSelect").val();
				outData["template"] = $("#templateSelect").val();
				*/
				outData.environment = {};
				outData.environment.name = $("#instanceName").val();
				outData.environment.type = $("#cloudSelect").val();
				outData.environment.zone = $("#zoneSelect").val();
				outData.environment.template = $("#templateSelect").val();

                outData.elements = [];
				// Initialize outData
				/*
				for(var i = 0; i < elements.length; i++){
					outData[elements[i].element.name]				  	= "false";
					outData[elements[i].element.name + "clustersize"] 	= 0;
					outData[elements[i].element.name + "machinesize"] 	= 0;
					outData[elements[i].element.name + "esb"] 		  	= "false";
					outData[elements[i].element.name + "volumesize"]  	= 0;
				}
				for(i = 0; i < elements.length; i++){
					if($('#' + "togglePlatformRadioOn_" + elements[i].element.name).attr('checked')){
						outData[elements[i].element.name] = "true";
						outData[elements[i].element.name + "clustersize"] = $('#' + elements[i].element.name + ' .clusterSizeRow .jq_slider').parent().next().text();
						outData[elements[i].element.name + "machinesize"] = machineSize("", elements[i].element.name, 1);
						if (elements[i].element.replicated == true){
							outData[elements[i].element.name + "replclustersize"] = $('#' + elements[i].element.name + ' .replicationClusterSizeRow .jq_slider').parent().next().text();
							outData[elements[i].element.name + "replmachinesize"] = machineSize("", elements[i].element.name, 2);
						}
						if($('#' + "imageTypeEphemeral_" + elements[i].element.name).attr('checked')){
							outData[elements[i].element.name + "imagetype"] = "0";
						}
						else
							outData[elements[i].element.name + "imagetype"] = "1";
						if($('#' + "toggleEbsRadioOn_" + elements[i].element.name).attr('checked')){
							outData[elements[i].element.name + "esbvolumesize"] = $('#' + elements[i].element.name + ' .ebsSizeRow .jq_slider').parent().next().text();
						}
					}
				}
                */
                //elements, accordion, outData
                //prepareRequestParameters(outData, dialog);
                prepareRequestParameters(elements, $("#cloudTypesSelectionAccordion"), outData);
				/*
				$.ajax({
					type: 'POST',
					url: portletURL.url.instance.addInstanceURL,
					data: outData,
					dataType: 'json'
				});
				*/
				console.log("Sending data (nat!):" + outData);
				$("#cloudTypesSelectionAccordion").accordion("option", "active", false);
				$(this).trigger("instancetable.refresh").dialog("close");
			};

			this.instanceAddButtons[dialogRes.resource.dialog.cancel] = function() {
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

	instDlg.dialog.dialog({
            autoOpen : false,
            height : 810,
            width : 770,
            modal : true,
            buttons : cloudadmin.dialog.instanceAddButtons
        });

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