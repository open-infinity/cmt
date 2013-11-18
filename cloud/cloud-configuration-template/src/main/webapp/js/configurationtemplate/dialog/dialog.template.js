(function($) {
    var template = window.app.dialog.template || {};
    template.elementsTable = $("edit-template-elements-grid");
    $.extend(template, {

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
        }
    });

    // Initialize the dialogs

    $("#dlg-edit-template").dialog({
        title: "Edit template",
        autoOpen: false,
        modal: true,
        width: 600,
        height: 840 ,
        buttons: {
            "Submit changes": function() {
                submitTemplate();
                cleanUpDialog();
                $(this).dialog( "close" );
            },
            Cancel: function() {
                cleanUpDialog();
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
        $(".list-container").find("li").click(function(){
            $(this).toggleClass("ui-state-highlight");
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
            console.log("found? =" + selectedIndices.indexOf(value.id));
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
                              <div class='dlg-edit-template-organizationId'></div>\
                              <div class='dlg-edit-template-organizationName'></div>\
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
            $.data(lastChild, "config", value);
    }

    function storeOrganizationToDom(htmlTemplate, value, list){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            lastChild.find(".dlg-edit-template-organizationId").text(value.organizationId);
            lastChild.find(".dlg-edit-template-organizationName").text(value.name);
            $.data(lastChild, "config", value);
     }

    function moveMultipleElements(list, selected) {
        $(selected).each(function () {
            $(this).appendTo(list).removeClass("ui-state-highlight").fadeIn();
        });
    }

    function moveSingleElement(elem, list) {
        elem.appendTo(list).removeClass("ui-state-highlight").fadeIn();
    }


    function cleanUpDialog(){
        $(".list-container").find("ul").empty();
    }

    function submitTemplate(){
        var outData = {};
        $.post(portletURL.url.cluster.updatePublishedURL, outData);
    }

})(jQuery);
