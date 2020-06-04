/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author lacal
 */
@WebServlet(urlPatterns = {"/logout"})
public class logout extends HttpServlet {

    HttpSession session;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            // vymaze session, nebude viac pristupna
            session = request.getSession();
            session.invalidate();
            out.println("<html lang='en'>");
            out.println("<title>ProGamerShop.sk</title>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<link rel='stylesheet' href='https://www.w3schools.com/w3css/4/w3.css'>");
            out.println("<link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Lato'>");
            out.println("<link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Montserrat'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css'>");
            out.println("<style>");
            out.println("body,h1,h2,h3,h4,h5,h6 {font-family: 'Lato', sans-serif}");
            out.println(".w3-bar,h1,button {font-family: 'Montserrat', sans-serif}");
            out.println(".fa-anchor,.fa-coffee {font-size:200px}");
            out.println("</style>");
            out.println("<body>");

            out.println("<header class='w3-container w3-red w3-center' style='padding:128px 16px' >");
            out.println("<h1 class='w3-margin w3-jumbo'>ProGamerShop.sk</h1>");
            out.println("<p class='w3-xlarge'>Ďakujeme za Vašu návštevu! :)</p>");
            out.println("<a href='index.html' class='w3-button w3-black w3-padding-large w3-large w3-margin-top'>Späť na Domov</a> <br />");
            out.println("</header>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
