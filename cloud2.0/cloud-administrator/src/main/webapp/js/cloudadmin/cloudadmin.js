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
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */


/**
 *  Application startup
 */


jQuery(function($) {
	var cloudadmin = window.cloudadmin || {};
	
	// After this, no need to add random at URL end
	$.ajaxSetup({cache: false});
	
	cloudadmin = {	
			
		init: function() {	
			console.log("cloudadmin.init()");
	
			instanceManager.setupTabs();
			instanceManager.setupButtons();
			instanceManager.setupTableViews();
		
			cloudadmin.setupErrorHandler();
			cloudadmin.setupEventListeners();
			
			// Initialize event listener last
			instanceManager.setupTabsEventListener();
		},
		
		setupErrorHandler: function () {
		    $('body').ajaxError(function (event, xhr, ajaxOptions, thrownError) {
		        if (xhr.getAllResponseHeaders().getResponseHeader('portlet.http-status-code') == "421") {
		        	alert("Something went wrong: \n\n" + xhr.responseText);	        	
		        }
		        console.log("XHR Response: " + JSON.stringify(xhr));
		      });
	    },
	    
	    setupEventListeners: function () {
	    	
	    	$(document).bind("instancetable.refresh", function(event) {
	    		console.log("refresh instancetable event");
	    		
	    		setTimeout(function () {
	    			instanceManager.refreshInstanceTable();
	    		}, 2000);
	    	});

	    	$(document).bind("instance.delete", function(event, instanceId) {
	    		console.log("delete instance event, instanceId " + instanceId);
				instanceManager.closeTab(instanceId);
	    	});
	    	
	    	$(document).bind("cluster.delete", function(event, clusterId) {
	    		console.log("delete cluster event, clusterId " + clusterId);
				instanceManager.removeClusterListener(clusterId);
	    	});
	    	
	    }
	};

	
	// Initialize
	 cloudadmin.init();
});

/**
 * Instance / Tab handling
 */

