package com.raiden.homework.rpcserver;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.raiden.homework.rpcserver.Constants.classMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/6/26
 */
public class RpcProxyServer {

    ExecutorService service = Executors.newCachedThreadPool();

    public void init() {
        scanClass("com.raiden.homework.rpcserver.impl");
        publisher(8811);
    }

    public void scanClass(String packageName) {
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = this.getClass().getClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    addClasses(packageName, filePath, false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addClasses(String packageName,
                           String packagePath, final boolean recursive) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                addClasses(packageName + "."
                        + file.getName(), file.getAbsolutePath(), recursive);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    String fullName = packageName + '.' + className;
                    Class clazz = Class.forName(fullName);
                    Constants.classMap.put(className, clazz);
                    Constants.classMap.put(fullName, clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void publisher(int port) {
        ServerSocket serverSocket = null;
        try {
//            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(port));
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
//                Socket socket = socketChannel.socket();
                service.execute(new ProcessHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
