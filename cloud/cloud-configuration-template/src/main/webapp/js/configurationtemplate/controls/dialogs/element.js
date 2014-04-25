/**
 * TODO
 * -generalize item selection view [done]
 * -generalize item selection events [done]
 * -generalize item selection fetching data [done]
 * -validate modules [done]
 * -combo box for element types
 * -sliders for element machine size
 * -radio button for element with jQuery
 * -more information id dialog title (item name etc)
 * -UI sexyfication
 * -packages design [uploads?] and finalization

 * Server side
 * -optimize edits
 * -available items can not contain self [done]
 * -auth aspect
 */
(function($) {

    var app = window.app || {};
    var infoDlg = window.app.dialog.info;
    var dlg = window.app.dialog.element || {};
    dlg.mode =  null;
    dlg.html = {};
    dlg.html.idContainer = $($(".dlg-input-container", "#dlg-element-general-tab").first());
    dlg.html.self = $("#dlg-element");
    dlg.html.tabs = $("#dlg-element-tabs");
    dlg.html.dependees = $("#dlg-element-dependees-item-select");
    dlg.html.modules = $("#dlg-element-modules-item-select");
    dlg.html.id = $("#dlg-element-value-id");
    dlg.html.type = $("#dlg-element-value-type");
    dlg.html.name = $("#dlg-element-value-name");
    dlg.html.version = $("#dlg-element-value-version");
    dlg.html.description = $("#dlg-element-general-tab").find("textarea");
    dlg.html.minMachines = $("#dlg-element-value-min-machines");
    dlg.html.maxMachines = $("#dlg-element-value-max-machines");
    dlg.html.replicated = $("#dlg-element-replicated-radio");
    dlg.html.minReplicationMachines = $("#dlg-element-value-min-repl-machines");
    dlg.html.maxReplicationMachines = $("#dlg-element-value-max-repl-machines");

    $.extend(dlg, {

		create : function(){
		    $.when(
                $.ajax({
                    url: portletURL.url.element.getAllDependenciesURL,
                    dataType: "json"
                }),
                $.ajax({
                    url: portletURL.url.element.getAllModulesURL,
                    dataType: "json"
                }))
                .done(function(dataDependencies, dataModules){
                    dlg.html.dependees.itemselect("init", dataDependencies[0]);
                    dlg.html.modules.itemselect("init", dataModules[0]);
                    configureEventHandling();
                    })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching items for dialog");});
		    dlg.mode = "create";
			dlg.open();
		},

        edit : function(id){
            $.ajax({
                url: portletURL.url.element.getElementURL + "&elementId=" + id,
                dataType: "json"
                }).done(function(data) {
                    dlg.html.id.text(data.id);
                    dlg.html.type.val(data.type);
                    dlg.html.name.val(data.name);
                    dlg.html.version.val(data.version);
                    dlg.html.description.val(data.description);
                    dlg.html.minMachines.val(data.minMachines);
                    dlg.html.maxMachines.val(data.maxMachines);
                    if (data.replicated === true){
                        dlg.html.replicated.find("input").first().prop('checked', true);
                        dlg.html.replicated.find("input").last().prop('checked', false);
                        toggleReplicatedMachinesInput("true");
                    }
                    else{
                        dlg.html.replicated.find("input").first().prop('checked', false);
                        dlg.html.replicated.find("input").last().prop('checked', true);
                        toggleReplicatedMachinesInput("false");
                    }
                    dlg.html.minReplicationMachines.val(data.minReplicationMachines);
                    dlg.html.maxReplicationMachines.val(data.maxReplicationMachines);
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching element");
            });
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
                        dlg.html.dependees.itemselect("init", dataDependencies[0]);
                        dlg.html.modules.itemselect("init", dataModules[0]);
                        configureEventHandling();
                        })
                    .fail(function(jqXHR, textStatus, errorThrown) {
                        console.log("Error fetching items for dialog");});
            dlg.mode = "edit";
            dlg.open(id);
        },

        open : function(){
			if (dlg.mode !== "create" && dlg.mode != "edit") {
				console.log("Invalid dialog mode: " + dlg.mode);
				return;
			}
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
            dlg.html.replicated.find("input").last().prop('checked', true);
            toggleReplicatedMachinesInput("false");

            // open dialog
            dlg.html.self.dialog("option", "title", title);
            dlg.html.tabs.tabs();
            dlg.html.tabs.tabs('select', 0);
            dlg.html.self.show();
            dlg.html.self.dialog("open");
        },

        close : function(){
            cleanUpDialog();
            dlg.html.self.dialog("close");
        }
    });


    // Cleanup

    function cleanUpDialog(){
        dlg.html.dependees.itemselect("destroy");
        dlg.html.modules.itemselect("destroy");
        dlg.html.id.text("");
        dlg.html.type.val("");
        dlg.html.version.val("");
        dlg.html.name.val("");
        dlg.html.description.val("");
        dlg.html.minMachines.val("");
        dlg.html.maxMachines.val("");
        dlg.html.replicated.find("input").first().prop('checked', false);
        dlg.html.replicated.find("input").last().prop('checked', true);
        dlg.html.minReplicationMachines.val("");
        dlg.html.maxReplicationMachines.val("");

        // clear error styles
        $.each(dlg.html.self.find("input"), function(index, value){
            clearStyleForErrorInput(index, $(value));
        });
    }

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
        var dependees = dlg.html.dependees.itemselect("getVal");
        var modules = dlg.html.modules.itemselect("getVal");
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
        bindInputClicksAndKeys();
        infoDlg.bind();
        bindRadioChange();
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
        dlg.html.replicated.find("input").on('change', function(){
            var replicated = dlg.html.replicated.find('input[name=dlg-element-replicated-radio]:checked').val();
            toggleReplicatedMachinesInput(replicated);
        });
    }

    function toggleReplicatedMachinesInput(replicated){
        if (replicated === "false"){
          dlg.html.minReplicationMachines.parent().hide();
          dlg.html.maxReplicationMachines.parent().hide();
        }
        else{
          dlg.html.minReplicationMachines.parent().show();
          dlg.html.maxReplicationMachines.parent().show();
        }
    }
    function clearStyleForErrorInput(index, item){
        if (item.hasClass("dlg-error-input")){
            item.removeClass("dlg-error-input");
        }
    }
    // Utility functions

    function isPosInt(obj){
        return (obj !== "" && typeof obj !== 'undefined' && !isNaN(obj) && (Math.round(obj) == obj) && obj > 0) ? true : false;
    }

    function isPosIntOrZero(obj){
            return (obj !== "" && typeof obj !== 'undefined' && !isNaN(obj) && (Math.round(obj) == obj) && obj >= 0) ? true : false;
        }

    function getElement(){
        var e = {};
        e.id = (dlg.mode == "edit") ? parseInt(dlg.html.id.text(), 10) : -1;
        e.type = dlg.html.type.val();
        e.name = dlg.html.name.val();
        e.version = dlg.html.version.val();
        e.description = dlg.html.description.val();
        e.minMachines = dlg.html.minMachines.val();
        e.maxMachines = dlg.html.maxMachines.val();
        e.replicated = dlg.html.replicated.find('input[name=dlg-element-replicated-radio]:checked').val();
        e.minReplicationMachines = dlg.html.minReplicationMachines.val();
        e.maxReplicationMachines = dlg.html.maxReplicationMachines.val();
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

        if (!isPosInt(element.id) && dlg.mode == "edit") {
            res = false;
            console.log("err.internalError");
        }
        else if (!isPosIntOrZero(element.type)){
            res = false;
            alertWrongInput(dlg.html.type, err.mustBePositiveInteger);
        }
        else if (element.name === ""){
            res = false;
            alertWrongInput(dlg.html.name, err.emptyItem);
        }
        else if (element.version === ""){
            res = false;
            alertWrongInput(dlg.html.version, err.emptyItem);
        }
        else if (!isPosInt(element.minMachines)){
            res = false;
            alertWrongInput(dlg.html.minMachines, err.mustBePositiveInteger);
        }
        else if (!isPosInt(element.maxMachines)){
            res = false;
            alertWrongInput(dlg.html.maxMachines, err.mustBePositiveInteger);
        }
        else if (element.minMachines > element.maxMachines){
            res = false;
            alertWrongInput(dlg.html.maxMachines, err.invalidMachineRange);
        }
        else if (element.replicated !== 'false' && element.replicated !== 'true'){
            res = false;
            alertWrongInput(dlg.html.replicated, err.mustBeBoolean);
        }
        else if (!isPosInt(element.minReplicationMachines) && element.replicated === 'true'){
            res = false;
            alertWrongInput(dlg.html.minReplicationMachines, err.mustBePositiveInteger);
        }
        else if (!isPosInt(element.maxReplicationMachines) && element.replicated === 'true'){
            res = false;
            alertWrongInput(dlg.html.maxReplicationMachines, err.mustBePositiveInteger);
        }
        else if (element.minReplicationMachines > element.maxReplicationMachines && element.replicated === 'true') {
            res = false;
            alertWrongInput(dlg.html.maxReplicationMachines, err.invalidMachineRange);
        }

        if (res === true){
            res = validateItems(dependees) && validateItems(modules);
        }

        return res;
    }

})(jQuery);
