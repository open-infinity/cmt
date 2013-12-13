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

<%-- Element edit and create dialog --%>

<div id="dlg-element">

	<%-- Tabs --%>
	<div id="dlg-element-tabs">
		<ul>
			<li><a href="#dlg-element-general-tab">General</a></li>
			<li><a href="#dlg-element-dependencies-tab">Dependencies</a></li>
			<%--
			<li><a href="#dlg-element-keys-tab">Keys</a></li>
			--%>
		</ul>

		<%-- General --%>
		<div id="dlg-element-general-tab">
			<div id="dlg-element-fields" class="dlg-tab-items-container">
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Id</div>
					<div id="dlg-element-value-id"></div>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Type</div>
					<input id="dlg-element-value-type" type='text' value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Name</div>
					<input id="dlg-element-value-name" type='text' value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Version</div>
					<input id="dlg-element-value-version" type='text' value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Description</div>
					<textarea id="dlg-element-value-description" rows="3" cols="40"></textarea>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">MinMachines</div>
					<input id="dlg-element-value-min-machines" type='text' value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">MaxMachines</div>
					<input id="dlg-element-value-max-machines" type='text' value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Replicated</div>
					<div id="dlg-element-replicated-radio">
						<input type="radio" name="dlg-element-replicated-radio"
							value="true">Yes <input type="radio"
							name="dlg-element-replicated-radio" value="false">No
					</div>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">MinReplMachines</div>
					<input id="dlg-element-value-min-repl-machines" type='text'
						value='' />
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">MaxReplMachines</div>
					<input id="dlg-element-value-max-repl-machines" type='text'
						value='' />
				</div>
			</div>
		</div>

		<%-- Dependees for element --%>
		<div id="dlg-element-dependencies-tab">
			<div id="dlg-dependency-selection-container"
				class="dlg-tab-items-container">
				<div class="dlg-list-panel-container dlg-tab-item">
					<div class="dlg-list-panel-container-title">Dependees</div>
					<div class="dlg-list-panel-container-columns">
						<div class="list-item-column">Platform</div>
						<div>Version</div>
					</div>
					<div id="dlg-element-selected-dependees"
						class="dlg-item-list-container">
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
					<div id="dlg-element-available-dependees"
						class="dlg-item-list-container">
						<ul></ul>
					</div>
				</div>
			</div>
		</div>

		<%-- Keys for element  --%>

		<%--

	        <div id="dlg-element-keys-tab">
                <div id="dlg-keys-values-container" class ="dlg-tab-items-container">
                    <div class="dlg-list-panel-container dlg-tab-item">
                        <div id="dlg-list-keys-container" class="dlg-list-panel-container-title">Keys</div>
                        <div id="dlg-keys" class="dlg-item-list-container">
                            <ul></ul>
                        </div>
                    </div>
                    <div id="dlg-list-values-container" class="dlg-list-panel-container dlg-tab-item">
                        <div class="dlg-list-panel-container-title">Values for key:<span id="dlg-element-key-name"></span></div>
                        <div id="dlg-values" class="dlg-item-list-container">
                            <ul></ul>
                        </div>
                    </div>
                </div>
            </div>

            --%>

	</div>
</div>