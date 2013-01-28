
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
				$.ajax({dataType: "json", url: portletURL.url.instance.getClusterTypesURL}),
				$.ajax({dataType: "json", url: portletURL.url.instance.getMachineTypesURL}))
				.done(function(resultClusterTypes, resultMachineTypes){
					clusters = cloudadmin.resource.clusterTypes = resultClusterTypes[0]; 
					cloudadmin.resource.machineTypes = resultMachineTypes[0]; 
					
					var o = new Object();
					o.dialog = $("#addInstanceDialog");
					o.accordion = $("#cloudTypesSelectionAccordion");
					
					// The main creation loop. Reads all available cluster/platform types and create accordion segment for each
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
					$("#addInstanceDialog .valueDisplayButtonSet").text(cloudadmin.resource.machineTypes[0]);
	
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
						var cloud = $('#cloudSelect option:selected').val();
						var url = portletURL.url.instance.getCloudZonesURL+"&cloud="+cloud+"&rnd="+Math.random();
						var options = '';
					
						$.getJSON(url, function(data) {
							$.each(data, function(key,val) {
								options += '<option value="'+key+'">'+val+'</option>';
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
							if  (dependency != -1){
								var depenentPlatformContainer = $("#" +  cloudadmin.resource.clusterTypes[dependency].name);
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
							grandpa.find(".toggleEbsRow :radio").attr("disabled", false).button("refresh");
							if ($('#' + "toggleEbsRadioOn_" + grandpa.data('clusterConfiguration').name).attr('checked')){
								grandpa.find(".ebsSizeRow").fadeTo(500, "1");	
								grandpa.find(".jq_slider").slider({ disabled: false });	
							}
						}
						else if (el.attr("id").indexOf("togglePlatformRadioOff") !=  -1) {
							var grandpa = toggleGrandunclesClass(el, "unselect", 0);
							var dependency = grandpa.data('clusterConfiguration').dependency;
							var myId = grandpa.data('clusterConfiguration').id;
							if  (dependency != -1){
								var depenentPlatformContainer = $("#" +  cloudadmin.resource.clusterTypes[dependency].name);
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
							grandpa.find(".machineSizeRow :radio").attr("disabled", true).button("refresh");
							grandpa.find(".toggleEbsRow :radio").attr("disabled", true).button("refresh");
						}	
						el.siblings().attr('checked',false).button("refresh");
						el.attr('checked',true).button("refresh");			 
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
					
					$("#addInstanceDialog .machineSizeRow :radio").change(function(e) {
						$(this).parent().next().text(cloudadmin.resource.machineTypes[$(this).attr("value")]);
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
		
		// This fucntion is called when dialog is already created. It is called from instances page, 
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
			$('#addInstanceDialog .toggleEbsRow input[id*="toggleEbsRadioOff_"]').attr('checked',true).button("refresh");
			$("#addInstanceDialog .machineSizeRow input:first-child").attr('checked',true).button("refresh");
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
				}
				for(var i = 0; i < clusters.length; i++){
					if($('#' + "togglePlatformRadioOn_" + clusters[i].name).attr('checked')){
						outData[clusters[i].name] 				  	= "true";
						// TODO: There is a bug somewhere, values can't be read from slider correctly as documented in jQuery API docs, 
						// workaround is used
						//outData[clusters[i].name + "clustersize"] 	= $('#' + clusters[i].name + ' .clusterSizeRow .jq_slider').slider("value");
						outData[clusters[i].name + "clustersize"] 	= $('#' + clusters[i].name + ' .clusterSizeRow .jq_slider').parent().next().text();
	
						outData[clusters[i].name + "machinesize"] 	= machineSize(clusters[i].name, 1);
						// clusters with replication
						if (clusters[i].replicated == true){
							outData[clusters[i].name + "replclustersize"] 	= $('#' + clusters[i].name + ' .replicationClusterSizeRow .jq_slider').parent().next().text();
							outData[clusters[i].name + "replmachinesize"] 	= machineSize(clusters[i].name, 2);
						}
						
						if($('#' + "toggleEbsRadioOn_" + clusters[i].name).attr('checked')){
							outData[clusters[i].name + "esbvolumesize"] 	= $('#' + clusters[i].name + ' .ebsSizeRow .jq_slider').parent().next().text();
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
		},						
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
	}
	
})(jQuery);