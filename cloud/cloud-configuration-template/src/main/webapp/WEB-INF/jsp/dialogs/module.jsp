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

<%-- Module edit and create dialog --%>

<div id="dlg-module">

	<%-- Tabs --%>
	<div id="dlg-module-tabs">
		<ul>
			<li><a href="#dlg-module-general-tab">General</a></li>
			<li><a href="#dlg-module-packages-tab">Packages</a></li>
			<li><a href="#dlg-module-keys-tab">Keys</a></li>
		</ul>

		<%-- General --%>
		<div id="dlg-module-general-tab">
			<div id="dlg-module-fields" class="dlg-tab-items-container">
				<div class="dlg-input-container">
					<div class="dlg-value-label">Id</div>
					<div id="dlg-module-value-id"></div>
				</div>

				<%--
				<div class="dlg-input-container">
					<div class="dlg-value-label">Type</div>
					<input id="dlg-module-value-type" type='text' value='' />
				</div>
				--%>

				<div class="dlg-input-container">
					<div class="dlg-value-label">Name</div>
					<input id="dlg-module-value-name" type='text' value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-value-label">Version</div>
					<input id="dlg-module-value-version" type='text' value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-value-label">Description</div>
					<textarea id="dlg-module-value-description" rows="3" cols="40" class="dlg-item-description"></textarea>
				</div>
			</div>
		</div>

		<%-- Packages for module --%>
		<div id="dlg-module-packages-tab">
		    <div id="dlg-module-packages-item-select" class='dlg-tab-items-container'></div>
		    <%--
			<div id="dlg-package-selection-container" class="dlg-tab-items-container">
				<div class="dlg-list-panel-container dlg-tab-item">
					<div class="dlg-list-panel-container-title">Dependees</div>
					<div class="dlg-list-panel-container-columns">
						<div class="list-item-column">Platform</div>
						<div>Version</div>
					</div>
					<div id="dlg-module-selected-dependees"	class="dlg-item-list-container">
						<ul></ul>
					</div>
				</div>
				<div class="dlg-arrows-img dlg-tab-item"></div>
				<div class="dlg-list-panel-container dlg-tab-item">
					<div class="dlg-list-panel-container-title">All</div>
					<div class="dlg-list-panel-container-columns">
						<div class="list-item-column">Platform</div>
						<div>Version</div>
					</div>
					<div id="dlg-module-available-dependees"
						class="dlg-item-list-container">
						<ul></ul>
					</div>
				</div>
			</div>
			--%>
		</div>

		<%-- Keys and values for module  --%>
		<div id="dlg-module-keys-tab">
			<div id="dlg-keys-values-container" class="dlg-tab-items-container">
				<div class="dlg-list-panel-container dlg-tab-item">
					<div id="dlg-list-keys-container"
						class="dlg-list-panel-container-title">Keys</div>
					<div id="dlg-keys" class="dlg-module-item-list-container">
						<ul></ul>
					</div>
				</div>
				<div id="dlg-list-values-container"
					class="dlg-list-panel-container dlg-tab-item">
					<div class="dlg-list-panel-container-title">
						Values for key:<span id="dlg-module-key-name"></span>
					</div>
					<div id="dlg-values" class="dlg-module-item-list-container">
						<ul></ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>