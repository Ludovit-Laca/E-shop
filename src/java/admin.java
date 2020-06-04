/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
@WebServlet(urlPatterns = {"/admin"})
public class admin extends HttpServlet {

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
    
    // vypiše všetky objednávky a použivateľov
    public void VypisVsetkyObjednavky(PrintWriter out) {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM obj_zoznam INNER JOIN pouzivatelia ON obj_zoznam.ID_pouzivatela = pouzivatelia.ID");
            out.println("<h1 class='w3-padding-32'>" + "Objednávky" + "</h1>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Číslo objednávky</th>");
            out.println("<th>Dátum objednávky</th>");
            out.println("<th>Meno a priezvisko</th>");
            out.println("<th>Suma</th>");
            out.println("<th>Stav</th>");
            out.println("<th>Zmena stavu</th>");
            out.println("<th></th>");
            out.println("</tr>");
            // vypisuje objednávky
            while (rs.next()) {          
                out.println("<tr>");
                out.println("<td>" + rs.getString("obj_cislo") + "</td>");
                out.println("<td>" + rs.getDate("datum_objednavky") + "</td>");
                out.println("<td>" + rs.getString("meno") + " " + rs.getString("priezvisko") + "</td>");
                out.println("<td>" + rs.getDouble("suma") + "</td>");
                out.println("<td>" + rs.getString("stav") + "</td>");
                out.println("<form action = 'admin' method = 'post'><td>");
                out.println("<input type='hidden' name='obj_cislo' value='" + rs.getString("obj_cislo") + "'>");
                out.println("<select id='novy_stav' name='novy_stav'>");
                out.println("<option value='spracovana'>spracovana</option>");
                out.println("<option value='odoslana'>odoslana</option>");
                out.println("<option value='zaplatena'>zaplatena</option>");
                out.println("</select>");
                out.println("</td>");
                out.println("<td><button class = 'w3-black' name = 'odosli' value='update' type = 'submit'>Odoslať</button>");
                out.println("&emsp;<button class = 'w3-black' name = 'odosli' value='delete' type = 'submit'>x</button></td></form>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<h1 class='w3-padding-32'>" + "Používatelia" + "</h1>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<th>Meno a priezvisko</th>");
            out.println("<th>Adresa</th>");
            out.println("<th>Mail</th>");
            out.println("<th>Zľava</th>");
            out.println("<th>Práva</th>");
            out.println("<th>Zmena práv</th>");
            out.println("<th></th>");
            out.println("</tr>");

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM pouzivatelia");
            // vypisuje použivatelov
            while (rs.next()) {                          
                out.println("<tr>");
                out.println("<td>" + rs.getString("meno") + " " + rs.getString("priezvisko") + "</td>");
                out.println("<td>" + rs.getString("adresa") + "</td>");
                out.println("<td>" + rs.getString("mail") + "</td>");
                out.println("<td>" + rs.getInt("zlava") + "</td>");
                out.println("<td>" + rs.getString("poznamky") + "</td>");
                out.println("<form action = 'admin' method = 'post'><td>");
                out.println("<input type='hidden' name='ID_uzivatel' value='" + rs.getString("ID") + "'>");
                out.println("<select id='zmena_prava' name='zmena_prava'>");
                out.println("<option value='user'>user</option>");
                out.println("<option value='admin'>admin</option>");
                out.println("</select>");
                out.println("</td>");
                out.println("<td><button class = 'w3-black' name = 'odosli' value='update2' type = 'submit'>Odoslať</button>");
                out.println("&emsp;<button class = 'w3-black' name = 'odosli' value='delete2' type = 'submit'>x</button></td></form>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("<h1 class='w3-padding-32'></h1>");
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
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
            // načíta id usera a connection ak existuje session
            session = request.getSession();
            id_usera = (Integer) session.getAttribute("ID");
            con = (Connection) session.getAttribute("conn");

            if (id_usera == null) {
                ZobrazNeopravnenyPristup(out);
                return;
            }
            
            // pusti len admina
            if (!(session.getAttribute("poznamky").equals("admin"))) {   
                ZobrazNeopravnenyPristup(out);
                return;
            } 

            if (request.getMethod().equals("POST")) {
                try {
                    // updatne stav objednávky alebo vymaže objednávku
                    // updatne práva použivateľa alebo ho vymaže
                    if (request.getParameter("odosli").equals("update")) {             
                        stmt = con.createStatement();
                        stmt.executeUpdate("UPDATE obj_zoznam SET stav = '" + request.getParameter("novy_stav")
                                + "' WHERE obj_cislo = " + request.getParameter("obj_cislo"));
                    } else if (request.getParameter("odosli").equals("delete")) {
                        stmt = con.createStatement();
                        stmt.executeUpdate("DELETE FROM obj_zoznam WHERE obj_cislo = " + request.getParameter("obj_cislo"));
                        stmt = con.createStatement();
                        stmt.executeUpdate("DELETE FROM obj_polozky WHERE obj_cislo = " + request.getParameter("obj_cislo"));
                    } else if (request.getParameter("odosli").equals("update2")) {
                        stmt = con.createStatement();
                        stmt.executeUpdate("UPDATE pouzivatelia SET poznamky = '" + request.getParameter("zmena_prava")
                                + "' WHERE ID = " + request.getParameter("ID_uzivatel"));
                    } else if (request.getParameter("odosli").equals("delete2")) {
                        stmt = con.createStatement();
                        stmt.executeUpdate("DELETE FROM pouzivatelia WHERE ID = " + request.getParameter("ID_uzivatel"));
                    }
                    response.sendRedirect("admin");
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            out.println("<!DOCTYPE html>");
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

            out.println("table { font-family: arial, sans-serif; border-collapse: collapse; width: 100%; }");
            out.println("td, th { border: 1px solid #dddddd; text-align: left; padding: 8px; }");
            out.println("tr:nth-child(even) { background-color: #dddddd; }");

            out.println("</style>");
            out.println("<body>");

            String meno = (String) session.getAttribute("meno");

            out.println("<div class='w3-top'>");
            out.println("<div class='w3-bar w3-red w3-card w3-left-align w3-large'>");
            out.println("<a class='w3-bar-item w3-button w3-hide-medium w3-hide-large w3-right w3-padding-large w3-hover-white w3-large w3-red' href='javascript:void(0);' onclick='myFunction()' title='Toggle Navigation Menu'><i class='fa fa-bars'></i></a>");

            // meno
            out.println("<a href='admin' class='w3-bar-item w3-button w3-padding-large w3-white'>" + meno + "</a>");

            // odhlasenie
            out.println("<a href='logout' class='w3-bar-item w3-button w3-hide-small w3-padding-large w3-hover-white ' >Odhlásenie</a>");

            out.println("</div>");

            out.println("<div id='navDemo' class='w3-bar-block w3-white w3-hide w3-hide-large w3-hide-medium w3-large'>");
            out.println("<a href='logout' class='w3-bar-item w3-button w3-padding-large'>Odhlásenie</a>");
            out.println("</div>");
            out.println("</div>");

            out.println("<header class='w3-container w3-red w3-center' style='padding:128px 16px' >");
            out.println("<h1 class='w3-margin w3-jumbo'>Vitajte admin " + meno + "</h1>");
            out.println("<p class='w3-xlarge'>Admin rozhranie</p>");
            out.println("</header>");

            VypisVsetkyObjednavky(out);

            out.println("<div class='w3-container w3-black w3-center w3-opacity w3-padding-64'>");
            out.println("<h1 class='w3-margin w3-xlarge'>King of the Jungle</h1>");
            out.println("</div>");

            out.println("<footer id = 'footer' class='w3-container w3-padding-64 w3-center w3-opacity'>");
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
