package io.github.darkkronicle.kronhud.gui.hud.vanilla;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.config.*;
import io.github.darkkronicle.kronhud.gui.component.DynamicallyPositionable;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.gui.layout.AnchorPoint;
import io.github.darkkronicle.kronhud.util.Rectangle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHud extends TextHudEntry implements DynamicallyPositionable {

    public static final Identifier ID = new Identifier("kronhud", "scoreboardhud");
    public static final ScoreboardObjective placeholder = Util.make(() -> {
        Scoreboard placeScore = new Scoreboard();
        ScoreboardObjective objective = placeScore.addObjective("placeholder", ScoreboardCriterion.DUMMY,
                Text.literal("Scoreboard"),
                ScoreboardCriterion.RenderType.INTEGER);
        ScoreboardPlayerScore dark = placeScore.getPlayerScore("DarkKronicle", objective);
        dark.setScore(8780);

        ScoreboardPlayerScore moeh = placeScore.getPlayerScore("moehreag", objective);
        moeh.setScore(743);

        ScoreboardPlayerScore kode = placeScore.getPlayerScore("TheKodeToad", objective);
        kode.setScore(2948);

        placeScore.setObjectiveSlot(1, objective);
        return objective;
    });

    private final KronExtendedColor backgroundColor = new KronExtendedColor("backgroundcolor", ID.getPath(), new ExtendedColor(0x4C000000, ExtendedColor.ChromaOptions.getDefault()));
    private final KronExtendedColor topColor = new KronExtendedColor("topbackgroundcolor", ID.getPath(), new ExtendedColor(0x66000000, ExtendedColor.ChromaOptions.getDefault()));
    private final KronInteger topPadding = new KronInteger("toppadding", ID.getPath(), 0, 0, 4);
    private final KronBoolean scores = new KronBoolean("scores", ID.getPath(), true);
    private final KronColor scoreColor = new KronColor("scorecolor", ID.getPath(), new Color(0xFFFF5555));
    private final KronOptionList<AnchorPoint> anchor = DefaultOptions.getAnchorPoint(AnchorPoint.MIDDLE_RIGHT);

    public ScoreboardHud() {
        super(200, 146, true);
    }

    @Override
    public void render(MatrixStack matrices, float delta) {
        matrices.push();
        scale(matrices);
        renderComponent(matrices, delta);
        matrices.pop();
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        Scoreboard scoreboard = this.client.world.getScoreboard();
        ScoreboardObjective scoreboardObjective = null;
        Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
        if (team != null) {
            int t = team.getColor().getColorIndex();
            if (t >= 0) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(3 + t);
            }
        }

        ScoreboardObjective scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
        if (scoreboardObjective2 != null) {
            this.renderScoreboardSidebar(matrices, scoreboardObjective2);
        }
    }

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        renderScoreboardSidebar(matrices, placeholder);
    }

    // Abusing this could break some stuff/could allow for unfair advantages. The goal is not to do this, so it won't
    // show any more information than it would have in vanilla.
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> filteredScores = scores.stream().filter((testScore) ->
                testScore.getPlayerName() != null && !testScore.getPlayerName().startsWith("#")
        ).collect(Collectors.toList());

        if (filteredScores.size() > 15) {
            scores = Lists.newArrayList(Iterables.skip(filteredScores, scores.size() - 15));
        } else {
            scores = filteredScores;
        }

        List<Pair<ScoreboardPlayerScore, Text>> scoresWText = Lists.newArrayListWithCapacity(scores.size());
        Text text = objective.getDisplayName();
        int displayNameWidth = client.textRenderer.getWidth(text);
        int maxWidth = displayNameWidth;
        int spacerWidth = client.textRenderer.getWidth(": ");

        ScoreboardPlayerScore scoreboardPlayerScore;
        MutableText formattedText;
        for (
                Iterator<ScoreboardPlayerScore> scoresIterator = scores.iterator();
                scoresIterator.hasNext();
                maxWidth = Math.max(maxWidth, client.textRenderer.getWidth(formattedText) + spacerWidth + client.textRenderer.getWidth(Integer.toString(scoreboardPlayerScore.getScore())))
        ) {
            scoreboardPlayerScore = scoresIterator.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            formattedText = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
            scoresWText.add(Pair.of(scoreboardPlayerScore, formattedText));
        }
        maxWidth = maxWidth + 6;

        int scoresSize = scores.size();
        int scoreHeight = scoresSize * 9;
        int fullHeight = scoreHeight + 11 + topPadding.getValue() * 2;

        boolean updated = false;
        if (fullHeight + 1 != height) {
            setHeight(fullHeight + 1);
            updated = true;
        }
        if (maxWidth + 1 != width) {
            setWidth(maxWidth + 1);
            updated = true;
        }
        if (updated) {
            onBoundsUpdate();
        }

        Rectangle bounds = getBounds();

        int renderX = bounds.x() + bounds.width() - maxWidth;
        int renderY = bounds.y() + (bounds.height() / 2 - fullHeight / 2) + 1;

        int scoreX = renderX + 4;
        int scoreY = renderY + scoreHeight + 10;
        int num = 0;
        int textOffset = scoreX - 4;

        for (Pair<ScoreboardPlayerScore, Text> scoreboardPlayerScoreTextPair : scoresWText) {
            ++num;
            ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboardPlayerScoreTextPair.getFirst();
            Text scoreText = scoreboardPlayerScoreTextPair.getSecond();
            String score = String.valueOf(scoreboardPlayerScore2.getScore());
            int relativeY = scoreY - num * 9 + topPadding.getValue() * 2;

            if (background.getValue() && backgroundColor.getValue().alpha() > 0) {
                if (num == scoresSize) {
                    RenderUtil.drawRectangle(
                            matrices,
                            textOffset, relativeY - 1, maxWidth, 10, backgroundColor.getValue()
                    );
                } else if (num == 1) {
                    RenderUtil.drawRectangle(
                            matrices,
                            textOffset,
                           relativeY, maxWidth, 10, backgroundColor.getValue()
                    );
                } else {
                    RenderUtil.drawRectangle(
                            matrices,
                            textOffset, relativeY, maxWidth, 9, backgroundColor.getValue()
                    );
                }
            }

            if (shadow.getValue()) {
                client.textRenderer.drawWithShadow(matrices, scoreText, (float) scoreX, (float) relativeY, -1);
            } else {
                client.textRenderer.draw(matrices, scoreText, (float) scoreX, (float) relativeY, -1);
            }
            if (this.scores.getValue()) {
                drawString(matrices, client.textRenderer, Text.literal(score),
                        (float) (scoreX + maxWidth - client.textRenderer.getWidth(score) - 6), (float) relativeY,
                        scoreColor.getValue().color(), shadow.getValue());
            }
            if (num == scoresSize) {
                // Draw the title
                if (background.getValue()) {
                    RenderUtil.drawRectangle(matrices, textOffset, relativeY - 10 - topPadding.getValue() * 2 - 1, maxWidth, 10 + topPadding.getValue() * 2, topColor.getValue());
                }
                float title = (float) (renderX + (maxWidth - displayNameWidth) / 2);
                if (shadow.getValue()) {
                    client.textRenderer.drawWithShadow(matrices, text, title, (float) (relativeY - 9) - topPadding.getValue(), -1);
                }
                else {
                    client.textRenderer.draw(matrices, text, title, (float) (relativeY - 9), -1);
                }
            }
        }

        if (outline.getValue() && outlineColor.getValue().alpha() > 0) {
            RenderUtil.drawOutline(matrices, textOffset, bounds.y(), maxWidth, fullHeight + 2, outlineColor.getValue());
        }
    }

    @Override
    public List<KronConfig<?>> getConfigurationOptions() {
        List<KronConfig<?>> options = super.getConfigurationOptions();;
        options.add(topColor);
        options.add(scores);
        options.add(scoreColor);
        options.add(anchor);
        options.add(topPadding);
        options.remove(textColor);
        return options;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public AnchorPoint getAnchor() {
        return anchor.getValue();
    }
}
