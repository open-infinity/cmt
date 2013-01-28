/**
 * Cluster type resources
 */

(function($) {
	console.log("initializing cloudadmin.resource");

	var cloudadmin = window.cloudadmin || {};
	
	cloudadmin.resource = {
		clusters : [
			{
				type: 0,
				name: 'portal',
				dependency: 4,
				elastic: true,
				min: 1,
				max: 12
			},
			{
				type: 1,
				name: 'mule',
				dependency: null,
				min: 1,
				max: 12
			},
			{
				type: 2,
				name: 'pentaho',
				dependency: null,
				min: 1,
				max: 12
			},				
			{
				type: 3,
				name: 'bigdata',
				dependency: null,
				min: 7,
				max: 12,
				repMin:3,
				repMax:10
			},
			{
				type: 4,
				name: 'dbms',
				dependency: null,
				min: 1,
				max: 1
			},
			{
				type: 5,
				name: 'bas',
				dependency: 4,
				elastic: true,
				min: 1,
				max: 12
			},
			{
				type: 6,
				name: 'nosql',
				dependency: null,
				min: 6,
				max: 10,
				repMin:3,
				repMax:10
			},
			{
				type: 7,
				name: 'ig',
				dependency: null,
				min: 1,
				max: 12
			},
			{
				type: 8,
				name: 'ee',
				dependency: null,
				min: 1,
				max: 12
			},
			{
				type: 9,
				name: 'ecm',
				dependency: null,
				min: 1,
				max: 12
			}
		],

		
		/*
				id: 1,                          // reference number
				type: 1,						// type of tempalate to be used for cluster configuration
				name: 'ig',						// short name (used where?)
				title:'Identity Gateway',		// title appearing at nstance creation dialog
				dependency: null,				// dependency on other cluster types
				min: 1,							// minimal machines 
				max: 12							// maximal machines
		 */
		/*clusterTypes : [
			{
				id: 0,
				type: 1,
				name: 'ig',
				title:'Identity Gateway',
				dependency: null,
				min: 1,
				max: 12
			},
			{
				id: 1,
				type:1,
				name: 'bas',
				title:'BAS Platform',
				dependency: null,
				elastic: true,
				min: 1,
				max: 12
			},
			{
				id: 2,
				type: 1,
				name: 'portal',
				title:'Portal Platform',
				dependency: 4,
				elastic: true,
				min: 1,
				max: 12
			},
			{
				id: 3,
				type: 1,
				name: 'mq',
				title:'Service Platform',
				dependency: 4,
				elastic: true,
				min: 1,
				max: 12
			},
			{
				id: 4,
				type: 1,
				name: 'rdbms',
				title:'Relational Database Management',
				dependency: null,
				min: 1,
				max: 1
			},
			{
				id: 5,
				type: 2,
				name: 'nosql',
				title:'NoSQL Repository',
				dependency: null,
				min: 6,
				max: 12,
				repMin:3,
				repMax:10
			},
			{
				id: 6,
				type: 2,
				name: 'bigdata',
				title:'Big Data Repository',
				dependency: null,
				min: 7,
				max: 12,
				repMin:3,
				repMax:10
			},
			{
				id: 7,
				type: 1,
				name: 'ee',
				title:'EE Platform',
				dependency: null,
				min: 1,
				max: 12
			},
			{
				id: 8,
				type: 1,
				name: 'ecm',
				title:'Enterprise Content Management',
				dependency: null,
				min: 1,
				max: 12
			}			
		],
		machineSizes:[
			"Cores: 1, RAM: 1GB, Disk: 100GB",
			"Cores: 2, RAM: 2GB, Disk: 200GB",
			"Cores: 4, RAM: 4GB, Disk: 400GB",
			"Cores: 8, RAM: 8GB, Disk: 800GB",
			"Cores: 16, RAM: 16GB, Disk: 1000GB"
		]*/
	};
	
})(jQuery);
