package com.devcolibri.database;


import com.mysql.cj.jdbc.MysqlDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/ilon_companies"
            +
            "?verifyServerCertificate=false" +
            "&useSSL=false" +
            "&requireSSL=false" +
            "&useLegacyDatetimeCode=false" +
            "&amp" +
            "&serverTimezone=UTC"; // use false, чтобы не выдавало ошибку, связанную с ussl и правильно устанавливало время
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static Connection con;


    public static void main(String[] args) {

        try {
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // { //изменение уровня языка: Project structure - Modules - Sources - изменить тут уровень языка на 1.8
            // и в Settings -> Build, Execution, Deployment ->Compiler -> Java Compiler -> установить версию 1.8\
            // можно и через pom.xml https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-source-and-target.html
//            statement.execute("insert into")
            if (!con.isClosed()) {
                System.out.println("соединение с бд установлено!");
            }
            while (true) {
                int userChoice = chooseAValue();
                switch (userChoice) {
                    case 1:{
                        insert("INSERT INTO `ilon_companies`.`director` (`first_name`, `last_name`, `age`, `sex`) VALUES ('Vasia', 'Rozhkov', '55', '1');");
                        continue;}
                    case 2:{
                        updateTable("update director set first_name = 'женя' where first_name = 'vasia';");
                        continue;}
                    case 3:{
                        delete(" delete from director where first_name = 'женя';");
                        continue;}
                    case 4:{
                        getTable("select* from director;");
                        continue;}
                    case 5:{
                        executeBatch();
                        continue;}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int chooseAValue() {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String text = "Введите значение операции: \nДоступные операции: \n1-вставка новой записи \n2-изменение записи\n" +
                "3-удаление записи \n4-вывод всех значений \n5-единовременное исполнение последовательности запросов";
        System.out.println(text);
        while (true) {
            try {
                int value = Integer.parseInt(bf.readLine());
                if (value < 6) {
                    return value;
                }
                System.out.println("Введенное значение не является допустимым. Введите значение от 1 до 5");
            } catch (Exception e) {
                System.out.println("Введенное значение недопустимо. Введите число");
            }
            continue;
        }
    }

    public static void insert(String comand) {
        try (Statement st = con.createStatement()) {
            st.execute(comand); //универсальная команда, с помощью её можно делать вставку, получение. может получать несколько
            // резалт сетов


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public static void updateTable(String comand) {
        try (Statement st = con.createStatement()) {
            st.executeUpdate(comand); // с помощью executeUpdate можно делать запросы типа insert, update, delete, можно выполнять
            // с его помощью можно менять таблицы. что еще важно: он возвращает интовое значение с кол-вом строк, в которых
            // произошли изменения.

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void delete(String comand) {
        try (Statement st = con.createStatement()) {
            st.executeUpdate(comand);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void getTable(String comand) {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(comand);) { // позволяет вытаскивать result set (еще не полностью разобрался с ними)
            int columns = rs.getMetaData().getColumnCount();// высчитывает количество колонок
            while (rs.next()) { //изначально next на нулевой строке, каждый вызов next - это переход на новую строку
                for (int i = 1; i <= columns; i++) { //
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
                //resultset можно не закрывать, т.к. он закрывается автоматически при закрытии statement;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void executeBatch() { //execute batch позволяет отправить на выполнение сразу нескольк команд
        try (Statement st = con.createStatement()) {
            st.addBatch("insert into director (first_name, last_name, age, sex) VALUES ('Vitya', 'Lezov', 25, 1 );");
            st.addBatch("insert into director (first_name, last_name, age, sex) VALUES ('Maksim', 'Miranovich', 25, 1 );");
            st.executeBatch();
            st.clearBatch();

        } catch (SQLException e) {
            System.out.println("ошибка");
        }
    }
}
