package br.edu.biblioteca.ui;

import br.edu.biblioteca.repository.*;
import br.edu.biblioteca.model.*;
import java.util.*;

/**
 * Exemplo de como integrar o banco de dados MySQL com a interface do usuário.
 * Este arquivo mostra como usar os Repositories com persistência em banco de dados.
 */
public class ExemploIntegracaoBD {

    // Instâncias dos repositórios
    private static UsuarioRepository usuarioRepo;
    private static LivroRepository livroRepo;
    private static ExemplarRepository exemplarRepo;
    private static EmprestimoRepository emprestimoRepo;
    private static ReservaRepository reservaRepo;

    public static void main(String[] args) {
        // Inicializar repositórios
        usuarioRepo = new UsuarioRepository();
        livroRepo = new LivroRepository();
        exemplarRepo = new ExemplarRepository();
        emprestimoRepo = new EmprestimoRepository();
        reservaRepo = new ReservaRepository();

        // Testar conexões
        if (!testarConexoes()) {
            System.err.println("✗ Falha na conexão com o banco de dados!");
            return;
        }

        System.out.println("\n==================== EXEMPLOS DE OPERAÇÕES ====================\n");

        // Exemplos de operações
        exemploUsuarios();
        exemploLivros();
        exemploExemplares();
        exemploEmprestimos();
        exemploReservas();

        System.out.println("\n==================== FIM DOS EXEMPLOS ====================\n");
    }

    /**
     * Testa a conexão com o banco de dados
     */
    private static boolean testarConexoes() {
        System.out.println("Testando conexões com o banco de dados...\n");
        
        boolean usuarioOk = usuarioRepo.testarConexao();
        boolean livroOk = livroRepo.testarConexao();
        boolean exemplarOk = exemplarRepo.testarConexao();
        boolean emprestimoOk = emprestimoRepo.testarConexao();
        boolean reservaOk = reservaRepo.testarConexao();

        System.out.println("UsuarioRepository: " + (usuarioOk ? "✓ OK" : "✗ FALHA"));
        System.out.println("LivroRepository: " + (livroOk ? "✓ OK" : "✗ FALHA"));
        System.out.println("ExemplarRepository: " + (exemplarOk ? "✓ OK" : "✗ FALHA"));
        System.out.println("EmprestimoRepository: " + (emprestimoOk ? "✓ OK" : "✗ FALHA"));
        System.out.println("ReservaRepository: " + (reservaOk ? "✓ OK" : "✗ FALHA"));
        System.out.println();

        return usuarioOk && livroOk && exemplarOk && emprestimoOk && reservaOk;
    }

    /**
     * Exemplos de operações com usuários
     */
    private static void exemploUsuarios() {
        System.out.println("--- EXEMPLO: GERENCIAR USUÁRIOS ---\n");

        // Listar todos os usuários
        System.out.println("1. Listando todos os usuários:");
        List<Usuario> usuarios = usuarioRepo.listarTodos();
        for (Usuario u : usuarios) {
            System.out.println("  " + u);
        }
        System.out.println();

        // Buscar usuário por ID
        if (!usuarios.isEmpty()) {
            Usuario primeiro = usuarios.get(0);
            System.out.println("2. Buscando usuário por ID (" + primeiro.getId() + "):");
            Usuario encontrado = usuarioRepo.buscarPorId(primeiro.getId());
            System.out.println("  " + encontrado);
            System.out.println();
        }

        // Buscar usuários por tipo
        System.out.println("3. Buscando usuários por tipo (ALUNO):");
        List<Usuario> alunos = usuarioRepo.buscarPorTipo(Usuario.Tipo.ALUNO);
        System.out.println("  Total de alunos: " + alunos.size());
        for (Usuario u : alunos) {
            System.out.println("  " + u);
        }
        System.out.println();

        // Buscar por nome
        System.out.println("4. Buscando por nome (contém 'Silva'):");
        List<Usuario> silva = usuarioRepo.buscarPorNome("Silva");
        for (Usuario u : silva) {
            System.out.println("  " + u);
        }
        System.out.println();

        // Criar novo usuário
        System.out.println("5. Criando novo usuário...");
        Usuario novoUser = new Usuario(0, "Pedro Oliveira", "pedro@email.com", Usuario.Tipo.ALUNO);
        usuarioRepo.salvar(novoUser);
        System.out.println("  ✓ Usuário criado com ID: " + novoUser.getId());
        System.out.println();

        // Listar bloqueados
        System.out.println("6. Listando usuários bloqueados:");
        List<Usuario> bloqueados = usuarioRepo.listarBloqueados();
        System.out.println("  Total bloqueados: " + bloqueados.size());
        System.out.println();
    }

    /**
     * Exemplos de operações com livros
     */
    private static void exemploLivros() {
        System.out.println("--- EXEMPLO: GERENCIAR LIVROS ---\n");

        // Listar todos os livros
        System.out.println("1. Listando todos os livros:");
        List<Livro> livros = livroRepo.listarTodos();
        System.out.println("  Total de livros: " + livros.size());
        for (Livro l : livros) {
            System.out.println("  " + l);
        }
        System.out.println();

        // Buscar por título
        System.out.println("2. Buscando livros por título (contém 'Clean'):");
        List<Livro> clean = livroRepo.buscarPorTitulo("Clean");
        for (Livro l : clean) {
            System.out.println("  " + l);
        }
        System.out.println();

        // Buscar por autor
        System.out.println("3. Buscando livros por autor (contém 'Martin'):");
        List<Livro> martin = livroRepo.buscarPorAutor("Martin");
        for (Livro l : martin) {
            System.out.println("  " + l);
        }
        System.out.println();

        // Buscar por categoria
        System.out.println("4. Buscando livros por categoria (Programação):");
        List<Livro> prog = livroRepo.buscarPorCategoria("Programação");
        System.out.println("  Total: " + prog.size());
        System.out.println();
    }

