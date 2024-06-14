/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimento;
import model.Produto;
import model.Usuario;

/**
 *
 * @author luisf
 */
public class CadastroThread2 extends Thread {
    private final ProdutoJpaController ctrlProd;
    private final UsuarioJpaController ctrlUsu;
    private final MovimentoJpaController ctrlMov;
    private final PessoaJpaController ctrlPessoa;
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public CadastroThread2(ProdutoJpaController ctrlProd, UsuarioJpaController ctrlUsu,MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa,Socket socket) {
        this.ctrlProd = ctrlProd;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            if (usuario == null) {
                out.writeObject("Credenciais inválidas. Conexão encerrada.");
                return;
            }

            out.writeObject("Usuário conectado com sucesso.");
            

            String comando;
            while ((comando = (String) in.readObject()) != null) {
                if (comando.equalsIgnoreCase("X")) {
                    out.writeObject("Conexão encerrada pelo cliente.");
                    System.out.println("Cliente solicitou encerramento da conexão.");
                    break;
                }
                try {
                  switch (comando) {
                case "L":
                     List<Produto> produtos = ctrlProd.findProdutoEntities();
                     out.writeObject(produtos);
                    break;
                case "X":
                    break;              
                case "E":
                    criarMovimento(out, in, usuario, "E");
                    break;
                case "S":
                    criarMovimento(out, in, usuario, "S");
                    break;
                default:                   
                    out.writeObject("Comando desconhecido.");
                    break;
            }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    Logger.getLogger(CadastroThread.class.getName()).log(Level.SEVERE, null, ex);
                }     
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CadastroThread2.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CadastroThread2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void criarMovimento(ObjectOutputStream out, ObjectInputStream in,Usuario usuario, String tipo) throws IOException, ClassNotFoundException, Exception {
        int idPessoa = (int) in.readObject();
        int idProduto = (int) in.readObject();
        int quantidade = (int) in.readObject();
        Float valorUnitario = (Float) in.readObject();

        Movimento movimento = new Movimento();
        movimento.setPessoaidPessoa(ctrlPessoa.findPessoa(idPessoa));
        movimento.setUsuarioidUsuario(ctrlUsu.findUsuarioById(usuario.getIdUsuario()));
        movimento.setProdutoidProduto(ctrlProd.findProduto(idProduto));
        movimento.setQuantidade(quantidade);
        movimento.setValorUnitario(valorUnitario);
        movimento.setTipo(tipo);

        ctrlMov.create(movimento);
        Produto produto = ctrlProd.findProduto(idProduto);
        
        if(tipo.equals("E")) {
            produto.setQuantidade(produto.getQuantidade() + quantidade);
            ctrlProd.edit(produto);

            out.writeObject("Entrada registrada com sucesso!");
        }
        
        if(tipo.equals("S")) {
            produto.setQuantidade(produto.getQuantidade() - quantidade);
            ctrlProd.edit(produto);

            out.writeObject("Saída registrada com sucesso!");
        }
        
    }
}
