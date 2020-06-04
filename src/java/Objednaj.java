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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@WebServlet(urlPatterns = {"/Objednaj"})
public class Objednaj extends HttpServlet {

    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session;
    Integer id_usera;
    DateFormat dateFormat;
    Date date;

    public void ZobrazNeopravnenyPristup(PrintWriter out) {
        try {
            out.println("Neoprávnený prístup");
        } catch (Exception ex) {
            out.println(ex.toString());
        }
    }
    
    // zisti dostatok tovaru
    public boolean DostatokTovaru() {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `kosik` INNER JOIN sklad ON kosik.ID_tovaru = sklad.ID WHERE ID_pouzivatela = " + id_usera);

            while (rs.next()) {
                int pocet_kusov_sklad = rs.getInt("sklad.ks");
                int pocet_kusov_kosik = rs.getInt("kosik.ks");

                if (pocet_kusov_kosik > pocet_kusov_sklad) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public void ZapisTovar() {
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM `kosik` INNER JOIN sklad ON kosik.ID_tovaru = sklad.ID WHERE ID_pouzivatela = " + id_usera);

            dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            date = new Date();
            // vygeneruje číslo objednávky pomocou id usera a presného dátumu kedy to objednal
            String cislo_objednavky = id_usera + dateFormat.format(date);
            
            // zapíše produkty z košíka do obj_polozky
            while (rs.next()) {
                stmt = con.createStatement();
                String sstr = "insert into obj_polozky (obj_cislo , id_tovaru, cena, ks) values ("
                        + "'" + cislo_objednavky + "', "
                        + "'" + rs.getString("ID_tovaru") + "', "
                        + "'" + rs.getString("cena") + "', "
                        + "'" + rs.getString("kosik.ks") + "') ";
                stmt.executeUpdate(sstr);
                
                // zmení počet kusov na sklade
                int zmena_poctu_kusov = rs.getInt("sklad.ks") - rs.getInt("kosik.ks");
                stmt = con.createStatement();
                sstr = "UPDATE sklad SET ks = " + zmena_poctu_kusov + " WHERE ID = " + rs.getString("ID_tovaru");
                stmt.executeUpdate(sstr);
            }
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = new Date();
            
            // zapíše objednávku do obj_zoznam
            stmt = con.createStatement();
            String sstr = "insert into obj_zoznam (obj_cislo, datum_objednavky, ID_pouzivatela, suma, stav) values ("
                    + "'" + cislo_objednavky + "', "
                    + "'" + dateFormat.format(date) + "', "
                    + "'" + id_usera + "', "
                    + "'" + session.getAttribute("cena_po_zlave") + "', "
                    + "'" + "spracovana" + "') ";
            stmt.executeUpdate(sstr);
            
            // vymaže košík podľa id usera
            stmt = con.createStatement();
            sstr = "DELETE FROM kosik WHERE ID_pouzivatela = " + id_usera;
            stmt.executeUpdate(sstr);

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
    
    // synchronizované objednávanie
    public boolean Objednavam() {
        try {
            synchronized (this) {
                if (DostatokTovaru()) {
                    ZapisTovar();
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
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

                out.println("<header class='w3-container w3-red w3-center' style='padding:128px 16px' >");
                out.println("<h1 class='w3-margin w3-jumbo'>ProGamerShop.sk</h1>");
                if (Objednavam()) {
                    out.println("<p class='w3-xlarge'>Ďakujeme za Vašu objednávku :)</p>");
                    out.println("<a href='main' class='w3-button w3-black w3-padding-large w3-large w3-margin-top'>Späť do obchodu</a> <br />");
                    out.println("</header>");
                } else {
                    out.println("<p class='w3-xlarge'>Nedostatok tovaru na sklade :(</p>");
                    out.println("<a href='main' class='w3-button w3-black w3-padding-large w3-large w3-margin-top'>Späť do obchodu</a> <br />");
                    out.println("</header>");
                }
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
