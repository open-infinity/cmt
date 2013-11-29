(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.element || {};

    var timer;

    dlg.mode =  null;

    dlg.state = {};
    dlg.state.selectedKey = null;

    dlg.data = {};
    dlg.data.parameters = {};
    dlg.data.element = {};
    dlg.data.element.id = $("#dlg-element-value-id");
    dlg.data.element.type = $("#dlg-element-value-type");
    dlg.data.element.name = $("#dlg-element-value-name");
    dlg.data.element.version = $("#dlg-element-value-version");
    dlg.data.element.description = $("#dlg-element-value-description");
    dlg.data.element.minMachines = $("#dlg-element-value-min-machines");
    dlg.data.element.maxMachines = $("#dlg-element-value-max-machines");
    dlg.data.element.replicated = $("#dlg-element-value-replicated");
    dlg.data.element.minReplicationMachines = $("#dlg-element-value-min-repl-machines");
    dlg.data.element.maxReplicationMachines = $("#dlg-element-value-max-repl-machines");

    dlg.txt = {};
    dlg.txt.addNewKey = "Add new key";
    dlg.txt.addNewValue = "Add new value";
    dlg.txt.addNewType = "Add new type";
    dlg.txt.alertNewKey = "New key name must not be empty";
    dlg.txt.alertNewValue = "New value name must not be empty";
    dlg.txt.alertNewType = "New type name must not be empty";
    dlg.txt.alertKeyAlreadyExists = "Key name already exists";

    dlg.html = {};
    dlg.html.idContainer = $($(".dlg-element-container", "#dlg-element-general-tab").first());
    dlg.html.self = $("#dlg-element");
    dlg.html.tabs = $("#dlg-element-tabs");
    dlg.html.selectedDependeesList = $("ul", "#dlg-element-selected-dependees");
    dlg.html.availableDependeesList = $("ul", "#dlg-element-available-dependees");
    dlg.html.parameterKeysList = $("ul", "#dlg-keys");
    dlg.html.parameterValuesList = $("ul", "#dlg-values");

    dlg.html.template = {};
    dlg.html.template.dependee = "<li class='ui-state-default'><div class='dlg-element-dependee-name'></div><div class='dlg-element-dependee-version'></div></li>";
    dlg.html.template.key = "<li class='ui-state-default key dlg-element-key-value-list-item'><div class='dlg-element-parameter-key-name'><input class='dlg-key-name' type='text'/></div><div class='dlg-element-list-item-button dlg-element-list-item-delete-button'>-</div></li>";
    //dlg.html.template.keyFocused = "<li class='ui-state-default key dlg-element-key-value-list-item'><div class='dlg-element-parameter-key-name'><input class='focus dlg-key-name' type='text'/></div><div class='dlg-element-list-item-button dlg-element-list-item-delete-button'>-</div></li>";
    dlg.html.template.value = "<li class='ui-state-default dlg-element-key-value-list-item'><div><div class='dlg-element-parameter-value-type'><input class='dlg-value-type' type='text'/></div><div class='dlg-element-parameter-value-value'><input class='dlg-value-value' type='text'/></div></div><div class='dlg-element-list-item-button dlg-element-list-item-delete-button'>-</div></li>";

    $.extend(dlg, {

        create : function(){
        },

        remove : function(id){
            console.log("remove with argument id:" + id);
        },

        edit : function(id){
            var jqxhrTemplate = $.ajax({
                url: portletURL.url.element.getElementURL + "&elementId=" + id,
                dataType: "json"
                }).done(function(data) {
                    dlg.data.element.id.text(data.id);
                    dlg.data.element.type.val(data.type);
                    dlg.data.element.name.val(data.name);
                    dlg.data.element.version.val(data.version);
                    dlg.data.element.description.val(data.description);
                    dlg.data.element.minMachines.val(data.minMachines);
                    dlg.data.element.maxMachines.val(data.maxMachines);
                    dlg.data.element.replicated.val(data.replicated);
                    dlg.data.element.minReplicationMachines.val(data.minReplicationMachines);
                    dlg.data.element.maxReplicationMachines.val(data.maxReplicationMachines);
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
                dlg.data.parameters = dataKeyValues[0];
                populateDependencies(dataDependencies[0]);
                populateKeyValues(dataKeyValues[0]);
                configureEventHandling();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
                });

            dlg.open("edit");
            //$(dlg.html.parameterKeysList.find("input").first()).focus();
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
                $(this).hidealertNewKey();
                $(this).dialog( "close" );
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
        bindDeleteButtonsClick();
        bindNewKeyButtonClick();
        bindNewValueButtonClick();
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

    function bindKeyListItemClicks(){
        $("input", ".key", "#dlg-keys").bind( "click",  function(){
            //clearInterval(timer);
            setKeySelected($(this).val());
        });
    }

    function bindNewItemInputClicks(){
        $(".dlg-element-new-key-button").parent().find("input").bind( "click", function(){
            var val =  $(this).val();
            if (val == "" || val == dlg.txt.addNewKey){
                $(this).val("").css("color", "black");
            }
        });

        $(".dlg-element-new-value-button").parent().find("input").bind( "click", function(){
            var val =  $(this).val();
            if (val == "" || val == dlg.txt.addNewType || val == dlg.txt.addNewValue){
                $(this).val("").css("color", "black");
            }
        });
    }

    function bindDeleteButtonsClick(){
        $(".dlg-element-list-item-delete-button").bind( "click", function(){
            var button =  $(this);

            // delete for keys
            var input = button.parent().find("input");
            if (input.hasClass("dlg-key-name")){
                var list = input.parents("ul");
                $(this).parent().remove();
                var newlySelectedInput = list.find("input").first();
                newlySelectedInput.focus();
                setKeySelected(newlySelectedInput.val());
            }
            // delete for values
            else{
                $(this).parent().remove();
            }
        });
    }

    function bindNewKeyButtonClick(){
        $(".dlg-element-new-key-button").bind( "click", function(){
            var keyInput = $(this).parent("li").find("input");
            var key = keyInput.val();
            if (key == "" || key == dlg.txt.addNewKey){
                alertWrongInput(keyInput, dlg.txt.alertNewKey);
                return;
            }

            // store locally the new key
            var exists = false;
            $.each(dlg.data.parameters, function(parameterKey, parameterValues){
                if (parameterKey == key){
                    alertWrongInput(keyInput, dlg.txt.alertKeyAlreadyExists);
                    exists = true;
                    return;
                }
                else{
                    dlg.data.parameters[key] = [];
                }
            });

            if (exists == true) return;

            // create and remove items
            $(this).parent("li").remove();
            dlg.html.parameterValuesList.empty();
            storeKeyToDom(dlg.html.template.key, key, dlg.html.parameterKeysList);
            storeKeyToDom(dlg.html.template.key, null, dlg.html.parameterKeysList);
            storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList);

            // re-bind events
            unbindKeyHandlers();
            bindKeyListItemClicks();
            bindNewItemInputClicks();
            bindDeleteButtonsClick();
            bindNewKeyButtonClick();
        });
    }

    function bindNewValueButtonClick(){
        $(".dlg-element-new-value-button", "#dlg-values").bind( "click", function(){
            var valueInput = $(this).parent("li").find("input.dlg-value-value");
            var paramVal = valueInput.val();
                if (paramVal == "" || paramVal == dlg.txt.addNewValue){
                    alertWrongInput(valueInput, dlg.txt.alertNewValue);
                    return;
                }
            var typeInput = $(this).parent("li").find("input.dlg-value-type");
            var paramType = typeInput.val();
                if (paramType == "" || paramType == dlg.txt.addNewType){
                    alertWrongInput(typeInput, dlg.txt.addNewType);
                    return;
                }
            var value = {
                type : paramType,
                value : paramVal
            }

            // locally store new value and assign it to key
            if (dlg.state.selectedKey == null){
                alert("Internal error");
                return;
            }
            else{
                var values = dlg.data.parameters[dlg.state.selectedKey];
                values.push(value);
            }

            // create and remove items
            $(this).parent("li").remove();
            storeValueToDom(dlg.html.template.value, value, dlg.html.parameterValuesList);
            storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList);

            // re-bind events
            unbindValueHandlers();
            bindNewItemInputClicks();
            bindDeleteButtonsClick();
            bindNewValueButtonClick();
        });
    }

    function unbindKeyHandlers(){
        $("input", ".key", "#dlg-keys").unbind();
        $(".dlg-element-new-key-button").parent().find("input").unbind();
        $(".dlg-element-new-value-button").parent().find("input").unbind();
        $(".dlg-element-list-item-delete-button").unbind();
        $(".dlg-element-new-key-button").unbind();
    }

    function unbindValueHandlers(){
        $(".dlg-element-new-key-button").parent().find("input").unbind();
        $(".dlg-element-new-value-button").parent().find("input").unbind();
        $(".dlg-element-list-item-delete-button").unbind();
        $(".dlg-element-new-value-button", "#dlg-values").unbind();
    }

    function setKeySelected(key){

            // clear values for previously selected key
            dlg.html.parameterValuesList.empty();

            // find values for key and put them to dom
            $.each(dlg.data.parameters, function(parameterKey, parameterValues){
                if (parameterKey == key){
                    $.each(parameterValues, function(index, value){
                        storeValueToDom(dlg.html.template.value, value, dlg.html.parameterValuesList);
                    });
                    storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList);
                }
            });

            // store locally currently selected key
            dlg.state.selectedKey = key;
        }


    function populateDependencies(data){
        try{
            var selectedIndices = [];

            $.each(data.selected, function(index, value){
               storeDependeesToDom(dlg.html.template.dependee, value, dlg.html.selectedDependeesList);
               selectedIndices.push(value.organizationId);
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

    function populateKeyValues(data){
        try{
            var count = 0;
            $.each(data, function(parameterKey, parameterValues){

                // Show values only for key #1, which will be selected by default.
                if (count++ == 0){
                    var htmlKey = storeKeyToDom(dlg.html.template.key, parameterKey, dlg.html.parameterKeysList);

                    // find key in list and focus on it
                    //setTimeout(function(){htmlKey.find("input").focus()},2000);
                    timer = setInterval(function(){
                        var input = htmlKey.find("input");
                        input.focus();
                        if (input.hasClass("focus")){
                           clearInterval(timer);
                        }
                    }, 300);

                    // display values for key
                    dlg.state.selectedKey = parameterKey;
                    $.each(parameterValues, function(index, value){
                        storeValueToDom(dlg.html.template.value, value, dlg.html.parameterValuesList);
                    });
                    storeValueToDom(dlg.html.template.value, null, dlg.html.parameterValuesList);
                }
                else{
                    storeKeyToDom(dlg.html.template.key, parameterKey, dlg.html.parameterKeysList);
                }
            });
            storeKeyToDom(dlg.html.template.key, null, dlg.html.parameterKeysList);
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
            if (value != null){
                lastChild.find(".dlg-key-name").val(value);
                lastChild.data("config", value);
            }
            else {
                lastChild.find(".dlg-key-name").val(dlg.txt.addNewKey).css("color", "silver");
                lastChild.find(".dlg-element-list-item-button").text("+").addClass("dlg-element-new-key-button").removeClass("dlg-element-list-item-delete-button").removeClass("key");
            }
            return lastChild;
    }

    function storeValueToDom(htmlTemplate, value, list){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            if (value != null){
                lastChild.find(".dlg-value-type").val(value.type);
                lastChild.find(".dlg-value-value").val(value.value);
                lastChild.data("config", value);

            }
            else{
                // create a new value item
                lastChild.find(".dlg-value-type").val(dlg.txt.addNewType).css("color", "silver");
                lastChild.find(".dlg-value-value").val(dlg.txt.addNewValue).css("color", "silver");
                lastChild.find(".dlg-element-list-item-button").text("+").addClass("dlg-element-new-value-button").removeClass("dlg-element-list-item-delete-button");
            }
            lastChild.toggleClass("ui-state-highlight");
    }

    function storeNewItemToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        list.find("li:last-child").text(value);
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
        dlg.data.element.id.text("");
        dlg.data.element.type.val("");
        dlg.data.element.name.val("");
        dlg.data.element.description.val("");
        dlg.data.element.minMachines.val("");
        dlg.data.element.maxMachines.val("");
        dlg.data.element.replicated.val("");
        dlg.data.element.minReplicationMachines.val("");
        dlg.data.element.maxReplicationMachines.val("");
    }

    function cleanUpTable(that){
        that.find("tr").remove();
    }

    function submitElement(mode){
        var outData = {};

        dlg.data.element.id.text(data.id);
        dlg.data.element.type.val(data.type);
        dlg.data.element.name.val(data.name);
        dlg.data.element.description.val(data.description);
        dlg.data.element.minMachines.val(data.minMachines);
        dlg.data.element.maxMachines.val(data.maxMachines);
        dlg.data.element.replicated.val(data.replicated);
        dlg.data.element.minReplicationMachines.val(data.minReplicationMachines);
        dlg.data.element.maxReplicationMachines.val(data.maxReplicationMachines);

        outData["id"] = parseInt(dlg.data.element.id.text());
        outData["type"] = dlg.data.element.type.val();
        outData["name"] = dlg.data.element.name.val();
        outData["description"] = dlg.data.element.description.val();
        outData["minMachines"] = dlg.data.element.minMachines.val();
        outData["maxMachines"] = dlg.data.element.maxMachines.val();
        outData["replicated"] = dlg.data.element.replicated.val();
        outData["minReplicationMachines"] = dlg.data.element.minReplicationMachines.val();
        outData["maxReplicationMachines"] = dlg.data.element.maxReplicationMachines.val();

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

    function alertWrongInput(item, msg){
        alert(msg);
        item.css("border-color", "red");
    }

})(jQuery);
