import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Car park simulator
 *
 * <p>Car park simulator with multiple queued entrances and exits
 *
 * @author Cian Butler <cian.butler25@mail.dcu.ie>, Terry Bolt <terrence.bolt2@mail.dcu.ie>
 * @version 1.0
 * @since 1.0
 */
public class Parking {

  private final JFrame mainFrame = new JFrame("Carpark");
  private final JLabel headerLabel = new JLabel("CarPark", JLabel.CENTER);
  private final JPanel controlPanel = new JPanel();
  private JLabel carsLabel = new JLabel("There are currently 0 Cars in the Carpark", JLabel.CENTER);
  private JLabel spacesLabel =
      new JLabel("There are currently 1000 Spaces in the Carpark", JLabel.CENTER);
  private JLabel queueLabel =
      new JLabel("There are currently 0 Cars searching for a space", JLabel.CENTER);
  private JLabel parkedLabel = new JLabel("There are currently 0 Cars parked", JLabel.CENTER);
  private JLabel Exit1Label = new JLabel("No obstructions at Exit 1", JLabel.CENTER);
  private JLabel Exit2Label = new JLabel("No obstructions at Exit 2", JLabel.CENTER);
  private JLabel Exit3Label = new JLabel("No obstructions at Exit 3", JLabel.CENTER);

  private final CarPark multiStory = new CarPark(1000);

  private final Clock clock = new Clock(headerLabel);
  private final Entrance in1 = new Entrance(multiStory, 1, clock);
  private final Entrance in2 = new Entrance(multiStory, 2, clock);
  private final Entrance in3 = new Entrance(multiStory, 3, clock);
  private final Exit out1 = new Exit(multiStory, 1, clock, Exit1Label);
  private final Exit out2 = new Exit(multiStory, 2, clock, Exit2Label);
  private final Exit out3 = new Exit(multiStory, 3, clock, Exit3Label);
  private final Valet wait1 =
      new Valet(multiStory, carsLabel, spacesLabel, parkedLabel, queueLabel);
  private final Valet wait2 =
      new Valet(multiStory, carsLabel, spacesLabel, parkedLabel, queueLabel);
  private final Valet wait3 =
      new Valet(multiStory, carsLabel, spacesLabel, parkedLabel, queueLabel);

  /** main method */
  public static void main(String[] args) {
    Parking carParkOverView = new Parking();
  }

  /**
   * Constructor. Prepare the User dashboard of information and controls
   *
   * @see javax.swing
   */
  public Parking() {
    mainFrame.setSize(400, 400);
    mainFrame.setLayout(new GridLayout(4, 1));
    spacesLabel.setSize(350, 100);
    carsLabel.setSize(350, 100);
    queueLabel.setSize(350, 100);
    parkedLabel.setSize(350, 100);
    Exit1Label.setSize(115, 100);
    Exit2Label.setSize(115, 100);
    Exit3Label.setSize(115, 100);

    mainFrame.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent windowEvent) {
            System.exit(0);
          }
        });
    controlPanel.setLayout(new FlowLayout());

    mainFrame.add(headerLabel);
    mainFrame.add(spacesLabel);
    mainFrame.add(carsLabel);
    mainFrame.add(queueLabel);
    mainFrame.add(parkedLabel);
    mainFrame.add(controlPanel);
    mainFrame.add(Exit1Label);
    mainFrame.add(Exit2Label);
    mainFrame.add(Exit3Label);
    mainFrame.setVisible(true);

    JButton runButton = new JButton("Run");
    JButton stopButton = new JButton("Stop");
    JButton exitButton = new JButton("Exit");
    runButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
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
          }
        });
    stopButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
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
          }
        });
    exitButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
    controlPanel.add(runButton);
    controlPanel.add(stopButton);
    controlPanel.add(exitButton);
    mainFrame.setVisible(true);
  }
}
