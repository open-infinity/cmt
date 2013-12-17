(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.template || {};
    var infoDlg = window.app.dialog.info;

    $.extend(dlg, {

        // jQuery objects from DOM elements

        // General
        self : $("#dlg-edit-template"),

        mode : null,

        tabs : $("#dlg-edit-template-tabs"),

        // Template
        templateIdContainer : $(".dlg-edit-template-template-label-container").first(),

        templateId : $("#dlg-edit-template-id-value"),

        templateName : $("#dlg-edit-template-name + input"),

        templateDescription : $("#template-description + textarea"),

        // Elements
        selectedElementsPanel : $("#elements-selection-container").find(".dlg-edit-template-list-panel-container").first(),

        selectedElementsList : $("#dlg-edit-template-selected-elements"),

        availableElementsPanel : $("#elements-selection-container").find(".dlg-edit-template-list-panel-container").last(),

        // Organizations
        selectedOrganizationsPanel : $("#organizations-selection-container").find(".dlg-edit-template-list-panel-container").first(),

        availableOrganizationsPanel : $("#organizations-selection-container").find(".dlg-edit-template-list-panel-container").last(),

        selectedOrganizationsList : $("#dlg-edit-template-selected-organizations"),

        // Dialog functions

        create: function(){
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
        },

        remove: function(id){
            console.log("remove with argument id:" + id);
        },

        edit: function(id){
            $.ajax({
                url: portletURL.url.template.getTemplateURL + "&templateId=" + id,
                dataType: "json"
                }).done(function(data) {
                    dlg.templateId.text(data.id);
                    dlg.templateName.val(data.name);
                    dlg.templateDescription.val(data.description);
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
                infoDlg.bind();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
                });

            dlg.open("edit");
        },

        open : function(mode){
            dlg.mode = mode;
            var title = "Create new template";
            if (mode == "edit"){
                dlg.templateIdContainer.show();
                title = "Edit template";
            }
            else{
                console.log("Unexpected mode for dialog.");
            }
            dlg.self.dialog("option", "title", title);
            dlg.tabs.tabs({active: 1});
            dlg.self.show();
            dlg.self.dialog("open");
        }
    });

    // Initialize dialogs

    dlg.self.dialog({
        title: "Edit template",
        autoOpen: false,
        modal: true,
        width: 650,
        height: 510,
        draggable: false,
        resizable: false,
        buttons: {
            "Submit changes": function() {
                submitTemplate(dlg.mode);
                cleanUpDialog($(this));
                //$(this).hide();
                $(this).dialog( "close" );
            },
            Cancel: function() {
                cleanUpDialog($(this));
                //$(this).hide();
                $(this).dialog( "close" );
            }
        }
    });

    // Helper functions

    function configureDragAndDrop(){
        $(".dlg-edit-template-list-panel-container").droppable({
            activeClass: "ui-state-highlight",
            drop: function (event, ui) {
                var list = $(this).find("ul");
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

        /*
        $(".dlg-item-list-container").find("li").
            click(function(){
                $(this).toggleClass("ui-state-highlight");
            }).
            dblclick(function () {
                app.dialog.info.dialog("open");
                var configData = $(this).data("config");
                storeToTable(configData, $("#dlg-item-table"));
            });
            */
    }

    function populateElements(data){
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

    function populateOrganizations(data){
        var listSelected = dlg.selectedOrganizationsPanel.find("ul");
        var listAvailable = dlg.availableOrganizationsPanel.find("ul");
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
        lastChild.find(".name").text(value.name.substring(0, 17));
        lastChild.find(".version").text(value.version.substring(0, 5));
        lastChild.data("config", value);
    }

/*
    function storeToTable(configData, table){
        for (var key in configData) {
            table.append("<tr>" + "<th style='text-align: left;'>" + key + "</th>" + "<td>" +  configData[key] +  "</td>" + "</tr>");
        }
    }
  */

    function storeOrganizationToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");

        var oid = value.organizationId;
        if (oid >= 1000000){
            oid = Math.floor(100000 / oid) + "..";
        }

        lastChild.find(".dlg-edit-template-organization-id").text(oid);
        lastChild.find(".dlg-edit-template-organization-name").text(value.name.substring(0, 13));
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
        dlg.templateId.text("");
        dlg.templateName.val("");
        dlg.templateDescription.val("");
    }

    function submitTemplate(mode){
        var outData = {};
        outData.templateId = parseInt(dlg.templateId.text());
        outData.templateName = dlg.templateName.val();
        outData.templateDescription = dlg.templateDescription.val();
        outData.elementsSelected = JSON.stringify(getSelectedElements());
        outData.organizationsSelected = JSON.stringify(getSelectedOrganizations());

        $.post((mode == "edit") ? portletURL.url.template.editTemplateURL : portletURL.url.template.createTemplateURL, outData)
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