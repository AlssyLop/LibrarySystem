/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author Usuario
 */

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ConnectionDB {

   private static Connection conn = null;

   public static Connection connect() {
      try {
         if (conn == null || conn.isClosed()) {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/LibraryDB");
            conn = ds.getConnection();
         }
      } catch (SQLException e) {
         System.out.println("Error SQL: " + e);
      } catch (Exception e) {
         System.out.println("Error DataSource: " + e);
      }
      return conn;
   }

   public static void disconnect() {
      try {
         if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Conexión cerrada");
         }
      } catch (SQLException e) {
         System.out.println("Error al cerrar conexión: " + e);
      }
   }
}
