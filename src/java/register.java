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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lacal
 */
@WebServlet(urlPatterns = {"/register"})
public class register extends HttpServlet {

    String driver = "com.mysql.jdbc.Driver";
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    String userName = "root";
    String password = "";
    String URL = "jdbc:mysql://localhost/obchod";
    String meno, priezvisko, adresa, login, heslo, mail;

    @Override
    public void init() {
        try {
            super.init();
            Class.forName(driver);
            con = DriverManager.getConnection(URL, userName, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // zaregistuje uživateľa ak login ktorý zadal ešte neexistuje a vráti odpoveď
    public String Zaregistruj(String meno, String priezvisko, String adresa, String login, String heslo, String mail) {
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.adresa = adresa;
        this.login = login;
        this.heslo = heslo;
        this.mail = mail;
        
        // random vygeneruje zľavu pre nového použivateľa
        int zlava = (int) (1 + Math.random() * 20);

        int vysledok = 0;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("select max(id) as iid,count(id) as pocet from pouzivatelia "
                    + "where login='" + this.login + "'");

            rs.next();
            if (rs.getInt("pocet") == 0) {
                vysledok = rs.getInt("iid");

                Statement stmt = con.createStatement();
                String mySQL = "INSERT INTO pouzivatelia (login, heslo, mail, adresa, zlava, meno, priezvisko, poznamky) "
                        + "VAlUES ('" + this.login + "', '" + this.heslo + "', '" + this.mail + "', '" + this.adresa + "', '" + zlava + "', '" + this.meno + "', '" + this.priezvisko + "', 'user');";
                int riadky = stmt.executeUpdate(mySQL);
            } else {
                return "Zadaný login už existuje";
            }
            stmt.close();
        } catch (Exception ex) {
            return ex.toString();
        }
        return "Registrácia prebehla úspešne!";

    }

//**********************************************************************
    public void ZobrazNeopravnenyPristup(PrintWriter out) {

        try {
            out.println("Neoprávnený prístup");
        } catch (Exception ex) {
            out.println(ex.toString());
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
            String tlacidlo = "";
            // Ak stlačil tlačidlo na registráciu, pokúsi sa ho zaregistrovať 
            try { 
                tlacidlo = (request.getParameter("tlacidlo")).substring(0, 1);
            } catch (Exception e) {
                ZobrazNeopravnenyPristup(out);
                return;
            }
            if (tlacidlo.equals("Z")) {
                String stav = Zaregistruj(request.getParameter("meno"), request.getParameter("priezvisko"),
                        request.getParameter("adresa"), request.getParameter("login"),
                        request.getParameter("pw"), request.getParameter("email"));

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
                out.println("<p class='w3-xlarge'>" + stav + "</p>");
                out.println("<div class='w3-xlarge w3-hide-small w3-padding-large'>" + meno + " " + priezvisko + "</div> <br />");
                out.println("<a href='index.html#prihlasenie' class='w3-button w3-black w3-padding-large w3-large w3-margin-top'>Prihláste sa</a> <br />");
                out.println("</header>");

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
