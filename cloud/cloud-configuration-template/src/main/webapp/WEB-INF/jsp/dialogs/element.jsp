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

<%-- Configuration Element edit and create dialog --%>

<div id="dlg-element">

	<%-- Tabs --%>
	<div id="dlg-element-tabs">
		<ul>
			<li><a href="#dlg-element-general-tab">General</a></li>
			<li><a href="#dlg-element-dependencies-tab">Dependencies </a></li>
			<li><a href="#dlg-element-modules-tab">Modules</a></li>
		</ul>

		<%-- General Configuration Element data--%>
		<div id="dlg-element-general-tab">
			<div id="dlg-element-fields" class="dlg-tab-items-container">
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Id</div>
					<div id="dlg-element-value-id"></div>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Type</div>
					<input id="dlg-element-value-type" type='text' value='' class ='dlg-input'/>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Name</div>
					<input id="dlg-element-value-name" type='text' value='' class ='dlg-input'/>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Version</div>
					<input id="dlg-element-value-version" type='text' value='' class ='dlg-input'/>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">Description</div>
					<textarea rows="3" cols="40" class="dlg-input"></textarea>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">MinMachines</div>
					<input id="dlg-element-value-min-machines" type='text' value='' class ='dlg-input'/>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">MaxMachines</div>
					<input id="dlg-element-value-max-machines" type='text' value='' class ='dlg-input'/>
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
						value='' class ='dlg-input'/>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-element-value-label">MaxReplMachines</div>
					<input id="dlg-element-value-max-repl-machines" type='text'
						value='' class ='dlg-input'/>
				</div>
			</div>
		</div>

		<%-- Other Configuration Elements as dependees for Configuration Element --%>
		<div id="dlg-element-dependencies-tab">
		    <div id="dlg-element-dependees-item-select" class='dlg-tab-items-container'></div>
		</div>
		
		<%-- Installation Modules for Configuration Element --%>
		<div id="dlg-element-modules-tab">
		    <div id="dlg-element-modules-item-select" class='dlg-tab-items-container'></div>
		</div>

	</div>
</div>