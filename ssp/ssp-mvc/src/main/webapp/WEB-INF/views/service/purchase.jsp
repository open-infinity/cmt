<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="container">
    <h1>
        Purchase service
    </h1>
    <div class="span-12 last">  
        
        <h4>Requested service will be activated after succesful PayPal transaction</h4>
        
        <!-- 
        <script type="text/javascript"
         src="<c:url value='/resources/js/paypal-button.min.js?merchant=vbartoni@gmail.com data-button="buynow' />">
         </script>
        -->
         
        <script src="../resources/js/paypal-button.min.js?merchant=vbartoni@gmail.com"
            data-button="buynow"
            data-name="Service 222" 
            data-quantity="1" 
            data-amount="1000" 
            data-currency="EUR" 
            data-callback="http://localhost:8080/ssp-mvc/service/view" 
            data-env="sandbox">
        </script>
        
        <form method="get" action="service/purchase/cancel">
            <input type="submit" value="Cancel">
        </form>
        <br>
    </div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
