# 🗄️ Guia de Configuração - Banco de Dados MySQL

## 📋 Sumário
1. [Pré-requisitos](#pré-requisitos)
2. [Instalação do MySQL](#instalação-do-mysql)
3. [Criação do Banco de Dados](#criação-do-banco-de-dados)
4. [Configuração do Projeto Java](#configuração-do-projeto-java)
5. [Estrutura das Tabelas](#estrutura-das-tabelas)
6. [Testando a Conexão](#testando-a-conexão)

---

## 📌 Pré-requisitos

- **Java 8+** instalado
- **MySQL Server** (5.7+) ou **MariaDB** (10.3+)
- **Driver MySQL JDBC** (mysql-connector-java-8.x.x.jar)

---

## ⬇️ Instalação do MySQL

### Windows
1. Baixar em: https://dev.mysql.com/downloads/mysql/
2. Executar o instalador
3. Seguir o wizard de instalação
4. Anotar a senha do usuário `root`
5. Iniciar o MySQL Server

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

### macOS
```bash
brew install mysql
mysql_secure_installation
```

---

## 🗄️ Criação do Banco de Dados

### Opção 1: Usando o Script SQL (Recomendado)

1. Abrir o MySQL Command Line Client:
```bash
mysql -u root -p
```

2. Digitar a senha do `root`

3. Executar o script completo:
```sql
source /caminho/para/biblioteca_db.sql
```

Ou copiar e colar o conteúdo do arquivo `biblioteca_db.sql` no MySQL Workbench

### Opção 2: Passo a Passo

```sql
-- Criar o banco de dados
CREATE DATABASE biblioteca;

-- Usar o banco
USE biblioteca;

-- Criar as tabelas (veja o arquivo biblioteca_db.sql)
```

### Verificar a Criação

```sql
-- Ver os bancos de dados
SHOW DATABASES;

-- Usar o banco biblioteca
USE biblioteca;

-- Ver as tabelas
SHOW TABLES;

-- Ver a estrutura das tabelas
DESCRIBE usuarios;
DESCRIBE livros;
DESCRIBE exemplares;
DESCRIBE emprestimos;
DESCRIBE reservas;
```

---

## ☕ Configuração do Projeto Java

### 1. Adicionar o Driver MySQL JDBC

O driver **DEVE** estar no classpath do projeto. Opções:

**Opção A: Adicionar manualmente**
- Baixar: https://dev.mysql.com/downloads/connector/j/
- Extrair e copiar `mysql-connector-java-X.X.X.jar` para uma pasta `lib/` do projeto
- Adicionar ao classpath durante a compilação

**Opção B: Se usar Maven (arquivo pom.xml)**
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

**Opção C: Se usar Gradle (arquivo build.gradle)**
```gradle
dependencies {
    implementation 'mysql:mysql-connector-java:8.0.33'
}
```

### 2. Verificar a Configuração de Conexão

Arquivo: `src/main/java/br/edu/biblioteca/database/ConexaoDB.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
private static final String USUARIO = "root";
private static final String SENHA = "root"; // ⚠️ Alterar conforme necessário
private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
```

**IMPORTANTE:** Alterar a senha se necessário!

### 3. Usar a Classe de Conexão

Exemplo de uso no seu código:

```java
// Obter a instância da conexão (Singleton)
ConexaoDB conexaoDB = ConexaoDB.getInstance();

// Testar a conexão
if (conexaoDB.testarConexao()) {
    System.out.println("✓ Conectado ao banco de dados!");
} else {
    System.out.println("✗ Falha ao conectar");
}

// Obter uma conexão
try (Connection conn = conexaoDB.obterConexao()) {
    // Usar a conexão
    String sql = "SELECT * FROM usuarios";
    // ...
}
```

---

## 🏗️ Estrutura das Tabelas

### Tabela: usuarios
```
id          : INT PRIMARY KEY AUTO_INCREMENT
nome        : VARCHAR(150) NOT NULL
email       : VARCHAR(100) UNIQUE
tipo        : ENUM('ALUNO', 'PROFESSOR', 'BIBLIOTECARIO')
bloqueado   : BOOLEAN DEFAULT FALSE
data_criacao: TIMESTAMP
```

### Tabela: livros
```
isbn        : VARCHAR(20) PRIMARY KEY
titulo      : VARCHAR(255) NOT NULL
autor       : VARCHAR(150) NOT NULL
editora     : VARCHAR(150)
ano         : INT
categoria   : VARCHAR(100)
data_criacao: TIMESTAMP
```

### Tabela: exemplares
```
id          : INT PRIMARY KEY AUTO_INCREMENT
isbn_livro  : VARCHAR(20) FOREIGN KEY
status      : ENUM('DISPONIVEL', 'EMPRESTADO', 'RESERVADO', 'INATIVO')
data_criacao: TIMESTAMP
```

### Tabela: emprestimos
```
id                      : INT PRIMARY KEY AUTO_INCREMENT
usuario_id              : INT FOREIGN KEY
isbn_livro              : VARCHAR(20) FOREIGN KEY
data_emprestimo         : DATE NOT NULL
data_prevista_devolucao : DATE NOT NULL
data_devolucao          : DATE (NULL se não devolvido)
devolvido               : BOOLEAN DEFAULT FALSE
data_criacao            : TIMESTAMP
```

### Tabela: reservas
```
id          : INT PRIMARY KEY AUTO_INCREMENT
usuario_id  : INT FOREIGN KEY
isbn_livro  : VARCHAR(20) FOREIGN KEY
data_reserva: DATE NOT NULL
status      : ENUM('AGUARDANDO', 'ATENDIDA', 'CANCELADA')
data_criacao: TIMESTAMP
```

---

## 🧪 Testando a Conexão

### Criar um Teste Simples

```java
import br.edu.biblioteca.database.ConexaoDB;
import java.sql.*;

public class TesteConexao {
    public static void main(String[] args) {
        ConexaoDB conexao = ConexaoDB.getInstance();
        
        if (conexao.testarConexao()) {
            System.out.println("✓ Conexão com banco de dados OK!");
            
            try (Connection conn = conexao.obterConexao()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM usuarios");
                
                if (rs.next()) {
                    System.out.println("Total de usuários: " + rs.getInt("total"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("✗ Falha na conexão com banco de dados!");
        }
    }
}
```

---

## 🚀 Usando os Repositories

### UsuarioRepository

```java
UsuarioRepository repo = new UsuarioRepository();

// Criar novo usuário
Usuario usuario = new Usuario(0, "João Silva", "joao@email.com", Usuario.Tipo.ALUNO);
repo.salvar(usuario);

// Buscar por ID
Usuario encontrado = repo.buscarPorId(1);

// Listar todos
List<Usuario> todos = repo.listarTodos();

// Buscar por nome
List<Usuario> resultado = repo.buscarPorNome("João");

// Atualizar
usuario.setNome("João Silva Santos");
repo.salvar(usuario);

// Remover
repo.remover(usuario.getId());
```

### LivroRepository

```java
LivroRepository repo = new LivroRepository();

// Criar novo livro
Livro livro = new Livro("978-8535914849", "Clean Code", "Robert C. Martin", 
                        "Prentice Hall", 2008, "Programação");
repo.salvar(livro);

// Buscar por ISBN
Livro encontrado = repo.buscarPorIsbn("978-8535914849");

// Buscar por título
List<Livro> resultado = repo.buscarPorTitulo("Clean");
```

### ExemplarRepository

```java
ExemplarRepository repo = new ExemplarRepository();

// Criar novo exemplar
Exemplar exemplar = new Exemplar(0, "978-8535914849");
repo.salvar(exemplar);

// Buscar exemplares disponíveis
List<Exemplar> disponiveis = repo.buscarDisponiveisPorIsbn("978-8535914849");
```

### EmprestimoRepository

```java
EmprestimoRepository repo = new EmprestimoRepository();

// Criar novo empréstimo
LocalDate hoje = LocalDate.now();
LocalDate devolucao = hoje.plusDays(14); // 14 dias

Emprestimo emp = new Emprestimo(0, usuarioId, isbn, hoje, devolucao);
repo.salvar(emp);

// Listar empréstimos ativos
List<Emprestimo> ativos = repo.listarAtivos();

// Listar em atraso
List<Emprestimo> atrasados = repo.listarEmAtraso();
```

### ReservaRepository

```java
ReservaRepository repo = new ReservaRepository();

// Criar nova reserva
Reserva reserva = new Reserva(0, usuarioId, isbn, LocalDate.now());
repo.salvar(reserva);

// Listar reservas aguardando
List<Reserva> aguardando = repo.listarAguardando();
```

---

## ⚠️ Troubleshooting

### Erro: "Unable to load driver"
- **Solução:** Verificar se o JAR do MySQL Connector está no classpath

### Erro: "Access denied for user 'root'@'localhost'"
- **Solução:** Verificar a senha em `ConexaoDB.java`

### Erro: "Database 'biblioteca' doesn't exist"
- **Solução:** Executar o script `biblioteca_db.sql` para criar o banco de dados

### Erro: "Communications link failure"
- **Solução:** Verificar se MySQL Server está rodando

### Conexão muito lenta
- **Solução:** Verificar a conexão de rede e as configurações do MySQL

---

## 📚 Referências

- [MySQL Documentation](https://dev.mysql.com/doc/)
- [JDBC Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/)
- [MySQL Connector/J](https://dev.mysql.com/products/connector/j/)

---

**Criado em:** 2024
**Versão:** 1.0
