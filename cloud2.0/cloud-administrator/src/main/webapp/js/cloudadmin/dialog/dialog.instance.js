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
	cloudadmin.dialog.instance = {};

	$.extend(cloudadmin.dialog, {
		cid : {},
		instanceAddButtons : {},
		instanceDeleteButtons : {},		
		createNewInstance: function() {
            delete cloudadmin.dialog.instance;
		    cloudadmin.dialog.instance = new Object();
            cloudadmin.dialog.instance.idPrefix = '';
            cloudadmin.dialog.instance.dialog = $("#addInstanceDialog");
            cloudadmin.dialog.instance.accordion = $("#cloudTypesSelectionAccordion");
            cloudadmin.dialog.instance.templateSelect = $("#addInstanceTemplateSelect");
            cloudadmin.dialog.instance.oldTemplateVal = -1;

            // Initialize accordion once the data from CMT DB arrives
            $.when($.ajax({dataType: "json", url: portletURL.url.instance.getCloudProvidersURL}),
                    $.ajax({dataType: "json", url: portletURL.url.instance.getMachineTypesURL}),
                    $.ajax({dataType: "json", url: portletURL.url.instance.getTemplatesURL})).done(function(resultCloudProviders, resultMachineTypes, resultTemplates){

                var cloudProviders = cloudadmin.resource.cloudProviders = resultCloudProviders[0];
                var machineTypes = cloudadmin.resource.machineTypes = resultMachineTypes[0];
                var templates = cloudadmin.resource.templates = resultTemplates[0];

                var cloudSelect = $("#cloudSelect").empty().append("<option></option>");
                $.each(cloudProviders, function(index, provider) {
                    cloudSelect.append("<option value='" + provider.id + "'>" + provider.name + "</option>");
                });

                // Get templates and populate the templates combo box
                cloudadmin.dialog.instance.templateSelect.empty();
                $.each(templates, function(index, t) {
                    cloudadmin.dialog.instance.templateSelect.append("<option value='" + t.id + "'>" + t.name + "</option>");
                });

                // Get elements for selected template
                var url = portletURL.url.instance.getElementsForTemplateURL + "&templateId=" + templates[0].id;
                $.getJSON(url, function(data) {
                    cloudadmin.resource.elements = data;
                    if ($.isEmptyObject(data)){
                        console.log("Unable to populate UI - configuration elements not available");
                    }
                    else{
                        createPlatformSelectAccordion(cloudadmin.dialog.instance, cloudadmin.resource);
                    }
                });


                // Events

                // Handle zone update on cloudSelect change
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

                // Handle elements update on template change
                $("#addInstanceTemplateSelect").change(function() {
                    var templateId = $("#addInstanceTemplateSelect").val();

                    if (!templateId || cloudadmin.dialog.instance.oldTemplateVal === templateId) {
                        return;
                    }
                    cloudadmin.dialog.instance.oldTemplateVal = templateId;

                    var url = portletURL.url.instance.getElementsForTemplateURL + "&templateId=" + templateId;
                    $.getJSON(url, function(data) {
                        cloudadmin.resource.elements = data;
                        if ($.isEmptyObject(data)){
                            console.log("Unable to populate UI - configuration elements not available");
                            $("#cloudTypesSelectionAccordion").fadeOut(500);
                        }
                        else{
                            $("#cloudTypesSelectionAccordion").fadeOut(500);
                            createPlatformSelectAccordion(cloudadmin.dialog.instance, cloudadmin.resource);
                            $("#cloudTypesSelectionAccordion").fadeIn(500);
                        }
                    });
                });
            });

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
				if (!validateField($("#instanceName"))) return;
				if (!validateField($("#cloudSelect"))) return;
				if (!validateField($("#zoneSelect"))) return;

				outData.environment = {};
				outData.environment.name = $("#instanceName").val();
				outData.environment.type = $("#cloudSelect").val();
				outData.environment.zone = $("#zoneSelect").val();

                outData.configurations = [];
                getValues(cloudadmin.resource.elements, outData, cloudadmin.dialog.instance.idPrefix);
                /*
                if (outData.configurations.length === 0){

                     // Push placeholder into empty array to make Spring Jackson parser happy
                    outData.configurations.push(0);
                }
                */

				// In this way the controller will receive parameter "requestData", which is a Json formatted outData
                var data = {};
                data.requestData = JSON.stringify(outData);
				$.ajax({
                    type: 'POST',
                    url: portletURL.url.environment.addEnvironmentURL,
                    data: data,
                    dataType: 'json'
                });

				console.log("Sending data:" + JSON.stringify(outData));
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

	$("#addInstanceDialog").dialog({
            autoOpen : false,
            height : 810,
            width : 770,
            modal : true,
            buttons : cloudadmin.dialog.instanceAddButtons,
            beforeClose: function(event, ui){
                deletePlatformSelectAccordion(cloudadmin.dialog.instance.accordion);
            }
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