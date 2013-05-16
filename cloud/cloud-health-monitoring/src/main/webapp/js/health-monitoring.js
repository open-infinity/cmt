(function() {
	var acquireNs = function(namespace, opts) {
		if (typeof namespace == "object") {
			return namespace;
		}
		if (namespace && typeof namespace != "string") {
			throw TypeError("insufficient type specified " + String(namespace.constructor));
		}
		var ns = namespace || "";
		var cns = window;
		if (/^\s*$/.test(ns)) {
			return cns;
		}
		var nss = ns.split(".");
		for (var i = 0; i < nss.length; i++) {
			var nsName = nss[i];
			var ins = cns[nsName];
			if (typeof ins == "undefined") {
				ins = cns[nsName] = {};
			}
			cns = ins;
		}
		return jQuery.extend(true, cns, opts || {});
	};
	var infinityNs = acquireNs("openInfinity");
	infinityNs.acquireNs = acquireNs;
	infinityNs.acquireNs("openInfinity.health");
	
	$.ajaxSetup({
		complete : function(jqXHR, textStatus) {
			openInfinity.health.handleHealthDataUnavailable(jqXHR);
		},
		cache: false,
		traditional: true
	});
	Highcharts.setOptions({
		global : {
			useUTC : false
		}
	});
	
	
})();

