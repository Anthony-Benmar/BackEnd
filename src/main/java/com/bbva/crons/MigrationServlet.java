package com.bbva.crons;

import com.google.api.client.http.HttpStatusCodes;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

public class MigrationServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(HttpStatusCodes.STATUS_CODE_OK);
            response.getWriter().print("Hello from cron job!");
        } catch (IOException e) {
            response.setStatus(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
        }
    }

}
