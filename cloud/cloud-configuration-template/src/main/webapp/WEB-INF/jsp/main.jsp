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
            </div>
    	</div>

		<%--
		<div id="tabs-2">
		  Template slkdhf 2
		</div>

		<div id="tabs-3">
		  Template lkjh 3
		</div>
		--%>
	</div>

	<div id="dlg-edit-template">

        <%-- Template --%>
        <div id="edit-template-fields" class="dlg-edit-template-item">
                <div class="dlg-edit-template-item-title">Template information</div>
                <div class="dlg-edit-template-template-label">
                    <span>Id:</span>
                    <span id="template-id-value"></span>
                </div>
                <div id="template-name" class="dlg-edit-template-template-label">Name</div> <input type='text' value=''>
                <div id="template-description" class="dlg-edit-template-template-label">Description</div> <input id ="dlg-edit-template-description" type='text' value=''>
        </div>

        <%-- Elements --%>
        <div id ="elements-selection-container" class ="dlg-edit-template-item-selection-container dlg-edit-template-item">
            <div class = "dlg-edit-template-item-title">Template elements</div>
            <div>
            <div class="selected-list-panel-container dlg-edit-template-list-panel-container dlg-edit-template-item">
                <div class="dlg-edit-template-list-panel-container-title">Selected elements</div>
                <div class="dlg-edit-template-list-panel-container-columns">
                    <div class="list-item-column">Platform</div>
                    <div>Version</div>
                </div>
                <div class="list-container">
                    <ul></ul>
                </div>
            </div>
            <div class="available-list-panel-container dlg-edit-template-list-panel-container dlg-edit-template-item">
                <div class="dlg-edit-template-list-panel-container-title">Available elements</div>
                <div class="dlg-edit-template-list-panel-container-columns">
                    <div class="list-item-column">Platform</div>
                    <div>Version</div>
                </div>
                <div class="list-container">
                    <ul></ul>
                </div>
            </div>
            </div>
        </div>

        <%-- Organizations --%>
        <div id ="organizations-selection-container" class = "dlg-edit-template-item-selection-container dlg-edit-template-item">
            <div class = "dlg-edit-template-item-title">Template organizations</div>
                <div class="selected-list-panel-container dlg-edit-template-list-panel-container dlg-edit-template-item">
                    <div class="dlg-edit-template-list-panel-container-title">Selected organizations</div>
                    <div class="dlg-edit-template-list-panel-container-columns">
                        <div class="list-item-column">Id</div>
                        <div>Name</div>
                    </div>
                    <div class="list-container">
                        <ul></ul>
                    </div>
                </div>
                <div class="available-list-panel-container dlg-edit-template-list-panel-container dlg-edit-template-item">
                    <div class="dlg-edit-template-list-panel-container-title">Available organizations</div>
                    <div class="dlg-edit-template-list-panel-container-columns">
                        <div class="list-item-column">Id</div>
                        <div>Name</div>
                    </div>
                    <div class="list-container">
                        <ul></ul>
                    </div>
                </div>
            </div>
	</div>

</body>
</html>

<jsp:include page="resource.jsp" />
