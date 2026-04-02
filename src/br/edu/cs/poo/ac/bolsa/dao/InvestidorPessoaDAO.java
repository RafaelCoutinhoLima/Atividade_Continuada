package br.edu.cs.poo.ac.bolsa.dao;
import br.edu.cs.poo.ac.bolsa.entidade.InvestidorPessoa;
public class InvestidorPessoaDAO extends DAOGenerico {
    public InvestidorPessoaDAO(){
        inicializarCadastro(InvestidorPessoa.class);
    }
    public void incluir(InvestidorPessoa investidor){
        cadastro.incluir(investidor,investidor.getCpf());
    }
    public void alterar(InvestidorPessoa investidor){
        cadastro.alterar(investidor,investidor.getCpf());
    }
    public InvestidorPessoa buscar(String cpf) {
        return (InvestidorPessoa) cadastro.buscar(cpf);
    }
    public void excluir(String cpf){
        cadastro.excluir(cpf);
    }
}
