import com.sun.xml.internal.bind.v2.TODO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Created by David on 9/10/2017.
 */
public class BookGUI extends JFrame{

    private JPanel mainPanel;
    private JComboBox searchBy;
    private JTextField searchText;
    private JButton searchButton;
    private JScrollPane googleBooksList;
    private JButton addButton;
    private JButton removeButton;
    private JLabel statusLabel;
    private JList<String> googleBooks;
    private JTextArea bookNameTextArea;
    private JTextField authorTextArea;
    private JTextField publisherTextField;
    private JTextField isbnTextField;
    private JTextField ratingTextField;
    private JTextArea descriptionTextArea;
    private JTextArea buyLinkTextArea;
    private JTextField buyLinkTextField;
    private JTextField descriptionTextField;
    private DefaultListModel<String> googleBooksModel;
    private int searchType;
    ArrayList<BookClass> allBooks = new ArrayList<BookClass>();


    BookGUI(){
        populateSearchType();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        googleBooksModel = new DefaultListModel<String>();
        googleBooks.setModel(googleBooksModel);
        setContentPane(mainPanel);
        pack();
        setVisible(true);



        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                allBooks.clear();
                googleBooksModel.clear();
                String query = searchText.getText();
                String formatedQuery = formatQuery(query);
                if (searchType ==0) {
                    findBook(formatedQuery);
                }
                else if (searchType == 1){
                    findBookByISBN(query);

                }
                else if(searchType ==2){
                    findBOoksByAuthor(query);

                }
                else if (searchType == 3){
                    findBooksByGenre(query);
                }
                setListData(allBooks);
            }
        });

        googleBooks.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selected = googleBooks.getSelectedIndex();
                displaySelectedBook(selected);
            }
        });
        searchBy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchType = searchBy.getSelectedIndex();
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
        searchList.add("Title");
        searchList.add("ISBN");
        searchList.add("Author");
        searchList.add("Genre");

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

        String[] names = new String[data.size()];
        int count = 0;
        for (BookClass book:data){
            names[count] = book.title;
            googleBooksModel.add(0,book.title);
        }

        Collections.reverse(allBooks);


    }
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
            System.out.println("Problem");
        }
        String responseString = builder.toString();


        JSONObject jsonObject = new JSONObject(responseString);
        System.out.println(jsonObject.length());

        JSONArray items = jsonObject.getJSONArray("items");

        for (int count = 0; count != items.length();count++) {
            JSONObject volumeInfo = items.getJSONObject(count).getJSONObject("volumeInfo");
            String id = items.getJSONObject(count).getString("id");
            String title = volumeInfo.getString("title");
            String description = "";
            try {
                description = volumeInfo.getString("description");
            }catch (JSONException jse){
                description = "NA";
            }


            String publisher = "not found";
            try{
            publisher = volumeInfo.getString("publisher");
            }catch (JSONException jse){
                System.out.println("Publisher not found");
            }
            String authors = "";
            String isbn;

            JSONArray authorList = new JSONArray();
            try {
                authorList = volumeInfo.getJSONArray("authors");
            }catch (JSONException je) {
                System.out.println("No author(s) listed for this book.");
            }

            for (Object author:authorList){
                author = author.toString();
                authors += author+", ";
            }
            authors = authors.substring(0, authors.length() - 2);


            double googleRating = 0;
            try{
                googleRating = volumeInfo.getDouble("averageRating");
            }catch (JSONException je) {
                System.out.println("No rating provided for this book.");
            }
            isbn = getIsbn(id);
            String buyLink = getBuyLink(id);

            BookClass book = new BookClass(title,authors,publisher,description,isbn,googleRating,buyLink);
            allBooks.add(book);


        }}

    private String getIsbn(String id){
        String key = getKey();
        String baseURL = "https://www.googleapis.com/books/v1/volumes/%s?key=%s";
        String url = String.format(baseURL, id, key);
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
        JSONArray isbnArray = new JSONArray();
        String isbn = "";
        try{
            isbnArray = jsonObject.getJSONObject("volumeInfo").getJSONArray("industryIdentifiers");
            isbn = isbnArray.getJSONObject(0).getString("identifier");

        }catch (JSONException jse){
            isbn = "NA";
        }

        return isbn;
    }

    private String getBuyLink(String id){
        String key = getKey();
        String baseURL = "https://www.googleapis.com/books/v1/volumes/%s?key=%s";
        String url = String.format(baseURL, id, key);
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


        JSONObject saleInfo = jsonObject.getJSONObject("saleInfo");
        String buyLink = "";
        try{
            buyLink = saleInfo.getString("buyLink");
        }catch (JSONException jse){
            buyLink = "NA";
        }

        return buyLink;

    }




    private void displaySelectedBook(int selected){
        bookNameTextArea.setText(allBooks.get(selected).title);
        authorTextArea.setText(allBooks.get(selected).author);
        publisherTextField.setText(allBooks.get(selected).publisher);
        isbnTextField.setText(allBooks.get(selected).isbn);
        ratingTextField.setText(Double.toString(allBooks.get(selected).rating));
        descriptionTextArea.setText(allBooks.get(selected).description);
        buyLinkTextArea.setText(allBooks.get(selected).buyLink);
    }

    private void findBookByISBN(String isbn){
        String key = getKey();
        String baseURL = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s&key=%s";
        String url = String.format(baseURL, isbn, key);
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


            JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");
            String id = items.getJSONObject(0).getString("id");
            String title = volumeInfo.getString("title");
            String description = volumeInfo.getString("description");
            String publisher = "not found";
            try{
                publisher = volumeInfo.getString("publisher");
            }catch (JSONException jse){
                System.out.println("Publisher not found");
            }
            String authors = "";
            JSONArray authorList = new JSONArray();
            try {
                authorList = volumeInfo.getJSONArray("authors");
            }catch (JSONException je) {
                System.out.println("No author(s) listed for this book.");
            }

            for (Object author:authorList){
                author = author.toString();
                authors += author+", ";
            }
            authors = authors.substring(0, authors.length() - 2);


            double googleRating = 0;
            try{
                googleRating = volumeInfo.getDouble("averageRating");
            }catch (JSONException je) {
                System.out.println("No rating provided for this book.");
            }
            isbn = getIsbn(id);
            String buyLink = getBuyLink(id);

            BookClass book = new BookClass(title,authors,publisher,description,isbn,googleRating,buyLink);
            allBooks.add(book);
    }

    private void findBOoksByAuthor(String author){
        String key = getKey();
        String baseURL = "https://www.googleapis.com/books/v1/volumes?q=inauthor:%s&key=%s";
        String url = String.format(baseURL, author, key);
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
            String id = items.getJSONObject(count).getString("id");
            String title = volumeInfo.getString("title");
            String description = "";
            try {
                description = volumeInfo.getString("description");
            }catch (JSONException jse){
                description = "NA";
            }
            String publisher = "not found";
            try{
                publisher = volumeInfo.getString("publisher");
            }catch (JSONException jse){
                System.out.println("Publisher not found");
            }

            String isbn;

            double googleRating = 0;
            try{
                googleRating = volumeInfo.getDouble("averageRating");
            }catch (JSONException je) {
                System.out.println("No rating provided for this book.");
            }
            isbn = getIsbn(id);
            String buyLink = getBuyLink(id);

            BookClass book = new BookClass(title,author,publisher,description,isbn,googleRating,buyLink);
            allBooks.add(book);
    }
}

    private void findBooksByGenre(String subject){
        String key = getKey();
        String baseURL = "https://www.googleapis.com/books/v1/volumes?q=subject:%s&key=%s";
        String url = String.format(baseURL, subject, key);
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
            String id = items.getJSONObject(count).getString("id");
            String title = volumeInfo.getString("title");
            String description = "";
            try {
                description = volumeInfo.getString("description");
            }catch (JSONException jse){
                description = "NA";
            }
            String publisher = "not found";
            try{
                publisher = volumeInfo.getString("publisher");
            }catch (JSONException jse){
                System.out.println("Publisher not found");
            }
            String authors = "";
            JSONArray authorList = new JSONArray();
            try {
                authorList = volumeInfo.getJSONArray("authors");
            }catch (JSONException je) {
                System.out.println("No author(s) listed for this book.");
            }

            for (Object author:authorList){
                author = author.toString();
                authors += author+", ";
            }
            authors = authors.substring(0, authors.length() - 2);

            String isbn;

            double googleRating = 0;
            try{
                googleRating = volumeInfo.getDouble("averageRating");
            }catch (JSONException je) {
                System.out.println("No rating provided for this book.");
            }
            isbn = getIsbn(id);
            String buyLink = getBuyLink(id);

            BookClass book = new BookClass(title,authors,publisher,description,isbn,googleRating,buyLink);
            allBooks.add(book);
        }
}
}




