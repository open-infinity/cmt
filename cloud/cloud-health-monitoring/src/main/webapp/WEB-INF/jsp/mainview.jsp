<%@ page contentType="text/html"%>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<fmt:setBundle basename="health-monitoring"/>
<html>
<head>
</head>
<body>
	<portlet:resourceURL id='getNodeList' var="nodeListUrl"/>
	<portlet:resourceURL id='getHealthStatusResponse' var="healthStatusUrl"/>
	<portlet:resourceURL id="getMetricResponse" var="healthFormUrl"/>
	<portlet:resourceURL id="getHealthBoundariesResponse" var="healthBoundariesUrl"/>
	<portlet:resourceURL id="getGroupList" var="getGroupListUrl"/>
	<portlet:resourceURL id="getNotifications" var="getNotificationsUrl"/>
	<script type="text/javascript">
	$(function() {
		openInfinity.health.healthStatusUrl = '${healthStatusUrl}';
		openInfinity.health.nodeListUrl = '${nodeListUrl}';
		openInfinity.health.boundariesUrl = '${healthBoundariesUrl}';
		openInfinity.health.groupListUrl = '${getGroupListUrl}';
		openInfinity.health.notificationsUrl = '${getNotificationsUrl}';

 		openInfinity.health.nodeListChart = openInfinity.health.chart.ajaxChart("nodeListChartContainer", '${nodeListUrl}', "spline", undefined, openInfinity.health.chart.nodeListJSONParser, 
			{
				chart : {
 		            events: {
		                load : function() {}
		            }
				},
				plotOptions: {
					pie: {
			            cursor: 'pointer',
			            dataLabels: {
			            	enabled: false,
			            	color: '#000000',
			            	connectorColor: '#000000'
			            }
			         },
			         series: {
					     marker: {
					    	 enabled: false
					     }
			         }

				},
	            tooltip: {
	                formatter: function() {        
	                	if (this.point.name) {	                		
	                		return '<b>' + this.point.name + '</b>: ' + this.point.y + ' (' + Highcharts.numberFormat(this.percentage, 1) + ' %)';
	                	} else {
						          return '<b>'+ this.series.name +'</b><br/>'+
						      '<fmt:message key="health.mainview.chart.nodehealth.date" /> ' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/><fmt:message key="health.mainview.chart.nodehealth.value" /> '+ Highcharts.numberFormat(this.y, 0);
						} 
	                }
           		},
           		labels: {
        			items: [{
        				html: '<fmt:message key="health.mainview.nodeavailabilityratio" />',
        				style: {
        					left: '30px',
        					top: '30px',
        					color: 'black'
        				}
        			}]
        		},
				xAxis: {
			         type: 'datetime'
			    },
			    yAxis: {
					title: {
						text: ''
					},
					min: 0,
					allowDecimals: false
				},
				title: {
					text: '<fmt:message key="health.mainview.chart.nodelist.title" />'
				},
	           	series: [{
	           		id: "splineSeries",
	           		name: '<fmt:message key="health.mainview.activenodes" />',
	                type: 'spline',
	                showInLegend: false,
	                data: []
	           	}, {
	           		id: "pieSeries",
	                name: 'node percentage',
	                center: [80, 110],
	                size: 100,
	                type: 'pie',
	                data: [],
	                events: {
	                	click: function(event) {	                		
	                		openInfinity.health.showNodeNames(event.point.id);	                		
	                	}
	                }
	           	}]
			}
		);

		openInfinity.health.healthStatusChart = openInfinity.health.chart.ajaxChart("healthStatusChartContainer", null, {}, "spline", null, 
			{
			chart: {
				zoomType: 'x',
 				events: {
		            load: function() {}
		         }
			},
			legend: {
				labelFormatter: function() {
					return openInfinity.health.formatMetric(this.name);
				}
			},
			xAxis: {
		         type: 'datetime'
		      },
		      yAxis: {
		         title: {
		            text: ''
		         },
		         min: 0
		      },
				plotOptions: {
				      marker: {
			               lineWidth: 1
			            }
				},
				title: {
					text: '<fmt:message key="health.mainview.chart.nodehealth.title" />'
				},
				tooltip: {
			         formatter: function() {
			                   return '<b>'+ openInfinity.health.formatMetric(this.series.name) +'</b><br/>'+
			               '<fmt:message key="health.mainview.chart.nodehealth.date" /> ' + Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/><fmt:message key="health.mainview.chart.nodehealth.value" /> '+ Highcharts.numberFormat(this.y, 0);
			         }
			    },
	           	series: [{
	                name: '<fmt:message key="health.mainview.chart.nodehealth.selectmetric" />'
	             }]
			}
		);
		
		openInfinity.health.initPortlet();
	});
	</script>
	
	<div id="healthPortlet" style="position: relative;">
		<ul>
			<li><a href="#nodeListTab"><fmt:message key="health.mainview.tab.nodelist" /></a></li>
			<li><a href="#healthStatusTab"><fmt:message key="health.mainview.tab.healthstatus" /></a></li>
			<li><a href="#notificationsTab"><fmt:message key="health.mainview.tab.notifications" /></a></li>
		</ul>	
		<div id="nodeListTab" style="z-index: 1001">
			<div id="nodeListChartContainer" class="healthChart"></div>

			<div id="groupAccordion" class="healthAccordion">
				<h3><a href="#"><fmt:message key="health.mainview.accordion.general" /></a></h3>
				<div id="nodeListInfoContainer">
					<c:choose>
						<c:when test="${empty healthStatus}">
							<fmt:message key="health.mainview.activenodescount" /> <span id="activeNodesCount"><c:out value="${fn:length(hostList.activeNodes)}" /></span><br />
							<fmt:message key="health.mainview.inactivenodescount" /> <span id="inactiveNodesCount"><c:out value="${fn:length(hostList.inactiveNodes)}" /></span>
						</c:when>
						<c:otherwise>
							${healthStatus.name}<br/>
							${healthStatus.responseStatus}<br/>
							${healthStatus.values}<br/>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="error globalError"></div>
			<h2 id="activeNodeNamesTitle" style="display:none"><fmt:message key="health.mainview.nodeList.activeNodeNames" /></h2>
			<h2 id="inactiveNodeNamesTitle" style="display:none"><fmt:message key="health.mainview.nodeList.inactiveNodeNames" /></h2>
			<div id="nodeNamesDialog" style="display:none" title="">			
				<div id="nodeNamesDiv"></div>
			</div>			
		</div>
		<div id="healthStatusTab">
			<div id="healthStatusChartContainer" class="healthChart"></div>
			<div style="clear: both;">
				<form:form id="healthForm" commandName="request" action="${healthFormUrl}" method="get">
					<div id="healthSourceTypeDiv">
						<input type="radio" id="healthTypePerNode" name="sourceType" checked="checked" value="node"/><label for="healthTypePerNode">Per node</label>
						<input type="radio" id="healthTypePerGroup" name="sourceType" value="group"/><label for="healthTypePerGroup">Per group</label>
					</div>
					<div id="healthMerticSelectionDiv">
						<div class="vertical-aligned"> 
							<form:select path="sourceName" id="hostName">
								<c:forEach items="${hostList.activeNodes }" var="node">
									<form:option value="${node.nodeName }" label="${node.nodeName}"></form:option>
								</c:forEach>						
								<c:forEach items="${hostList.inactiveNodes }" var="node">
									<form:option value="${node.nodeName }" label="${node.nodeName}" disabled="true"></form:option>
								</c:forEach>
							</form:select>
						</div>
						<div class="vertical-aligned">
							<form:select path="metricType" items="${metricTypes.metricTypes}" id="metricType"/>
						</div>
						<div class="vertical-aligned">
							<form:select path="metricNames" items="${metricNames.metricNames}" id="metricName" multiple="multiple"/>
						</div>
					</div>
					<div id="healthMerticSelectionDiv">
						
					</div>
					<div id="healthPeriodSetupDiv">
						<input type="radio" id="healthPeriodHour" name="healthPeriod" checked="checked" value="hour"/><label for="healthPeriodHour"><fmt:message key="health.mainview.period.hour" /></label>
						<input type="radio" id="healthPeriodDay" name="healthPeriod" value="day"/><label for="healthPeriodDay"><fmt:message key="health.mainview.period.day" /></label>
						<input type="radio" id="healthPeriodCustom" name="healthPeriod" value="custom"/><label for="healthPeriodCustom"><fmt:message key="health.mainview.period.custom" /></label>
					</div>
					<div id="healthPeriodCustomDiv" style="display: none;">
						<fmt:message key="health.mainview.period.start" /> <input id="healthCustomStart" name="healthCustomStart">
						<fmt:message key="health.mainview.period.end" /> <input id="healthCustomEnd" name="healthCustomEnd"/>
					</div>
				</form:form>
			</div>
			<div class="error healthStatusTabError"></div>
			<div class="error globalError"></div>
		</div>			
		<div id="notificationsTab">
			<input type="hidden" name="lastUpdateTime" id="lastUpdateTime" value="0"/>			
			<div id="notificationsArea">
			</div>
		</div>
	</div>
	<%-- predefined error messages --%>
	<div style="display: none;">
		<input id="healthError1" value='<fmt:message key="health.mainview.error.rrdunavailable" />'/>
		<input id="healthError2" value='<fmt:message key="health.mainview.error.metricavailable" />'/>
		<input id="healthError3" value='<fmt:message key="health.mainview.error.nodeunavailable" />'/>
	</div>
	</body>
</html>