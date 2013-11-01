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
        createTemplateURL :   '<portlet:resourceURL id="createTemplate"/>',
        deleteTemplateURL :   '<portlet:resourceURL id="deleteTemplate"/>',
        editTemplateURL :   '<portlet:resourceURL id="editTemplate"/>',
    	getTemplatesForUserURL :   '<portlet:resourceURL id="getTemplatesForUser"/>',
    	getElementsForTemplateURL :   '<portlet:resourceURL id="getElementsForTemplate"/>',
    	getOrganizationsForTemplateURL :   '<portlet:resourceURL id="getOrganizationsForTemplate"/>',
    },
    organization: {
    	availableClustersURL :     '<portlet:resourceURL id="availableClusters"/>',    	
    },
    element: {
    	getAllElementsURL :               '<portlet:resourceURL id="getAllElements"/>',
    }
});
console.log("portletURL.url=", portletURL.url);
</script>
