import com.sun.xml.internal.bind.v2.TODO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by David on 9/10/2017.
 */
public class BookGUI extends JFrame{

    private JPanel mainPanel;
    private JComboBox searchBy;
    private JTextField searchText;
    private JButton searchButton;
    private JButton addButton;
    private JButton removeButton;
    private JScrollPane googleBooksList;
    private JLabel statusLabel;
    private JList<BookClass> googleBooks;
    private DefaultListModel<BookClass> googleBooksModel;
    ArrayList<BookClass> allBooks = new ArrayList<BookClass>();

    private void findBook(String query) {
        String key = getKey();
        String baseURL = "https://www.googleapis.com/books/v1/volumes?q=%s&key=%s";
        String url = String.format(baseURL, query, key);
        InputStream stream = null;
        try {
            stream = new URL(url).openConnection().getInputStream();

        }
        catch (IOException ioe){
            statusLabel.setText("IO exception");
        }
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuilder builder = new StringBuilder();

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        }catch (IOException ioe){
            statusLabel.setText("Problem");
        }
        String responseString = builder.toString();


        JSONObject jsonObject = new JSONObject(responseString);
        JSONArray items = jsonObject.getJSONArray("items");

        for (int count = 0; count != items.length();count++) {
            JSONObject volumeInfo = items.getJSONObject(count).getJSONObject("volumeInfo");
            String title = volumeInfo.getString("title");
            String description = volumeInfo.getString("description");
            String publisher = volumeInfo.getString("publisher");
            String authors;
            String isbn;
            try {
                JSONArray authorList = volumeInfo.getJSONArray("authors");
            }catch (JSONException je) {
                System.out.println("No author(s) listed for this book.");
            }

            double googleRating = 0;
            try{
                googleRating = volumeInfo.getDouble("averageRating");
            }catch (JSONException je) {
                System.out.println("No rating provided for this book.");
            }
            BookClass book = new BookClass(title,authors="none",publisher,description,isbn ="none",googleRating);
            allBooks.add(book);


        }}





    BookGUI(){
        populateSearchType();

        googleBooks = new JList<BookClass>();
        googleBooksList = new JScrollPane(googleBooks);
        setContentPane(mainPanel);
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        googleBooksModel = new DefaultListModel<BookClass>();
        googleBooks.setModel(googleBooksModel);
        setVisible(true);


        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String query = searchText.getText();
                String formatedQuery = formatQuery(query);
                findBook(formatedQuery);
                setListData(allBooks);
            }
        });
/*       addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO remove book from library list
            }
        });*/
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

    private String formatQuery(String query) {
        query = query.replaceAll("\\s","+");
        return query;
    }




    private String getKey(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("key.txt"));
            String key = bufferedReader.readLine();
            if (key == null){
                statusLabel.setText("Key not found. Paste your key in the first line of the key.txt file.");
                System.exit(-1);
            }
            return key;
        }
        catch (IOException ioe){
            statusLabel.setText("key.txt not found. Please create a key.txt file in the root directory.");
            System.exit(-1);
            return null;
        }
    }
    private void addBookToLibrary(){
        // TODO add the selected book to the library list
    }


    void setListData(ArrayList<BookClass> data){
        googleBooksModel.clear();
        for (BookClass book: data){
            googleBooksModel.addElement(book);
        }
    }

}
