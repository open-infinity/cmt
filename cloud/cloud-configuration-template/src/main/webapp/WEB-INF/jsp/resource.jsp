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
        getTemplateURL :   '<portlet:resourceURL id="getTemplate"/>',
    	getTemplatesForUserURL :   '<portlet:resourceURL id="getTemplatesForUser"/>',
    	getElementsForTemplateURL :   '<portlet:resourceURL id="getElementsForTemplate"/>',
    	getAllAvailableElementsURL :               '<portlet:resourceURL id="getAllAvailableElements"/>',
    	getOrganizationsForTemplateURL :   '<portlet:resourceURL id="getOrganizationsForTemplate"/>',
    	getAllOrganizationsURL :   '<portlet:resourceURL id="getAllOrganizations"/>',
    },
    organization: {
    	availableClustersURL :     '<portlet:resourceURL id="availableClusters"/>',    	
    },
    element: {
    	getElementsURL :               '<portlet:resourceURL id="getElements"/>',
    }
});
console.log("portletURL.url=", portletURL.url);
</script>
