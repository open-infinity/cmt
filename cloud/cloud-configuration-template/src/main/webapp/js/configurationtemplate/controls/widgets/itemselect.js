(function($){

	var itemselect_data ={
        name:"itemselect",
        version:"1.0",
        defaults:{
            template : "<li class='ui-state-default'> <div></div> <div></div> </li>",
            text : {
                selected : {
                    title : "Selected",
                    column : {
                        title : "Name",
                        value : ["Version"]
                    }
                },
                available : {
                    title : "Available",
                    column : {
                        title : "Name",
                        value : ["Version"]
                    }
                }
            }
        }
    };

    // public methods of the widget
	var methods = {
        init : function(data, options){
            return this.each(function(){

                // merge settings
                var widget = this;
                widget.settings = {};
                widget.html = {};
                $.extend (widget.settings, itemselect_data.defaults, options);

                // build widget's dom
                // TODO use mustache.js
                widget.html.root = $("<div class='is-container'></div>");
                widget.html.panelSelected = $("<div class='is-panel dlg-tab-item'></div>");   //dlg-list-panel-container
                widget.html.panelAvailable = $("<div class='is-panel dlg-tab-item'></div>");
                widget.html.placeholder = $("<div class='is-placeholder dlg-tab-item'></div>");
                widget.html.panelSelectedTitle = $("<div class='is-panel-title'>" + widget.settings.text.selected.title + "</div>");
                widget.html.panelAvailableTitle = $("<div class='is-panel-title'>" + widget.settings.text.available.title + "</div>");
                widget.html.panelSelectedColumns = $("<div class='is-panel-columns'></div>");
                widget.html.panelAvailableColumns = $("<div class='is-panel-columns'></div>");
                widget.html.selectedColumnTitle = $("<div>" + widget.settings.text.selected.column.title + "</div>");
                widget.html.availableColumnTitle = $("<div>" + widget.settings.text.available.column.title + "</div>");
                widget.html.selectedColumnValue = $("<div>" + widget.settings.text.selected.column.value[0] + "</div>");
                widget.html.availableColumnValue = $("<div>" + widget.settings.text.available.column.value[0] + "</div>");
                widget.html.selectedListContainer = $("<div class='is-list-container'>");
                widget.html.availableListContainer = $("<div class='is-list-container'>");
                widget.html.selected = $("<ul></ul>");
                widget.html.available = $("<ul></ul>");

                // build level 1
                $(widget).append(widget.html.root);

                // build level 2
                widget.html.root.append(widget.html.panelSelected).append(widget.html.placeholder).append(widget.html.panelAvailable);

                // build level 3
                widget.html.panelSelected.append(widget.html.panelSelectedTitle).
                        append(widget.html.panelSelectedColumns).
                        append(widget.html.selectedListContainer);
                widget.html.panelAvailable.append(widget.html.panelAvailableTitle).
                        append(widget.html.panelAvailableColumns).
                        append(widget.html.availableListContainer);

                // build level 4
                widget.html.panelSelectedColumns.append(widget.html.selectedColumnTitle).append(widget.html.selectedColumnValue);
                widget.html.panelAvailableColumns.append(widget.html.availableColumnTitle).append(widget.html.availableColumnValue);
                widget.html.selectedListContainer.append(widget.html.selected);
                widget.html.availableListContainer.append(widget.html.available);

                // populate widget with data
                populateItems(data, this.settings.template, widget.html.selected, widget.html.available);

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
                var lis = widget.html.selected.find("li");
            for (var i = 0; i < lis.length; i++){
                selectedIndices.push($(lis[i]).data("config").id);
            }
            return selectedIndices;
        },

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

    function populateItems(data, template, selected, available){
        var selectedIndices = [];
        $.each(data.selected, function(index, value){
            storeItemToDom(value, selected, template);
            selectedIndices.push(value.id);
        });
        $.each(data.available, function(index, value){
            if (selectedIndices.indexOf(value.id) == -1){
                storeItemToDom(value, available, template);
            }
        });
    }

    function storeItemToDom(value, list, template){
        list.append(template);
        var lastChild = list.find("li:last-child");
        lastChild.find("div").first().text(value.name.substring(0, 17));
        lastChild.find("div").last().text(value.version.substring(0, 5));
        lastChild.data("config", value);
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

})(jQuery);