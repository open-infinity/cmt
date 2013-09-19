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
	
	var tabTitle = $("#tab_title" ),
    tabContent = $( "#tab_content" ),
    tabTemplate = "<li><a href='#{href}'>#{label}</a> " +
    		          "<span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span>" +
    		      "</li>",
    tabCounter = 2;
	var tabs = $( "#tabs" ).tabs();
	
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
				          {name:'id', index:'id', width:50, align:"center"},
				          {name:'name', index:'name', width:150, align:"left"},
				          // 545
				          {name:'description', index:'description', width:535, align:"left"}
				          ],
				rowNum: 10,
				width: 750,
				height: 300,
				pager: '#template-grid-pager',
				sortname: 'id',
				viewrecords: true,
				shrinkToFit: false,
				sortorder: 'id'
			})
		},
		
		setupTabs: function(){
			$( "#tabs" ).tabs();
		},
		
		bindEventHandlers: function(){
		    $("#edit-template").bind( "click", portlet.editTemplate);   
		    $("#new-template").bind( "click", portlet.createNewTemplate);
		    $("#assign-template").bind( "click", portlet.assignTemplate);
		    $("#delete-template").bind( "click", portlet.deleteTemplate);

		},
		
		createNewTemplate: function(){
			alert( "User clicked on 'New '" );
			var label = tabTitle.val() || "Tab " + tabCounter,
				id = "tabs-" + tabCounter,
				li = $(tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) ),
				tabContentHtml = tabContent.val() || "Tab " + tabCounter + " content.";
	 
			tabs.find( ".ui-tabs-nav" ).append( li );
			tabs.append( "<div id='" + id + "'><p>" + tabContentHtml + "</p></div>" );
			tabs.tabs( "refresh" );
			tabCounter++;
		},
		
		deleteTemplate: function(){
			alert( "User clicked on 'Delete '" );
		},
		
		editTemplate: function(){
			alert( "User clicked on 'Edit '" );
			
		},
		
		assignTemplate: function(){
			alert( "User clicked on 'Assign '" );
		},
		
		addTab: function(){
		      var label = tabTitle.val() || "Tab " + tabCounter,
		        id = "tabs-" + tabCounter,
		        li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) ),
		        tabContentHtml = tabContent.val() || "Tab " + tabCounter + " content.";
		 
		      tabs.find( ".ui-tabs-nav" ).append( li );
		      tabs.append( "<div id='" + id + "'><p>" + tabContentHtml + "</p></div>" );
		      tabs.tabs( "refresh" );
		      tabCounter++;
		    }
	
	};
	
	function addOneTab() {
	      var label = tabTitle.val() || "Tab " + tabCounter,
	        id = "tabs-" + tabCounter,
	        li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) ),
	        tabContentHtml = tabContent.val() || "Tab " + tabCounter + " content.";
	 
	      tabs.find( ".ui-tabs-nav" ).append( li );
	      tabs.append( "<div id='" + id + "'><p>" + tabContentHtml + "</p></div>" );
	      tabs.tabs( "refresh" );
	      tabCounter++;
	    }
	

	
	
	portlet.init();
	portlet.setupTemplatesTable();
	portlet.setupTabs();
	portlet.bindEventHandlers();
			
});
		
	