import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

/**
 * Car park simulator
 *
 * <p>Car park simulator with multiple queued entrances and exits
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Eanna Byrne <eanna.byrne76@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
public class Parking {

  private JFrame mainFrame;
  private JLabel headerLabel;
  private JLabel spacesLabel;
  private JLabel carsLabel;
  private JLabel parkedLabel;
  private JLabel queueLabel;
  private JLabel delayLabel;
  private JPanel controlPanel;
  private CarPark multiStory = new CarPark(1000);

  private Clock clock = new Clock(multiStory);
  private Entrance in1 = new Entrance(multiStory, 1, clock);
  private Entrance in2 = new Entrance(multiStory, 2, clock);
  private Entrance in3 = new Entrance(multiStory, 3, clock);
  private Exit out1 = new Exit(multiStory, 1, clock);
  private Exit out2 = new Exit(multiStory, 2, clock);
  private Exit out3 = new Exit(multiStory, 3, clock);
  private Parker wait1 = new Parker(multiStory.getQueue(), multiStory);
  private Parker wait2 = new Parker(multiStory.getQueue(), multiStory);
  private Parker wait3 = new Parker(multiStory.getQueue(), multiStory);

  private static DecimalFormat df = new DecimalFormat("00");

  /** Constructor. */
  public Parking() {
    prepareGUI();
  }

  /** main method */
  public static void main(String[] args) {
    Parking carParkOverView = new Parking();
    carParkOverView.showDashboard();
  }

  /**
   * Prepare the User dashboard of information and controls
   *
   * @see javax.swing
   */
  private void prepareGUI() {
    mainFrame = new JFrame("Carpark");
    mainFrame.setSize(400, 400);
    mainFrame.setLayout(new GridLayout(4, 1));

    headerLabel = new JLabel("CarPark", JLabel.CENTER);
    carsLabel = new JLabel("", JLabel.CENTER);
    spacesLabel = new JLabel("", JLabel.CENTER);
    queueLabel = new JLabel("", JLabel.CENTER);
    parkedLabel = new JLabel("", JLabel.CENTER);
    delayLabel = new JLabel("No obstructions at the gates", JLabel.CENTER);
    spacesLabel.setSize(350, 100);
    carsLabel.setSize(350, 100);
    queueLabel.setSize(350, 100);
    parkedLabel.setSize(350, 100);
    delayLabel.setSize(350, 100);

    mainFrame.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent windowEvent) {
            System.exit(0);
          }
        });
    controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout());

    mainFrame.add(headerLabel);
    mainFrame.add(spacesLabel);
    mainFrame.add(carsLabel);
    mainFrame.add(queueLabel);
    mainFrame.add(parkedLabel);
    mainFrame.add(delayLabel);
    mainFrame.add(controlPanel);
    mainFrame.setVisible(true);
  }

  /** Show Dashboard to user */
  private void showDashboard() {
    JButton runButton = new JButton("Run");
    JButton stopButton = new JButton("Stop");
    JButton exitButton = new JButton("Exit");
    runButton.setActionCommand("Run");
    stopButton.setActionCommand("Stop");
    exitButton.setActionCommand("Exit");
    runButton.addActionListener(new ButtonClickListener());
    stopButton.addActionListener(new ButtonClickListener());
    exitButton.addActionListener(new ButtonClickListener());
    controlPanel.add(runButton);
    controlPanel.add(stopButton);
    controlPanel.add(exitButton);
    mainFrame.setVisible(true);
    stats.start();
  }

  /**
   * Display status of exits
   *
   * @return String of the current status of the exits
   */
  private String checkExits() {
    if (!out1.checkObstruction() && !out2.checkObstruction() && !out3.checkObstruction()) {
      return "No obstructions at the gates";
    } else {
      String status = "There is a car currently stuck at a exit: ";
      if (out1.checkObstruction()) {
        status = status + "#1";
      }
      if (out2.checkObstruction()) {
        if (status.contains("#")) {
          status = status + ", #2";
        } else {
          status = status + "#2";
        }
      }
      if (out3.checkObstruction()) {
        if (status.contains("#")) {
          status = status + ", #3";
        } else {
          status = status + "#3";
        }
      }
      return status;
    }
  }

  /** Timer to refresh Dashboard every second with current information from the carpark */
  Timer stats =
      new Timer(
          1000,
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              headerLabel.setText(
                  "The time in the CarPark is "
                      + df.format(clock.getHour())
                      + ":"
                      + df.format(clock.getTime()));
              carsLabel.setText(
                  "There are currently " + multiStory.getTotalCars() + " Cars in the Carpark");
              spacesLabel.setText(
                  "There are currently " + multiStory.getSpaces() + " Spaces in the Carpark");
              parkedLabel.setText(
                  "There are currently " + multiStory.getParkedCars() + " Cars parked");
              queueLabel.setText(
                  "There are currently "
                      + multiStory.getQueue().getNumWaiting()
                      + " Cars searching for a space");
              delayLabel.setText(checkExits());
            }
          });

  /** Listen for button click to start and stop threads */
  private class ButtonClickListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();
      if (command.equals("Run")) {
        clock.start();
        in1.start();
        in2.start();
        in3.start();
        wait1.start();
        wait2.start();
        wait3.start();
        out1.start();
        out2.start();
        out3.start();
      } else if (command.equals("Stop")) {
        clock.kill();
        in1.kill();
        in2.kill();
        in3.kill();
        wait1.kill();
        wait2.kill();
        wait3.kill();
        out1.kill();
        out2.kill();
        out3.kill();
      } else {
        System.exit(0);
      }
    }
  }
}
