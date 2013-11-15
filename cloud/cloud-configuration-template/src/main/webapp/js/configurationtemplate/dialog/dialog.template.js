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

            // Show configuraiton elements
            var jqxhrElement = $.ajax({
                url: portletURL.url.template.getElementsForTemplateURL + "&templateId=" + id,
                dataType: "json"
            }).done(function(data) {
                var htmlTemplate = "<li class='ui-state-default'>\
                                        <div class='name'></div>\
                                        <div class='version'></div>\
                                    </li>";

                var listSelected = $("#selected-elements-container").find("ul");
                var listAvailable = $("#available-elements-container").find("ul");

                var selectedIndices = [];
                $.each(data.selectedElements, function(index, value){
                    storeElementToDom(htmlTemplate, value, listSelected);
                    selectedIndices.push(value.id);
                });
                $.each(data.availableElements, function(index, value){
                    if (selectedIndices.indexOf(value.id) == -1){
                        storeElementToDom(htmlTemplate, value, listAvailable);
                    }
                });
                configureDragAndDrop();

            }).fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching elements for template ");
            });

            console.log("edit with argument id:" + id);

            /*
            $("#template-edit-organization-grid").jqGrid({
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
                colNames:[
                    'organizationId', 'companyId', 'parentOrganizationId', 'treePath', 'name',
                    'type_', 'recursable', 'regionId', 'countryId', 'statusId', 'comments'
                ],
                colModel:[
                    {name:'organizationId', index:'organizationId', width:50, align:"center"},
                    {name:'companyId', index:'companyId', width:50, align:"center"},
                    {name:'parentOrganizationId', index:'parentOrganizationId', width:50, align:"center"},
                    {name:'treePath', index:'treePath', width:50, align:"center"},
                    {name:'name', index:'name', width:50, align:"center"},
                    {name:'type_', index:'type_', width:50, align:"center"},
                    {name:'recursable', index:'recursable', width:50, align:"center"},
                    {name:'regionId', index:'regionId', width:50, align:"center"},
                    {name:'countryId', index:'countryId', width:50, align:"center"},
                    {name:'statusId', index:'statusId', width:50, align:"center"},
                    {name:'comments', index:'comments', width:50, align:"center"}
                ],
                rowNum: 10,
                width: 900,
                height: 300,
                pager: '#template-edit-organization-grid-pager',
                sortname: 'id',
                viewrecords: true,
                shrinkToFit: false,
                sortorder: 'id'
            });

            */
            $("#dialog-template-edit").dialog("open");
        }
    });

    // Initialize the dialogs

    $("#dialog-template-edit").dialog({
        title: "Edit template",
        autoOpen: false,
        modal: true,
        width: 1000,
        height: 1000 ,
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

    $("#dialog-template-create").dialog({
        title: "Create template",
        autoOpen: false,
        modal: true,
        width: 700,
        height: 1000
    });

    function storeElementToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");
        lastChild.find(".name").text(value.name);
        lastChild.find(".version").text(value.version);
        $.data(lastChild, "config", value);
    }

    function configureDragAndDrop(){
        /*
        $("li", "#available-elements" ).draggable({
            revert: "invalid",
            containment: "document",
            helper: "clone",
            cursor: "move",
            scroll:true
        });
        $("#available-elements-container").droppable({
            accept: "#selected-elements > li",
            activeClass: "ui-state-highlight",
            drop: function(event, ui) {
                unSelect(ui.draggable);
            }
        });
        $("#selected-elements-container").droppable({
            accept: "#available-elements > li",
            activeClass: "ui-state-highlight",
            drop: function(event, ui) {
                select(ui.draggable);
            }
        });
        $("li", "#selected-elements" ).draggable({
            revert: "invalid",
            containment: "document",
            helper: "clone",
            cursor: "move",
            scroll:true
        });
        */
        $(".elements-container").droppable({
            activeClass: "ui-state-highlight",
            drop: function (event, ui) {
                var list = $(this).find("ul");
                var helper = ui.helper;
                var selected = $(this).siblings(".elements-container").find("li.ui-state-highlight");
                if (selected.length > 1) {
                    moveMultipleElements(list, selected);
                } else {
                    moveSingleElement(ui.draggable, list);
                }
            },
            tolerance: "touch"
        });

        $("li", ".elements-container").draggable({
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

        $("#elements-selection-container").find("li", ".elements-list-container").click(function () {
            $(this).toggleClass("ui-state-highlight");
        });
    }

    /*
    function select(item){
        var selected = $("#selected-elements");
        item.appendTo(selected).fadeIn();
    }

    function unSelect(item){
        var available = $("#available-elements");
        item.appendTo(available).fadeIn();
    }
    */

    function moveMultipleElements(list, selected) {
        $(selected).each(function () {
            $(this).appendTo(list).removeClass("ui-state-highlight").fadeIn();
        });
    }

    function moveSingleElement(elem, list) {
        elem.appendTo(list).removeClass("ui-state-highlight").fadeIn();
    }

    function cleanUpDialog(){
        $("#selected-elements").empty();
        $("#available-elements").empty();
    }

    function submitTemplate(){
        var outData = {};
        //outData["templateName"] = $item.attr('id').slice(8);
        //outData["templateDescription"] = target.parentNode.getAttribute('data-pub-id');
        $.post(portletURL.url.cluster.updatePublishedURL, outData);
    }

})(jQuery);
