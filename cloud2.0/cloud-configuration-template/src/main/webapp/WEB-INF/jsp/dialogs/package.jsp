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

<%-- Package edit and create dialog --%>

<div id="dlg-package">

	<%-- Tabs --%>
	<div id="dlg-package-tabs">
		<ul>
			<li><a href="#dlg-package-general-tab">General</a></li>
			<li><a href="#dlg-package-upload-tab">Upload</a></li>
		</ul>

		<%-- General --%>
		<div id="dlg-package-general-tab">
			<div id="dlg-package-fields" class="dlg-tab-items-container">
				<div class="dlg-input-container">
					<div class="dlg-value-label">Id</div>
					<div id="dlg-package-value-id"></div>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-value-label">Name</div>
					<input id="dlg-package-value-name" type='text' value='' class ='dlg-input'/>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-value-label">Version</div>
					<input id="dlg-package-value-version" type='text' value='' class ='dlg-input'/>
				</div>
				<div class="dlg-input-container">
					<div class="dlg-value-label">Description</div>
					<textarea id="dlg-package-value-description" rows="3" cols="40" class="dlg-input"></textarea>
				</div>
			</div>
		</div>

		<%-- Upload tab --%>
		<div id="dlg-package-upload-tab"></div>

	</div>
</div>