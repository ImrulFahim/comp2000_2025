import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.time.Duration;
import java.time.Instant;

public class Main extends JFrame {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Main...");
        Main window = new Main();
        window.run();
    }

    class Canvas extends JPanel implements MouseListener {
        Stage stage;

        public Canvas() {
            System.out.println("Creating Canvas...");
            setPreferredSize(new Dimension(1024, 720));
            this.addMouseListener(this);

            // Try to load stage from file
            Stage loaded = StageReader.readStage("data/stage1.rvb");
            if (loaded != null) {
                System.out.println("Stage loaded from data/stage1.rvb");
                stage = loaded;
            } else {
                System.out.println("StageReader returned null, using default Stage()");
                stage = new Stage();
            }

            // 🔗 Start streaming weather into this stage
            System.out.println("Starting weather stream...");
            Client.startWeatherStream(stage::applyWeather);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g); // clear background
            Point mousePos = getMousePosition();
            if (stage != null) {
                stage.paint(g, mousePos);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (stage != null) {
                stage.mouseClicked(e.getX(), e.getY());
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }

    private Main() {
        System.out.println("Creating Main window...");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas canvas = new Canvas();
        this.setContentPane(canvas);
        this.pack();
        this.setVisible(true);
        System.out.println("Window visible.");
    }

    public void run() {
        System.out.println("Entering main loop...");
        while (true) {
            Instant startTime = Instant.now();
            repaint();
            Instant endTime = Instant.now();
            long howLong = Duration.between(startTime, endTime).toMillis();
            long sleep = 20L - howLong;
            if (sleep < 1L) sleep = 1L; // avoid negative sleep
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("thread was interrupted, nothing to worry about!");
            }
        }
    }
}

