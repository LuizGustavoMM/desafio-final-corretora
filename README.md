# Sistema de Corretora de Valores Distribuido

Este projeto implementa um sistema de negociacao de ativos (ações/criptomoedas) capaz de suportar multiplas corretoras conectadas simultaneamente. O sistema garante o processamento de ordens por ordem de chegada e mantem a consistencia do livro de ofertas sob intenso paralelismo.

A arquitetura e dividida em modulos independentes que rodam em JVMs distintas e se comunicam exclusivamente via rede utilizando Sockets TCP, Java RMI e gRPC.

---
## Regras de Negocio e Logica de Mercado

O sistema simula o funcionamento real de uma bolsa de valores, operando sob o modelo de **Leilao Duplo Continuo** (Continuous Double Auction) e seguindo diretrizes estritas de integridade financeira e concorrencia.

### 1. O Livro de Ofertas (Order Book) e o "Match"
O nucleo da corretora (Modulo 2) tem a funcao de gerenciar o Livro de Ofertas e casar ordens de compra e venda que possuam precos compativeis[cite: 1].  Para isso, o sistema divide as intencoes dos investidores em duas filas distintas para cada ativo (ex: PETR4):

*   **Fila de Compras (Bids):** Os investidores que desejam comprar querem pagar o menor preco possivel, mas a prioridade na fila e dada a quem **oferece o maior preco**.
*   **Fila de Vendas (Asks):** Os investidores que desejam vender querem lucrar o maximo possivel, mas a prioridade na fila e dada a quem **aceita o menor preco**.

**A Regra do Casamento (Matching):**
Um negocio so e fechado quando o preco oferecido pelo primeiro comprador da fila e **maior ou igual** ao preco exigido pelo primeiro vendedor da fila[cite: 1]. Se uma ordem for maior em quantidade do que a sua contraparte, o sistema realiza uma execucao parcial e recria uma nova ordem com o saldo restante, reinserindo-a no livro para aguardar um novo comprador/vendedor.

### 2. Prioridade por Ordem de Chegada e Concorrencia
Como o sistema recebe fluxos massivos de ordens de multiplas corretoras simultaneamente, ele deve garantir que as ordens sejam processadas por ordem de chegada[cite: 1].

*   **Desempate:** Se dois investidores oferecerem exatamente o mesmo preco por uma acao, a prioridade absoluta e dada a ordem que chegou primeiro ao sistema (avaliada pelo `timestamp` exato da criacao da ordem)[cite: 1].
*   **Thread-Safety:** O motor garante que a mesma acao nao seja processada duas vezes e que o acesso ao livro de ofertas nao gere condicoes de corrida (Race Conditions)[cite: 1]. Isso e feito aplicando bloqueios (Locks) em nivel de ativo, permitindo que o livro da VALE3 seja processado em paralelo com o livro da PETR4, mas serializando as modificacoes dentro de um mesmo ativo.

### 3. Validacao Financeira (Custodia) e Resiliencia
A bolsa de valores atua apenas como intermediaria; ela nao detem o dinheiro nem as acoes. Por isso, a regra de liquidacao exige comunicacao com o Sistema de Custodia (Modulo 3)[cite: 1].

*   **Bloqueio de Saldo:** O Broker (Core) e obrigatoriamente exigido a consultar o servico de Custodia antes de confirmar qualquer transacao[cite: 1]. Mesmo que dois investidores concordem com o preco no Livro de Ofertas, o negocio e abortado e a ordem e descartada se o comprador nao tiver saldo financeiro suficiente.
*   **Circuit Breaker (Suspensao de Negocios):** O sistema possui uma regra de protecao contra falhas em cascata. O sistema deve tratar quedas de conexao ou indisponibilidade dos servicos; especificamente, se o sistema de custodia cair, o core da corretora deve suspender as negociacoes imediatamente[cite: 1]. Nenhuma acao pode trocar de maos sem a garantia de registro na custodia.

## Fluxo de Comunicacao

A comunicacao entre os modulos ocorre da seguinte forma:

1. Modulo 1 (Gateway) recebe conexoes de corretoras externas via Sockets TCP. Cada conexao e tratada de forma assincrona em uma Thread separada.
2. O Gateway recebe o objeto `Order` e invoca remotamente o Modulo 2 (Core) utilizando Java RMI, repassando a ordem recebida.
3. O Core aloca a ordem no `OrderBook` correspondente ao ativo.
4. Quando o `OrderBook` encontra uma compatibilidade de preco entre uma ordem de compra e uma de venda, antes de fechar o negocio, ele atua como um cliente gRPC e consulta o Modulo 3 (Custodia).
5. O Modulo de Custodia (gRPC) valida o saldo do investidor. Se aprovado, o Core efetiva a transacao gerando um `Trade`.

