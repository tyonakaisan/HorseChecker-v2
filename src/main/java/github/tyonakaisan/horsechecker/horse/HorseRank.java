package github.tyonakaisan.horsechecker.horse;

import com.tyonakaisan.glowlib.glow.Glow;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.awt.*;


@DefaultQualifier(NonNull.class)
public final class HorseRank {

    private HorseRank() {}

    public static double calcEvaluateValue(final double paramSpeed, final double jumpHeight) {
        final double jumpRating = Math.floor(Math.pow(jumpHeight, 1.7) * 5.293 * 2.0D) / (2.0D * 5.0D);

        final double speedHeavy = 10.0D;
        final double heightHeavy = 1.0D;

        final double valueMax = 0.3375D * speedHeavy + heightHeavy;
        final double value = (paramSpeed * speedHeavy) + jumpRating * heightHeavy;
        return value / valueMax;
    }

    public static HorseRankData calcEvaluateRankData(final double paramSpeed, final double jumpHeight) {
        final double horseEvaluate = calcEvaluateValue(paramSpeed, jumpHeight);
        int alpha = 32;

        String rank;
        TextColor textColor;
        Glow.Color glowColor;

        final String[] ratingStage = {
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

        final double rate = horseEvaluate * 2.0D - 1.0;
        final int pt = (int) (rate * ratingStage.length);

        if (pt >= ratingStage.length) {
            rank = ratingStage[ratingStage.length - 1];
            textColor = TextColor.color(255, 204, 255);

            return new HorseRankData(rank, pt, textColor, new Color(textColor.red(), textColor.green(), textColor.blue(), alpha).getRGB(), Glow.Color.LIGHT_PURPLE);
        }
        if (pt < 0) {
            rank = ratingStage[0];
            textColor = TextColor.color(85, 85, 85);

            return new HorseRankData(rank, pt, textColor, new Color(textColor.red(), textColor.green(), textColor.blue(), alpha).getRGB(), Glow.Color.GRAY);
        }

        rank = ratingStage[pt];
        switch (rank) {
            case "B", "B+" -> {
                textColor = TextColor.color(85, 85, 255);
                glowColor = Glow.Color.BLUE;
            }
            case "B++" -> {
                textColor = TextColor.color(0, 170, 255);
                glowColor = Glow.Color.DARK_AQUA;
            }
            case "A" -> {
                textColor = TextColor.color(85, 255, 255);
                glowColor = Glow.Color.AQUA;
            }
            case "A+" -> {
                textColor = TextColor.color(85, 255, 85);
                glowColor = Glow.Color.GREEN;
            }
            case "A++" -> {
                textColor = TextColor.color(255, 255, 85);
                glowColor = Glow.Color.YELLOW;
            }
            case "S" -> {
                textColor = TextColor.color(255, 170, 0);
                glowColor = Glow.Color.GOLD;
            }
            case "S+" -> {
                textColor = TextColor.color(255, 85, 85);
                glowColor = Glow.Color.RED;
            }
            case "S++" -> {
                textColor = TextColor.color(255, 85, 255);
                glowColor = Glow.Color.DARK_PURPLE;
            }
            case "LEGEND" -> {
                textColor = TextColor.color(255, 204, 255);
                glowColor = Glow.Color.LIGHT_PURPLE;
            }
            default -> {
                textColor = TextColor.color(85, 85, 85);
                glowColor = Glow.Color.GRAY;
            }
        }

        return new HorseRankData(rank, pt, textColor, new Color(textColor.red(), textColor.green(), textColor.blue(), alpha).getRGB(), glowColor);
    }

    public record HorseRankData(
            String rank,
            int point,
            TextColor textColor,
            int backgroundColor,
            Glow.Color glowColor
    ) {
    }
}
