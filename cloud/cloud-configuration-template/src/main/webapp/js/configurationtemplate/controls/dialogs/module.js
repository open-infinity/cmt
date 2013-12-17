(function($) {

    var dlg = window.app.dialog.module || {};
    //var infoDlg = window.app.dialog.info;
    
    dlg.mode =  null;
    
    dlg.model = {};
    dlg.model.parameters = null;
    
    dlg.state = {};
    dlg.state.selectedKey = null;
    
    dlg.html = {};
    dlg.html.module = {};
    dlg.html.module.id = $("#dlg-module-value-id");
    dlg.html.module.type = $("#dlg-module-value-type");
    dlg.html.module.name = $("#dlg-module-value-name");
    dlg.html.module.version = $("#dlg-module-value-version");
    dlg.html.module.description = $("#dlg-module-value-description");
    
    dlg.html.self = $("#dlg-module");

    $.extend(dlg, {

        create : function(){

        },

        edit : function(id){
            $.ajax({
                url: portletURL.url.module.getModuleURL + "&moduleId=" + id,
                dataType: "json"
                }).done(function(data) {
                	console.log(data);	
                	dlg.html.module.id.text(data.id);
                    dlg.html.module.type.val(data.type);
                    dlg.html.module.name.val(data.name);
                    dlg.html.module.version.val(data.version);
                    dlg.html.module.description.val(data.description);
                    
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching installation module");
            });
            $.when(

                    $.ajax({
                        url: portletURL.url.module.getPackagesForModuleURL + "&moduleId=" + id,
                        dataType: "json"
                    }),
                    $.ajax({
                        url: portletURL.url.module.getParameterKeysAndValuesURL + "&moduleId=" + id,
                        dataType: "json"
                    }))
                .done(function(dataPackages, dataKeyValues){
                    dlg.keyCount = 0;
                    dlg.model.parameters = dataKeyValues[0];
                    console.log("received parameters:" + dlg.model.parameters);
                    populatePackages(dataPackages[0]);
                    showViewSelectedKeyInitial(dataKeyValues[0]);
                    configureEventHandling();
                    })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching items for dialog");
                    });
            dlg.mode = "edit";
            dlg.open();
        },

        open : function(){
            var title;
            if (dlg.mode == "edit"){
                //dlg.html.idContainer.show();
                title = "Edit module";
            }
            else if (dlg.mode == "create"){
                //dlg.html.idContainer.hide();
                title = "Create new module";
            }
            else{
                console.log("Unexpected mode for dialog.");
                return;
            }

            // open dialog
            dlg.html.self.dialog("option", "title", title);
            dlg.html.self.dialog("open");
        },

        close : function(){
            // cleanup dialog html elements
        	cleanUpDialog(dlg.html.self);
            // clear error styles

            // close jQuery dialog
            dlg.html.self.dialog("close");

        }
    });

    // initialize dialog
    dlg.html.self.dialog({
        title: "Edit module",
        autoOpen: false,
        modal: true,
        width: 650,
        height: 560 ,
        buttons: {
            "Submit changes": function() {
                if (submitModule(dlg.mode) === 0){
                    dlg.close();
                }
            },
            Cancel: function() {
                dlg.close();
            }
        }
    });

    // Cleanup

    function cleanUpDialog(that){
    	that.find(".dlg-item-list-container").find("ul").empty();
    	dlg.html.module.id.text("");
        dlg.html.module.type.val("");
        dlg.html.module.name.val("");
        dlg.html.module.version.val("");
        dlg.html.module.description.val("");
        delete dlg.model.parameters;
        dlg.state.selectedKey = null;
        
        // clear error styles
        $.each(dlg.html.self.find("input"), function(index, value){
            clearStyleForErrorInput(index, $(value));
        });
    }


    // Sending data to backend

    function submitModule(mode){
        var err = 0;                  
        if (updateModel() === 0){

            // get input data
            var module = getModule();
            var packages = getPackages();

            // validate input data
            if (!validateInput(module, packages, dlg.model.parameters)){
                return;
            }

            // serialize input data
            var outData = {};
            outData.module = JSON.stringify(module);
            outData.packages = JSON.stringify(packages);
            outData.parameters = JSON.stringify(dlg.model.parameters);

            // send input data
            console.log("Posting module parameters:" + outData.parameters);
            
            $.post((mode == "edit") ? portletURL.url.module.editModuleURL : portletURL.url.module.createModuleURL, outData)
            .done(function(){
                app.reloadModulesTable();
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                alertPostFailure(dlg.mode, textStatus, errorThrown);
                err = 2;
            });
        } else{
            console.log("Invalid parameters, aborting submit");
            err = 1;
        }
        return err;
    }

    // Events handling

    function configureEventHandling(){

        //itemSelect events
        itemSelectConfigureDragAndDrop();

        // Parameter key-value events
        bindKeyListItemClicks();
        bindNewItemInputClicks();
        bindDeleteKeysButtonsClick($(".dlg-module-list-item-delete-button", "#dlg-keys"));


        /*

        bindDeleteKeysButtonsClick($(".dlg-element-list-item-delete-button", "#dlg-keys"));
        bindNewKeyButtonClick();
        bindInputClicksAndKeys();
        infoDlg.bind();
        bindRadioChange();
        */
    }
    /*  -> in widget
    function configureDragAndDrop(){
    */
    
    function populatePackages(data){
        try{
            var selectedIndices = [];

            $.each(data.selected, function(index, value){
               storeDependeesToDom(tpl.dependee, value, dlg.html.selectedDependeesList);
               selectedIndices.push(value.id);
            });

            $.each(data.available, function(index, value){
               if (selectedIndices.indexOf(value.id) == -1){
                   storeDependeesToDom(tpl.dependee, value, dlg.html.availableDependeesList);
               }
            });
        }
        catch(err){
            console.log(err.message);
        }
    }



        function bindGeneralAttributeInputClicks(){
            $("input","#dlg-module-general-tab").bind( "click",  function(){
                clearStyleForErrorInput(0, $(this));
            });
        }

        function bindKeyListItemClicks(){
            $("input", ".key", "#dlg-keys").bind( "click",  function(){
                clearStyleForErrorInput(0, $(this));
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
            bindKeyValueInputClicks(findNewKeyInput());
            bindKeyValueInputClicks(findNewValueInput());
        }

        function bindKeyValueInputClicks(items){
            items.bind("click", function(){
                var val =  $(this).val();
                if (val === "" || val == msg.addNewKey || val == msg.addNewValue ){
                    $(this).val("").css("color", "black");
                }
                clearStyleForErrorInput(0, $(this));
            });
        }

        function bindInputClicksAndKeys(){
            var inputs = $("input", "#dlg-module-fields");
            inputs.bind("click", function(){
                clearStyleForErrorInput(0, $(this));
            });
            inputs.keypress(function(event) {
                clearStyleForErrorInput(0, $(this));
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
            $(".dlg-module-new-key-button").bind( "click", function(){

                // fetch the new key
                var keyInput = $(this).parent("li").find("input");
                var key = keyInput.val();
                if (key === "" || key == msg.addNewKey){
                    alertWrongInput(keyInput, err.emptyKey);
                    return;
                }

                dlg.state.selectedKey = key;

                // store the new key into model

                // check if the key already exists in the model, and add the key if it does not already exist
                var exists = false;
                if (!$.isEmptyObject(dlg.model.parameters)){
                    $.each(dlg.model.parameters, function(parameterKey, parameterValues){
                        if (parameterKey == key){
                            alertWrongInput(keyInput, err.keyAlreadyExists);
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
                storeKeyToDom(tpl.key, key, dlg.html.parameterKeysList).find("input").focus();
                storeKeyToDom(tpl.key, null, dlg.html.parameterKeysList);
                storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);

                // re-bind events
                unbindKeyHandlers();
                bindKeyListItemClicks();
                bindNewItemInputClicks();
                bindDeleteButtonsClick($(".dlg-module-list-item-delete-button"));
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
                    alertWrongInput(findNewKeyInput(), err.keyFirst);
                    return;
                }
                else{
                    var values = dlg.model.parameters[dlg.state.selectedKey];
                    values.push(value);
                }

                // create and remove items
                $(this).parent("li").remove();
                storeValueToDom(tpl.value, value, dlg.html.parameterValuesList, dlg.model.parameters[dlg.state.selectedKey].length -1);
                storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);
            });
        }
        function bindRadioChange(){
            dlg.html.elem.replicated.find("input").on('change', function(){
                var replicated = dlg.html.elem.replicated.find('input[name=dlg-module-replicated-radio]:checked').val();
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
            findNewKeyInput().unbind();
            findNewValueInput().unbind();
            $(".dlg-module-list-item-delete-button").unbind();
            $(".dlg-module-new-key-button").unbind();
        }

        function unbindValueHandlers(){
            findNewKshowViewSelectedKeyInitialeyInput().unbind();
            findNewValueInput().unbind();
            $(".dlg-module-list-item-delete-button").unbind();
            $(".dlg-module-new-value-button", "#dlg-values").unbind();
        }

        // View management functions

        function populateDependencies(data){
            try{
                var selectedIndices = [];

                $.each(data.selected, function(index, value){
                   storeDependeesToDom(tpl.dependee, value, dlg.html.selectedDependeesList);
                   selectedIndices.push(value.id);
                });

                $.each(data.available, function(index, value){
                   if (selectedIndices.indexOf(value.id) == -1){
                       storeDependeesToDom(tpl.dependee, value, dlg.html.availableDependeesList);
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
                    var htmlKey = storeKeyToDom(tpl.key, parameterKey, dlg.html.parameterKeysList);
                    // display values for key
                    dlg.state.selectedKey = parameterKey;
                    $.each(parameterValues, function(index, parameterValue){
                        storeValueToDom(tpl.value, parameterValue, dlg.html.parameterValuesList, index);
                    });
                    dlg.html.keyName.text(parameterKey);
                }
                else{
                    storeKeyToDom(tpl.key, parameterKey, dlg.html.parameterKeysList);
                }
            });

            // Store "new item" rows to DOM
            storeKeyToDom(tpl.key, null, dlg.html.parameterKeysList);
            storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);
        }

        function showViewSelectedKey(key){

            // clear ValuesView for previously selected key
            dlg.html.parameterValuesList.empty();

            // update ValuesView with find values for the selected key
            var found = false;
            $.each(dlg.model.parameters, function(parameterKey, parameterValues){
                if (parameterKey === key.val() && typeof parameterValues !== 'undefined'){
                    $.each(parameterValues, function(index, value){
                        storeValueToDom(tpl.value, value, dlg.html.parameterValuesList, index);
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
            storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);

            // store state
            dlg.state.selectedKey = key.val();
        }

        function storeDependeesToDom(htmlTemplate, value, list){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            lastChild.find(".dlg-module-dependee-name").text(value.name.substring(0, 17));
            lastChild.find(".dlg-module-dependee-version").text(value.version.substring(0, 5));
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
                    lastChild.find(".dlg-key-name").val(msg.addNewKey).css("color", "silver");
                    lastChild.find(".dlg-module-list-item-button").text("+").addClass("dlg-module-new-key-button").removeClass("dlg-module-list-item-delete-button").removeClass("key");
                }
                return lastChild;
        }

        function storeValueToDom(htmlTemplate, paramValue, list, index){
                list.append(htmlTemplate);
                var lastChild = list.find("li:last-child");
                if (paramValue !== null && index!== null){
                    lastChild.find(".dlg-value-value").val(paramValue);
                    bindDeleteValuesButtonsClick(lastChild.find(".dlg-module-list-item-delete-button"));
                    lastChild.data("index", index);
                }
                else{
                    // create a new value item
                    lastChild.find(".dlg-value-value").val(msg.addNewValue).css("color", "silver");
                    var addButton = lastChild.find(".dlg-module-list-item-button");
                    addButton.text("+").addClass("dlg-module-new-value-button").removeClass("dlg-module-list-item-delete-button");

                    // bind events for new value item
                    bindKeyValueInputClicks(lastChild.find("input"));
                    bindNewValueButtonClick(addButton);

                    lastChild.data("index", -1);
                }
        }

        function storeNewItemToDom(htmlTemplate, value, list){
            list.append(htmlTemplate);
            list.find("li:last-child").text(value);
        }

        // Style handling functions

        function moveMultipleModules(list, selected) {
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

        // Model management functions

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
            var arrayOfKeyLis = dlg.html.parameterKeysList.find(".dlg-module-key-value-list-item");
            for(var i = 0; i < arrayOfKeyLis.length - 1; i++){
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
                var arrayOfValueLis = dlg.html.parameterValuesList.find(".dlg-module-key-value-list-item");
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

        // Utility functions

        function isPosInt(obj){
            return (obj !== "" && typeof obj !== 'undefined' && !isNaN(obj) && (Math.round(obj) == obj) && obj > 0) ? true : false;
        }

        function getDependencies(){
            var selectedItems = [];
                var arrayOfLis = dlg.html.selectedDependeesList.find("li");
            for (var i = 0; i < arrayOfLis.length; i++){
                selectedItems.push($(arrayOfLis[i]).data("config").id);
            }
            return selectedItems;
        }

        function getModule(){
            var e = {};
            e.id = (dlg.mode == "edit") ? parseInt(dlg.html.module.id.text(), 10) : -1;
            e.type = dlg.html.module.type.val();
            e.name = dlg.html.module.name.val();
            e.version = dlg.html.module.version.val();
            e.description = dlg.html.module.description.val();
            return e;
        }

        function getParameterValue(li){
            var value = null;
            var valueInput = li.find("input.dlg-value-value");
            var paramVal = valueInput.val();
            if (paramVal === "" || paramVal === msg.addNewValue){
                alertWrongInput(valueInput, err.emptyValue);
            }
            else value = paramVal;
            return value;
        }

        function findNewKeyInput(){
            return dlg.html.parameterKeysList.find(".dlg-module-new-key-button").parent().find("input");
        }

        function findNewValueInput(){
            return $(".dlg-module-new-value-button").parent().find("input");
        }

        // Alerts

        function alertPostFailure(mode, textStatus, errorThrown){
            alert("Server error at template" + mode + ", text status:" + textStatus + " " + "errorThrown:" + errorThrown);
        }

        function alertWrongInput(item, msg){
            alert(msg);
            item.addClass("dlg-error-input");

            // open the tab with erroneous item and focus on it
            var inModuleTab = (item.parents("#dlg-module-general-tab")).length > 0;
            if (inModuleTab){
                dlg.html.tabs.tabs('select', 0);
            }
            else{
                dlg.html.tabs.tabs('select', 2);
            }

            item.focus();
        }
    /*
    function bindDependencyListItemClicks(){
         $("li", "#dlg-dependency-selection-container").
            click(function(){
                $(this).toggleClass("ui-state-highlight");
         });
    }

    function bindGeneralAttributeInputClicks(){
        $("input","#dlg-element-general-tab").bind( "click",  function(){
            clearStyleForErrorInput(0, $(this));
        });
    }

    function bindKeyListItemClicks(){
        $("input", ".key", "#dlg-keys").bind( "click",  function(){
            clearStyleForErrorInput(0, $(this));
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
        bindKeyValueInputClicks(findNewKeyInput());
        bindKeyValueInputClicks(findNewValueInput());
    }

    function bindKeyValueInputClicks(items){
        items.bind("click", function(){
            var val =  $(this).val();
            if (val === "" || val == msg.addNewKey || val == msg.addNewValue ){
                $(this).val("").css("color", "black");
            }
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
            if (key === "" || key == msg.addNewKey){
                alertWrongInput(keyInput, err.emptyKey);
                return;
            }

            dlg.state.selectedKey = key;

            // store the new key into model

            // check if the key already exists in the model, and add the key if it does not already exist
            var exists = false;
            if (!$.isEmptyObject(dlg.model.parameters)){
                $.each(dlg.model.parameters, function(parameterKey, parameterValues){
                    if (parameterKey == key){
                        alertWrongInput(keyInput, err.keyAlreadyExists);
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
            storeKeyToDom(tpl.key, key, dlg.html.parameterKeysList).find("input").focus();
            storeKeyToDom(tpl.key, null, dlg.html.parameterKeysList);
            storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);

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
                alertWrongInput(findNewKeyInput(), err.keyFirst);
                return;
            }
            else{
                var values = dlg.model.parameters[dlg.state.selectedKey];
                values.push(value);
            }

            // create and remove items
            $(this).parent("li").remove();
            storeValueToDom(tpl.value, value, dlg.html.parameterValuesList, dlg.model.parameters[dlg.state.selectedKey].length -1);
            storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);
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
        findNewKeyInput().unbind();
        findNewValueInput().unbind();
        $(".dlg-element-list-item-delete-button").unbind();
        $(".dlg-element-new-key-button").unbind();
    }

    function unbindValueHandlers(){
        findNewKshowViewSelectedKeyInitialeyInput().unbind();
        findNewValueInput().unbind();
        $(".dlg-element-list-item-delete-button").unbind();
        $(".dlg-element-new-value-button", "#dlg-values").unbind();
    }

    // View management functions

    function populateDependencies(data){
        try{
            var selectedIndices = [];

            $.each(data.selected, function(index, value){
               storeDependeesToDom(tpl.dependee, value, dlg.html.selectedDependeesList);
               selectedIndices.push(value.id);
            });

            $.each(data.available, function(index, value){
               if (selectedIndices.indexOf(value.id) == -1){
                   storeDependeesToDom(tpl.dependee, value, dlg.html.availableDependeesList);
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
                var htmlKey = storeKeyToDom(tpl.key, parameterKey, dlg.html.parameterKeysList);
                // display values for key
                dlg.state.selectedKey = parameterKey;
                $.each(parameterValues, function(index, parameterValue){
                    storeValueToDom(tpl.value, parameterValue, dlg.html.parameterValuesList, index);
                });
                dlg.html.keyName.text(parameterKey);
            }
            else{
                storeKeyToDom(tpl.key, parameterKey, dlg.html.parameterKeysList);
            }
        });

        // Store "new item" rows to DOM
        storeKeyToDom(tpl.key, null, dlg.html.parameterKeysList);
        storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);
    }

    function showViewSelectedKey(key){

        // clear ValuesView for previously selected key
        dlg.html.parameterValuesList.empty();

        // update ValuesView with find values for the selected key
        var found = false;
        $.each(dlg.model.parameters, function(parameterKey, parameterValues){
            if (parameterKey === key.val() && typeof parameterValues !== 'undefined'){
                $.each(parameterValues, function(index, value){
                    storeValueToDom(tpl.value, value, dlg.html.parameterValuesList, index);
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
        storeValueToDom(tpl.value, null, dlg.html.parameterValuesList, null);

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
                lastChild.find(".dlg-key-name").val(msg.addNewKey).css("color", "silver");
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
                lastChild.find(".dlg-value-value").val(msg.addNewValue).css("color", "silver");
                var addButton = lastChild.find(".dlg-element-list-item-button");
                addButton.text("+").addClass("dlg-element-new-value-button").removeClass("dlg-element-list-item-delete-button");

                // bind events for new value item
                bindKeyValueInputClicks(lastChild.find("input"));
                bindNewValueButtonClick(addButton);

                lastChild.data("index", -1);
            }
    }

    function storeNewItemToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        list.find("li:last-child").text(value);
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

    // Model management functions

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
        for(var i = 0; i < arrayOfKeyLis.length - 1; i++){
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

    // Utility functions

    function isPosInt(obj){
        return (obj !== "" && typeof obj !== 'undefined' && !isNaN(obj) && (Math.round(obj) == obj) && obj > 0) ? true : false;
    }

    function getDependencies(){
        var selectedItems = [];
            var arrayOfLis = dlg.html.selectedDependeesList.find("li");
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

    function getParameterValue(li){
        var value = null;
        var valueInput = li.find("input.dlg-value-value");
        var paramVal = valueInput.val();
        if (paramVal === "" || paramVal === msg.addNewValue){
            alertWrongInput(valueInput, err.emptyValue);
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
    */
        
    // Validation

    function validateInput(module, packages, parameters){
        var res = true;

        // validate module
        if (!isPosInt(module.id) && dlg.mode == "edit") {
            res = false;
            console.log("err.internalError");
        }
        else if (!isPosInt(module.type)){
            res = false;
            alertWrongInput(dlg.html.module.type, err.mustBePositiveInteger);
        }
        else if (module.name === ""){
            res = false;
            alertWrongInput(dlg.html.module.name, err.emptyItem);
        }
        else if (module.version === ""){
            res = false;
            alertWrongInput(dlg.html.module.version, err.emptyItem);
        }
        
        if (res === true){
            res = validateItems(packages);
            // TODO validate parameters
        }

        return res;
    }


})(jQuery);
