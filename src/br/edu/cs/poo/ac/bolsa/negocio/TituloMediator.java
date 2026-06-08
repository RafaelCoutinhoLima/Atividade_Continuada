package br.edu.cs.poo.ac.bolsa.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import br.edu.cs.poo.ac.bolsa.dao.DAO;
import br.edu.cs.poo.ac.bolsa.entidade.Ativo;
import br.edu.cs.poo.ac.bolsa.entidade.FaixaRenda;
import br.edu.cs.poo.ac.bolsa.entidade.Investidor;
import br.edu.cs.poo.ac.bolsa.entidade.StatusTitulo;
import br.edu.cs.poo.ac.bolsa.entidade.Titulo;
import br.edu.cs.poo.ac.bolsa.util.ExcecaoNegocio;
import br.edu.cs.poo.ac.bolsa.util.ExcecaoOobjetoNaoExistente;
import br.edu.cs.poo.ac.bolsa.util.MensagensValidacao;

public class TituloMediator {

    private static TituloMediator instancia = new TituloMediator();

    private DAO<Titulo> daoTitulo = new DAO<>(Titulo.class);
    private AtivoMediator ativoMediator = AtivoMediator.getInstancia();
    private InvestidorMediator investidorMediator = new InvestidorMediator();

    private TituloMediator() {}

    public static TituloMediator getInstancia() {
        return instancia;
    }

    public void incluir(DadosTitulo dados) throws ExcecaoNegocio {
        MensagensValidacao msgs = new MensagensValidacao();

        if (dados.getCpfOuCnpj() == null || dados.getCpfOuCnpj().isBlank()) {
            msgs.adicionar("CPF/CNPJ inválido");
        }
        if (dados.getCodigoAtivo() <= 0) {
            msgs.adicionar("Código do ativo inválido");
        }
        if (dados.getValorInvestido() == null) {
            msgs.adicionar("Valor investido não pode ser nulo");
        }
        if (dados.getTaxaDiaria() == null) {
            msgs.adicionar("Taxa diária não pode ser nula");
        }
        if (!msgs.estaVazio()) {
            throw new ExcecaoNegocio(msgs);
        }

        Ativo ativo = ativoMediator.buscar(dados.getCodigoAtivo());
        if (ativo == null) {
            msgs.adicionar("Ativo não encontrado");
            throw new ExcecaoNegocio(msgs);
        }

        Investidor investidor = investidorMediator.buscarInvestidor(dados.getCpfOuCnpj());
        if (investidor == null) {
            msgs.adicionar("Investidor não encontrado");
            throw new ExcecaoNegocio(msgs);
        }
        BigDecimal valorInvestido = dados.getValorInvestido();
        if (valorInvestido.compareTo(BigDecimal.valueOf(ativo.getValorMinimoAplicacao())) < 0
                || valorInvestido.compareTo(BigDecimal.valueOf(ativo.getValorMaximoAplicacao())) > 0) {
            msgs.adicionar("Valor investido fora da faixa permitida");
        }

        BigDecimal taxaDiaria = dados.getTaxaDiaria();
        BigDecimal taxaDiariaDecimal = taxaDiaria.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal fator = BigDecimal.ONE.add(taxaDiariaDecimal);
        BigDecimal taxaMensal = BigDecimal.valueOf(100).multiply(fator.pow(30).subtract(BigDecimal.ONE));
        if (taxaMensal.compareTo(BigDecimal.valueOf(ativo.getTaxaMensalMinima())) < 0
                || taxaMensal.compareTo(BigDecimal.valueOf(ativo.getTaxaMensalMaxima())) > 0) {
            msgs.adicionar("Taxa diária fora da faixa permitida");
        }

        FaixaRenda faixaMinima = ativo.getFaixaMinimaPermitida();
        if (investidor.getEntradaFinanceira()
                .compareTo(BigDecimal.valueOf(faixaMinima.getValorInicial())) < 0) {
            msgs.adicionar("Faixa de renda do investidor incompatível com o ativo");
        }

        if (!msgs.estaVazio()) {
            throw new ExcecaoNegocio(msgs);
        }
        LocalDate dataAplicacao = LocalDate.now();
        LocalDate dataVencimento = dataAplicacao.plusMonths(ativo.getPrazoEmMeses());

        Titulo titulo = new Titulo(
                investidor, ativo,
                valorInvestido, valorInvestido,
                taxaDiaria,
                dataAplicacao, dataVencimento,
                null, StatusTitulo.ATIVO
        );

        try {
            daoTitulo.incluir(titulo);
        } catch (Exception e) {
            msgs.adicionar("Título já existente");
            throw new ExcecaoNegocio(msgs);
        }
    }

    public void cancelarTitulo(String numero) throws ExcecaoNegocio {
        MensagensValidacao msgs = new MensagensValidacao();

        Titulo titulo = daoTitulo.buscar(numero);

        if (titulo == null) {
            msgs.adicionar("Título não encontrado");
            throw new ExcecaoNegocio(msgs);
        }

        if (titulo.getStatus() == StatusTitulo.VENCIDO
                || titulo.getStatus() == StatusTitulo.CANCELADO) {
            msgs.adicionar("Título não pode ser cancelado");
            throw new ExcecaoNegocio(msgs);
        }

        titulo.setStatus(StatusTitulo.CANCELADO);
        try {
            daoTitulo.alterar(titulo);
        } catch (ExcecaoOobjetoNaoExistente e) {
        }

        Investidor investidor = investidorMediator
                .buscarInvestidor(titulo.getInvestidor().getIdentificador());
        BigDecimal debitoBonus = investidor.getBonus().multiply(new BigDecimal("0.70"));
        investidor.debitarBonus(debitoBonus);
        investidorMediator.alterarInvestidor(investidor);
    }

    public void processarRendimentos() {
        Titulo[] titulos = daoTitulo.buscarTodos();
        for (Titulo titulo : titulos) {
            boolean rendeu = titulo.render();

            if (rendeu) {
                BigDecimal diferenca = titulo.getValorAtual().subtract(titulo.getValorInvestido());
                BigDecimal bonus = diferenca.multiply(new BigDecimal("0.0001"));
                Investidor investidor = investidorMediator
                        .buscarInvestidor(titulo.getInvestidor().getIdentificador());
                investidor.creditarBonus(bonus);
                investidorMediator.alterarInvestidor(investidor);
            }

            if (!titulo.getDataVencimento().isAfter(LocalDate.now())) {
                titulo.setStatus(StatusTitulo.VENCIDO);
            }

            try {
                daoTitulo.alterar(titulo);
            } catch (ExcecaoOobjetoNaoExistente e) {
            }
        }
    }
}