/**
 * Utility functions for CloudAdmin
 */

function urlContainer (name) {
	this.url = null;
	this.name = name;
	
	console.log("urlContainer ["+ this.name +"] created.");
	
    this.initialize = function(data) {
    	console.log(this.name + ".initialize called.");	
    	this.url = data;    
    };
};


function resourceContainer(name) {
	this.resource = null;
	this.name = name;
	
	console.log("reourceContainer["+ this.name +"] created.");
	
	this.setResource = function(data) {
		console.log(this.name + ".setResource called.")
		this.resource = data;
	};
};

// Shows or hides html elements
function toggleElements(elements) {
	return function () {
		if($(this).attr('checked'))
			elements.show("blind", {}, 500, null);
		else 
			elements.hide("blind", {}, 500, null);	
	};
}
