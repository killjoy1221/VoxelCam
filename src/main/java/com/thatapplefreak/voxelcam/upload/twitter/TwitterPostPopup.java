package com.thatapplefreak.voxelcam.upload.twitter;

import java.io.File;
import java.io.IOException;

import com.voxelmodpack.common.gui.GuiDialogBox;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class TwitterPostPopup extends GuiDialogBox {

	private boolean uploading = false;

	private volatile GuiScreen completeDialog;

	private GuiTextField textbox;
	private File toPost;
	private int tweetLengh = 100;
	private TwitterHandler twitter;

	public TwitterPostPopup(GuiScreen parentScreen, File toPost) {
		super(parentScreen, 210, 90, I18n.format("postto") + " Twitter");
		this.twitter = new TwitterHandler(this);
	}

	@Override
	protected void onInitDialog() {
		btnOk.displayString = I18n.format("post");
		textbox = new GuiTextField(0xFFFFFF, fontRendererObj, width / 2 - (200 / 2), height / 2 - (16 / 2) - 8, 200, 16);
		textbox.setMaxStringLength(tweetLengh);
		textbox.setFocused(true);
	}

	@Override
	protected void drawDialog(int mouseX, int mouseY, float f) {
		super.drawDialog(mouseX, mouseY, f);

		if (uploading) {
			buttonList.clear();
			drawCenteredString(fontRendererObj, I18n.format("uploading") + "...", width / 2, height / 2, 0xFFFFFF);
		} else {
			textbox.drawTextBox();
			drawString(fontRendererObj, I18n.format("composetweet") + ":", dialogX + 5, height / 2 - 28, 0xFFFFFF);
			drawString(fontRendererObj, I18n.format("remainingletters") + ":", width / 2 - 5, height / 2 + 5, 0xFFFFFF);
			drawString(fontRendererObj, Integer.toString(tweetLengh - textbox.getText().length()), width / 2 + 84, height / 2 + 5, 0xFFFFFF);
		}

		if (this.completeDialog != null) {
			this.mc.displayGuiScreen(this.completeDialog);
			this.completeDialog = null;
		}
	}

	@Override
	protected void mouseClickedEx(int mouseX, int mouseY, int button) throws IOException {
		super.mouseClickedEx(mouseX, mouseY, button);
		textbox.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void onKeyTyped(char keyChar, int keyCode) {
		textbox.textboxKeyTyped(keyChar, keyCode);
	}

	@Override
	public void onSubmit() {
	}

	@Override
	public boolean validateDialog() {
		twitter.setText(textbox.getText());
		twitter.doTwitter(toPost);
		uploading = true;
		return false;
	}
	
	public void onUploadComplete(GuiScreen result) {
		this.completeDialog = result;
	}

}