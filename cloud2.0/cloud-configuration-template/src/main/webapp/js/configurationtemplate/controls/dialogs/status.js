(function($) {

    var dlg = window.app.dialog.info || {};
    dlg.self = $("#dlg-status");

    $.extend(dlg, {
        setup: function(){
            dlg.self.dialog({
                 title: "Action status",
                 autoOpen: false,
                 modal: true,
                 width: "auto",
                 height: "auto",
                 draggable: false,
                 resizable: false,
                 buttons: {
                     Ok: function() {
                         $(this).dialog("close");
                     }
                 }
             });
        }
    });

    dlg.setup();

})(jQuery);
