import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class InvalidDataFormatException extends Exception {
    InvalidDataFormatException(String message) {
        super(message);
    }
}

class UserData {
    private String surname;
    private String name;
    private String patronymic;
    private String birthDate;
    private long phoneNumber;
    private char gender;

    UserData(String surname, String name, String patronymic, String birthDate, long phoneNumber, char gender) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    public String getFullName() {
        return surname + " " + name + " " + patronymic;
    }

    public String getFormattedData() {
        return String.format("%s %s %s %s %d %c", surname, name, patronymic, birthDate, phoneNumber, gender);
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public char getGender() {
        return gender;
    }
}

public class Main {
    private static final List<UserData> userDataList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        File folder = new File("./users");
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try {
                        List<UserData> dataList = readUserDataFromFile(file);
                        userDataList.addAll(dataList);
                    } catch (IOException e) {
                        System.err.println("Ошибка при чтении файла " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("Existed users:");
        // Вывод списка данных
        for (UserData userData : userDataList) {
            System.out.println(userData.getFormattedData());
        }

        while (true) {
            System.out.println("Chose action:");
            System.out.println("1. Create new account");
            System.out.println("2. Search");
            System.out.println("3. Search and delete");
            System.out.println("4. Quit");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createNewRecord(scanner);
                    break;
                case "2":
                    search(scanner);
                    break;
                case "3":
                    searchAndDelete(scanner);
                    break;
                case "4":
                    System.out.println("Shutdown.");
                    return;
                default:
                    System.out.println("Invalid input. Please select an action from the list.");
            }
        }
    }

    private static List<UserData> readUserDataFromFile(File file) throws IOException {
        List<UserData> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Парсинг строки файла и создание объекта UserData
                // Предполагается, что данные в файле разделены пробелом или другим разделителем
                String[] parts = line.split("\\s+"); // Разделение строки по пробелам
                // Создание объекта UserData из частей строки
                UserData userData = new UserData(parts[0], parts[1], parts[2], parts[3], Long.parseLong(parts[4]), parts[5].charAt(0));
                dataList.add(userData);
            }
        }
        return dataList;
    }

    private static void printUserData(UserData userData) {
        System.out.println("Second Name: " + userData.getSurname());
        System.out.println("Name: " + userData.getName());
        System.out.println("patronymic: " + userData.getPatronymic());
        System.out.println("Birthday: " + userData.getBirthDate());
        System.out.println("Phone number: " + userData.getPhoneNumber());
        System.out.println("Gender: " + userData.getGender());
    }

    private static void saveToFile(UserData userData) {
        String filename = "users" + File.separator + userData.getSurname() + ".txt";
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(userData.getFormattedData() + "\n"); // Добавляем отформатированные данные пользователя
            System.out.println("Data is saved to file " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void createNewRecord(Scanner scanner) {
        System.out.println("Enter data in the format: Last Name First Name Patronymic DD.MM.YYYY Phone Number Gender (m/f)");
        String input = scanner.nextLine();
        try {
            UserData userData = parseUserInput(input);
            userDataList.add(userData);
            saveToFile(userData);
        } catch (InvalidDataFormatException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static UserData  search(Scanner scanner) {
        System.out.println("Select search criteria:");
        System.out.println("1. Search by last name");
        System.out.println("2. Search by name");
        System.out.println("3. Search by phone number");
        System.out.println("4. Back");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                return searchBySurname(scanner);
                
            case "2":
                return searchByName(scanner);
                
            case "3":
                return searchByPhoneNumber(scanner);
                
            case "4":
                return null;
            default:
                System.out.println("Invalid input. Please select a search term from the list.");
        }


        return null;
    }

    private static void searchAndDelete(Scanner scanner) {
        UserData userData = search(scanner);

        if (userData != null) {
            String lastName = userData.getSurname();
            deleteFile("./users/" + lastName + ".txt");
        } else {
            System.out.println("User wasnt found");
        }


    }

    private static void deleteFile(String filename) {
        File file = new File(filename);
        if (file.exists()) { // Проверяем, существует ли файл
            if (file.delete()) { // Удаляем файл и проверяем успешность операции
                System.out.println("User removed successfully: " + filename);
            } else {
                System.out.println("Unable to remove file: " + filename);
            }
        } else {
            System.out.println("File wasnt found: " + filename);
        }
    }

    private static UserData searchBySurname(Scanner scanner) {
        System.out.println("Enter last name to search:");
        String searchQuery = scanner.nextLine().toLowerCase();
        for (UserData userData : userDataList) {
            if (userData.getSurname().toLowerCase().contains(searchQuery)) {
                System.out.println("Record found:");
                printUserData(userData);
                return userData;
            }
        }
        System.out.println("No record found with this last name.");

        return null;
    }

    private static UserData searchByPhoneNumber(Scanner scanner) {
        System.out.println("Enter your phone number to search:");
        long searchQuery = Long.parseLong(scanner.nextLine());
        for (UserData userData : userDataList) {
            if (userData.getPhoneNumber() == searchQuery) {
                System.out.println("Record found:");
                printUserData(userData);
                return userData;
            }
        }
        System.out.println("No record found with this phone number.");

        return null;
    }

    private static UserData searchByName(Scanner scanner) {
        System.out.println("Enter name to proceed:");
        String searchQuery = scanner.nextLine().toLowerCase();
        System.out.println(searchQuery);
        for (UserData userData : userDataList) {
            if (userData.getName().toLowerCase().contains(searchQuery)) {
                System.out.println("Record found:");
                printUserData(userData);
                return userData;
            }
        }
        System.out.println("An entry with this name was not found.");

        return null;
    }

    private static UserData parseUserInput(String inputString) throws InvalidDataFormatException {
        System.out.println(inputString);
        String[] data = inputString.split("\\s+");
        if (data.length != 6) {
            throw new InvalidDataFormatException("Invalid amount of data.");
        }
    
        String surname = data[0];
        String name = data[1];
        String patronymic = data[2];
        String birthDate = data[3];

        long phoneNumber;
        try {
            phoneNumber = Long.parseLong(data[4]);
        } catch (NumberFormatException e) {
            throw new InvalidDataFormatException("Invalid phone number format.");
        }
        String gender = data[5].toLowerCase(); // Преобразование строки к нижнему регистру

        if (!gender.equals("m") && !gender.equals("f")) {
            throw new InvalidDataFormatException("Invalid gender. Use 'm' or 'f'.");
        }
    
        return new UserData(surname, name, patronymic, birthDate, phoneNumber, gender.charAt(0));
    }
}
