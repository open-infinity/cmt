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

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>

<%-- Templates edit and create dialog --%>

<div id="dlg-edit-template">

	<%-- Tabs --%>
	<div id="dlg-edit-template-tabs">
		<ul>
			<li><a href="#dlg-edit-template-template-tab">General</a></li>
			<li><a href="#dlg-edit-template-elements-tab">Elements</a></li>
			<li><a href="#dlg-edit-template-organizations-tab">Organizations</a></li>
		</ul>

		<%-- Template --%>
		<div id="dlg-edit-template-template-tab">
			<div id="edit-template-fields" class="dlg-tab-items-container">
				<div class="dlg-edit-template-template-label-container">
					<span class="dlg-value-label">Template Id</span> <span id="dlg-edit-template-id-value"></span>
				</div>
				<div class="dlg-edit-template-template-label-container">
					<div id="dlg-edit-template-name" class="dlg-value-label">Template name</div>
					<input type='text' class ='dlg-input' value='' />
				</div>
				<div class="dlg-edit-template-template-label-container">
					<div id="template-description" class="dlg-value-label">Template description</div>
					<textarea rows="8" cols="68" class="dlg-input"></textarea>
				</div>
			</div>
		</div>

		<%-- Elements for template--%>
		<div id="dlg-edit-template-elements-tab">
  		    <div id="dlg-template-elements-item-select" class='dlg-tab-items-container'></div>
		</div>

		<%-- Organizations for template--%>
		<div id="dlg-edit-template-organizations-tab">
   		    <div id="dlg-template-organizations-item-select" class='dlg-tab-items-container'></div>
		</div>
	</div>
</div>