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
    cloudadmin.dialog.service = {};
    //cloudadmin.resource = {};

    //var cloudadmin = window.cloudadmin || {};
    //var cloudadmin.dialog.service = {};
	
	$.extend(cloudadmin.dialog, {
        /*
		initAddServiceDialog: function () {
			var dc = new Object();	
			dc.dialog = $("#addServicesDialog");
			dc.accordion = $("#addServicesAccordion");	
			dc.idPrefix = 'sa_';
			dc.instanceId = 0;
			dc.dialog.dialog({
				autoOpen: false,
				resizable: true,
				height: 590,
				width: 620,
				modal: true,
				buttons: {
					"Create cluster": function() {
						var outData = {};	
						prepareRequestParameters(outData, dc);					
						
						var request = $.ajax({
							type: 'POST',
							url: portletURL.url.service.newServiceURL + "&id=" + dc.instanceId,
							data: outData,
							dataType: 'json'
						});
						
						request.fail(function(data, textStatus, jqXHR) {
							  alert( "Unable to add service. Cluster type already exists for the instance.");
							});
						
						request.success(function(data, textStatus, jqXHR) {
							dc.accordion.accordion("option", "active", false);
							dc.dialog.dialog("close");							
						});

					},
					"Cancel": function() {
						dc.accordion.accordion("option", "active", false);
						dc.dialog.dialog("close");
					}
				}
			});
			if ($.isEmptyObject(cloudadmin.resource.elements)){
                console.log("Unable to populate UI - configuration elements not available");
            }
            else{
			    createPlatformSelectAccordion(dc, cloudadmin.resource, cloudadmin.resource.machineTypes, dc.idPrefix);
            }
			cloudadmin.dialog.addServiceDialog = dc;
		},
        */
		/*
		addNewService: function(dc) {	
			var clusters = cloudadmin.resource.elements;
			for(var i = 0; i < clusters.length; i++){
				var selectorName = '#' + dc.idPrefix + clusters[i].name;
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
			dc.accordion.accordion("option", "active", false);		
			dc.accordion.find('.togglePlatformSelectionRow input[id*="togglePlatformRadioOff_"]').
				attr('checked',true).button("refresh");
			dc.accordion.find('.imageTypeRow input[id*="imageTypeEphemeral_"]').attr('checked',true).button("refresh");
			dc.accordion.find('.toggleEbsRow input[id*="toggleEbsRadioOff_"]').attr('checked',true).button("refresh");
			
			// select first machine type and reset label 
            if (cloudadmin.resource.machineTypes.length > 0) { 
    			dc.accordion.find('.valueDisplayButtonSet').text(cloudadmin.resource.machineTypes[0].specification);
    			dc.accordion.find('.machineSizeRow input:first-child').attr('checked',true).button("refresh");
            }

            dimAccordionElements(dc.accordion);
            dc.dialog.dialog("open");
		}
		*/
		initAddServiceDialog: function () {



        },

         addNewService: function(instanceId) {
            var instDlg = cloudadmin.dialog.service;
            instDlg.dialog = $("#addServicesDialog");
            instDlg.accordion = $("#addServicesAccordion");
            instDlg.idPrefix = 'sa_';
            instDlg.instanceId = 0;

            // Initialize accordion once the data from CMT DB arrives
             $.when($.ajax({dataType: "json", url: portletURL.url.instance.getMachineTypesURL}),
                    $.ajax({dataType: "json", url: portletURL.url.instance.getTemplatesURL})).done(function(resultMachineTypes, resultTemplates) {
                var machineTypes = cloudadmin.resource.machineTypes = resultMachineTypes[0];
                var templates = cloudadmin.resource.templates = resultTemplates[0];

                // Get templates and populate the templates combo box
                var templateSelect = $(".templateSelect", cloudadmin.dialog.service.dialog).empty();
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
                        createPlatformSelectAccordion(instDlg, cloudadmin.resource);
                    }
                });

                // Update elements on template change
                templateSelect.change(function() {
                    var templateId = $('option:selected', '.templateSelect', cloudadmin.dialog.service.dialog).val();
                    if (!templateId) {
                        return;
                    }
                    var url = portletURL.url.instance.getElementsForTemplateURL + "&templateId=" + templateId;
                    $.getJSON(url, function(data) {
                        cloudadmin.resource.elements = data;
                        if ($.isEmptyObject(data)){
                            console.log("Unable to populate UI - configuration elements not available");
                            $("#addServicesAccordion").fadeOut(500);
                        }
                        else{
                            $("#addServicesAccordion").fadeOut(500);
                            createPlatformSelectAccordion(instDlg, cloudadmin.resource);
                            $("#addServicesAccordion").fadeIn(500);
                        }
                    });
                });

            });

            $("#addServicesAccordion").accordion("option", "active", false);

            // reset cloud selection, fire also change event to update zone selection
            $("#cloudSelect option").eq(0).attr("selected", "selected").trigger("change");
            dimAccordionElements($("#addServicesAccordion"));
            $("#addServicesDialog").dialog("open");
        }
	
	});

    $("#addServicesDialog").dialog({
        autoOpen: false,
        resizable: true,
        height : 810,
        width : 770,
        buttons: {
            "Create service": function() {
                var outData = {environment:{}, configurations:[]};
                prepareRequestData(cloudadmin.resource.elements, outData, cloudadmin.dialog.service.idPrefix);
                if (outData.configurations.length === 0){
                    outData.configurations.push(0);
                }

                // In this way the controller will receive parameter "requestData", which is a Json formatted outData
                var data = {};
                data.requestData = JSON.stringify(outData);

                console.log("Sending data (nat!):" + JSON.stringify(outData));
                /*

                var request = $.ajax({
                    type: 'POST',
                    url: portletURL.url.service.newServiceURL + "&id=" + dc.instanceId,
                    data: outData,
                    dataType: 'json'
                });

                request.fail(function(data, textStatus, jqXHR) {
                  alert( "Unable to add service. Service already exists for the instance.");
                });

                request.success(function(data, textStatus, jqXHR) {
                    dc.accordion.accordion("option", "active", false);
                    dc.dialog.dialog("close");
                });

                */
                $("#addServicesAccordion").accordion("option", "active", false);
                $(this).trigger("instancetable.refresh").dialog("close");
            },
            "Cancel": function() {
                cloudadmin.dialog.service.accordion.accordion("option", "active", false);
                cloudadmin.dialog.service.dialog.dialog("close");
            }
        }
    });
})(jQuery);
