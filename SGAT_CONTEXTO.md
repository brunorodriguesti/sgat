# SGAT – Sistema de Gerenciamento de Alocação de Talentos

> Arquivo de contexto para programação assistida (Claude, Copilot, Cursor etc.).
> Idioma do projeto: **português (pt-BR)** – nomes de domínio, mensagens de tela e documentação.
> Repositório: https://github.com/brunorodriguesti/sgat · **branch padrão de trabalho: `dev`**

**Legenda de confiança.** Itens marcados com ✅ foram confirmados nos documentos/capturas de tela fornecidos. Itens com 🔶 são inferências ou precisam ser verificados no código antes de usar. Itens com ➕ são propostas, não existem ainda.

---

## 1. Visão geral do negócio

A empresa  presta o serviço de **Talent Body**: aloca consultores (desenvolvedores, DevOps, analistas funcionais etc.) em projetos de empresas clientes. O serviço é **cobrado por hora**.

**Problema atual:** o gestor preenche uma planilha por funcionário (dias e horas trabalhadas, compensações para mais e para menos). Os gerentes dos projetos no cliente validam as planilhas e só depois ocorre o faturamento. O processo é manual, lento, sujeito a erro e atrasa o faturamento (pode haver de 1 a mais de 40 funcionários por setor).

**Solução (projeto do LTD – Laboratório de Transformação Digital, Estácio BH):** sistema web que calcula automaticamente as horas (descontando feriados, férias e licenças), permite ajustes manuais e gera o relatório de cobrança. Em fases futuras: importar arquivos do sistema de ponto, aprovação de horas extras e login do gestor do cliente para validar as horas direto no sistema.

### Atores
| Ator | Papel |
|---|---|
| **Gestor**  | Cadastra dados, controla/ajusta horas, gera e imprime o relatório. É o único ator da entrega atual. |
| **Cliente** (gestor do cliente) | Consulta o relatório e valida/contesta horas por funcionário (fora do escopo atual). |

---

## 2. Escopo e fases

| Fase | Período | Conteúdo | Situação |
|---|---|---|---|
| 1 – Cadastros | LTD 2025.2 | CDU001 Manter Cliente · CDU002 Manter Projeto · CDU003 Manter Funcionário · CDU004 Manter Feriados · CDU005 Manter Perfil · CDU006 Manter Férias e Licenças | ✅ desenvolvido |
| 2 – Controle de horas (MVP) | LTD 2026.1 | CDU007 Controlar Horas (filtros → gerar → editar → excluir → salvar → imprimir PDF) | ✅ em andamento (tela e serviço já existem; ⚠️ erro ao imprimir relatório a consertar; rever caso de uso de horas registradas) |
| Futuro (Posteriormente) | – | **Área do Cliente:** CDU008 Consultar Horas (somente leitura para o cliente) · CDU009 Validar Horas (cliente dá check "OK" ou "Não OK", com comentário obrigatório se Não OK) · Tela de login do cliente · **Relatório do Funcionário por Dia:** relatório indicando qual dia trabalhou e qual faltou · **Revisão de Feriados:** dia/mês sem ano, feriados móveis (carnaval), integração com API ou carga CSV · Importação de planilha de férias · Importação de arquivos do sistema de ponto · Aprovação de horas extras · Publicação na Web | ⛔ fora do escopo atual |

> Na numeração do documento de Contexto, **Controlar Horas = CDU007**. Nos documentos da Atividade 7 o exercício reinicia a numeração (CDU001 = Controlar Horas, CDU002 = Gerar Relatório). Ao referenciar casos de uso no código, use a numeração do Contexto (CDU007) e trate "Gerar Relatório de Horas Trabalhadas" como subfluxo dele.

---

## 3. Stack tecnológica

