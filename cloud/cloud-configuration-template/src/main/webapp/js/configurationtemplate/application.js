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
            // General
            $.ajaxSetup({cache: false});

            // Tabs
            app.tabCounter = 1;
            app.tabsContainer = $("#tabsContainer");
            app.tabsReferenceList = $("#tabsReferenceList");

            // Templates tab
            app.templatesTable = $("#templates-grid");
            app.editTemplateButton = $("#edit-template");
            app.newTemplateButton = $("#new-template");
            app.deleteTemplateButton = $("#delete-template");

            // Elements tab
            app.elementsTable = $("#elements-grid");
            app.editElementButton = $("#edit-element");
            app.newElementButton = $("#new-element");
            app.deleteElementButton = $("#delete-element");
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
                ondblClickRow: app.editTableRow(app.templatesTable, app.dialog.template),
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
        setupElementsTable: function(){
            app.elementsTable.jqGrid({
                url: portletURL.url.element.getElementsURL,
                datatype: "json",
                jsonReader : {
                    repeatitems : false,
                    id: "Id",
                    root : function(obj) { return obj.rows;},
                    page : function(obj) {return obj.page;},
                    total : function(obj) {return obj.total;},
                    records : function(obj) {return obj.records;}
                    },
                colNames:['Id', 'Type', 'Name', 'Version', 'Description', 'MinMachines', 'MaxMachines', 'Repl', 'MinReplMachines', 'MaxReplMachines'],
                colModel:[
                          {name:'id', index:'id', width:35, align:"center", sortable:true, sorttype:"int"},
                          {name:'type', index:'type', width:35, align:"center",sortable:true},
                          {name:'name', index:'name', width:75, align:"left",sortable:true}, //70
                          {name:'version', index:'version', width:35, align:"center", sortable:true},
                          {name:'description', index:'description', width:200, align:"left", sortable:true},
                          {name:'minMachines', index:'minMachines', width:60, align:"center", sortable:true},
                          {name:'maxMachines', index:'maxMachines', width:60, align:"center", sortable:true},
                          {name:'replicated', index:'replicated', width:30, align:"center", sortable:true},
                          {name:'minReplicationMachines', index:'minReplicationMachines', width:86, align:"center", sortable:true},
                          {name:'maxReplicationMachines', index:'maxReplicationMachines', width:86, align:"center", sortable:true},
                          ],
                rowNum: 20,
                height: "auto",
                width: 750,
                pager: '#element-grid-pager',
                sortname: 'id',
                viewrecords: true,
                shrinkToFit: false,
                sortorder: "asc",
                ondblClickRow: app.editTableRow(app.elementsTable, app.dialog.element),
                loadonce: true,
                gridComplete: function(){
                    $("#elemements-grid").setGridParam({datatype: 'local'});
                }
            });
            app.elementsTable.jqGrid(
                'navGrid',
                '#element-grid-pager',
                {add:false, del:false, search:true, refresh:false, edit:false},
                {}, //  default settings for edit
                {}, //  default settings for add
                {}, //  default settings for delete
                {odata : ['equal', 'not equal', 'less', 'less or equal','greater','greater or equal', 'begins with','does not begin with','is in','is not in','ends with','does not end with','contains','does not contain']}, // search options
                {}  //  view parameters
            );

        },

        reloadTemplatesTable: function(){
            app.templatesTable.setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
        },

        reloadElementsTable: function(){
            app.elementsTable.setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
        },

        setupTabs: function(){
            app.tabsContainer.tabs({active: 1});
        },

        bindEventHandlers: function(){
            // Templates
            app.editTemplateButton.bind( "click", app.editTemplate);
            app.newTemplateButton.bind( "click", app.createTemplate);
            app.deleteTemplateButton.bind( "click", app.deleteTemplate);

            // Elements
             app.editElementButton.bind( "click", app.editTableRow(app.elementsTable, app.dialog.element));
             app.newElementButton.bind( "click", app.createElement);
             app.deleteElementButton.bind( "click", app.deleteElement);
        },

        createTemplate: function(){
            app.dialog.template.create();
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

        deleteElement: function(){
            var id = app.templatesTable.jqGrid('getGridParam','selrow');
            if (id == null) {
                alert( "Please select a row for deletion");
                return;
            }
            var ret = app.templatesTable.jqGrid('getRowData', id);
            $.ajax({
              url: portletURL.url.template.deleteElementURL + "&elementId=" + ret.id,
              cache: false
            })
            .done(function() {
                app.reloadElementsTable();
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

        editTableRow: function(argTable, argDialog){
            return (function(){
                var table = argTable;
                var dialog = argDialog;

                console.log("Editing row");
                var id = table.jqGrid('getGridParam','selrow');
                if (id)	{
                    var ret = table.jqGrid('getRowData',id);
                    dialog.edit(ret.id);
                } else {
                    alert("Please select a row for editing");
                }
            });
        },


    });

	app.init();
	app.setupTemplatesTable();
	app.setupElementsTable();
	app.setupTabs();
	app.bindEventHandlers();
});
		
	