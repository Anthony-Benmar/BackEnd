package com.bbva.crons;

import javax.servlet.http.*;

import com.bbva.service.dictionary.GenerationService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InactivateDictionaryCron extends HttpServlet{
    
    public static final Logger LOGGER = Logger.getLogger(InactivateDictionaryCron.class.getName());

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        try {
            GenerationService.getInstance().desactivarAntiguos();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, getServletInfo(), e);
        }
    }
}
