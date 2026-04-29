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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectionDB {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionDB.class);

   private static final Properties props = new Properties();

   static {
      try (InputStream input = ConnectionDB.class.getClassLoader().getResourceAsStream("database.properties")) {
         if (input != null) {
            props.load(input);
         } else {
            logger.info("Advertencia: No se encontró el archivo database.properties en resources.");
         }
      } catch (Exception e) {
         logger.error("Error cargando database.properties: ", e);
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
            logger.info("Conexión a base de datos OK");
         }
      } catch (SQLException e) {
         logger.error("Error SQL: ", e);
      } catch (ClassNotFoundException e) {
         logger.error("Driver no encontrado: ", e);
      } catch (Exception e) {
         logger.error("Error general: ", e);
      }
      return conn;
   }

   public static void disconnect() {
      try {
         if (conn != null && !conn.isClosed()) {
            conn.close();
            logger.info("Conexión cerrada");
         }
      } catch (SQLException e) {
         logger.error("Error al cerrar conexión: ", e);
      }
   }
}
