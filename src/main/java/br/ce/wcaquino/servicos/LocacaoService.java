package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.*;
import static br.ce.wcaquino.utils.DataUtils.adicionarDias;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoService {

    private LocacaoDAO dao;
    private SPCService spcService;
    private EmailService emailService;

    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {
        if (usuario == null)
            throw new LocadoraException("Usuário vazio");

        if (filmes == null || filmes.isEmpty())
            throw new LocadoraException("Filme vazio");

        for (Filme filme : filmes) {
            if (filme.getEstoque() == 0) {
                throw new FilmeSemEstoqueException();
            }
        }

        boolean negativado = false;
        try {
            negativado = spcService.possuiNegativacao(usuario);
        } catch (Exception e) {
            throw new LocadoraException("Problemas com SPC, tente novamente");
        }

        if (negativado)
            throw new LocadoraException("Usuário negativado");

        Locacao locacao = new Locacao();
        locacao.setFilmes(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(obterData());
        locacao.setValor(calcularValorLocacao(filmes));

        //Entrega no dia seguinte
        Date dataEntrega = obterData();
        dataEntrega = adicionarDias(dataEntrega, 1);
        if (verificarDiaSemana(dataEntrega, Calendar.SUNDAY))
            dataEntrega = adicionarDias(dataEntrega, 1);

        locacao.setDataRetorno(dataEntrega);

        //Salvando a locacao...
        dao.salvar(locacao);

        return locacao;
    }

    protected Date obterData() {
        return new Date();
    }

    private Double calcularValorLocacao(List<Filme> filmes) {
        System.out.println("Estou calculando");

        Double valorTotal = 0d;
        for (int i = 0; i < filmes.size(); i++) {
            Filme filme = filmes.get(i);
            Double valorFilme = filme.getPrecoLocacao();
            switch (i) {
                case 2: valorFilme *= 0.75; break;
                case 3: valorFilme *= 0.5; break;
                case 4: valorFilme *= 0.25; break;
                case 5: valorFilme = 0d; break;
            }

            valorTotal += valorFilme;
        }

        return valorTotal;
    }

    void notificarAtrasos() {
        List<Locacao> locacoes = dao.obterLocacoesPendentes();
        for (Locacao locacao : locacoes) {
            if (locacao.getDataRetorno().before(obterData())) {
                emailService.notificarAtraso(locacao.getUsuario());
            }
        }
    }

    public void prorrogarLocacao(Locacao locacao, int dias) {
        Locacao novaLocacao = new Locacao();
        novaLocacao.setUsuario(locacao.getUsuario());
        novaLocacao.setFilmes(locacao.getFilmes());
        novaLocacao.setDataLocacao(obterData());
        novaLocacao.setDataRetorno(obterDataComDiferencaDias(dias));
        novaLocacao.setValor(locacao.getValor() * dias);
        dao.salvar(novaLocacao);
    }
}