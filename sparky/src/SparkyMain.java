import jodd.util.ClassLoaderUtil;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;

public class SparkyMain extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private static Object instance;

  public SparkyMain() {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    buttonCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    });

// call onCancel() when cross is clicked
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
        System.exit(0);
      }
    });

// call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  private void onOK() {
    setVisible(false);

    try {
      ClassLoaderUtil.addUrlToClassPath(new File("DroidTowers-patch.jar").toURL());
      ClassLoaderUtil.addUrlToClassPath(new File("DroidTowers-release.jar").toURL());

      Class aClass = ClassLoaderUtil.loadClass("com.unhappyrobot.DesktopGame");
      Method main = aClass.getDeclaredMethod("main", String[].class);
      instance = aClass.newInstance();
      main.invoke(instance, new Object[]{null});
    } catch (Exception e) {
      e.printStackTrace();
    }

    dispose();
  }

  private void onCancel() {
// add your code here if necessary
    dispose();
  }

  public static void main(String[] args) {
    SparkyMain dialog = new SparkyMain();
    dialog.pack();
    dialog.setVisible(true);
  }
}
