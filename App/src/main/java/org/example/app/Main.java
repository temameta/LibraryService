package org.example.app;


import org.example.library.api.Library;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        try (Library library = new Library(" ")) {
            System.out.println("Простая CLI программа для демонстрации работы библиотеки для индексации файлов");
            System.out.println("Введите lib shutdown для завершения");
            System.out.println("Введите lib help для вывода доступных команд");
            String command;
            do {
                command = sc.nextLine().strip();
                if (command.startsWith("lib")) {
                    command = command.substring(4);
                    if (command.startsWith("help")) {
                        System.out.println("indexdir [path] - добавляет в индекс директорию");
                        System.out.println("indexfile [path] - добавляет в индекс файл");
                        System.out.println("search [word] - выведет все файлы, содержащее слово");
                    } else if (command.startsWith("indexdir")) {
                        Path path = Path.of(command.substring(9));
                        library.indexDirectory(path);
                    } else if (command.startsWith("indexfile")) {
                        Path path = Path.of(command.substring(10));
                        library.indexFile(path);
                    } else if (command.startsWith("search")) {
                        String word = command.substring(7);
                        library.search(word).forEach(path -> System.out.println(path.toString()));
                    } else System.out.println("Неизвестная команда. Введите lib help для вывода доступных команд");
                } else System.out.println("Имя " + command.split(" ")[0] + " не распознано как имя командлета");
            } while (!command.equals("shutdown"));
            System.out.println("Завершение работы программы");
        }
    }
}