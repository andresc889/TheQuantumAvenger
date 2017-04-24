
import javax.swing.JPanel;

public class ScreenEvent {
    public static final int START_EASY_GAME = 1;
    public static final int START_NORMAL_GAME = 2;
    public static final int START_HARD_GAME = 3;
    public static final int SHOW_WINNING_SCREEN = 4;
    public static final int SHOW_LOSING_SCREEN = 5;
    public static final int SHOW_MENU = 6;
    public static final int EXIT = 7;
    
    private JPanel source;
    private int type;

    public ScreenEvent(JPanel source, int type) {
        this.source = source;
        this.type = type;
    }

    public JPanel getSource() {
        return source;
    }

    public int getType() {
        return type;
    }
    
}
