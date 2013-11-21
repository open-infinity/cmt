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
	var app = window.app || {};

    $.extend(app, {

        init: function(){
            $.ajaxSetup({cache: false});
            app.templatesTable = $("#templates-grid");
            app.editTemplateButton = $("#edit-template");
            app.newTemplateButton = $("#new-template");
            app.deleteTemplateButton = $("#delete-template");
            app.tabsContainer = $( "#tabs" );
        },

        setupTemplatesTable: function(){
            app.templatesTable.jqGrid({
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
                          {name:'id', index:'id', width:50, align:"center", sortable:true, sorttype:"int",searchoptions:{sopt:['eq','ne','le','lt','gt','ge']}},
                          {name:'name', index:'name', width:150, align:"left", searchoptions:{sopt:['eq','ne','le','lt','gt','ge']}},
                          {name:'description', index:'description', width:535, align:"left", searchoptions:{sopt:['eq','ne','le','lt','gt','ge']}}
                          ],
                rowNum: 10,
                width: 750,
                height: "auto",
                pager: '#template-grid-pager',
                sortname: 'id',
                viewrecords: true,
                shrinkToFit: false,
                sortorder: "asc",
                ondblClickRow: app.editTemplate,
                loadonce: true,
                gridComplete: function(){
                    $("#templates-grid").setGridParam({datatype: 'local'});
                }
            });
            app.templatesTable.jqGrid('filterToolbar',{searchOperators : true});
        },

        reloadTemplatesTable: function(){
            app.templatesTable.setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
        },

        setupTabs: function(){
            app.tabsContainer.tabs();
        },

        bindEventHandlers: function(){
            app.editTemplateButton.bind( "click", app.editTemplate);
            app.newTemplateButton.bind( "click", app.createTemplate);
            app.deleteTemplateButton.bind( "click", app.deleteTemplate);
        },

        createTemplate: function(){
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
            var id = app.templatesTable.jqGrid('getGridParam','selrow');
            if (id == null) {
                alert( "Please select a row for deletion");
                return;
            }
            var ret = app.templatesTable.jqGrid('getRowData', id);
            $.ajax({
              url: portletURL.url.template.deleteTemplateURL + "&templateId=" + ret.id,
              cache: false
            })
            .done(function() {
                app.reloadTemplatesTable();
            });

        },

        editTemplate: function(){
            var id = app.templatesTable.jqGrid('getGridParam','selrow');
            if (id)	{
                var ret = app.templatesTable.jqGrid('getRowData',id);
                app.dialog.template.edit(ret.id);
            } else {
                alert("Please select a row for editing");
            }
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
    });

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
	var tabTitle = $("#tab_title" ),
    tabContent = $( "#tab_content" ),
    tabTemplate = "<li><a href='#{href}'>#{label}</a> " +
    		          "<span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span>" +
    		      "</li>",
    tabCounter = 2;
	var tabs = $( "#tabs" ).tabs();

	app.init();
	app.setupTemplatesTable();
	app.setupTabs();
	app.bindEventHandlers();
});
		
	