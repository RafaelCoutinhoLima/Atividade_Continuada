package br.edu.cs.poo.ac.bolsa.util;

public class ValidadorCpfCnpj {

    public static ResultadoValidacao validarCpf(String cpf) {
        if (cpf == null || cpf.isEmpty())
            return ResultadoValidacao.NAO_INFORMADO;

        String numeros = cpf.replaceAll("[.\\-]", "");

        if (numeros.length() != 11 || numeros.matches("(\\d)\\1{10}"))
            return ResultadoValidacao.FORMATO_INVALIDO;

        if (!validarDvCpf(numeros))
            return ResultadoValidacao.DV_INVALIDO;

        return null;
    }

    public static ResultadoValidacao validarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isEmpty())
            return ResultadoValidacao.NAO_INFORMADO;


        String numeros = cnpj.replaceAll("[.\\-/]", "");

        if (numeros.length() != 14 || numeros.matches("(\\d)\\1{13}"))
            return ResultadoValidacao.FORMATO_INVALIDO;
        if (!validarDvCnpj(numeros))
            return ResultadoValidacao.DV_INVALIDO;

        return null;
    }
    private static boolean validarDvCpf(String cpf) {
        int[] pesos1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        int dv1 = calcularDv(cpf, pesos1);
        int dv2 = calcularDv(cpf, pesos2);

        return cpf.charAt(9) - '0' == dv1 && cpf.charAt(10) - '0' == dv2;
    }

    private static boolean validarDvCnpj(String cnpj) {
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int dv1 = calcularDv(cnpj, pesos1);
        int dv2 = calcularDv(cnpj, pesos2);

        return cnpj.charAt(12) - '0' == dv1 && cnpj.charAt(13) - '0' == dv2;
    }

    private static int calcularDv(String numeros, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (numeros.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}