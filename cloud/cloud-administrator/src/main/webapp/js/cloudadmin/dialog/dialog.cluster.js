(function($) {
	console.log("initializing cloudadmin.dialog.cluster");
	var cloudadmin = window.cloudadmin || {};
	function getCheckboxValue (element) {
		if (element.length == 1) return true;
		else return false;
	}
	$.extend(cloudadmin.dialog, {
			
		// Show cluster information dialog
		showClusterInformation: function (id) {
			var urlData = portletURL.url.cluster.getClusterInfoURL + "&clusterId="+id+"&rnd="+Math.random();

			//Clear cluster data
			$("#clusterdatatable tr:gt(0)").remove();
			
			$.getJSON(urlData, function(data) { 
					$.each(data, function(key,val) {					
						$('#clusterdatatable > tbody:last').append('<tr><td>' + key + '</td><td>' + val + '</td></tr>');
					});
			});
			
			cloudadmin.dialog.updateClusterStatusTable(id);
			
			$("#clusterdatatable tr:even").addClass("odd");
			$("#clusterstatustable tr:even").addClass("odd");
			
		    $("#clusterdialog").dialog("open");
		},

		updateClusterStatusTable: function (id) {
			var urlStatus = portletURL.url.cluster.getClusterStatusURL + "&clusterId="+id+"&rnd="+Math.random(); 

			$("#clusterstatustable tr:gt(0)").remove();
			
			$.getJSON(urlStatus, function(data) { 
				$.each(data, function(key,val) {				
					if(key == "configured_machines")
						$('#clusterstatustable > tbody:last').append('<tr><td>Configured Machines</td><td>'+val+'</td></tr>');
					if(key == "machines_with_errors")
						$('#clusterstatustable > tbody:last').append('<tr><td>Machines with errors</td><td>'+val+'</td></tr>');
					if(key == "running_machines")
						$('#clusterstatustable > tbody:last').append('<tr><td>Running Machines</td><td>'+val+'</td></tr>');
					if(key == "total_machines")
						$('#clusterstatustable > tbody:last').append('<tr><td>Total Machines</td><td>'+val+'</td></tr>');				
				});
			});
			
		},
		
		// Show cluster configuration dialog
		showClusterConfiguration: function () {
			var urlData = portletURL.url.cluster.getElasticIPListURL + "&rnd="+Math.random();
			
			var select = $("#configureClusterDialog  select");
			$.getJSON(urlData, function(data) {
				$.each(data, function(key,val) {
					console.log("key: " + key + " val: " + val );
					select.append(' <option>' + val + '</option>');
					//TODO: lisää vastaukset elastic-ip select boksiin
					//select.
				});
			});
			
		}
	});
	
	
	// Initialize the dialogs
	$("#clusterdialog").dialog({
		autoOpen: false,
		modal: true,
		width: 475,
		height: 340
	});
	
	$("#configureClusterDialog").dialog({
		autoOpen: false,
		modal: true,
		width: 395,
		height: 540,
	});
		
	$("#scaleClusterDialog").dialog({
		autoOpen: false,
		resizable: false,
		modal: true,
		width: 460,
		height: 510,
		buttons: {
			"Scale cluster": function() {
				var outData = {};
				var outJson = {};
				var clusterId = this.getAttribute("data-cluster");				 
				var dateFormat = "%e-%b-%Y-%H:%i";
				var defaultConv = new AnyTime.Converter({format:dateFormat});
				
				function getDateTimePickerValue (element) {
					if(element.val().length > 0) return defaultConv.parse(element.val()).getTime();
					else return 0;
				}
				try {
					//JSON.stringify({ "userName": userName, "password" : password })
					
					
					outData = {
						'cluster':						 clusterId,
						'periodicScalingOn':			 getCheckboxValue($("#automatic_provisioning_checkbox:checked")),
						'scheduledScalingOn':			 getCheckboxValue($("#scehduled_scale_checkbox:checked")),
						'scheduledScalingState':		 1,
						'maxNumberOfMachinesPerCluster': $("#jq_cluster_size_range_slider").slider("values", 1),
						'minNumberOfMachinesPerCluster': $("#jq_cluster_size_range_slider").slider("values", 0),
						'maxLoad':			 			 $("#jq_load_thresholds_slider").slider("values", 1),
						'minLoad':	    	 			 $("#jq_load_thresholds_slider").slider("values", 0),
						'periodFrom':				     getDateTimePickerValue($("#scale_scheduler_datetime_picker_from")),
						'periodTo':				         getDateTimePickerValue($("#scale_scheduler_datetime_picker_to")),
						'scheduledClusterSize':		     $("#mb_scheduled_size_slider").mbgetVal(),
						'clusterSizeOriginal':			 -1,
						'jobId':						 -1,
						'manualScaling':				 getCheckboxValue($("#manual_provisioning_checkbox:checked")),
						'machineCount':				     $("#mb_manual_scale_slider").mbgetVal()
					};
					
					outJson = JSON.stringify(outData);
					/*
					outData['cluster'] 						 = clusterId;
					outData['periodicScalingOn'] 			 = getCheckboxValue($("#automatic_provisioning_checkbox:checked"));
					outData['scheduledScalingOn'] 			 = getCheckboxValue($("#scehduled_scale_checkbox:checked"));
					outData['scheduledScalingState'] 		 = 1;
					outData['maxNumberOfMachinesPerCluster'] = $("#jq_cluster_size_range_slider").slider("values", 1);
					outData['minNumberOfMachinesPerCluster'] = $("#jq_cluster_size_range_slider").slider("values", 0);
					outData['maxLoad'] 			 			 = $("#jq_load_thresholds_slider").slider("values", 1);
					outData['minLoad'] 	    	 			 = $("#jq_load_thresholds_slider").slider("values", 0);
					outData['periodFrom'] 				     = getDateTimePickerValue($("#scale_scheduler_datetime_picker_from"));
					outData['periodTo'] 				     = getDateTimePickerValue($("#scale_scheduler_datetime_picker_to"));
					outData['scheduledClusterSize'] 		 = $("#mb_scheduled_size_slider").mbgetVal();
					outData['clusterSizeOriginal'] 			 = -1;
					outData['jobId'] 						 = -1;

					outData['manualScaling'] 				 = getCheckboxValue($("#manual_provisioning_checkbox:checked"));
					outData['machineCount'] 				 = $("#mb_manual_scale_slider").mbgetVal();
					*/

				}
				catch(e){
					console.log("Exception thrown: " + err.message);
				}
				$.ajax({
					  type: "POST",
					  url: portletURL.url.cluster.scaleClusterURL,
					  data: outJson,
					  dataType: 'json'
					});
				
				//$.post(portletURL.url.cluster.scaleClusterURL, outJson);
				this.removeAttribute("data-cluster");
				$(this).dialog("close");
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		}
	});
	
	$("#automaticProvisioning").change(function() {
		if($('#automaticProvisioning:checked').val() !== undefined) {
			$("#provision").show("blind", {}, 500, null);
		} else {
			$("#provision").hide("blind", {}, 500, null);
		}
	});	

	// TODO: delete cluster buttons
	$("#deleteClusterConfirmDialog").dialog({
		autoOpen: false,
		resizable: false,
		height: 140,
		modal: true,
		buttons: {
			"Delete cluster": function() {
				var outData = {};
				var clusterId = this.getAttribute("data-cluster");
				var cluster = $("#cluster_"+ clusterId);
				
				console.log("cluster to delete: " + clusterId);
				cluster.fadeOut();

				outData['clusterId'] = clusterId;
				$.post(portletURL.url.cluster.deleteClusterURL, outData);
				
				this.removeAttribute("data-cluster");
				
				$(this).trigger("cluster.delete", clusterId);
				$(this).dialog("close");
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		}
	});
	
	// Cluster config port addition
	$("#configureClusterDialog .add-ip").each(function() {
		var add, input;

		input = $(this).find('input').clone();
		add = $(this).find('.add');

		return add.click(function() {
			var clone, close, cont, dataIdx, idx;
			close = $('<a href="#" />').text('x').addClass('remove');
			
			close.click(function() {
				$(this).fadeOut(100, function() {
					return $(this).prev('input').slideUp(100, function() {
						$(this).remove();
						return close.remove();
					});
				});
				return false;
			});
			
			close.insertBefore(add);
			cont = $(this).parent();
			dataIdx = cont.attr('data-idx');
			
			if ((dataIdx != null) && !isNaN(parseInt(dataIdx))) {
				cont.attr('data-idx', parseInt(cont.attr('data-idx')) + 1);
			} else {
				cont.attr('data-idx', '1');
			}
			
			idx = cont.attr('data-idx');
			clone = input.clone();
			clone.attr('name', input.attr('name').replace("[0]", "[" + idx + "]"));
			clone.hide();
			clone.insertBefore(add);
			clone.slideDown(100);
			return false;
		});
	});
	
	
})(jQuery);