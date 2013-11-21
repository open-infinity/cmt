(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.template || {};

    $.extend(dlg, {

        create: function () {
        },

        remove: function (id) {
            console.log("remove with argument id:" + id);
        },

        edit: function (id) {

            // Show configuration Template data
            var jqxhrTemplate = $.ajax({
                url: portletURL.url.template.getTemplateURL + "&templateId=" + id,
                dataType: "json"
                }).done(function(data) {
                    console.log(data);
                    $("#template-id-value").text(data.id);
                    $("#template-name + input").val(data.name);
                    $("#template-description + input").val(data.description);
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching template");
            });

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

            $("#dlg-edit-template").dialog("open");
        },

        self: $("#dlg-edit-template"),

        infoDialog: $("#dlg-info"),

        selectedElementsList: $("#dlg-edit-template-selected-elements"),

        selectedOrganizationsList: $("#dlg-edit-template-selected-organizations")

    });

    // Initialize the dialogs
    dlg.self.dialog({
        title: "Edit template",
        autoOpen: false,
        modal: true,
        width: 650,
        height: 980 ,
        buttons: {
            "Submit changes": function() {
                submitTemplate();
                cleanUpDialog($(this));
                $(this).dialog( "close" );
            },
            Cancel: function() {
                cleanUpDialog($(this));
                $(this).dialog( "close" );
            }
        }
    });

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

    function configureDragAndDrop(){
        $(".dlg-edit-template-list-panel-container").droppable({
            activeClass: "ui-state-highlight",
            drop: function (event, ui) {
                var list = $(this).find("ul");
                var helper = ui.helper;
                //var selected = $(this).siblings(".list-container").find("li.ui-state-highlight");
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
                $("#dlg-info").dialog("open");
                var configData = $(this).data("config");
                storeToTable(configData, $("#dlg-item-table"));
            });
    }
    function populateElements(data){
        var cnt = $("#elements-selection-container");
        var panelSelected =  cnt.find(".selected-list-panel-container");
        var panelAvailable =  cnt.find(".available-list-panel-container");
        var listSelected = panelSelected.find("ul");
        var listAvailable = panelAvailable.find("ul");
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

    function populateOrganizations(data){
        var cnt = $("#organizations-selection-container");
        var panelSelected =  cnt.find(".selected-list-panel-container");
        var panelAvailable =  cnt.find(".available-list-panel-container");
        var listSelected = panelSelected.find("ul");
        var listAvailable = panelAvailable.find("ul");
        var htmlTemplate = "<li class='ui-state-default'>\
                              <div class='dlg-edit-template-organization-id'></div>\
                              <div class='dlg-edit-template-organization-name'></div>\
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
        lastChild.find(".dlg-edit-template-organization-id").text(value.organizationId);
        lastChild.find(".dlg-edit-template-organization-name").text(value.name);
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
        that.find(".list-container").find("ul").empty();
    }

    function cleanUpTable(that){
        that.find("tr").remove();
    }

    function submitTemplate(){
        var outData = {};
        outData["templateId"] = parseInt($("#template-id-value").text());
        outData["templateName"] = $("#template-name + input").val();
        outData["templateDescription"] = $("#dlg-edit-template-description").val();
        outData["elementsSelected"] = JSON.stringify(getSelectedElements());
        outData["organizationsSelected"] = JSON.stringify(getSelectedOrganizations());

        $.post(portletURL.url.template.editTemplateURL, outData).done(function(){
            app.reloadTemplatesTable();
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

})(jQuery);
