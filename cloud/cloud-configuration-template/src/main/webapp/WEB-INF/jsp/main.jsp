<%--

/*
 * Copyright (c) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 */

@author Vedran Bartonicek
@version 1.3.0
@since 1.3.0

 --%>

<%@taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false"%>

<portlet:defineObjects/>
<portlet:actionURL var="action" />

<html>
<head>
    <META http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title>Cloud Template</title>
</head>
<body>	
    <div>Templates available for  
        <div id="user-name">Vedran</div> 
    </div>
    
    <div id="tab_title">TAB TITLE</div>
    <div id="tab_content">TAB CONTENT</div>
    
    <div id="tabs">
        <ul>
			<li><a href="#tabs-1">Templates</a></li>
			<!-- 
			<li><a href="#tabs-2">Template 1</a></li>
			<li><a href="#tabs-3">Template 2</a></li>
			-->	
        </ul>
	
		<div id="tabs-1">
			<table id="templates-grid"> </table>
			<div id="template-grid-pager"> </div>
		    <div class="ui-button-bar">
		        <button id="edit-template">Edit</button>
		        <button id="new-template">New</button>
		        <button id="delete-template">Delete</button>
	            <button id="assign-template">Assign</button>  
            </div>
    	</div>
		
		<!--  
		<div id="tabs-2">
		  Template slkdhf 2
		</div>
		
		<div id="tabs-3">
		  Template lkjh 3  
		</div>
		-->
	</div>

</body>
</html>

<jsp:include page="resource.jsp" />