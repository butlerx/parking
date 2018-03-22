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

  private JFrame mainFrame = new JFrame("Carpark");
  private JPanel controlPanel = new JPanel();
  private JLabel headerLabel = new JLabel("CarPark", JLabel.CENTER);
  private JLabel carsLabel = new JLabel("There are currently 0 Cars in the Carpark", JLabel.CENTER);
  private JLabel spacesLabel =
      new JLabel("There are currently 1000 Spaces in the Carpark", JLabel.CENTER);
  private JLabel queueLabel =
      new JLabel("There are currently 0 Cars searching for a space", JLabel.CENTER);
  private JLabel parkedLabel = new JLabel("There are currently 0 Cars parked", JLabel.CENTER);
  private JLabel Exit1Label = new JLabel("No obstructions at Exit 1", JLabel.CENTER);
  private JLabel Exit2Label = new JLabel("No obstructions at Exit 2", JLabel.CENTER);
  private JLabel Exit3Label = new JLabel("No obstructions at Exit 3", JLabel.CENTER);

  private Clock.Seed seed = new Clock.Seed();
  private CarPark carpark = new CarPark();
  private String state = "";

  private Valet valet;
  private Clock clock;
  private Entrance in1;
  private Entrance in2;
  private Entrance in3;
  private Exit out1;
  private Exit out2;
  private Exit out3;

  /** main method */
  public static void main(String[] args) {
    Parking carParkOverView = new Parking();
  }

  /**
   * Constructor Prepare the User dashboard of information and controls
   *
   * @see javax.swing
   */
  public Parking() {
    this.mainFrame.setSize(400, 400);
    this.mainFrame.setLayout(new GridLayout(4, 1));

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
    mainFrame.add(Exit1Label);
    mainFrame.add(Exit2Label);
    mainFrame.add(Exit3Label);
    mainFrame.add(controlPanel);
    mainFrame.setVisible(true);

    JButton runButton = new JButton("Run");
    JButton stopButton = new JButton("Stop");
    JButton exitButton = new JButton("Exit");
    runButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (state != "running") {
              state = "running";
              valet = new Valet(carpark, carsLabel, spacesLabel, parkedLabel, queueLabel);
              clock = new Clock(headerLabel, seed);
              in1 = new Entrance(valet, 1, clock);
              in2 = new Entrance(valet, 2, clock);
              in3 = new Entrance(valet, 3, clock);
              out1 = new Exit(valet, 1, clock, Exit1Label);
              out2 = new Exit(valet, 2, clock, Exit2Label);
              out3 = new Exit(valet, 3, clock, Exit3Label);
              valet.execute();
              clock.execute();
              in1.execute();
              in2.execute();
              in3.execute();
              out1.execute();
              out2.execute();
              out3.execute();
            }
          }
        });
    stopButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (state == "running") {
              state = "";
              clock.cancel(false);
              valet.cancel(false);
              in1.cancel(true);
              in2.cancel(true);
              in3.cancel(true);
              out1.cancel(true);
              out2.cancel(true);
              out3.cancel(true);
            }
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
