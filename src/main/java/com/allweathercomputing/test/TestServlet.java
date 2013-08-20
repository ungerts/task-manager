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

package com.allweathercomputing.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.allweathercomputing.htm.org.IOrganizationManager;
import com.htm.exceptions.HumanTaskManagerException;
import com.htm.utils.JEEUtils;
import com.htm.utils.Utilities;

@WebServlet(name = "TestServlet", urlPatterns = "/test")
public class TestServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = -7397824565311693906L;

    @EJB
    private TestBean bean;

    private Logger log;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        this.log = Utilities.getLogger(this.getClass());
        // TODO Auto-generated method stub
        // super.doGet(req, resp);
        /*
           * Get the value of form parameter
           */
        // String name = req.getParameter("name");

        String welcomeMessage = "Welcome";
        /*
           * Set the content type(MIME Type) of the response.
           */
        resp.setContentType("text/html");
        // req.getUserPrincipal().

        PrintWriter out = resp.getWriter();
        /*
           * Write the HTML to the response
           */
        out.println("<html>");
        out.println("<head>");
        out.println("<title> A very simple servlet example</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>" + welcomeMessage + "</h1>");
        out.println("<p>" + req.getUserPrincipal().getName() + "</p>");
        out.println("<p>" + req.isUserInRole("toll") + "</p>");

        try {
            InitialContext ic = new InitialContext();

            IOrganizationManager bean1 = (IOrganizationManager) ic
                    .lookup("java:global/TaskManagerTest/OrganizationManagerBean");
            out.println("<p>" + bean1.getClass().getName() + "</p>");

            NamingEnumeration<NameClassPair> list = ic.list("java:comp/env");
            NameClassPair pair;
            while (list.hasMore()) {
                pair = list.next();
                out.println("<p>" + pair.getClassName() + "</p>");
                out.println("<p>" + pair.getName() + "</p>");
                out.println("<p>" + pair.getNameInNamespace() + "</p>");
                out.println("<p>" + pair.isRelative() + "</p>");
            }

        } catch (Exception e) {
            out.println("<p>" + e.getMessage() + "</p>");
        }

        try {

            out.println("<p>" + bean.testSessionContext() + "</p>");

        } catch (Exception e) {
            out.println("<p>" + e.getMessage() + "</p>");
        }

        try {

            out.println("<p>" + bean.testOrgManagerBean() + "</p>");

        } catch (Exception e) {
            out.println("<p>" + e.getMessage() + "</p>");
        } // catch (HumanTaskManagerException e) {
        // out.println("<p>" + e.getMessage() + "</p>");
        // }
        // out.println("<a href="/servletexample/pages/form.html">"+"Click here to go back to input page "+"</a>");

        try {
            out.println("<p>"
                    + JEEUtils.getOrganizationQueryManagerBean().getClass()
                    .getName() + "</p>");
        } catch (HumanTaskManagerException e) {
            out.println("<p>" + e.getMessage() + "</p>");
        }

        out.println("</body>");
        out.println("</html>");
        out.close();
        log.debug("Hallo");
    }

}