/*
 * Copyright 2012 Bangkok Project Team, GRIDSOLUT GmbH + Co.KG, and
 * University of Stuttgart (Institute of Architecture of Application Systems)
 * All Rights Reserved.
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

package com.htm.query;

import java.util.List;

import com.htm.exceptions.IllegalArgumentException;
import com.htm.query.jxpath.JXpathQueryEvaluator;

public interface IQueryEvaluator {

    public static final String XPATH_QUERY = "xpathQuery";


    public static class Factory {


        public static IQueryEvaluator newInstance(String queryLanguage, Object context) {
            return new JXpathQueryEvaluator(context);
//			if (queryLanguage.equals(XPATH_QUERY)) {
//				if (queryEvaluator == null) {
//					queryEvaluator = new JXpathQueryEvaluator(context);
//					return queryEvaluator;
//				} else {
//					return queryEvaluator;
//				}
//				
//				
//			}
//			return null;
        }
    }

    public void setContext(Object context);

    public Object getContext();

    public List<?> evaluateQuery(IQuery query) throws IllegalArgumentException;


}
