package br.edu.cs.poo.ac.bolsa.entidade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import br.edu.cs.poo.ac.bolsa.util.Registro;

public class Titulo extends Registro {

    private Investidor investidor;
    private Ativo ativo;
    private BigDecimal valorInvestido;
    private BigDecimal valorAtual;
    private BigDecimal taxaDiaria;
    private LocalDate dataAplicacao;
    private LocalDate dataVencimento;
    private LocalDate dataUltimoRendimento;
    private StatusTitulo status;

    public Titulo() {
    }

    public Titulo(Investidor investidor, Ativo ativo,
                  BigDecimal valorInvestido, BigDecimal valorAtual, BigDecimal taxaDiaria,
                  LocalDate dataAplicacao, LocalDate dataVencimento,
                  LocalDate dataUltimoRendimento, StatusTitulo status) {
        this.investidor = investidor;
        this.ativo = ativo;
        this.valorInvestido = valorInvestido;
        this.valorAtual = valorAtual;
        this.taxaDiaria = taxaDiaria;
        this.dataAplicacao = dataAplicacao;
        this.dataVencimento = dataVencimento;
        this.dataUltimoRendimento = dataUltimoRendimento;
        this.status = status;
    }

    @Override
    public String getIdentificador() {
        return getNumero();
    }

    public String getNumero() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String dataFormatada = dataAplicacao.atStartOfDay().format(formatter);
        String id = investidor.getIdentificador();
        String idPadded = String.format("%14s", id).replace(' ', '0');
        return idPadded + ativo.getCodigo() + dataFormatada;
    }

    public Investidor getInvestidor() {
        return investidor;
    }

    public void setInvestidor(Investidor investidor) {
        this.investidor = investidor;
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

    public boolean render() {
        LocalDate hoje = LocalDate.now();

        if (this.status != StatusTitulo.ATIVO) return false;

        if (this.dataUltimoRendimento != null &&
                (hoje.isBefore(this.dataUltimoRendimento) || hoje.isEqual(this.dataUltimoRendimento)))
            return false;

        if (hoje.isBefore(this.dataAplicacao) || hoje.isEqual(this.dataAplicacao)) return false;

        if (hoje.isAfter(this.dataVencimento) || hoje.isEqual(this.dataVencimento)) return false;

        long diferencaDias;
        if (this.dataUltimoRendimento == null) {
            diferencaDias = ChronoUnit.DAYS.between(this.dataAplicacao, hoje);
        } else {
            diferencaDias = ChronoUnit.DAYS.between(this.dataUltimoRendimento, hoje);
        }

        if (diferencaDias == 0) return false;

        BigDecimal taxa = this.taxaDiaria.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal fator = BigDecimal.ONE.add(taxa);
        BigDecimal fatorTotal = fator.pow((int) diferencaDias);
        this.valorAtual = this.valorAtual.multiply(fatorTotal);
        this.dataUltimoRendimento = hoje;

        return true;
    }
}