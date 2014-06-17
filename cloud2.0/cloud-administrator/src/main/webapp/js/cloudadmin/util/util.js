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
	
	console.log("resourceContainer["+ this.name +"] created.");
	
	this.setResource = function(data) {
		console.log(this.name + ".setResource called.")
		this.resource = data;
	};
};


 
