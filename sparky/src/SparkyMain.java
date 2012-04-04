import jodd.util.ClassLoaderUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class SparkyMain extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JLabel happyDroidsLogo;
  private JProgressBar updateProgress;
  private JEditorPane gameUpdates;

  public SparkyMain() {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

// call onCancel() when cross is clicked
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

// call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);



    new Thread() {
      @Override
      public void run() {
        gameUpdates.setEditable(false);
        try {
          gameUpdates.setPage("http://local.happydroids.com/game-updates");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  private void onOK() {
    setVisible(false);

    try {
      ClassLoaderUtil.addUrlToClassPath(new File("DroidTowers-patch.jar").toURL());
      ClassLoaderUtil.addUrlToClassPath(new File("DroidTowers-release.jar").toURL());

      Class aClass = ClassLoaderUtil.loadClass("com.unhappyrobot.DesktopGame");
      Method main = aClass.getDeclaredMethod("main", String[].class);
      Object instance = aClass.newInstance();
      main.invoke(instance, new Object[]{null});
    } catch (Exception e) {
      e.printStackTrace();
    }

    dispose();
  }

  private void onCancel() {
    System.exit(0);
    dispose();
  }

  public static void main(String[] args) {
    SparkyMain dialog = new SparkyMain();
    dialog.pack();
    dialog.setVisible(true);
  }

  private void createUIComponents() {
    try {
      happyDroidsLogo = new JLabel(new ImageIcon(ImageIO.read(new File("happy-droids-logo.png"))));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