---

## Estrutura do Projeto e Funcao das Classes

O projeto e gerenciado pelo Maven (`pom.xml` pai) e dividido em 4 submodulos:

### 1. Modulo: broker-shared
Contem os contratos de rede e modelos de dominio que transitarão entre os outros modulos.

* Pacote: `br.com.broker.shared.domain`
  * `Order.java`: Representa a intencao de compra ou venda. Contem dados do ativo, quantidade, preco e timestamp. Implementa Comparable para garantir a ordem de chegada.
  * `OrderSide.java`: Enum que define se a ordem e de COMPRA (BUY) ou VENDA (SELL).
  * `Trade.java`: Representa a concretizacao de um negocio (match) entre dois investidores.

* Pacote: `br.com.broker.shared.rmi`
  * `OrderSubmissionRemote.java`: Interface RMI que o Gateway usa para enviar ordens ao Core.
  * `MarketMetricsRemote.java`: Interface RMI para um painel de monitoramento consultar metricas do Core.

* Diretorio: `src/main/proto`
  * `custody.proto`: Arquivo Protocol Buffers que define os contratos de comunicacao gRPC (mensagens e servicos) entre o Core e a Custodia.
  * Nota: O build do Maven gera automaticamente as classes Java deste proto dentro do pacote `br.com.broker.shared.grpc`.

### 2. Modulo: broker-gateway
Responsavel por absorver o fluxo massivo de entradas simulando os clientes.

* Pacote: `br.com.broker.gateway.server`
  * `GatewayServer.java`: Servidor de Sockets principal. Aceita conexoes na porta 8080 e delega cada conexao para o Thread Pool.
  * `BrokerConnectionHandler.java`: Thread que gerencia a conexao viva de uma corretora, le o objeto `Order` recebido da rede e o encaminha para o Core via cliente RMI.
  * `SystemLauncher.java`: Classe orquestradora para fins de teste. Inicia o CustodyServer, CoreServer e GatewayServer em processos/threads paralelos.

* Pacote: `br.com.broker.gateway.client`
  * `BrokerSimulator.java`: Script de teste automatizado que cria multiplos Sockets conectando no Gateway e envia centenas de ordens aleatorias por segundo.

### 3. Modulo: broker-core
O motor de processamento (Matching Engine).

* Pacote: `br.com.broker.core.server`
  * `CoreServer.java`: Inicializa o registro do RMI na porta 1099 e expoem o servico de submissao de ordens.

* Pacote: `br.com.broker.core.rmi`
  * `OrderSubmissionImpl.java`: Implementacao real do servico RMI. Recebe a ordem pela rede e a roteia para o livro de ofertas adequado.

* Pacote: `br.com.broker.core.engine`
  * `OrderBook.java`: Gerencia as filas de Compra e Venda utilizando PriorityQueue. Utiliza ReentrantLock para garantir que as alteracoes sejam Thread-Safe e executa a logica do leilao continuo (match).

* Pacote: `br.com.broker.core.grpc`
  * `CustodyClient.java`: Cliente gRPC embutido no Core que envia requisicoes de validacao de saldo para o servidor de custodia na porta 9090.

### 4. Modulo: broker-custody
Sistema financeiro independente.

* Pacote: `br.com.broker.custody.server`
  * `CustodyServer.java`: Inicializa o servidor gRPC na porta 9090.

* Pacote: `br.com.broker.custody.service`
  * `CustodyServiceImpl.java`: Implementa os metodos gerados pelo arquivo proto. Realiza a validacao logica de saldo do investidor simulando um banco de dados e gerencia o streaming de resposta.

---

## Instrucoes de Execucao

1. Compilacao:
Na raiz do projeto (onde fica o pom.xml pai), execute o comando do Maven para gerar os arquivos do gRPC:
mvn clean install

2. Inicializacao do Ambiente:
No IntelliJ, execute a classe `SystemLauncher.java` (localizada no modulo broker-gateway). Ela se encarregara de subir todos os tres servidores na ordem correta.

3. Teste de Carga:
Com os servidores rodando, execute a classe `BrokerSimulator.java` para bombardear o Gateway com ordens simultaneas e acompanhar o console do Core realizando os fechamentos de negocios com protecao contra condicoes de corrida.
