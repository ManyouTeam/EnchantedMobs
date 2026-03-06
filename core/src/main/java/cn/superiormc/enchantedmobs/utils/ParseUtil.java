package cn.superiormc.enchantedmobs.utils;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {

    /**
     * 解析单个颜色字符串为 Bukkit Color
     * 支持格式：
     * - "#RRGGBB"
     * - "R,G,B"
     * - 命名颜色：red, blue, green 等
     */
    /**
     * 解析单个颜色字符串为 Bukkit Color，支持格式：
     * - "#RRGGBB"
     * - "R,G,B"
     * - 命名颜色：red, blue, green 等
     */
    public static Color parseColor(String value) {
        value = value.trim().toLowerCase();

        if (value.startsWith("#")) {
            // 十六进制颜色 (#FF0000)
            java.awt.Color awt = java.awt.Color.decode(value);
            return Color.fromRGB(awt.getRed(), awt.getGreen(), awt.getBlue());

        } else if (value.contains(",")) {
            // RGB 格式 (255,0,0)
            String[] parts = value.split(",");
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            return Color.fromRGB(r, g, b);

        } else {
            // 命名颜色
            if (value.equals("red")) return Color.RED;
            if (value.equals("green")) return Color.GREEN;
            if (value.equals("blue")) return Color.BLUE;
            if (value.equals("white")) return Color.WHITE;
            if (value.equals("black")) return Color.BLACK;
            if (value.equals("yellow")) return Color.YELLOW;
            if (value.equals("aqua")) return Color.AQUA;
            if (value.equals("gray") || value.equals("grey")) return Color.GRAY;
            if (value.equals("lime")) return Color.LIME;
            if (value.equals("orange")) return Color.ORANGE;
            if (value.equals("purple")) return Color.PURPLE;
            if (value.equals("fuchsia") || value.equals("pink")) return Color.FUCHSIA;

            throw new IllegalArgumentException("未知颜色名: " + value);
        }
    }

    /**
     * 解析为 Color 列表
     */
    public static List<Color> parseColorList(List<String> rawList) {
        List<Color> colors = new ArrayList<>();

        for (String value : rawList) {
            try {
                colors.add(parseColor(value));
            } catch (Exception e) {
                return colors;
            }
        }

        return colors;
    }
}
