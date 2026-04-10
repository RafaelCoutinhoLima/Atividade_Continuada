package br.edu.cs.poo.ac.bolsa.negocio;

import br.edu.cs.poo.ac.bolsa.dao.DAOInvestidorEmpresa;
import br.edu.cs.poo.ac.bolsa.dao.DAOInvestidorPessoa;
import br.edu.cs.poo.ac.bolsa.entidade.*;
import br.edu.cs.poo.ac.bolsa.util.MensagensValidacao;
import br.edu.cs.poo.ac.bolsa.util.ValidadorCpfCnpj;
import java.time.LocalDate;

public class InvestidorMediator {
    private DAOInvestidorEmpresa daoInvEmp=new DAOInvestidorEmpresa();
    private DAOInvestidorPessoa daoInvPes = new DAOInvestidorPessoa();

    private MensagensValidacao validarEndereco(Endereco endereco){
        MensagensValidacao msgs = new MensagensValidacao();
        if (endereco.getLogradouro()==null || endereco.getLogradouro().trim().isEmpty()){
            msgs.adicionar("Logradouro é obrigatório.");
        }
        if (endereco.getNumero()==null || endereco.getNumero().trim().isEmpty()){
            msgs.adicionar("Número é obrigatório.");
        }
        if (endereco.getCidade()==null || endereco.getCidade().trim().isEmpty()){
            msgs.adicionar("Cidade é obrigatório.");
        }
        if (endereco.getEstado()==null || endereco.getEstado().trim().isEmpty()){
            msgs.adicionar("Estado é obrigatório.");
        }
        if (endereco.getPais()==null || endereco.getPais().trim().isEmpty()){
            msgs.adicionar("País é obrigatório.");
        }
        return msgs;
    }
    private MensagensValidacao validarContatos(Contatos contatos,boolean ehPessoaJuridica){
        MensagensValidacao msgs = new MensagensValidacao();
        if(contatos.getEmail()==null || contatos.getEmail().trim().isEmpty()|| !contatos.getEmail().contains("@")){
            msgs.adicionar("E-mail inválido.");
        }
        if(contatos.getTelefoneFixo()!=null && !contatos.getTelefoneFixo().isEmpty() && !contatos.getTelefoneFixo().matches("\\d+")){
        msgs.adicionar("Telefone fixo deve conter apenas números.");
        }
        if(contatos.getTelefoneCelular()!=null && !contatos.getTelefoneCelular().isEmpty() && !contatos.getTelefoneCelular().matches("\\d+")){
            msgs.adicionar("Telefone celular deve conter apenas números.");
        }
        if(contatos.getNumeroWhatsApp()!=null && !contatos.getNumeroWhatsApp().isEmpty() && !contatos.getNumeroWhatsApp().matches("\\d+")){
            msgs.adicionar("Telefone whatsapp deve conter apenas números.");
        }
        boolean temTelefone = (contatos.getTelefoneFixo() != null && !contatos.getTelefoneFixo().isEmpty()) ||
                        (contatos.getTelefoneCelular() != null && !contatos.getTelefoneCelular().isEmpty()) ||
                        (contatos.getNumeroWhatsApp() != null && !contatos.getNumeroWhatsApp().isEmpty());
        if (!temTelefone) {
            msgs.adicionar("Pelo menos um telefone deve ser informado.");
        }
        if (ehPessoaJuridica && (contatos.getNomeParaContato() == null || contatos.getNomeParaContato().trim().isEmpty())) {
            msgs.adicionar("Nome para contato é obrigatório para pessoa jurídica.");
        }
        return msgs;
    }
    private MensagensValidacao validar(DadosInvestidor dadosInv) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (dadosInv != null) {
            if (dadosInv.getNome() == null || dadosInv.getNome().trim().isEmpty()) {
                msgs.adicionar("Nome é obrigatório.");
            }
            if (dadosInv.getEndereco() == null) {
                msgs.adicionar("Endereço é obrigatório.");
            }
            if (dadosInv.getDataCriacao() == null || dadosInv.getDataCriacao().isAfter(LocalDate.now())) {
                msgs.adicionar("Data de criação inválida.");
            }
            if (dadosInv.getBonus() == null || dadosInv.getBonus().signum() < 0) {
                msgs.adicionar("Bônus inválido.");
            }
            if (dadosInv.getContatos() == null) {
                msgs.adicionar("Contatos é obrigatório.");
            }
            if (dadosInv.getEndereco() != null) {
                msgs.adicionar(validarEndereco(dadosInv.getEndereco()));
            }
            if (dadosInv.getContatos() != null) {
                msgs.adicionar(validarContatos(dadosInv.getContatos(), dadosInv.ehInvestidorEmpresa()));
            }
        }
        return msgs;
    }
    private MensagensValidacao validarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = new MensagensValidacao();
        DadosInvestidor dados = new DadosInvestidor(ie, null);
        msgs.adicionar(validar(dados));
        if (ValidadorCpfCnpj.validarCnpj(ie.getCnpj()) != null) {
            msgs.adicionar("CNPJ inválido.");
        }
        if (ie.getFaturamento() < 100000.0) {
            msgs.adicionar("Faturamento deve ser maior ou igual a 100000.");
        }
        return msgs;
    }
    private MensagensValidacao validarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = new MensagensValidacao();
        DadosInvestidor dados = new DadosInvestidor(null, ip);
        msgs.adicionar(validar(dados));
        if (ValidadorCpfCnpj.validarCpf(ip.getCpf()) != null) {
            msgs.adicionar("CPF inválido.");
        }
        if (ip.getRenda() < 10000.0) {
            msgs.adicionar("Renda deve ser maior ou igual a 10000.");
        }
        for (FaixaRenda f : FaixaRenda.values()) {
            if (ip.getRenda() >= f.getValorInicial() && ip.getRenda() <= f.getValorFinal()) {
                ip.setFaixaRenda(f);
                break;
            }
        }
        return msgs;
    }
    public MensagensValidacao incluirInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);
        if (msgs.estaVazio()) {
            if (daoInvEmp.buscar(ie.getCnpj()) != null) {
                msgs.adicionar("Investidor Empresa já existente.");
            } else {
                daoInvEmp.incluir(ie);
            }
        }
        return msgs;
    }

    public MensagensValidacao alterarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);
        if (msgs.estaVazio()) {
            if (daoInvEmp.buscar(ie.getCnpj()) == null) {
                msgs.adicionar("Investidor Empresa não existente.");
            } else {
                daoInvEmp.alterar(ie);
            }
        }
        return msgs;
    }

    public MensagensValidacao excluirInvestidorEmpresa(String cnpj) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (daoInvEmp.buscar(cnpj) == null) {
            msgs.adicionar("Investidor Empresa não existente.");
        } else {
            daoInvEmp.excluir(cnpj);
        }
        return msgs;
    }
    public InvestidorEmpresa buscarInvestidorEmpresa(String cnpj) {
        if (ValidadorCpfCnpj.validarCnpj(cnpj) != null) return null;
        return daoInvEmp.buscar(cnpj);
    }
    public MensagensValidacao incluirInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);
        if (msgs.estaVazio()) {
            if (daoInvPes.buscar(ip.getCpf()) != null) {
                msgs.adicionar("Investidor Pessoa já existente.");
            } else {
                daoInvPes.incluir(ip);
            }
        }
        return msgs;
    }

    public MensagensValidacao alterarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);
        if (msgs.estaVazio()) {
            if (daoInvPes.buscar(ip.getCpf()) == null) {
                msgs.adicionar("Investidor Pessoa não existente.");
            } else {
                daoInvPes.alterar(ip);
            }
        }
        return msgs;
    }

    public MensagensValidacao excluirInvestidorPessoa(String cpf) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (daoInvPes.buscar(cpf) == null) {
            msgs.adicionar("Investidor Pessoa não existente.");
        } else {
            daoInvPes.excluir(cpf);
        }
        return msgs;
    }

    public InvestidorPessoa buscarInvestidorPessoa(String cpf) {
        if (ValidadorCpfCnpj.validarCpf(cpf) != null) return null;
        return daoInvPes.buscar(cpf);
    }
}
