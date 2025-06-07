# 🗳️ Sistema de Votação em Tempo Real

Este é um sistema de votação em tempo real que permite múltiplos clientes votarem simultaneamente, com atualizações em tempo real dos resultados. O sistema utiliza comunicação TCP para envio de votos e UDP para broadcast de atualizações.

## 📁 Estrutura do Projeto

### 🏗️ Classes Principais

#### 📊 Modelo

- **Enquete**: Classe central que gerencia o estado da votação
  - Gerencia o tempo de abertura e duração
  - Controla o status da enquete (aberta/fechada)
  - Mantém a lista de candidatos e seus votos
  - Implementa lógica de fechamento automático baseado no tempo

#### 💻 Cliente

- **ClienteTCP**: Gerencia a comunicação TCP com o servidor

  - Envia votos para o servidor
  - Obtém informações iniciais da enquete
  - Trata respostas do servidor
  - **Funcionalidade de Criação de Enquetes**: Permite enviar novas enquetes para o servidor, incluindo título, candidatos e duração.
  - **Listagem de Enquetes**: Solicita e recebe a lista de enquetes ativas do servidor.

- **ClienteUDP**: Gerencia a comunicação UDP para atualizações em tempo real
  - **Recebimento Contínuo**: Escuta broadcasts do servidor para atualizações de enquetes.
  - **Atualização da UI**: Notifica a interface do usuário sobre novas enquetes ou alterações nos votos existentes, garantindo que os dados exibidos estejam sempre sincronizados.
  - Implementa listener para notificações de atualização

#### 🖥️ Servidor

- **ServidorTCP**: Gerencia conexões TCP e processamento de votos

  - Aceita conexões de múltiplos clientes
  - Processa votos e atualiza contadores
  - **Criação de Enquetes**: Recebe dados de novas enquetes enviadas pelos clientes e as adiciona à lista de enquetes ativas.
  - Envia respostas aos clientes
  - **Listagem de Enquetes**: Responde a requisições de clientes com a lista de enquetes disponíveis e seus estados.

- **ServidorUDP**: Gerencia broadcast de atualizações
  - Envia atualizações periódicas para todos os clientes
  - Mantém os clientes sincronizados com o estado atual

#### 🎨 Interface

- **TelaEnquete**: A tela principal que exibe todas as enquetes disponíveis.

  - **Listagem Dinâmica**: Carrega e exibe as enquetes ativas em formato de cards.
  - **Layout Responsivo**: Utiliza um `JScrollPane` para garantir que todas as enquetes sejam exibidas corretamente, mesmo em grande número, evitando sobreposições.
  - **Atualizações em Tempo Real**: Recebe notificações via `ClienteUDP` e atualiza a lista de enquetes automaticamente.
  - **Navegação**: Permite ao usuário selecionar uma enquete para votar ou criar uma nova enquete.

- **TelaCriarEnquete**: Interface para a criação de novas enquetes.

  - **Formulário Intuitivo**: Permite ao usuário inserir o título da enquete, adicionar candidatos e definir o tempo de duração.
  - **Validação de Candidatos**: Impede a adição de mais de 30 candidatos por enquete, garantindo a usabilidade e consistência.
  - **Envio ao Servidor**: Envia os dados da nova enquete para o `ServidorTCP` para registro e ativação.

- **TelaVotar**: Interface gráfica do cliente para a votação.
  - Exibe lista de candidatos e seus votos
  - Permite seleção e envio de votos
  - Atualiza em tempo real com novos resultados

## ⚙️ Funcionalidades

### 🎯 Sistema de Votação

- Múltiplos clientes podem votar simultaneamente
- Interface gráfica intuitiva com cards para cada candidato
- Exibição em tempo real do candidato mais votado
- Contagem automática de votos
- Fechamento automático da enquete após o tempo de duração
- **Criação de Novas Enquetes**: Capacidade de adicionar novas votações ao sistema.
- **Visualização de Enquetes Ativas**: Exibição de todas as enquetes disponíveis em uma lista scrollável.

### ⏰ Controle de Tempo

- Tempo de abertura configurável
- Duração da votação configurável
- Fechamento automático quando o tempo expira

### 🔄 Atualizações em Tempo Real

- Broadcast UDP para todos os clientes
- Atualização automática da interface em `TelaEnquete` e `TelaVotar`.
- Sincronização de votos entre todos os clientes
- Exibição do status atual da enquete

## 🔄 Fluxo de Comunicação

### 📡 TCP (Transmissão de Votos e Gerenciamento de Enquetes)

1. **Criação de Enquete**:
   - Cliente (`TelaCriarEnquete`) estabelece conexão TCP com o servidor.
   - Cliente envia o comando `CRIAR_ENQUETE` e os detalhes da nova enquete (título, candidatos, duração).
   - Servidor processa a requisição, cria a enquete e envia uma confirmação ao cliente.
2. **Votação**:
   - Cliente (`TelaVotar`) estabelece conexão TCP com o servidor.
   - Cliente envia o voto para um candidato específico de uma enquete.
   - Servidor processa o voto e atualiza os contadores.
   - Servidor envia confirmação ao cliente.
3. **Listagem de Enquetes**:
   - Cliente (`TelaEnquete`) estabelece conexão TCP com o servidor.
   - Cliente envia o comando `LISTAR_ENQUETES`.
   - Servidor responde com a lista de todas as enquetes ativas, incluindo seus detalhes.
4. Conexão TCP é fechada após a operação.

### 📢 UDP (Atualizações em Tempo Real de Enquetes e Votos)

1. Servidor UDP envia broadcast periódico (a cada 1 segundo) contendo o estado atual de todas as enquetes (títulos, status, candidatos e votos).
2. Todos os clientes UDP (`TelaEnquete` e `TelaVotar`) recebem essas atualizações.
3. Clientes atualizam suas interfaces com os novos dados recebidos, garantindo que as informações exibidas (como o status da enquete, número de votos e candidatos) estejam sempre sincronizadas com o servidor.
4. Processo de broadcast continua até o fechamento das enquetes ou encerramento do servidor.

## 🚀 Como Executar

### 🖥️ Servidor

1. Execute a classe `ServidorTCP`
2. O servidor iniciará nas portas:
   - TCP: 9871
   - UDP: 9872

### 💻 Cliente

1. Execute a classe `Main`
2. A interface gráfica será exibida
3. O cliente se conectará automaticamente ao servidor
4. Você pode executar múltiplas instâncias do cliente

## 📋 Requisitos

- Java 8 ou superior
- Interface gráfica Swing
- Conexão de rede para comunicação cliente-servidor

## ℹ️ Observações

- O sistema suporta múltiplos clientes simultâneos
- As atualizações são em tempo real via UDP
- A enquete fecha automaticamente após o tempo configurado
- Os votos são persistidos apenas em memória
- As atualizações são em tempo, devido a natureza do UDP, podem conter falhas
