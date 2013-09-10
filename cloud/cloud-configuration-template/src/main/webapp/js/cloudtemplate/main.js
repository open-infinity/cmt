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
 * @author Vedran Bartonicek
 * @version 1.3.0 
 * @since 1.3.0
 */

jQuery(function($){
	var portlet = window.portlet || {};
	
	console.log("enter main");
	console.log(portletURL.url.template.getTemplatesForUserURL);
	/*
	function initializePortlet(){
		$.ajaxSetup({cache: false});
	}
	
	function setupCloudTemplatesTable(){
		console.log("enter setupCloudTemplatesTable");
		$("#templates-grid").jqGrid({
			url: portletURL.url.template.getTemplatesForUserURL,
			datatype: "json",
			jsonReader : {
				repeatitems : false,
				id: "Id",
				root : function(obj) { return obj.rows;},
				page : function(obj) {return obj.page;},
				total : function(obj) {return obj.total;},
				records : function(obj) {return obj.records;}
				},
			colNames:['Id', 'Name', 'Description'],
			colModel:[
			          {name:'id', index:'id', width:100, align:"center"},
			          {name:'name', index:'name', width:217, align:"center"},
			          {name:'description', index:'cloudType', width:100, align:"center"}
			          ],
			rowNum:10,
			width: 882,
			height: 346,
			pager: '#template-grid-pager',
			sortname: 'id',
			viewrecords: true,
			shrinkToFit:false,
			sortorder: 'desc'
		});
	}
	
	setupCloudTemplatesTable();
	*/
	portlet = {
		init: function(){
			$.ajaxSetup({cache: false});
		},
		setupTemplatesTable: function(){
			$("#templates-grid").jqGrid({
				url: portletURL.url.template.getTemplatesForUserURL,
				datatype: "json",
				jsonReader : {
					repeatitems : false,
					id: "Id",
					root : function(obj) { return obj.rows;},
					page : function(obj) {return obj.page;},
					total : function(obj) {return obj.total;},
					records : function(obj) {return obj.records;}
					},
				colNames:['Id', 'Name', 'Description'],
				colModel:[
				          {name:'id', index:'id', width:100, align:"center"},
				          {name:'name', index:'name', width:217, align:"center"},
				          {name:'description', index:'description', width:100, align:"center"}
				          ],
				rowNum:10,
				width: 882,
				height: 346,
				pager: '#template-grid-pager',
				sortname: 'id',
				viewrecords: true,
				shrinkToFit:false,
				sortorder: 'id'
			})
		}
	};
    console.log("portletURL.url.template.getTemplatesForUserURL ");
	console.log("portletURL.url.template.getTemplatesForUserURL " + portletURL.url.template.getTemplatesForUserURL);
	$.get(portletURL.url.template.getTemplatesForUserURL, function(data) {
		  console.log(data);
		});
	
	portlet.init();
	//portlet.setupTemplatesTable();
			
});
		
	