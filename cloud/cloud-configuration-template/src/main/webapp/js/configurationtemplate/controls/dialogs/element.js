/**
 * TODO
 * -generalize item selection view
 * -generalize item selection events
 * -generalize item selection fetching data
 * -validate modules

 * Server side
 * -optimize edits
 * -available items can  not contain self
 * -auth aspect
 */
(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.element || {};
    var infoDlg = window.app.dialog.info;

    dlg.mode =  null;

    dlg.html = {};
    dlg.html.idContainer = $($(".dlg-input-container", "#dlg-element-general-tab").first());
    dlg.html.self = $("#dlg-element");
    dlg.html.tabs = $("#dlg-element-tabs");

    // Selection widget
    dlg.html.selectionWidget = {};
    dlg.html.selectionWidget.dependencies = $("#dlg-element-dependencies-tab").find(".dlg-item-selection-container");
    dlg.html.selectionWidget.modules = $("#dlg-element-modules-tab").find(".dlg-item-selection-container");

    // Selection widget internals
    // TODO: remove this once the widget is isolated
    dlg.html.selectionWidget.selectedDependeesList = dlg.html.selectionWidget.dependencies.find(".dlg-item-list-container").first().find("ul");
    dlg.html.selectionWidget.availableDependeesList = dlg.html.selectionWidget.dependencies.find(".dlg-item-list-container").last().find("ul");
    dlg.html.selectionWidget.selectedModulesList = dlg.html.selectionWidget.modules.find(".dlg-item-list-container").first().find("ul");
    dlg.html.selectionWidget.availableModulesList = dlg.html.selectionWidget.modules.find(".dlg-item-list-container").last().find("ul");

    //dlg.html.availableDependeesList = $("ul", "#dlg-element-available-dependees");

    dlg.html.elem = {};
    dlg.html.elem.id = $("#dlg-element-value-id");
    dlg.html.elem.type = $("#dlg-element-value-type");
    dlg.html.elem.name = $("#dlg-element-value-name");
    dlg.html.elem.version = $("#dlg-element-value-version");
    dlg.html.elem.description = $("#dlg-element-value-description");
    dlg.html.elem.minMachines = $("#dlg-element-value-min-machines");
    dlg.html.elem.maxMachines = $("#dlg-element-value-max-machines");
    dlg.html.elem.replicated = $("#dlg-element-replicated-radio");
    dlg.html.elem.minReplicationMachines = $("#dlg-element-value-min-repl-machines");
    dlg.html.elem.maxReplicationMachines = $("#dlg-element-value-max-repl-machines");

    $.extend(dlg, {

		create : function(){
		    dlg.mode = "create";
			dlg.open();
		},

        edit : function(id){
            $.ajax({
                url: portletURL.url.element.getElementURL + "&elementId=" + id,
                dataType: "json"
                }).done(function(data) {
                    dlg.html.elem.id.text(data.id);
                    dlg.html.elem.type.val(data.type);
                    dlg.html.elem.name.val(data.name);
                    dlg.html.elem.version.val(data.version);
                    dlg.html.elem.description.val(data.description);
                    dlg.html.elem.minMachines.val(data.minMachines);
                    dlg.html.elem.maxMachines.val(data.maxMachines);
                    if (data.replicated === true){
                        dlg.html.elem.replicated.find("input").first().prop('checked', true);
                        dlg.html.elem.replicated.find("input").last().prop('checked', false);
                        toggleReplicatedMachinesInput("true");
                    }
                    else{
                        dlg.html.elem.replicated.find("input").first().prop('checked', false);
                        dlg.html.elem.replicated.find("input").last().prop('checked', true);
                        toggleReplicatedMachinesInput("false");
                    }
                    dlg.html.elem.minReplicationMachines.val(data.minReplicationMachines);
                    dlg.html.elem.maxReplicationMachines.val(data.maxReplicationMachines);
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching element");
            });
            dlg.mode = "edit";
            dlg.open(id);
        },

        open : function(id){
			if (dlg.mode !== "create" && dlg.mode != "edit") {
				console.log("Invalid dialog mode: " + dlg.mode);
				return;
			}
            $.when(
                    $.ajax({
                        url: portletURL.url.element.getDependenciesURL + "&elementId=" + id,
                        dataType: "json"
                    }),
                    $.ajax({
                        url: portletURL.url.element.getModulesForElementURL + "&elementId=" + id,
                        dataType: "json"
                    }))
            .done(function(dataDependencies, dataModules){
                // TODO : this function belongs to selectionWidget
                updateSelectionWidget(dataDependencies[0], tpl.item, dlg.html.selectionWidget.selectedDependeesList, dlg.html.selectionWidget.availableDependeesList);
                updateSelectionWidget(dataModules[0], tpl.item, dlg.html.selectionWidget.selectedModulesList, dlg.html.selectionWidget.availableModulesList);
                configureEventHandling();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
                });
            
            var title = "";
            if (dlg.mode == "edit"){
                dlg.html.idContainer.show();
                title = "Edit element";
            }
            else if (dlg.mode == "create"){
                dlg.html.idContainer.hide();
                title = "Create new element";
            }

            // set default value to  radio box
            dlg.html.elem.replicated.find("input").last().prop('checked', true);
            toggleReplicatedMachinesInput("false");

            // open dialog
            dlg.html.self.dialog("option", "title", title);
            dlg.html.tabs.tabs();
            dlg.html.tabs.tabs('select', 0);
            dlg.html.self.show();
            dlg.html.self.dialog("open");
        },

        close : function(){
            // cleanup dialog html elements
            dlg.html.self.find(".dlg-item-list-container").find("ul").empty();
            dlg.html.elem.id.text("");
            dlg.html.elem.type.val("");
            dlg.html.elem.version.val("");
            dlg.html.elem.name.val("");
            dlg.html.elem.description.val("");
            dlg.html.elem.minMachines.val("");
            dlg.html.elem.maxMachines.val("");
            dlg.html.elem.replicated.find("input").first().prop('checked', false);
            dlg.html.elem.replicated.find("input").last().prop('checked', true);
            dlg.html.elem.minReplicationMachines.val("");
            dlg.html.elem.maxReplicationMachines.val("");

            // clear error styles
            $.each(dlg.html.self.find("input"), function(index, value){
                clearStyleForErrorInput(index, $(value));
            });

            // close jQuery dialog
            dlg.html.self.dialog("close");
        }
    });

    // initialize dialog
    dlg.html.self.dialog({
        title: "Edit element",
        autoOpen: false,
        modal: true,
        width: 650,
        height: 560 ,
        buttons: {
            "Submit changes": function() {
                if (submitElement(dlg.mode) === 0){
                    dlg.close();
                }
            },
            Cancel: function() {
                dlg.close();
            }
        }
    });
    
    // Sending data to backend

    function submitElement(mode){
        var err = 0;

        // get input data
        var dependees = getSelectionWidgetItems(dlg.html.selectionWidget.selectedDependeesList);
        var modules = getSelectionWidgetItems(dlg.html.selectionWidget.selectedModulesList);
        var element = getElement();

        // validate input data
        if (!validateInput(element, dependees, modules)){
            return;
        }

        // serialize input data
        var outData = {};
        outData.element = JSON.stringify(element);
        outData.dependees = JSON.stringify(dependees);
        outData.modules = JSON.stringify(modules);

        // send input data
        $.post((mode == "edit") ? portletURL.url.element.editElementURL : portletURL.url.element.createElementURL, outData)
        .done(function(){
            app.reloadElementsTable();
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            alertPostFailure(dlg.mode, textStatus, errorThrown);
            err = 1;
        });
        
        return err;
    }

    // Events handling

    function configureEventHandling(){
        configureDragAndDrop();
        bindDependencyListItemClicks();
        bindInputClicksAndKeys();
        infoDlg.bind();
        bindRadioChange();
    }
/*
    function configureDragAndDrop(){
        $(".dlg-list-panel-container", "#dlg-dependency-selection-container").droppable({
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
        $("li", "#dlg-dependency-selection-container").draggable({
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
    }
*/
    //dlg-element-tabs
	function configureDragAndDrop(){
		dlg.html.self.find(".dlg-list-panel-container").droppable({
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
        $("li", ".dlg-item-selection-container").draggable({
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
    }

    function bindDependencyListItemClicks(){
         $("li", ".dlg-item-selection-container").
            click(function(){
                $(this).toggleClass("ui-state-highlight");
         });
    }

    function bindGeneralAttributeInputClicks(){
        $("input","#dlg-element-general-tab").bind( "click",  function(){
            clearStyleForErrorInput(0, $(this));
        });
    }

    function bindInputClicksAndKeys(){
        var inputs = $("input", "#dlg-element-fields");
        inputs.bind("click", function(){
            clearStyleForErrorInput(0, $(this));
        });
        inputs.keypress(function(event) {
            clearStyleForErrorInput(0, $(this));
        });
    }

    function bindRadioChange(){
        dlg.html.elem.replicated.find("input").on('change', function(){
            var replicated = dlg.html.elem.replicated.find('input[name=dlg-element-replicated-radio]:checked').val();
            toggleReplicatedMachinesInput(replicated);
        });
    }

    function toggleReplicatedMachinesInput(replicated){
        if (replicated === "false"){
          dlg.html.elem.minReplicationMachines.parent().hide();
          dlg.html.elem.maxReplicationMachines.parent().hide();
        }
        else{
          dlg.html.elem.minReplicationMachines.parent().show();
          dlg.html.elem.maxReplicationMachines.parent().show();
        }
    }

    function updateSelectionWidget(data, template, listSelected, listAvailable){
        var selectedIndices = [];
        $.each(data.selected, function(index, value){
            storeItemToDom(template, value, listSelected);
            selectedIndices.push(value.id);
        });
        $.each(data.available, function(index, value){
            if (selectedIndices.indexOf(value.id) == -1){
                storeItemToDom(template, value, listAvailable);
            }
        });
    }
    
/*
    function storeDependeesToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        lastChild.find(".dlg-element-dependee-name").text(value.name.substring(0, 17));
        var lastChild = list.find("li:last-child");
        lastChild.find(".dlg-element-dependee-version").text(value.version.substring(0, 5));
        lastChild.data("config", value);
    }
*/
    // TODO:fixme
    function storeItemToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");
        lastChild.find("div").first().text(value.name.substring(0, 17));
        lastChild.find("div").last().text(value.version.substring(0, 5));
        lastChild.data("config", value);
    }
    
    // Style handling functions

    function moveMultipleElements(list, selected) {
        $(selected).each(function () {
            $(this).appendTo(list).removeClass("ui-state-highlight").fadeIn();
        });
    }

    function clearStyleForErrorInput(index, item){
        if (item.hasClass("dlg-error-input")){
            item.removeClass("dlg-error-input");
        }
    }

    function moveSingleElement(elem, list) {
        elem.appendTo(list).removeClass("ui-state-highlight").fadeIn();
    }

    // Utility functions

    function isPosInt(obj){
        return (obj !== "" && typeof obj !== 'undefined' && !isNaN(obj) && (Math.round(obj) == obj) && obj > 0) ? true : false;
    }

    /*
    function getDependencies(){
        var selectedItems = [];
            var arrayOfLis = dlg.html.selectedDependeesList.find("li");
        for (var i = 0; i < arrayOfLis.length; i++){
            selectedItems.push($(arrayOfLis[i]).data("config").id);
        }
        return selectedItems;
    }
    */

    function getSelectionWidgetItems(widget){
            var selectedItems = [];
                var arrayOfLis = widget.find("li");
            for (var i = 0; i < arrayOfLis.length; i++){
                selectedItems.push($(arrayOfLis[i]).data("config").id);
            }
            return selectedItems;
        }

    function getElement(){
        var e = {};
        e.id = (dlg.mode == "edit") ? parseInt(dlg.html.elem.id.text(), 10) : -1;
        e.type = dlg.html.elem.type.val();
        e.name = dlg.html.elem.name.val();
        e.version = dlg.html.elem.version.val();
        e.description = dlg.html.elem.description.val();
        e.minMachines = dlg.html.elem.minMachines.val();
        e.maxMachines = dlg.html.elem.maxMachines.val();
        e.replicated = dlg.html.elem.replicated.find('input[name=dlg-element-replicated-radio]:checked').val();
        e.minReplicationMachines = dlg.html.elem.minReplicationMachines.val();
        e.maxReplicationMachines = dlg.html.elem.maxReplicationMachines.val();
        return e;
    }

    // Alerts

    function alertPostFailure(mode, textStatus, errorThrown){
        alert("Server error at template" + mode + ", text status:" + textStatus + " " + "errorThrown:" + errorThrown);
    }

    function alertWrongInput(item, msg){
        alert(msg);
        item.addClass("dlg-error-input");

        // open the tab with erroneous item and focus on it
        var inElementTab = (item.parents("#dlg-element-general-tab")).length > 0;
        if (inElementTab){
            dlg.html.tabs.tabs('select', 0);
        }
        else{
            dlg.html.tabs.tabs('select', 2);
        }

        item.focus();
    }

    // Validation

    function validateInput(element, dependees, modules){
        var res = true;

        // validate element
        if (!isPosInt(element.id) && dlg.mode == "edit") {
            res = false;
            console.log("err.internalError");
        }
        else if (!isPosInt(element.type)){
            res = false;
            alertWrongInput(dlg.html.elem.type, err.mustBePositiveInteger);
        }
        else if (element.name === ""){
            res = false;
            alertWrongInput(dlg.html.elem.name, err.emptyItem);
        }
        else if (element.version === ""){
            res = false;
            alertWrongInput(dlg.html.elem.version, err.emptyItem);
        }
        else if (!isPosInt(element.minMachines)){
            res = false;
            alertWrongInput(dlg.html.elem.minMachines, err.mustBePositiveInteger);
        }
        else if (!isPosInt(element.maxMachines)){
            res = false;
            alertWrongInput(dlg.html.elem.maxMachines, err.mustBePositiveInteger);
        }
        else if (element.minMachines >= element.maxMachines){
            res = false;
            alertWrongInput(dlg.html.elem.maxMachines, err.invalidMachineRange);
        }
        else if (element.replicated !== 'false' && element.replicated !== 'true'){
            res = false;
            alertWrongInput(dlg.html.elem.replicated, err.mustBeBoolean);
        }
        else if (!isPosInt(element.minReplicationMachines) && element.replicated === 'true'){
            res = false;
            alertWrongInput(dlg.html.elem.minReplicationMachines, err.mustBePositiveInteger);
        }
        else if (!isPosInt(element.maxReplicationMachines) && element.replicated === 'true'){
            res = false;
            alertWrongInput(dlg.html.elem.maxReplicationMachines, err.mustBePositiveInteger);
        }
        else if (element.minReplicationMachines >= element.maxReplicationMachines && element.replicated === 'true') {
            res = false;
            alertWrongInput(dlg.html.elem.maxReplicationMachines, err.invalidMachineRange);
        }

        if (res === true){
            res = validateItems(dependees) && validateItems(modules);
        }

        return res;
    }

})(jQuery);
