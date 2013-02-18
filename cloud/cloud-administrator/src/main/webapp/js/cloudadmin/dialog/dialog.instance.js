
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
			$.when(
				$.ajax({dataType: "json", url: portletURL.url.instance.getCloudProvidersURL}),
				$.ajax({dataType: "json", url: portletURL.url.instance.getClusterTypesURL}),
				$.ajax({dataType: "json", url: portletURL.url.instance.getMachineTypesURL}))
				.done(function(resultCloudProviders, resultClusterTypes, resultMachineTypes) {
					var cloudProviders = cloudadmin.resource.cloudProviders = resultCloudProviders[0];
					var clusters = cloudadmin.resource.clusterTypes = resultClusterTypes[0];
					var machineTypes = cloudadmin.resource.machineTypes = resultMachineTypes[0];
					
					var o = new Object();
					o.dialog = $("#addInstanceDialog");
					o.accordion = $("#cloudTypesSelectionAccordion");
					
					// populates cloud provider options
					var cloudSelect = o.dialog.find("#cloudSelect");
					$.each(cloudProviders, function(index, provider) {
						cloudSelect.append("<option value='" + provider.id + "'>" + provider.name + "</option>");
					});
					
					// The main creation loop of platforms. Reads all available cluster/platform types and create accordion segment for each
					for(var i = 0; i < clusters.length; i++){
						var header = $("#clusterConfigurationTemplate .clusterTypeConfigurationHeader").clone();
						var body = $("#clusterConfigurationTemplate .clusterTypeConfigurationBody").clone();	
						if(clusters[i].replicated == true){
							var replicationClusterSizeRow = $("#clusterConfigurationTemplate .clusterTypeConfigurationBody .clusterSizeRow").clone();
							var replicationMachineSizeRow = $("#clusterConfigurationTemplate .clusterTypeConfigurationBody .machineSizeRow").clone();		
							replicationClusterSizeRow.attr('class', 'replicationClusterSizeRow configRow').find("label").html("Repl. cluster size");
							replicationMachineSizeRow.attr('class', 'machineSizeRow configRow replication');
							replicationMachineSizeRow.find(".instanceCreationLabel").html("Repl. machine size");
							replicationMachineSizeRow.find('[type="radio"]').each(function () {
								$(this).attr('id', "replication" + $(this).attr('id'));
								$(this).attr('name', "replication" + $(this).attr('name'));
								var label = $(this).next("label");
								label.attr('for', "replication" + label.attr('for'));
							});		
							body.find(".machineSizeRow").after(replicationMachineSizeRow).after(replicationClusterSizeRow);
						}				
						header.find(".clusterTypeTitle").html(clusters[i].title);

						// insert data source fields into body
						if ("jbossservice" == clusters[i].name || "jbossportal" == clusters[i].name) {
							var datasource = $('#datasourceTemplate .datasourceBody').clone();
							datasource.find('[type="text"]').each(function () {
								$(this).attr('id', $(this).attr('id') + clusters[i].name);
								$(this).attr('name', $(this).attr('name') + clusters[i].name);
							});
							datasource.find('[type="password"]').each(function () {
								$(this).attr('id', $(this).attr('id') + clusters[i].name);
								$(this).attr('name', $(this).attr('name') + clusters[i].name);
							});
							body.find('.ebsSizeRow').after(datasource);
						}
						// insert staging (liveInstance) fields into body
						if ("jbossportal" == clusters[i].name) {
							var datasource = $('#liveInstanceTemplate .liveInstanceBody').clone();
							body.find('.ebsSizeRow').after(datasource);
						}
						// insert machine types into body before element ids and names are adjusted below
						var machineTypeInjectLocation$ = body.find('.machineSizeRow .radioButton');
						for (var mt = 0; mt < machineTypes.length; ++mt) {
							var machineTypeInstanceId = 'machineSizeRadio' + machineTypes[mt].name + '_';
							$('#machineTypeTemplate').children('[type="radio"]').clone().attr({id: machineTypeInstanceId, value: machineTypes[mt].id}).appendTo(machineTypeInjectLocation$);
							$('#machineTypeTemplate').children('label').clone().attr({'for': machineTypeInstanceId}).html(machineTypes[mt].name).appendTo(machineTypeInjectLocation$);
						}
						// prepares element ids and names
						body.attr('id', clusters[i].name).find('[type="radio"]').each(function () {
							$(this).attr('id', $(this).attr('id') + clusters[i].name);
							$(this).attr('name', $(this).attr('name') + clusters[i].name);
							var label = $(this).next("label");
							label.attr('for', label.attr('for') + clusters[i].name);
						});
						body.data('clusterConfiguration', cloudadmin.resource.clusterTypes[i]);
						o.accordion.append(header);
						o.accordion.append(body);				
					} 
					// End of creation loop
					
					// Initialize other widgets 
					$("#addInstanceDialog .radioButton").buttonset();
                    if (cloudadmin.resource.machineTypes.length > 0)
					    $("#addInstanceDialog .valueDisplayButtonSet").text(cloudadmin.resource.machineTypes[0].specification);
	
					o.accordion.accordion({collapsible: true, autoHeight:false, heightStyle: "content", active:false});
					o.dialog.dialog({		
						autoOpen : false,
						height : 745,
						width : 710,
						modal : true,
						buttons : cloudadmin.dialog.instanceAddButtons
					});	
					
					dimElements();		
	
					// Events
					$("#cloudSelect").change(function() {
						var cloudId = $('#cloudSelect option:selected').val();
						if (!cloudId) { // empty selection was made
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
	
					$("#addInstanceDialog .togglePlatformSelectionRow :radio").change(function(e) {
						var el = $(this);
						if (el.attr("id").indexOf("togglePlatformRadioOn") !=  -1) {
							//el.parents(".togglePlatformSelectionRow").next().find(".sliderMask").css("display", "none").prev().fadeTo(1500, "1");
							var grandpa = toggleGrandunclesClass(el, "select", 0);
							var dependency = grandpa.data('clusterConfiguration').dependency;				
							if  (dependency != -1) {
								var requiredClusterType = findClusterTypeById(dependency);
								var depenentPlatformContainer = $("#" +  requiredClusterType.name);
								var dependentTogglePlatformOnRadio = depenentPlatformContainer.find('input[id*="togglePlatformRadioOn_"]');
								dependentTogglePlatformOnRadio.attr('checked',true).button("refresh");
								// make dependency selected
								toggleGrandunclesClass(dependentTogglePlatformOnRadio, "select", 0);
								// make dependency impossible to unselect
								depenentPlatformContainer.find('.togglePlatformSelectionRow :radio').attr("disabled", true).button("refresh");
							}
							// Styling
							grandpa.find(".configRow").fadeTo(500, "1");
							grandpa.find(".clusterSizeRow .jq_slider").slider({ disabled: false});	
							grandpa.find(".replicationClusterSizeRow  .jq_slider").slider({ disabled: false});	
							grandpa.find(".machineSizeRow :radio").attr("disabled", false).button("refresh");
							grandpa.find(".imageTypeRow :radio").attr("disabled", false).button("refresh");
							grandpa.find(".toggleEbsRow :radio").attr("disabled", false).button("refresh");
							grandpa.find(".toggleDatasourceRow :radio").attr("disabled", false).button("refresh");
							grandpa.find(".toggleLiveInstanceRow :radio").attr("disabled", false).button("refresh");
							if ($('#' + "toggleEbsRadioOn_" + grandpa.data('clusterConfiguration').name).attr('checked')){
								grandpa.find(".ebsSizeRow").fadeTo(500, "1");	
								grandpa.find(".jq_slider").slider({ disabled: false });	
							}
							if ($('#' + "toggleDatasourceRadioOn_" + grandpa.data('clusterConfiguration').name).attr('checked')){
								grandpa.find(".datasourceRow").fadeTo(500, "1");
								grandpa.find(".datasourceRow :text").attr("disabled", false);
								grandpa.find(".datasourceRow :password").attr("disabled", false);								
							}							
						}
						else if (el.attr("id").indexOf("togglePlatformRadioOff") !=  -1) {
							var grandpa = toggleGrandunclesClass(el, "unselect", 0);
							var dependency = grandpa.data('clusterConfiguration').dependency;
							var myId = grandpa.data('clusterConfiguration').id;
							if  (dependency != -1){
								var requiredClusterType = findClusterTypeById(dependency);
								var dependentPlatformContainer = $("#" +  requiredClusterType.name);
								// check if some other platform also depends on "dependent platform"
								var found = false;
								for(var i = 0; i < clusters.length; i++){
									if (clusters[i].dependency == dependency  &&  clusters[i].id != myId){
										// Check if it is active. If one is active no clearing is done.
										 if  ($('#togglePlatformRadioOn_' + clusters[i].name).attr('checked')) {
											found  = true;
											break;
										} else continue;
									}
								}
								// make dependency possible to unselect
								if (!found ) depenentPlatformContainer.find('.togglePlatformSelectionRow :radio').attr("disabled", false).button("refresh");
							}
							grandpa.find(".jq_slider").slider({ disabled: true});		
							grandpa.find(".configRow").fadeTo(500, ".5");
							grandpa.find(".ebsSizeRow").fadeTo(500, ".5");	
							grandpa.find(".datasourceRow").fadeTo(500, ".5");							
							grandpa.find(".machineSizeRow :radio").attr("disabled", true).button("refresh");
							grandpa.find(".imageTypeRow :radio").attr("disabled", true).button("refresh");
							grandpa.find(".toggleEbsRow :radio").attr("disabled", true).button("refresh");
							grandpa.find(".toggleDatasourceRow :radio").attr("disabled", true).button("refresh");
							grandpa.find(".toggleLiveInstanceRow :radio").attr("disabled", true).button("refresh");
							grandpa.find(".datasourceRow :text").attr("disabled", true);
							grandpa.find(".datasourceRow :password").attr("disabled", true);
						}
						el.siblings().attr('checked',false).button("refresh");
						el.attr('checked',true).button("refresh");		
						
						function findClusterTypeById(clusterId) {
							var matchedTypes = $.grep(cloudadmin.resource.clusterTypes, function(obj) {
								return obj.id == clusterId;	
							});
							if (matchedTypes.length != 0)
								return matchedTypes[0];
						}
					 });
					
					$("#addInstanceDialog .toggleEbsRow :radio").change(function(e) {
						var el = $(this);		
						var sliderRow = el.parent().parent().next();
						var jqSlider = sliderRow.find(".jq_slider");
	
						if (el.attr("id").indexOf("toggleEbsRadioOff") !=  -1) {
							sliderRow.fadeTo(500, ".5");
							jqSlider.slider({ disabled: true});		
						}
						else{
							sliderRow.fadeTo(500, "1");
							jqSlider.slider({ disabled: false});		
						}
					});

					$("#addInstanceDialog .toggleDatasourceRow :radio").change(function(e) {
						var el = $(this);
						var datasourceRow = el.parent().parent().next();
	
						if (el.attr("id").indexOf("toggleDatasourceRadioOff") !=  -1) {
							datasourceRow.fadeTo(500, ".5");							
							datasourceRow.find(":text").attr("disabled", true);
							datasourceRow.find(":password").attr("disabled", true);
							
						}
						else{
							datasourceRow.fadeTo(500, "1");
							datasourceRow.find(":text").attr("disabled", false);
							datasourceRow.find(":password").attr("disabled", false);
						}
					});
										
					$("#addInstanceDialog .machineSizeRow :radio").change(function(e) {
						$(this).parent().next().text(cloudadmin.resource.machineTypes[$(this).attr("value")].specification);
					});	
							
					 // Helper functions
					function  toggleGrandunclesClass(el, mode, delay){
						var grandpa = el.parents(".ui-accordion-content");
						var granduncle = grandpa.prev();
						if(mode === "select"){
							granduncle.addClass("platformSelected",delay).removeClass("platformNotSelected");}
						else  if (mode === "unselect"){
							granduncle.addClass("platformNotSelected").removeClass("platformSelected",delay);
						}
						return grandpa;
					}
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
			$("#addInstanceDialog .radioButton input").attr('checked',false).button("refresh");
			$('#addInstanceDialog .togglePlatformSelectionRow input[id*="togglePlatformRadioOff_"]').attr('checked',true).button("refresh");
			$('#addInstanceDialog .imageTypeRow input[id*="imageTypeEphemeral_"]').attr('checked',true).button("refresh");
			$('#addInstanceDialog .toggleEbsRow input[id*="toggleEbsRadioOff_"]').attr('checked',true).button("refresh");
			$("#addInstanceDialog .machineSizeRow input:first-child").attr('checked',true).button("refresh");
			$('#addInstanceDialog .toggleDatasourceRow input[id*="toggleDatasourceRadioOff_"]').attr('checked',true).button("refresh");			
			$('#addInstanceDialog .toggleLiveInstanceRow input[id*="toggleLiveInstanceRadioOff_"]').attr('checked',true).button("refresh");			
			$('#addInstanceDialog .datasourceRow input').val('');
			dimElements();
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
					outData[clusters[i].name + "datasourceurl"] = "";
				}
				for(var i = 0; i < clusters.length; i++){
					if($('#' + "togglePlatformRadioOn_" + clusters[i].name).attr('checked')){
						outData[clusters[i].name] = "true";
						// TODO: There is a bug somewhere, values can't be read from slider correctly as documented in jQuery API docs, 
						// workaround is used
						// should be like this: 
						// outData[clusters[i].name + "clustersize"] 	= $('#' + clusters[i].name + ' .clusterSizeRow .jq_slider').slider("value");
						outData[clusters[i].name + "clustersize"] = $('#' + clusters[i].name + ' .clusterSizeRow .jq_slider').parent().next().text();
						outData[clusters[i].name + "machinesize"] = machineSize(clusters[i].name, 1);
						if (clusters[i].replicated == true){
							outData[clusters[i].name + "replclustersize"] = $('#' + clusters[i].name + ' .replicationClusterSizeRow .jq_slider').parent().next().text();
							outData[clusters[i].name + "replmachinesize"] = machineSize(clusters[i].name, 2);
						}
						if($('#' + "imageTypeEphemeral_" + clusters[i].name).attr('checked')){
							outData[clusters[i].name + "imagetype"] = "0";
						}
						else
							outData[clusters[i].name + "imagetype"] = "1";
						if($('#' + "toggleEbsRadioOn_" + clusters[i].name).attr('checked')){
							outData[clusters[i].name + "esbvolumesize"] = $('#' + clusters[i].name + ' .ebsSizeRow .jq_slider').parent().next().text();
						}
						if($('#' + "toggleLiveInstanceRadioOn_" + clusters[i].name).attr('checked')){
							outData[clusters[i].name + "liveinstance"] = "true";
						}
						else {
							outData[clusters[i].name + "liveinstance"] = "false";
						}
						if($('#' + "toggleDatasourceRadioOn_" + clusters[i].name).attr('checked')){
							if (!validateField($('#' + "newDatasourceUrlText_" + clusters[i].name))) return;
							if (!validateField($('#' + "newDatasourceUserNameText_" + clusters[i].name))) return;
							if (!validateField($('#' + "newDatasourcePasswordText_" + clusters[i].name))) return;
							outData[clusters[i].name + "datasourceurl"] = $('#' + "newDatasourceUrlText_" + clusters[i].name).val();
							outData[clusters[i].name + "datasourceuser"] = $('#' + "newDatasourceUserNameText_" + clusters[i].name).val();
							outData[clusters[i].name + "datasourcepassword"] = $('#' + "newDatasourcePasswordText_" + clusters[i].name).val();
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
	
	function machineSize(clusterName, machineType){
		var ret = 0;
		var rowClass = "";
		machineType== 1 ? rowClass = ".ordinaryMachine": rowClass = ".replicationMachine";
		$('#' + clusterName + ' ' +  rowClass + ' .radioButton input').each(function(){	
			if ($(this).attr('checked')){
				ret = $(this).attr("value");
				return false;
			}
		});
		return ret;
	}
	
	function dimElements(){
		$("#addInstanceDialog .jq_slider").slider({ disabled: true }).css("opacity", "1");
		$("#addInstanceDialog .clusterSizeRow").css("opacity", ".5");
		$("#addInstanceDialog .configRow").css("opacity", ".5");	
		$("#addInstanceDialog .ebsSizeRow").css("opacity", ".5");			
		$("#addInstanceDialog .datasourceRow").css("opacity", ".5");			
	}
	
})(jQuery);