| Camada | Tecnologia | Observação |
|---|---|---|
| Backend | **Java + Spring Boot** (Maven) | ✅ `mvn spring-boot:run`. 🔶 Versões de Java/Spring Boot: conferir `pom.xml`. |
| Persistência | **Spring Data JPA/Hibernate** + **MySQL** | ✅ entidades JPA e repositórios Spring Data; esquema MySQL `sgat`. |
| Migrações | **Flyway** 🔶 | Pasta `src/main/resources/db.migration` com `V001__criar_tabelas_fase.sql` e `V002__alterar_tabela_horas_trabalhadas...sql`. Confirmar dependência e `spring.flyway.locations` em `application.properties`. |
| Mapeamento | **ModelMapper** | ✅ injetado nos services para Entity ↔ DTO. |
| Boilerplate | **Lombok** | ✅ `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` nas entidades. |
| Frontend | **Angular** (Angular CLI) | ✅ `ng serve`. Estrutura com `app-routing.module.ts`, `declarations` em testes → 🔶 projeto baseado em **NgModules** (confirmar versão no `package.json`). |
| IDEs usadas | IntelliJ IDEA (API) e VS Code (UI) | ✅ |

### Como executar (HOW TO do repositório)
1. MySQL ativo com o esquema `sgat` criado.
2. API: `cd api/SGAT && mvn spring-boot:run`
3. UI: `cd sgat-ui && npm install && ng serve` (ou `ng serve --open`)
4. **Usar a branch `dev` como padrão.**

---

## 4. Estrutura do repositório

```
sgat/
├── api/SGAT/                 # Backend Spring Boot
├── sgat-ui/                  # Frontend Angular
├── documentação/             # Documentos do projeto
├── HOW TO.txt
├── README.md
└── caso de uso horas trabalhadas.txt   # Versão original do caso de uso (Atividade 7)
```

### 4.1 Backend (`api/SGAT/src/main/java/com/ltd/sgat/sgat`) ✅ observado em captura de tela

```
com.ltd.sgat.sgat
├── SgatApplication.java
├── api/                      # 🔶 controllers REST (confirmar)
├── config/                   # 🔶 configurações (ex.: CORS, ModelMapper) – confirmar
├── domain/
│   ├── cliente/
│   ├── feriado/
│   ├── feriaslicencas/
│   ├── funcionario/
│   ├── horastrabalhadas/
│   │   ├── HorasTrabalhadas.java            # @Entity @Table(name="horas_trabalhadas")
│   │   ├── HorasTrabalhadasRepository.java  # interface Spring Data
│   │   └── HorasTrabalhadasService.java     # @Service (inclui gerarRelatorioHoras)
│   ├── model/                # Cliente, Feriado, FeriasLicenca, Funcionario, Perfil, Projeto,
│   │                         # enums: Territorio, TipoContrato, TipoFeriado
│   ├── perfil/
│   ├── projeto/
│   ├── repository/           # 🔶 repositórios dos demais agregados
│   └── service/              # 🔶 services dos demais agregados
src/main/resources/
├── db.migration/             # Flyway: V001__..., V002__...
├── static/  templates/
└── application.properties
```

> 🔶 **Atenção:** há dois estilos convivendo – pacote por domínio (`domain/horastrabalhadas/` com entity+repository+service juntos) e pacotes técnicos (`domain/model`, `domain/repository`, `domain/service`). Antes de criar código novo, abra o pacote do agregado em questão e **siga o padrão já usado nele**; para o módulo de horas, o padrão é o de pacote por domínio.

### 4.2 Frontend (`sgat-ui/src/app`) ✅ observado em captura de tela

```
src/app
├── cliente/            (list)     ├── cliente-form/
├── controlar-hora-mes/            # tela Controlar Horas Trabalhadas (filtros + relatório)
│   ├── controlar-hora-mes.component.{html,ts,css,spec.ts}
├── horas-trabalhadas-form/        # componente de formulário
├── horas-trabalhadas-list/        # componente de lista
├── horas-trabalhadas.service.ts   # chamadas HTTP à API
├── feriados/  ferias/  funcionario/  perfil/  projeto/
├── home/  layout/                 # layout com menu lateral
└── app-routing.module.ts
```