var instanceManager = {
		tabArray : new Array(),
		$tabs : null,
		
		tabListener : null,
		clusterListeners : new Array(),

		// Tab setup
		setupTabs: function () {
			
			// Create instance manager-tab and a place holder,
			// in the array for it and template for tabs
			var $tab_content_input = $("#tabs-0");
			instanceManager.tabArray.push(new instanceManager.tabObj(0, 0, 0));

			this.$tabs = $("#tabs").tabs({
				tabTemplate: "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>" + dialogRes.resource.tabs.removeTab + "</span></li>",
				add: function( event, ui ) {
					var tab_content = $tab_content_input.val() || " ";
				}
			});		
			
			// Bind the close (x) functionality for instance-tabs
			$(document).on("click", "#tabs span.ui-icon-close", function() {			
				var index = $("li", instanceManager.$tabs).index($(this).parent());
				instanceManager.$tabs.tabs("remove", index);
				
				console.log("tab array size: " + instanceManager.tabArray.length);
				console.log("tab index [" + index + "] removed!");
				console.log("removing tab for intance : " + instanceManager.tabArray[index].id + " from tabArray");
				
				instanceManager.tabArray.splice(index, 1);
			});	
		},

		// Close tab if found according to instance id
		closeTab: function(instanceId) {
			for(var i = 0; i < instanceManager.tabArray.length; i++) {
				if(instanceId == instanceManager.tabArray[i].id) {
					instanceManager.$tabs.tabs("remove", i);
					instanceManager.tabArray.splice(i, 1);
				}
			}
		},

		setupTabsEventListener: function () {
			
		    // Start polling of the main table
			instanceManager.tabListener = setInterval(function () {
						instanceManager.dataPoller("tabs-0");
					}, 5000);
			
			
			// Bind poller update for tab-selection
			$('#tabs').bind('tabsselect', function(event, ui) {
				
			    // Objects available in the function context:
			    console.log("selected tab                   : " + ui.tab);     // anchor element of the selected (clicked) tab
			    console.log("div for selected tab container : " + ui.panel.id);   // element, that contains the selected/clicked tab contents
			    console.log("index of the selected tab      : " + ui.index);   // zero-based index of the selected (clicked) tab

			    var tabSelected = ui.panel.id;
			   
				// Clear the previous interval
			    if(instanceManager.tabListener) {
			    	console.log("clearInterval("+ instanceManager.tabListener +") called.");
			    	clearInterval(instanceManager.tabListener);
			    } else {
			    	console.log("instanceManager.tabListener not set!");
			    }
			    
			    // Clear all cluster listeners
			    while (instanceManager.clusterListeners.length > 0) {
			    	instanceManager.removeClusterListener(instanceManager.clusterListeners.pop().clusterId); 
			    }
			     
			    // Start a new interval
				instanceManager.tabListener = setInterval(function () {
							instanceManager.dataPoller(tabSelected);
						}, 5000);
			});
		},

		dataPoller: function(tabId) {
			var id = "#" + tabId;
			
			if(id == "#tabs-0") {
				instanceManager.refreshInstanceStatus();
				return;
			} else {
				for(var i = 0; i < instanceManager.tabArray.length; i++) {
					if(instanceManager.tabArray[i].tabName == id) {
						instanceManager.refreshInstanceView(instanceManager.tabArray[i].id);
						return;
					}
				}
			}
			return;
		},
		
		addClusterListener: function (clusterId) {
			console.log("cluster listener added for cluster : " + clusterId);
			var clusterHandler = setInterval(function () {
												instanceManager.updateClusterMachineInfo(clusterId);
										}, 7000);
			
			instanceManager.clusterListeners.push(new instanceManager.clusterObj(clusterId, clusterHandler));
		},
		
		removeClusterListener: function (clusterId) {
			console.log("removing listener for cluster : " + clusterId);
			
			for(var i = 0; i < instanceManager.clusterListeners.length; i++) {	
				if(instanceManager.clusterListeners[i].clusterId == clusterId) {
					console.log("cluster listener found at index : " + i);
					
					clearInterval(instanceManager.clusterListeners[i].listenerHandle);
					instanceManager.clusterListeners.splice(i, 1);
				}
			}
			
		},
		
		// Main view button setup
		setupButtons: function() {
			$("#manage-instance").button().click(function() {
				instanceManager.openInstance();
			});
			
			$("#create-instance").button().click(function() {
				cloudadmin.dialog.createNewInstance();
			});
			
			$("#delete-instance").button().click(function() {
				instanceManager.deleteInstance();
			});
		},
		
		// Open instance in tab
		openInstance: function () {
			console.log("instanceManager.openInstance()");
			
			var row = $("#instances").jqGrid('getGridParam','selrow');
			var found = false;
			
			if (row)	{
					var sel = $("#instances").jqGrid('getRowData',row);
					var tab_title = sel.name;
					var tab = "#tabs-" + sel.instanceId;			
					
					var url = portletURL.url.instance.instanceURL + "&id="+sel.instanceId+"&rnd="+Math.random();
					
					// If tab already open, select it
					for(var i = 0; i < instanceManager.tabArray.length; i++) {	
						if(instanceManager.tabArray[i].id == sel.instanceId) {
							console.log("intance tab already open in : " + instanceManager.tabArray[i].tabName);
							
							instanceManager.$tabs.tabs('select', instanceManager.tabArray[i].tabName);
							found = true;
							break;
						}
					}
					
					// If not found, create a new tab for the selected instance
					if(!found) {
						instanceManager.$tabs.tabs("add", "#tabs-" + sel.instanceId, tab_title);
						instanceManager.instanceDataToTab(url, tab);
						instanceManager.tabArray.push(new instanceManager.tabObj(sel.instanceId, tab));
						instanceManager.$tabs.tabs('select', "#tabs-" + sel.instanceId);
					}

					// Debugging output
					for(var i = 0; i < instanceManager.tabArray.length; i++) {
						if(instanceManager.tabArray[i] !== null && instanceManager.tabArray[i] !== undefined) {
							console.log("instance id [" + instanceManager.tabArray[i].id +"]  |  tab name [" + instanceManager.tabArray[i].tabName +"]");
						}
					}
							
			} else {
				alert("Please select instance first!");
			}	
		},
		
		instanceDataToTab: function (url, tabName) {
			console.log("instanceManager.instanceDataToTab()");
			
			$.getJSON(url, function(data) {
				if(jQuery.isEmptyObject(data)) {
					// TODO: in case of error, close the tab and return to mainview
					alert("Error: No instance data.");
					return;
				}

				var instanceId = data.instanceId;
				
				instanceManager.createInstanceView(data, tabName);
				instanceManager.updateInstanceClusters(data.instanceId);
			});
		},
		
		// Create the instance view from the template (in instanceview.jsp)
		createInstanceView: function (data, tabName) {
			
			var $template = $("#instanceviewtemplate").clone(true).attr("id","cloudadmincontent_"+ data.instanceId);
			
			$template.find("*").each(function () {
				var uusi = this.getAttribute("id");
				
				if(uusi) {
					uusi = uusi + "_" + data.instanceId;
					this.setAttribute("id", uusi);
				}
			});
			
			// Add services button
			$template.find(".add-services").button().click(function() {
 				cloudadmin.dialog.addNewService(data.instanceId, data.cloudType, data.zone);
			});
			
			//  View instance machines button
			$template.find(".view-machines").button().click(function() {			
				cloudadmin.dialog.initMachineDialog(data.instanceId);
				$("#machineListDialog").dialog("open");
			});

			//  Get instance ssh-key button
			$template.find(".get-key").button().click(function() {			
				var url = portletURL.url.instance.getInstanceKeyURL+"&id=" + data.instanceId;
				window.location.href = url;
			});
			
			// Instance view droppable areas
			$template.find(".droppable").droppable({
				accept: '.newlist',					
				hoverClass: 'hovered',
				drop: function(event, ui) {
					instanceManager.clusterDrop(this, ui.draggable);
				}
			});
			
			$template.appendTo($(tabName));
			$template.show();
		},
		
		
		updateInstanceClusters: function (instanceId) {
			var url = portletURL.url.cluster.availableClustersURL + "&instanceId="+instanceId;
			
			$.getJSON(url, function(data) {
				$.each(data, function(key,val) {

					// Check if the cluster is already added to view
					if($("#cluster_"+ val.id).length == 0) {
						var $template = $("#clusterTemplate > li").clone();

						$template.draggable({
							cancel: "a.ui-icon",
							revert: 'invalid',
							helper: 'clone',
							opacity: 0.5,
							cursor: 'move'
						});
						
						//TODO: Add dependency requirements for cluster-handling
						//$("li[data-cluster-type='2']").length // returns all li-elements that have cluster-type 2
						//$("#cloudadmincontent_45 li[data-cluster-type='2']").data().clusterId // .data() returns the first object that matches the query and the fields are accessibele
						
						// Apply cluster data to template
						$template.attr("id", "cluster_" + val.id);
						$template.attr("data-cluster-type", val.type);
						
						switch (val.type)
						{
						case 0:
							// portal
							$template.attr("data-cluster-min-size", 1);
							$template.attr("data-cluster-max-size", 12);
						  break;
						case 1:
							// mule
							$template.attr("data-cluster-min-size", 1);
							$template.attr("data-cluster-max-size", 12);
						  break;
						case 2:
							// pentaho
							$template.attr("data-cluster-min-size", 1);
							$template.attr("data-cluster-max-size", 12);
						  break;
						case 3:
							// bigdata
							$template.attr("data-cluster-min-size", 7);
							$template.attr("data-cluster-max-size", 12);
						  break;
						case 4:
							// database
							$template.attr("data-cluster-min-size", 1);
							$template.attr("data-cluster-max-size", 1);
						  break;
						case 5:
							// bas
							$template.attr("data-cluster-min-size", 1);
							$template.attr("data-cluster-max-size", 12);
						  break;
						case 6:
							// nosql
							$template.attr("data-cluster-min-size", 6);
							$template.attr("data-cluster-max-size", 10);
						  break;  
						default:
						  console.log("No cluster-type matches in template creation.");
						}
						
						$template.attr("data-cluster-instance-id", val.instanceId);						
						$template.children().eq(1).append('<b>'+val.name+'</b>');
						
						
						// Add options-panel functionality
						var $optionsPanel = $template.find(".options_panel > li");
						
						$optionsPanel.eq(0).children().click(function() {	
							cloudadmin.dialog.showClusterInformation(val.id);
						});

						$optionsPanel.eq(1).children().click(function() {
							instanceManager.initClusterConfigureDialog(val.id);
						});
						
						$optionsPanel.eq(2).children().click(function() {
							instanceManager.handleClusterScale(val.id);
						});
						
						$optionsPanel.eq(3).children().click(function() {
							instanceManager.handleClusterDelete(val.id);
						});
						
						$template.children().eq(2).click(function() {
							console.log("open option panel for clusterid :  #cluster_" + val.id);
							$("#cluster_" + val.id + " .options_panel").slideToggle('medium');
						});
						
						// Add cluster listener for cluster updates
						instanceManager.addClusterListener(val.id);
						
						// Add cluster to service category
						switch(val.published)
						{
						case 1:
							$template.appendTo('#publiclist_'+instanceId).fadeIn();
							break;
						case 2:
							$template.appendTo('#privatelist_'+instanceId).fadeIn();
							break;
						case 3:
							$template.appendTo('#newlist_'+instanceId).fadeIn();
							break;
						default:
							console.log("No service-type match found for cluster.");
						}
						
						// Update cluster machine info
						instanceManager.updateClusterMachineInfo(val.id);
					}
					
				});
			});			
		},
				
		// Remove selected instance
		deleteInstance: function () {
			console.log("instanceManager.deleteInstance()");
			var row = $("#instances").jqGrid('getGridParam','selrow');
			
			if(row) {
				var sel = $("#instances").jqGrid('getRowData',row);
				console.log("instanceId : " + sel.instanceId);
				
				document.getElementById("deleteInstanceConfirmDialog").setAttribute("data-instance-id", sel.instanceId);
				$("#deleteInstanceConfirmDialog").dialog("open");
					
			} else {
				// TODO: replace alert with div
				alert("Please select instance first!");
			}	
		},

		// Reload whole instance-table
		refreshInstanceTable: function () {
			 //console.log("instanceManager.refreshInstanceTable() called");
			 $("#instances").trigger("reloadGrid");
		},

		// Refresh status field of an instance in the table
		refreshInstanceStatus: function () {
			//console.log("instanceManager.refreshInstanceStatus() called");
			var id = null;
			var url = null;
			var records = $('#instances').jqGrid('getGridParam', 'records');
			records++;
						
			for(var i = 1; i < records; i++) {
				id = $('#instances').jqGrid('getCell',i,'instanceId');
				if (id != false) {
					//console.log("refreshInstanceStatus id="+id);
					url = portletURL.url.instance.instanceStatusURL + "&instanceId="+id;
	
					//console.log("fetching data for instance id : " + id);
					instanceManager.updateInstanceStatus(i, url);
				}
			}	
		},
		
		updateInstanceStatus: function(row, url) {
			$.post(url, function(data) {
				//console.log("row number : [" + row + "] data value : " + data);
				if(data == "notfound")
					$('#instances').jqGrid('delRowData',row);				
				else	
					$('#instances').jqGrid('setCell',row,'status',data);
			});
		},
		
		// Refresh the instanceview
		refreshInstanceView: function (instanceId) {
			 //console.log("refreshInstanceView("+instanceId+") called");
			 instanceManager.updateInstanceClusters(instanceId);
		},
		
		// Tables (instance manager)	
		setupTableViews: function () {

			$(function() {
				$("#instances").jqGrid({
					url: portletURL.url.instance.instanceTableURL+"&rnd="+Math.random(),
					datatype: "json",
					jsonReader : {
						repeatitems : false,
						id: "Id",
						root : function(obj) { return obj.rows;},
						page : function(obj) {return obj.page;},
						total : function(obj) {return obj.total;},
						records : function(obj) {return obj.records;}
						},
					colNames:['Instance Id', 'Name', 'Cloud Type', 'User', 'Organization', 'Zone', 'Status'],
					colModel:[
					          {name:'instanceId', index:'instanceId', width:100, align:"center"},
					          {name:'name', index:'name', width:217, align:"center"},
					          {name:'cloudType', index:'cloudType', width:100, formatter:instanceManager.cloudTypeFmatter, align:"center"},
					          {name:'userName', index:'userId', width:100, align:"center"},
					          {name:'organizationName', index:'organizationName', width:150, align:"center"},
					          {name:'zone', index:'zone', width:120, align:"center"},
					          {name:'status', index:'status', width:60, align:"center"}
					          ],
					rowNum:15,
					width: 882,
					height: 346,
					pager: '#instancepager',
					sortname: 'id',
					viewrecords: true,
					shrinkToFit:false,
					sortorder: 'desc',
					ondblClickRow: instanceManager.openInstance
				});

				$("#instances").jqGrid('navGrid','#instancepager',{edit:false,add:false,del:false});			
			});

		},

		cloudTypeFmatter: function (cellvalue, options, rowObject) {
			   //console.log("CloudType: " + cellvalue);

			   if(cellvalue == 0)
				   return "Amazon";
			   else if(cellvalue == 1)
				   return "Eucalyptus";
			   else
				   return "";
		},
		
		// Cluster drop handler		
		clusterDrop: function (target, $item) {
			var outData = {};
			
			$item.fadeOut(function() {
				$item.appendTo(target.childNodes[1]).fadeIn();
			});
			
			outData["clusterId"] = $item.attr('id').slice(8);
			outData["pubId"] = target.parentNode.getAttribute('data-pub-id');
			$.post(portletURL.url.cluster.updatePublishedURL, outData);
		},
		
		// Cluster delete handler
		handleClusterDelete: function (item) {
			console.log("handleClusterDelete("+item+")");
			document.getElementById("deleteClusterConfirmDialog").setAttribute("data-cluster", item);
			$("#deleteClusterConfirmDialog").dialog("open");
		},

		// Cluster scale handler
		handleClusterScale: function (item) {
			
			document.getElementById("scaleClusterDialog").setAttribute("data-cluster", item);
			var clusterData = $("#cluster_" + item).data();
			var machines = parseInt($("#cluster_"+ item + " .machineinfo").children().eq(3).text());
			var scDialog = $("#scaleClusterDialog").empty();
			var dateFormat = "%e-%b-%Y-%H:%i";
			var defaultConv = new AnyTime.Converter({format:dateFormat});			
			var template = $("#scaleClusterTemplate .dialog_fieldset").clone();
			var o = new Object(); 
			scDialog.append(template);

			// Cache jQuery objects  
			o.manualProvisioningCheckbox = 			$("#scaleClusterDialog .manual_provisioning_checkbox").attr("id", "manual_provisioning_checkbox");
			o.manualProvisioningSettings = 			$("#scaleClusterDialog .manual_provisioning_settings");
			o.mbManualScaleSlider = 				$("#scaleClusterDialog .manual_scale_slider > .mb_slider").attr("id", "mb_manual_scale_slider").addClass("{startAt: "+ machines +"}");				
			o.automaticProvisioningCheckbox = 		$("#scaleClusterDialog .automatic_provisioning_checkbox").attr("id", "automatic_provisioning_checkbox");
			o.automaticProvisioningSettings = 		$("#scaleClusterDialog .automatic_provisioning_settings");
			o.clusterSizeRange = 					$("#scaleClusterDialog .cluster_size_range_select_slider > .range_select_display").attr("id", "cluster_size_range");
			o.jqClusterSizeRangeSlider =			$("#scaleClusterDialog .cluster_size_range_select_slider > .jq_slider").attr("id", "jq_cluster_size_range_slider");
			o.cpuThresholdRange = 					$("#scaleClusterDialog .cpu_load_range_select_slider >.range_select_display").attr("id", "cpu_threshold_range");
			o.jqCpuLoadThresholdsSlider = 			$("#scaleClusterDialog .cpu_load_range_select_slider > .jq_slider").attr("id", "jq_load_thresholds_slider");			
			o.scheduledScalingElements =			$("#scaleClusterDialog .scale_scheduler_datetime_picker_elements").attr("id", "scale_scheduler_datetime_picker_elements");;
			o.scehduledScaleCheckbox = 				$("#scaleClusterDialog .scehduled_scale_checkbox").attr("id", "scehduled_scale_checkbox");
			o.scaleSchedulerDatetimePickerFrom = 	$("#scaleClusterDialog .scale_scheduler_datetime_picker_from").attr("id", "scale_scheduler_datetime_picker_from");
			o.scaleSchedulerDatetimePickerTo = 		$("#scaleClusterDialog .scale_scheduler_datetime_picker_to").attr("id", "scale_scheduler_datetime_picker_to").attr("disabled", "disabled");
			o.mbScheduledSizeSlider = 				$("#scaleClusterDialog .scheduled_size_slider > .mb_slider").attr("id", "mb_scheduled_size_slider");
			
			// Initialize UI elements
			o.scaleSchedulerDatetimePickerFrom.AnyTime_noPicker();
			o.scaleSchedulerDatetimePickerFrom.AnyTime_picker({
				format:		dateFormat,
				labelTitle:	"Scheduled scaling start"
			});
			o.scaleSchedulerDatetimePickerTo.AnyTime_noPicker();
			o.scaleSchedulerDatetimePickerTo.AnyTime_picker({
				format:		dateFormat,  
				labelTitle:	"Scheduled scaling end"
			});
			o.mbManualScaleSlider.addClass("{startAt: "+ machines +"}").mbSlider({			
				minVal: clusterData.clusterMinSize,
				maxVal: clusterData.clusterMaxSize,
				grid: 1
			});							
			o.jqClusterSizeRangeSlider.slider({
				range: true,
				min: clusterData.clusterMinSize,
				max: clusterData.clusterMaxSize,
				values: [ clusterData.clusterMinSize +1 , clusterData.clusterMaxSize -1],
				slide: function( event, ui) {
					o.clusterSizeRange.text(setRangeText(ui));
				}
			});
			o.jqCpuLoadThresholdsSlider.slider({
				range: true,
				min: 0,
				max: 1,
				values: [ 0.2, 0.9 ],
				step: 0.01,
				slide: function(event, ui) {
					o.cpuThopenresholdRange.text(setRangeText(ui));
				}
			});			
			o.mbScheduledSizeSlider.mbSlider({			
				minVal: 0,
				maxVal: 100,
				grid: 1
			});
			o.clusterSizeRange.text(updateRangeText(o.jqClusterSizeRangeSlider));
			o.cpuThresholdRange.text(updateRangeText(o.jqCpuLoadThresholdsSlider));
			o.manualProvisioningSettings.hide();
			o.automaticProvisioningSettings.hide();
			o.scheduledScalingElements.hide();
			
			// Handle changes
			o.manualProvisioningCheckbox.change(toggle(o.manualProvisioningSettings));	
			o.automaticProvisioningCheckbox.change(toggle(o.automaticProvisioningSettings));
			o.scehduledScaleCheckbox.change(toggle(o.scheduledScalingElements));
			o.scaleSchedulerDatetimePickerFrom.change(function(e){
				try {
					var fromTime = defaultConv.parse(o.scaleSchedulerDatetimePickerFrom.val()).getTime();
					var oneMinute = 60000;
				    var minuteLater = new Date(fromTime + oneMinute);
				    o.scaleSchedulerDatetimePickerTo.
				        AnyTime_noPicker().
				        removeAttr("disabled").
				        val(defaultConv.format(minuteLater)).
				        AnyTime_picker({earliest: 	minuteLater,
				        				format: 	dateFormat});
				    } catch(e){ 
				    	o.scaleSchedulerDatetimePickerTo.val("").attr("disabled","disabled");
						console.log(e.message);
					}
				}
			);
			
			// Ajax callback function, updates UI elements with values from DB  
			var updateElements = function(scalingRuleData, textStatus, jqXHR){
				console.log("Ajax get rules for cluster " + item);
				if (scalingRuleData.ruleDefined){
					if (scalingRuleData.periodic){
						o.automaticProvisioningCheckbox.prop('checked', true);
						o.automaticProvisioningSettings.show();
					}
					else{
						o.automaticProvisioningCheckbox.prop('checked', false);
						o.automaticProvisioningSettings.hide();						
					}
				
					if (scalingRuleData.scheduled){
						o.scehduledScaleCheckbox.prop('checked', true);
						o.scheduledScalingElements.show();
					}
					else{
						o.scehduledScaleCheckbox.prop('checked', false);
						o.scheduledScalingElements.hide();						
					}
					o.jqClusterSizeRangeSlider.slider("option", "values", [scalingRuleData.minMachines, scalingRuleData.maxMachines]);
					o.jqCpuLoadThresholdsSlider.slider("option", "values", [scalingRuleData.minLoad, scalingRuleData.maxLoad]);	
					o.scaleSchedulerDatetimePickerFrom.val(defaultConv.format(new Date(scalingRuleData.periodFrom)));
					o.scaleSchedulerDatetimePickerTo.val(defaultConv.format(new Date(scalingRuleData.periodTo)));
					o.clusterSizeRange.text(updateRangeText(o.jqClusterSizeRangeSlider));
					o.cpuThresholdRange.text(updateRangeText(o.jqCpuLoadThresholdsSlider));
					o.mbScheduledSizeSlider.mbsetVal(scalingRuleData.scheduledSize);
				}
			};	
			
			// Utility functions
			
			// Creates range text by using slider
			function updateRangeText(ui){
				var text = ui.slider( "values", 0 ) + " - "	+ ui.slider( "values", 1 );
				return text;
			}
			
			// Creates range text  based on values of slider's handles.
			function setRangeText(ui){
				var text = ui.values[ 0 ] + " - " + ui.values[ 1 ];
				return text;
			}
			
			// Shows or hides html elements
			function toggle(elements) {
				return function () {
					if($(this).attr('checked'))
						elements.show("blind", {}, 500, null);
					else 
						elements.hide("blind", {}, 500, null);	
				};
			}
			
			$.ajax({
				  url: portletURL.url.cluster.getClusterScalingRuleURL,
				  dataType: 'json',
				  data: {clusterId: item},
				  success: updateElements,
				});
			
			scDialog.dialog("open");
			o.manualProvisioningCheckbox.blur();
		},

		// Cluster configure 
		initClusterConfigureDialog: function (item) {
			
			// Init common dialog elements
			var count = 0;
			var o = new Object(); 
			o.dialog = $("#configureClusterDialog").empty();
			o.dialog.data('clusterId', item);
			o.dialog.append($("#configClusterTemplate .clusterConfigureContent").clone(true));
			
			// Init elastic IP configuration elements 
			o.elasticIpConfiguration = $("#configureClusterDialog elasticIpConfiguration");
			o.elasticIpSelect = $("#configureClusterDialog .elasticIpSelect").hide();
			o.elasticIpSelectLabel = $("#configureClusterDialog .elasticIpSelectLabel").hide();
			o.elasticIpLabel = $("#configureClusterDialog .elasticIpLabel").hide();
			o.elasticIpStatusBar = $("#configureClusterDialog .elasticIpStatusBar").hide();

			o.elasticIpUseButton = $("#configureClusterDialog .elasticIpUseButton").hide();
			o.elasticIpDropButton = $("#configureClusterDialog .elasticIpDropButton").hide();
			
			// TODO: backend implementation not ready to support sticky sessions
			// Sticky session 
			//o.useStickySessionsCheckBox = $("#configureClusterDialog .useStickySessionsCheckBox").hide();
			//o.useStickySessionsLabel = $("#configureClusterDialog .useStickySessionsLabel").hide();
			
			// Init elements for configuration of authorized connections
			o.ipAddressInput = $("#configureClusterDialog .ipAddressInput");
			o.portFromInput = $("#configureClusterDialog .portFromInput");
			o.portToInput = $("#configureClusterDialog .portToInput");
			o.addConnectionButton = $("#configureClusterDialog .addConnectionButton");
			o.openConnectionsList = $("#configureClusterDialog .openConnectionsList").hide();
			
			o.cidrPrefixSelect = $("#configureClusterDialog .cidrPrefixSelect");
			o.protocolSelect = $("#configureClusterDialog .protocolSelect");
			for (var i = 31; i >= 0; i--) {
				o.cidrPrefixSelect.append('<option>' + i + '</option>');
			}
			o.deleteConnectionButton =  new Array();
			
			try{
				// Check if elastic IP is used already for cluster
				$.ajax({
				url: portletURL.url.cluster.getElasticIPForClusterURL,
				dataType: 'json',
				data: {clusterId: item},
				success: function(elasticIp, textStatus, jqXHR) {
					//o.useStickySessionsCheckBox.show();
					//o.useStickySessionsLabel.show();
					console.log("elasticIP =" + elasticIp);
					if(elasticIp == -1){
						o.elasticIpSelectLabel.text("Internal error: elastic IP management function is not available").show();
					} else if (!(elasticIp == null || elasticIp =='undefined')) {
						o.elasticIpLabel.text(elasticIp.ipAddress).show();
						o.elasticIpSelectLabel.text("Disassociate elastic IP address with cluster").show();
						o.elasticIpDropButton.show();
					} else{
						getAndShowElasticIPs();
					}
				}
				});		
				// Get open connections	
				$.ajax({
					url: portletURL.url.cluster.getUserAuthorizedIPsURL,
					dataType: 'json',
					data: {clusterId: item},
					success: function(data, textStatus, jqXHR) {
						$.each(data, function(key,val) {
							var cidrIp = val.cidrIp;
							var parsed = cidrIp.split("/");
							addConnection(++count, parsed[0], parsed[1], val.fromPort, val.toPort, val.id, val.protocol);
						});
					}
				});
			} catch(e){
				console.log("Exception thrown: " + e.message);
			}	
			
			// Callbacks
			o.elasticIpUseButton.on("click", function(event){
				var outData = {};
				outData["clusterId"] = item;
				outData["ipId"] = parseInt(o.elasticIpSelect.val());
				
				o.elasticIpStatusBar.show();
				o.elasticIpSelect.hide();
				o.elasticIpUseButton.hide();
				
				function setElasticIp(){
					$.ajax({
						url: portletURL.url.cluster.setElasticIPURL,
						data: outData,
						success: function(data, textStatus, jqXHR) {
							console.log(data);
							o.elasticIpLabel.text(o.elasticIpSelect.find("option:selected").text()).show();
							o.elasticIpDropButton.show();
							o.elasticIpStatusBar.hide();
							o.elasticIpSelectLabel.text("Disassociate elastic IP address with cluster").show();
						},
						error: function (response) {
							o.elasticIpStatusBar.hide();
							var r = jQuery.parseJSON(response.responseText);
							alert("Failed to associate elastic IP address with cluster.");
							console.log(r.Message);
							console.log(r.StackTrace);
							console.log(r.ExceptionType);
						}
					});	
				}
				window.setTimeout(setElasticIp, 8000);
			});
			o.elasticIpDropButton.on("click", function(event){
				var outData = {};
				outData["clusterId"] = item;
				o.elasticIpStatusBar.show();
				o.elasticIpLabel.hide();
				o.elasticIpDropButton.hide();

				function dropElasticIp(){
					$.ajax({
						url: portletURL.url.cluster.removeElasticIPURL,
						data: outData,
						success: function(data, textStatus, jqXHR) {
							console.log(data);
							o.elasticIpStatusBar.hide();
							getAndShowElasticIPs();
						},
						error: function (response) {
							o.elasticIpStatusBar.hide();
							var r = jQuery.parseJSON(response.responseText);
							alert("Failed to disassociate elastic IP address from cluster.");
							console.log(r.Message);
							console.log(r.StackTrace);
							console.log(r.ExceptionType);
						}
					});
				}
				
				window.setTimeout(dropElasticIp, 8000);

			});			
			o.addConnectionButton.on("click", function(event){
				count ++;
				var ipAddress = o.ipAddressInput.val();
				var portFrom = parseInt(o.portFromInput.val());
				var portTo = parseInt(o.portToInput.val());
				var cidr = o.cidrPrefixSelect.val();
				var protocol = o.protocolSelect.val();
				if (!(isDottedIPv4(ipAddress) 
						&& isTcpPort(portFrom) 
						&& isTcpPort(portTo)
						&& portFrom <= portTo)){
					alert("Invalid IP address or port");
					return;
				}	
				o.ipAddressInput.val("");
				o.portFromInput.val("");
				o.portToInput.val("");
				o.cidrPrefixSelect.val("32");
				o.protocolSelect.val("tcp");
	
				var outData = {};
				outData['clusterId'] = item;
				outData['cidrIp'] = ipAddress + "/" + cidr;
				outData['portFrom'] = portFrom.toString();
				outData['portTo'] = portTo.toString();
				outData['protocol'] = protocol;
				$.ajax({
					url: portletURL.url.cluster.addUserAuthorizedIPURL,
					data: outData,
					success: function(data, textStatus, jqXHR) {
						console.log(data);
						if (!(data == null || data =='undefined')){
							addConnection(count, ipAddress, cidr, portFrom, portTo, -1, protocol);
							var newConnectionObj = o.openConnectionsList.find("#openConnection" + count);
							newConnectionObj.data("ipData").ipId = data;
							console.log(newConnectionObj.data());
						}
						else{
							alert("Failed to add ingress rule to the cluster");
						}
					},
					error: function (response) {
						var r = jQuery.parseJSON(response.responseText);
						alert("Failed to add ingress rule to the cluster");
						console.log(r.Message);
						console.log(r.StackTrace);
						console.log(r.ExceptionType);
					}
					});
			});
			
			// Helper functions
			function isDottedIPv4(s){
				var match = s.match(/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/);
				return match != null && match[1] <= 255 && match[2] <= 255 && match[3] <= 255 && match[4] <= 255;
			}
			
			function isTcpPort(port){
				if (port < 1 || port > 65535 || isNaN(port)) return false;
				else return true;		
			}
			
			function addConnection(count, ipAddress, cidr, portFrom, portTo, ipId, protocol){
				var newConnection = "openConnection" + count;
				var buttonName = "deleteConnectionButton" + count;

				o.openConnectionsList.append('<div id ="' + newConnection +'"class=newConnectionRow></div>');
				var newConnectionObj = o.openConnectionsList.find("#" + newConnection);
				newConnectionObj
					.append('<div class="ipEntry">' + ipAddress + "/"  + cidr +'</div>')
					.append('<div class="portFromEntry">' + portFrom + '</div>')
					.append('<div class="separatorEntry" >-</div>')
					.append('<div class="portToEntry">' + portTo + '</div>')
					.append('<div class="protocolEntry">' + protocol + '</div>');
		
				newConnectionObj.append('<a href="#" id ="' + buttonName + '" class = "deleteConnectionButton ipButton">' + "-" + '</a>');
				newConnectionObj.data("ipData",{
					"ipAddress"	: ipAddress,
					"cidr"		: cidr,
					"portFrom"	: portFrom,
					"portTo"	: portTo,
					"ipId"		: ipId,
					"protocol"	: protocol
				});
				o.openConnectionsList.show();
				o.deleteConnectionButton[count] =  $("#" + buttonName); 
				o.deleteConnectionButton[count].on("click", function(event){
					var outData = {};
					outData['ipId'] = newConnectionObj.data("ipData").ipId;
					outData['clusterId'] = item;
					outData['cidrIp'] = newConnectionObj.data("ipData").ipAddress + "/" + newConnectionObj.data("ipData").cidr;
					outData['portFrom'] = newConnectionObj.data("ipData").portFrom;
					outData['portTo'] = newConnectionObj.data("ipData").portTo;
					outData['protocol'] = newConnectionObj.data("ipData").protocol;
							
					try{
						$.ajax({
							url: portletURL.url.cluster.deleteUserAuthorizedIPURL,
							data: outData,
							success: function(data, textStatus, jqXHR) {
								console.log("deleteUserAuthorizedIPsURL reply");
								console.log(data);
								if (!(data == null || data =='undefined')){
									newConnectionObj.remove();
									if ($("#configureClusterDialog .newConnectionRow").length == 0) o.openConnectionsList.hide();
								}
								else
									alert("Failed to revoke ingress rule for the cluster");
							},
							error: function (response) {
								var r = jQuery.parseJSON(response.responseText);
								alert("Failed to revoke ingress rule for the cluster");
								console.log(r.Message);
								console.log(r.StackTrace);
								console.log(r.ExceptionType);
							}
						});
					}
					catch(e){
						console.log("Exception thrown: " + e.message);
		                initClusterConfigureDialog
		            }
				});
			}
			
			function getAndShowElasticIPs(){
				$.getJSON(portletURL.url.cluster.getElasticIPListURL, function(data) {
					if (data.length==0) {
						o.elasticIpSelectLabel.text("Note: elastic IPs are not available").show();
						o.elasticIpSelect.hide();
						o.elasticIpUseButton.hide();
					}else{
						o.elasticIpSelect.empty();
						o.elasticIpSelectLabel.text("Associate elastic IP with cluster").show();
						o.elasticIpSelect.show();
						o.elasticIpUseButton.show();
						$.each(data, function(key,val) {
							console.log(data);
							o.elasticIpSelect.append(' <option value="' +val.id + '">' + val.ipAddress + '</option>');
						});
					}
				});
			}	
			o.dialog.dialog("open");
		},
		
		// Updates clusterbar machine information
		updateClusterMachineInfo: function (id) {
			var urlStatus = portletURL.url.cluster.getClusterStatusURL + "&clusterId="+id+"&rnd="+Math.random(); 
			var $info = $("#cluster_"+id+" .machineinfo").children();
			
			$.getJSON(urlStatus, function(data) { 
				$.each(data, function(key,val) {					
					if(key == "machines_with_errors") {
						if(val > 0) {
							$info.eq(2).empty();
							$info.eq(2).append(val);
							$info.eq(2).parent().show();							
						} else {
							$info.eq(2).parent().hide();									
						}
					}
					if(key == "configured_machines") {	
						if(val > 0) {
							$info.eq(0).empty();
							$info.eq(0).append(val);
							$info.eq(0).parent().show();
						} else {
							$info.eq(0).parent().hide();									
						}

					}
					if(key == "starting_machines") {
						if(val > 0) {
							$info.eq(1).empty();
							$info.eq(1).append(val);
							$info.eq(1).parent().show();							
						} else {
							$info.eq(1).parent().hide();									
						}						
					}
					if(key == "total_machines") {
						$info.eq(3).empty();
						$info.eq(3).append(val);
					}
				});
			});		

		},
		
		
		// Create a new object presenting tab
		tabObj: function (id, tab) {
			this.id=id;
			this.tabName=tab;
		},
		
		// Create a new object presenting cluster
		clusterObj: function (clusterId, listenerHandle) {
			this.clusterId = clusterId;
			this.listenerHandle = listenerHandle;
		}
		
};
