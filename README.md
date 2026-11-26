# 🌿 Organização - Gestão Financeira Pessoal Inteligente

![Android](https://img.shields.io/badge/Android-Kotlin-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Supabase](https://img.shields.io/badge/Backend-Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.4.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![AGP](https://img.shields.io/badge/AGP-9.5.3-000000?style=for-the-badge&logo=gradle&logoColor=white)

**Organização** é um aplicativo Android moderno e completo para gestão financeira pessoal. Desenvolvido com **Jetpack Compose**, **Kotlin** e integrado ao **Supabase**, ele combina facilidade de uso, leitura inteligente de notas fiscais (OCR/DANFE) e um **Agente Virtual com IA** para auxiliar o usuário no controle de orçamento e alcance de metas financeiras.

---

## ✨ Funcionalidades Principais

- 📊 **Dashboard de Saúde Financeira**: Visualização em tempo real de saldo disponível, total de receitas, despesas, economia mensal e barra de progresso do orçamento.
- 💸 **Gestão de Transações**: Controle completo de Entradas (Receitas) e Saídas (Despesas) por categoria, tipo de gasto (fixo/variável) e forma de pagamento (Pix, Cartão, Dinheiro).
- 🧾 **Leitura Inteligente de Nota Fiscal (DANFE/OCR)**: Leitor de cupons e notas fiscais que extrai automaticamente o estabelecimento, valor total, quantidade de itens, chave de acesso e categoria, registrando o gasto com 1 clique.
- 🤖 **Agente Virtual AI (Assistente Financeiro)**: Chat interativo inteligente com IA que analisa o saldo, orçamento e metas do usuário para responder dúvidas, emitir alertas e dar dicas de economia.
- 🎯 **Planejamento & Metas de Economia**: Criação e acompanhamento de progresso de metas de economia (Ex: Reserva de Emergência, Viagens, Compras).
- ⚠️ **Limites por Categoria**: Definição e monitoramento de limites de orçamento para categorias específicas (Alimentação, Transporte, Lazer, Moradia, etc.).
- 🔄 **Despesas Recorrentes**: Gerenciamento de contas fixas mensais com controle de data de vencimento.
- ☁️ **Sincronização em Nuvem via Supabase**: Persistência de dados segura com PostgreSQL, Postgrest REST API e autenticação.
- 🌙 **Suporte a Modo Escuro**: Tema adaptativo com paleta verde-folha nativa em Material 3.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: [Kotlin](https://kotlinlang.org/) (Coroutines, StateFlow, Serialization)
- **Interface Gráfica**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3, Navigation)
- **Arquitetura**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Backend / Nuvem**: [Supabase Kotlin SDK 3.x](https://supabase.com/) (`auth-kt` e `postgrest-kt`)
- **HTTP Client**: [Ktor Client](https://ktor.io/)
- **Build System**: Gradle 9.x com Android Gradle Plugin (AGP 9.5.3)

---

## 🏗️ Estrutura do Projeto

```
app/src/main/java/com/folhafinanceira/organizao/
├── data/
│   ├── model/
│   │   └── Models.kt                # Modelos de dados serializáveis (@Serializable)
│   └── remote/
│       ├── SupabaseClient.kt        # Configuração e inicialização do Supabase
│       └── SupabaseRepository.kt    # Repositório de dados e autenticação
├── ui/
│   ├── navigation/
│   │   └── NavGraph.kt              # Navegação principal e Bottom Navigation
│   ├── screens/
│   │   ├── DashboardScreen.kt       # Tela Inicial / Resumo Financeiro
│   │   ├── TransactionsScreen.kt    # Lista e gerenciamento de Transações
│   │   ├── ReceiptScannerScreen.kt  # Leitor e extrator de Notas Fiscais (OCR)
│   │   ├── AssistantScreen.kt       # Chat com Agente Virtual AI
│   │   ├── LimitsScreen.kt          # Definição e progresso de Limites por Categoria
│   │   ├── PlanningScreen.kt        # Metas de Economia e Despesas Recorrentes
│   │   ├── ReportsScreen.kt         # Gráficos e relatórios visuais
│   │   ├── SettingsScreen.kt        # Perfil, preferências e controle de conta
│   │   └── AuthScreen.kt            # Login e Cadastro de novos usuários
│   ├── theme/                       # Cores, Tipografia e Tema Material 3
│   └── viewmodel/
│       └── FinanceViewModel.kt      # ViewModel central da aplicação
└── MainActivity.kt                  # Activity principal
```

---

## 🗄️ Configuração do Banco de Dados no Supabase

Para conectar o aplicativo ao seu próprio projeto Supabase:

1. Crie um projeto em [Supabase.com](https://supabase.com).
2. Vá ao **SQL Editor** do Supabase e execute o script SQL de criação das tabelas:

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Perfis de Usuário
CREATE TABLE IF NOT EXISTS public.users (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    monthly_budget_limit NUMERIC(10, 2) DEFAULT 3000.00,
    is_dark_mode BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Receitas
CREATE TABLE IF NOT EXISTS public.incomes (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    description TEXT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    category TEXT NOT NULL,
    date TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Despesas
CREATE TABLE IF NOT EXISTS public.expenses (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    description TEXT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    category TEXT NOT NULL,
    date TEXT NOT NULL,
    type TEXT NOT NULL,
    payment_method TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Limites de Categoria
CREATE TABLE IF NOT EXISTS public.category_limits (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    category TEXT NOT NULL,
    limit_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Metas de Economia
CREATE TABLE IF NOT EXISTS public.savings_goals (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    title TEXT NOT NULL,
    target_amount NUMERIC(10, 2) NOT NULL,
    current_amount NUMERIC(10, 2) DEFAULT 0.00,
    deadline TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Despesas Recorrentes
CREATE TABLE IF NOT EXISTS public.recurring_expenses (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    description TEXT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    category TEXT NOT NULL,
    due_date TEXT NOT NULL,
    payment_method TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Notas Fiscais
CREATE TABLE IF NOT EXISTS public.invoices (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    store_name TEXT NOT NULL,
    access_key TEXT DEFAULT '',
    total_amount NUMERIC(10, 2) NOT NULL,
    date TEXT NOT NULL,
    category TEXT NOT NULL,
    items_count INT DEFAULT 1,
    raw_text TEXT DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Mensagens do Agente Virtual AI
CREATE TABLE IF NOT EXISTS public.chat_messages (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    sender TEXT NOT NULL,
    message TEXT NOT NULL,
    timestamp TEXT DEFAULT '',
    action_type TEXT DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Habilitar RLS e Permissões
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.incomes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.expenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.category_limits ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.savings_goals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.recurring_expenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.invoices ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.chat_messages ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Permitir tudo em users" ON public.users FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Permitir tudo em incomes" ON public.incomes FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Permitir tudo em expenses" ON public.expenses FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Permitir tudo em category_limits" ON public.category_limits FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Permitir tudo em savings_goals" ON public.savings_goals FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Permitir tudo em recurring_expenses" ON public.recurring_expenses FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Permitir tudo em invoices" ON public.invoices FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Permitir tudo em chat_messages" ON public.chat_messages FOR ALL USING (true) WITH CHECK (true);
```

3. Abra o arquivo `app/src/main/java/com/folhafinanceira/organizao/data/remote/SupabaseClient.kt` e insira sua `SUPABASE_URL` e `SUPABASE_ANON_KEY`:

```kotlin
const val SUPABASE_URL = "https://seu-projeto.supabase.co"
const val SUPABASE_ANON_KEY = "sua-chave-anonima-publica"
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Android Studio** Ladybug ou superior
- **JDK 17** ou superior
- Dispositivo físico Android ou Emulador (API 24+)

### Passos
1. Clone este repositório:
   ```bash
   git clone https://github.com/SEU_USUARIO/organizao.git
   ```
2. Abra a pasta do projeto no **Android Studio**.
3. Aguarde a sincronização do Gradle.
4. Clique em **Run 'app'** (`Shift + F10`) para executar no emulador ou dispositivo.

---

## 📄 Licença

Este projeto é desenvolvido para fins educacionais e de gestão financeira pessoal. Sinta-se livre para contribuir ou utilizar como base!