Padrão dos cadastros: componente `*-list` (tabela "Lista de X" + botão "Novo X" + ações **Editar/Excluir**) e componente `*-form`, com um `*.service.ts` por recurso.

---

## 5. Modelo de domínio e banco de dados

Fonte: diagrama ER (MySQL Workbench) e diagrama de classes UML da Atividade 8 + entidade `HorasTrabalhadas` vista no código.

### 5.1 Tabelas (diagrama ER) ✅

| Tabela | Colunas | Relacionamentos |
|---|---|---|
| `cliente` | `id_cliente` INT PK · `nome` VARCHAR(100) | 1 cliente → N projetos |
| `projeto` | `id_projeto` PK · `nome` VARCHAR(100) · `id_cliente` FK | 1 projeto → N funcionários |
| `perfil` | `id_perfil` PK · `descricao` VARCHAR(100) · `valor_hora_padrao` DECIMAL(10,2) · `horas_padrao` TIME | 1 perfil → N funcionários |
| `funcionario` | `id_funcionario` PK · `cpf` VARCHAR(11) · `matricula` VARCHAR(11) · `nome` VARCHAR(100) · `id_projeto` FK · `id_perfil` FK · `valor_hora` DECIMAL(11,2) · `tipo_contrato` VARCHAR(45) · `territorio_sindicato` VARCHAR(45) | 1 funcionário → N férias/licenças; 1 → N horas trabalhadas |
| `feriados` | `id_feriado` PK · `data` DATE · `tipo_feriado` VARCHAR(50) · `territorio` VARCHAR(50) · `descricao` VARCHAR(100) | independente |
| `ferias_licencas` | `id_ferias_licencas` PK · `dt_inicio` DATE · `data_fim` DATE · `descricao` VARCHAR(50) · `id_funcionario` FK · `horas_afastadas` TIME | N → 1 funcionário |
| `horas_trabalhadas` | `id_horas_trabalhadas_mes` PK · `data_relatorio` DATE · `horas_trabalhadas` TIME · `id_funcionario` FK · `valor` DECIMAL · `data_inicio` DATE · `data_fim` DATE | N → 1 funcionário |

### 5.2 Enums (diagrama de classes) ✅
- `TipoContrato`: `CLT`, `PJ`
- `Territorio`: `SP`, `MG`, `RJ`, `DF`
- `TipoFeriado`: `NACIONAL`, `ESTADUAL`, `MUNICIPAL`

### 5.3 Entidade `HorasTrabalhadas` (trecho observado) ✅
```java
@Entity @Table(name = "horas_trabalhadas")
@Data @NoArgsConstructor @AllArgsConstructor
public class HorasTrabalhadas {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_trabalho", nullable = false)
    private LocalDate dataTrabalho;

    @Column(name = "horas_trabalhadas", nullable = false)
    private LocalTime horasTrabalhadas; // Or BigDecimal if representing duration

    @ManyToOne @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "valor", precision = 11, scale = 2)
    private BigDecimal valor; // 🔶 confirmar tipo
    // 🔶 demais campos (data_inicio/data_fim?) não visíveis na captura
}
```

### 5.4 Service de horas (padrão observado) ✅
`HorasTrabalhadasService` injeta via `@Autowired`: `HorasTrabalhadasRepository`, `FuncionarioRepository`, `ClienteRepository`, `ProjetoRepository`, `FeriadoRepository`, `FeriasLicencasRepository` e `ModelMapper`. Métodos vistos: `findAll()`, `findById(Long)`, `save(DTO)`, `saveHorasTrabalhadasList(List<DTO>)`, `gerarRelatorioHoras(...)`, além de `convertToDto` / `convertToEntity`. Fluxo: **DTO → Entity (ModelMapper) → Repository → DTO**.

