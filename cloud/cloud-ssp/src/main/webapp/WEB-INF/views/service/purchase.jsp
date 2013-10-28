<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/views/common/includes.jsp"%>
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<!-- 
    .paypal-button button 
        {
        background-image:none;
        padding: 0 0;
        font-size: 14px;
        font-weight: normal;
        font-style: normal;        
        border-radius: 0;
        color:#000000;
        background-color:#DDD;
        z-index:"";
        }
         -->
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
        <style>
		.button-purchase 
		{
		float:left;
		width:110px;
		height:90px;
		margin:5px;
		}
		.paypal-button button.large {
	       padding: 4px 19px;
	font-size: 14px;
	}
	
	
		</style>
		
		
        <div id = "purcahse-buttons-container" style=""> 
	        <div id = "button-purchase-paypal" class = "button-purchase"> 
	        <script src="../resources/js/paypal-button.min.js?merchant=vbartoni@gmail.com"
	            data-size="small"
	            data-button="buynow"
	            data-name="Service 222" 
	            data-quantity="1" 
	            data-amount="1000" 
	            data-currency="EUR" 
	            data-callback="http://localhost:8080/ssp-mvc/service/view" 
	            data-env="sandbox">
	        </script>
	        </div>       
	        <div class = "button-purchase"> 
	        <form method="get" action="/ssp-mvc/service/purchase/cancel">
	            <input type="submit" value="Cancel">
	        </form>
	        </div>
        </div>
        <br>
    </div>
<%@ include file="/WEB-INF/views/common/footer.jsp"%>
