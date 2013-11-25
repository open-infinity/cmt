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
    <%--
    <div>Templates available for
        <div id="user-name">Vedran</div> 
    </div>
    
    <div id="tab_title">TAB TITLE</div>
    <div id="tab_content">TAB CONTENT</div>
    --%>

    <div id="tabsContainer">
        <ul id ="tabsReferenceList" >
			<li><a href="#templatesTab">Templates</a></li>
			<li><a href="#elementsTab">Elements</a></li>
        </ul>

		<div id="templatesTab">
			<table id="templates-grid"> </table>
			<div id="template-grid-pager"> </div>
		    <div class="ui-button-bar">
		        <button id="edit-template">Edit</button>
		        <button id="new-template">New</button>
		        <button id="delete-template">Delete</button>
            </div>
    	</div>


    	<div id="elementsTab">
            <table id="elements-grid"> </table>
            <div id="element-grid-pager"> </div>
            <div class="ui-button-bar">
                <button id="edit-element">Edit</button>
                <button id="new-element">New</button>
                <button id="delete-element">Delete</button>
            </div>
        </div>

	</div>

	<div id="dlg-edit-template">

        <%-- Tabs --%>
	    <div id="dlg-edit-template-tabs">
	    <ul>
            <li><a href="#dlg-edit-template-template-tab">General</a></li>
            <li><a href="#dlg-edit-template-elements-tab">Elements</a></li>
            <li><a href="#dlg-edit-template-organizations-tab">Organizations</a></li>
        </ul>

        <%-- Template --%>
        <div id = "dlg-edit-template-template-tab">
            <div id="edit-template-fields" class="dlg-edit-template-item-container">
                <div class="dlg-edit-template-template-label-container">
                    <span>Template Id:</span>
                    <span id="dlg-edit-template-id-value"></span>
                </div>
                <div class="dlg-edit-template-template-label-container">
                    <div id="dlg-edit-template-name">Template name</div>
                    <input type='text' value=''>
                </div>
                <div class="dlg-edit-template-template-label-container">
                    <div id="template-description">Template description</div>
                    <textarea rows="8" cols="68"></textarea>
                 </div>
            </div>
        </div>

        <%-- Elements --%>
        <div id = "dlg-edit-template-elements-tab">
            <div id="elements-selection-container" class ="dlg-edit-template-item-container">
                <div class="dlg-edit-template-list-panel-container dlg-edit-template-item">
                    <div class="dlg-edit-template-list-panel-container-title">Selected elements</div>
                    <div class="dlg-edit-template-list-panel-container-columns">
                        <div class="list-item-column">Platform</div>
                        <div>Version</div>
                    </div>
                    <div id="dlg-edit-template-selected-elements" class="list-container">
                        <ul></ul>
                    </div>
                </div>
                <div class="dlg-edit-template-arrows-img dlg-edit-template-item"></div>
                <div class="dlg-edit-template-list-panel-container dlg-edit-template-item">
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
        <div id="dlg-edit-template-organizations-tab">
            <div id="organizations-selection-container" class="dlg-edit-template-item-container">
                <div class="dlg-edit-template-list-panel-container dlg-edit-template-item">
                    <div class="dlg-edit-template-list-panel-container-title">Selected organizations</div>
                    <div class="dlg-edit-template-list-panel-container-columns">
                        <div class="list-item-column">Id</div>
                        <div>Name</div>
                    </div>
                    <div id="dlg-edit-template-selected-organizations" class="list-container">
                        <ul></ul>
                    </div>
                </div>
                <div class="dlg-edit-template-arrows-img dlg-edit-template-item"></div>
                <div class="dlg-edit-template-list-panel-container dlg-edit-template-item">
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
	</div>

	<%-- Item info dialog --%>
    <div id="dlg-info">
        <table id="dlg-item-table"></table>
    </div>

</body>
</html>

<jsp:include page="resource.jsp" />
