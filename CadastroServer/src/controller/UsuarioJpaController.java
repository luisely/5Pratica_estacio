/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import model.Usuario;

/**
 *
 * @author luisf
 */
public class UsuarioJpaController {
        private EntityManagerFactory emf;

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Usuario findUsuarioById(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public Usuario findUsuario(String login, String senha) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Query query = em.createQuery("SELECT user FROM Usuario user WHERE user.login = :login AND user.senha = :senha");
            
            query.setParameter("login", login);
            query.setParameter("senha", senha);
            
            
            return (Usuario) query.getSingleResult();
        } catch (NoResultException e) {
             return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
    }
}
