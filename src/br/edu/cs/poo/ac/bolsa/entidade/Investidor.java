package br.edu.cs.poo.ac.bolsa.entidade;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Investidor implements Serializable {
    private String nome;
    private Endereco endereco;
    private LocalDate dataCriacao;
    private BigDecimal bonus;
    private Contatos contatos;
    public Investidor(String nome, Endereco endereco, LocalDate dataCriacao, BigDecimal bonus,Contatos contatos) {
        this.nome = nome;
        this.endereco = endereco;
        this.dataCriacao = dataCriacao;
        this.bonus = bonus;
        this.contatos = contatos;
    }

    public Investidor() {
    }

    public int getIdade() {
        return (int) ChronoUnit.YEARS.between(dataCriacao, LocalDate.now());
    }
    public void creditarBonus(BigDecimal valor){
        if (valor==null){
            return;
        }
        this.bonus = bonus.add(valor);
    }
    public void debitarBonus(BigDecimal valor){
        if (valor==null){
            return;
        }
        this.bonus = bonus.subtract(valor);
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    public Contatos getContatos() {
        return contatos;
    }
    public void setContatos(Contatos contatos) {
        this.contatos = contatos;
    }
    protected LocalDate getDataCriacao() {
        return dataCriacao;
    }
    protected void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    public BigDecimal getBonus() {
        return bonus;
    }
}
