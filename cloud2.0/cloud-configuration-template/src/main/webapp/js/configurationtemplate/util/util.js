/**
 * Copyright (c) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0 
 * @since 1.3.0
 */

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

function isPosInt(obj){
    return (obj !== "" && typeof obj !== 'undefined' && !isNaN(obj) && (Math.round(obj) == obj) && obj > 0) ? true : false;
}

// Alerts

function alertPostFailure(mode, textStatus, errorThrown){
    alert("Server error at template" + mode + ", text status:" + textStatus + " " + "errorThrown:" + errorThrown);
}

function alertWrongInput(item, msg){
    alert(msg);
    item.addClass("dlg-error-input");

    // open the tab with erroneous item and focus on it
    var inTemplateTab = (item.parents("#dlg-edit-template-template-tab")).length > 0;
    var inElementTab = (item.parents("#dlg-element-general-tab")).length > 0;
    var inModuleTab = (item.parents("#dlg-module-general-tab")).length > 0;
    var inPackageTab = (item.parents("#dlg-package-general-tab")).length > 0;

    if (inTemplateTab || inElementTab || inModuleTab || inPackageTab){
        dlg.html.tabs.tabs('select', 0);
    }
    else{
        dlg.html.tabs.tabs('select', 2);
    }

    item.focus();
}

