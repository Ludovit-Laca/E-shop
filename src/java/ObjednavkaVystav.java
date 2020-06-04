/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
@WebServlet(urlPatterns = {"/ObjednavkaVystav"})
public class ObjednavkaVystav extends HttpServlet {

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session;
    Integer id_usera;

    public void ZobrazNeopravnenyPristup(PrintWriter out) {
        try {
            out.println("Neoprávnený prístup");
        } catch (Exception ex) {
            out.println(ex.toString());
        }
    }

    // vypíše objednávku
    public void vypisObjednavku(PrintWriter out) {
        if (con == null) {
            out.println("niet spojenia<BR />");
        }
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM kosik WHERE ID_pouzivatela = " + id_usera);

            double celkova_suma = 0;
            // vypočíta celkovú sumu
            while (rs.next()) {
                stmt = con.createStatement();
                celkova_suma += rs.getInt("cena") * rs.getInt("ks");
            }
            stmt = con.createStatement();

            rs = stmt.executeQuery("select * from kosik INNER JOIN sklad ON kosik.ID_tovaru = sklad.ID where (ID_pouzivatela='" + id_usera + "')");

            out.println("<div id='novinky' class='w3-row-padding w3-padding-64 w3-container'>");
            out.println("<div class='w3-content'>");
            out.println("<div class='w3-twothird'>");
            out.println("<h1 class='w3-padding-32'>" + "Váša objednávka" + "</h1>");
            out.println("");
            while (rs.next()) {
                out.println("<div class='w3-third w3-center'>");
                out.println("<img src='images/games/" + rs.getString("obrazok") + "' width='70' height='90' >");
                out.println("</div>");
                out.println("<h5>" + rs.getString("nazov") + " </h5>");
                out.println("<div class='w3-text-grey '>" + rs.getString("cena") + " EUR  -  " + rs.getString("ks") + " ks </div><br /><br />");
            }
            out.println("<strong><hr></strong>");

            out.println("<h5><strong>Meno a priezvisko:</strong> " + session.getAttribute("meno") + "</h5>");
            out.println("<h5><strong>Adresa:</strong> " + session.getAttribute("adresa") + "</h5>");
            out.println("<h5><strong>Email:</strong> " + session.getAttribute("mail") + "</h5>");
            out.println("<hr>");
            out.println("<h5>Celková suma: " + (int) celkova_suma + " EUR</h5>");
            double cena_po_zlave = celkova_suma - ((celkova_suma / 100) * (int) session.getAttribute("zlava"));
            // ulozi cenu po zlave do session
            session.setAttribute("cena_po_zlave", cena_po_zlave);
            out.println("<h5>Zľava: " + session.getAttribute("zlava") + " %</h5>");
            out.println("<h5><strong>Suma po zľave: " + cena_po_zlave + " EUR</strong></h5>");
            out.println("<form action='Objednaj' method='post'>");
            out.print("<input class='w3-button w3-black w3-padding-large w3-large w3-margin-top' type='submit' value='Objednať'>");
            out.println("</form>");

            out.println("</div>");
            out.println("</div>");
            out.println("</div>");

            stmt.close();
        } catch (Exception e) {
            System.out.println("Problém s čítaním " + e.toString());
        }
    }

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
            // načíta id usera a connection ak existuje session
            session = request.getSession();
            id_usera = (Integer) session.getAttribute("ID");
            con = (Connection) session.getAttribute("conn");

            if (id_usera == null) {
                ZobrazNeopravnenyPristup(out);
                return;
            }

            if (request.getMethod().equals("POST")) {
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

                String meno = (String) session.getAttribute("meno");

                out.println("<div class='w3-top'>");
                out.println("<div class='w3-bar w3-red w3-card w3-left-align w3-large'>");
                out.println("<a class='w3-bar-item w3-button w3-hide-medium w3-hide-large w3-right w3-padding-large w3-hover-white w3-large w3-red' href='javascript:void(0);' onclick='myFunction()' title='Toggle Navigation Menu'><i class='fa fa-bars'></i></a>");
                out.println("<a href='main' class='w3-bar-item w3-button w3-padding-large w3-white'>Obchod</a>");

                // meno
                out.println("<a href='user_objednavky' class='w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white'>" + meno + "</a>");
                out.println("<a href='#footer' class='w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white'>Kontakt</a>");

                // odhlasenie
                out.println("<a href='logout' class='w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white ' >Odhlásenie</a>");

                // kosik
                out.println("<a href='kosik' class='w3-bar-item w3-hide-small w3-padding-large '><img src=\"images/shopping_cart.png\" width=\"20\" height=\"15\" ></a>");
                out.println("</div>");

                out.println("<div id='navDemo' class='w3-bar-block w3-white w3-hide w3-hide-large w3-hide-medium w3-large'>");
                out.println("<a href='user_objednavky' class='w3-bar-item w3-button w3-padding-large'>" + meno + "</a>");
                out.println("<a href='#footer' class='w3-bar-item w3-button w3-padding-large'>Kontakt</a>");
                out.println("<a href='logout' class='w3-bar-item w3-button w3-padding-large'>Odhlásenie</a>");
                out.println("<a href='kosik' class='w3-bar-item w3-button w3-padding-large'>Košík</a>");
                out.println("</div>");
                out.println("</div>");

                out.println("<header class='w3-container w3-red w3-center' style='padding:128px 16px' >");
                out.println("<h1 class='w3-margin w3-jumbo'>ProGamerShop.sk</h1>");
                out.println("</header>");
                vypisObjednavku(out);

                out.println("<div class='w3-container w3-black w3-center w3-opacity w3-padding-64'>");
                out.println("<h1 class='w3-margin w3-xlarge'>Objednaj to konečne!</h1>");
                out.println("</div>");

                out.println("<footer id='footer' class='w3-container w3-padding-64 w3-center w3-opacity'>");
                out.println("<div class='w3-xlarge w3-padding-32'>");
                out.println("<a href='https://www.facebook.com/nitraprogamingshop/' target='_blank'><i class='fa fa-facebook-official w3-hover-opacity' ></i></a>");
                out.println("<a href='https://twitter.com/ProGamingShopsk' target='_blank'><i class='fa fa-instagram w3-hover-opacity'></i></a>");
                out.println("<a href='https://www.instagram.com/progamingshop_sk/' target='_blank'><i class='fa fa-twitter w3-hover-opacity'></i></a>");
                out.println("</div>");
                out.println("</footer>");

                out.println("<script>");
                // Used to toggle the menu on small screens when clicking on the menu button
                out.println("function myFunction() {");
                out.println("var x = document.getElementById('navDemo');");
                out.println("if (x.className.indexOf('w3-show') == -1) {");
                out.println("x.className += ' w3-show';");
                out.println("} else {");
                out.println("x.className = x.className.replace(' w3-show', '');");
                out.println("}");
                out.println("}");
                out.println("</script>");
                out.println("</body>");

            } else {
                ZobrazNeopravnenyPristup(out);
                return;
            }
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
