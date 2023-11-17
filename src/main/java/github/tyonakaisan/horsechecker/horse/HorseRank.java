package github.tyonakaisan.horsechecker.horse;

import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.awt.Color;


@DefaultQualifier(NonNull.class)
public final class HorseRank {

    private HorseRank() {
        throw new AssertionError();
    }

    public static double calcEvaluateValue(double paramSpeed, double jumpHeight) {
        double jumpRating = Math.floor(Math.pow(jumpHeight, 1.7) * 5.293 * 2.0D) / (2.0D * 5.0D);

        final double speedHeavy = 10.0D;
        final double heightHeavy = 1.0D;

        final double valueMax = 0.3375D * speedHeavy + heightHeavy;
        double value = (paramSpeed * speedHeavy) + jumpRating * heightHeavy;
        return value / valueMax;
    }

    public static HorseRankData calcEvaluateRankData(double paramSpeed, double jumpHeight) {
        double horseEvaluate = calcEvaluateValue(paramSpeed, jumpHeight);
        String rank;
        TextColor textColor;
        int alpha = 32;

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

        double rate = horseEvaluate * 2.0D - 1.0;
        int pt = (int) (rate * ratingStage.length);

        if (pt >= ratingStage.length) {
            rank = ratingStage[ratingStage.length - 1];
            textColor = TextColor.color(255, 204, 255);

            return new HorseRankData(rank, textColor, new Color(textColor.red(), textColor.green(), textColor.blue(), alpha).getRGB());
        }
        if (pt < 0) {
            rank = ratingStage[0];
            textColor = TextColor.color(85, 85, 85);

            return new HorseRankData(rank, textColor, new Color(textColor.red(), textColor.green(), textColor.blue(), alpha).getRGB());
        }

        rank = ratingStage[pt];
        switch (rank) {
            case "B", "B+" -> textColor = TextColor.color(85, 85, 255);
            case "B++" -> textColor = TextColor.color(0, 170, 255);
            case "A" -> textColor = TextColor.color(85, 255, 255);
            case "A+" -> textColor = TextColor.color(85, 255, 85);
            case "A++" -> textColor = TextColor.color(255, 255, 85);
            case "S" -> textColor = TextColor.color(255, 170, 0);
            case "S+" -> textColor = TextColor.color(255, 85, 85);
            case "S++" -> textColor = TextColor.color(255, 85, 255);
            case "LEGEND" -> textColor = TextColor.color(255, 204, 255);
            default -> textColor = TextColor.color(85, 85, 85);
        }

        return new HorseRankData(rank, textColor, new Color(textColor.red(), textColor.green(), textColor.blue(), alpha).getRGB());
    }

    public record HorseRankData(
            String rank,
            TextColor textColor,
            int BackgroundColor
    ) {}
}
