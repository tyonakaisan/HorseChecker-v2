package github.tyonakaisan.horsechecker.utils;

import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.awt.Color;

@DefaultQualifier(NonNull.class)
public final class HorseRank {

    public static double calcEvaluateValue(double paramSpeed, double jumpHeight) {
        double jumpRating = Math.floor(Math.pow(jumpHeight, 1.7) * 5.293 * 2.0D) / (2.0D * 5.0D);

        final double speedHeavy = 10.0D;
        final double heightHeavy = 1.0D;

        final double valueMax = 0.3375D * speedHeavy + heightHeavy;
        double value = (paramSpeed * speedHeavy) + jumpRating * heightHeavy;
        return value / valueMax;
    }

    public static String calcEvaluateRankString(double paramSpeed, double jumpHeight) {
        double horseEvaluate = calcEvaluateValue(paramSpeed, jumpHeight);

        final String [] rankString = {
                "G", "G", "G",
                "F", "F", "F",
                "E", "E", "E",
                "D", "D", "D",
                "C", "C+", "C++",
                "B", "B+", "B++",
                "A", "A+", "A++",
                "S", "S+", "S++",
                "LEGEND"
        };

        double rate = horseEvaluate * 2.0D - 1.0;

        int pt = (int)(rate * rankString.length);
        if (pt >= rankString.length) {
            return rankString[rankString.length-1];
        }
        if (pt < 0) {
            return rankString[0];
        }

        return rankString[pt];
    }

    public static TextColor calcEvaluateRankColor(String rankString) {

        return switch (rankString) {
            case "B", "B+" -> TextColor.color(85, 85, 255);
            case "B++" -> TextColor.color(0, 170, 255);
            case "A" -> TextColor.color(85, 255, 255);
            case "A+" -> TextColor.color(85, 255, 85);
            case "A++" -> TextColor.color(255, 255, 85);
            case "S" -> TextColor.color(255, 170, 0);
            case "S+" -> TextColor.color(255, 85, 85);
            case "S++" -> TextColor.color(255, 85, 255);
            case "LEGEND" -> TextColor.color(255, 204, 255);
            default -> TextColor.color(85, 85, 85);
        };
    }

    public static int calcEvaluateRankBackgroundColor(String rankString) {
        int alpha = 32;

        return switch (rankString) {
            case "B", "B+" -> new Color(85, 85, 255, alpha).getRGB();
            case "B++" -> new Color(0, 170, 255, alpha).getRGB();
            case "A" -> new Color(85, 255, 255, alpha).getRGB();
            case "A+" -> new Color(85, 255, 85, alpha).getRGB();
            case "A++" -> new Color(255, 255, 85, alpha).getRGB();
            case "S" -> new Color(255, 170, 0, alpha).getRGB();
            case "S+" -> new Color(255, 85, 85, alpha).getRGB();
            case "S++" -> new Color(255, 85, 255, alpha).getRGB();
            case "LEGEND" -> new Color(255, 204, 255, alpha).getRGB();
            default -> new Color(85, 85, 85, alpha).getRGB();
        };
    }

}
