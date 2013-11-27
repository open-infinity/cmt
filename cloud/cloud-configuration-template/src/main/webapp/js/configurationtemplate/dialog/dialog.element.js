(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.element || {};

    dlg.mode =  null;

    dlg.value = {};
    dlg.value.id = $("#dlg-element-value-id");
    dlg.value.type = $("#dlg-element-value-type");
    dlg.value.name = $("#dlg-element-value-name");
    dlg.value.version = $("#dlg-element-value-version");
    dlg.value.description = $("#dlg-element-value-description");
    dlg.value.minMachines = $("#dlg-element-value-min-machines");
    dlg.value.maxMachines = $("#dlg-element-value-max-machines");
    dlg.value.replicated = $("#dlg-element-value-replicated");
    dlg.value.minReplicationMachines = $("#dlg-element-value-min-repl-machines");
    dlg.value.maxReplicationMachines = $("#dlg-element-value-max-repl-machines");

    dlg.html = {};
    dlg.html.idContainer = $($("#dlg-element-general-tab").find(".dlg-element-container").first());
    dlg.html.self = $("#dlg-element");
    dlg.html.tabs = $("#dlg-element-tabs");
    dlg.html.selectedDependedeesList = $("#dlg-element-selected-dependees").find("ul");
    dlg.html.availableDependedeesList = $("#dlg-element-available-dependees").find("ul");

    dlg.template = {};
    dlg.template.dependee = "<li class='ui-state-default'><div class='dlg-element-dependee-name'></div><div class='dlg-element-dependee-version'></div></li>";

    $.extend(dlg, {

        // jQuery objects from DOM elements

        // General
        //self : $("#dlg-element"),

        //mode : null,

        //tabs : $("#dlg-element-tabs"),

        //infoDialog : $("#dlg-info"),

        // General
        /*
        name : $("#dlg-element-id-value"),

        templateName : $("#dlg-element-name + input"),

        templateDescription : $("#template-description + textarea"),

        // Elements
        selectedElementsPanel : $("#elements-selection-container").find(".dlg-list-panel-container").first(),

        selectedElementsList : $("#dlg-element-selected-elements"),

        availableElementsPanel : $("#elements-selection-container").find(".dlg-list-panel-container").last(),

        // Organizations
        selectedOrganizationsPanel : $("#organizations-selection-container").find(".dlg-list-panel-container").first(),

        availableOrganizationsPanel : $("#organizations-selection-container").find(".dlg-list-panel-container").last(),

        selectedOrganizationsList : $("#dlg-element-selected-organizations"),
        */
        // Dialog functions

        create : function(){
            /*
            $.when(
                $.ajax({
                    url: portletURL.url.template.getAllAvailableElementsURL,
                    dataType: "json"
                }),
                $.ajax({
                    url: portletURL.url.template.getAllOrganizationsURL,
                    dataType: "json"
                }))
            .done(function(dataElements, dataOrganizations){
                populateElements(dataElements[0]);
                populateOrganizations(dataOrganizations[0]);
                configureDragAndDrop();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
            });

            dlg.open("create");
            */
        },

        remove : function(id){
            console.log("remove with argument id:" + id);
        },

        edit : function(id){
            var jqxhrTemplate = $.ajax({
                url: portletURL.url.element.getElementURL + "&elementId=" + id,
                dataType: "json"
                }).done(function(data) {
                    dlg.value.id.text(data.id);
                    dlg.value.type.val(data.type);
                    dlg.value.name.val(data.name);
                    dlg.value.version.val(data.version);
                    dlg.value.description.val(data.description);
                    dlg.value.minMachines.val(data.minMachines);
                    dlg.value.maxMachines.val(data.maxMachines);
                    dlg.value.replicated.val(data.replicated);
                    dlg.value.minReplicationMachines.val(data.minReplicationMachines);
                    dlg.value.maxReplicationMachines.val(data.maxReplicationMachines);
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching element");
            });
            /*
            $.when(
                $.ajax({
                    url: portletURL.url.template.getElementsForTemplateURL + "&templateId=" + id,
                    dataType: "json"
                }),
                $.ajax({
                    url: portletURL.url.template.getOrganizationsForTemplateURL + "&templateId=" + id,
                    dataType: "json"
                }))
            .done(function(dataElements, dataOrganizations){
                populateElements(dataElements[0]);
                populateOrganizations(dataOrganizations[0]);
                configureDragAndDrop();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
                });
            */
            $.when(
                $.ajax({
                    url: portletURL.url.element.getDependenciesURL + "&elementId=" + id,
                    dataType: "json"
                }),
                $.ajax({
                ))    url: portletURL.url.template.getParameterKeysAndValuesURL + "&elementId=" + id,
                    dataType: "json"
                }))
            .done(function(dataDependencies, dataKeyValues){
                populateDependencies(dataDependencies[0]);
                populateKeyValues(dataKeyValues[0]);
                configureDragAndDrop();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
                });

            dlg.open("edit");
        },

        open : function(mode){
            dlg.mode = mode;
            var title;
            if (mode == "edit"){
                dlg.html.idContainer.show();
                title = "Edit element";
            }
            else if (mode == "create"){
                dlg.html.idContainer.hide();
                title = "Create new element";
            }
            else{
                console.log("Unexpected mode for dialog.");
            }
            dlg.html.self.dialog("option", "title", title);
            dlg.html.tabs.tabs();
            dlg.html.self.show();
            dlg.html.self.dialog("open");
        }
    });

    // Initialize dialogs

    dlg.html.self.dialog({
        title: "Edit element",
        autoOpen: false,
        modal: true,
        width: 650,
        height: 750 ,
        buttons: {
            "Submit changes": function() {
                submitElement(dlg.mode);
                cleanUpDialog($(this));
                $(this).hide();
                $(this).dialog( "close" );
            },
            Cancel: function() {
                cleanUpDialog($(this));
                $(this).hide();
                $(this).dialog( "close" );
            }
        }
    });

