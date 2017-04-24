
public class Animator implements Runnable {

    // Volatile: http://stackoverflow.com/questions/2491588/how-a-thread-should-close-itself-in-java
    volatile boolean running;
    
    GamePanel gamePanel = null;
    GameData gameData = null;

    public Animator() {
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            gameData.update();
            gamePanel.gameRender();
            gamePanel.printScreen();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
            }
        }
    }
}
