package br.edu.cs.poo.ac.bolsa.dao;
import br.edu.cs.poo.ac.bolsa.entidade.Titulo;

public class DAOTitulo extends DAOGenerico{
    public DAOTitulo(){
        inicializarCadastro(Titulo.class);
    }
    public void incluir(Titulo titulo){
        cadastro.incluir(titulo, titulo.getNumero());
    }
    public void alterar (Titulo titulo){
        cadastro.alterar(titulo,titulo.getNumero());
    }
    public Titulo buscar(String Numero){
        return (Titulo) cadastro.buscar(Numero);
    }
    public void excluir(String Numero){
        cadastro.excluir(Numero);
    }
}
