(function($) {
	console.log("initializing cloudadmin.dialog.machine");
	var cloudadmin = window.cloudadmin || {};
	
	$.extend(cloudadmin.dialog, {
			
		initMachineDialog: function(instanceId) {
			console.log("cloudadmin.dialog.initMachineDialog("+instanceId+") called.");
			$("#machines").jqGrid('GridUnload');
			
			// Tables (machine table)	
			$("#machines").jqGrid({
				url: portletURL.url.machine.machineListURL + "&instanceId=" + instanceId + "&rnd="+Math.random(),
				datatype: "json",
				jsonReader : {
						repeatitems : false,
						id: "Id",
						root : function(obj) { return obj.rows;},
						page : function(obj) {return obj.page;},
						total : function(obj) {return obj.total;},
						records : function(obj) {return obj.records;}
						},
				colNames:['Id', 'Instance Id', 'Name', 'Key', 'Dns name', 'Username', 'State'],
				colModel:[
				          {name:'id',index:'machine_id', width:30},
				          {name:'instanceId',index:'machine_instance_id', width:100},
				          {name:'name',index:'machine_name', width:100},
				          {name:'key',index:'machine_key',width:30},
				          {name:'dnsName',index:'machine_dns_name',width:240},
				          {name:'userName',index:'machine_username',width:80},
				          {name:'state',index:'machine_state',width:80}
				          ],
				rowNum:10,
				width: 700,
				height: 300,
				pager: '#machinepager',
				sortname: 'id',
				viewrecords: true,
				sortorder: 'desc',
				caption: "Machines",
				onSelectRow: cloudadmin.dialog.fetchMachineRowData
			});
			$("#machines").jqGrid('navGrid','#machinepager',{edit:false,add:false,del:false});
		},
		
		
		fetchMachineRowData: function(id, status) {
			var mId = jQuery("#machines").jqGrid('getCell', id, 0);
			var url = portletURL.url.machine.machineURL + "&id="+mId+"&rnd="+Math.random();
			var table = document.getElementById('machinedatatable');
			var rowCount = table.rows.length;
			
			for(var i = 0; i < rowCount; i++) {
				table.deleteRow(i);
				rowCount--;
				i--;
			}
			
			$.getJSON(url, function(data) {
				$.each(data, function(key,val) {
					var table2 = document.getElementById('machinedatatable');
					var rowCount2 = table2.rows.length;
					var row2 = table2.insertRow(rowCount2);
					var cell1 = row2.insertCell(0);
					var cell2 = row2.insertCell(1);
					cell1.innerHTML = key;
					cell2.innerHTML = val;
					
					if(key == 'id') {
						machineId = val;
					}
				});
			});
			
			$("#machineDialog").dialog("open");
		},
		
		// Refresh the machine-table
		refreshMachineTable: function () {
			 $("#machines").trigger("reloadGrid");
		}		
	});
	
	// Dialogs
	$("#machineListDialog").dialog({
		autoOpen: false,
		modal: true,			
		height : 500,
		width : 750
	});
		
	$("#machineDialog").dialog({
		autoOpen: false,
		modal: true,
		width : 400,
		height : 340
	});
	
	$("#machineDialog").dialog("option", "buttons", [
			                                          {
				                                          text : "Terminate machine",
				                                          click: function() {
			                                        	  	var outData = {};
			                          					
			                          						outData["id"] = mId;
			                          						$.post({
			                          							url: portletURL.url.machine.terminateMachineURL,
			                          							data: outData
			                          						});
			                          						$("#machines").trigger("reloadGrid");
			                          						$(this).dialog("close");
			                                               }
			                                          }
			                                          ]);

})(jQuery);
