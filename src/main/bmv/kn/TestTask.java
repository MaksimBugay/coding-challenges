package bmv.kn;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestTask {
    public static void main(String[] args) {
        // For all books of authors with name "John" calculate prices with discount 20% and sort them (prices) in
        // descending order

        // Example
        List<Book> books = Arrays.asList(
                new Book(new Author("John", "Deer"), "Blue book", 13.50),
                new Book(new Author("David", "Cameron"), "Black book", 3.30),
                new Book(new Author("John", "Davidson"), "Red book", 27.50)
        );

        // Expected output
        // [22.0, 10.8]
        books.stream()
            .filter(book -> book.author != null && "John".equals(book.author.name))
            .map(book -> book.getPrice()*0.8)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList()).forEach(System.out::println);
    }

    static class Book {
        private Author author;
        private String name;
        private double price;

        public Book(Author author, String name, double price) {
            this.author = author;
            this.name = name;
            this.price = price;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    static class Author {
        private String name;
        private String surname;

        public Author(String name, String surname) {
            this.name = name;
            this.surname = surname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }
}