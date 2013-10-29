(function($) {
	var portlet = window.portlet || {};
    console.log("initializing dialog.template");
	$.extend(portlet.dialog, {
		// Show cluster information dialog
		createTemplate: function () {

		},

		deleteTemplate: function (id) {

        },

        editTemplate: function (id) {
            console.log("argument id:" + id);
            # get template data

            # put template data into edit dialog

            # show dialog
            $("#dialog-template-edit").dialog("open");


            /*
            var urlData = portletURL.url.cluster.getClusterInfoURL + "&clusterId=" + id;

            //Clear cluster data
            $("#clusterdatatable tr:gt(0)").remove();

            $.getJSON(urlData, function(data) {
                    $.each(data, function(key,val) {
                        $('#clusterdatatable > tbody:last').append('<tr><td>' + key + '</td><td>' + val + '</td></tr>');
                    });
            });

            cloudadmin.dialog.updateClusterStatusTable(id);

            $("#clusterdatatable tr:even").addClass("odd");
            $("#clusterstatustable tr:even").addClass("odd");

            $("#clusterdialog").dialog("open");
            */
        }
	});

	// Initialize the dialogs
	$("#dialog-template-edit").dialog({
		autoOpen: false,
		modal: true,
		width: 475,
		height: 340
	});

    $("#dialog-template-create").dialog({
        autoOpen: false,
        modal: true,
        width: 475,
        height: 340
    });


})(jQuery);
