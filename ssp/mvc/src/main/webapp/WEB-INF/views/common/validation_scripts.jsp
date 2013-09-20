<script type="text/javascript">	

	$(document).ready(function() {
		$("#${model}").submit(function() {
			var request = $(this).serializeObject();
			$.postJSON("${model}", request, 
			function(request) {
				setStatusField("Product registered successfully!");
				$.each($('#${model}').serializeArray(), function(i, field) {
				    fieldValidated(field.name, { valid : true });
				});
			}, 
			function(error) {
				// Set default view
				$.each($('#${model}').serializeArray(), function(i, field) {
				    fieldValidated(field.name, { valid : true });
				});
				// Set error view
				var obj = jQuery.parseJSON(error.responseText);
				var errorCounter = 0;
				var businessViolation = false;
				$.each(obj, function(key, val) {
					if ($.isArray(val)) {
						var realArray = $.makeArray(val);
						$.map(realArray, function(item, i) {
							document.getElementById('statusbox').innerHTML=item;
							businessViolation = true;
						});
					} else {
						fieldValidated(key, { valid : false, message : val});
						errorCounter++;
					}
				});
				if (!businessViolation)
					setStatusField("Product under editing contains " + errorCounter + " warning messages.");
			});
			return false;				
		});
	});
	
	function checkErrorFieldStatusForObject(field) {
		if (field==true) {
			fieldValidated(field, { valid : true });
		} else {
			fieldValidated(field, { valid : false, message : $('#'+field).val() + " is not valid."});
		}
	}
	
	function setStatusField(status) {
		document.getElementById('statusbox').innerHTML=status;
	}
	
	function validateField(validationUrl, field) {
		$.getJSON(validationUrl, { name: $("#" + field).val() }, function(fieldStatus) {
			if (fieldStatus.stringFieldValid) {
				fieldValidated(field, { valid : true });
			} else {
				fieldValidated(field, { valid : false, message : $("#" + field).val() + " field contains errros: "+ fieldStatus.stringFieldInvalid});
			}
		});
	}

	function fieldValidated(field, result) {
		if (result.valid) {
			$("#" + field).css({backgroundColor: 'white', border: '1px solid black', color: 'black', border: '3px inset green', foreGround: 'green'});
			$("#" + field + "Label").removeClass("error");
			$("#" + field + "\\.errors").remove();
			$('#create').attr("disabled", false);
		} else {
			$("#" + field + "Label").addClass("error");
			if ($("#" + field + "\\.errors").length == 0) {
				$("#" + field).css({backgroundColor: '#ffe', border: '3px inset red'});
				$("#" + field).after("<span id='" + field + ".errors'> " + result.message + "</span>").css({color: 'red'});		
			} else {
				$("#" + field + "\\.errors").html("<span id='" + field + ".errors'> " + result.message + "</span>");		
			}				
		}			
	}

	function resetForm() {
		$('#${model}')[0].reset();
	}
	
</script>