package br.edu.cs.poo.ac.bolsa.negocio;

import br.edu.cs.poo.ac.bolsa.dao.DAOInvestidorEmpresa;
import br.edu.cs.poo.ac.bolsa.dao.DAOInvestidorPessoa;
import br.edu.cs.poo.ac.bolsa.entidade.*;
import br.edu.cs.poo.ac.bolsa.util.MensagensValidacao;
import br.edu.cs.poo.ac.bolsa.util.ResultadoValidacao;
import br.edu.cs.poo.ac.bolsa.util.ValidadorCpfCnpj;
import br.edu.cs.poo.ac.bolsa.util.Comparavel;
import br.edu.cs.poo.ac.bolsa.util.Comparador;
import br.edu.cs.poo.ac.bolsa.util.ComparadorGenerico;
import br.edu.cs.poo.ac.bolsa.util.Ordenador;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class InvestidorMediator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private DAOInvestidorEmpresa daoInvEmp = new DAOInvestidorEmpresa();
    private DAOInvestidorPessoa daoInvPes = new DAOInvestidorPessoa();

    private MensagensValidacao validarEndereco(Endereco endereco) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (endereco.getLogradouro() == null || endereco.getLogradouro().isBlank()) {
            msgs.adicionar("Logradouro é obrigatório.");
            return msgs;
        }
        if (endereco.getNumero() == null || endereco.getNumero().isBlank())
            msgs.adicionar("Número é obrigatório.");
        if (endereco.getPais() == null || endereco.getPais().isBlank())
            msgs.adicionar("País é obrigatório.");
        if (endereco.getEstado() == null || endereco.getEstado().isBlank())
            msgs.adicionar("Estado é obrigatório.");
        if (endereco.getCidade() == null || endereco.getCidade().isBlank())
            msgs.adicionar("Cidade é obrigatório.");
        return msgs;
    }

    private MensagensValidacao validarContatos(Contatos contatos, boolean ehPessoaJuridica) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (contatos == null) {
            msgs.adicionar("Contatos nao pode ser nulo.");
            return msgs;
        }
        String fixo = contatos.getTelefoneFixo();
        String celular = contatos.getTelefoneCelular();
        String whatsapp = contatos.getNumeroWhatsApp();
        boolean temContato = false;
        if (fixo != null && !fixo.isBlank()) {
            temContato = true;
            if (!fixo.matches("\\d+")) msgs.adicionar("Telefone fixo deve conter apenas números");
        }
        if (celular != null && !celular.isBlank()) {
            temContato = true;
            if (!celular.matches("\\d+")) msgs.adicionar("Telefone celular deve conter apenas números.");
        }
        if (whatsapp != null && !whatsapp.isBlank()) {
            temContato = true;
            if (!whatsapp.matches("\\d+")) msgs.adicionar("WhatsApp deve conter apenas números");
        }
        if (!temContato) msgs.adicionar("Pelo menos um telefone deve ser informado.");
        if (contatos.getEmail() == null || contatos.getEmail().isBlank()) {
            msgs.adicionar("Email deve ser diferente de null e/ou branco.");
        } else if (!EMAIL_PATTERN.matcher(contatos.getEmail()).matches()) {
            msgs.adicionar("E-mail inválido.");
        }
        if (ehPessoaJuridica) {
            if (contatos.getNomeParaContato() == null || contatos.getNomeParaContato().isBlank())
                msgs.adicionar("Nome para contato é obrigatório para pessoa jurídica.");
        }
        return msgs;
    }

    private MensagensValidacao validar(DadosInvestidor dadosInv) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (dadosInv == null) {
            msgs.adicionar("Dados investidor deve ser diferente de null.");
            return msgs;
        }
        if (dadosInv.getNome() == null || dadosInv.getNome().isBlank())
            msgs.adicionar("Nome deve ser diferente de null e de brancos.");
        if (dadosInv.getEndereco() == null)
            msgs.adicionar("Endereco deve ser diferente de null.");
        LocalDate hoje = LocalDate.now();
        if (dadosInv.getDataCriacao() == null) {
            msgs.adicionar("Data de criacao deve ser diferente de null.");
        } else if (dadosInv.getDataCriacao().isAfter(hoje)) {
            msgs.adicionar("Data de criacao deve ser menor ou igual à data atual.");
        }
        if (dadosInv.getBonus() == null) {
            msgs.adicionar("Bonus deve ser diferente de null.");
        } else if (dadosInv.getBonus().signum() == -1) {
            msgs.adicionar("Bonus deve ser maior ou igual a 0.");
        }
        if (dadosInv.getContatos() == null) {
            msgs.adicionar("Contatos deve ser diferente de null.");
        } else {
            msgs.adicionar(validarContatos(dadosInv.getContatos(), dadosInv.ehInvestidorEmpresa()));
        }
        if (dadosInv.getEndereco() != null)
            msgs.adicionar(validarEndereco(dadosInv.getEndereco()));
        return msgs;
    }

    private MensagensValidacao validarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (ie == null) {
            msgs.adicionar("Investidor empresa nao pode ser nulo.");
            return msgs;
        }
        msgs.adicionar(validar(new DadosInvestidor(ie, null)));
        ResultadoValidacao resultado = ValidadorCpfCnpj.validarCnpj(ie.getCnpj());
        if (resultado != null) msgs.adicionar(resultado.getMensagem());
        if (ie.getFaturamento() < 100000.0)
            msgs.adicionar("Faturamento tem que ser maior ou igual a 100000.0.");
        return msgs;
    }

    private MensagensValidacao validarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (ip == null) {
            msgs.adicionar("Investidor pessoa nao pode ser nulo.");
            return msgs;
        }
        msgs.adicionar(validar(new DadosInvestidor(null, ip)));
        ResultadoValidacao resultado = ValidadorCpfCnpj.validarCpf(ip.getCpf());
        if (resultado != null) msgs.adicionar(resultado.getMensagem());
        if (ip.getRenda() < 10000.0) {
            msgs.adicionar("Renda tem que ser maior ou igual a 10000.0.");
        } else if (ip.getRenda() <= 50000.00) {
            ip.setFaixaRenda(FaixaRenda.REGULAR);
        } else if (ip.getRenda() <= 300000.00) {
            ip.setFaixaRenda(FaixaRenda.DIFERENCIADA);
        } else if (ip.getRenda() <= 100000000.00) {
            ip.setFaixaRenda(FaixaRenda.PREMIUM);
        }
        return msgs;
    }

    public MensagensValidacao incluirInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);
        if (msgs.estaVazio())
            if (!daoInvEmp.incluirInvestidorEmpresa(ie))
                msgs.adicionar("Investidor Empresa já existente.");
        return msgs;
    }

    public MensagensValidacao alterarInvestidorEmpresa(InvestidorEmpresa ie) {
        MensagensValidacao msgs = validarInvestidorEmpresa(ie);
        if (msgs.estaVazio())
            if (!daoInvEmp.alterarInvestidorEmpresa(ie))
                msgs.adicionar("Investidor Empresa não existente.");
        return msgs;
    }

    public MensagensValidacao excluirInvestidorEmpresa(String cnpj) {
        MensagensValidacao msgs = new MensagensValidacao();
        ResultadoValidacao resultado = ValidadorCpfCnpj.validarCnpj(cnpj);
        if (resultado != null) {
            msgs.adicionar("Cnpj invalido");
            return null;
        }
        if (!daoInvEmp.excluirInvestidorEmpresa(cnpj))
            msgs.adicionar("Investidor Empresa não existente.");
        return msgs;
    }

    public InvestidorEmpresa buscarInvestidorEmpresa(String cnpj) {
        if (ValidadorCpfCnpj.validarCnpj(cnpj) != null) return null;
        return daoInvEmp.buscarInvestidorEmpresa(cnpj);
    }

    public MensagensValidacao incluirInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);
        if (msgs.estaVazio())
            if (!daoInvPes.incluirInvestidorPessoa(ip))
                msgs.adicionar("Investidor Pessoa já existente.");
        return msgs;
    }

    public MensagensValidacao alterarInvestidorPessoa(InvestidorPessoa ip) {
        MensagensValidacao msgs = validarInvestidorPessoa(ip);
        if (msgs.estaVazio())
            if (!daoInvPes.alterarInvestidorPessoa(ip))
                msgs.adicionar("Investidor Pessoa não existente.");
        return msgs;
    }

    public MensagensValidacao excluirInvestidorPessoa(String cpf) {
        MensagensValidacao msgs = new MensagensValidacao();
        ResultadoValidacao resultado = ValidadorCpfCnpj.validarCpf(cpf);
        if (resultado != null) msgs.adicionar(resultado.getMensagem());
        if (msgs.estaVazio())
            if (!daoInvPes.excluirInvestidorPessoa(cpf))
                msgs.adicionar("Investidor Pessoa não existente.");
        return msgs;
    }

    public InvestidorPessoa buscarInvestidorPessoa(String cpf) {
        if (ValidadorCpfCnpj.validarCpf(cpf) != null) return null;
        return daoInvPes.buscarInvestidorPessoa(cpf);
    }

    public InvestidorPessoa[] consultarInvestidorPessoa(OrdenacaoInvestidorPessoa criterio) {
        InvestidorPessoa[] investidores = daoInvPes.consultarTodos();
        if (investidores == null) return null;
        Comparador comparador;
        if (criterio == OrdenacaoInvestidorPessoa.RENDA) {
            comparador = new ComparadorInvestidorPessoaRenda();
        } else {
            comparador = new ComparadorGenerico();
        }
        Ordenador.ordenar((Comparavel[]) investidores, comparador);
        return investidores;
    }
}