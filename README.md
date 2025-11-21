# Sistema de Gerenciamento de Tarefas - Cliente-Servidor

## Descrição do Projeto

Aplicação distribuída de gerenciamento de tarefas (To-Do List) que implementa comunicação Cliente-Servidor utilizando **Sockets TCP**. O projeto foi desenvolvido para a disciplina de Arquitetura de Software e Computação em Nuvem.

### Funcionalidades

- **Servidor**: Gerencia uma lista centralizada de tarefas
  - Aceita múltiplos clientes simultaneamente
  - Adiciona, lista, remove e marca tarefas como completas
  - Mantém sincronização entre todos os clientes conectados

- **Cliente**: Interface gráfica JavaFX para interação com o servidor
  - Conexão configurável (host e porta)
  - Adicionar novas tarefas
  - Visualizar lista de tarefas
  - Marcar tarefas como completas
  - Remover tarefas individuais ou todas de uma vez
  - Feedback visual do status da conexão

## Tecnologias Utilizadas

- **Java 17**
- **JavaFX 17.0.2** (Interface gráfica)
- **Maven** (Gerenciamento de dependências)
- **Sockets TCP** (Comunicação em rede)

## Estrutura do Projeto

```
cliente-servidor/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── todoapp/
│                   ├── model/          # Classes de modelo
│                   │   ├── Task.java
│                   │   └── Protocol.java
│                   ├── server/         # Servidor
│                   │   └── TaskServer.java
│                   ├── client/         # Cliente (lógica)
│                   │   └── TaskClient.java
│                   └── ui/             # Interface JavaFX
│                       └── TaskClientUI.java
├── pom.xml
└── README.md
```

## Pré-requisitos

- **Java JDK 17** ou superior
- **Maven 3.6** ou superior
- **JavaFX 17** (será baixado automaticamente pelo Maven)

### Verificar instalação do Java

```bash
java -version
```

### Verificar instalação do Maven

```bash
mvn -version
```

## Como Compilar o Projeto

1. **Clone ou navegue até o diretório do projeto:**

```bash
cd c:\Users\User\Desktop\coding\cliente-servidor\cliente-servidor
```

2. **Compile o projeto com Maven:**

```bash
mvn clean compile
```

3. **Criar os arquivos JAR executáveis:**

```bash
mvn package
```

Este comando irá criar o arquivo `servidor.jar` no diretório `target/`.

## Como Executar

### Passo 1: Iniciar o Servidor

Abra um terminal e execute:

```bash
java -cp target/servidor.jar com.todoapp.server.TaskServer
```

Ou, se preferir compilar e executar diretamente:

```bash
mvn exec:java -Dexec.mainClass="com.todoapp.server.TaskServer"
```

Você verá uma mensagem indicando que o servidor está rodando:

```
========================================
    SERVIDOR DE GERENCIAMENTO DE TAREFAS
========================================
Iniciando servidor na porta 5000...
Servidor iniciado com sucesso!
Aguardando conexoes de clientes...
```

### Passo 2: Iniciar o Cliente (Interface JavaFX)

Em outro terminal, execute:

```bash
mvn javafx:run
```

Ou compile e execute com:

```bash
mvn clean javafx:run
```

### Passo 3: Conectar ao Servidor

1. Na interface do cliente, você verá campos para **Host** e **Porta**
2. Por padrão, já estão preenchidos com:
   - **Host**: `localhost`
   - **Porta**: `5000`
3. Clique no botão **"Conectar"**
4. Se a conexão for bem-sucedida, o status mudará para verde e você poderá usar a aplicação

## Como Usar a Aplicação

### Adicionar uma Tarefa

1. Digite a descrição da tarefa no campo "Nova Tarefa"
2. Clique em **"Adicionar"** ou pressione Enter
3. A tarefa será enviada ao servidor e aparecerá na lista

### Visualizar Tarefas

- Todas as tarefas são exibidas na lista central
- Cada tarefa mostra:
  - **ID**: Identificador único
  - **Status**: [PENDENTE] ou [COMPLETA]
  - **Descrição**: Texto da tarefa

### Atualizar Lista

- Clique no botão **"Atualizar"** para sincronizar com o servidor

