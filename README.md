# Atividade Continuada — Sistema de Gestão de Bolsa de Investimentos

Projeto acadêmico desenvolvido para a disciplina de Programação Orientada a Objetos (POO) na **Cesar School**. Implementa um sistema completo de gestão de investimentos em bolsa, cobrindo entidades de domínio, regras de negócio, persistência de dados e uma suíte de testes com 130+ casos.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Arquitetura e Estrutura do Projeto](#arquitetura-e-estrutura-do-projeto)
- [Domínio](#domínio)
- [Camada de Negócio](#camada-de-negócio)
- [Camada de Persistência (DAO)](#camada-de-persistência-dao)
- [Utilitários](#utilitários)
- [Padrões de Projeto Utilizados](#padrões-de-projeto-utilizados)
- [Tecnologias e Dependências](#tecnologias-e-dependências)
- [Testes](#testes)
- [Como Executar](#como-executar)
- [Autor](#autor)

---

## Visão Geral

O sistema simula operações de uma bolsa de investimentos, permitindo:

- Cadastro e gerenciamento de **investidores** (pessoa física e jurídica)
- Cadastro de **ativos financeiros** com regras de taxa, valor e prazo
- Emissão e gerenciamento de **títulos de investimento**
- Processamento de **rendimentos diários** com juros compostos
- Cancelamento de títulos com aplicação de penalidades
- Consultas e ordenações de investidores

---

## Funcionalidades

| Módulo | Funcionalidades |
|---|---|
| **Investidores** | Cadastro, edição, exclusão e consulta de pessoas físicas (CPF) e jurídicas (CNPJ) |
| **Ativos** | Cadastro e validação de ativos com faixa de valor, taxa mensal e prazo |
| **Títulos** | Emissão, cancelamento, processamento de rendimentos e controle de status |
| **Rendimentos** | Cálculo por juros compostos com bônus automático de crédito |
| **Validações** | CPF/CNPJ com verificação de dígito verificador, e-mail, telefone, endereço |
| **Ordenação** | Investidores ordenados por nome ou renda com comparadores customizáveis |

---

## Arquitetura e Estrutura do Projeto

```
AtividadeContinuada/
├── src/
│   └── br/edu/cs/poo/ac/bolsa/
│       ├── entidade/        # Entidades de domínio (modelo)
│       ├── dao/             # Camada de acesso a dados
│       ├── negocio/         # Regras de negócio (Mediators)
│       └── util/            # Utilitários, validadores e ordenação
├── test/
│   └── br/edu/cs/poo/ac/bolsa/testes/   # Suíte de testes JUnit 5
├── libs/                    # Dependências externas (.jar)
│   ├── PersistenciaObjetos.jar
│   └── junit-jupiter-*.jar
└── AtividadeContinuada.iml  # Configuração do IntelliJ IDEA
```

O projeto segue uma arquitetura em três camadas:

```
┌─────────────────────────────────┐
│         Camada de Negócio        │  Mediators (regras, validações)
├─────────────────────────────────┤
│         Camada de Domínio        │  Entidades e DTOs
├─────────────────────────────────┤
│      Camada de Persistência      │  DAOs + PersistenciaObjetos.jar
└─────────────────────────────────┘
```

---

## Domínio

### Hierarquia de Investidores

```
Investidor (abstract)
├── InvestidorPessoa   → identificado por CPF, classificado por renda mensal
└── InvestidorEmpresa  → identificado por CNPJ, classificado por faturamento
```

Cada investidor possui:
- `Endereco` — rua, número, cidade, estado, país e CEP
- `Contatos` — e-mail, telefone, WhatsApp e nome do contato

### Faixas de Renda (`FaixaRenda`)

| Faixa | Renda / Faturamento |
|---|---|
| `REGULAR` | R$ 10.000 – R$ 50.000 |
| `DIFERENCIADA` | R$ 5.000 – R$ 300.000 |
| `PREMIUM` | R$ 300.000 – R$ 100.000.000 |

A faixa é atribuída automaticamente com base na renda/faturamento informado.

### Ativo

Representa um produto financeiro disponível para investimento:

| Campo | Descrição |
|---|---|
| Código | Identificador único |
| Valores mín/máx | Faixa de aporte permitida |
| Taxas mín/máx | Rentabilidade mensal |
| Prazo | Duração em meses |
| Faixa de renda | Público-alvo do ativo |

### Título (`Titulo`)

Representa a compra de um ativo por um investidor:

- Número gerado automaticamente: `[CPF/CNPJ com 14 dígitos][código do ativo][yyyyMMddHHmm]`
- Status: `ATIVO`, `CANCELADO` ou `VENCIDO`
- Armazena valor atual (atualizado diariamente) e data de emissão/vencimento

---

## Camada de Negócio

### `InvestidorMediator`

Responsável pelo ciclo de vida dos investidores:

- Inclusão com validação completa (endereço, contatos, CPF/CNPJ, renda)
- Alteração e exclusão
- Consulta por CPF ou CNPJ
- Listagem ordenada por nome ou renda

**Validações aplicadas:**
- Formato e dígito verificador de CPF e CNPJ
- Padrão de e-mail via regex: `^[A-Za-z0-9+_.-]+@(.+)$`
- Obrigatoriedade de pelo menos um telefone (fixo ou WhatsApp)
- Restrições de renda por faixa

### `AtivoMediator` *(Singleton)*

Gerencia ativos financeiros:

- Inclusão, alteração, exclusão e consulta por código
- Validação de coerência entre valores e taxas mínimos/máximos
- Verificação da faixa de renda associada

### `TituloMediator` *(Singleton)*

Controla operações sobre títulos:

- **Emissão:** valida ativo, investidor e valor do aporte; gera número único
- **Cancelamento:** aplica penalidade de débito de 30% sobre o valor atual
- **Processamento de rendimentos:** aplica juros compostos diários e bônus de crédito de 0,01% sobre o ganho

**Fórmula de juros compostos:**
```
valorAtual = valorAtual × (1 + taxaDiaria/100)^diasDecorridos
```

---

## Camada de Persistência (DAO)

### Hierarquia de DAOs

```
DAO<T extends Registro>         (interface genérica)
└── DAOGenerico                 (implementação base)
    ├── DAOInvestidorPessoa
    ├── DAOInvestidorEmpresa
    └── DAOTitulo

DAORegistro                     (retorno booleano)
└── DAOAtivo
```

- Utiliza a biblioteca **PersistenciaObjetos.jar** (Cesar School) para serialização em arquivo
- Cada entidade é armazenada em seu próprio diretório: `InvestidorPessoa/`, `InvestidorEmpresa/`, `Ativo/`, `Titulo/`
- Arquivos nomeados pelo identificador da entidade (ex.: `80052380610.dat`)

---

## Utilitários

### `ValidadorCpfCnpj`

Valida CPF (11 dígitos) e CNPJ (14 dígitos):
- Verificação de formato
- Cálculo e conferência dos dígitos verificadores pelo módulo 11

### Framework de Ordenação

| Classe / Interface | Papel |
|---|---|
| `Comparavel` | Interface para objetos comparáveis |
| `Comparador` | Estratégia de comparação (Strategy) |
| `ComparadorGenerico` | Delega a comparação para `Comparavel` |
| `ComparadorInvestidorPessoaRenda` | Ordena investidores por renda |
| `Ordenador` | Implementa Bubble Sort com comparador injetável |

### Exceções de Negócio

- `ExcecaoNegocio` — agrega múltiplas mensagens de validação
- `ExcecaoObjetoJaExistente` — objeto duplicado
- `ExcecaoObjetoNaoExistente` — objeto não encontrado

---

## Padrões de Projeto Utilizados

| Padrão | Onde é aplicado |
|---|---|
| **Mediator** | `InvestidorMediator`, `TituloMediator`, `AtivoMediator` |
| **Singleton** | `TituloMediator`, `AtivoMediator` |
| **Strategy** | Interface `Comparador` com implementações intercambiáveis |
| **Generic / Template** | `DAO<T extends Registro>` |
| **DTO (Data Transfer Object)** | `DadosInvestidor`, `DadosTitulo` |
| **Value Object** | `Endereco`, `Contatos` |
| **Template Method** | Classe base `DAOGenerico` |

---

## Tecnologias e Dependências

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 8+ | Linguagem principal |
| JUnit Jupiter | 5.8.2 | Framework de testes |
| PersistenciaObjetos.jar | — | Persistência em arquivo (Cesar School) |
| IntelliJ IDEA | — | IDE de desenvolvimento |

**Recursos Java utilizados:**
- Generics e Reflection
- `BigDecimal` para cálculos financeiros
- `LocalDate` / `LocalDateTime` para datas
- Expressões regulares (Regex)
- Enums, classes abstratas e interfaces

---

## Testes

A suíte cobre 130+ casos de teste distribuídos nos seguintes arquivos:

| Arquivo de Teste | Escopo |
|---|---|
| `InvestidorMediatorTest` | CRUD completo e todas as validações de investidores |
| `TituloTest` | Cálculo de rendimentos, numeração e status de títulos |
| `ValidadorCpfCnpjTest` | Validação de formato e dígito verificador |
| `AtivoMediatorTest` | CRUD e validações de ativos |
| `TesteTituloMediator` | Fluxo completo de emissão, rendimento e cancelamento |
| `TesteConsultasOrdenadas` | Ordenação por nome e renda |
| `TesteDAORegistro` | Persistência via DAO |
| `TesteDAO` | DAO genérico |
| `InvestidorTest` | Operações de bônus crédito/débito |
| `InvestidorTest2aUnidade` | Testes complementares da segunda unidade |

Para executar os testes, abra o projeto no IntelliJ IDEA e execute a suíte via **Run > Run All Tests** ou clique com o botão direito na pasta `test` e selecione **Run Tests**.

---

## Como Executar

### Pré-requisitos

- Java 8 ou superior instalado
- IntelliJ IDEA (recomendado) ou qualquer IDE compatível com projetos `.iml`

### Passos

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd AtividadeContinuada
   ```

2. Abra no IntelliJ IDEA:
   - `File > Open` → selecione a pasta do projeto
   - As dependências em `libs/` são configuradas automaticamente pelo `.iml`

3. Execute os testes:
   - Clique com o botão direito na pasta `test` → **Run 'All Tests'**

---

## Autor

**Rafael Coutinho Lima**  
Estudante de Engenharia de Software — Cesar School  
[rafaelcoutinholima12345@gmail.com](mailto:rafaelcoutinholima12345@gmail.com)

---

*Projeto desenvolvido para fins acadêmicos como Atividade Continuada da disciplina de POO.*
