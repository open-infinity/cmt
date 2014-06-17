<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

		<div class="container">
			<br />
			<h2>
				Edit Product
			</h2>
			<br />
			<div class="sub_menu"><b>Please Define The Product Information</b></div>
			<br />			
			<div id="statusbox"></div>
			<br/>
			<div id="productForm" class="span-12 last">	
				<form:form modelAttribute="productModel" action="product" method="POST">
					<table id="productTable">
						<tr>
							<td><form:label	for="name" path="name" cssErrorClass="error">Name</form:label></td>
							<td><form:input path="name" /></td>
							<td><form:errors path="name" /></td>
						</tr>
						<tr>	
							<td><form:label for="description" path="description" cssErrorClass="error">Description</form:label></td>
							<td><form:input path="description" /></td>
							<td><form:errors path="description" /></td>
						</tr>
						<tr>	
							<td><form:label for="company" path="company" cssErrorClass="error">Company</form:label></td>
							<td><form:input path="company" /></td>
							<td><form:errors path="company" /></td>
						</tr>
						<tr>
							<td>
								<p>	
									<form:hidden path="id" id="id" />
									<input id="save" type="submit" value="Save" />
								</p>
							</td>
							<td></td>
							<td></td>
						</tr>
						</table>
				</form:form>
			</div>	
		</div>
		
		<c:set var="model" value="productModel" />
		
<%@ include file="/WEB-INF/views/common/validation_scripts.jsp" %>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>