package Game;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import util.DrawUtils;
import util.JTableUtils;
import util.SwingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MainForm extends JFrame {
    private JPanel panelMain;
    private JTable tableGameField;
    private JLabel score;
    private JLabel nameScore;
    private ImageIcon icon = new ImageIcon("E:\\Game2048\\src\\2048_icon.png");

    private static int openedDialogs = 0;
    private static final int DEFAULT_COL_COUNT = 4;
    private static final int DEFAULT_ROW_COUNT = 4;
    private static final int DEFAULT_GAP = 8;
    private static final int DEFAULT_CELL_SIZE = 170;

    private static final Map<Integer, Color> COLORS = new HashMap<Integer, Color>() {{
        put(2, Color.BLACK);
        put(4, Color.GRAY);
        put(8, Color.WHITE);
        put(16, Color.YELLOW);
        put(32, Color.ORANGE);
        put(64, Color.RED);
        put(128, Color.MAGENTA);
        put(256, Color.PINK);
        put(512, Color.GREEN);
        put(1024, Color.CYAN);
        put(2048, Color.BLUE);
    }};

    private GameParams params = new GameParams(DEFAULT_ROW_COUNT, DEFAULT_COL_COUNT);
    private Game game = new Game();
    private ParamsDialog dialogParams;


    public MainForm() {
        this.setTitle("2048");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(icon.getImage());
        this.pack();

        setJMenuBar(createMenuBar());
        this.pack();

        SwingUtils.setShowMessageDefaultErrorHandler();

        score.setFont(getFont(30));
        nameScore.setFont(getFont(20));

        tableGameField.setRowHeight(DEFAULT_CELL_SIZE);
        JTableUtils.initJTableForArray(tableGameField, DEFAULT_CELL_SIZE, false, false, false, false);
        tableGameField.setIntercellSpacing(new Dimension(0, 0));
        tableGameField.setEnabled(false);
        
        tableGameField.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final class DrawComponent extends Component {
                private int row = 0, column = 0;

                @Override
                public void paint(Graphics gr) {
                    Graphics2D g2d = (Graphics2D) gr;
                    int width = getWidth() - 2;
                    int height = getHeight() - 2;
                    paintCell(row, column, g2d, width, height);
                }
            }

            DrawComponent comp = new DrawComponent();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                comp.row = row;
                comp.column = column;
                return comp;
            }
        });

        newGame();

        updateWindowSize();
        updateView();

        dialogParams = new ParamsDialog(params, tableGameField, e -> newGame());


        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (openedDialogs == 0) {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D' || e.getKeyChar() == 'В' || e.getKeyChar() == 'в') {
                            game.right();
                        }
                        if (e.getKeyChar() == 's' || e.getKeyChar() == 'S' || e.getKeyChar() == 'ы' || e.getKeyChar() == 'Ы') {
                            game.down();
                        }
                        if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A' || e.getKeyChar() == 'ф' || e.getKeyChar() == 'Ф') {
                            game.left();
                        }
                        if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W' || e.getKeyChar() == 'Ц' || e.getKeyChar() == 'ц') {
                            game.up();
                        }
                        score.setText(String.valueOf(game.getScore()));
                        updateView();
                        gameWinOrOver();
                    }
                }
                return false;
            }
        });
    }

    private void gameWinOrOver() {
        if (!game.haveAnyoneMove()) {
            if (game.win()) {
                showDialog("Победа!!! Ваш набранный счет: " + score.getText());
            } else {
                showDialog("Проигрыш!!! Ваш набранный счет: " + score.getText());
            }
        } else {
            if (game.win()) {
                showDialog("Победа!!! Ваш набранный счет: " + score.getText());
            }
        }
    }

    private void showDialog(String message) {
        if (openedDialogs > 0) {
            return;
        }
        openedDialogs++;

        //Создаем диалог
        JDialog dialog = new JDialog();
        dialog.setTitle("Игра окончена");
        dialog.setIconImage(icon.getImage());
        dialog.setModal(true);
        dialog.setSize(700, 200);

        // Устанавливаем позицию диалога
        Dimension panelSize = panelMain.getSize();
        Dimension dialogSize = dialog.getSize();
        int x = (panelSize.width - dialogSize.width) / 2 + panelMain.getX();
        int y = (panelSize.height - dialogSize.height) / 2 + panelMain.getY();
        dialog.setLocation(x, y);

        //Назначаем кнопке закрытия диалога закрытие всего приложения
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        // Добавление сообщения о результате игры в диалог
        String text = "<html><div style='text-align: center;'>"
                + message + "<br>" + "Вы хотите продолжить?" + "</div></html>";
        JLabel messageLabel = new JLabel(text);
        messageLabel.setFont(getFont(30));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.getContentPane().add(messageLabel, BorderLayout.CENTER);

        // Создание кнопок с заданным размером
        JButton yesButton = new JButton("Да, конечно!");
        yesButton.setFont(getFont(20));
        yesButton.setPreferredSize(new Dimension(200, 50));
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                openedDialogs--;
                newGame();
            }
        });

        JButton noButton = new JButton("Нет, спасибо!");
        noButton.setPreferredSize(new Dimension(200, 50));
        noButton.setFont(getFont(20));
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Добавление кнопок в диалог
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(yesButton);
        panel.add(noButton);
        dialog.getContentPane().add(panel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JMenuItem createMenuItem(String text, String shortcut, Character mnemonic, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        if (shortcut != null) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcut.replace('+', ' ')));
        }
        if (mnemonic != null) {
            menuItem.setMnemonic(mnemonic);
        }
        return menuItem;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBarMain = new JMenuBar();

        JMenu menuGame = new JMenu("Игра");
        menuBarMain.add(menuGame);
        menuGame.add(createMenuItem("Новая", "ctrl+N", null, e -> {
            newGame();
        }));
        menuGame.add(createMenuItem("Параметры", "ctrl+P", null, e -> {
            dialogParams.updateView();
            dialogParams.setVisible(true);
        }));
        menuGame.addSeparator();
        menuGame.add(createMenuItem("Выход", "ctrl+X", null, e -> {
            System.exit(0);
        }));

        JMenu menuView = new JMenu("Вид");
        menuBarMain.add(menuView);
        menuView.add(createMenuItem("Подогнать размер окна", null, null, e -> {
            updateWindowSize();
        }));
        menuView.addSeparator();
        SwingUtils.initLookAndFeelMenu(menuView);

        return menuBarMain;
    }

    private void updateWindowSize() {
        int menuSize = this.getJMenuBar() != null ? this.getJMenuBar().getHeight() : 0;
        int scoreSize = score.getHeight();
        SwingUtils.setFixedSize(
                this,
                tableGameField.getWidth() + 2 * DEFAULT_GAP + 60,
                tableGameField.getHeight() + panelMain.getY() +
                        menuSize + scoreSize + 1 * DEFAULT_GAP + 2 * DEFAULT_GAP + 60
        );
        this.setMaximumSize(null);
        this.setMinimumSize(null);
    }

    private void updateView() {
        tableGameField.repaint();
    }


    private Font font = null;

    private Font getFont(int size) {
        if (font == null || font.getSize() != size) {
            font = new Font("Comic Sans MS", Font.BOLD, size);
        }
        return font;
    }

    private void paintCell(int row, int column, Graphics2D g2d, int cellWidth, int cellHeight) {
        int cellValue = game.getCell(row, column);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (cellValue <= 0) {
            return;
        }
        Color color = COLORS.get(cellValue);
        int size = Math.min(cellWidth, cellHeight);
        int bound = (int) Math.round(size * 0.1);

        g2d.setColor(color);
        g2d.fillRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(bound, bound, size - 2 * bound, size - 2 * bound, bound * 3, bound * 3);

        g2d.setFont(getFont(size - 7 * bound));
        g2d.setColor(DrawUtils.getContrastColor(color));
        DrawUtils.drawStringInCenter(g2d, font, "" + cellValue, 0, 0, cellWidth, (int) Math.round(cellHeight * 0.95));
    }

    private void newGame() {
        game.newGame(params.getRowCount(), params.getColCount(), 0);
        score.setText("0");
        JTableUtils.resizeJTable(tableGameField,
                game.getRowCount(), game.getColCount(),
                tableGameField.getRowHeight(), tableGameField.getRowHeight()
        );
        updateView();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(2, 5, new Insets(10, 10, 10, 10), -1, 10));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelMain.add(scrollPane1, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableGameField = new JTable();
        scrollPane1.setViewportView(tableGameField);
        nameScore = new JLabel();
        nameScore.setText("Текущий счет:");
        panelMain.add(nameScore, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        score = new JLabel();
        score.setText("0");
        panelMain.add(score, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panelMain.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panelMain.add(spacer2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

}
