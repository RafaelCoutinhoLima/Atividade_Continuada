package br.edu.cs.poo.ac.bolsa.negocio;

import br.edu.cs.poo.ac.bolsa.dao.DAOAtivo;
import br.edu.cs.poo.ac.bolsa.entidade.Ativo;
import br.edu.cs.poo.ac.bolsa.util.MensagensValidacao;

public class AtivoMediator {
    private static AtivoMediator instancia;
    private DAOAtivo dao = new DAOAtivo();
    private AtivoMediator(){
    }
    public static AtivoMediator getInstancia(){
        if(instancia == null){
            instancia = new AtivoMediator();
        }
        return instancia;
    }
    private MensagensValidacao validar(Ativo ativo) {
        MensagensValidacao msgs = new MensagensValidacao();
        if (ativo != null) {
            if (ativo.getCodigo() <= 0) {
                msgs.adicionar("Código deve ser maior que zero.");
            }
            if (ativo.getDescricao() == null || ativo.getDescricao().trim().isEmpty()) {
                msgs.adicionar("Descrição é obrigatória.");
            }
            if (ativo.getValorMinimoAplicacao() <= 0 || ativo.getValorMinimoAplicacao() > ativo.getValorMaximoAplicacao()) {
                msgs.adicionar("Valor mínimo de aplicação inválido.");
            }
            if (ativo.getValorMaximoAplicacao() <= 0 || ativo.getValorMaximoAplicacao() < ativo.getValorMinimoAplicacao()) {
                msgs.adicionar("Valor máximo de aplicação inválido.");
            }
            if (ativo.getTaxaMensalMinima() < 0 || ativo.getTaxaMensalMinima() > ativo.getTaxaMensalMaxima()) {
                msgs.adicionar("Taxa mensal mínima inválida.");
            }
            if (ativo.getTaxaMensalMaxima() < 0 || ativo.getTaxaMensalMaxima() < ativo.getTaxaMensalMinima()) {
                msgs.adicionar("Taxa mensal máxima inválida.");
            }
            if (ativo.getFaixaMinimaPermitida() == null) {
                msgs.adicionar("Faixa mínima permitida é obrigatória.");
            }
            if (ativo.getPrazoEmMeses() <= 0) {
                msgs.adicionar("Prazo em meses deve ser maior que zero.");
            }
        }
        return msgs;
    }
    public MensagensValidacao incluir(Ativo ativo) {
        MensagensValidacao msgs = validar(ativo);
        if (msgs.estaVazio()) {
            if (!dao.incluir(ativo)) {
                msgs.adicionar("Ativo já existente.");
            }
        }
        return msgs;
    }
    public MensagensValidacao alterar(Ativo ativo) {
        MensagensValidacao msgs = validar(ativo);
        if (msgs.estaVazio()){
            if(!dao.alterar(ativo)) {
                msgs.adicionar("Ativo não existente.");
            }
        }
        return msgs;
    }
    public MensagensValidacao excluir(long codigo){
        MensagensValidacao msgs = new MensagensValidacao();
        if (codigo <= 0) {
            msgs.adicionar("Código deve ser maior que zero.");
        }else{
            if (!dao.excluir(codigo)) {
            msgs.adicionar("Ativo não existente.");
            }
        }
        return msgs;
    }
    public Ativo buscar(long codigo){
        if (codigo <=0){
            return null;
        }else{
            return dao.buscar(codigo);
        }
    }
}