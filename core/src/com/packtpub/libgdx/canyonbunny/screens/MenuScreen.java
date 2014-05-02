package com.packtpub.libgdx.canyonbunny.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packtpub.libgdx.canyonbunny.game.Assets;
import com.packtpub.libgdx.canyonbunny.screens.transitions.ScreenTransition;
import com.packtpub.libgdx.canyonbunny.screens.transitions.ScreenTransitionFade;
import com.packtpub.libgdx.canyonbunny.util.AudioManager;
import com.packtpub.libgdx.canyonbunny.util.CharacterSkin;
import com.packtpub.libgdx.canyonbunny.util.Constants;
import com.packtpub.libgdx.canyonbunny.util.GamePreferences;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class MenuScreen extends AbstractGameScreen {

	// debug
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private final boolean debugEnabled = false;
	private Stage stage;
	private Skin skinCanyonBunny;
	private Skin skinLibgdx;
	// menu
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgCoins;
	private Image imgBunny;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	// options
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private CheckBox chkMusic;
	private Slider sldMusic;
	private SelectBox selCharSkin;
	private Image imgCharSkin;
	private CheckBox chkShowFpsCounter;
	private float debugRebuildStage;
	private CheckBox chkUseMonochromeShader;

	public MenuScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (debugEnabled) {
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}
		stage.act(deltaTime);
		stage.draw();
		Table.drawDebug(stage);
	}

	private void rebuildStage() {
		skinCanyonBunny = new Skin(Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
		// build all layers
		Table layerBackground = buildBackgroundLayer();
		Table layerObjects = buildObjectsLayer();
		Table layerLogos = buildLogosLayer();
		Table layerControls = buildControlsLayer();
		Table layerOptionsWindow = buildOptionsWindowLayer();
		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerObjects);
		stack.add(layerLogos);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
	}

	private Table buildBackgroundLayer() {
		Table layer = new Table();
		// + Background
		imgBackground = new Image(skinCanyonBunny, "background");
		layer.add(imgBackground);
		return layer;
	}

	private Table buildObjectsLayer() {
		Table layer = new Table();
		// + Coins
		imgCoins = new Image(skinCanyonBunny, "coins");
		layer.addActor(imgCoins);
		imgCoins.setOrigin(imgCoins.getWidth() / 2, imgCoins.getHeight() / 2);
		imgCoins.addAction(sequence(moveTo(135, -20), scaleTo(0, 0), fadeOut(0), delay(2.5f), parallel(moveBy(0, 100, 0.5f, Interpolation.swingOut),
						scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear), alpha(1.0f, 0.5f))
		));
		// + Bunny
		imgBunny = new Image(skinCanyonBunny, "bunny");
		layer.addActor(imgBunny);
		imgBunny.addAction(sequence(moveTo(655, 510), delay(4.0f), moveBy(-70, -100, 0.5f, Interpolation.fade), moveBy(-100, -50, 0.5f,
				Interpolation.fade), moveBy(-150, -300, 1.0f, Interpolation.elasticIn)));
		return layer;
	}

	private Table buildLogosLayer() {
		Table layer = new Table();
		layer.left().top();
		// + Game Logo
		imgLogo = new Image(skinCanyonBunny, "logo");
		layer.add(imgLogo);
		layer.row().expandY();
		// + Info Logos
		imgInfo = new Image(skinCanyonBunny, "info");
		layer.add(imgInfo).bottom();
		if (debugEnabled) {
			layer.debug();
		}
		return layer;
	}

	private Table buildControlsLayer() {
		Table layer = new Table();
		layer.right().bottom();
		// + Play Button
		btnMenuPlay = new Button(skinCanyonBunny, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});
		layer.row();
		// + Options Button
		btnMenuOptions = new Button(skinCanyonBunny, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});
		if (debugEnabled) {
			layer.debug();
		}
		return layer;
	}

	private void onPlayClicked() {
		ScreenTransition transition = ScreenTransitionFade.init(0.75f);
		game.setScreen(new GameScreen(game), transition);
	}

	private void onOptionsClicked() {
		loadSettings();
		showMenuButtons(false);
		showOptionsWindow(true, true);
	}

	private Table buildOptionsWindowLayer() {
		winOptions = new Window("Options", skinLibgdx);
		// + Audio Settings: Sound/Music CheckBox and Volume Slider
		winOptions.add(buildOptWinAudioSettings()).row();
		// + Character Skin: Selection Box (White, Gray, Brown)
		winOptions.add(buildOptWinSkinSelection()).row();
		// + Debug: Show FPS Counter
		winOptions.add(buildOptWinDebug()).row();
		// + Separator and Buttons (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
		// Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);
		// Hide options window by default
		showOptionsWindow(false, false);
		if (debugEnabled) {
			winOptions.debug();
		}
		// Let TableLayout recalculate widget sizes and positions
		winOptions.pack();
		// Move options window to bottom right corner
		winOptions.setPosition(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winOptions;
	}

	@Override
	public void resize(int width, int height) {
		Viewport viewport = new FillViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		stage.setViewport(viewport);
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		stage = new Stage();
		rebuildStage();
	}

	@Override
	public void hide() {
		stage.dispose();
		skinCanyonBunny.dispose();
		skinLibgdx.dispose();
	}

	@Override
	public void pause() {
	}

	private void loadSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		selCharSkin.setSelectedIndex(prefs.charSkin);
		onCharSkinSelected(prefs.charSkin);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
		chkUseMonochromeShader.setChecked(prefs.useMonochromeShader);
	}

	private void saveSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.charSkin = selCharSkin.getSelectedIndex();
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		prefs.useMonochromeShader = chkUseMonochromeShader.isChecked();
		prefs.save();
	}

	private void onCharSkinSelected(int index) {
		CharacterSkin skin = CharacterSkin.values()[index];
		imgCharSkin.setColor(skin.getColor());
	}

	private void onSaveClicked() {
		saveSettings();
		onCancelClicked();
		AudioManager.instance.onSettingsUpdated();
	}

	private void onCancelClicked() {
		showMenuButtons(true);
		showOptionsWindow(false, true);
		AudioManager.instance.onSettingsUpdated();
	}

	private Table buildOptWinAudioSettings() {
		Table table = new Table();
		// + Title: "Audio"
		table.pad(10, 10, 0, 10);
		table.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
		table.row();
		table.columnDefaults(0).padRight(10);
		table.columnDefaults(1).padRight(10);
		// + Checkbox, "Sound" label, sound volume slider
		chkSound = new CheckBox("", skinLibgdx);
		table.add(chkSound);
		table.add(new Label("Sound", skinLibgdx));
		sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		table.add(sldSound);
		table.row();
		// + Checkbox, "Music" label, music volume slider
		chkMusic = new CheckBox("", skinLibgdx);
		table.add(chkMusic);
		table.add(new Label("Music", skinLibgdx));
		sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		table.add(sldMusic);
		table.row();
		return table;
	}

	private Table buildOptWinSkinSelection() {
		Table tbl = new Table();
		// + Title: "Character Skin"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Character Skin", skinLibgdx, "default-font", Color.ORANGE)).colspan(2);
		tbl.row();
		// + Drop down box filled with skin items
		selCharSkin = new SelectBox(skinLibgdx);
		selCharSkin.setItems(CharacterSkin.values());
		selCharSkin.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCharSkinSelected(((SelectBox) actor).getSelectedIndex());
			}
		});
		tbl.add(selCharSkin).width(120).padRight(20);
		// + Skin preview image
		imgCharSkin = new Image(Assets.instance.bunny.head);
		tbl.add(imgCharSkin).width(50).height(50);
		return tbl;
	}

	private Table buildOptWinDebug() {
		Table table = new Table();
		// + Title: "Debug"
		table.pad(10, 10, 0, 10);
		table.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3);
		table.row();
		table.columnDefaults(0).padRight(10);
		table.columnDefaults(1).padRight(10);
		// + Checkbox, "Show FPS Counter" label
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		table.add(new Label("Show FPS Counter", skinLibgdx));
		table.add(chkShowFpsCounter);
		table.row();
		// + Checkbox, "Use Monochrome Shader" label
		chkUseMonochromeShader = new CheckBox("", skinLibgdx);
		table.add(new Label("Use Monochrome Shader", skinLibgdx));
		table.add(chkUseMonochromeShader);
		table.row();
		return table;
	}

	private Table buildOptWinButtons() {
		Table table = new Table();
		// + Separator
		Label label = new Label("", skinLibgdx);
		label.setColor(0.75f, 0.75f, 0.75f, 1);
		label.setStyle(new LabelStyle(label.getStyle()));
		label.getStyle().background = skinLibgdx.newDrawable("white");
		table.add(label).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		table.row();
		label = new Label("", skinLibgdx);
		label.setColor(0.5f, 0.5f, 0.5f, 1);
		label.setStyle(new LabelStyle(label.getStyle()));
		label.getStyle().background = skinLibgdx.newDrawable("white");
		table.add(label).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		table.row();
		// + Save Button with event handler
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		table.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});
		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
		table.add(btnWinOptCancel);
		btnWinOptCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});
		return table;
	}

	private void showMenuButtons(boolean visible) {
		float moveDuration = 1.0f;
		Interpolation moveEasing = Interpolation.swing;
		float delayOptionsButton = 0.25f;
		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0;
		final Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
		btnMenuOptions.addAction(sequence(delay(delayOptionsButton), moveBy(moveX, moveY, moveDuration, moveEasing)));
		SequenceAction seq = sequence();
		if (visible) {
			seq.addAction(delay(delayOptionsButton + moveDuration));
		}
		seq.addAction(run(new Runnable() {
			public void run() {
				btnMenuPlay.setTouchable(touchEnabled);
				btnMenuOptions.setTouchable(touchEnabled);
			}
		}));
		stage.addAction(seq);
	}

	private void showOptionsWindow(boolean visible, boolean animated) {
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled;
		winOptions.addAction(sequence(touchable(touchEnabled), alpha(alphaTo, duration)));
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}

}