openInfinity.acquireNs("openInfinity.health.chart").DCC = function(options) {
    var defaultConfig = {
        title: {
        	text : ''
        },
        tooltip: {
            formatter: function() {                   		
                    return '<b>'+ this.point.name +'</b>: '+ this.point.y;
            }
        },
        plotOptions: {
            column: {
            	cursor: 'pointer',
                dataLabels: {
                    enabled: true
                },
                events: {
                }
            },
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series: [{data:[]}]
    };
    $.extend(true, defaultConfig, options || {});
    return defaultConfig;
};

openInfinity.acquireNs("openInfinity.health.chart", {
	ajaxChart: function(container, url, params, type, dataParser, options) {
	    var chartData = $.extend(true, {
		    chart: {
	            renderTo: container,
	            type: type,
	            events: {
	                load : function() {
	                	openInfinity.health.chart.loadData(this, url, params, dataParser);
	                }
	            }
		    }
	    }, new openInfinity.health.chart.DCC(), options || {});
	    
	    return new Highcharts.Chart(chartData);
	},
	loadData: function(chart, url, params, dataParser, postAction) {
		$.getJSON(url, params, function(data) {
			var parsingResult = dataParser(data, chart.options.colors);
			openInfinity.health.chart.applyLoadedData(chart, url, params, parsingResult);
			if (postAction) {				
				postAction(chart);
			}
	    });
	},
	healthStatusDataPostAction: function(chart) {
		// load boundaries for selected metric
		openInfinity.health.chart.loadBoundaries(chart, openInfinity.health.boundariesUrl, {
			sourceName: $("#hostName").val() || "", 
			metricType: $("#metricType").val() || "", 
			metricNames: $("#metricName").val() || ""
		});			
	},
	clearOldData: function(chart) {
    	//remove old splines
		for (var i = chart.series.length; i-- > 0; ) {
    		chart.series[i].remove(false);
    	}
	},
	applyLoadedData: function(chart, url, params, parsingResult) {
		this.clearOldData(chart);
        $.each(parsingResult.series, function(i, v) {
        	var color = chart.options.colors[i%chart.options.colors.length];
        	var cSeries = chart.addSeries({id: v.id, name: v.name, marker: { radius: 1}, y: v.y, color: color}, false);
        	cSeries.id = v.name;
        	var cData = v.seriesData || v;
        	cSeries.setData(cData, false);
        });
        
        chart.redraw();
	},
	addData: function(chart, url, params, dataParser) {
		$.getJSON(url, params, function(data) {
			var parsingResult = dataParser(data, chart.options.colors);
	        $.each(parsingResult.series, function(i, v) {
	        	var cSeries = chart.series[i];
	        	if (cSeries) {
					for (var i=0; i<v.seriesData.length; i++) {
						if (cSeries.xData[cSeries.xData.length-1] < v.seriesData[i][0]) {
							cSeries.addPoint(v.seriesData[i], false, true);
						}
					}        	
	        	}
	        });
	        chart.redraw();
		});
	},
	loadListChart: function(chart, url, params, dataParser) {
		$.getJSON(url, params, function(data) {
			var parsingResult = dataParser(data, chart.options.colors);
			if (parsingResult.series.length > 0) {
				var y = parsingResult.series[0].seriesData[0].y;
				chart.series[1].setData(parsingResult.series[0].seriesData, false);
				// add two points on load to draw a spline
				openInfinity.health.chart.addNodeTrendPoint(chart, y, false);
				openInfinity.health.chart.addNodeTrendPoint(chart, y, false);
				chart.redraw();
			}
	    });		
	},
	updatePie: function(chart, url, params, dataParser) {
	    $.getJSON(url, params, function(data) {
	        var parsingResult = dataParser(data, chart.options.colors);
	        if (parsingResult.series[0]) {
	        	openInfinity.health.chart.addNodeTrendPoint(chart, parsingResult.series[0].seriesData[0].y, true);
	        	if (chart.series[1] && chart.series[1].data.length > 0) {
			        $.each(parsingResult.series[0].seriesData, function(i, v) {	        	
			        	chart.series[1].data[i].update(v.y);
			        });    		
	        	} else {
	        		openInfinity.health.chart.loadListChart(chart, url, params, parsingResult);
	        	}
	        } else {
	        	// assuming no node available
	        	openInfinity.health.chart.addNodeTrendPoint(chart, 0, true);
	        }	        
	    });
	},
	addNodeTrendPoint: function(chart, y, redraw) {
        var now = new Date().getTime();
        chart.xAxis[0].setExtremes(now - 1000*60*60, now+1000*60);
        chart.series[0].addPoint([now, y], redraw, false);		
	},
	nodeListJSONParser: function(data, colors) {	
		var series = [];
		var seriesData = [];		
		if (!data.activeNodes) {
			return {series: series};
		}
		var index = 0;
		seriesData.push({name: "Active Nodes", y: data.activeNodes.length, color: colors ? colors[index++] : undefined, id: 'activeNodeNames'});
		seriesData.push({name: "Inactive Nodes", y: data.inactiveNodes.length, color: colors ? colors[index++] : undefined, id: 'inactiveNodeNames'});
		series.push({seriesData: seriesData});
		
		openInfinity.health.activeNodeNames = new Array();
        $.each(data.activeNodes, function(index, value) {
        	openInfinity.health.activeNodeNames.push(value.nodeName);
        });
        openInfinity.health.inactiveNodeNames= new Array();
        $.each(data.inactiveNodes, function(index, value) {
        	openInfinity.health.inactiveNodeNames.push(value.nodeName);
        });
		
		return {series: series, status: data.responseStatus};
	},
	healthStatusJSONParser: function(data, colors) {
		var series = [];
		$.each(data.metrics, function(i, record) {
			if (record.values != undefined) {
				$.each(record.values, function(key, metric) {
					var seriesData = [];
					for (var i=0; i<metric.length; i++) {
						// NaN means no data, break spline
						var val = metric[i].value != "NaN" ? metric[i].value : null;
				        seriesData.push([metric[i].date, val]);
					}
					var sName = record.name + (key != 'value' ? (' ' + key) : '');
					// replace 'value' with 'values' due to back-end
					var sId = record.name + (key == 'value' ? '_values' : '_' + key);
					series.push({id: sId, name: sName, seriesData: seriesData});
				});
			}
		});
		return {series: series, status: data.responseStatus};	
	},
	clearOldBoundaries: function(chart) {
		while (this.presentBoundaries.length != 0) {
			chart.yAxis[0].removePlotLine(this.presentBoundaries.pop());
		}
	},
	loadBoundaries: function(chart, url, params) {
		this.clearOldBoundaries(chart);
		$.getJSON(url, params, function(data) {			
			var values = data.boundaries;
			if (values != undefined) {
				$.each(values, function(metricName, metricSubNames) {
					$.each(metricSubNames, function(metricSubName, boundaries) {
						var fullName = metricName + '.rrd_' + metricSubName;
						var serias = openInfinity.health.healthStatusChart.get(fullName);
						if (serias != null) {
							var color = serias != null ? serias.color : null;
							$.each(boundaries, function(boundaryType, boundaryValue) {
								var dashStyle = 'Dot'; // Warning style
								if ((/^Failure/).test(boundaryType)) {
									dashStyle = 'Dash'; // Failure style
								}
								var boundary = {
										id: fullName + '_' + boundaryType,
										width: 2,
										color: color,
										value: boundaryValue,
										dashStyle: dashStyle,
										zIndex: 10,
										label: {
											text: boundaryType,
											align: 'left',
											style: {
												color: color,
												fontSize: '0.8em'
											}
										}
								};
								openInfinity.health.healthStatusChart.yAxis[0].addPlotLine(boundary);
								openInfinity.health.chart.presentBoundaries.push(boundary.id);
							});
						}
					});
				});
			}
		});
	},
	presentBoundaries: []	
});

openInfinity.acquireNs("openInfinity.health", {
	lastUpdateTimeStamp: 1326979550000,
	updateDelta: 1000*60*15,
	healthStatusIntervalRef: undefined,
	nodeListIntervalRef: undefined,
	refreshInterval: 10*1000,
	sourceName: null,
	metricType: null,
	metricNames: null,
	activeNodeNames : new Array(),
	inactiveNodeNames : new Array(),
	
	getSourceType: function() {
		return $("input[name='sourceType']:checked").val();
	},
	getTimePeriod: function() {
		var start = null;
		var end = null;
		var period = $("input[name='healthPeriod']:checked").val(); 
		switch (period) {
		case "custom":
			$("#healthPeriodCustomDiv").show();
			end = $("#healthCustomEnd").datetimepicker('getDate').getTime();
			start = $("#healthCustomStart").datetimepicker('getDate').getTime();	
			break;
		case "hour":
			$("#healthPeriodCustomDiv").hide();
			end = new Date().getTime();
			start = end - 1000*60*60;
			break;
		case "day":
		default:
			$("#healthPeriodCustomDiv").hide();
			end = new Date().getTime();
			start = end - 1000*60*60*24;
			break;
		}
		return {start: start, end: end};
	},
	healthStatusChartLoad: function(ch) {
		clearInterval(this.healthStatusIntervalRef);
		clearInterval(this.nodeListIntervalRef);	
		var period = this.getTimePeriod();
		
		// load selected metric data for period
		openInfinity.health.chart.loadData(ch, openInfinity.health.healthStatusUrl, 
			{	
				startTimeStamp: period.start, 
				endTimeStamp: period.end, 
				sourceName: $("#hostName").val() || "", 
				sourceType: openInfinity.health.getSourceType() || "", 
				metricType: $("#metricType").val() || "", 
				metricNames: $("#metricName").val() || ""
			}, openInfinity.health.chart.healthStatusJSONParser, openInfinity.health.chart.healthStatusDataPostAction);
		
		// set metric data update interval
		this.healthStatusIntervalRef = setInterval(function() {
			var cDate = new Date().getTime() - 1000*20;
	    	openInfinity.health.chart.addData(ch, openInfinity.health.healthStatusUrl, 
				{
					startTimeStamp: cDate, 
					endTimeStamp: cDate, 
					sourceName: $("#hostName").val() || "",
					sourceType: openInfinity.health.getSourceType() || "", 
					metricType: $("#metricType").val() || "", 
					metricNames: $("#metricName").val() || ""
				}, openInfinity.health.chart.healthStatusJSONParser);
	    }, this.refreshInterval);
		
		// set nodes pie and selectbox update interval
		this.nodeListIntervalRef = setInterval(function() {		
			$("#hostName,#metricType,#metricName").unbind("change");
			var parser = function(data, colors) {				
				if (openInfinity.health.getSourceType() == 'node') {
					$("#hostName").children().map(function() {	    
					    var active = false;
						for (var i in data.activeNodes) {
					        if ($(this).val() == data.activeNodes[i].nodeName) {
					            active = true;
					            break;
					        }
					    }
					    if (active) {
					    	$(this).removeAttr('disabled');
					    } else {
					    	$(this).attr('disabled', true);
					    }
					});
				}
				if (data.activeNodes) {
					$("span#activeNodesCount").html(data.activeNodes.length);				
					$("span#inactiveNodesCount").html(data.inactiveNodes.length);
				}
				return openInfinity.health.chart.nodeListJSONParser(data, openInfinity.health.nodeListChart.options.colors);
			};
			openInfinity.health.chart.updatePie(openInfinity.health.nodeListChart, openInfinity.health.nodeListUrl, {}, parser);
			openInfinity.health.loadGroups();	
			$("#hostName,#metricType,#metricName").bind("change", openInfinity.health.onParamChange);
	    }, this.refreshInterval);
	},
	healthFormHandler: function(json, statusText, xhr, $form) {
		$("#hostName,#metricType,#metricName,input[name='sourceType']").unbind("change");
		// since metricName is multy-select, do not repopulate anything if only metricNames ware changed
		if (!openInfinity.health.onlyNamesChanged) {
			if (openInfinity.health.getSourceType() == 'node' && json.listResponse) {		
				openInfinity.health.populateSelectBox(json.listResponse.activeNodes, $("select#hostName"), openInfinity.health.sourceName, null, "nodeName");
				var j = json.listResponse.inactiveNodes;
				for(var i = 0; i < j.length; i++) {
					var t = j[i].nodeName;
					$("<option/>", {
						value: t, text: t, selected: t == openInfinity.health.sourceName, disabled: true
					}).appendTo($("select#hostName"));
				}
			}
			if (openInfinity.health.getSourceType() == 'group' && json.groupListResponse) {
				var host = $("select#hostName");
				$("option", host).remove();
				$.each(json.groupListResponse.groups, function(g, nodes) {
					$("<option/>", {
						value: g, text: g, selected: g == openInfinity.health.sourceName
					}).appendTo(host);
				});
			}
			if (json.typesResponse) {
				openInfinity.health.populateSelectBox(json.typesResponse.metricTypes, $("select#metricType"), openInfinity.health.metricType, openInfinity.health.formatMetric);
			}
			if (json.namesResponse) {
	        	openInfinity.health.populateSelectBox(json.namesResponse.metricNames, $("select#metricName"), openInfinity.health.metricNames, openInfinity.health.formatMetric);
	        	if ($("select#metricName option:selected").length == 0) {
	        		$("select#metricName option").first().attr('selected', 'selected');
	        	}
			} else {
				$("select#metricType").trigger("change");
			}
		}
//		if ($("#metricName").val()) {		
			openInfinity.health.healthStatusChartLoad(openInfinity.health.healthStatusChart);
//		}
		$("#hostName,#metricType,#metricName,input[name='sourceType']").bind("change", openInfinity.health.onParamChange);
	},
	populateSelectBox: function(list, selectBox, tempVar, formatter, field) {
		$("option", selectBox).remove();
		if (list != null)
		$.each(list, function(i, option) {
			var selected;
			if (typeof tempVar == "object") {
				selected = tempVar != null && $.inArray(option, tempVar) != -1;
			} else {
				selected = field ? (option[field] == tempVar) : (option == tempVar);
			}
			var value = field ? option[field] : option;
			var text = formatter ? formatter(value) : value;
			$("<option/>", {
				value: value, text: text, selected: selected
			}).appendTo(selectBox);
		});		
	},
	formatMetric: function(name) {
		var res = name.replace(/(_|-|\.rrd)/g, " ");
		return res.charAt(0).toUpperCase() + res.slice(1);
	},
	handleHealthDataUnavailable: function(jqXHR) {
		var st = $.parseJSON(jqXHR.responseText);
		var isFailed = true;
		var elem = $("#healthError"+st.responseStatus);
		switch (st.responseStatus) {
			case 0:
				$(".healthStatusTabError,.globalError").html("").hide();
				this.removeOverlay();
				isFailed = false;
				break;
			case 2:
			case 3:
			case 4:
				if (!$(".globalError").is(":visible")) {
					$(".healthStatusTabError").append(elem.val()).show();
				}
				break;
			case 1:
			default:
				$(".healthStatusTabError").html("").hide();
				this.addOverlay(elem.val());
				break;
		}
		return isFailed;
	},
	onParamChange : function(arg) {
		openInfinity.health.onlyNamesChanged = false;
		var elem = arg.currentTarget;
		if (elem.id == "metricName") {
			openInfinity.health.onlyNamesChanged = true;
		} else if (elem.name == 'sourceType') {
			$("option", $("select#hostName")).remove();
			$("option", $("select#metricType")).remove();
			$("option", $("select#metricName")).remove();
		}
		openInfinity.health.sourceName = $("#hostName").val();
		openInfinity.health.metricType = $("#metricType").val();
		openInfinity.health.metricNames = $("#metricName").val();
		$('#healthForm').submit();
	},
	onlyNamesChanged: false,
	loadGroups: function(event, ui) {
		$.getJSON(openInfinity.health.groupListUrl, function(data) {
			var i = $("#groupAccordion").accordion("option", "active");
			$("#groupAccordion h3:not(:first), #groupAccordion div:not(:first)").remove();
			$.each(data.groups, function(groupName, hosts) {
				var hostsStr = '';
				$.each(hosts, function(i, host) {
					hostsStr += '<li>'+host;
				});
				$("#groupAccordion").append('<h3><a href="#">Group: '+groupName+'</a></h3><div><ul>' + hostsStr + '</ul></div>');
			});
			$("#groupAccordion").accordion("destroy").accordion({
				active: i ? i : false,
				collapsible: true
			});
		});
	},
	addOverlay: function(message) {
		var jqdiv = $("#healthPortlet div.ui-tabs-panel:not(.ui-tabs-hide)");
		if (jqdiv.next("div.ui-widget-overlay.health-overlay").length == 0) {
			var error = $('<div class="noticeContainer"></div>').html(message).notice({type: 'alert'});
			var overlay = $('<div class="ui-widget-overlay health-overlay"></div>')
			.css("top", jqdiv.position().top)
			.css("left", jqdiv.position().left)
			.css("width", jqdiv.outerWidth())
			.css("height", jqdiv.outerHeight())
			.append(error);
			jqdiv.after(overlay);
		}
		$("#healthPortlet").tabs("option", "disabled", [0,1,2]);
	},
	removeOverlay: function() {
		var jqdiv = $("#healthPortlet div.ui-tabs-panel:not(.ui-tabs-hide)");
		jqdiv.next("div.ui-widget-overlay.health-overlay").remove();
		$("#healthPortlet").tabs("option", "disabled", []);
	},
	initPortlet: function() {             
		$("#healthPortlet").tabs({
/*			select: function(event, ui) {
				// do not wait up to 10 sec, reload chart immediately
				if (ui.panel.id =='healthStatusTab' && $("#metricName").val()) {
					openInfinity.health.healthStatusChartLoad(openInfinity.health.healthStatusChart);
				}
			}*/
		});
		$("#healthPeriodSetupDiv,#healthSourceTypeDiv").buttonset();
		$("#healthCustomStart,#healthCustomEnd").datetimepicker({ 
			showOn: 'both',
			maxDate: 0
		}).next('button').text('').button({icons:{primary : 'ui-icon-calendar'}}).css({"height": "2em", "width": "2.5em"});
		$("#healthCustomStart,#healthCustomEnd").datetimepicker("setDate", new Date());
		$("input[name='healthPeriod']").click(function() {                           
			openInfinity.health.healthStatusChartLoad(openInfinity.health.healthStatusChart);
		});
		$("#healthCustomStart,#healthCustomEnd").change(function() {
		 	openInfinity.health.healthStatusChartLoad(openInfinity.health.healthStatusChart);
		});
		
		var aFormOptions = {
			success: openInfinity.health.healthFormHandler,
			traditional: true
		};
		$('#healthForm').ajaxForm(aFormOptions);
		$("#hostName,#metricType,#metricName,input[name='sourceType']").unbind("change").bind("change", openInfinity.health.onParamChange);
		
		// 1st tab: monitoring nodes
		openInfinity.health.chart.loadListChart(openInfinity.health.nodeListChart, openInfinity.health.nodeListUrl, {}, openInfinity.health.chart.nodeListJSONParser);
		openInfinity.health.loadGroups();
		// init empty accordion for the first time before AJAX calls back
		$("#groupAccordion").accordion({
			active: false,
			collapsible: true
		});
		// 2nd tab: load health chart for the first metric available
		$("#hostName").trigger("change");
		// 3rd tab: start notifications interval
		setInterval(this.updateNotifications, 10000);
		$("#nodeNamesDialog").dialog({autoOpen : false,
				height : 400,
				width : 450,
				modal : true,
				buttons : {
					'OK' : function() {
						$("#nodeNamesDiv").html("");
						$(this).dialog("close");
					} 
						
				}
		});
	},
	updateNotifications: function() {
		$.getJSON(openInfinity.health.notificationsUrl, {lastUpdateTime: $('#lastUpdateTime').val()}, function(data) {
			if (data.responseStatus == 0) {
				var lastUpdate = $('#lastUpdateTime').val();
				for (var i=0; i< data.notifications.length;i++) {
					var notification = data.notifications[i];
					var date = new Date(notification.time);
					var formattedDate = Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', date);
					var notificationStr = "<p>" + formattedDate + "| <b class='" + notification.severity.toLowerCase() + "Text'>" + notification.severity + "</b> Host: " + notification.hostName + "<br/> Metric type: " + notification.plugin + ", Metric name: " + notification.type+ "-" + notification.typeInstance;
					if (notification.currentValue && notification.currentValue != null) {
						notificationStr += ",   Value: " + notification.currentValue;
					}
					notificationStr += "</p>";
					if (notification.message && notification.message != null) {
						notificationStr += "<p>" + notification.message + "</p>";
					}
					$("#notificationsArea").append(notificationStr);
					if (notification.fileModificationTime > lastUpdate) {
						lastUpdate = notification.fileModificationTime; 
					}
//					lastUpdate = notification.time;
				}
				$("#lastUpdateTime").val(lastUpdate);
			}
		});
	},
	showNodeNames: function(type) {
		var title = $("#"+type+"Title").html();
		$("#nodeNamesDialog").dialog("option", "title", title);
		$.each(openInfinity.health[type], function(index, value){
			$("#nodeNamesDiv").append("<p>" + value + "</p>");
		});
		$("#nodeNamesDialog").dialog("open");
	}
});
