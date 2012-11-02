package uk.ac.cam.db538.dexter.gui;

import gr.zeus.ui.JMessage;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import lombok.val;
import uk.ac.cam.db538.dexter.dex.Dex;

import com.alee.extended.window.WebProgressDialog;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuBar;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.tabbedpane.WebTabbedPane;

public class MainWindow {

  private JFrame Frame;
  private JTabbedPane TabbedPane;

  public MainWindow() {
    initialize();
  }

  private void initialize() {
    Frame = new JFrame("Dexter");
    Frame.setBounds(100, 100, 800, 600);
    Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    TabbedPane = new WebTabbedPane();
    Frame.add(TabbedPane);

    val menubar = new WebMenuBar();
    {
      val menuFile = new WebMenu("File");
      menuFile.setMnemonic(KeyEvent.VK_F);
      {
        val menuFileOpen = new WebMenuItem("Open", KeyEvent.VK_O);
        menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuFileOpen.addActionListener(Listener_FileOpen);
        menuFile.add(menuFileOpen);

        val menuFileInstrument = new WebMenuItem("Instrument", KeyEvent.VK_I);
        menuFileInstrument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        menuFileInstrument.addActionListener(Listener_FileInstrument);
        menuFile.add(menuFileInstrument);

        val menuFileSave = new WebMenuItem("Save", KeyEvent.VK_S);
        menuFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuFileSave.addActionListener(Listener_FileSave);
        menuFile.add(menuFileSave);
      }
      menubar.add(menuFile);
    }
    Frame.setJMenuBar(menubar);
  }

  private void doModal(String message, final Thread task) {
    // Load dialog
    val progress = new WebProgressDialog(Frame, "");
    progress.setText(message);
    progress.setIndeterminate(true);
    progress.setShowProgressText(false);
    progress.setResizable(false);
    progress.setDefaultCloseOperation(WebProgressDialog.DO_NOTHING_ON_CLOSE);
//    progress.addWindowListener(new WindowListener)

    // Starting updater thread
    new Thread(new Runnable() {
      @Override
      public void run() {
        task.start();
        try {
          task.join();
        } catch (InterruptedException e) {
        } finally {
          progress.setVisible(false);
        }
      }
    }).start();

    progress.setModal(true);
    progress.setVisible(true);
  }

  private ActionListener Listener_FileOpen = new ActionListener() {

    private JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));

    @Override
    public void actionPerformed(ActionEvent arg0) {
      if (fc.showOpenDialog(Frame) != JFileChooser.APPROVE_OPTION)
        return;

      val file = fc.getSelectedFile();
      doModal("Loading " + file.getName(), new Thread(new Runnable() {
        public void run() {
          try {
            val splitPane = new FileTab(new Dex(file));
            TabbedPane.addTab(file.getName(), splitPane);
          } catch (Throwable e) {
            JMessage.showErrorMessage(Frame, "A problem occurred while loading file \"" + file.getName() + "\".", e);
            return;
          }
        }
      }));
    }
  };

  private ActionListener Listener_FileInstrument = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent arg0) {
      val selected = TabbedPane.getSelectedComponent();
      if (selected == null)
        return;

      val tab = (FileTab) selected;
      val dex = tab.getOpenedFile();
      doModal("Instrumenting " + dex.getFilename().getName(), new Thread(new Runnable() {
        public void run() {
          dex.instrument();
          tab.getTreeListener().valueChanged(null);
        }
      }));
    }
  };

  private ActionListener Listener_FileSave = new ActionListener() {

    private JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));

    @Override
    public void actionPerformed(ActionEvent arg0) {
      val selected = TabbedPane.getSelectedComponent();
      if (selected == null)
        return;

      if (fc.showSaveDialog(Frame) != JFileChooser.APPROVE_OPTION)
        return;

      val tab = (FileTab) selected;
      val file = fc.getSelectedFile();

      doModal("Saving " + file.getName(), new Thread(new Runnable() {
        public void run() {
          try {
            tab.getOpenedFile().writeToFile(file);
          } catch (Throwable e) {
            JMessage.showErrorMessage(Frame, "A problem occurred while saving into file \"" + file.getName() + "\".", e);
            return;
          }
        }
      }));
    }
  };

  // MAIN FUNCTION

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MainWindow window = new MainWindow();
          window.Frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}
