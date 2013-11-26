(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.element || {};

    dlg.mode =  null;

    dlg.value = {};
    dlg.value.id = $("#dlg-element-value-id");
    dlg.value.type = $("#dlg-element-value-type");
    dlg.value.name = $("#dlg-element-value-name");
    dlg.value.description = $("#dlg-element-value-description");
    dlg.value.minMachines = $("#dlg-element-value-minMachines");
    dlg.value.maxMachines = $("#dlg-element-value-maxMachines");
    dlg.value.replicated = $("#dlg-element-value-replicated");
    dlg.value.minReplicationMachines = $("#dlg-element-value-minReplicationMachines");
    dlg.value.maxReplicationMachines = $("#dlg-element-value-maxReplicationMachines");

    dlg.html = {};
    dlg.html.idContainer = $($("#dlg-element-general-tab").find(".dlg-element-container").first());
    dlg.html.self = $("#dlg-element");
    dlg.html.tabs = $("#dlg-element-tabs");

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
        selectedElementsPanel : $("#elements-selection-container").find(".dlg-element-list-panel-container").first(),

        selectedElementsList : $("#dlg-element-selected-elements"),

        availableElementsPanel : $("#elements-selection-container").find(".dlg-element-list-panel-container").last(),

        // Organizations
        selectedOrganizationsPanel : $("#organizations-selection-container").find(".dlg-element-list-panel-container").first(),

        availableOrganizationsPanel : $("#organizations-selection-container").find(".dlg-element-list-panel-container").last(),

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
                $(this).dialog( "close" );
            },
            Cancel: function() {
                cleanUpDialog($(this));
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
        $(".dlg-element-list-panel-container").droppable({
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
        $("li", ".list-container").draggable({
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
        $(".list-container").find("li").
            click(function(){
                $(this).toggleClass("ui-state-highlight");
            }).
            dblclick(function () {
                dlg.infoDialog.dialog("open");
                var configData = $(this).data("config");
                storeToTable(configData, $("#dlg-item-table"));
            });
    }

    function populateElements(data){
        try{
            var listSelected = dlg.selectedElementsPanel.find("ul");
            var listAvailable = dlg.availableElementsPanel.find("ul");
            htmlTemplate = "<li class='ui-state-default'>\
                                <div class='name list-item-column'></div>\
                                <div class='version'></div>\
                            </li>";
            var selectedIndices = [];
            $.each(data.selected, function(index, value){
                storeElementToDom(htmlTemplate, value, listSelected);
                selectedIndices.push(value.id);
            });
            $.each(data.available, function(index, value){
                if (selectedIndices.indexOf(value.id) == -1){
                    storeElementToDom(htmlTemplate, value, listAvailable);
                }
            });
        }
        catch(err){
            console.log(err.message);
        }
    }

    function populateOrganizations(data){
        try{
            var listSelected = dlg.selectedOrganizationsPanel.find("ul");
            var listAvailable = dlg.availableOrganizationsPanel.find("ul");
            var htmlTemplate = "<li class='ui-state-default'>\
                                  <div class='dlg-element-organization-id'></div>\
                                  <div class='dlg-element-organization-name'></div>\
                               </li>";
            var selectedIndices = [];

            $.each(data.selected, function(index, value){
               storeOrganizationToDom(htmlTemplate, value, listSelected);
               selectedIndices.push(value.organizationId);
            });

            $.each(data.available, function(index, value){
               if (selectedIndices.indexOf(value.organizationId) == -1){
                   storeOrganizationToDom(htmlTemplate, value, listAvailable);
               }
            });
        }
        catch(err){
            console.log(err.message);
        }
    }

    function storeElementToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");
        lastChild.find(".name").text(value.name);
        lastChild.find(".version").text(value.version);
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
