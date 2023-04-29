package io.github.darkkronicle.kronhud.gui.hud.simple;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.config.KronBoolean;
import io.github.darkkronicle.kronhud.config.KronConfig;
import io.github.darkkronicle.kronhud.gui.entry.TextHudEntry;
import io.github.darkkronicle.kronhud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.atlas.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GameTimeHud extends TextHudEntry
{
	public static final Identifier ID = new Identifier("kronhud","gametimehud");

	private final KronBoolean isAmPm = new KronBoolean("is12hour",ID.getPath(),true,this::updateFormatter);

	private final long dayTicks = 24000;

	private final Identifier bedTexture = Identifier.of("kronhud","textures/gui/icons/classic_red_bed.png");

	private DateFormat formatter;

	public GameTimeHud() {
		super(79,20,true);
	}

	public Identifier getId() {
		return ID;
	}

	public String getValue() {
		return formatTime(getDayTicks());
	}

	@Override
	public void renderComponent(MatrixStack matrices, float delta) {
		DrawPosition pos = getPos();
		drawString(matrices,
				client.textRenderer,
				Text.literal(getValue()),
				pos.x() + getWidth() - 42,
				pos.y() + getHeight() / 2 - 8,
				textColor.getValue().color(),
				shadow.getValue()
		);
		RenderUtil.drawItem(matrices, new ItemStack(Items.CLOCK), pos.x()+2, pos.y()+2);
		// sleep condition (thunderstorms can also allow sleep)
		boolean canSleep = (MinecraftClient.getInstance().world.isThundering()
				|| getDayTicks() >= 12542)
				&& MinecraftClient.getInstance().world.getRegistryKey() == World.OVERWORLD;
		RenderSystem.setShaderColor(1,1,1,
				canSleep ? 1 : 0.5f
		);
		client.getTextureManager().bindTexture(bedTexture);
		DrawableHelper.drawTexture(matrices,pos.x()+18,pos.y()+2,0,0,16,16,16,16);
		RenderSystem.setShaderColor(1,1,1,1);
	}

	@Override
	public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
		DrawPosition pos = getPos();
		drawString(matrices,
				client.textRenderer,
				Text.literal(formatTime(0)),
				pos.x() + getWidth() - 42,
				pos.y()+ getHeight() / 2 - 8,
				textColor.getValue().color(),
				shadow.getValue()
		);
		RenderUtil.drawItem(matrices, new ItemStack(Items.CLOCK), pos.x()+2, pos.y()+2);
		RenderUtil.drawItem(matrices, new ItemStack(Items.RED_BED), pos.x()+18, pos.y()+2);
	}

	private String formatTime(long currentTick) {
		if (formatter == null)
			updateFormatter(isAmPm.getValue());
		long hour = (currentTick / (dayTicks / 24) + 6) % 24; // Minecraft Wiki says time 0 is effectively 6:00
		long minute = (currentTick / (dayTicks / (24 * 60))) % 60;
		Date time = new Calendar.Builder()
				.setTimeOfDay((int)hour,(int)minute,0)
				.build()
				.getTime();
		return formatter.format(time);
	}

	private long getDayTicks() {
		return MinecraftClient.getInstance().world.getTimeOfDay() % dayTicks;
	}

	public void updateFormatter(boolean value) {
		if (value) {
			formatter = new SimpleDateFormat("hh:mm a");
		} else {
			formatter = new SimpleDateFormat("HH:mm");
		}
	}

	public List<KronConfig<?>> getConfigurationOptions() {
		List<KronConfig<?>> options = super.getConfigurationOptions();
		options.add(isAmPm);
		return options;
	}
}
