package br.edu.cs.poo.ac.bolsa.dao;

import br.edu.cs.poo.ac.bolsa.entidade.InvestidorEmpresa;

public class DAOInvestidorEmpresa extends DAOGenerico{
    public DAOInvestidorEmpresa(){
        inicializarCadastro(InvestidorEmpresa.class);
    }
    public void incluir(InvestidorEmpresa investidor){
        cadastro.incluir(investidor,investidor.getCnpj());
    }
    public void alterar(InvestidorEmpresa investidor) {
        cadastro.alterar(investidor, investidor.getCnpj());
    }

    public void excluir(String cnpj) {
        cadastro.excluir(cnpj);
    }

    public InvestidorEmpresa buscar(String cnpj) {
        return (InvestidorEmpresa) cadastro.buscar(cnpj);
    }

}
