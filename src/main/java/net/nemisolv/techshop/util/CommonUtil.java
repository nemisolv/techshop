package net.nemisolv.techshop.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Công cụ chung
 */
public class CommonUtil {

    public static final String NUMBER_DIGITS = "0123456789";

    /**
     * Đổi tên bằng UUID
     *
     * @param fileName Tên tệp
     * @return Tên đã được định dạng
     */
    public static String generateUniqueFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }


    /**
     * Tạo số ngẫu nhiên 6 chữ số
     */
    public static String getRandomNum() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            int num = ThreadLocalRandom.current().nextInt(NUMBER_DIGITS.length());
            sb.append(NUMBER_DIGITS.charAt(num));
        }
        return sb.toString();
    }

    /**
     * Lấy chuỗi đặc biệt + 6 chữ số ngẫu nhiên
     *
     * @return
     */
    public static String generateSpecialString(String str) {
        return str + getRandomNum();
    }
}