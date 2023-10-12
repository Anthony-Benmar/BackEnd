package com.bbva.jetty;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class MainAppErrorHandler extends ErrorHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        PrintWriter writer = response.getWriter();
        int status = response.getStatus();
        var encodedUri = StringEscapeUtils.escapeHtml4(request.getRequestURI());

        var jsonResponse = new JSONObject()
                .put("target", encodedUri)
                .put("status", status)
                .put("success", false)
                .put("message", "Ocurrio un error!")
                .put("message error", response.getOutputStream().toString())
                .toString();

        response.setContentType(MediaType.APPLICATION_JSON);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        writer.write(jsonResponse);
    }
}