### 5.5 Divergências entre documentos e implementação (resolver antes de evoluir) 🔶
1. **Funcionário × projeto × perfil.** O Contexto diz que um funcionário pode estar em **vários projetos, com perfil diferente em cada um**; o ER/classes modelam `funcionario.id_projeto` e `id_perfil` (1 projeto e 1 perfil por funcionário). Se a regra do Contexto valer, é preciso uma tabela associativa (ex.: `alocacao`: funcionário, projeto, perfil, valor_hora, data_inicio, data_fim).
2. **Cliente sem "código interno".** O Contexto pede nome **e código interno**; o ER só tem `nome`.
3. **Perfil.** O Contexto fala em "cargo e valor base"; o ER tem `descricao`, `valor_hora_padrao`, `horas_padrao`. A classe `Perfil` do diagrama UML está vazia. Valor do funcionário "pode herdar do perfil".
4. **Nomes de coluna.** ER usa `data_relatorio`, `id_funcionario`; a entidade usa `data_trabalho`, `funcionario_id` (a migration V002 pode ter ajustado – conferir o esquema real).
5. **Tipo de `horas_trabalhadas`.** `LocalTime`/`TIME` **não representa totais de horas do mês** (168 h não cabe em `LocalTime`, que é hora do dia). Preferir `BigDecimal` (horas decimais) ou `Integer` (minutos) / `Duration`. O próprio comentário na entidade já aponta isso. Idem `ferias_licencas.horas_afastadas`.
6. **Territórios.** O Contexto cita municipais SP, RJ, Brasília, BH e estaduais "por estado"; o enum só tem SP, MG, RJ, DF. Definir se `Territorio` mistura estado e município.
7. **Armazenar CPF/matrícula** como `VARCHAR(11)` sem máscara; validar dígitos.
8. **Revisão de feriados (dia e mês sem ano, feriados móveis e carga externa):**
   - *Feriados fixos:* atualmente a coluna é `data DATE` (com ano). O ideal para feriados fixos que se repetem anualmente (ex.: 01/01, 21/04, 01/05, 07/09, 25/12) é colocar **dia e mês sem ano** para não exigir recadastro a cada ano.
   - *Feriados móveis:* datas com variação anual (como **carnaval**, páscoa, corpus christi) dependem do ano civil e exigem tratamento diferenciado (cadastro com ano ou cálculo algorítmico).
   - *Alimentação dos feriados:* avaliar o consumo de uma **API de feriados** (ex.: BrasilAPI) ou **dar carga com arquivo externo em formato CSV**.
9. **Revisão do caso de uso de horas registradas e impressão de relatório:**
   - O caso de uso de horas registradas precisa ser revisto para garantir consistência no ciclo completo: cálculo → edição → salvamento → emissão.
   - ⚠️ **Erro ao imprimir relatório no controle de horas (Consertar):** a implementação atual no backend (`HorasTrabalhadasService.generatePdfReport`) devolve texto plano como bytes em vez de gerar um PDF binário formatado real, além de a UI não repassar as horas editadas (`editableHoras`). Precisa ser corrigido.

---

## 6. Regras de negócio – Controlar Horas

