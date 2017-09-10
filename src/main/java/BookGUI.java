import com.sun.xml.internal.bind.v2.TODO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by David on 9/10/2017.
 */
public class BookGUI extends JFrame{

    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JPanel googleBooksTab;
    private JPanel libraryTab;
    private JComboBox searchBy;
    private JTextField searchText;
    private JButton searchButton;
    private JButton addButton;
    private JComboBox sortByComboBox;
    private JButton removeButton;
    private JScrollPane googleBooksList;


    BookGUI(){
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        populateSearchType();


        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String query = searchText.getText();
                findBook(query);
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO remove book from library list
            }
        });
    }
    private void populateSearchType(){
        ArrayList<String> searchList = new ArrayList<String>();
        searchList.add("Author");
        searchList.add("ISBN");
        searchList.add("Name");

        for (String item : searchList){
            searchBy.addItem(item);
        }
    }

    private void findBook(String query){
        // TODO implement API with query, also sort by search type, then add to book class and results list
    }
    private void addBookToLibrary(){
        // TODO add the selected book to the library list
    }
}
