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
	$.extend(cloudadmin.dialog, {

         addNewService: function(environmentId, cloudType, zone) {
            o = cloudadmin.dialog.service;
            o.dialog = $("#addServicesDialog");
            o.accordion = $("#addServicesAccordion");
            o.idPrefix = 'sa_';
            o.templateSelect = $("#addServicesTemplateSelect");
            o.oldTemplateVal = -1;
            o.environmentId = environmentId;
            o.cloudType = cloudType;
            o.zone = zone;

            // Initialize dialog once the data from CMT DB arrives
            $.when($.ajax({dataType: "json", url: portletURL.url.instance.getMachineTypesURL}),
                    $.ajax({dataType: "json", url: portletURL.url.instance.getTemplatesURL})).done(function(resultMachineTypes, resultTemplates) {
                var machineTypes = cloudadmin.resource.machineTypes = resultMachineTypes[0];
                var templates = cloudadmin.resource.templates = resultTemplates[0];

                // Get templates and populate the templates combo box
                o.templateSelect.empty();

                $.each(templates, function(index, t) {
                    o.templateSelect.append("<option value='" + t.id + "'>" + t.name + "</option>");
                });

                // Get elements for selected template
                var url = portletURL.url.instance.getElementsForTemplateURL + "&templateId=" + templates[0].id;
                $.getJSON(url, function(data) {
                    cloudadmin.resource.elements = data;
                    if ($.isEmptyObject(data)){
                        console.log("Unable to populate UI - configuration elements not available");
                    }
                    else{
                        createPlatformSelectAccordion(o, cloudadmin.resource);
                    }
                });

                // Update elements on template change
                $("#addServicesTemplateSelect").change(function() {
                    var templateId = $("#addServicesTemplateSelect").val();

                    // fcking java script and jQuery
                    if (!templateId || o.oldTemplateVal === templateId) {
                        return;
                    }
                    o.oldTemplateVal = templateId;

                    var url = portletURL.url.instance.getElementsForTemplateURL + "&templateId=" + templateId;
                    $.getJSON(url, function(data) {
                        cloudadmin.resource.elements = data;
                        if ($.isEmptyObject(data)){
                            console.log("Unable to populate UI - configuration elements not available");
                            $("#addServicesAccordion").fadeOut(500);
                        }
                        else{
                            $("#addServicesAccordion").fadeOut(500);
                            createPlatformSelectAccordion(o, cloudadmin.resource);
                            $("#addServicesAccordion").fadeIn(500);
                        }
                    });
                });
            });

            $("#addServicesAccordion").accordion("option", "active", false);
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
                var outData = {
                    environment:{
                        id: cloudadmin.dialog.service.environmentId,
                        type: cloudadmin.dialog.service.cloudType,
                        zone: cloudadmin.dialog.service.zone
                    },
                    configurations:[]
                };

                getValues(cloudadmin.resource.elements, outData, cloudadmin.dialog.service.idPrefix);
                if (outData.configurations.length === 0){
                    outData.configurations.push(0);
                }

                // In this way the controller will receive parameter "requestData", which is a Json formatted outData
                var data = {};
                data.requestData = JSON.stringify(outData);

                console.log("Sending json data:" + data.requestData);

                var request = $.ajax({
                    type: 'POST',
                    url: portletURL.url.service.newServiceURL,
                    data: data,
                    dataType: 'json'
                });

                request.fail(function(data, textStatus, jqXHR) {
                  alert( "Unable to add service. Service already exists for the instance.");
                });

                $(this).dialog("close");
            },
            "Cancel": function() {
                $(this).dialog("close");
            }
        },
        beforeClose: function(event, ui){
            deletePlatformSelectAccordion(cloudadmin.dialog.service.accordion);
        }
    });

})(jQuery);
