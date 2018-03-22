package parking

import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Car park simulator
 *
 *
 * Car park simulator with multiple queued entrances and exits
 *
 * @author Cian Butler <cian.butler25></cian.butler25>@mail.dcu.ie>, Terry Bolt <terrence.bolt2></terrence.bolt2>@mail.dcu.ie>
 * @version 2.0
 * @since 1.0
 */
internal class Parking {
    private val mainFrame = JFrame("Carpark")
    private val headerLabel = JLabel("CarPark", JLabel.CENTER)
    private val controlPanel = JPanel()
    private val carsLabel = JLabel("There are currently 0 Cars in the Carpark", JLabel.CENTER)
    private val spacesLabel = JLabel("There are currently 1000 Spaces in the Carpark", JLabel.CENTER)
    private val queueLabel = JLabel("There are currently 0 Cars searching for a space", JLabel.CENTER)
    private val parkedLabel = JLabel("There are currently 0 Cars parked", JLabel.CENTER)
    private val Exit1Label = JLabel("No obstructions at Exit 1", JLabel.CENTER)
    private val Exit2Label = JLabel("No obstructions at Exit 2", JLabel.CENTER)
    private val Exit3Label = JLabel("No obstructions at Exit 3", JLabel.CENTER)

    private val seed = Clock.Seed()
    private val carpark = CarPark()
    private var running = false

    private var valet = Valet(carpark, carsLabel, spacesLabel, parkedLabel, queueLabel)
    private var clock = Clock(headerLabel, seed)
    private var in1 = Entrance(valet, 1, clock)
    private var in2 = Entrance(valet, 2, clock)
    private var in3 = Entrance(valet, 3, clock)
    private var out1 = Exit(valet, 1, clock, Exit1Label)
    private var out2 = Exit(valet, 2, clock, Exit2Label)
    private var out3 = Exit(valet, 3, clock, Exit3Label)

    /**
     * Constructor
     *
     * Prepare the User dashboard of information and controls
     *
     * @see javax.swing
     */
    init {
        this.mainFrame.setSize(400, 400)
        this.mainFrame.setLayout(GridLayout(4, 1))

        spacesLabel.setSize(350, 100)
        carsLabel.setSize(350, 100)
        queueLabel.setSize(350, 100)
        parkedLabel.setSize(350, 100)
        Exit1Label.setSize(115, 100)
        Exit2Label.setSize(115, 100)
        Exit3Label.setSize(115, 100)

        mainFrame.addWindowListener(
                object : WindowAdapter() {
                    override fun windowClosing(windowEvent: WindowEvent) {
                        System.exit(0)
                    }
                })
        controlPanel.setLayout(FlowLayout())
        mainFrame.add(headerLabel)
        mainFrame.add(spacesLabel)
        mainFrame.add(carsLabel)
        mainFrame.add(queueLabel)
        mainFrame.add(parkedLabel)
        mainFrame.add(Exit1Label)
        mainFrame.add(Exit2Label)
        mainFrame.add(Exit3Label)
        mainFrame.add(controlPanel)
        mainFrame.setVisible(true)

        val runButton = JButton("Run")
        val stopButton = JButton("Stop")
        val exitButton = JButton("Exit")
        runButton.addActionListener(
                object : ActionListener {
                    override fun actionPerformed(e: ActionEvent) {
                        if (!running) {
                            running = true
                            valet = Valet(carpark, carsLabel, spacesLabel, parkedLabel, queueLabel)
                            clock = Clock(headerLabel, seed)
                            in1 = Entrance(valet, 1, clock)
                            in2 = Entrance(valet, 2, clock)
                            in3 = Entrance(valet, 3, clock)
                            out1 = Exit(valet, 1, clock, Exit1Label)
                            out2 = Exit(valet, 2, clock, Exit2Label)
                            out3 = Exit(valet, 3, clock, Exit3Label)
                            valet.execute()
                            clock.execute()
                            in1.execute()
                            in2.execute()
                            in3.execute()
                            out1.execute()
                            out2.execute()
                            out3.execute()
                        }
                    }
                })
        stopButton.addActionListener(
                object : ActionListener {
                    override fun actionPerformed(e: ActionEvent) {
                        if (running) {
                            running = false
                            clock.cancel(false)
                            valet.cancel(false)
                            in1.cancel(true)
                            in2.cancel(true)
                            in3.cancel(true)
                            out1.cancel(true)
                            out2.cancel(true)
                            out3.cancel(true)
                        }
                    }
                })
        exitButton.addActionListener(
                object : ActionListener {
                    override fun actionPerformed(e: ActionEvent) {
                        System.exit(0)
                    }
                })
        controlPanel.add(runButton)
        controlPanel.add(stopButton)
        controlPanel.add(exitButton)
        mainFrame.setVisible(true)
    }
}