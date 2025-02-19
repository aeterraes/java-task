package aeterraes.console;

import aeterraes.client.SFTPClient;
import aeterraes.command.*;
import aeterraes.handler.CustomJSONHandler;
import com.jcraft.jsch.JSchException;

import java.util.Scanner;

public class Runner {
    private final Scanner scanner = new Scanner(System.in);
    private SFTPClient sftpClient;
    private CustomJSONHandler handler;
    private CommandInvoker invoker;

    public void run() {
        try {
            initializeSFTP();
            initializeHandler();
            initializeCommands();
            mainLoop();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        } finally {
            if (sftpClient != null) {
                sftpClient.disconnect();
            }
        }
    }

    private void initializeSFTP() throws JSchException {
        System.out.print("Введите хост: ");
        String host = scanner.nextLine();
        System.out.print("Введите порт: ");
        int port = Integer.parseInt(scanner.nextLine());
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        sftpClient = new SFTPClient(host, port, username, password);
        sftpClient.connect();
    }

    private void initializeHandler() throws Exception {
        String remoteFilePath = sftpClient.getAddressesFileName();
        sftpClient.createAddressesFileIfNotExists();
        handler = new CustomJSONHandler(remoteFilePath);
        handler.loadFromFile();
    }

    private void initializeCommands() {
        invoker = new CommandInvoker();
        invoker.register("1", new GetSortedDataCommand(handler));
        invoker.register("2", new GetIpByDomainCommand(handler, ""));
        invoker.register("3", new GetDomainByIpCommand(handler, ""));
        invoker.register("4", new AddPairCommand(handler, "", ""));
        invoker.register("5", new DeletePairByDomainCommand(handler, ""));
        invoker.register("6", new DeletePairByIpCommand(handler, ""));
    }

    private void mainLoop() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            if ("0".equals(choice)) {
                break;
            }

            handleUserInput(choice);
            invoker.execute(choice);
        }
    }

    private void printMenu() {
        System.out.println("\nВыберите команду:");
        System.out.println("1 - Получить список доменов и IP");
        System.out.println("2 - Найти IP по домену");
        System.out.println("3 - Найти домен по IP");
        System.out.println("4 - Добавить новую пару");
        System.out.println("5 - Удалить по домену");
        System.out.println("6 - Удалить по IP");
        System.out.println("0 - Выйти");
        System.out.print("Ваш выбор: ");
    }

    private void handleUserInput(String choice) {
        switch (choice) {
            case "2":
            case "3":
                System.out.print("Введите значение: ");
                String input = scanner.nextLine();
                if (choice.equals("2") && handler.isValidDomain(input)) {
                    invoker.register("2", new GetIpByDomainCommand(handler, input));
                } else if (choice.equals("3") && handler.isValidAddress(input)) {
                    invoker.register("3", new GetDomainByIpCommand(handler, input));
                } else {
                    System.out.println("Некорректный ввод");
                }
                break;
            case "4":
                System.out.print("Введите домен: ");
                String domainToAdd = scanner.nextLine();
                System.out.print("Введите IP: ");
                String ipToAdd = scanner.nextLine();
                invoker.register("4", new AddPairCommand(handler, domainToAdd, ipToAdd));
                break;
            case "5":
            case "6":
                if (choice.equals("5")) {
                    System.out.print("Введите домен: ");
                    String domain = scanner.nextLine();
                    invoker.register("5", new DeletePairByDomainCommand(handler, domain));
                } else {
                    System.out.print("Введите IP: ");
                    String ip = scanner.nextLine();
                    invoker.register("6", new DeletePairByIpCommand(handler, ip));
                }
                break;
        }
    }
}
