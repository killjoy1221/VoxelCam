package com.thatapplefreak.voxelcam.gui.manager;

import static com.thatapplefreak.voxelcam.Translations.BACK;
import static com.thatapplefreak.voxelcam.Translations.DELETE;
import static com.thatapplefreak.voxelcam.Translations.EDIT;
import static com.thatapplefreak.voxelcam.Translations.OPEN_SCREENSHOTS_FOLDER;
import static com.thatapplefreak.voxelcam.Translations.POST_TO;
import static com.thatapplefreak.voxelcam.Translations.RENAME;

import java.awt.Desktop;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Objects;
import com.thatapplefreak.voxelcam.VoxelCamCore;
import com.thatapplefreak.voxelcam.gui.editor.GuiEditScreenshot;
import com.thatapplefreak.voxelcam.imagehandle.GLImageMemoryHandler;
import com.thatapplefreak.voxelcam.imagehandle.ScreenshotIncapable;
import com.thatapplefreak.voxelcam.io.VoxelCamIO;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * This Gui shows the player the screenshots he/she has taken and can
 * rename/delete/post to imgur,facebook,twitter
 * 
 * @author thatapplefreak
 * 
 */
public class GuiScreenShotManager extends GuiScreen implements ScreenshotIncapable {

	/**
	 * Frame for the currently displaying picture
	 */
	private ScalePhotoFrame frame;

	/**
	 * Selector for the user to pic the photo they want to view
	 */
	private PhotoSelector selector;

	/**
	 * Button to go to previous screen
	 */
	private GuiButton btnBack, btnRename, btnDelete, btnPost, btnOpenFolder, btnEditPicture;

	public SearchBar searchBar;

	private VoxelCamIO images;

	private static final float frameScale = 22F / 30F;

	public GuiScreenShotManager() {
		images = new VoxelCamIO();
		frame = new ScalePhotoFrame(this, (int) (width - (width * (frameScale))), 10, frameScale, images.getSelectedPhoto());
	}

	@Override
	public void initGui() {

		searchBar = new SearchBar(fontRendererObj, 11, 14, 50, 13);

		selector = new PhotoSelector(this, images, 125);
		selector.registerScrollButtons(buttonList, 7, 8);

		btnBack = new GuiButton(0, 10, height - 30, 70, 20, I18n.format(BACK));
		buttonList.add(btnBack);

		btnRename = new GuiButton(1, width - (70 * 3) - 5, height - 45, 70, 20, I18n.format(RENAME));
		buttonList.add(btnRename);

		btnDelete = new GuiButton(2, width - (70 * 2) - 5, height - 45, 70, 20, I18n.format(DELETE));
		buttonList.add(btnDelete);

		btnEditPicture = new GuiButton(3, width - (70 * 1) - 5, height - 45, 70, 20, I18n.format(EDIT));
		buttonList.add(btnEditPicture);
		btnEditPicture.enabled = false;

		btnOpenFolder = new GuiButton(4, width - (70 * 3) - 5, height - 25, 140, 20, I18n.format(OPEN_SCREENSHOTS_FOLDER));
		buttonList.add(btnOpenFolder);

		btnPost = new GuiButton(5, width - (70 * 1) - 5, height - 25, 70, 20, I18n.format(POST_TO));
		buttonList.add(btnPost);

	}

	@Override
	public void drawScreen(int xPos, int yPos, float partialTicks) {
		if (!mc.isSingleplayer()) {
			drawDefaultBackground();
		} else {
			drawBackground(0);
		}

		super.drawScreen(xPos, yPos, partialTicks);
		frame.draw(xPos, yPos, partialTicks);
		selector.drawScreen(xPos, yPos, partialTicks);
		searchBar.drawTextBox();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		images.updateScreenShotFilesList(searchBar.getText());
		if (!Objects.equal(frame.getPhoto(), images.getSelectedPhoto())) {

			frame.setPhoto(images.getSelectedPhoto());

		}
		frame.update((int) (btnPost.xPosition + 70 - (width * frameScale)), 13);
		selector.setDimensionsAndPosition(10, 28, frame.x, frame.y + frame.height);
		searchBar.setWidth(selector.right - selector.left - 2);
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		if (btn.equals(btnBack)) {
			mc.displayGuiScreen(null);
		} else if (btn.equals(btnRename)) {
			mc.displayGuiScreen(new RenamePopup(this, images));
		} else if (btn.equals(btnDelete)) {
			mc.displayGuiScreen(new DeletePopup(this, images));
		} else if (btn.equals(btnEditPicture)) {
			mc.displayGuiScreen(new GuiEditScreenshot(this, images.getSelectedPhoto()));
		} else if (btn.equals(btnOpenFolder)) {
			try {
				Desktop.getDesktop().browse(VoxelCamCore.getScreenshotsDir().toURI());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (btn.equals(btnPost)) {
			mc.displayGuiScreen(new PostPopup(this, images.getSelectedPhoto()));
		}
	}

	public void enableButtons(boolean b) {
		if (b) {
			btnRename.enabled = true;
			btnDelete.enabled = true;
//			btnEditPicture.enabled = true;
			btnPost.enabled = true;
		} else {
			btnRename.enabled = false;
			btnDelete.enabled = false;
			btnEditPicture.enabled = false;
			btnPost.enabled = false;
		}
		btnBack.enabled = true;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		super.mouseClicked(mouseX, mouseY, button);
		searchBar.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void keyTyped(char keyChar, int keyCode) throws IOException {
		super.keyTyped(keyChar, keyCode);
		searchBar.textboxKeyTyped(keyChar, keyCode);
		if (!searchBar.isFocused() && btnDelete.enabled) {
			if (keyChar == 'r') {
				actionPerformed(btnRename);
			} else if (keyChar == 'd') {
				actionPerformed(btnDelete);
			} else if (keyChar == 'p') {
				actionPerformed(btnPost);
			} else if (keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_W) {
				images.previous();
			} else if (keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_S) {
				images.next();
			}
		}
	}

	@Override
	public void onGuiClosed() {
		GLImageMemoryHandler.requestImageFlush();
	}

}
