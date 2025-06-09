package com.warehouse.wms.util;

import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    
    /**
     * Hash mật khẩu bằng BCrypt
     * @param password mật khẩu thô
     * @return mật khẩu đã hash
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    /**
     * Kiểm tra mật khẩu
     * @param password mật khẩu thô
     * @param hashedPassword mật khẩu đã hash
     * @return true nếu khớp
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    
    /**
     * Validate email format
     * @param email email cần kiểm tra
     * @return true nếu format hợp lệ
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate username format
     * @param username username cần kiểm tra
     * @return true nếu format hợp lệ
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Validate password strength
     * @param password mật khẩu cần kiểm tra
     * @return null nếu hợp lệ, message lỗi nếu không hợp lệ
     */
    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Mật khẩu không được để trống";
        }
        
        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }
        
        if (password.length() > 100) {
            return "Mật khẩu không được vượt quá 100 ký tự";
        }
        
        // Có thể thêm các điều kiện khác như phải có chữ hoa, chữ thường, số, ký tự đặc biệt
        
        return null; // Hợp lệ
    }
    
    /**
     * Validate họ tên
     * @param name họ tên cần kiểm tra
     * @return null nếu hợp lệ, message lỗi nếu không hợp lệ
     */
    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Họ tên không được để trống";
        }
        
        if (name.trim().length() < 2) {
            return "Họ tên phải có ít nhất 2 ký tự";
        }
        
        if (name.trim().length() > 100) {
            return "Họ tên không được vượt quá 100 ký tự";
        }
        
        return null; // Hợp lệ
    }
    
    /**
     * Sanitize input string
     * @param input chuỗi đầu vào
     * @return chuỗi đã được làm sạch
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("&", "&amp;");
    }
    
    /**
     * Kiểm tra chuỗi có rỗng hoặc null không
     * @param str chuỗi cần kiểm tra
     * @return true nếu rỗng hoặc null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Kiểm tra chuỗi có hợp lệ không (không rỗng và trong giới hạn độ dài)
     * @param str chuỗi cần kiểm tra
     * @param minLength độ dài tối thiểu
     * @param maxLength độ dài tối đa
     * @return true nếu hợp lệ
     */
    public static boolean isValidString(String str, int minLength, int maxLength) {
        if (isEmpty(str)) {
            return false;
        }
        
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
}
