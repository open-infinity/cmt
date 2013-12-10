(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.element || {};
    var infoDlg = window.app.dialog.info;
    var timer;

    dlg.mode =  null;

    dlg.state = {};
    dlg.state.selectedKey = null;

    dlg.model = {};
    dlg.model.parameters = {};

    dlg.txt = {};
    dlg.txt.addNewKey = "Add new key";
    dlg.txt.addNewValue = "Add new value";
    dlg.txt.err = {};
    dlg.txt.err.emptyKey = "Key name must not be empty";
    dlg.txt.err.emptyValue = "Value name must not be empty";
    dlg.txt.err.keyFirst = "First create a key, then assign value to it";
    dlg.txt.err.keyAlreadyExists = "Name already exists";
    dlg.txt.err.mustBeInteger = "Integer value expected";
    dlg.txt.err.mustBeBoolean = "Boolean value expected";
    dlg.txt.err.mustBeString = "String value expected";
    dlg.txt.err.internalError = "Internal error";

    dlg.html = {};

    dlg.html.idContainer = $($(".dlg-element-container", "#dlg-element-general-tab").first());
    dlg.html.self = $("#dlg-element");
    dlg.html.tabs = $("#dlg-element-tabs");
    dlg.html.selectedDependeesList = $("ul", "#dlg-element-selected-dependees");
    dlg.html.availableDependeesList = $("ul", "#dlg-element-available-dependees");
    dlg.html.parameterKeysList = $("ul", "#dlg-keys");
    dlg.html.parameterValuesList = $("ul", "#dlg-values");
    dlg.html.keyName = $("#dlg-element-key-name");

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

    dlg.html.template = {};
    dlg.html.template.dependee = "<li class='ui-state-default'><div class='dlg-element-dependee-name'></div><div class='dlg-element-dependee-version'></div></li>";
    dlg.html.template.key = "<li class='ui-state-default key dlg-element-key-value-list-item'><div class='dlg-element-parameter-key-name'><input class='dlg-key-name' type='text'/></div><div class='dlg-element-list-item-button dlg-element-list-item-delete-button'>-</div></li>";
    dlg.html.template.value = "<li class='ui-state-default dlg-element-key-value-list-item'><div><div class='dlg-element-parameter-value-value'><input class='dlg-value-value' type='text'/></div></div><div class='dlg-element-list-item-button dlg-element-list-item-delete-button'>-</div></li>";

    $.extend(dlg, {

        create : function(){
            $.ajax({
                url: portletURL.url.element.getAllAvailableDependenciesURL,
                dataType: "json"
            })
            .done(function(data){
                dlg.keyCount = 0;
                dlg.model.parameters = {};
                console.log("received parameters:" + dlg.model.parameters);
                populateDependencies(data);
                showViewSelectedKeyInitial([]);
                configureEventHandling();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
                });
            dlg.open("create");
        },

        remove : function(id){
            console.log("remove with argument id:" + id);

        },

        edit : function(id){
            var jqxhrTemplate = $.ajax({
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
            $.when(
                $.ajax({
                    url: portletURL.url.element.getDependenciesURL + "&elementId=" + id,
                    dataType: "json"
                }),
                $.ajax({
                    url: portletURL.url.element.getParameterKeysAndValuesURL + "&elementId=" + id,
                    dataType: "json"
                }))
            .done(function(dataDependencies, dataKeyValues){
                dlg.keyCount = 0;
                //delete dlg.model.parameters;
                dlg.model.parameters = dataKeyValues[0];
                console.log("received parameters:" + dlg.model.parameters);
                populateDependencies(dataDependencies[0]);
                showViewSelectedKeyInitial(dataKeyValues[0]);
                configureEventHandling();
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

            dlg.html.elem.replicated.find("input").last().prop('checked', true);
            toggleReplicatedMachinesInput("false");

            dlg.html.self.dialog("option", "title", title);
            dlg.html.tabs.tabs({active: 1});
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
        height: 560 ,
        buttons: {
            "Submit changes": function() {
                if (submitElement(dlg.mode) === 0){
                    cleanUpDialog($(this));
                    $(this).dialog( "close" );
                }
            },
            Cancel: function() {
                cleanUpDialog($(this));
                $(this).hide();
                $(this).dialog( "close" );
            }
        }
    });

    // Helper functions

    function configureEventHandling(){
        configureDragAndDrop();
        bindDependencyListItemClicks();
        bindKeyListItemClicks();
        bindNewItemInputClicks();
        bindDeleteKeysButtonsClick($(".dlg-element-list-item-delete-button", "#dlg-keys"));
        bindNewKeyButtonClick();
        infoDlg.bind();
        bindRadioChange();
    }

    function configureDragAndDrop(){
        $(".dlg-list-panel-container", "#dlg-dependency-selection-container").droppable({
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

    function bindDependencyListItemClicks(){
         $("li", "#dlg-dependency-selection-container").
            click(function(){
                $(this).toggleClass("ui-state-highlight");
         });
    }

    function bindGeneralAttributeInputClicks(){
        $("input", ".key", "#dlg-keys").bind( "click",  function(){

        });
    }

    function bindKeyListItemClicks(){
        $("input", ".key", "#dlg-keys").bind( "click",  function(){

            // clear styles for erroneous input
            if ($(this).hasClass("dlg-error-input")){
                $(this).removeClass("dlg-error-input");
            }

            // do nothing if selected key remains the same
            if (dlg.state.selectedKey == $(this).parents("li").data("keyName")){
                return;
            }

            // if a different key was selected, update model and process key selection
            else{
                var err = updateModel();
                if (err === 0){
                    showViewSelectedKey($(this));
                }
                else{
                    console.log("Error updating model:" + err);
                }
            }
        });
    }

    function bindNewItemInputClicks(){
        bindInputClicks(findNewKeyInput());
        bindInputClicks(findNewValueInput());
    }

    function bindInputClicks(items){
        items.bind( "click", function(){
            var val =  $(this).val();
            if (val === "" || val == dlg.txt.addNewKey || val == dlg.txt.addNewValue /*|| val == dlg.txt.addNewType*/){
                $(this).val("").css("color", "black");
            }
        });
    }

    function bindDeleteButtonsClick(items){
        bindDeleteKeysButtonsClick(items);
        bindDeleteValuesButtonsClick(items);
    }

    function bindDeleteValuesButtonsClick(items){
        items.bind( "click", function(){
            var values = dlg.model.parameters[dlg.state.selectedKey];
            var index = $(this).parents("li").data("index");
            if (index > -1) {
                values.splice(index, 1);
                }
        $(this).parent().remove();
        });
    }

    function bindDeleteKeysButtonsClick(items){
        items.bind( "click", function(){

            // update SelectedKeyView
            var parent = $(this).parent();
            var input = parent.find("input");
            var keyName =  input.val();
            if (input.hasClass("dlg-key-name")){
                parent.remove();
                showViewSelectedKey(dlg.html.parameterKeysList.find("input").first().focus());
            }

            // update key model
            delete dlg.model.parameters[keyName];
        });
    }

    function bindNewKeyButtonClick(){
        $(".dlg-element-new-key-button").bind( "click", function(){

            // fetch the new key
            var keyInput = $(this).parent("li").find("input");
            var key = keyInput.val();
            if (key === "" || key == dlg.txt.addNewKey){
                alertWrongInput(keyInput, dlg.txt.err.emptyKey);
                return;
            }

            dlg.state.selectedKey = key;

            // store the new key into model

            // check if the key already exists in the model, and add the key if it does not already exist
            var exists = false;
            if (!$.isEmptyObject(dlg.model.parameters)){
                $.each(dlg.model.parameters, function(parameterKey, parameterValues){
                    if (parameterKey == key){
                        alertWrongInput(keyInput, dlg.txt.err.keyAlreadyExists);
                        exists = true;
                    }
                    //else{
                    //    dlg.model.parameters[key] = [];
                    //}
                });
                if (exists === true) {
                    return;
                }

                // store key to model
                else{
                    //dlg.model.parameters[key] = [];
                    dlg.model.parameters[key] = [];
                }
            }

            // if model is empty, store key to model
            else{
                //dlg.model.parameters[key] = [];
                dlg.model.parameters[key] = [];
            }

            // update view
            $(this).parent().remove();
            dlg.html.parameterValuesList.empty();
            dlg.html.keyName.text(key);
            storeKeyToDom(dlg.html.template.key, key, dlg.html.parameterKeysList).find("input").focus();
            storeKeyToDom(dlg.html.template.key, null, dlg.html.parameterKeysList);
            storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList, null);

            // re-bind events
            unbindKeyHandlers();
            bindKeyListItemClicks();
            bindNewItemInputClicks();
            bindDeleteButtonsClick($(".dlg-element-list-item-delete-button"));
            bindNewKeyButtonClick();
        });
    }

    function bindNewValueButtonClick(items){
        items.bind( "click", function(){

            var value = getParameterValue($(this).parent("li"));
            if (value === null) {
                console.log("Error reading parameters");
                return;
            }
            // locally store new value and assign it to key
            if (dlg.state.selectedKey === null || typeof dlg.model.parameters[dlg.state.selectedKey] === 'undefined'){
                //alert(dlg.txt.err.internalError);
                //dlg.html.parameterKeysList.find(".dlg-element-new-key-button").parent().find("input")
                alertWrongInput(findNewKeyInput(), dlg.txt.err.keyFirst);
                return;
            }
            else{
                var values = dlg.model.parameters[dlg.state.selectedKey];
                values.push(value);
            }

            // create and remove items
            $(this).parent("li").remove();
            storeValueToDom(dlg.html.template.value, value, dlg.html.parameterValuesList, dlg.model.parameters[dlg.state.selectedKey].length -1);
            storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList, null);
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

    function unbindKeyHandlers(){
        $("input", ".key", "#dlg-keys").unbind();
        //$(".dlg-element-new-key-button").parent().find("input").unbind();
        findNewKeyInput().unbind();
        //$(".dlg-element-new-value-button").parent().find("input").unbind();
        findNewValueInput().unbind();
        $(".dlg-element-list-item-delete-button").unbind();
        $(".dlg-element-new-key-button").unbind();
    }

    function unbindValueHandlers(){
        //$(".dlg-element-new-key-button").parent().find("input").unbind();
        findNewKeyInput().unbind();
        //$(".dlg-element-new-value-button").parent().find("input").unbind();
        findNewValueInput().unbind();
        $(".dlg-element-list-item-delete-button").unbind();
        $(".dlg-element-new-value-button", "#dlg-values").unbind();
    }

    function populateDependencies(data){
        try{
            var selectedIndices = [];

            $.each(data.selected, function(index, value){
               storeDependeesToDom(dlg.html.template.dependee, value, dlg.html.selectedDependeesList);
               selectedIndices.push(value.id);
            });

            $.each(data.available, function(index, value){
               if (selectedIndices.indexOf(value.id) == -1){
                   storeDependeesToDom(dlg.html.template.dependee, value, dlg.html.availableDependeesList);
               }
            });
        }
        catch(err){
            console.log(err.message);
        }
    }

    // TODO: use generalized showViewSelectedKey() instead this function
    function showViewSelectedKeyInitial(data){
        var count = 0;

        // show all keys. Show values only for for the first key.
        $.each(data, function(parameterKey, parameterValues){

            // show values for the first key, which will be selected by default.
            if (count++ === 0){
                var htmlKey = storeKeyToDom(dlg.html.template.key, parameterKey, dlg.html.parameterKeysList);
                // display values for key
                dlg.state.selectedKey = parameterKey;
                $.each(parameterValues, function(index, parameterValue){
                    storeValueToDom(dlg.html.template.value, parameterValue, dlg.html.parameterValuesList, index);
                });
                dlg.html.keyName.text(parameterKey);
            }
            else{
                storeKeyToDom(dlg.html.template.key, parameterKey, dlg.html.parameterKeysList);
            }
        });

        // Store "new item" rows to DOM
        storeKeyToDom(dlg.html.template.key, null, dlg.html.parameterKeysList);
        storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList, null);
    }

    function showViewSelectedKey(key){

        // clear ValuesView for previously selected key
        dlg.html.parameterValuesList.empty();

        // update ValuesView with find values for the selected key
        var found = false;
        $.each(dlg.model.parameters, function(parameterKey, parameterValues){
            if (parameterKey == key.val() && typeof parameterValues != 'undefined'){
                $.each(parameterValues, function(index, value){
                    storeValueToDom(dlg.html.template.value, value, dlg.html.parameterValuesList, index);
                });
                dlg.html.keyName.text(key.val());
                found = true;
            }
        });
        // Key not in model
        if (!found){
            dlg.html.keyName.text("");
        }

        // add rows for creation of new values to ValuesView
        storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList, null);

        // store state
        dlg.state.selectedKey = key.val();
    }

    function storeDependeesToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");
        lastChild.find(".dlg-element-dependee-name").text(value.name.substring(0, 17));
        lastChild.find(".dlg-element-dependee-version").text(value.version.substring(0, 5));
        lastChild.data("config", value);
    }

    function storeKeyToDom(htmlTemplate, value, list){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            if (value !== null){
                lastChild.find(".dlg-key-name").val(value);
                lastChild.data("keyName", value);
            }
            else {
                lastChild.find(".dlg-key-name").val(dlg.txt.addNewKey).css("color", "silver");
                lastChild.find(".dlg-element-list-item-button").text("+").addClass("dlg-element-new-key-button").removeClass("dlg-element-list-item-delete-button").removeClass("key");
            }
            return lastChild;
    }

    function storeValueToDom(htmlTemplate, paramValue, list, index){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            if (paramValue !== null && index!== null){
                lastChild.find(".dlg-value-value").val(paramValue);
                bindDeleteValuesButtonsClick(lastChild.find(".dlg-element-list-item-delete-button"));
                lastChild.data("index", index);
            }
            else{
                // create a new value item
                lastChild.find(".dlg-value-value").val(dlg.txt.addNewValue).css("color", "silver");
                var addButton = lastChild.find(".dlg-element-list-item-button");
                addButton.text("+").addClass("dlg-element-new-value-button").removeClass("dlg-element-list-item-delete-button");

                // bind events for new value item
                bindInputClicks(lastChild.find("input"));
                bindNewValueButtonClick(addButton);

                lastChild.data("index", -1);
            }
    }

    function storeNewItemToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        list.find("li:last-child").text(value);
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
        dlg.html.keyName.text("");
        dlg.html.elem.id.text("");
        dlg.html.elem.type.val("");
        dlg.html.elem.version.val("");
        dlg.html.elem.name.val("");
        dlg.html.elem.description.val("");
        dlg.html.elem.minMachines.val("");
        dlg.html.elem.maxMachines.val("");
        //dlg.html.elem.replicated.val("");
        //$(this).prop('checked', false);
        dlg.html.elem.replicated.find("input").first().prop('checked', false);
        dlg.html.elem.replicated.find("input").last().prop('checked', true);

        dlg.html.elem.minReplicationMachines.val("");
        dlg.html.elem.maxReplicationMachines.val("");
        delete dlg.model.parameters;
        dlg.state.selectedKey = null;
    }

    function cleanUpTable(that){
        that.find("tr").remove();
    }

    function submitElement(mode){
        var err = 0;
        if (updateModel() === 0){
            var element = {};
            element["id"] = (dlg.mode == "edit") ? parseInt(dlg.html.elem.id.text(), 10) : -1;
            element["type"] = dlg.html.elem.type.val();
            element["name"] = dlg.html.elem.name.val();
            element["version"] = dlg.html.elem.version.val();
            element["description"] = dlg.html.elem.description.val();
            element["minMachines"] = dlg.html.elem.minMachines.val();
            element["maxMachines"] = dlg.html.elem.maxMachines.val();
            //element["replicated"] = dlg.html.elem.replicated.val();
            element["replicated"] = dlg.html.elem.replicated.find('input[name=dlg-element-replicated-radio]:checked').val();
            element["minReplicationMachines"] = dlg.html.elem.minReplicationMachines.val();
            element["maxReplicationMachines"] = dlg.html.elem.maxReplicationMachines.val();

            var outData = {};
            outData.element = JSON.stringify(element);
            outData.dependencies = JSON.stringify(getDependencies());
            outData.parameters = JSON.stringify(dlg.model.parameters);

            //var dataValid = validateInput(outData);
            if !validateInput(outData) return;
            console.log("Posting element parameters:" + outData.parameters);

            $.post((mode == "edit") ? portletURL.url.element.editElementURL : portletURL.url.element.createElementURL, outData)
            .done(function(){
                app.reloadElementsTable();
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                alertPostFailure(dlg.mode, textStatus, errorThrown);
                err = 2;
            });
        }
        else{
            console.log("Invalid parameters, aborting submit");
            err = 1;
        }
        return err;
    }

    function updateModel(){
        var err = 1;
        if( updateKeysModel() === 0 && updateValuesModel() === 0){
            err = 0;
        }
        return err;
    }

    // for each key, check if possibly edited key name is matching with the value stored in model.
    function updateKeysModel(){
        var err = 0;
        var arrayOfKeyLis = dlg.html.parameterKeysList.find(".dlg-element-key-value-list-item");
        for (var i = 0; i < arrayOfKeyLis.length - 1; i++){
            var li = $(arrayOfKeyLis[i]);
            var keyName = li.data("keyName");
            var newKeyName = li.find("input").val();
            if (keyName != newKeyName){
                dlg.model.parameters[newKeyName] = dlg.model.parameters[keyName];
                delete dlg.model.parameters[keyName];
                li.data("keyName", newKeyName);
            }
        }
        return err;
    }

    // for each value displayed for currently selected key, store the value to model
    function updateValuesModel(){
        var err = 0;
        if (isSelectedKeyInModel()){
            var arrayOfValueLis = dlg.html.parameterValuesList.find(".dlg-element-key-value-list-item");
            var values = [];
            for (var i = 0; i < arrayOfValueLis.length - 1; i++){
                var value = getParameterValue($(arrayOfValueLis[i]));
                if (value !== null){
                    values.push(value);
                }
                else{
                    err = 1;
                }
            }
            dlg.model.parameters[dlg.state.selectedKey] =  values;
        }
        return err;
    }

    function isSelectedKeyInModel(){
        for(var key in dlg.model.parameters){
            if (key === dlg.state.selectedKey) return true;
        }
        return false;
    }

    function getDependencies(){
        var selectedItems = [];
            var arrayOfLis = dlg.html.selectedDependeesList.find("li");
        for (var i = 0; i < arrayOfLis.length; i++){
            selectedItems.push($(arrayOfLis[i]).data("config").id);
        }
        return selectedItems;
    }

    function alertPostFailure(mode, textStatus, errorThrown){
        alert("Server error at template" + mode + ", text status:" + textStatus + " " + "errorThrown:" + errorThrown);
    }

    function alertWrongInput(item, msg){
        alert(msg);
        item.addClass("dlg-error-input");
    }

    function getParameterValue(li){
        var value = null;
        var valueInput = li.find("input.dlg-value-value");
        var paramVal = valueInput.val();
        if (paramVal === "" || paramVal === dlg.txt.addNewValue){
            alertWrongInput(valueInput, dlg.txt.err.emptyValue);
        }
        else value = paramVal;
        return value;
    }

    function findNewKeyInput(){
        return dlg.html.parameterKeysList.find(".dlg-element-new-key-button").parent().find("input");
    }

    function findNewValueInput(){
        return $(".dlg-element-new-value-button").parent().find("input");
    }

    function validateInput(element){
        var res = true;
        if (isInt(element["id"])) {
            res = false;
            console.log("dlg.txt.err.internalError");
        }
        else if (isInt(element.type)){
            res = false;
            alertWrongInput(dlg.html.elem.type, dlg.txt.err.mustBeInteger);
        }
        else if (typeof element.name !== 'string'){
            res = false;
            alertWrongInput(dlg.html.elem.name, dlg.txt.err.mustBeString);
        }
        else if (typeof element.version !== 'string' && typeof element.version !== 'number'){
            res = false;
            alertWrongInput(dlg.html.elem.version, dlg.txt.err.mustBeString);
        }
        else if (typeof element.description !== 'string'){
            res = false;
            alertWrongInput(dlg.html.elem.description, dlg.txt.err.mustBeString);
        }
        else if (isInt(element.minMachines)){
            res = false;
            alertWrongInput(dlg.html.elem.minMachines, dlg.txt.err.mustBeInteger);

        }
        else if (isInt(element.maxMachines)){
            res = false;
            alertWrongInput(dlg.html.elem.maxMachines, dlg.txt.err.mustBeInteger);
        }
        /*
        else if (typeof element.replicated !== 'boolean'){
            res = false;
            alertWrongInput(dlg.html.elem.replicated, dlg.txt.err.mustBeBoolean);
        }
        */
        else if (isInt(element.minReplicationMachines)){
            res = false;
            alertWrongInput(dlg.html.elem.minReplicationMachines, dlg.txt.err.mustBeInteger);

        }
        else if (isInt(maxReplicationMachines)){
            res = false;
            alertWrongInput(dlg.html.elem.maxReplicationMachines, dlg.txt.err.mustBeInteger);
        }
        return res;
    }

    function isInt(obj){
        return (isNaN(obj) || !(Math.round(obj) == obj)) ? false : true;
    }

})(jQuery);