| ID | Regra |
|---|---|
| RN01 | Jornada padrão: **8 horas por dia útil** (segunda a sexta). |
| RN02 | Sábados e domingos **não** são contabilizados. |
| RN03 | **Feriados** que caem em dia útil abatem 8 h de todos os funcionários. Feriados fixos devem considerar dia e mês sem ano; feriados móveis (como carnaval) consideram o ano do cálculo. Avaliar integração com API externa ou carga via arquivo CSV. 🔶 Considerar o território: nacional para todos; estadual/municipal conforme `territorio_sindicato` do funcionário. |
| RN04 | **Férias e licenças** (médica, maternidade, paternidade e outros afastamentos) abatem 8 h por dia útil do funcionário afetado, considerando apenas os dias **dentro do período** do relatório. |
| RN05 | Dia com feriado **e** licença é abatido **uma única vez**. |
| RN06 | `valor total = horas trabalhadas × valor da hora do funcionário`. |
| RN07 | O gestor pode **editar manualmente** as horas (compensações para mais ou para menos, horas extras). O valor total é recalculado. Aceitar apenas números ≥ 0. |
| RN08 | **Excluir** remove o funcionário só do relatório em tela, nunca do cadastro. |
| RN09 | Salvar grava, por funcionário: id, período (início/fim), horas (calculadas ou editadas), valor da hora e valor total. 🔶 Definir comportamento ao salvar de novo o mesmo funcionário/projeto/período (sobrescrever, bloquear ou versionar). |
| RN10 | **Área do cliente (Acesso e Validação):** Posteriormente, o cliente terá acesso restrito exclusivamente para **visualizar os dados** dos seus projetos (somente leitura). O cliente poderá dar check indicando se está **"OK"** ou **"Não OK"**. Se marcar **"Não OK"**, é **obrigatório preencher um comentário** com a justificativa. |
| RN11 | **Relatório do funcionário por dia:** O sistema deverá gerar relatório detalhado do funcionário por dia, indicando para cada dia se ele trabalhou ou faltou (especificando motivo: feriado, licença/férias ou falta). |

**Exemplo de conferência:** 01/09/2026 a 30/09/2026 → 22 dias úteis; feriado em 07/09 e 2 dias de licença → 19 dias × 8 = **152 h**; a R$ 100,00/h → **R$ 15.200,00**.

---

## 7. Caso de uso – Controlar Horas (versão revisada)

**Ator:** Gestor · **Pré-condição:** tela *Controlar Horas Trabalhadas* aberta · **Pós-condição:** lista exibida, pronta para editar/excluir/salvar/imprimir.

**Fluxo principal**
1. Gestor informa data inicial e data final.
2. Gestor seleciona o cliente; o sistema lista os projetos **daquele cliente**.
3. Gestor seleciona o projeto e clica em **Gerar Relatório**.
4. Sistema valida os filtros (FE01–FE03).
5. Sistema busca os funcionários do projeto (FE04 se nenhum).
6. Sistema identifica dias úteis, feriados em dia útil e férias/licenças de cada funcionário no período.
7. Sistema calcula dias trabalhados → horas (×8) → valor total.
8. Sistema exibe: **funcionário, CPF, horas calculadas, horas editáveis, valor da hora, valor total**.

**Fluxos alternativos (a partir da lista):** *Editar* horas (recalcula valor) · *Excluir selecionados* (com confirmação) · *Salvar* (persiste a lista) · *Imprimir PDF* (reflete edições e exclusões; ⚠️ *Erro ao imprimir relatório no controle de horas a consertar*).

**Exceções:** FE01 data inválida · FE02 data inicial posterior à final · FE03 campo obrigatório vazio · FE04 nenhum funcionário alocado no período · FE05 valor de horas inválido na edição (mantém o anterior).

### User stories (resumo)
| ID | História | Critério-chave |
|---|---|---|
| US01 | Como gestor, quero informar período, cliente e projeto para gerar o relatório com os dados que preciso. | Campos obrigatórios; projetos filtrados pelo cliente; data inicial ≤ final. |
| US02 | Como gestor, quero que o sistema calcule as horas automaticamente para eliminar planilha e erros. | RN01–RN05. |
| US03 | Como gestor, quero ver CPF, nome, horas, valor da hora e valor total para conferir antes de enviar. | Total = horas × valor/hora; moeda em R$ pt-BR. |
| US04 | Como gestor, quero editar as horas de um funcionário para ajustar o que o cálculo não cobre. | Recalcula total; só números ≥ 0. |
| US05 | Como gestor, quero remover funcionários da lista para cobrar só quem deve. | Multisseleção; confirmação; não apaga cadastro. |
| US06 | Como gestor, quero salvar as horas apuradas para manter histórico. | Grava id, período, horas, valor/hora, total; mensagem de sucesso. |
| US07 | Como gestor, quero gerar PDF para enviar ao gestor do cliente. | PDF = lista exibida, com edições/exclusões. ⚠️ Corrigir erro de geração. |

