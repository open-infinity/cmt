(function($) {

    var dlg = window.app.dialog.package || {};
    var infoDlg = window.app.dialog.info;
    
    dlg.state = {};
    
    dlg.html = {};
    dlg.html.idContainer = $($(".dlg-input-container", "#dlg-package-general-tab").first());
    dlg.html.pkg = {};
    dlg.html.pkg.id = $("#dlg-package-value-id");
    dlg.html.pkg.name = $("#dlg-package-value-name");
    dlg.html.pkg.version = $("#dlg-package-value-version");
    dlg.html.pkg.description = $("#dlg-package-value-description");
    dlg.html.tabs = $("#dlg-package-tabs");
    dlg.html.self = $("#dlg-package");

    $.extend(dlg, {

        create : function(){
            dlg.mode = "create";
            dlg.open();
        },

        edit : function(id){
            $.ajax({
                url: portletURL.url.package.getPackageURL + "&packageId=" + id,
                dataType: "json"
                }).done(function(data) {
                	console.log(data);	
                	dlg.html.pkg.id.text(data.id);
                    dlg.html.pkg.name.val(data.name);
                    dlg.html.pkg.version.val(data.version);
                    dlg.html.pkg.description.val(data.description);
                    configureEventHandling();
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching installation pkg");
            });
            dlg.mode = "edit";
            dlg.open();
        },

        open : function(){
            var title;
            if (dlg.mode == "edit"){
                title = "Edit package";
                dlg.html.idContainer.show();

            }
            else if (dlg.mode == "create"){
                title = "Create new package";
                dlg.html.idContainer.hide();
            }
            else{
                console.log("Unexpected mode for dialog.");
                return;
            }

            // open dialog
            dlg.html.tabs.tabs();
            dlg.html.tabs.tabs('select', 0);
            dlg.html.self.dialog("option", "title", title);
            dlg.html.self.dialog("open");
        },

        close : function(){
        	cleanUpDialog(dlg.html.self);
            dlg.html.self.dialog("close");

        }
    });

    // initialize dialog
    dlg.html.self.dialog({
        title: "Edit Installation package",
        autoOpen: false,
        modal: true,
        width: 650,
        height: 560 ,
        buttons: {
            "Submit changes": function() {
                if (submitPackage(dlg.mode) === 0){
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
    	dlg.html.pkg.id.text("");
        dlg.html.pkg.name.val("");
        dlg.html.pkg.version.val("");
        dlg.html.pkg.description.val("");
        
        // clear error styles
        $.each(dlg.html.self.find("input"), function(index, value){
            clearStyleForErrorInput(index, $(value));
        });
    }


    // Sending data to backend

    function submitPackage(mode){
        var err = 0;                  
        // get input data
        var pkg = getPackage();

        // validate input data
        if (!validateInput(pkg)){
            return 1;
        }

        // serialize input data
        var outData = {};
        outData.package = JSON.stringify(pkg);

        // send input data
        console.log("Posting package:" + outData.package);
        
        $.post((mode == "edit") ? portletURL.url.package.editPackageURL : portletURL.url.package.createPackageURL, outData)
        .done(function(){
            app.reloadPackagesTable();
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            alertPostFailure(dlg.mode, textStatus, errorThrown);
            err = 2;
        });
        return err;
    }

    // Events handling

    function configureEventHandling(){

        // click and key press on pkg general input tags
        //bindInputClicksAndKeys();

        bindGeneralAttributeInputClicks();
        // itemSelect events
        //itemSelectConfigureDragAndDrop();

        // parameter key-value events
        //bindParameterKeyClicks();
        //bindParameterNewKeyInputClicks();
        //bindParameterDeleteKeyClicks($(".dlg-pkg-list-item-delete-button", "#dlg-keys"));
        //bindParameterNewKeyClicks();

        // TODO: ouch, this is not nice ->refactor
        // Double clicks for mini info dialog
        infoDlg.bind();
    }

/*
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

    */

    function bindGeneralAttributeInputClicks(){
        $("input","#dlg-pkg-general-tab").bind( "click",  function(){
            clearStyleForErrorInput(0, $(this));
        });
    }
  /*
    function bindParameterKeyClicks(){
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

    function bindParameterNewKeyInputClicks(){
        bindParameterInputClick(findNewKeyInput());
        bindParameterInputClick(findNewValueInput());
    }

    function bindParameterInputClick(items){
        items.bind("click", function(){
            var val =  $(this).val();
            if (val === "" || val == msg.addNewKey || val == msg.addNewValue ){
                $(this).val("").css("color", "black");
            }
            clearStyleForErrorInput(0, $(this));
        });
    }

    function bindInputClicksAndKeys(){
        var inputs = $("input", "#dlg-pkg-fields");
        inputs.bind("click", function(){
            clearStyleForErrorInput(0, $(this));
        });
        inputs.keypress(function(event) {
            clearStyleForErrorInput(0, $(this));
        });
    }


    function bindParameterDeleteClicks(items){
        bindParameterDeleteKeyClicks(items);
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

    function bindParameterDeleteKeyClicks(items){
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

    function bindParameterNewKeyClicks(){
        $(".dlg-pkg-new-key-button").bind( "click", function(){

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
                    dlg.model.parameters[key] = [];
                }
            }

            // if model is empty, store key to model
            else{
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
            bindParameterKeyClicks();
            bindParameterNewKeyInputClicks();
            bindParameterDeleteClicks($(".dlg-pkg-list-item-delete-button"));
            bindParameterNewKeyClicks();
        });
    }

    function bindParameterNewValueClick(items){
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
*/
    /*
    function unbindKeyHandlers(){
        $("input", ".key", "#dlg-keys").unbind();
        findNewKeyInput().unbind();
        findNewValueInput().unbind();
        $(".dlg-pkg-list-item-delete-button").unbind();
        $(".dlg-pkg-new-key-button").unbind();
    }

    function unbindValueHandlers(){
        findNewKeyInput().unbind();
        findNewValueInput().unbind();
        $(".dlg-pkg-list-item-delete-button").unbind();
        $(".dlg-pkg-new-value-button", "#dlg-values").unbind();
    }
    */

    // TODO: use generalized showViewSelectedKey() instead this function
    /*
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

    function storeKeyToDom(htmlTemplate, value, list){
        list.append(htmlTemplate);
        var lastChild = list.find("li:last-child");
        if (value !== null){
            lastChild.find(".dlg-key-name").val(value);
            lastChild.data("keyName", value);
        }
        else {
            lastChild.find(".dlg-key-name").val(msg.addNewKey).css("color", "silver");
            lastChild.find(".dlg-pkg-list-item-button").text("+").addClass("dlg-pkg-new-key-button").removeClass("dlg-pkg-list-item-delete-button").removeClass("key");
        }
        return lastChild;
    }

    function storeValueToDom(htmlTemplate, paramValue, list, index){
            list.append(htmlTemplate);
            var lastChild = list.find("li:last-child");
            if (paramValue !== null && index!== null){
                lastChild.find(".dlg-value-value").val(paramValue);
                bindDeleteValuesButtonsClick(lastChild.find(".dlg-pkg-list-item-delete-button"));
                lastChild.data("index", index);
            }
            else{
                // create a new value item
                lastChild.find(".dlg-value-value").val(msg.addNewValue).css("color", "silver");
                var addButton = lastChild.find(".dlg-pkg-list-item-button");
                addButton.text("+").addClass("dlg-pkg-new-value-button").removeClass("dlg-pkg-list-item-delete-button");

                // bind events for new value item
                bindParameterInputClick(lastChild.find("input"));
                bindParameterNewValueClick(addButton);

                lastChild.data("index", -1);
            }
    }
    */

    // Style handling functions

    function clearStyleForErrorInput(index, item){
        if (item.hasClass("dlg-error-input")){
            item.removeClass("dlg-error-input");
        }
    }

/*
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
        var arrayOfKeyLis = dlg.html.parameterKeysList.find(".dlg-pkg-key-value-list-item");
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
            var arrayOfValueLis = dlg.html.parameterValuesList.find(".dlg-pkg-key-value-list-item");
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
*/

/*
    function isSelectedKeyInModel(){
        for(var key in dlg.model.parameters){
            if (key === dlg.state.selectedKey) return true;
        }
        return false;
    }
*/

    function getPackage(){
        var e = {};
        e.id = (dlg.mode == "edit") ? parseInt(dlg.html.pkg.id.text(), 10) : -1;
        e.name = dlg.html.pkg.name.val();
        e.version = dlg.html.pkg.version.val();
        e.description = dlg.html.pkg.description.val();
        return e;
    }
/*
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
        return dlg.html.parameterKeysList.find(".dlg-pkg-new-key-button").parent().find("input");
    }

    function findNewValueInput(){
        return $(".dlg-pkg-new-value-button").parent().find("input");
    }
*/
    // Alerts

    function alertPostFailure(mode, textStatus, errorThrown){
        alert("Server error at template" + mode + ", text status:" + textStatus + " " + "errorThrown:" + errorThrown);
    }

    function alertWrongInput(item, msg){
        alert(msg);
        item.addClass("dlg-error-input");

        // open the tab with erroneous item and focus on it
        var inPkgTab = (item.parents("#dlg-pkg-general-tab")).length > 0;
        if (inPkgTab){
            dlg.html.tabs.tabs('select', 0);
        }
        else{
            dlg.html.tabs.tabs('select', 2);
        }

        item.focus();
    }

    // Validation

    function validateInput(pkg){
        var res = true;

        // validate pkg
        if (!isPosInt(pkg.id) && dlg.mode == "edit") {
            res = false;
            console.log("err.internalError");
        }
        else if (pkg.name === ""){
            res = false;
            alertWrongInput(dlg.html.pkg.name, err.emptyItem);
        }
        else if (pkg.version === ""){
            res = false;
            alertWrongInput(dlg.html.pkg.version, err.emptyItem);
        }
        
        return res;
    }


})(jQuery);
