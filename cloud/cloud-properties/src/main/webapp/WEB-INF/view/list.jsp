<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet_2_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<portlet:defineObjects />

<script type="text/javascript">
	var row_num = 1;

	// Returns number at the end of the id separated by underscore
	function _parse_num(id) {
		var pos = id.indexOf("_");
		if (pos != -1) {
			return id.substring(pos + 1);
		} else {
			return null;
		}
	}
	
	// Add a new row to the properties table
	function _add_row(num, key, value) {
		$("#prop-table").append('<div class="prop-row" id="row_' + num + '"></div>');
		$("#row_" + num).append('<div class="prop-key" id="key_' + num + '"><div class="edit" id="prop-key-editable_' + num + '">' + key + '</div></div>');
		$("#row_" + num).append('<div class="prop-value" id="value_' + num + '"><div class="edit" id="prop-value-editable_' + num + '">' + value + '</div></div>');
		$("#row_" + num).append('<div class="prop-tools" id="tools_' + num + '">[<a href="#" id="prop-del_' + num + '">del</a>]</div>');
		
		// Add event handler for row deletion
		$("#prop-del_" + num).on('click', function(event) {
			$.ajax({
				type: "POST",
				url: "<portlet:resourceURL id="deleteProperty"/>",
				data: { id: event.target.id, key: $("#prop-key-editable_" + _parse_num(this.id)).text() }
			}).done(function( msg ) {
				$("#row_" + _parse_num(event.target.id)).remove();
			});			
		});

		$("#prop-key-editable_" + num).editable('<portlet:resourceURL id="changePropertyKey"/>', { 
			submitdata : function(value, settings) {
				return {'oldvalue': value};
			}
		});
		$("#prop-value-editable_" + num).editable('<portlet:resourceURL id="savePropertyValue"/>', {
			submitdata : function(value, settings) {
				return {'key': $("#prop-key-editable_" + _parse_num(this.id)).text()};
			}
		});
	}

	$('#prop-organization').change() {
		$.ajax({
			type: "POST",
			url: "<portlet:resourceURL id="setOrganization"/>",
			data: { organizationId: event.target.id }
		}).done(function( msg ) {
		});			
	}
	
	$(document).ready(function() {		
		// Add row event handler
		$("#propAdd").on('click', function(event) {
			var key = "new_" + row_num; // TODO
			_add_row(row_num, "", "");
			row_num++;
			// TODO: save with Ajax?
		});

		<c:forEach var="prop" items="${props}">
		_add_row(row_num++, '${prop.key}', '${prop.value}');	
		</c:forEach>
	});

</script>

<div>Organization: 
	<select class="prop-organization" name="organization">
		<c:forEach var="org" items="${organizations}">
			<option value="${org.organizationId}">${org.name}</option>
		</c:forEach>
	</select>
</div>	 
	 
<p>[<a rhef="#" id="propAdd">add</a>]</p>

<div class="prop-table" id="prop-table">
	<div class="prop-row">
		<div class="prop-header">Keys</div>
		<div class="prop-header">Values</div>
	</div>
</div>

<div>
</div>