### Marcar Tarefa como Completa

1. Selecione uma tarefa na lista
2. Clique em **"Marcar como Completa"**
3. A tarefa mudará seu status para [COMPLETA]

### Remover uma Tarefa

1. Selecione uma tarefa na lista
2. Clique em **"Remover"**
3. Confirme a remoção no diálogo

### Limpar Todas as Tarefas

1. Clique em **"Limpar Todas"**
2. Confirme a ação no diálogo
3. Todas as tarefas serão removidas do servidor

### Desconectar

- Clique no botão **"Desconectar"** para encerrar a conexão com o servidor
- Você pode reconectar a qualquer momento

## Executar Múltiplos Clientes

Para demonstrar o suporte a múltiplos clientes:

1. Mantenha o servidor em execução
2. Abra vários terminais
3. Em cada terminal, execute: `mvn javafx:run`
4. Conecte todos os clientes ao servidor
5. Experimente adicionar/remover tarefas de diferentes clientes

Todas as operações são sincronizadas no servidor, mas você precisará clicar em **"Atualizar"** em cada cliente para ver as mudanças feitas por outros clientes.

## Protocolo de Comunicação

A comunicação entre Cliente e Servidor segue um protocolo simples baseado em texto:

### Comandos do Cliente para o Servidor:

- `ADD|descrição` - Adiciona uma nova tarefa
- `LIST` - Lista todas as tarefas
- `REMOVE|id` - Remove uma tarefa pelo ID
- `COMPLETE|id` - Marca uma tarefa como completa
- `CLEAR` - Remove todas as tarefas
- `DISCONNECT` - Desconecta do servidor

### Respostas do Servidor:

- `SUCCESS|mensagem` - Operação realizada com sucesso
- `ERROR|mensagem` - Erro ao processar a operação

### Exemplo de Comunicação:

```
Cliente -> Servidor: ADD|Estudar para a prova
Servidor -> Cliente: SUCCESS|Tarefa adicionada com ID: 1

Cliente -> Servidor: LIST
Servidor -> Cliente: SUCCESS|1:Estudar para a prova:false
```

## Solução de Problemas

### Erro: "Conexão recusada"

- Verifique se o servidor está em execução
- Confirme que a porta 5000 está disponível
- Verifique o firewall

### Erro: "JavaFX runtime components are missing"

Execute com o plugin JavaFX do Maven:

```bash
mvn javafx:run
```

### Erro: "Port already in use"

- A porta 5000 já está em uso
- Encerre o processo que está usando a porta ou altere a porta no código do servidor

### Múltiplos clientes não se conectam

- Certifique-se de que o servidor suporta threads múltiplas (já implementado)
- Verifique se não há limite de conexões no firewall

## Estrutura do Código

### Classes Principais:

1. **Task.java** - Modelo de dados para uma tarefa
2. **Protocol.java** - Define os comandos e constantes do protocolo
3. **TaskServer.java** - Servidor que gerencia as tarefas e conexões
4. **TaskClient.java** - Cliente que se comunica via Socket
5. **TaskClientUI.java** - Interface gráfica JavaFX

### Fluxo de Dados:

```
Interface JavaFX (TaskClientUI)
        ↓
Cliente Socket (TaskClient)
        ↓ TCP Socket
Servidor (TaskServer)
        ↓
Lista de Tarefas (Thread-Safe)
```

## Características Técnicas

- **Comunicação**: TCP Sockets (porta 5000)
- **Multithreading**: Servidor aceita múltiplos clientes simultaneamente
- **Sincronização**: Métodos synchronized para garantir thread-safety
- **Interface**: JavaFX com estilização CSS inline
- **Protocolo**: Baseado em texto com separador "|"

## Autores

Projeto desenvolvido para a disciplina de Arquitetura de Software e Computação em Nuvem.

## Licença

Este projeto é destinado para fins educacionais.

## Apresentação

Data de apresentação: 27/11/2024

---

**Nota**: Este projeto demonstra os conceitos de:
- Comunicação em rede com Sockets
- Arquitetura Cliente-Servidor
- Programação concorrente (multithreading)
- Interface gráfica com JavaFX
- Protocolo de comunicação customizado
