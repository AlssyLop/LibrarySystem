/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDB {
   private static final Properties props = new Properties();

   static {
      try (InputStream input = ConnectionDB.class.getClassLoader().getResourceAsStream("database.properties")) {
         if (input != null) {
            props.load(input);
         } else {
            System.out.println("Advertencia: No se encontró el archivo database.properties en resources.");
         }
      } catch (Exception e) {
         System.out.println("Error cargando database.properties: " + e.getMessage());
      }
   }

   private static Connection conn = null;

   public static Connection connect() {
      try {
         if (conn == null || conn.isClosed()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
               props.getProperty("db.url"), 
               props.getProperty("db.user"), 
               props.getProperty("db.password")
            );
            System.out.println("Conexión a base de datos OK");
         }
      } catch (SQLException e) {
         System.out.println("Error SQL: " + e.getMessage());
      } catch (ClassNotFoundException e) {
         System.out.println("Driver no encontrado: " + e.getMessage());
      } catch (Exception e) {
         System.out.println("Error general: " + e.getMessage());
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
         System.out.println("Error al cerrar conexión: " + e.getMessage());
      }
   }
}
