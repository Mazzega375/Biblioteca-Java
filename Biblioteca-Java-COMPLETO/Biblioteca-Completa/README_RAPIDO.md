# 🚀 Guia Rápido - Banco de Dados MySQL para Projeto Biblioteca Java

## ✅ O que foi feito:

1. ✓ **Criado banco de dados MySQL** com 5 tabelas
2. ✓ **Criada classe de conexão** (ConexaoDB.java)
3. ✓ **Atualizados todos os Repositories** (UsuarioRepository, LivroRepository, ExemplarRepository, EmprestimoRepository, ReservaRepository)
4. ✓ **Dados de exemplo** pré-carregados no banco

---

## 🔧 Passo 1: Instalar MySQL

### Windows
- Baixar em: https://dev.mysql.com/downloads/mysql/
- Instalar e anotar a senha do `root`

### Linux/Ubuntu
```bash
sudo apt update
sudo apt install mysql-server
```

### macOS
```bash
brew install mysql
```

---

## 🗄️ Passo 2: Criar o Banco de Dados

### Abrir o MySQL
```bash
mysql -u root -p
```
(Digitar a senha do root)

### Executar o script SQL
```sql
source /caminho/para/biblioteca_db.sql
```

Ou copiar e colar o conteúdo do arquivo `biblioteca_db.sql`

### Verificar
```sql
USE biblioteca;
SHOW TABLES;
SELECT * FROM usuarios;
```

---

## ☕ Passo 3: Configurar o Projeto Java

### Adicionar o driver MySQL JDBC

**Opção A: Maven** (no pom.xml)
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

**Opção B: Gradle** (no build.gradle)
```gradle
implementation 'mysql:mysql-connector-java:8.0.33'
```

**Opção C: Manual**
1. Baixar em: https://dev.mysql.com/downloads/connector/j/
2. Extrair e colocar o JAR em `lib/` do projeto
3. Adicionar ao classpath na compilação

---

## ⚙️ Passo 4: Configurar Credenciais (se necessário)

Arquivo: `src/main/java/br/edu/biblioteca/database/ConexaoDB.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
private static final String USUARIO = "root";
private static final String SENHA = "root";  // ⚠️ Alterar se necessário
```

---

## 📝 Passo 5: Usar os Repositories

### Exemplo de Uso

```java
import br.edu.biblioteca.repository.*;
import br.edu.biblioteca.model.*;

public class Teste {
    public static void main(String[] args) {
        // Criar repositórios
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        LivroRepository livroRepo = new LivroRepository();
        
        // Testar conexão
        if (usuarioRepo.testarConexao()) {
            System.out.println("✓ Conectado!");
        }
        
        // Criar novo usuário
        Usuario user = new Usuario(0, "Ana Silva", "ana@email.com", Usuario.Tipo.ALUNO);
        usuarioRepo.salvar(user);
        
        // Listar todos
        usuarioRepo.listarTodos().forEach(System.out::println);
    }
}
```

---

## 🎯 Estrutura das Tabelas

| Tabela | Campos | Descrição |
|--------|--------|-----------|
| **usuarios** | id, nome, email, tipo, bloqueado | Alunos, professores, bibliotecários |
| **livros** | isbn, titulo, autor, editora, ano, categoria | Acervo da biblioteca |
| **exemplares** | id, isbn_livro, status | Cópias físicas dos livros |
| **emprestimos** | id, usuario_id, isbn_livro, datas | Registros de empréstimos |
| **reservas** | id, usuario_id, isbn_livro, data, status | Fila de reservas |

---

## 📊 Dados de Exemplo

O banco já vem com:
- ✓ 4 usuários (alunos, professor, bibliotecário)
- ✓ 4 livros de programação
- ✓ 5 exemplares
- ✓ 2 empréstimos ativos
- ✓ 2 reservas aguardando

---

## 🐛 Se der erro...

### "Unable to load driver"
→ Adicionar mysql-connector-java.jar ao classpath

### "Access denied for user 'root'"
→ Alterar a senha em ConexaoDB.java

### "Database 'biblioteca' doesn't exist"
→ Executar biblioteca_db.sql

### "Communications link failure"
→ MySQL Server não está rodando

---

## 📂 Arquivos Inclusos

1. **biblioteca_db.sql** - Script SQL para criar banco e tabelas
2. **ConexaoDB.java** - Classe de conexão (já no projeto)
3. **SETUP_BANCO_DADOS.md** - Documentação detalhada
4. **Biblioteca-Java-com-MySQL.zip** - Projeto completo atualizado

---

## 🔑 Principais Mudanças no Código

- ✓ Repositories agora usam **MySQL** em vez de arquivos
- ✓ Cache em memória para performance
- ✓ Suporte a **Prepared Statements** (segurança contra SQL Injection)
- ✓ Métodos auxiliares para busca e filtros
- ✓ Mensagens de log para debug

---

## 💡 Dicas

1. **Sempre feche conexões**: Use try-with-resources
2. **Cache**: Os repositories mantêm cache em memória
3. **Performance**: Use as buscas específicas (por nome, tipo, etc)
4. **Logs**: Preste atenção nas mensagens ✓ e ✗

---

## 🎓 Próximos Passos

1. Integrar com a UI (TelaUsuarios, TelaCatalogo, etc)
2. Adicionar mais validações
3. Implementar transações mais complexas
4. Criar relatórios com dados do banco

---

**Qualquer dúvida, revise o arquivo SETUP_BANCO_DADOS.md para mais detalhes!** 📚

