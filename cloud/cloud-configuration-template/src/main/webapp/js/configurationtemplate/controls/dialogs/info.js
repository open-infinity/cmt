
//TODO: Make a standalone widget or integrate with itemselect widget

(function($) {
    var dlg = window.app.dialog.info || {};
    dlg.self = $("#dlg-info");

    $.extend(dlg, {
        setup: function(){
            dlg.self.dialog({
                 title: "Detailed information",
                 autoOpen: false,
                 modal: true,
                 width: "auto",
                 height: "auto",
                 draggable: false,
                 resizable: false,
                 buttons: {
                     Ok: function() {
                         cleanUpInfoDialogTable($(this));
                         $(this).dialog("close");
                     }
                 }
             });
        },
        bind : function(){
            bindInfoDblClick();
        }
    });

    function cleanUpInfoDialogTable(that){
        that.find("tr").remove();
    }

    function bindInfoDblClick(){
        $(".dlg-item-list-container").find("li").
        click(function(){
            $(this).toggleClass("ui-state-highlight");
        }).
        dblclick(function () {
            dlg.self.dialog("open");
            var configData = $(this).data("config");
            storeToTable(configData, $("#dlg-item-table"));
        });
    }

    function storeToTable(configData, table){
        for (var key in configData) {
            table.append("<tr>" + "<th style='text-align: left;'>" + key + "</th>" + "<td>" +  configData[key] +  "</td>" + "</tr>");
        }
    }

    dlg.setup();

})(jQuery);