### 7.1 Revisão do caso de uso de horas registradas
O caso de uso de horas registradas / controle de horas precisa de revisão geral para assegurar conformidade do fluxo de ponta a ponta:
- **Cálculo de apontamento:** cálculo dos dias úteis abatendo feriados (fixos e móveis) e afastamentos.
- **Edição em tela:** propagação imediata do recálculo de valor total e uso das horas editadas na gravação e na impressão.
- **Gravação consistente:** persistência em `horas_trabalhadas` com tipos apropriados para acumulados mensais.
- ⚠️ **Erro ao imprimir relatório no controle de horas (Consertar):** o botão de impressão aciona o endpoint `/api/horas-trabalhadas/relatorio/pdf`, que atualmente retorna apenas uma string pura convertida em bytes, gerando erro de documento inválido no browser. É necessário implementar a geração real de PDF (backend ou frontend) e passar as horas editadas (`editableHoras`).

### 7.2 Casos de uso futuros (Posteriormente)

#### CDU008 – Área do Cliente (Acesso e Validação de Horas)
- **Ator:** Gestor do Cliente (Cliente).
- **Pré-condição:** Cliente autenticado no sistema.
- **Regras e Comportamento:**
  - **Acesso somente leitura:** O cliente só pode visualizar os dados de horas/apontamentos alocados aos seus projetos, sem permissão de alteração ou exclusão dos registros.
  - **Mecanismo de validação:** O cliente pode dar check indicando se as horas estão **"OK"** ou **"Não OK"**.
  - **Comentário obrigatório:** Se o cliente selecionar **"Não OK"**, o sistema exige obrigatoriamente o preenchimento de um **comentário/justificativa** apontando a divergência antes de submeter a validação.

#### CDU009 – Relatório do Funcionário por Dia
- **Ator:** Gestor (e posteriormente Cliente).
- **Objetivo:** Visualização detalhada diária dos apontamentos de um funcionário ao longo do período selecionado.
- **Detalhamento:**
  - Lista todos os dias do período especificado (calendário por dia).
  - Indica claramente **qual dia ele trabalhou** (ex.: 8h) e **qual dia faltou**.
  - Para cada ausência/falta, identifica a justificativa correspondente: feriado (nacional, estadual ou municipal), férias, licença médica/afastamento ou falta.

---

## 8. Interface (estado atual) ✅

- **Layout:** menu lateral fixo com os itens *Cadastro, Clientes, Novo Cliente, Projetos, Novo Projeto, Perfis, Novo Perfil, Funcionários, Novo Funcionário, Configurações, Feriados…* e área de conteúdo à direita.
- **Listas de cadastro:** título "Lista de …", botão "Novo …" e colunas com ações **Editar / Excluir** (Funcionários exibe ID, CPF, Matrícula, Nome, Projeto, Perfil, Valor Hora, Tipo Contrato, Território Sindicato).
- **Controlar Horas Trabalhadas** (`controlar-hora-mes`):
  - Formulário reativo `filtroForm` (`gerarRelatorio()` no submit) com *Data Inicial, Data Final, Cliente (select), Projeto (select)* e botão **Gerar Relatório**; mensagens de validação do tipo "Data Inicial é obrigatória.".
  - Tabela **Relatório de Horas Trabalhadas** com checkbox de seleção, *Funcionário, CPF, Horas Calculadas, Horas Editáveis (input), Valor Hora, Valor Total* e botões **Salvar**, **Excluir Selecionados**, **Imprimir PDF**.

