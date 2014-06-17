(function($){

	var itemselect_data ={
        name:"itemselect",
        version:"1.0",
        defaults:{
            //template : "<li class='ui-state-default'> <div></div> <div></div> </li>",
            template : "<li class='ui-state-default'>",
            /*
            text : {
                selected : {
                    title : "Selected",
                    column : {
                        title : "name",
                        value : ["version"]
                    }
                },
                available : {
                    title : "Available",
                    column : {
                        title : "name",
                        value : ["version"]
                    }
                }
            },
            */
            paneTitles:{leftPaneTitle:"Selected", rightPaneTitle:"Available"},
            colTitles:['Name', 'Version'],
            colModel:[{name:'name', width:125, align:"left"},
                      {name:'version', width:43, align:"right"}],
            colModelKey:'id'
        }
    };

    // public methods of the widget
	var methods = {
        init : function(data, options){
            return this.each(function(){

                var widget = this;
                widget.settings = {};
                widget.html = {};
                widget.template = {};

                // merge settings
                $.extend (widget.settings, itemselect_data.defaults, options);

                if (!validateSettings(widget)){
                    console.log("Invalid settings, unable to initialize widget");
                }

                // build widget's dom
                // TODO use mustache.js

                widget.template.columnTitles = [];
                for (var i = 0; i < widget.settings.colTitles.length; i++){
                    widget.template.columnTitles.push("<div>" + widget.settings.colTitles[i] + "</div>");
                }

                widget.html.root = $("<div class='is-container'></div>");
                widget.html.leftPanel = $("<div class='is-panel dlg-tab-item'></div>");   //dlg-list-panel-container
                widget.html.rightPanel = $("<div class='is-panel dlg-tab-item'></div>");
                widget.html.placeholder = $("<div class='is-placeholder dlg-tab-item'></div>");
                //widget.html.panelSelectedTitle = $("<div class='is-panel-title'>" + widget.settings.text.selected.title + "</div>");
                widget.html.leftPanelTitle = $("<div class='is-panel-title'>" + widget.settings.paneTitles.leftPaneTitle + "</div>");
                //widget.html.panelAvailableTitle = $("<div class='is-panel-title'>" + widget.settings.text.available.title + "</div>");
                widget.html.rightPanelTitle = $("<div class='is-panel-title'>" + widget.settings.paneTitles.rightPaneTitle + "</div>");

                widget.html.leftPanelColumns = $("<div class='is-panel-columns'></div>");
                widget.html.rightPanelColumns = $("<div class='is-panel-columns'></div>");

                //widget.html.selectedColumnTitle = $("<div>" + widget.settings.text.selected.column.title + "</div>");
                //widget.html.availableColumnTitle = $("<div>" + widget.settings.text.available.column.title + "</div>");
                //widget.html.selectedColumnValue = $("<div>" + widget.settings.text.selected.column.value[0] + "</div>");
                //widget.html.availableColumnValue = $("<div>" + widget.settings.text.available.column.value[0] + "</div>");

                widget.html.selectedListContainer = $("<div class='is-list-container'>");
                widget.html.availableListContainer = $("<div class='is-list-container'>");
                widget.html.selected = $("<ul></ul>");
                widget.html.available = $("<ul></ul>");

                // build level 1
                $(widget).append(widget.html.root);

                // build level 2
                widget.html.root.append(widget.html.leftPanel).append(widget.html.placeholder).append(widget.html.rightPanel);

                // build level 3
                widget.html.leftPanel.append(widget.html.leftPanelTitle).
                        append(widget.html.leftPanelColumns).
                        append(widget.html.selectedListContainer);
                widget.html.rightPanel.append(widget.html.rightPanelTitle).
                        append(widget.html.rightPanelColumns).
                        append(widget.html.availableListContainer);

                // build level 4
                //widget.html.leftPanelColumns.append(widget.html.selectedColumnTitle).append(widget.html.selectedColumnValue);
                for (var i = 0; i < widget.settings.colTitles.length; i++){
                    widget.html.leftPanelColumns.append($(widget.template.columnTitles[i]));
                    widget.html.rightPanelColumns.append($(widget.template.columnTitles[i]));
                }
                //widget.html.rightPanelColumns.append(widget.html.availableColumnTitle).append(widget.html.availableColumnValue);
                widget.html.selectedListContainer.append(widget.html.selected);
                widget.html.availableListContainer.append(widget.html.available);

                // populate widget with data

                // create template and compute css styles for it
                var template = widget.settings.template;
                for (var j = 0; j < widget.settings.colModel.length; j++){
                    var style ='';
                    if (j === 0){
                        style += 'width:' + widget.settings.colModel[j].width + 'px' +';float:left';
                    }
                    template += "<div class='is-item-value' style =" + "'" + style + "'" + "></div>";
                }
                template += '</li>';
                //populateItems(data, this.settings.template, widget);
                populateItems(data, template, widget);

                // bind widget events
                configureDragAndDrop($(this));
                $("li", $(this)).click(function(){
                    $(this).toggleClass("ui-state-highlight");
                });
            });
        },
        clean : function(){
            $("ul", $(this)).empty();
        },
        destroy : function(){
        	$(this).empty();
        },
        getVal : function(){
            var selectedIndices = [];
                var lis = this.find("ul:first").find("li");
            for (var i = 0; i < lis.length; i++){
                //selectedIndices.push($(lis[i]).data("config").id);
                selectedIndices.push(getValueForKey(this[0].settings.colModelKey, $(lis[i]).data("config")));
            }
            return selectedIndices;
        }
    };

    // method calling logic
    $.fn.itemselect = function(method){
        if (methods[method]){
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        }
        else if (typeof method === 'object' || ! method){
            return methods.init.apply( this, arguments );
        }
        else{
            $.error('Method ' +  method + ' does not exist on jQuery.tooltip');
        }
    };

    // Private methods of the widget

    function populateItems(data, template, self){
        var selectedIndices = [];
        $.each(data.selected, function(index, value){
            storeItemToDom(value, self.html.selected, template, self);
            //selectedIndices.push(value.id);
            selectedIndices.push(getValueForKey(self.settings.colModelKey, value));
        });
        $.each(data.available, function(index, value){
            //if (selectedIndices.indexOf(value.id) == -1){
            if (selectedIndices.indexOf(getValueForKey(self.settings.colModelKey, value)) == -1){
                storeItemToDom(value, self.html.available, template, self);
            }
        });
    }

    function storeItemToDom(responseObj, list, template, widget){
        list.append(template);
        var lastChild = list.find("li:last-child");
        lastChild.data("config", responseObj);

        var column = null;
        for (var i = 0; i < widget.settings.colModel.length; i++){
            column = getValueForKey(widget.settings.colModel[i].name, responseObj);
            if (column !== null){
                //lastChild.find("div").first().text(column.substring(0, 17));
                var seq = i +1;
                var filter = "'" + ':nth-child(' +  seq  + ')' + "'";
                lastChild.find("div").filter(filter).text(column);
            }
        }

        /*
        // Set "title" column
        //widget.html.columnTitles
        var column = getValueForKey(widget.settings.text.available.column.title, responseObj);
        if (column !== null){
            lastChild.find("div").first().text(column.substring(0, 17));
        }

        // Set "value" column
        column = getValueForKey(widget.settings.text.selected.column.value[0], responseObj);
        if (column === null){
            lastChild.find("div").first().text(column.substring(0, 5));
        }
        */
    }

    // Checks if object has a key and returns a value for it
    function getValueForKey(key, obj){
        var retVal = null;
        var keys = Object.keys(obj);
        for (var i = 0; i < keys.length; i++){
            if (key === keys[i]){
                retVal = obj[key];
                break;
            }
        }
        if (retVal === null){
             console.log("Unable to find key in object:" + obj);
        }
        return retVal;
    }

    function configureDragAndDrop(self){
        self.find(".is-panel").droppable({
            activeClass: "ui-state-highlight",
            drop: function (event, ui) {
                var list = $(this).find("ul");
                var selected = $(this).siblings().find("li.ui-state-highlight");
                if (selected.length > 1) {
                    moveMultipleItems(list, selected);
                } else {
                    moveSingleItem(ui.draggable, list);
                }
            },
            tolerance: "touch"
        });
        self.find("li", ".is-container").draggable({
            revert: "invalid",
            containment: "document",
            helper: "clone",
            cursor: "move",
            scroll: true,
            drag: function (event, ui) {
                var helper = ui.helper;
                var selected = $(this).parent().find("li.ui-state-highlight", "ul");
                if (selected.length > 2) {
                    $(helper).html(selected.length - 1 + " items");
                }
            }
        });
    }

    function moveSingleItem(elem, list) {
        elem.appendTo(list).removeClass("ui-state-highlight").fadeIn();
    }

    function moveMultipleItems(list, selected) {
        $(selected).each(function () {
            $(this).appendTo(list).removeClass("ui-state-highlight").fadeIn();
        });
    }

    function validateSettings(self){
        var ret = true;
        if (self.settings.colTitles.length !== self.settings.colModel.length){
            ret = false;
        }
        return ret;
    }

})(jQuery);