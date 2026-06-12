package cloud.yunyat.controller.tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HierarchyIdGenerator {

    /**
     * 生成可读的层级唯一标识
     */
    public static String generateReadableId(String prefix, String device, String channel, String site) {
        return prefix + ":" + device + ":" + channel + ":" + site;
    }

    /**
     * 生成定长的 MD5 唯一标识（32位字符）
     */
    public static String generateShortHashId(String prefix, String device, String channel, String site) {
        String rawPath = generateReadableId(prefix, device, channel, site);
        return encodeToMd5(rawPath);
    }

    private static String encodeToMd5(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("系统不支持 MD5 算法", e);
        }
    }
}