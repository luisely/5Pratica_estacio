/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroclientev2;

import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import model.Produto;

/**
 *
 * @author luisf
 */
public class CadastroClienteV2 {

   
    public static void main(String[] args) throws ClassNotFoundException {
        boolean rodando = true;
        
        try {
            Socket socket = new Socket("localhost", 4321);

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Scanner scanner = new Scanner(System.in);
            
            Frame frame = new Frame();
            SwingUtilities.invokeLater(() -> {
                frame.setVisible(true);
            });
            
            Thread messageThread = new Thread(new ThreadClient(in, frame.texto));
            messageThread.start();

            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            out.writeObject(login);
            out.writeObject(senha);
           

            while (rodando) {
                System.out.println("Menu:");
                System.out.println("L - Listar");
                System.out.println("E - Entrada");
                System.out.println("S - Saída");
                System.out.println("X - Finalizar");
                String command = scanner.nextLine().toUpperCase();

                out.writeObject(command);

                if (command.equals("X")) {
                   System.out.println("Finalizando...");
                   rodando = false;
                    break;
                }

                if (command.equals("L")) {
                    continue;
                }

                if (command.equals("E") || command.equalsIgnoreCase("S")) {
                    System.out.print("Id da pessoa: ");
                    int idPessoa = scanner.nextInt();
                    out.writeObject(idPessoa);

                    System.out.print("Id do produto: ");
                    int idProduto = scanner.nextInt();
                    out.writeObject(idProduto);

                    System.out.print("Quantidade: ");
                    int quantidade = scanner.nextInt();
                    out.writeObject(quantidade);

                    System.out.print("Valor unitário: ");
                    Float valorUnitario = scanner.nextFloat();
                    out.writeObject(valorUnitario);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class Frame extends JDialog {
        public JTextArea texto;

        public Frame() {
            super();
            texto = new JTextArea();
            setBounds(100, 100, 400, 300);
            setModal(false);
            add(texto);
        }
    }
    
    public static class ThreadClient extends Thread {
        private ObjectInputStream entrada;
        private JTextArea textArea;

        public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
            this.entrada = entrada;
            this.textArea = textArea;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Object receivedObject = entrada.readObject();

                    if (receivedObject instanceof String) {
                        String message = (String) receivedObject;
                        SwingUtilities.invokeLater(() -> {
                            textArea.append(message + "\n");
                        });
                    } else if (receivedObject instanceof List) {
                        List<Produto> produtos = (List<Produto>) receivedObject;
                        SwingUtilities.invokeLater(() -> {
                            for (Produto produto : produtos) {
                                textArea.append(produto.getNome() + ": " + produto.getQuantidade() + "\n");
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
