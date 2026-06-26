-- ============================================
-- BANCO DE DADOS: biblioteca
-- Sistema de Gerenciamento de Biblioteca
-- ============================================

-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

-- ============================================
-- TABELA: usuarios
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    tipo ENUM('ALUNO', 'PROFESSOR', 'BIBLIOTECARIO') NOT NULL DEFAULT 'ALUNO',
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: livros
-- ============================================
CREATE TABLE IF NOT EXISTS livros (
    isbn VARCHAR(20) PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(150) NOT NULL,
    editora VARCHAR(150),
    ano INT,
    categoria VARCHAR(100),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_titulo (titulo),
    INDEX idx_autor (autor),
    INDEX idx_categoria (categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: exemplares
-- ============================================
CREATE TABLE IF NOT EXISTS exemplares (
    id INT PRIMARY KEY AUTO_INCREMENT,
    isbn_livro VARCHAR(20) NOT NULL,
    status ENUM('DISPONIVEL', 'EMPRESTADO', 'RESERVADO', 'INATIVO') NOT NULL DEFAULT 'DISPONIVEL',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (isbn_livro) REFERENCES livros(isbn) ON DELETE CASCADE,
    INDEX idx_isbn_livro (isbn_livro),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: emprestimos
-- ============================================
CREATE TABLE IF NOT EXISTS emprestimos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    isbn_livro VARCHAR(20) NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_prevista_devolucao DATE NOT NULL,
    data_devolucao DATE,
    devolvido BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (isbn_livro) REFERENCES livros(isbn) ON DELETE CASCADE,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_isbn_livro (isbn_livro),
    INDEX idx_devolvido (devolvido),
    INDEX idx_data_emprestimo (data_emprestimo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABELA: reservas
-- ============================================
CREATE TABLE IF NOT EXISTS reservas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    isbn_livro VARCHAR(20) NOT NULL,
    data_reserva DATE NOT NULL,
    status ENUM('AGUARDANDO', 'ATENDIDA', 'CANCELADA') NOT NULL DEFAULT 'AGUARDANDO',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (isbn_livro) REFERENCES livros(isbn) ON DELETE CASCADE,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_isbn_livro (isbn_livro),
    INDEX idx_status (status),
    INDEX idx_data_reserva (data_reserva)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- DADOS DE EXEMPLO
-- ============================================

-- Usuários
INSERT INTO usuarios (nome, email, tipo) VALUES
('João Silva', 'joao@email.com', 'ALUNO'),
('Maria Santos', 'maria@email.com', 'ALUNO'),
('Prof. Carlos', 'carlos@email.com', 'PROFESSOR'),
('Bibliotecário Admin', 'admin@biblioteca.com', 'BIBLIOTECARIO');

-- Livros
INSERT INTO livros (isbn, titulo, autor, editora, ano, categoria) VALUES
('978-8535914849', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', 2008, 'Programação'),
('978-8595084543', 'Design Patterns', 'Gang of Four', 'Addison-Wesley', 1994, 'Programação'),
('978-8590688298', 'O Programador Pragmático', 'Andrew Hunt', 'Bookman', 2000, 'Programação'),
('978-8595843370', 'Estruturas de Dados e Algoritmos', 'Mark Allen Weiss', 'Prentice Hall', 2012, 'Algoritmos');

-- Exemplares
INSERT INTO exemplares (isbn_livro, status) VALUES
('978-8535914849', 'DISPONIVEL'),
('978-8535914849', 'EMPRESTADO'),
('978-8595084543', 'DISPONIVEL'),
('978-8590688298', 'RESERVADO'),
('978-8595843370', 'DISPONIVEL');

-- Empréstimos
INSERT INTO emprestimos (usuario_id, isbn_livro, data_emprestimo, data_prevista_devolucao, devolvido) VALUES
(1, '978-8535914849', '2024-06-01', '2024-06-15', FALSE),
(2, '978-8595084543', '2024-06-10', '2024-06-24', FALSE);

-- Reservas
INSERT INTO reservas (usuario_id, isbn_livro, data_reserva, status) VALUES
(3, '978-8590688298', '2024-06-15', 'AGUARDANDO'),
(1, '978-8595084543', '2024-06-18', 'AGUARDANDO');
