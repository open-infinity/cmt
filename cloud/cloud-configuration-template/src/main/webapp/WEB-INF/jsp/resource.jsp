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
        createTemplateURL : '<portlet:resourceURL id="createTemplate"/>',
        getTemplateURL : '<portlet:resourceURL id="getTemplate"/>',
        getTemplatesURL : '<portlet:resourceURL id="getTemplates"/>',
        getElementsForTemplateURL : '<portlet:resourceURL id="getElementsForTemplate"/>',
        getAllAvailableElementsURL : '<portlet:resourceURL id="getAllAvailableElements"/>',
        getOrganizationsForTemplateURL : '<portlet:resourceURL id="getOrganizationsForTemplate"/>',
        getAllOrganizationsURL : '<portlet:resourceURL id="getAllOrganizations"/>',
        editTemplateURL : '<portlet:resourceURL id="editTemplate"/>',
        deleteTemplateURL : '<portlet:resourceURL id="deleteTemplate"/>'
    },
    organization: {
    	availableClustersURL :'<portlet:resourceURL id="availableClusters"/>',
    },
    element: {
       	createElementURL : '<portlet:resourceURL id="createElement"/>',
    	getElementURL : '<portlet:resourceURL id="getElement"/>',
    	getElementsURL : '<portlet:resourceURL id="getElements"/>',
    	getAllDependenciesURL : '<portlet:resourceURL id="getAllDependencies"/>',
    	getAllModulesURL : '<portlet:resourceURL id="getAllModules"/>',
    	getModulesForElementURL : '<portlet:resourceURL id="getModulesForElement"/>',
    	getDependenciesURL : '<portlet:resourceURL id="getDependencies"/>',
    	editElementURL : '<portlet:resourceURL id="editElement"/>',
        deleteElementURL : '<portlet:resourceURL id="deleteElement"/>'
    },
    module: {
      	createModuleURL : '<portlet:resourceURL id="createModule"/>',
    	getModuleURL : '<portlet:resourceURL id="getModule"/>',
    	getModulesURL : '<portlet:resourceURL id="getModules"/>',
    	getPackagesForModuleURL : '<portlet:resourceURL id="getPackagesForModule"/>',
    	getAllPackagesURL : '<portlet:resourceURL id="getAllPackages"/>',
    	getParameterKeysAndValuesURL : '<portlet:resourceURL id="getParameterKeysAndValues"/>',
    	editModuleURL : '<portlet:resourceURL id="editModule"/>',
        deleteModuleURL : '<portlet:resourceURL id="deleteModule"/>'
    },
    package: {
        createPackageURL : '<portlet:resourceURL id="createPackage"/>',
        getPackageURL : '<portlet:resourceURL id="getPackage"/>',
        getPackagesURL : '<portlet:resourceURL id="getPackages"/>',
        editPackageURL : '<portlet:resourceURL id="editPackage"/>',
        deletePackageURL : '<portlet:resourceURL id="deletePackage"/>'
    }
});

</script>
