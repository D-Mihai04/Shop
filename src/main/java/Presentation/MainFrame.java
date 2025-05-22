package Presentation;

import javax.swing.*;


/**
 *MainFrame is the main page of the aplication.
 * It contains all tabs for the tables.
 */
public class MainFrame extends JFrame {
    private final JTabbedPane tabbedPane;
    private final Presentation.ClientPanel clientPanel;
    private final Presentation.ProductPanel productPanel;
    private final Presentation.OrderPanel orderPanel;
    private final Presentation.BillPanel billPanel;


    public MainFrame() {
        setTitle("Shop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        clientPanel = new Presentation.ClientPanel();
        productPanel = new Presentation.ProductPanel();
        orderPanel = new Presentation.OrderPanel();
        billPanel = new Presentation.BillPanel();

        tabbedPane.addTab("Clients", clientPanel);
        tabbedPane.addTab("Prodcts", productPanel);
        tabbedPane.addTab("Orders", orderPanel);
        tabbedPane.addTab("Bills", billPanel);

        add(tabbedPane);
    }
}
