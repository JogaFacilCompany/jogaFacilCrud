# Joga Fácil — CRUD Desktop

Aplicação **desktop** em JavaFX para gerenciamento de locação de quadras esportivas.
O sistema cobre quatro perfis de usuário (**locador**, **gerente**, **locatário** e **árbitro**)
e o ciclo completo do negócio: cadastro de quadras e modalidades, delegação de quadras a
gerentes, locação de equipamentos, organização de torneios e — na etapa final — reservas e
lobbies para formação de partidas.

> Este é o módulo desktop do projeto **Joga Fácil**. A versão web do produto vive em
> [`jogaFacilWebsite`](https://github.com/JogaFacilCompany/jogaFacilWebsite).

## Sumário

- [Visão geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Como executar](#como-executar)
- [Perfis de usuário e regras de negócio](#perfis-de-usuário-e-regras-de-negócio)
- [Modelo de domínio (dicionário de dados)](#modelo-de-domínio-dicionário-de-dados)
- [Fluxo de uso](#fluxo-de-uso)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Persistência](#persistência)
- [Estado atual e roadmap](#estado-atual-e-roadmap)
- [Requisitos do projeto e avaliação (RA3)](#requisitos-do-projeto-e-avaliação-ra3)
- [Licença](#licença)

## Visão geral

O **Joga Fácil** conecta donos de quadras esportivas a quem quer jogar. O **locador** é o dono
do negócio: cadastra suas quadras e modalidades e pode delegar a administração de cada quadra a
um **gerente**. O gerente cuida do dia a dia da quadra delegada — reservas, equipamentos e
torneios. O **locatário** é o cliente: reserva quadras, monta lobbies para completar times e
participa de torneios. O **árbitro** oferece seus serviços e entra nos lobbies que precisam de
arbitragem.

## Tecnologias

- **Java 11**
- **JavaFX 13** (`javafx-controls`, `javafx-fxml`)
- **Maven** (build e execução, via `javafx-maven-plugin`)
- Persistência em arquivos via **serialização de objetos** (`*.dat`)

## Pré-requisitos

- JDK 11 ou superior
- Maven 3.6+

## Como executar

```bash
mvn clean javafx:run
```

A classe principal é `br.com.jogafacil.App`. Ao iniciar, a aplicação abre a tela de login.
A pasta `dados/` é criada automaticamente na primeira gravação.

## Perfis de usuário e regras de negócio

O sistema tem quatro perfis. Cada um enxerga e gerencia apenas o que lhe compete — essas são as
regras de negócio que governam o acesso aos dados.

### Locador *(dono de quadras)*
- Cadastra, vê, edita e exclui o **próprio perfil**.
- Cadastra, vê, edita e exclui as **suas quadras**.
- Cadastra, vê, edita e exclui os **seus gerentes** (e delega uma quadra a cada um).
- Cadastra, vê, edita e exclui as **modalidades**.

### Gerente *(administra quadras delegadas pelo locador)*
- Vê e edita as **quadras que lhe foram delegadas** — não cria nem exclui quadras.
- Vê, edita e exclui as **reservas** feitas nas quadras delegadas a ele.
- Cadastra, vê, edita e exclui os **equipamentos**.
- Cadastra, vê, edita e exclui os **torneios**.

### Locatário *(aluga quadras)*
- Cadastra, vê, edita e exclui o **próprio perfil**.
- Cadastra, vê, edita e exclui as **suas reservas**.
- Cadastra, vê, edita e exclui os **seus lobbies**.
- **Vê** lobbies criados por outros e **entra** neles.
- **Vê** torneios e **entra** neles.

### Árbitro *(apita partidas)*
- Cadastra, vê, edita e exclui o **próprio perfil**.
- **Entra** em lobbies que precisam de árbitro.

### Resumo — quem gerencia o quê

| Entidade        | Gerenciada por (CRUD)        | Também acessada por                                  |
|-----------------|------------------------------|------------------------------------------------------|
| Perfil próprio  | cada usuário (o seu)         | —                                                    |
| Modalidade      | Locador                      | consumida por Quadra (e, no spec, por Árbitro)       |
| Quadra          | Locador                      | Gerente (vê/edita as delegadas a ele)                |
| Gerente         | Locador                      | —                                                    |
| Equipamento     | Gerente                      | —                                                    |
| Torneio         | Gerente                      | Locatário (vê/entra)                                 |
| Reserva         | Locatário                    | Gerente (vê/edita/exclui nas quadras delegadas)      |
| Lobby           | Locatário                    | outros Locatários (veem/entram), Árbitro (entra)     |

### Autenticação

O login é feito por **e-mail + senha**, validados contra os arquivos de cada perfil. O e-mail é o
identificador único da conta (não pode haver duas contas com o mesmo e-mail) e a senha exige no
mínimo 4 caracteres. A sessão do usuário logado é mantida em memória pelo singleton
`SessaoUsuario`, que expõe o tipo do perfil (`isLocador()`, `isGerente()`, `isLocatario()`,
`isArbitro()`) usado para decidir quais telas e ações ficam disponíveis.

## Modelo de domínio (dicionário de dados)

Entidades de domínio (pacote `model`), todas `Serializable`. `Usuario` é a classe abstrata base
de `Locador`, `Locatario` e `Gerente`.

**Usuario** *(abstrato — base dos perfis que fazem login)*

| Campo   | Tipo     | Descrição                                   |
|---------|----------|---------------------------------------------|
| nome    | `String` | Nome do usuário                             |
| email   | `String` | Identificador de login (único)              |
| senha   | `String` | Mínimo 4 caracteres                         |
| tipo    | `String` | `LOCADOR` / `LOCATARIO` / `GERENTE` (abstrato) |

**Locador** e **Locatario** — estendem `Usuario` sem campos adicionais.

**Gerente** *(estende `Usuario`)*

| Campo   | Tipo     | Descrição                          |
|---------|----------|------------------------------------|
| quadra  | `Quadra` | Quadra delegada que ele administra |

**Quadra**

| Campo      | Tipo         | Descrição                       |
|------------|--------------|---------------------------------|
| modalidade | `Modalidade` | Modalidade praticada na quadra  |
| cnpj       | `String`     | CNPJ do estabelecimento         |
| endereco   | `String`     | Endereço                        |
| valor      | `double`     | Valor da locação                |

**Modalidade**

| Campo        | Tipo     | Descrição                     |
|--------------|----------|-------------------------------|
| nome         | `String` | Ex.: Futsal, Vôlei            |
| qtdJogadores | `int`    | Jogadores por time/partida    |
| descricao    | `String` | Descrição livre               |

**Equipamento**

| Campo     | Tipo     | Descrição           |
|-----------|----------|---------------------|
| nome      | `String` | Nome do item        |
| valor     | `double` | Valor de locação    |
| descricao | `String` | Descrição livre     |

**Torneio**

| Campo         | Tipo        | Descrição                       |
|---------------|-------------|---------------------------------|
| nome          | `String`    | Nome do torneio                 |
| premiacao     | `double`    | Premiação                       |
| dataInicio    | `LocalDate` | Data de início (`dd/MM/yyyy`)   |
| taxaInscricao | `double`    | Taxa de inscrição               |
| numeroTimes   | `int`       | Número de times                 |

**Arbitro**

| Campo        | Tipo     | Descrição              |
|--------------|----------|------------------------|
| nome         | `String` | Nome do árbitro        |
| cpf          | `String` | CPF                    |
| telefone     | `String` | Telefone de contato    |
| valorPartida | `double` | Valor cobrado por partida |

> **Divergência spec × código:** no PDF de especificação o árbitro é um *usuário* (campos
> `nome`, `email`, `senha`, `modalidade`, `valor`) que faz login e entra em lobbies. No código
> atual `Arbitro` é uma entidade simples (`cpf`, `telefone`, `valorPartida`) **gerenciada pelo
> locador**, sem login. Alinhar essas duas visões é um item em aberto (ver
> [Estado atual e roadmap](#estado-atual-e-roadmap)).

**Reserva** *(planejado — Fase 3, ainda não implementado)*

| Campo          | Tipo        | Descrição                              |
|----------------|-------------|----------------------------------------|
| locatario      | `Locatario` | Quem fez a reserva                     |
| horario        | —           | Data/horário da reserva                |
| quadra         | `Quadra`    | Quadra reservada                       |
| extra          | `boolean`   | Indica se há item/serviço extra        |
| extraDescricao | `String`    | Qual o extra                           |

**Lobby** *(planejado — Fase 3, ainda não implementado)*

| Campo            | Tipo      | Descrição                                       |
|------------------|-----------|-------------------------------------------------|
| reserva          | `Reserva` | Reserva à qual o lobby pertence                 |
| precisaArbitro   | `boolean` | Se o lobby requer árbitro                       |
| jogadoresEmFalta | `int`     | Quantos jogadores faltam para completar a partida |

**Relacionamentos**

- `Quadra` **1 — 1** `Modalidade`
- `Gerente` **1 — 1** `Quadra` (delegação feita pelo locador)
- `Reserva` **N — 1** `Quadra` e **N — 1** `Locatario`
- `Lobby` **1 — 1** `Reserva`; um árbitro entra nos lobbies que precisam de arbitragem

## Fluxo de uso

1. **Login / Criar conta** — o usuário entra com e-mail e senha ou cria uma conta escolhendo o
   tipo de perfil.
2. **Home do perfil** — após o login, abre-se a tela inicial do perfil, com os atalhos para os
   CRUDs que aquele perfil pode acessar.
3. **CRUDs** — cada tela de listagem traz uma tabela com os registros e os botões
   **Novo / Editar / Excluir**; o formulário valida os campos antes de salvar (e-mail válido,
   senha mínima, números e datas no formato `dd/MM/yyyy`).
4. **Sair** — encerra a sessão e volta ao login.

## Estrutura do projeto

```
src/main/java/br/com/jogafacil/
├── App.java                  # Ponto de entrada (Application JavaFX) — abre o login
├── module-info.java
├── model/                    # Entidades de domínio (serializáveis)
│   ├── Usuario.java          # Classe abstrata base dos perfis que logam
│   ├── Locador.java
│   ├── Locatario.java
│   ├── Gerente.java          # Usuario + Quadra delegada
│   ├── Arbitro.java
│   ├── Quadra.java
│   ├── Modalidade.java
│   ├── Equipamento.java
│   └── Torneio.java
├── util/                     # Utilitários
│   ├── Arquivo.java          # Salva/carrega ArrayList em arquivos .dat
│   ├── SessaoUsuario.java    # Singleton com o usuário logado
│   └── Validador.java        # Validações de formulário (e-mail, número, data)
└── view/                     # Telas JavaFX (listagem + formulário) com a lógica de CRUD
    ├── LoginView.java
    ├── CadastroView.java
    ├── LocadorHomeView.java      / LocadorPerfilView.java
    ├── LocatarioHomeView.java    / LocatarioPerfilView.java
    ├── QuadraView.java
    ├── ModalidadeView.java
    ├── GerenteView.java
    ├── ArbitroView.java
    ├── EquipamentoView.java
    └── TorneioView.java

dados/                        # Arquivos de dados gerados em runtime (.dat)
```

Cada tela de CRUD segue o mesmo padrão: um método `getSceneLista(stage)` com a tabela e os botões
de ação, e um `getSceneFormulario(stage, registro, lista)` para criar/editar.

## Persistência

Os dados são gravados na pasta `dados/`, criada automaticamente na primeira execução. Cada
entidade é persistida em um arquivo próprio via serialização Java (`ObjectOutputStream`):
`locadores.dat`, `locatarios.dat`, `gerentes.dat`, `quadras.dat`, `modalidades.dat`,
`torneios.dat`, etc. Esses arquivos são ignorados pelo Git (`.gitignore`).

## Estado atual e roadmap

O projeto foi dividido entre cinco integrantes, em três fases, respeitando as dependências entre
os CRUDs (um CRUD que referencia outra entidade só começa depois que ela existe):

| Fase | Responsável | CRUDs                   | Dependências                                          |
|------|-------------|-------------------------|-------------------------------------------------------|
| 1    | Aluno 1     | Modalidade + Equipamento| Nenhuma — começa primeiro                             |
| 1    | Aluno 2     | Locador + Locatário     | Nenhuma — começa junto com o Aluno 1                  |
| 2    | Aluno 3     | Quadra + Árbitro        | Aguarda Modalidade (Aluno 1)                          |
| 2    | Aluno 4     | Gerente + Torneio       | Aguarda Quadra (Aluno 3)                              |
| 3    | Aluno 5     | Reserva + Lobby         | Aguarda Quadra (A3), Locatário (A2) e Equipamento (A1)|

**✅ Implementado e acessível pela interface**
- Login e cadastro de **Locador** e **Locatário**.
- Home do **Locador** com CRUD de **Quadras**, **Gerentes**, **Modalidades**, **Árbitros** e **Torneios**.
- **Perfil** do Locador e do Locatário (ver / editar / excluir).

**🚧 Implementado parcialmente / ainda não conectado**
- **Equipamento**: o model e a tela (`EquipamentoView`) existem, mas ainda não há navegação até
  eles — serão acessados pela home do Gerente.
- **Árbitro**: hoje é um recurso cadastrado pelo Locador; o model diverge da especificação
  (`cpf`/`telefone`/`valorPartida` em vez de `email`/`senha`/`modalidade`) e ainda não é um perfil
  que faz login.
- **Login/cadastro de Gerente e Árbitro**: a base já existe no model, mas o trecho está comentado
  em `LoginView`/`CadastroView`.

**⬜ Pendente**
- Entidades e telas de **Reserva** e **Lobby** (Fase 3 / Aluno 5).
- Conectar os botões **"Minhas Reservas"**, **"Lobbies"** e **"Torneios"** da home do Locatário.
- Telas iniciais (home) dedicadas para **Gerente** e **Árbitro**.

## Requisitos do projeto e avaliação (RA3)

Este é o projeto de avaliação de **RA3** — uma aplicação **desktop Java** para consolidar
conceitos de Orientação a Objetos. A atividade é **obrigatoriamente em grupo** (de 3 a 6 alunos)
e cada integrante é responsável por **2 classes de modelo**, fazendo o CRUD completo (Inserção,
Consulta, Atualização e Exclusão) de cada uma — ver a divisão em
[Estado atual e roadmap](#estado-atual-e-roadmap).

### Requisitos técnicos obrigatórios

| Requisito | Como o projeto atende |
|-----------|------------------------|
| **Interface 100% em código** — proibido SceneBuilder e FXML (`FXMLLoader.load`); usar qualquer um zera o projeto (**nota 0**). | Todas as telas em `view/` instanciam e configuram os componentes JavaFX por código (`new Button(...)`, `VBox`, `GridPane`, …). Não há arquivos `.fxml` nem `FXMLLoader`. |
| **Persistência em arquivo** | `util/Arquivo.java` grava e lê `ArrayList` serializados em `dados/*.dat`. |
| **Tratamento de exceções** | Leitura/escrita de arquivos e conversão de campos (números, datas) tratam exceções; arquivos ausentes retornam lista vazia em vez de quebrar a aplicação. |
| **Separação domínio × interface** | Pacote `model/` (domínio) separado de `view/` (UI); as telas instanciam e chamam os modelos. |
| **≥ 3 atributos por classe de modelo** | Todas as entidades têm 3 ou mais campos (ver [dicionário de dados](#modelo-de-domínio-dicionário-de-dados)). |
| **Datas no formato pt-BR `DD/MM/AAAA`** | `Validador.isDataValida` / `parseData` usam o padrão `dd/MM/yyyy`. |
| **Campos numéricos validados** | `Validador.isNumeroValido` / `isInteiroPositivo` validam antes de converter. |
| **Programa único com todos os CRUDs acessíveis pela GUI** | Um único app: `App` → login → home por perfil → telas de CRUD acionadas por botões. |

> **Cenário de avaliação — remoção da persistência:** para testar o tratamento de exceções e a
> persistência de objetos, pode ser solicitado **apagar os arquivos `.dat`** (ou a pasta
> `dados/`) e rodar o software. A aplicação deve continuar funcionando e recriar os arquivos
> conforme novos dados são salvos. Isso já é coberto por `Arquivo.carregar` (devolve lista vazia
> quando o arquivo não existe) e por `Arquivo.salvar` (recria a pasta `dados/` antes de gravar).

### Critérios de avaliação (total 4,0)

| Critério | Valor | Observação |
|----------|-------|------------|
| **Funcionamento do software** — todas as classes e a UI funcionando | **2,5** | Desconto de 0,5 por problema (CRUD incompleto, sem tratamento de exceção, etc.) por aluno responsável pela classe/tela; mínimo 0. |
| **Autoria** — individual, por aluno | **1,0** | 1,0 se confirmada; 0,3 se parcial; 0,0 se não confirmada. Inclui perguntas sobre o código e pode-se solicitar uma implementação ao vivo. |
| **Documentação** do projeto (Partes 1 e 3) | **0,5** | Entregue corretamente; 0,0 se faltar ou estiver incompleta. |

A nota soma a avaliação **do grupo** (software + documentação) com a avaliação **individual**
(autoria). Como o funcionamento exige "todas as classes funcionando", os itens pendentes em
[Estado atual e roadmap](#estado-atual-e-roadmap) (Reserva, Lobby, logins de Gerente/Árbitro,
telas desconectadas) impactam diretamente a nota — vale priorizá-los antes da apresentação.

### Entregas

- **Documento de projeto (PDF)** no Canvas: capa com os nomes em ordem alfabética; descrição do
  problema; e, para cada classe, o nome, o autor, a descrição, a figura da tela (quando for UI,
  com os itens da interface) e a descrição de cada método.
- **Apresentação do software** para a turma (até ~20 min por equipe), com cada aluno explicando
  suas classes e telas — sem FXML/SceneBuilder.
- **Prova de autoria**: explicação oral e perguntas individuais sobre o próprio código
  (≈ 5–10 min por aluno).
- **Arquivo `.zip`** com o código completo + a documentação acrescida, por aluno, de uma análise
  pessoal e de uma tabela de uso de IA (prompts, qual IA, data/hora e motivação). O nome do zip
  deve ser o número da equipe no Canvas (ex.: `Equipe4.zip`).

## Licença

Veja o arquivo [LICENSE](LICENSE).
