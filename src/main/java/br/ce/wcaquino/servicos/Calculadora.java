package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;

import static java.lang.Integer.*;

public class Calculadora {

    public int somar(int a, int b) {
        System.out.print("Estou executando o método somar");
        return a + b;
    }

    public int subtrair(int a, int b) {
        return a - b;
    }

    public int dividir(int a, int b) throws NaoPodeDividirPorZeroException {
        if (b == 0)
            throw new NaoPodeDividirPorZeroException();

        return a / b;
    }

    public int dividir(String a, String b) {
        return valueOf(a) / valueOf(b);
    }

    public static void imprime() {
        System.out.println("Passei aqui");
    }

    public static void main(String[] args) {
        new Calculadora().dividir("a", "0");
    }
}
