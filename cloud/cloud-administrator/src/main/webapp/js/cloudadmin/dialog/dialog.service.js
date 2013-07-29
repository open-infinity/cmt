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
			
			// Initialize dialog container
			var dc = new Object();
			
			dc.dialog = $("#addServicesDialog");
			dc.accordion = $("#addServicesAccordion");	
			dc.idPrefix = 'sa_';
			dc.instanceId = 0;
			
			populateAccordion(dc, clusterTypes, machineTypes, dc.idPrefix);
			dc.accordion.accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
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
			dimAccordionElements(dc.accordion);
			cloudadmin.dialog.addServiceDialog = dc;

			// Events 
					
			dc.dialog.find(".togglePlatformSelectionRow :radio").change(function(e){
				handlePlatformSelectionChange($(this), dc.idPrefix);
			});	
			
			dc.dialog.find(".toggleEbsRow :radio").change(function(e) {
				handleEbsSelectionChange($(this));
			});

			dc.dialog.find(".machineSizeRow :radio").change(function(e) {
				handleMachineSizeChange($(this));
			});	 		
		},
		
		addNewService: function(dc) {	
			var clusters = cloudadmin.resource.clusterTypes;
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
	
	});

})(jQuery);