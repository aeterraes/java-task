package aeterraes.client;

import com.jcraft.jsch.*;
import java.io.*;
import java.util.Properties;

public class SFTPClient {
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private Session session;
    private ChannelSftp sftpChannel;
    private static final String ADDRESSES = "addresses_list";

    public SFTPClient(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void connect() throws JSchException {
        JSch jsch = new JSch();
        String knownHostsPath = System.getProperty("user.home") + "/.ssh/known_hosts";

        File knownHostsFile = new File(knownHostsPath);
        if (!knownHostsFile.exists()) {
            System.err.println("Файл known_hosts отсутствует! Подключение запрещено.");
            System.out.println("Добавьте ключ сервера вручную: ssh-keyscan -H " + host + " >> " + knownHostsPath);
            return;
        }

        jsch.setKnownHosts(knownHostsPath);

        session = jsch.getSession(username, host, port);
        session.setPassword(password);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "yes");
        config.put("PreferredAuthentications", "password");
        config.put("MaxAuthTries", "3");
        config.put("ConnectTimeout", "10000");
        session.setConfig(config);

        try {
            session.connect();
            if (session.isConnected()) {
                System.out.println("Успешно подключено к " + host);
            }

            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
        } catch (JSchException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
            if (e.getMessage().contains("UnknownHostKey")) {
                System.err.println("Сервер не найден в known_hosts");
            }
            throw e;
        }
    }

    public void disconnect() {
        if (sftpChannel != null) {
            sftpChannel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
        System.out.println("Завершение работы");
    }

    public void createAddressesFileIfNotExists() throws SftpException {
        try {
            sftpChannel.lstat(ADDRESSES);
            System.out.println("Файл " + ADDRESSES + " уже существует");
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                System.out.println("Файл " + ADDRESSES + " не найден. Создаю файл...");
                try (InputStream input = new ByteArrayInputStream(new byte[0])) {
                    sftpChannel.put(input, ADDRESSES);
                } catch (IOException ioException) {
                    System.err.println("Ошибка при создании файла: " + ioException.getMessage());
                }
            } else {
                throw e;
            }
        }
    }

    public String getAddressesFileName() {
        return ADDRESSES;
    }
}

