/*
 * Copyright (c) 2011 the original author or authors.
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
 */

package org.openinfinity.cloud.util.collection;

import java.util.ArrayList;
import java.util.List;

import org.openinfinity.core.exception.ExceptionLevel;
import org.openinfinity.core.util.ExceptionUtil;

/**
 * ListUtil provides utility functions to ease List manipulations
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
public class ListUtil {
	/**
	 * Returns a slice of a list based on imput parameters, and clears sliced portion of input list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List sliceList(int page, int rows, List inputList){
		if (page < 1) ExceptionUtil.throwApplicationException("Invalid page index requested from deployer portlet", ExceptionLevel.ERROR, "exception.error.invalid.parameter");
		int pageStartIndex = (page - 1) * rows;
		int pageEndIndex = pageStartIndex + rows;
		int totalRows = inputList.size();
		pageEndIndex = (pageEndIndex > totalRows) ? totalRows : pageEndIndex;
		List subList = inputList.subList(pageStartIndex, pageEndIndex); 
		List onePage = new ArrayList(subList);
		subList.clear();
		return onePage;
	}
}