/*
    dlg.infoDialog.dialog({
        title: "Detailed information",
        autoOpen: false,
        modal: true,
        width: 1000,
        height: 200 ,
        buttons: {
            Ok: function() {
                cleanUpTable($(this));
                $(this).dialog( "close" );
            }
        }
    });
    */
    // Helper functions
    function configureDragAndDrop(){
        $(".dlg-list-panel-container").droppable({
            activeClass: "ui-state-highlight",
            drop: function (event, ui) {
                var list = $(this).find("ul");
                var helper = ui.helper;
                var selected = $(this).siblings().find("li.ui-state-highlight");
                if (selected.length > 1) {
                    moveMultipleElements(list, selected);
                } else {
                    moveSingleElement(ui.draggable, list);
                }
            },
            tolerance: "touch"
        });
        $("li", ".dlg-item-list-container").draggable({
            revert: "invalid",
            containment: "document",
            helper: "clone",
            cursor: "move",
            scroll: true,
            drag: function (event, ui) {
                var helper = ui.helper;
                var selected = $(this).parent().find("li.ui-state-highlight", "ul");
                if (selected.length > 2) {
                    $(helper).html(selected.length - 1 + " items");
                }
            }
        });
        $(".dlg-item-list-container").find("li").
            click(function(){
                $(this).toggleClass("ui-state-highlight");
            }).
            dblclick(function () {
                dlg.infoDialog.dialog("open");
                var configData = $(this).data("config");
                storeToTable(configData, $("#dlg-item-table"));
            });
    }



    function populateDependencies(data){
        try{
            var htmlTemplate = "<li class='ui-state-default'>\
                                  <div class='dlg-element-dependee-name'></div>\
                                  <div class='dlg-element-dependee-version'></div>\
                               </li>";
            var selectedIndices = [];

            $.each(data.selected, function(index, value){
               storeDependeesToDom(htmlTemplate, value, dlg.html.selectedDependedeesList);
               selectedIndices.push(value.organizationId);
            });

            $.each(data.available, function(index, value){
               if (selectedIndices.indexOf(value.id) == -1){
                   storeDependeesToDom(htmlTemplate, value, dlg.html.availableDependedeesList);
               }
            });
        }
        catch(err){
            console.log(err.message);
        }
    }

    function populateKeyValues(data){
        try{
            var htmlTemplateKeys = "<li class='ui-state-default'>\
                                        <div class='dlg-element-parameter-key-name'></div>\
                                    </li>";
            var htmlTemplateValues = "<li class='ui-state-default'>\
                                        <div class='dlg-element-parameter-value-type'></div>\
                                        <div class='dlg-element-parameter-value-value'></div>\
                                    </li>";

            $.each(data.keys, function(index, value){
                storeKeyToDom(htmlTemplateKeys, value, dlg.html.parameterKeysList);
                for (parameterValue in data.values){
                   if (parameterValue.parameterKeyId == value.id){
                    storeValuesToDom(htmlTemplateValues, value, dlg.html.parameterKeysValues);
                   }
                }

            });

            $.each(data.values, function(index, value){
               if (selectedIndices.indexOf(value.id) == -1){
                   storeValuesToDom(htmlTemplate, value, dlg.html.parameterKeysValues);
               }
            });
        }
        catch(err){
            console.log(err.message);
        }
    }

    function storeDependeesToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");
        lastChild.find(".dlg-element-dependee-name").text(value.name);
        lastChild.find(".dlg-element-dependee-version").text(value.version);
        lastChild.data("config", value);
    }

    function storeKeyToDom(htmlTemplate, value, list){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            lastChild.find(".dlg-element-parameter-key-name").text(value.name);
            lastChild.data("config", value);
    }

    function storeValueToDom(htmlTemplate, value, list){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            lastChild.find(".dlg-element-parameter-value-type").text(value.type);
            lastChild.find(".dlg-element-parameter-value-value").text(value.value);
            lastChild.data("config", value);
    }

    function storeToTable(configData, table){
        var htmlTableRows = "<tr></tr><tr></tr>";
        table.append(htmlTableRows);
        for (var key in configData) {
            table.find("tr:first-child").append('<th>' + key + '</th>');
            table.find("tr:last-child").append('<td>' + configData[key] + '</td>');
        }
    }

    // TODO: make it generic
    function storeOrganizationToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");
        lastChild.find(".dlg-element-organization-id").text(value.organizationId);
        lastChild.find(".dlg-element-organization-name").text(value.name);
        lastChild.data("config", value);
     }

    function moveMultipleElements(list, selected) {
        $(selected).each(function () {
            $(this).appendTo(list).removeClass("ui-state-highlight").fadeIn();
        });
    }

    function moveSingleElement(elem, list) {
        elem.appendTo(list).removeClass("ui-state-highlight").fadeIn();
    }

    function cleanUpDialog(that){
        that.find(".dlg-item-list-container").find("ul").empty();
        dlg.value.id.text("");
        dlg.value.type.val("");
        dlg.value.name.val("");
        dlg.value.description.val("");
        dlg.value.minMachines.val("");
        dlg.value.maxMachines.val("");
        dlg.value.replicated.val("");
        dlg.value.minReplicationMachines.val("");
        dlg.value.maxReplicationMachines.val("");
    }

    function cleanUpTable(that){
        that.find("tr").remove();
    }

    function submitElement(mode){
        var outData = {};

        dlg.value.id.text(data.id);
        dlg.value.type.val(data.type);
        dlg.value.name.val(data.name);
        dlg.value.description.val(data.description);
        dlg.value.minMachines.val(data.minMachines);
        dlg.value.maxMachines.val(data.maxMachines);
        dlg.value.replicated.val(data.replicated);
        dlg.value.minReplicationMachines.val(data.minReplicationMachines);
        dlg.value.maxReplicationMachines.val(data.maxReplicationMachines);

        outData["id"] = parseInt(dlg.value.id.text());
        outData["type"] = dlg.value.type.val();
        outData["name"] = dlg.value.name.val();
        outData["description"] = dlg.value.description.val();
        outData["minMachines"] = dlg.value.minMachines.val();
        outData["maxMachines"] = dlg.value.maxMachines.val();
        outData["replicated"] = dlg.value.replicated.val();
        outData["minReplicationMachines"] = dlg.value.minReplicationMachines.val();
        outData["maxReplicationMachines"] = dlg.value.maxReplicationMachines.val();

        //outData["elementsSelected"] = JSON.stringify(getSelectedElements());
        //outData["organizationsSelected"] = JSON.stringify(getSelectedOrganizations());

        $.post((mode == "edit") ? portletURL.url.element.editElementURL : portletURL.url.element.createElementURL, outData)
        .done(function(){
            app.reloadTemplatesTable();
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            alertPostFailure(dlg.mode, textStatus, errorThrown);
        });
    }

    function getSelectedElements(){
        var selectedItems = [];
            var arrayOfLis = dlg.selectedElementsList.find("li");
        for (var i = 0; i < arrayOfLis.length; i++){
            selectedItems.push($(arrayOfLis[i]).data("config").id);
        }
        return selectedItems;
    }

    function getSelectedOrganizations(){
        var selectedItems = [];
        var arrayOfLis = dlg.selectedOrganizationsList.find("li");
        for (var i = 0; i < arrayOfLis.length; i++){
            selectedItems.push($(arrayOfLis[i]).data("config").organizationId);
        }
        return selectedItems;
    }

    function alertPostFailure(mode, textStatus, errorThrown){
        alert("Server error at template" + mode + ", text status:" + textStatus + " " + "errorThrown:" + errorThrown);
    }

})(jQuery);