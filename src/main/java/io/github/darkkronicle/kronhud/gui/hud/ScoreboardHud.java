package io.github.darkkronicle.kronhud.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.polish.api.EntryBuilder;
import io.github.darkkronicle.polish.gui.complexwidgets.EntryButtonList;
import io.github.darkkronicle.polish.gui.screens.BasicConfigScreen;
import io.github.darkkronicle.polish.impl.builders.DropdownSelectorEntryBuilder;
import io.github.darkkronicle.polish.util.Colors;
import io.github.darkkronicle.polish.util.DrawPosition;
import io.github.darkkronicle.polish.util.DrawUtil;
import io.github.darkkronicle.polish.util.Place;
import io.github.darkkronicle.polish.util.SimpleColor;
import io.github.darkkronicle.polish.util.SimpleRectangle;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "scoreboardhud");
    public static final Scoreboard placeholder = Util.make(() -> {
        Scoreboard placeScore = new Scoreboard();
        placeScore.addObjective("placeholder", ScoreboardCriterion.DUMMY, new LiteralText("Placeholder!"), ScoreboardCriterion.RenderType.INTEGER);
        ScoreboardObjective objective = placeScore.getObjective("Placeholder");
        ScoreboardPlayerScore score = new ScoreboardPlayerScore(placeScore, objective, "DarkKronicle");
        score.setScore(15);
        placeScore.updateScore(score);
        placeScore.updatePlayerScore("DarkKronicle", objective);
        placeScore.setObjectiveSlot(1, objective);
        return placeScore;
    });

    public ScoreboardHud() {
        super(300, 146);
    }

    @Override
    public void render(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
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
        matrices.pop();
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        matrices.scale(getStorage().scale, getStorage().scale, 1);
        DrawPosition pos = getScaledPos();
        if (hovered) {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.SELECTOR_BLUE.color().withAlpha(100).color());
        } else {
            DrawUtil.rect(matrices, pos.getX(), pos.getY(), width, height, Colors.WHITE.color().withAlpha(50).color());
        }
        DrawUtil.outlineRect(matrices, pos.getX(), pos.getY(), width, height, Colors.BLACK.color().color());

        ScoreboardObjective objective = placeholder.getObjective("placeholder");
        placeholder.setObjectiveSlot(1, objective);
        renderScoreboardSidebar(matrices, objective);
        hovered = false;
        matrices.pop();
    }

    // Abusing this could break some stuff/could allow for unfair advantages. The goal is not to do this, so it won't show any
    // more information than it would have in vanilla.
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> filteredScores = scores.stream().filter((scoreboardPlayerScorex) ->
                scoreboardPlayerScorex.getPlayerName() != null && !scoreboardPlayerScorex.getPlayerName().startsWith("#")
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
        for(Iterator<ScoreboardPlayerScore> scoresIterator = scores.iterator(); scoresIterator.hasNext(); maxWidth = Math.max(maxWidth, client.textRenderer.getWidth(formattedText) + spacerWidth + client.textRenderer.getWidth(Integer.toString(scoreboardPlayerScore.getScore())))) {
            scoreboardPlayerScore = scoresIterator.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            formattedText = Team.modifyText(team, new LiteralText(scoreboardPlayerScore.getPlayerName()));
            scoresWText.add(Pair.of(scoreboardPlayerScore, formattedText));
        }
        maxWidth = maxWidth + 2;

        if (maxWidth > width) {
            maxWidth = 200;
        }

        int scoresSize = scores.size();
        int scoreHeight = scoresSize * 9;
        DrawPosition pos = getScaledPos();
        SimpleRectangle bounds = new SimpleRectangle(pos.getX(), pos.getY(), width, height);
        SimpleRectangle inside = new SimpleRectangle(pos.getX(), pos.getY(), maxWidth, scoreHeight + 9);
        SimpleRectangle calculated = getStorage().place.calculate(bounds, inside);
        int scoreY = calculated.y() + scoreHeight + 9;
        int scoreX = calculated.x() + 2;
        int num = 0;
        int textOffset = scoreX - 2;

        for (Pair<ScoreboardPlayerScore, Text> scoreboardPlayerScoreTextPair : scoresWText) {
            ++num;
            ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboardPlayerScoreTextPair.getFirst();
            Text scoreText = scoreboardPlayerScoreTextPair.getSecond();
            String score = "" + scoreboardPlayerScore2.getScore();
            int relativeY = scoreY - num * 9;
            rect(matrices, textOffset, relativeY, maxWidth, 9, getStorage().background.color());
            client.textRenderer.draw(matrices, scoreText, (float) scoreX, (float) relativeY, Colors.WHITE.color().color());
            client.textRenderer.draw(matrices, score, (float) (scoreX + maxWidth - client.textRenderer.getWidth(score) - 2), (float) relativeY, getStorage().score.color());
            if (num == scoresSize) {
                rect(matrices, textOffset, relativeY - 10, maxWidth, 9, getStorage().top.color());
                rect(matrices, scoreX - 2, relativeY - 1, maxWidth, 1, getStorage().background.color());
                float title = (float) (scoreX + maxWidth / 2 - displayNameWidth / 2 - 1);
                client.textRenderer.draw(matrices, text, title, (float) (relativeY - 9), Colors.WHITE.color().color());
            }
        }

    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public boolean moveable() {
        return true;
    }

    @Override
    public Storage getStorage() {
        return KronHUD.storage.scoreboardHudStorage;
    }

    @Override
    public Text getName() {
        return new TranslatableText("hud.kronhud.scoreboardhud");
    }

    public static class Storage extends AbstractStorage {
        public SimpleColor score;
        public SimpleColor background;
        public SimpleColor outline;
        public SimpleColor top;
        public Place.Type place;

        public Storage() {
            x = 1F;
            y = 0.5F;
            scale = 1F;
            enabled = true;
            outline = Colors.WHITE.color().withAlpha(0);
            background = Colors.BLACK.color().withAlpha(100);
            score = Colors.SELECTOR_GREEN.color();
            top = Colors.BLACK.color().withAlpha(150);
            place = Place.Type.MIDDLE_RIGHT;
        }
    }

    @Override
    public Screen getConfigScreen() {
        EntryBuilder builder = EntryBuilder.create();
        EntryButtonList list = new EntryButtonList((client.getWindow().getScaledWidth() / 2) - 290, (client.getWindow().getScaledHeight() / 2) - 70, 580, 150, 1, false);
        list.addEntry(builder.startToggleEntry(new TranslatableText("option.kronhud.enabled"), getStorage().enabled).setDimensions(20, 10).setSavable(val -> getStorage().enabled = val).build(list));
        list.addEntry(builder.startFloatSliderEntry(new TranslatableText("option.kronhud.scale"), getStorage().scale, 0.2F, 1.5F).setWidth(80).setSavable(val -> getStorage().scale = val).build(list));
        DropdownSelectorEntryBuilder<Place.Type> places = builder.startDropdownEntry(new TranslatableText("option.kronhud.scoreboardhud.layout"), getStorage().place);
        for (Place.Type type : Place.Type.values()) {
            places.add(type, type.toString());
        }
        list.addEntry(places.setSavable(val -> getStorage().place = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.scoreboardhud.scorecolor"), getStorage().score).setSavable(val -> getStorage().score = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.backgroundcolor"), getStorage().background).setSavable(val -> getStorage().background = val).build(list));
        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.scoreboardhud.bordercolor"), getStorage().outline).setSavable(val -> getStorage().outline = val).build(list));

        list.addEntry(builder.startColorButtonEntry(new TranslatableText("option.kronhud.scoreboardhud.topcolor"), getStorage().top).setSavable(val -> getStorage().top = val).build(list));


        return new BasicConfigScreen(getName(), list, () -> KronHUD.storageHandler.saveDefaultHandling());

    }

}
