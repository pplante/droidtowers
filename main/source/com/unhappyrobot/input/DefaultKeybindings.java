package com.unhappyrobot.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.unhappyrobot.TowerGame;
import com.unhappyrobot.achievements.Achievement;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;

public class DefaultKeybindings {
  public static void initialize(TowerGame towerGame) {
    InputSystem.instance().bind(new int[]{InputSystem.Keys.PLUS, InputSystem.Keys.UP}, new InputCallback() {
      public boolean run(float timeDelta) {
        TowerGame.timeMultiplier += 0.5f;
        TowerGame.timeMultiplier = Math.min(TowerGame.timeMultiplier, 4);

        return true;
      }
    });

    InputSystem.instance().bind(new int[]{InputSystem.Keys.MINUS, InputSystem.Keys.DOWN}, new InputCallback() {
      public boolean run(float timeDelta) {
        TowerGame.timeMultiplier -= 0.5f;
        TowerGame.timeMultiplier = Math.max(TowerGame.timeMultiplier, 0.5f);

        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.G, new InputCallback() {
      public boolean run(float timeDelta) {
        TowerGame.getGameGridRenderer().toggleGridLines();

        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.T, new InputCallback() {
      public boolean run(float timeDelta) {
        TowerGame.getGameGridRenderer().toggleTransitLines();

        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.NUM_0, new InputCallback() {
      public boolean run(float timeDelta) {
        TowerGame.getCamera().zoom = 1f;

        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.R, new InputCallback() {
      public boolean run(float timeDelta) {
        Texture.invalidateAllTextures(Gdx.app);
        return true;
      }
    });

    InputSystem.instance().bind(InputSystem.Keys.A, new InputCallback() {
      public boolean run(float timeDelta) {
        for (Achievement achievement : AchievementEngine.instance().getAchievements()) {
          System.out.println("achievement = " + achievement);
          System.out.println("achievement.isCompleted() = " + achievement.isCompleted());
          System.out.println("\n\n");

          if (achievement.isCompleted()) {
            achievement.giveReward();
          }
        }

        return true;
      }
    });

    InputSystem.instance().bind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, new InputCallback() {
      private Dialog exitDialog;

      public boolean run(float timeDelta) {
        if (exitDialog != null) {
          exitDialog.dismiss();
        } else {
          exitDialog = new Dialog().setTitle("Awww, don't leave me.").setMessage("Are you sure you want to exit the game?").addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
            @Override
            public void onClick(Dialog dialog) {
              dialog.dismiss();
              Gdx.app.exit();
            }
          }).addButton(ResponseType.NEGATIVE, "No way!", new OnClickCallback() {
            @Override
            public void onClick(Dialog dialog) {
              dialog.dismiss();
            }
          }).centerOnScreen().show();

          exitDialog.onDismiss(new InputCallback() {
            public boolean run(float timeDelta) {
              exitDialog = null;
              return true;
            }
          });
        }

        return true;
      }
    });
  }
}
