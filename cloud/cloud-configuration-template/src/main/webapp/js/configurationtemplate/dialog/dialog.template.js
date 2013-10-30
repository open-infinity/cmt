(function($) {
	var template = window.app.dialog.template || {};
    template.elementsTable = $("edit-template-elements-grid");
	$.extend(template, {

		create: function () {
		},

		remove: function (id) {
            console.log("remove with argument id:" + id);
        },

        edit: function (id) {
            console.log("edit with argument id:" + id);
            /*
            template.tableEdit.jqGrid({
                url: portletURL.url.template.getTemplatesForUserURL,
                datatype: "json",
                jsonReader : {
                    repeatitems : false,
                    id: "Id",
                    root : function(obj) { return obj.rows;},
                    page : function(obj) {return obj.page;},
                    total : function(obj) {return obj.total;},
                    records : function(obj) {return obj.records;}
                    },
                colNames:['Id', 'Name', 'Description'],
                colModel:[
                          {name:'id', index:'id', width:50, align:"center"},
                          {name:'name', index:'name', width:150, align:"left"},
                          // 545
                          {name:'description', index:'description', width:535, align:"left"}
                          ],
                rowNum: 10,
                width: 750,
                height: 300,
                pager: '#template-grid-pager',
                sortname: 'id',
                viewrecords: true,
                shrinkToFit: false,
                sortorder: 'id'
            })
            */
            $("#dialog-template-edit").dialog("open");


           /*
             <div id="edit-template-dialog">
             	    <div id="edit-template-fields">
                         <p> Name <input type='text' value=''> </p>
                         <p> Description <input type='text' value=''> </p>
                         <p> ID <input type='text' value=''> </p>
             	    </div>
                     <table id="edit-template-elements-grid">elem</table>
                     <div id="edit-template-elements-pager">1</div>

                     <table id="edit-template-organizations-grid">org</table>
                     <div id="edit-template-organizations">2</div>
             	</div>

           */
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
