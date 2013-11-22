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
            app.tabCounter = 1;
            app.templatesTable = $("#templates-grid");
            app.editTemplateButton = $("#edit-template");
            app.newTemplateButton = $("#new-template");
            app.deleteTemplateButton = $("#delete-template");
            app.tabsContainer = $("#tabs");
            app.tabsReferenceList = $("#tabsReferenceList");
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
                          {name:'id', index:'id', width:50, align:"center", sortable:true, sorttype:"int"},
                          {name:'name', index:'name', width:150, align:"left"},
                          {name:'description', index:'description', width:535, align:"left"}
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
            app.templatesTable.jqGrid(
                'navGrid',
                '#template-grid-pager',
                {add:false, del:false, search:true, refresh:false, edit:false},
                {}, //  default settings for edit
                {}, //  default settings for add
                {}, //  default settings for delete
                {odata : ['equal', 'not equal', 'less', 'less or equal','greater','greater or equal', 'begins with','does not begin with','is in','is not in','ends with','does not end with','contains','does not contain']}, // search options
                {} /* view parameters*/
            );

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
        //"<span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span>"
            var tabTemplate = "<li><a href='#{href}'>#{label}</a><span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
            var tabLabel = "New template";
            var tabId = "tabs-" + ++app.tabCounter;
            var li = $(tabTemplate.replace(/#\{href\}/g, "#" + tabId ).replace( /#\{label\}/g, tabLabel ));

            //app.tabsReferenceList.append(li);
            //app.tabsContainer.append("<div id='" + tabId + "'><p>blah</p></div>");
            //<li><a href="#tabs-1">Templates</a></li>
            app.tabsReferenceList.append("<li><a href='#tabs-2'>Templates</a></li>");
            //app.tabsContainer.append("<div id='" + tabId + "'><p>blah</p></div>");
            app.tabsContainer.append("<div id='tabs-2'><p>blah</p></div>");
            app.tabsContainer.tabs("refresh");
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
    });

	app.init();
	app.setupTemplatesTable();
	app.setupTabs();
	app.bindEventHandlers();
});
		
	