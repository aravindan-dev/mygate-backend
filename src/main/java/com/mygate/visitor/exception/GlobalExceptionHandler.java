package com.mygate.visitor.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseConnectionException(DatabaseConnectionException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "DATABASE_ERROR");
        error.put("message", "Failed to connect to remote database on Windows server");
        error.put("details", ex.getMessage());
        error.put("server", "10.10.7.20:3306");
        error.put("suggestion", "Check if Windows MySQL server is running and accessible");
        
        System.err.println("❌ Database Connection Error: " + ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "DATA_ACCESS_ERROR");
        error.put("message", "Database operation failed");
        error.put("details", ex.getMessage());
        
        System.err.println("❌ Data Access Error: " + ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> handleSQLException(SQLException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "SQL_ERROR");
        error.put("message", "SQL operation failed");
        error.put("sqlState", ex.getSQLState());
        error.put("errorCode", ex.getErrorCode());
        error.put("details", ex.getMessage());
        
        System.err.println("❌ SQL Error: " + ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "ERROR");
        error.put("message", "An unexpected error occurred");
        error.put("details", ex.getMessage());
        
        System.err.println("❌ Unexpected Error: " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
