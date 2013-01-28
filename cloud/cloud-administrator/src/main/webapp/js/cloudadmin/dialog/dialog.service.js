
(function($) {
	console.log("initializing cloudadmin.dialog.service");
	var cloudadmin = window.cloudadmin || {};
	
	$.extend(cloudadmin.dialog, {

		// Add service dialog
		initAddServiceDialog: function (instanceId, clusters) {
			var url = portletURL.url.service.getAvailableServicesURL +"&id="+ instanceId +"&rnd="+Math.random();
			$("#addServiceDialog").attr("data-instance-id", instanceId);
			
			// Clear the previous data
			var $dialog = $("#addServiceDialog").empty();
			
	  		// Get the template
	  		var $template = $("#addServiceTemplate > form").clone();
	  		$template.find("select[name='serviceSelect']").attr("id", "serviceSelect");
	  		$template.find(".serviceSliders").empty();
	
			$dialog.append($template);
			
			// Get available services	
			$.getJSON(url, function(data) {
				var options = '';
				
				// First row empty
				options += '<option value=""> </option>';
				
				// Add available services to select box
				$.each(data, function(key,val) {
					options += '<option value="'+key+'">'+val+'</option>';
				});
				$("#serviceSelect").html(options);
				
				
				// Handle the selection change
			    $("#serviceSelect").change(function () {
			    	var clusterName = this.text;
			        var clusterType = this.value;
			        
			    	$("#serviceSelect option:selected").each(function () {
			    		clusterName += $(this).text() + " ";
			    	});
			          
			    	console.log("selected service " + clusterName);
			    	console.log("selected service type " + clusterType);
	
			    	// Clear the sliders
			    	$("#addServiceDialog .serviceSliders").remove();
			    	
			    	for(var i = 0; i < clusters.length; i++) {
			    		if(clusters[i].type == clusterType) {
	
			    			// Get the slider template
					  		var $slidertemplate = $("#addServiceTemplate .serviceSliders").clone();
					  		
					  		$slidertemplate.find("div[name='serviceslider']").attr("id", "serviceslider");
					  		$slidertemplate.find("div[name='serviceMachineCountSlider']").attr("id", "serviceMachineCountSlider").addClass("{startAt: "+ clusters[i].min +"}");
					  		$slidertemplate.find("div[name='serviceMachineSizeSelect']").attr("id", "serviceMachineSizeSelect");
					  		
					  		if(clusters[i].repMin) {
					  			$slidertemplate.find("div[name='replicationslider']").attr("id", "replicationslider");
					  			$slidertemplate.find("div[name='replicationSizeSlider']").attr("id", "replicationSizeSlider").addClass("{startAt: "+ clusters[i].repMin +"}");
						  		$slidertemplate.find("div[name='replicationMachineSizeSelect']").attr("id", "replicationMachineSizeSelect");

					  		} else {
					  			$slidertemplate.find("div[name='replicationslider']").remove();
					  			$slidertemplate.find("div[name='replicationSizeSlider']").remove();
						  		$slidertemplate.find("div[name='replicationMachineSizeSelect']").remove();
					  		}
					  		
					  		$slidertemplate.appendTo("#addServiceDialog fieldset");
					  		
					  		// Add sliders and dropbox
					  		$("#serviceslider .mb_slider").mbSlider({			
					  			minVal: clusters[i].min,
					  			maxVal: clusters[i].max,
					  			grid: 1
					  		});
			
					  		if(clusters[i].repMin) {
					  				$("#replicationslider .mb_slider").mbSlider({			
					  				minVal: clusters[i].repMin,
					  				maxVal: clusters[i].repMax,
					  				grid: 1
					  			});
					  		}
					  		
					  		$("#addServiceDialog .vmenu").vmenu();
			    		}
			    	}       
			          
			        });
			});
		}
	});

	
	// Initializing the dialog
	// TODO: create buttons 
	$("#addServiceDialog").dialog({
		autoOpen: false,
		resizable: false,
		height: 500,
		width: 510,
		modal: true,
		buttons: {
			"Create cluster": function() {
				var outData = {};
				var instanceId = document.getElementById("addServiceDialog").getAttribute("data-instance-id");
				outData["service"] = $("#serviceSelect").val();
				outData["machineCount"] = $("#serviceMachineCountSlider").mbgetVal();
				outData["machineSize"] = $("#serviceMachineSizeSelect").vmenu('getValId');
				$.ajax({
					type: 'POST',
					url: portletURL.url.service.newServiceURL+"&id="+instanceId,
					data: outData,
					dataType: 'json'
				});
				
				$("#addServiceDialog .vmenu").vmenu("reset");
				$(this).dialog("close");
			},
			"Cancel": function() {
				$("#addServiceDialog .vmenu").vmenu("reset");
				$(this).dialog("close");
			}
		}
	});
	
})(jQuery);