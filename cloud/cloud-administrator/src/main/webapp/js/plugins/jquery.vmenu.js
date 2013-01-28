/* 
* vmenu - touch friendly dropdown menu 
* author: Vedran Bartonicek
*/


(function( $ ){
	
	var vmenu_data ={
    name:"vmenu",
    version:"1.0",

    defaults:{
		title: 'Machine size',
		item_1: 'Small',
		item_2: 'Medium',
		item_3: 'Large'
    }};

	var methods = {
		init : function( options ) { 
			return this.each(function(){
				this.settings = {};
				var menu = this;
				$.extend (menu.settings, vmenu_data.defaults, options);
				
				// Build vmenu	
				menu.rootMenu = $("<ul class='vm_root_list'>"); 
				menu.rootMenuItem = $("<li class ='vm_root_list_item'>"); 
				menu.rootTitle = $("<a class='title' href='#'></a>"); 
				menu.levelOneMenu = $("<ul class='vm_level_1_list'>"); 
				menu.levelOneMenuItem ="<li>";
				menu.levelOneMenuItemText = "<a>";
				
				$(this).append(menu.rootMenu);
				menu.rootMenu.append(menu.rootMenuItem);
				menu.rootMenuItem.append(menu.rootTitle);
				menu.rootMenuItem.append(menu.levelOneMenu);
				
				menu.rootTitle.text(this.settings.title);
				for (var obj in this.settings){
					if (obj == 'title') continue;
					var item = $(menu.levelOneMenuItem);
					item.append($(menu.levelOneMenuItemText).text(this.settings[obj]));	
					menu.levelOneMenu.append(item);
				}
				$("li a:first", menu.levelOneMenu).addClass("vm_selected");
				
				// click root menu item
				menu.rootMenuItem.click(function () {
					menu.rootTitle.text(menu.settings.title); 
					if (menu.levelOneMenu.css("display") == "none"){
						menu.levelOneMenu.slideDown(300); 
					}
					else{
						var _menu = menu;
						window.setTimeout(function () {
							_menu.rootTitle.text($(".vm_selected", _menu).text());		
						}, 300);
						$('ul', this).slideUp(300);
					}
				});
					
				// click submenu item
				$("li", menu.levelOneMenu).click(function() {		
					menu.levelOneMenu.find("a").removeClass("vm_selected");
					$("a:first", this).addClass("vm_selected");
				});	
			});	
		},
		
		getVal : function() {
			return (".vm_selected", this).text();	
		},
		
		// return ID of a menu item
		getValId : function() {
			var index = 0;
			var vmenu_obj = this.get(0);
			for (var o in vmenu_obj.settings){
				if (vmenu_obj.settings[o] == $(".vm_selected", vmenu_obj).text()){
					break;
				}
				index ++;
			}	
			return index - 1;	
		},
		
		// clear selection
		reset : function() {
			var vmenu_list = this.get();
			for (var i in vmenu_list){
				$(".title", vmenu_list[i]).text(vmenu_list[i].settings.title);
				$("a", vmenu_list[i]).removeClass("vm_selected");
				$("ul li ul li:first a", vmenu_list[i]).addClass("vm_selected");
			}		
		}
	};
    
  $.fn.vmenu = function( method ) {
    
    // Method calling logic
    if ( methods[method] ) {
      return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
    } else if ( typeof method === 'object' || ! method ) {
      return methods.init.apply( this, arguments );
    } else {
      $.error( 'Method ' +  method + ' does not exist on jQuery.tooltip' );
    }    
  
  };
  })( jQuery );