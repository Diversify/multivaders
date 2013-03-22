package se.diversify.multivaders.strategy;

import se.diversify.multivaders.event.KeyEvent;

public class SumStrategy extends AbstractStrategy {

    private int leftRightSum = 0;
    private int previousSum = 0;

    private boolean isShootingDown = false;

    @Override
    public void process(KeyEvent event) {
        switch (event.getFunction()) {
            case left:
                handleLeftRight(-1, event.getClickType());
                break;
            case right:
                handleLeftRight(1, event.getClickType());
                break;
            case shoot:
                handleShoot(event.getClickType());
                break;
        }
    }

    private void handleShoot(KeyEvent.ClickType clickType) {
        if (clickType == KeyEvent.ClickType.down) {
            if (isShootingDown) {
                decisionMaker.sendResponse(new KeyEvent(KeyEvent.Function.shoot, KeyEvent.ClickType.up));
            }
            decisionMaker.sendResponse(new KeyEvent(KeyEvent.Function.shoot, KeyEvent.ClickType.down));
            isShootingDown = true;
        } else {
            if (isShootingDown) {
                decisionMaker.sendResponse(new KeyEvent(KeyEvent.Function.shoot, KeyEvent.ClickType.up));
            }
            isShootingDown = false;
        }
    }

    private void handleLeftRight(int direction, KeyEvent.ClickType clickType) {
        int value = 0;
        switch(clickType) {
            case down:
                value = 1;
                break;
            case up:
                value = -1;
                break;
        }

        leftRightSum += direction * value;

        if (leftRightSum == -1) {
            decisionMaker.sendResponse(new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.down));
        } else if (leftRightSum == 1) {
            decisionMaker.sendResponse(new KeyEvent(KeyEvent.Function.right, KeyEvent.ClickType.down));
        } else if (leftRightSum == 0) {
            if (previousSum < 0) {
                decisionMaker.sendResponse(new KeyEvent(KeyEvent.Function.left, KeyEvent.ClickType.up));
            } else {
                decisionMaker.sendResponse(new KeyEvent(KeyEvent.Function.right, KeyEvent.ClickType.up));
            }
        }

        previousSum = leftRightSum;
    }
}
