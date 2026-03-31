package br.edu.cs.poo.ac.bolsa.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Titulo {
    private InvestidorPessoa investidorPessoa;
    private InvestidorEmpresa investidorEmpresa;
    private Ativo ativo;
    private BigDecimal valorInvestido;
    private BigDecimal valorAtual;
    private BigDecimal taxaDiaria;
    private LocalDate dataAplicacao;
    private LocalDate dataVencimento;
    private LocalDate dataUltimoRendimento;
    private StatusTitulo status;

    public Titulo(InvestidorPessoa investidorPessoa, StatusTitulo status, LocalDate dataUltimoRendimento, LocalDate dataVencimento, BigDecimal taxaDiaria, LocalDate dataAplicacao, BigDecimal valorAtual, BigDecimal valorInvestido, Ativo ativo, InvestidorEmpresa investidorEmpresa) {
        this.investidorPessoa = investidorPessoa;
        this.status = status;
        this.dataUltimoRendimento = dataUltimoRendimento;
        this.dataVencimento = dataVencimento;
        this.taxaDiaria = taxaDiaria;
        this.dataAplicacao = dataAplicacao;
        this.valorAtual = valorAtual;
        this.valorInvestido = valorInvestido;
        this.ativo = ativo;
        this.investidorEmpresa = investidorEmpresa;
    }

    public InvestidorPessoa getInvestidorPessoa() {
        return investidorPessoa;
    }

    public void setInvestidorPessoa(InvestidorPessoa investidorPessoa) {
        this.investidorPessoa = investidorPessoa;
    }

    public InvestidorEmpresa getInvestidorEmpresa() {
        return investidorEmpresa;
    }

    public void setInvestidorEmpresa(InvestidorEmpresa investidorEmpresa) {
        this.investidorEmpresa = investidorEmpresa;
    }

    public Ativo getAtivo() {
        return ativo;
    }

    public void setAtivo(Ativo ativo) {
        this.ativo = ativo;
    }

    public BigDecimal getValorInvestido() {
        return valorInvestido;
    }

    public void setValorInvestido(BigDecimal valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public BigDecimal getTaxaDiaria() {
        return taxaDiaria;
    }

    public void setTaxaDiaria(BigDecimal taxaDiaria) {
        this.taxaDiaria = taxaDiaria;
    }

    public LocalDate getDataAplicacao() {
        return dataAplicacao;
    }

    public void setDataAplicacao(LocalDate dataAplicacao) {
        this.dataAplicacao = dataAplicacao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataUltimoRendimento() {
        return dataUltimoRendimento;
    }

    public void setDataUltimoRendimento(LocalDate dataUltimoRendimento) {
        this.dataUltimoRendimento = dataUltimoRendimento;
    }

    public StatusTitulo getStatus() {
        return status;
    }

    public void setStatus(StatusTitulo status) {
        this.status = status;
    }
}
