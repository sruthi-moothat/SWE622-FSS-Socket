package sruthi.swe622;

import java.io.*;
import java.net.Socket;

public class FileServerAgent {
    Socket socketConnection;

    public FileServerAgent(Socket clientSocket) {
         socketConnection = clientSocket;
    }

    public void start() throws IOException {

//        String fssRoot = System.getenv("FSS_ROOT");

            System.out.println("Client is connected");
            InputStream inFromClient = socketConnection.getInputStream();
            OutputStream outToClient = socketConnection.getOutputStream();

            int byteRead;
            StringBuilder optionBuilder = new StringBuilder();
            while ((byteRead = inFromClient.read()) != '=') {
                optionBuilder.append((char)byteRead);
            }
            String option = optionBuilder.toString();

            if(option.equals("upload")) {
                StringBuilder fileName = new StringBuilder();
                while ((byteRead = inFromClient.read()) != ';') {
                    fileName.append((char)byteRead);
                }

                OutputStream outToFile = new FileOutputStream(fileName.toString());
                while ((byteRead = inFromClient.read()) != -1) {
                    outToFile.write(byteRead);
                }
                outToClient.flush();
                outToClient.close();
            }

            else if(option.equals("download")) {
                StringBuilder fileName = new StringBuilder();
                while ((byteRead = inFromClient.read()) != ';') {
                    fileName.append((char)byteRead);
                }

                System.out.println("Download file: " + fileName.toString());

                try {
                    outToClient.write("Success".getBytes());
                    outToClient.write("=".getBytes());
                    FileInputStream inFile = new FileInputStream(fileName.toString());
                    while ((byteRead = inFile.read()) != -1) {
                        outToClient.write(byteRead);
                    }

                } catch (Exception e){
                    System.out.println(e.getMessage());
                    outToClient.write("Failure".getBytes());
                    outToClient.write("=".getBytes());
                }
                outToClient.flush();
                outToClient.close();
            }

            else if(option.equals("dir")) {
                StringBuilder directoryName = new StringBuilder();
                while ((byteRead = inFromClient.read()) != ';') {
                    directoryName.append((char) byteRead);
                }
                try {
                    File  dir = new File(directoryName.toString());
                    if(dir.exists()) {
                        outToClient.write("Success".getBytes());
                        outToClient.write("=".getBytes());
                        File[] list = new File(directoryName.toString()).listFiles();
                        for (int i = 0; i < list.length; i++) {
                            outToClient.write(i);
                            String file = list[i].toString();
                            outToClient.write(file.getBytes());
                            outToClient.write("\n".getBytes());
                        }
                    }
                } catch (Exception e) {
                    outToClient.write("Failure".getBytes());
                    outToClient.write("=".getBytes());
                }
                outToClient.flush();
                outToClient.close();
            }
            else if(option.equals("mkdir")) {
                StringBuilder directoryName = new StringBuilder();
                while ((byteRead = inFromClient.read()) != ';') {
                    directoryName.append((char) byteRead);
                }
                try {
                    File  newDir = new File(directoryName.toString());
                    if(!newDir.exists()) {
                        outToClient.write("Success".getBytes());
                        newDir.mkdir();
                    }
                    else {
                        outToClient.write("Exist".getBytes());
                    }

                } catch (Exception e) {
                    outToClient.write("Failure".getBytes());

                }
                outToClient.flush();
                outToClient.close();
            }

            else if(option.equals("rmdir")) {
                   StringBuilder directoryName = new StringBuilder();
                   while ((byteRead = inFromClient.read()) != ';') {
                    directoryName.append((char) byteRead);
                }
                try {
                    File dir = new File(directoryName.toString());
                    if(dir.exists()) {
                        String[] dirlist = dir.list();
                        if(dirlist.length == 0) {
                            outToClient.write("Success".getBytes());
                            dir.delete();
                        }
                        outToClient.write("NonEmpty".getBytes());
                    }

                }catch(Exception e) {
                    outToClient.write("Failure".getBytes());

                }
                outToClient.flush();
                outToClient.close();
            }

            else if(option.equals("rm")) {
                StringBuilder fileName = new StringBuilder();
                while ((byteRead = inFromClient.read()) != ';') {
                    fileName.append((char) byteRead);
                }
                try {
                    File file = new File(fileName.toString());
                    if(file.exists()) {
                        outToClient.write("Success".getBytes());
                        file.delete();
                    }

                }catch(Exception e) {
                    outToClient.write("Failure".getBytes());

                }
                outToClient.flush();
                outToClient.close();
            }

            else if(option.equals("shutdown")) {
               // serverSocket.close();
            }

            else {
                System.out.println("error");
            }

    }


}