function itemSelectConfigureDragAndDrop(widget){
    //dlg.html.self.find(".dlg-list-panel-container").droppable({
    widget.find(".dlg-list-panel-container").droppable({
        activeClass: "ui-state-highlight",
        drop: function (event, ui) {
            var list = $(this).find("ul");
            var selected = $(this).siblings().find("li.ui-state-highlight");
            if (selected.length > 1) {
                moveMultipleElements(list, selected);
            } else {
                moveSingleElement(ui.draggable, list);
            }
        },
        tolerance: "touch"
    });
    widget.find("li", ".dlg-item-selection-container").draggable({
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

function validateItems(items){
    res = true;
    for (var i = 0; i < items.length; i++){
        if (!isPosInt(items[i]) || items[i] < 0){
            res = false;
            alert(err.invalidItems);
            break;
         }
    }
    return res;
}