### Pontos observados na captura de tela do MVP 🔶
- Mensagem **"Erro ao salvar horas."** visível junto ao relatório → o *Salvar* falhava naquele momento (verificar mapeamento DTO ↔ entidade, `LocalTime` vs horas totais e a migration V002).
- ⚠️ **Erro ao imprimir relatório no controle de horas (Consertar):** ao clicar no botão "Imprimir PDF", ocorre erro no navegador. Causa identificada no código: o backend (`HorasTrabalhadasService.generatePdfReport`) devolve uma string de texto em bytes com header `application/pdf`, corrompendo o arquivo, e o frontend envia apenas `horasTrabalhadasCalculadas` ignorando `editableHoras`.
- Período **01/05/2026–31/05/2026** retornou **168 h** (21 dias úteis × 8). 1º de maio (sexta) é feriado nacional: se estiver cadastrado, o esperado seria 160 h → conferir cadastro de feriados e a regra RN03.
- Valores exibidos como `R$30,240.00` (formato en-US). Usar locale **pt-BR** (`R$ 30.240,00`) via `registerLocaleData(localePt)` e `LOCALE_ID = 'pt-BR'` / `currency:'BRL':'symbol':'1.2-2':'pt-BR'`.
- Os testes de componente (`*.spec.ts`) parecem ser o esqueleto gerado pelo CLI (com erros de compilação no editor) → ainda sem cobertura real.

---

## 9. Convenções para gerar/alterar código

**Geral**
- Nomes de entidades, tabelas, campos, mensagens e commits em **português**, sem acentos em identificadores (`funcionario`, `horasTrabalhadas`).
- Trabalhar e abrir PRs a partir da branch **`dev`**.
- Não reescrever código existente que funciona; mudanças pequenas e focadas.

**Backend**
- Entidade (`@Entity`, Lombok) → `Repository` (Spring Data) → `Service` (`@Service`, conversão DTO/Entity com ModelMapper) → controller REST em `api`.
- Preferir **injeção por construtor** em código novo (o existente usa `@Autowired` em campo; não é necessário refatorar).
- **Nunca editar migrations já aplicadas** (`V001`, `V002`). Toda alteração de esquema vira uma nova migration `V00N__descricao_em_snake_case.sql` (dois underscores).
- Dinheiro sempre `BigDecimal` (escala 2, `RoundingMode.HALF_UP`); horas totais como decimal ou minutos, não `LocalTime`.
- Validar entrada com Bean Validation (`@NotNull`, `@PastOrPresent` etc.) e devolver erros padronizados (400) com mensagem em português.
- Cálculo de dias úteis/feriados/licenças deve ficar em método **puro e testável** (sem acesso a repositório), coberto por testes unitários com os cenários da seção 6.

**Frontend**
- Um módulo/pasta por recurso, componentes `*-list` e `*-form`, e `*.service.ts` com `HttpClient`.
- Formulários reativos (`FormGroup`, `Validators.required`) e mensagens de erro em português.
- Moeda e datas no locale **pt-BR**.
- URL da API centralizada em `environment.ts` (🔶 confirmar se já existe).

---

## 10. Próximos passos sugeridos (backlog)

### 10.1 Rever e Corrigir (MVP / Curto Prazo)
1. **Rever Feriados:**
   - No cadastro de feriados, **colocar dia e mês sem ano** para feriados fixos anuais (ex.: 01/01, 21/04, 01/05, 07/09, 12/10, 02/11, 15/11, 20/11, 25/12), evitando a necessidade de recadastrá-los todos os anos.
   - **Pensar nos feriados móveis como carnaval** (e Páscoa, Sexta-feira Santa, Corpus Christi), cujas datas variam a cada ano civil.
   - Avaliar soluções de automação: **talvez usar uma API de feriado** (ex.: BrasilAPI) ou **dar carga com arquivo externo em formato CSV**.
