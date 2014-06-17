(function($) {

    var app = window.app || {};
    var dlg = window.app.dialog.template || {};
    var infoDlg = window.app.dialog.info;

    dlg.mode =  null;

    dlg.html = {};
    dlg.html.idContainer = $(".dlg-edit-template-template-label-container").first();
    dlg.html.self = $("#dlg-edit-template");
    dlg.html.tabs = $("#dlg-edit-template-tabs");
    dlg.html.template = {};
    dlg.html.template.id = $("#dlg-edit-template-id-value");
    dlg.html.template.name = $("#dlg-edit-template-name + input");
    dlg.html.template.description = $("#template-description + textarea");
    dlg.html.organizations = $("#dlg-template-organizations-item-select");
    dlg.html.elements = $("#dlg-template-elements-item-select");
    dlg.itemselectOptions = {
        colTitles:['Name', 'OrgId'],
        colModel:[{name:'name', width:120, align:"left"},
                  {name:'organizationId', width:43, align:"right"}],
        colModelKey:'organizationId'
    };

    $.extend(dlg, {
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
                dlg.html.elements.itemselect("init", dataElements[0]);
                dlg.html.organizations.itemselect("init", dataOrganizations[0], dlg.itemselectOptions);
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
                    dlg.html.template.id.text(data.id);
                    dlg.html.template.name.val(data.name);
                    dlg.html.template.description.val(data.description);
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
                dlg.html.elements.itemselect("init", dataElements[0]);
                dlg.html.organizations.itemselect("init", dataOrganizations[0], dlg.itemselectOptions);
                infoDlg.bind();
                })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.log("Error fetching items for dialog");
                });

            dlg.open("edit");
        },

        open : function(mode){
            dlg.mode = mode;
            var title = "";
            if (mode == "edit"){
                dlg.html.idContainer.show();
                title = "Edit template";
            }
            else if (mode == "create"){
                title = "Create new template";
                dlg.html.idContainer.hide();
            }
            else{
                console.log("Unexpected mode for dialog.");
                return;
            }
            dlg.html.self.dialog("option", "title", title);
            dlg.html.tabs.tabs({active: 1});
            dlg.html.self.show();
            dlg.html.self.dialog("open");
        }
    });

    // Initialize dialogs

    dlg.html.self.dialog({
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
                $(this).dialog( "close" );
            },
            Cancel: function() {
                cleanUpDialog($(this));
                $(this).dialog( "close" );
            }
        }
    });

    function cleanUpDialog(){
        dlg.html.elements.itemselect("destroy");
        dlg.html.organizations.itemselect("destroy");
        dlg.html.template.id.text("");
        dlg.html.template.name.val("");
        dlg.html.template.description.val("");
    }

    function submitTemplate(mode){
        var outData = {};
        var template = getTemplate();
        var elements = dlg.html.elements.itemselect("getVal");
        var organizations = dlg.html.organizations.itemselect("getVal");
        if (!validateInput(template, elements, organizations)){
            return;
        }
        outData.template = JSON.stringify(template);
        outData.elements = JSON.stringify(elements);
        outData.organizations = JSON.stringify(organizations);

        $.post((mode == "edit") ? portletURL.url.template.editTemplateURL : portletURL.url.template.createTemplateURL, outData)
        .done(function(){
            app.reloadTemplatesTable();
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            alertPostFailure(dlg.mode, textStatus, errorThrown);
        });
    }

    function alertPostFailure(mode, textStatus, errorThrown){
        alert("Server error at template" + mode + ", text status:" + textStatus + " " + "errorThrown:" + errorThrown);
    }

    function validateInput(template, elements, organizations){
            var res = true;

            if (!isPosInt(template.id) && dlg.mode == "edit") {
                res = false;
                console.log(err.internalError);
            }
            else if (template.name === ""){
                res = false;
                alertWrongInput(dlg.html.template.name, err.emptyItem);
            }

            return res;
    }


    // Utility functions

    function getTemplate(){
        var e = {};
        e.id = (dlg.mode == "edit") ? parseInt(dlg.html.template.id.text(), 10) : -1;
        e.name = dlg.html.template.name.val();
        e.description = dlg.html.template.description.val();
        return e;
    }

})(jQuery);