    /**
     * Exemplos de operações com exemplares
     */
    private static void exemploExemplares() {
        System.out.println("--- EXEMPLO: GERENCIAR EXEMPLARES ---\n");

        // Listar todos os exemplares
        System.out.println("1. Listando todos os exemplares:");
        List<Exemplar> exemplares = exemplarRepo.listarTodos();
        System.out.println("  Total de exemplares: " + exemplares.size());
        for (Exemplar e : exemplares) {
            System.out.println("  " + e);
        }
        System.out.println();

        // Buscar exemplares disponíveis
        if (!exemplares.isEmpty()) {
            String isbn = exemplares.get(0).getIsbnLivro();
            System.out.println("2. Buscando exemplares disponíveis para ISBN: " + isbn);
            List<Exemplar> disponiveis = exemplarRepo.buscarDisponiveisPorIsbn(isbn);
            System.out.println("  Total disponível: " + disponiveis.size());
            for (Exemplar e : disponiveis) {
                System.out.println("  " + e);
            }
            System.out.println();
        }

        // Buscar por status
        System.out.println("3. Buscando exemplares EMPRESTADOS:");
        List<Exemplar> emprestados = exemplarRepo.buscarPorStatus(Exemplar.Status.EMPRESTADO);
        System.out.println("  Total: " + emprestados.size());
        System.out.println();
    }

    /**
     * Exemplos de operações com empréstimos
     */
    private static void exemploEmprestimos() {
        System.out.println("--- EXEMPLO: GERENCIAR EMPRÉSTIMOS ---\n");

        // Listar todos os empréstimos
        System.out.println("1. Listando todos os empréstimos:");
        List<Emprestimo> emprestimos = emprestimoRepo.listarTodos();
        System.out.println("  Total de empréstimos: " + emprestimos.size());
        for (Emprestimo e : emprestimos) {
            System.out.println("  " + e);
        }
        System.out.println();

        // Listar empréstimos ativos
        System.out.println("2. Listando empréstimos ativos (não devolvidos):");
        List<Emprestimo> ativos = emprestimoRepo.listarAtivos();
        System.out.println("  Total ativo: " + ativos.size());
        for (Emprestimo e : ativos) {
            System.out.println("  " + e);
        }
        System.out.println();

        // Listar em atraso
        System.out.println("3. Listando empréstimos em atraso:");
        List<Emprestimo> atrasados = emprestimoRepo.listarEmAtraso();
        System.out.println("  Total em atraso: " + atrasados.size());
        for (Emprestimo e : atrasados) {
            System.out.println("  " + e);
            System.out.println("    → Multa por atraso: R$ " + String.format("%.2f", e.calcularMulta()));
        }
        System.out.println();
    }

    /**
     * Exemplos de operações com reservas
     */
    private static void exemploReservas() {
        System.out.println("--- EXEMPLO: GERENCIAR RESERVAS ---\n");

        // Listar todas as reservas
        System.out.println("1. Listando todas as reservas:");
        List<Reserva> reservas = reservaRepo.listarTodos();
        System.out.println("  Total de reservas: " + reservas.size());
        for (Reserva r : reservas) {
            System.out.println("  " + r);
        }
        System.out.println();

        // Listar aguardando
        System.out.println("2. Listando reservas aguardando atendimento:");
        List<Reserva> aguardando = reservaRepo.listarAguardando();
        System.out.println("  Total aguardando: " + aguardando.size());
        for (Reserva r : aguardando) {
            System.out.println("  " + r);
        }
        System.out.println();
    }

    /**
     * Exemplo: Simular um empréstimo
     */
    public static void exemploSimularEmprestimo() {
        System.out.println("\n--- EXEMPLO: SIMULAR UM EMPRÉSTIMO ---\n");

        // Obter primeiro usuário e livro
        List<Usuario> usuarios = usuarioRepo.listarTodos();
        List<Livro> livros = livroRepo.listarTodos();

        if (usuarios.isEmpty() || livros.isEmpty()) {
            System.out.println("✗ Não há usuários ou livros cadastrados");
            return;
        }

        Usuario usuario = usuarios.get(0);
        Livro livro = livros.get(0);

        System.out.println("Usuário selecionado: " + usuario.getNome());
        System.out.println("Livro selecionado: " + livro.getTitulo());

        // Criar empréstimo
        java.time.LocalDate hoje = java.time.LocalDate.now();
        java.time.LocalDate devolucao = hoje.plusDays(14);

        Emprestimo emp = new Emprestimo(0, usuario.getId(), livro.getIsbn(), hoje, devolucao);
        emprestimoRepo.salvar(emp);

        System.out.println("\n✓ Empréstimo criado!");
        System.out.println(emp);
    }
}
