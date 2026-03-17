package com.mygate.visitor.util;

import java.util.HashMap;
import java.util.Map;

public class DepartmentMapper {
    
    private static final Map<String, String> FULL_NAME_TO_CODE = new HashMap<>();
    private static final Map<String, String> CODE_TO_FULL_NAME = new HashMap<>();
    
    static {
        // Map full department names to short codes
        addMapping("Computer Science & Engineering", "CSE");
        addMapping("Computer Science and Engineering", "CSE");
        addMapping("Electronics & Communication", "ECE");
        addMapping("Electronics and Communication", "ECE");
        addMapping("Electronics & Communication Engineering", "ECE");
        addMapping("Electronics and Communication Engineering", "ECE");
        addMapping("Information Technology", "IT");
        addMapping("AI & Data Science", "AIDS");
        addMapping("AI and Data Science", "AIDS");
        addMapping("AI & Machine Learning", "AIML");
        addMapping("AI and Machine Learning", "AIML");
        addMapping("Mechanical Engineering", "MECH");
        addMapping("Civil Engineering", "CCE");
        addMapping("Civil Engineering", "CIVIL");
        addMapping("Electrical & Electronics Engineering", "EEE");
        addMapping("Electrical and Electronics Engineering", "EEE");
        addMapping("Computer Science & Business Systems", "CSBS");
        addMapping("Computer Science and Business Systems", "CSBS");
        addMapping("VLSI Design", "VLSI");
        addMapping("Administration", "ADMIN");
        addMapping("Library", "LIBRARY");
        addMapping("Other", "OTHER");
        
        // Also map the codes to themselves for direct lookup
        addMapping("CSE", "CSE");
        addMapping("ECE", "ECE");
        addMapping("EEE", "EEE");
        addMapping("IT", "IT");
        addMapping("AIDS", "AIDS");
        addMapping("AIML", "AIML");
        addMapping("MECH", "MECH");
        addMapping("CCE", "CCE");
        addMapping("CIVIL", "CIVIL");
        addMapping("CSBS", "CSBS");
        addMapping("VLSI", "VLSI");
        addMapping("ADMIN", "ADMIN");
        addMapping("LIBRARY", "LIBRARY");
        addMapping("OTHER", "OTHER");
    }
    
    private static void addMapping(String fullName, String code) {
        FULL_NAME_TO_CODE.put(fullName.toLowerCase(), code);
        CODE_TO_FULL_NAME.put(code.toLowerCase(), fullName);
    }
    
    /**
     * Convert full department name to short code
     * @param departmentName Full name or code
     * @return Short code (e.g., "CSE")
     */
    public static String toShortCode(String departmentName) {
        if (departmentName == null || departmentName.trim().isEmpty()) {
            return null;
        }
        
        String normalized = departmentName.trim().toLowerCase();
        
        // Check if it's already a code
        if (CODE_TO_FULL_NAME.containsKey(normalized)) {
            return departmentName.trim().toUpperCase();
        }
        
        // Convert full name to code
        String code = FULL_NAME_TO_CODE.get(normalized);
        return code != null ? code : departmentName.trim().toUpperCase();
    }
    
    /**
     * Convert short code to full department name
     * @param code Short code (e.g., "CSE")
     * @return Full name (e.g., "Computer Science & Engineering")
     */
    public static String toFullName(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        String normalized = code.trim().toLowerCase();
        String fullName = CODE_TO_FULL_NAME.get(normalized);
        return fullName != null ? fullName : code.trim();
    }
    
    /**
     * Check if two department names/codes refer to the same department
     * @param dept1 First department name or code
     * @param dept2 Second department name or code
     * @return true if they refer to the same department
     */
    public static boolean isSameDepartment(String dept1, String dept2) {
        if (dept1 == null || dept2 == null) {
            return false;
        }
        
        String code1 = toShortCode(dept1);
        String code2 = toShortCode(dept2);
        
        return code1 != null && code1.equalsIgnoreCase(code2);
    }
}
