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

            // Modules tab
            app.modulesTable = $("#modules-grid");
            app.editModuleButton = $("#edit-module");
            app.newModuleButton = $("#new-module");
            app.deleteModuleButton = $("#delete-module");

            // Packages tab
            app.packagesTable = $("#packages-grid");
            app.editPackageButton = $("#edit-package");
            app.newPackageButton = $("#new-package");
            app.deletePackageButton = $("#delete-package");
        },

        setupTemplatesTable: function(){
            app.templatesTable.jqGrid({
                url: portletURL.url.template.getTemplatesURL,
                datatype: "json",
                jsonReader : {
                    repeatitems : false,
                    id: "Id",
                    root : function(obj) {return obj.rows;},
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
                    root : function(obj) {return obj.rows;},
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
                          {name:'replicated', index:'replicated', width:28, align:"center", sortable:true},
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
        
        setupModulesTable: function(){
            app.modulesTable.jqGrid({
                url: portletURL.url.module.getModulesURL,
                datatype: "json",
                jsonReader : {
                    repeatitems : false,
                    id: "Id",
                    root : function(obj) {return obj.rows;},
                    page : function(obj) {return obj.page;},
                    total : function(obj) {return obj.total;},
                    records : function(obj) {return obj.records;}
                    },
                colNames:['Id', 'Name', 'Version', 'Description'],
                colModel:[
                          {name:'id', index:'id', width:50, align:"center", sortable:true, sorttype:"int"},
                          {name:'name', index:'name', width:195, align:"left"},
                          {name:'version', index:'version', width:150, align:"left"},
                          {name:'description', index:'description', width:335, align:"left"}
                          ],
                rowNum: 10,
                width: 750,
                height: "auto",
                pager: '#modules-grid-pager',
                sortname: 'id',
                viewrecords: true,
                shrinkToFit: false,
                sortorder: "asc",
                ondblClickRow: app.editTableRow(app.modulesTable, app.dialog.module),
                loadonce: true,
                gridComplete: function(){
                    $("#modules-grid").setGridParam({datatype: 'local'});
                }
            });
            app.modulesTable.jqGrid(
                'navGrid',
                '#modules-grid-pager',
                {add:false, del:false, search:true, refresh:false, edit:false},
                {}, //  default settings for edit
                {}, //  default settings for add
                {}, //  default settings for delete
                {odata : ['equal', 'not equal', 'less', 'less or equal','greater','greater or equal', 'begins with','does not begin with','is in','is not in','ends with','does not end with','contains','does not contain']}, // search options
                {} /* view parameters*/
            );

        },

        setupPackagesTable: function(){
            app.packagesTable.jqGrid({
                url: portletURL.url.package.getPackagesURL,
                datatype: "json",
                jsonReader : {
                    repeatitems : false,
                    id: "Id",
                    root : function(obj) {return obj.rows;},
                    page : function(obj) {return obj.page;},
                    total : function(obj) {return obj.total;},
                    records : function(obj) {return obj.records;}
                    },
                colNames:['Id', 'Name', 'Version', 'Description'],
                colModel:[
                          {name:'id', index:'id', width:50, align:"center", sortable:true, sorttype:"int"},
                          {name:'name', index:'name', width:195, align:"left"},
                          {name:'version', index:'version', width:150, align:"left"},
                          {name:'description', index:'description', width:335, align:"left"}
                          ],
                rowNum: 10,
                width: 750,
                height: "auto",
                pager: '#packages-grid-pager',
                sortname: 'id',
                viewrecords: true,
                shrinkToFit: false,
                sortorder: "asc",
                ondblClickRow: app.editTableRow(app.packagesTable, app.dialog.package),
                loadonce: true,
                gridComplete: function(){
                    $("#packages-grid").setGridParam({datatype: 'local'});
                }
            });
            app.packagesTable.jqGrid(
                'navGrid',
                '#packages-grid-pager',
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

        reloadElementsTable: function(){
            app.elementsTable.setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
        },

        reloadModulesTable: function(){
            app.modulesTable.setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
        },
        
        reloadPackagesTable: function(){
            app.packagesTable.setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
        },

        reloadTable: function(argTable){
            return (function(){
                var table = argTable;
                table.setGridParam({datatype:'json', page:1}).trigger('reloadGrid');
            });
        },

        setupTabs: function(){
            app.tabsContainer.tabs();
            app.tabsContainer.tabs('select', 0);
        },

        bindEventHandlers: function(){
            // Templates
            app.editTemplateButton.bind("click", app.editTableRow(app.templatesTable, app.dialog.template));
            app.newTemplateButton.bind("click", app.createTemplate);
            app.deleteTemplateButton.bind("click", app.deleteTemplate);

            // Elements
            app.editElementButton.bind("click", app.editTableRow(app.elementsTable, app.dialog.element));
            app.newElementButton.bind("click", app.create(app.dialog.element));
            app.deleteElementButton.bind("click", app.deleteElement);

            // Modules
            app.editModuleButton.bind("click", app.editTableRow(app.modulesTable, app.dialog.module));
            app.newModuleButton.bind("click", app.create(app.dialog.module));
            app.deleteModuleButton.bind("click", app.deleteModule);

            // Packages
            app.editPackageButton.bind( "click", app.editTableRow(app.packagesTable, app.dialog.package));
            app.newPackageButton.bind( "click", app.create(app.dialog.package));
            app.deletePackageButton.bind( "click", app.deletePackage);
        },

        bindInfoDblClick : function(){
            $(".dlg-item-list-container").find("li").
            click(function(){
                $(this).toggleClass("ui-state-highlight");
            }).
            dblclick(function () {
                app.dialog.info.dialog("open");
                var configData = $(this).data("config");
                storeToTable(configData, $("#dlg-item-table"));
            });
        },

        createTemplate : function(){
            app.dialog.template.create();
        },

        create : function(dialog){
            return function(){
                dialog.create();
            };
        },

        deleteTemplate : function(){
            var id = app.templatesTable.jqGrid('getGridParam','selrow');
            if (id == null) {
                alert( "Please select a row for deletion");
                return;
            }
            var ret = app.templatesTable.jqGrid('getRowData', id);
            $.ajax({
              url: portletURL.url.template.deleteTemplateURL + "&id=" + ret.id,
              cache: false
            })
            .done(function() {
                app.reloadTemplatesTable();
            });
        },

        deleteElement : function(){
            var id = app.elementsTable.jqGrid('getGridParam','selrow');
            if (id == null) {
                alert( "Please select a row for deletion");
                return;
            }
            var ret = app.elementsTable.jqGrid('getRowData', id);
            $.ajax({
              url: portletURL.url.element.deleteElementURL + "&id=" + ret.id,
              cache: false
            })
            .done(function() {
                app.reloadElementsTable();
            });
        },

        deleteModule : function(){
            var id = app.modulesTable.jqGrid('getGridParam','selrow');
            if (id == null) {
                alert( "Please select a row for deletion");
                return;
            }
            var ret = app.modulesTable.jqGrid('getRowData', id);
            $.ajax({
              url: portletURL.url.module.deleteModuleURL + "&id=" + ret.id,
              cache: false
            })
            .done(function() {
                app.reloadModulesTable();
            });
        },

        deletePackage : function(){
            var id = app.packagesTable.jqGrid('getGridParam','selrow');
            if (id == null) {
                alert( "Please select a row for deletion");
                return;
            }
            var ret = app.packagesTable.jqGrid('getRowData', id);
            $.ajax({
              url: portletURL.url.package.deletePackageURL + "&id=" + ret.id,
              cache: false
            })
            .done(function() {
                app.reloadPackagesTable();
            });
        },
        // TODO:use me
        deleteTableItem : function(argTable, argUrlPrefix){
            return (function(){
                var table = argTable;
                var urlPrefix = argUrlPrefix;
                var id = table.jqGrid('getGridParam','selrow');
                    if (id == null) {
                        alert( "Please select a row for deletion");
                        return;
                    }
                    var ret = table.jqGrid('getRowData', id);
                    $.ajax({
                      url: urlPrefix + "&id=" + ret.id,
                      cache: false
                    })
                    .done(function() {
                        app.reloadTable(table);
                    });
            });
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
        }
    });
      
	app.init();
	app.setupTemplatesTable();
	app.setupElementsTable();
	app.setupModulesTable();
	app.setupPackagesTable();
	app.setupTabs();
	app.bindEventHandlers();
});
		
	