2. **Rever o caso de uso de horas registradas:**
   - Rever as etapas do caso de uso de controle/registro de horas (cálculo de dias úteis, aplicação das horas editadas e persistência correta).
   - ⚠️ **Erro ao imprimir relatório no controle de horas (Consertar):** corrigir a falha no botão "Imprimir PDF". Implementar gerador real de PDF (no backend com biblioteca como JasperReports/PDFBox ou no frontend com jsPDF/pdfmake) e assegurar que as horas editadas pelo gestor sejam refletidas no documento impresso.
3. **Corrigir o salvamento de horas:** resolver falha de persistência e trocar `LocalTime` por tipo adequado (`BigDecimal` ou minutos) na entidade `HorasTrabalhadas` e na migration `V003`.
4. **Revisar o cálculo:** conferir dias úteis, regras de feriados por território sindicato e afastamentos/licenças no período.
5. **Decidir modelo de alocação:** múltiplos projetos/perfis por funcionário vs modelo 1:1 atual.
6. **Ajustes de UI/UX:** aplicar locale `pt-BR` em moedas e datas, diálogo de confirmação de exclusão e mensagens amigáveis de feedback.
7. **Testes:** unitários para as regras puras de cálculo de horas e testes de integração de persistência.

### 10.2 Posteriormente (Fases Futuras)
1. **Área para o cliente acessar:**
   - Criar uma área/portal de acesso para o cliente (gestor do cliente).
   - **O cliente só pode visualizar os dados** (perfil com permissão de somente leitura para os projetos vinculados a ele).
   - **Check de validação:** o cliente pode dar check se o relatório/apontamento está **"OK"** ou **"Não OK"**.
   - **Comentário obrigatório:** se o status for marcado como **"Não OK"**, o cliente deve **preencher um comentário** com a justificativa da discordância.
2. **Relatório do funcionário por dia:**
   - Fazer um relatório do funcionário por dia – indicando detalhadamente qual dia ele trabalhou e qual dia faltou (com status/motivo: 8h normais trabalhadas, feriado, férias, licença médica ou falta).
3. **Evoluções adicionais:** importação de planilha de férias, integração com arquivos do sistema de ponto, workflow de aprovação de horas extras e publicação em ambiente de produção/web.

---

## 11. Glossário

| Termo | Significado |
|---|---|
| **Talent Body** | Serviço de alocação de consultores especializados em clientes. |
| **Alocação** | Vínculo de um funcionário a um projeto de um cliente. |
| **Perfil** | Cargo/nível do funcionário (ex.: Desenvolvedor Pleno, Júnior) com valor-hora base. |
| **Licença / afastamento** | Médica, maternidade, paternidade etc.; desconta horas. |
| **Horas calculadas × editáveis** | Calculadas = resultado automático; editáveis = ajuste manual do gestor usado no valor final. |
| **Território sindicato** | UF/município que define feriados aplicáveis ao funcionário. |
| **LTD** | Laboratório de Transformação Digital da Estácio BH. |

---

## 12. Fontes e lacunas

**Fontes usadas:** `Contexto.docx` (requisitos e diagrama de casos de uso), `Atividade_7.docx` (caso de uso original), versão revisada do caso de uso e user stories, `Atividade_8.docx` (ER e classes), `Atividade_9.docx` (capturas do MVP), `HOW TO.txt` e `caso de uso horas trabalhadas.txt` do GitHub.

**Não foi possível ler:** o código-fonte em `api/SGAT` e `sgat-ui` (as páginas de pastas do GitHub bloqueiam leitura automatizada). Por isso `pom.xml`, `package.json`, controllers, endpoints, DTOs, `application.properties` e o conteúdo das migrations **não estão documentados aqui**. Para completar este arquivo, cole ou anexe esses arquivos, ou gere um ZIP/`git ls-files` do repositório.
