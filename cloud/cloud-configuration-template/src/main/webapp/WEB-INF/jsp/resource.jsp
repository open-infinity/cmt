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
    	machineURL :               '<portlet:resourceURL id="machine"/>',
    }
});
 private static final String PATH_GET_ELEMENTS_FOR_TEMPLATE = "getElementsForTemplate";
     private static final String PATH_GET_ORGANIZATIONS_FOR_TEMPLATE = "getOrganizationsForTemplate";

     private static final String PATH_EDIT_TEMPLATE = "editTemplate";
     private static final String PATH_CREATE_TEMPLATE = "createTemplate";
     private static final String PATH_DELETE_TEMPLATE = "deleteTemplate";
console.log("portletURL.url=", portletURL.url);
</script>
