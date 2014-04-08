package com.badlogic.gdx.twl.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessorQueue;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.input.Input;

/**
 * @author Yifu Huang
 */
public class GdxInput extends InputAdapter implements Input {

    private GUI gui;
    private InputProcessorQueue inputProcessor;

    public GdxInput(InputProcessorQueue inputProcessor) {
        this.inputProcessor = inputProcessor;
        this.inputProcessor.setProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        gui.handleKey(keycode, '\0', true);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        gui.handleKey(keycode, '\0', false);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        gui.handleKey(0, character, true);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount != 0) {
            gui.handleMouseWheel(amount / 120);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        gui.handleMouse(screenX, screenY, -1, false);
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        gui.handleMouse(screenX, screenY, -1, false);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        gui.handleMouse(screenX, screenY, button, true);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        gui.handleMouse(screenX, screenY, button, false);
        return false;
    }

    @Override
    public boolean pollInput(GUI gui) {
        this.gui = gui;
        inputProcessor.drain();
        return true;
    }
}
