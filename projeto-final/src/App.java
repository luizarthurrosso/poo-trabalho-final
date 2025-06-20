import javax.swing.SwingUtilities;

import view.TelaImportacao;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TelaImportacao().setVisible(true);
            }
        });
    }
}
