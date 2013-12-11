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


    <%-- Main page --%>

    <div id="tabsContainer">
        <ul id ="tabsReferenceList" >
			<li><a href="#templatesTab">Templates</a></li>
			<li><a href="#elementsTab">Elements</a></li>
			<li><a href="#modulesTab">Installation modules</a></li>
			<li><a href="#packagesTab">Installation packages</a></li>
			<li><a href="#platformsTab">Platforms</a></li>
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

        <div id="modulesTab">
            <table id="modules-grid"> </table>
            <div id="modules-grid-pager"> </div>
            <div class="ui-button-bar">
                <button id="edit-modules">Edit</button>
                <button id="new-modules">New</button>
                <button id="delete-modules">Delete</button>
            </div>
        </div>

        <div id="packagesTab">
            <table id="packages-grid"> </table>
            <div id="packages-grid-pager"> </div>
            <div class="ui-button-bar">
                <button id="edit-packages">Edit</button>
                <button id="new-packages">New</button>
                <button id="delete-packages">Delete</button>
            </div>
        </div>
	</div>

<jsp:include page="dialogs/template.jsp"/>
<jsp:include page="dialogs/element.jsp"/>
<jsp:include page="dialogs/module.jsp"/>
<jsp:include page="dialogs/package.jsp"/>
<jsp:include page="dialogs/info.jsp"/>

</body>
</html>

<jsp:include page="resource.jsp"/>
