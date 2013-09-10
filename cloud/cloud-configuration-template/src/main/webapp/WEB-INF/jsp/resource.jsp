<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>

<script type="text/javascript">

function urlContainer (name) {
    this.url = null;
    this.name = name;    
    this.initialize = function(data) {
    	console.log("Initalizing portletURL");
        this.url = data;    
    };
}

var portletURL = new urlContainer("portletURL");

portletURL.initialize({
    template: {
    	getTemplatesForUserURL :   '<portlet:resourceURL id="getTemplatesForUser"/>',	
    },
    organization: {
    	availableClustersURL :     '<portlet:resourceURL id="availableClusters"/>',    	
    },
    element: {
    	machineURL :               '<portlet:resourceURL id="machine"/>',
    }
});

console.log("portletURL.url=", portletURL.url);
</script>
