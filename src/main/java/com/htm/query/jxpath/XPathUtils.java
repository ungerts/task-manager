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

package com.htm.query.jxpath;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.htm.exceptions.HumanTaskManagerException;
import com.htm.utils.Utilities;

public class XPathUtils {

    private static final Logger log = Utilities.getLogger(XPathUtils.class);

    public static double getDouble(Node node) throws HumanTaskManagerException {
        try {
            return Double.parseDouble(node.getTextContent());
        } catch (Exception e) {
            String message = "Cannot parse double: " + e.getMessage();
            log.error(message, e);
            throw new HumanTaskManagerException(message, e);
        }
    }

    public static boolean getBoolean(Node node) {
        return Boolean.parseBoolean(node.getTextContent());
    }

    public static String getString(Node node) {
        return node.getTextContent();
    }

    // TODO duration
    public static <T> T getResultByType(Class<T> resultClass, Object object)
            throws Exception {
        // System.out.println("Node-Assignable: " +
        // Node.class.isAssignableFrom(object.getClass()));
        // System.out.println("Assignable: " +
        // resultClass.isAssignableFrom(object.getClass()));
        if (object == null) {
            return null;
        } else if (resultClass.isAssignableFrom(object.getClass())) {
            log.debug("Object of class " + object.getClass().getName()
                    + " can be casted to " + resultClass.getName());
            return resultClass.cast(object);
        } else if (object instanceof Node) {
            log.debug("Object is instance of node");
            return getResultByType(resultClass,
                    ((Node) object).getTextContent());
        } else if (resultClass.equals(String.class)) {

            String result = object.toString();

            return resultClass.cast(result);

        } else if (resultClass.equals(Double.class)) {
            Double result = Double.NaN;
            if (object instanceof Number) {
                result = ((Number) object).doubleValue();
            } else if (object instanceof String) {
                try {
                    result = Double.parseDouble(((String) object));
                } catch (Exception e) {
                    String message = "Cannot parse double: " + e.getMessage();
                    log.error(message, e);
                    throw new Exception(message, e);
                }

            } else {
                try {
                    result = Double.parseDouble((object.toString()));
                } catch (Exception e) {
                    String message = "Cannot parse double: " + e.getMessage();
                    log.error(message, e);
                    throw new Exception(message, e);
                }
            }
            return resultClass.cast(result);
        } else if (resultClass.equals(Boolean.class)) {
            Boolean result = Boolean.parseBoolean(object.toString());
            return resultClass.cast(result);
        } else if (resultClass.equals(Timestamp.class)) {
            Timestamp result;
            if (object instanceof Calendar) {
                result = new Timestamp(((Calendar) object).getTimeInMillis());
            } else if (object instanceof Number) {
                result = new Timestamp(((Number) object).longValue());
            } else if (object instanceof String) {
                result = getTimestampFromString((String) object);
            } else {
                result = getTimestampFromString(object.toString());
            }
            return resultClass.cast(result);
        } else if (resultClass.equals(Duration.class)) {
            Duration result;
            if (object instanceof Calendar) {
                result = getDurationFromLong(((Calendar) object)
                        .getTimeInMillis());
            } else if (object instanceof Number) {
                result = getDurationFromLong(((Number) object).longValue());
            } else if (object instanceof String) {
                result = getDurationFromString((String) object);
            } else {
                result = getDurationFromString(object.toString());
            }
            return resultClass.cast(result);
        } else {
            throw new Exception("Target type not supported: "
                    + resultClass.getName());
        }

        // return null;
    }

    private static Duration getDurationFromLong(long longValue)
            throws Exception {
        try {
            DatatypeFactory df = DatatypeFactory.newInstance();

            return df.newDuration(longValue);
        } catch (Exception e) {
            String message = "Cannot create duration from long: " + longValue;
            log.error(message, e);
            throw new Exception(message, e);

        }
    }

    private static Duration getDurationFromString(String object)
            throws Exception {
        DatatypeFactory df = DatatypeFactory.newInstance();
        try {
            return df.newDuration(object);
        } catch (Exception e) {
            String message = "Cannot create duration from string: " + object;
            log.error(message, e);
            try {
                log.debug("Trying to parse duration as number");
                Number number = Double.parseDouble(object);
                return df.newDuration(number.longValue());

            } catch (Exception ex) {
                throw new Exception(message, ex);
            }

        }
    }

    // TODO parse time from number
    private static Timestamp getTimestampFromString(String object)
            throws Exception {
        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            XMLGregorianCalendar timestamp = df.newXMLGregorianCalendar(object);
            return new Timestamp(timestamp.toGregorianCalendar()
                    .getTimeInMillis());
        } catch (Exception e) {
            String message = "Cannot create timestamp from string: " + object;
            try {
                log.debug("Trying to parse timestamp as number");
                Number number = Double.parseDouble(object);
                return new Timestamp(number.intValue());

            } catch (Exception ex) {
                throw new Exception(message, ex);
            }

        }
        // return null;
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            log.info("Result: "
                    + getResultByType(String.class, new String("1")));
            log.info("Result: " + getResultByType(Boolean.class, true));

            log.info("Time: " + getTimestampFromString("2001-10-26T21:32:52"));
            log.info("Time2:"
                    + getResultByType(Timestamp.class,
                    System.currentTimeMillis()));
            log.info("String1: "
                    + getResultByType(String.class,
                    new Timestamp(System.currentTimeMillis())));
            log.info("String: "
                    + getResultByType(Double.class, System.currentTimeMillis())
                    .longValue());
            log.info("String: " + getResultByType(Boolean.class, true));
            log.info("Number: "
                    + getResultByType(Number.class, 5).getClass().getName());
            log.info(Number.class.isAssignableFrom(Double.class));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // System.out.println(IJXPathQuery.class
        // .isAssignableFrom(XPathQueryImpl.class));
        // System.out.println(XPathQueryImpl.class
        // .isAssignableFrom(IJXPathQuery.class));
    }

    public static Timestamp getTimestampFromDuration(Duration duration) throws Exception {
        log.debug("Determing timestamp from duration");
        DatatypeFactory df = DatatypeFactory.newInstance();
        XMLGregorianCalendar timestamp = df.newXMLGregorianCalendar(new GregorianCalendar());
        timestamp.add(duration);
        return new Timestamp(timestamp.toGregorianCalendar().getTimeInMillis());
    }


}
