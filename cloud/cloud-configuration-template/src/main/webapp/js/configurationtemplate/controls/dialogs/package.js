(function($) {

    //var app = window.app || {};
    var dlg = window.app.dialog.package || {};
    //var infoDlg = window.app.dialog.info;

    dlg.mode =  null;
    dlg.state = {};
    dlg.model = {};
    dlg.html = {};
    dlg.html.self = $("#dlg-package");

    $.extend(dlg, {

        create : function(){

        },

        edit : function(id){
            $.ajax({
                url: portletURL.url.package.getPackageURL + "&packageId=" + id,
                dataType: "json"
                }).done(function(data) {
                console.log(data);	
                }).fail(function(jqXHR, textStatus, errorThrown) {
                    console.log("Error fetching installation package");
            });

            dlg.open("edit");
        },

        open : function(mode){
            dlg.mode = mode;
            var title;
            if (mode == "edit"){
                //dlg.html.idContainer.show();
                title = "Edit package";
            }
            else if (mode == "create"){
                //dlg.html.idContainer.hide();
                title = "Create new package";
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
            dlg.html.self.dialog("close");
        }
    });

    // initialize dialog
    dlg.html.self.dialog({
        title: "Edit package",
        autoOpen: false,
        modal: true,
        width: 650,
        height: 560 ,
        buttons: {
            "Submit changes": function() {
            },
            Cancel: function() {
                dlg.close();
            }
        }
    });

    // Cleanup

    function cleanUpDialog(that){
        /*
        that.find(".dlg-item-list-container").find("ul").empty();
        dlg.html.keyName.text("");
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
        delete dlg.model.parameters;
        dlg.state.selectedKey = null;

        // clear error styles
        $.each(dlg.html.self.find("input"), function(index, value){
            clearStyleForErrorInput(index, $(value));
        });
        */
    }


    // Sending data to backend

    function submitElement(mode){
        var err = 0;
        //if (updateModel() === 0){

            // get input data
            var dependencies = getDependencies();
            var element = getElement();

            // validate input data
            if (!validateInput(element, dependencies, dlg.model.parameters)){
                return;
            }

            // serialize input data
            var outData = {};
            //outData.element = JSON.stringify(element);
            //outData.dependencies = JSON.stringify(dependencies);
            //outData.parameters = JSON.stringify(dlg.model.parameters);

            // send input data
            //console.log("Posting package parameters:" + outData.parameters);
            /*
            $.post((mode == "edit") ? portletURL.url.element.editElementURL : portletURL.url.element.createElementURL, outData)
            .done(function(){
                app.reloadElementsTable();
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                alertPostFailure(dlg.mode, textStatus, errorThrown);
                err = 2;
            });
            */
        //}
        /*)
        else{
            console.log("Invalid parameters, aborting submit");
            err = 1;
        }
        */
        return err;
    }

    // Events handling

    function configureEventHandling(){
        /*
        configureDragAndDrop();
        bindDependencyListItemClicks();
        bindKeyListItemClicks();
        bindNewItemInputClicks();
        bindDeleteKeysButtonsClick($(".dlg-element-list-item-delete-button", "#dlg-keys"));
        bindNewKeyButtonClick();
        bindInputClicksAndKeys();
        infoDlg.bind();
        bindRadioChange();
        */
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




})(jQuery);
