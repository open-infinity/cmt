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
           $( "li", "#all-elements" ).draggable({
                revert: "invalid",
                containment: "document",
                helper: "clone",
                cursor: "move",
                scroll:true
            });

            $("#all-elements-container").droppable({
                accept: "#selected-elements > li",
                activeClass: "ui-state-highlight",
                drop: function( event, ui ) {
                    unSelect( ui.draggable );
                }
            });

            $("#selected-elements-container").droppable({
                accept: "#all-elements > li",
                activeClass: "ui-state-highlight",
                drop: function( event, ui ) {
                    select( ui.draggable );
                }
            });

            $( "li", "#selected-elements" ).draggable({
                revert: "invalid",
                containment: "document",
                helper: "clone",
                cursor: "move",
                scroll:true
            });

            function select(item){
                var sel = $("#selected-elements");
                item.appendTo(sel).fadeIn();
            }

            function unSelect(item){
                var unSel = $("#all-elements");
                item.appendTo(unSel).fadeIn();
            }

            /*
            $("#template-edit-organization-grid").jqGrid({
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
                colNames:[
                    'organizationId', 'companyId', 'parentOrganizationId', 'treePath', 'name',
                    'type_', 'recursable', 'regionId', 'countryId', 'statusId', 'comments'
                ],
                colModel:[
                    {name:'organizationId', index:'organizationId', width:50, align:"center"},
                    {name:'companyId', index:'companyId', width:50, align:"center"},
                    {name:'parentOrganizationId', index:'parentOrganizationId', width:50, align:"center"},
                    {name:'treePath', index:'treePath', width:50, align:"center"},
                    {name:'name', index:'name', width:50, align:"center"},
                    {name:'type_', index:'type_', width:50, align:"center"},
                    {name:'recursable', index:'recursable', width:50, align:"center"},
                    {name:'regionId', index:'regionId', width:50, align:"center"},
                    {name:'countryId', index:'countryId', width:50, align:"center"},
                    {name:'statusId', index:'statusId', width:50, align:"center"},
                    {name:'comments', index:'comments', width:50, align:"center"}
                ],
                rowNum: 10,
                width: 900,
                height: 300,
                pager: '#template-edit-organization-grid-pager',
                sortname: 'id',
                viewrecords: true,
                shrinkToFit: false,
                sortorder: 'id'
            });

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
                                                   0
            $("#clusterdialog").dialog("open");
            */
        }
	});

	// Initialize the dialogs

	$("#dialog-template-edit").dialog({
	    title: "Edit template",
		autoOpen: false,
		modal: true,
		width: 1000,
		height: 1000 ,
		buttons: {
            "Submit changes": function() {
              $(this).dialog( "close" );
              // post all
            },
            Cancel: function() {
              $(this).dialog( "close" );
            }
        }
	});



    $("#dialog-template-create").dialog({
  	    title: "Create template",
        autoOpen: false,
        modal: true,
        width: 700,
        height: 1000
    });


})(jQuery);
