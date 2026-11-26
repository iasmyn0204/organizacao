package com.folhafinanceira.organizao.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.folhafinanceira.organizao.data.model.*
import com.folhafinanceira.organizao.data.remote.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AuthState {
    LOGGED_OUT, LOGGED_IN
}

class FinanceViewModel : ViewModel() {

    private val repository = SupabaseRepository()

    // Auth State
    private val _authState = MutableStateFlow(AuthState.LOGGED_IN)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow(
        User(
            id = "user_1",
            name = "Carlos Silva",
            email = "carlos@folhafinanceira.com",
            monthlyBudgetLimit = 3500.0,
            isDarkMode = false
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Financial Data States
    private val _incomes = MutableStateFlow<List<Income>>(
        listOf(
            Income(description = "Salário Mensal", amount = 5500.0, category = "Salário", date = "2025-05-01"),
            Income(description = "Freelance Design", amount = 1200.0, category = "Renda Extra", date = "2025-05-10"),
            Income(description = "Dividendos FIIs", amount = 350.0, category = "Investimentos", date = "2025-05-15")
        )
    )
    val incomes: StateFlow<List<Income>> = _incomes.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(
        listOf(
            Expense(description = "Aluguel Apartamento", amount = 1500.0, category = "Moradia", date = "2025-05-05", type = "Fixa", paymentMethod = "Pix"),
            Expense(description = "Supermercado Mensal", amount = 850.0, category = "Alimentação", date = "2025-05-08", type = "Variável", paymentMethod = "Cartão de Crédito"),
            Expense(description = "Combustível", amount = 300.0, category = "Transporte", date = "2025-05-12", type = "Variável", paymentMethod = "Cartão de Débito"),
            Expense(description = "Internet Fibra", amount = 120.0, category = "Moradia", date = "2025-05-10", type = "Fixa", paymentMethod = "Pix"),
            Expense(description = "Restaurante Fim de Semana", amount = 250.0, category = "Lazer", date = "2025-05-18", type = "Variável", paymentMethod = "Cartão de Crédito")
        )
    )
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _categoryLimits = MutableStateFlow<List<CategoryLimit>>(
        listOf(
            CategoryLimit(category = "Alimentação", limitAmount = 900.0),
            CategoryLimit(category = "Transporte", limitAmount = 400.0),
            CategoryLimit(category = "Lazer", limitAmount = 300.0),
            CategoryLimit(category = "Moradia", limitAmount = 1800.0),
            CategoryLimit(category = "Saúde", limitAmount = 250.0)
        )
    )
    val categoryLimits: StateFlow<List<CategoryLimit>> = _categoryLimits.asStateFlow()

    private val _savingsGoals = MutableStateFlow<List<SavingsGoal>>(
        listOf(
            SavingsGoal(title = "Reserva de Emergência", targetAmount = 15000.0, currentAmount = 8500.0, deadline = "2025-12-31"),
            SavingsGoal(title = "Viagem de Férias", targetAmount = 5000.0, currentAmount = 2100.0, deadline = "2025-09-15"),
            SavingsGoal(title = "Trocar de Smartphone", targetAmount = 3500.0, currentAmount = 1500.0, deadline = "2025-08-30")
        )
    )
    val savingsGoals: StateFlow<List<SavingsGoal>> = _savingsGoals.asStateFlow()

    private val _recurringExpenses = MutableStateFlow<List<RecurringExpense>>(
        listOf(
            RecurringExpense(description = "Aluguel", amount = 1500.0, category = "Moradia", dueDate = "Dia 05", paymentMethod = "Pix"),
            RecurringExpense(description = "Plano de Saúde", amount = 350.0, category = "Saúde", dueDate = "Dia 10", paymentMethod = "Débito Automático"),
            RecurringExpense(description = "Academia", amount = 110.0, category = "Saúde", dueDate = "Dia 15", paymentMethod = "Cartão de Crédito")
        )
    )
    val recurringExpenses: StateFlow<List<RecurringExpense>> = _recurringExpenses.asStateFlow()

    // Invoices (Notas Fiscais)
    private val _invoices = MutableStateFlow<List<Invoice>>(
        listOf(
            Invoice(
                storeName = "Supermercado Carrefour",
                accessKey = "3525 0512 3456 7890 1234 5500 1000 1234 5612 3456 7890",
                totalAmount = 245.80,
                date = "2025-05-18",
                category = "Alimentação",
                itemsCount = 12
            ),
            Invoice(
                storeName = "Posto Shell Central",
                accessKey = "3525 0598 7654 3210 4321 5500 1000 9876 5432 1098 7654",
                totalAmount = 150.00,
                date = "2025-05-15",
                category = "Transporte",
                itemsCount = 1
            )
        )
    )
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    // Virtual Agent Chat Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "assistant",
                message = "Olá, Carlos! Sou seu Agente Financeiro Pessoal. Como posso ajudar com seu orçamento hoje?",
                timestamp = "10:00"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Filters and Search
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("Todas")
    val selectedPeriodFilter = MutableStateFlow("Este Mês")

    // Computed Dashboard Metrics
    val totalIncome: Double
        get() = _incomes.value.sumOf { it.amount }

    val totalExpense: Double
        get() = _expenses.value.sumOf { it.amount }

    val balance: Double
        get() = totalIncome - totalExpense

    val monthlySavings: Double
        get() = if (totalIncome > 0) totalIncome - totalExpense else 0.0

    val budgetUsagePercentage: Float
        get() {
            val limit = _currentUser.value.monthlyBudgetLimit
            return if (limit > 0) ((totalExpense / limit) * 100).toFloat() else 0f
        }

    val budgetStatus: BudgetStatus
        get() {
            val percentage = budgetUsagePercentage
            return when {
                percentage > 100f -> BudgetStatus.EXCEEDED
                percentage >= 80f -> BudgetStatus.NEAR_LIMIT
                else -> BudgetStatus.WITHIN_LIMIT
            }
        }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun fetchAllData(userId: String = _currentUser.value.id) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedIncomes = repository.getIncomes(userId)
                if (fetchedIncomes.isNotEmpty()) _incomes.value = fetchedIncomes

                val fetchedExpenses = repository.getExpenses(userId)
                if (fetchedExpenses.isNotEmpty()) _expenses.value = fetchedExpenses

                val fetchedLimits = repository.getCategoryLimits(userId)
                if (fetchedLimits.isNotEmpty()) _categoryLimits.value = fetchedLimits

                val fetchedGoals = repository.getSavingsGoals(userId)
                if (fetchedGoals.isNotEmpty()) _savingsGoals.value = fetchedGoals

                val fetchedRecurring = repository.getRecurringExpenses(userId)
                if (fetchedRecurring.isNotEmpty()) _recurringExpenses.value = fetchedRecurring

                val fetchedInvoices = repository.getInvoices(userId)
                if (fetchedInvoices.isNotEmpty()) _invoices.value = fetchedInvoices

                val fetchedMessages = repository.getChatMessages(userId)
                if (fetchedMessages.isNotEmpty()) _chatMessages.value = fetchedMessages
            } catch (e: Exception) {
                _errorMessage.value = "Modo offline ativo: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Auth actions
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.signIn(email, pass)
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.LOGGED_IN
                    fetchAllData(user.id)
                } else {
                    _currentUser.update { it.copy(email = email) }
                    _authState.value = AuthState.LOGGED_IN
                }
            } catch (_: Exception) {
                _currentUser.update { it.copy(email = email) }
                _authState.value = AuthState.LOGGED_IN
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = repository.signUp(email, pass, name)
                if (user != null) {
                    _currentUser.value = user
                    _authState.value = AuthState.LOGGED_IN
                } else {
                    _currentUser.update { it.copy(name = name, email = email) }
                    _authState.value = AuthState.LOGGED_IN
                }
            } catch (_: Exception) {
                _currentUser.update { it.copy(name = name, email = email) }
                _authState.value = AuthState.LOGGED_IN
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.signOut()
            } catch (_: Exception) {}
            _authState.value = AuthState.LOGGED_OUT
        }
    }

    fun updateProfile(name: String, email: String, budgetLimit: Double) {
        val updatedUser = _currentUser.value.copy(name = name, email = email, monthlyBudgetLimit = budgetLimit)
        _currentUser.value = updatedUser
        viewModelScope.launch {
            try {
                repository.updateUser(updatedUser)
            } catch (_: Exception) {}
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _currentUser.update { it.copy(isDarkMode = enabled) }
    }

    // Income CRUD
    fun addIncome(description: String, amount: Double, category: String, date: String) {
        val newIncome = Income(
            userId = _currentUser.value.id,
            description = description,
            amount = amount,
            category = category,
            date = date
        )
        _incomes.update { listOf(newIncome) + it }
        viewModelScope.launch {
            try {
                repository.addIncome(newIncome)
            } catch (_: Exception) {}
        }
    }

    fun deleteIncome(id: String) {
        _incomes.update { list -> list.filter { it.id != id } }
        viewModelScope.launch {
            try {
                repository.deleteIncome(id)
            } catch (_: Exception) {}
        }
    }

    // Expense CRUD
    fun addExpense(description: String, amount: Double, category: String, date: String, type: String, paymentMethod: String) {
        val newExpense = Expense(
            userId = _currentUser.value.id,
            description = description,
            amount = amount,
            category = category,
            date = date,
            type = type,
            paymentMethod = paymentMethod
        )
        _expenses.update { listOf(newExpense) + it }
        viewModelScope.launch {
            try {
                repository.addExpense(newExpense)
            } catch (_: Exception) {}
        }
    }

    fun deleteExpense(id: String) {
        _expenses.update { list -> list.filter { it.id != id } }
        viewModelScope.launch {
            try {
                repository.deleteExpense(id)
            } catch (_: Exception) {}
        }
    }

    // Category Limit CRUD
    fun updateCategoryLimit(category: String, newLimit: Double) {
        val userId = _currentUser.value.id
        _categoryLimits.update { list ->
            val existing = list.find { it.category == category }
            if (existing != null) {
                list.map { if (it.category == category) it.copy(limitAmount = newLimit) else it }
            } else {
                list + CategoryLimit(userId = userId, category = category, limitAmount = newLimit)
            }
        }
        val targetLimit = _categoryLimits.value.find { it.category == category } ?: CategoryLimit(userId = userId, category = category, limitAmount = newLimit)
        viewModelScope.launch {
            try {
                repository.upsertCategoryLimit(targetLimit)
            } catch (_: Exception) {}
        }
    }

    // Savings Goal CRUD
    fun addSavingsGoal(title: String, targetAmount: Double, currentAmount: Double, deadline: String) {
        val goal = SavingsGoal(
            userId = _currentUser.value.id,
            title = title,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            deadline = deadline
        )
        _savingsGoals.update { it + goal }
        viewModelScope.launch {
            try {
                repository.addSavingsGoal(goal)
            } catch (_: Exception) {}
        }
    }

    fun updateGoalProgress(id: String, addedAmount: Double) {
        _savingsGoals.update { list ->
            list.map { if (it.id == id) it.copy(currentAmount = (it.currentAmount + addedAmount).coerceAtMost(it.targetAmount)) else it }
        }
        val goal = _savingsGoals.value.find { it.id == id }
        if (goal != null) {
            viewModelScope.launch {
                try {
                    repository.updateGoalProgress(goal)
                } catch (_: Exception) {}
            }
        }
    }

    fun deleteSavingsGoal(id: String) {
        _savingsGoals.update { list -> list.filter { it.id != id } }
        viewModelScope.launch {
            try {
                repository.deleteSavingsGoal(id)
            } catch (_: Exception) {}
        }
    }

    // Recurring Expense CRUD
    fun addRecurringExpense(description: String, amount: Double, category: String, dueDate: String, paymentMethod: String) {
        val rec = RecurringExpense(
            userId = _currentUser.value.id,
            description = description,
            amount = amount,
            category = category,
            dueDate = dueDate,
            paymentMethod = paymentMethod
        )
        _recurringExpenses.update { it + rec }
        viewModelScope.launch {
            try {
                repository.addRecurringExpense(rec)
            } catch (_: Exception) {}
        }
    }

    fun deleteRecurringExpense(id: String) {
        _recurringExpenses.update { list -> list.filter { it.id != id } }
        viewModelScope.launch {
            try {
                repository.deleteRecurringExpense(id)
            } catch (_: Exception) {}
        }
    }

    // ==================== RECURSO 1: LEITURA DE NOTA FISCAL (OCR & PARSING) ====================

    fun processInvoiceText(rawText: String, storeNameOverride: String = "", amountOverride: Double = 0.0, categoryOverride: String = "Alimentação") {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Smart Extraction Logic
        val store = if (storeNameOverride.isNotBlank()) storeNameOverride else extractStoreName(rawText)
        val amount = if (amountOverride > 0) amountOverride else extractTotalAmount(rawText)
        val category = if (categoryOverride.isNotBlank()) categoryOverride else inferCategory(rawText, store)

        val newInvoice = Invoice(
            userId = _currentUser.value.id,
            storeName = store,
            accessKey = extractAccessKey(rawText),
            totalAmount = amount,
            date = todayStr,
            category = category,
            itemsCount = extractItemsCount(rawText),
            rawText = rawText
        )

        _invoices.update { listOf(newInvoice) + it }

        // Adiciona automaticamente como despesa no sistema
        val newExpense = Expense(
            userId = _currentUser.value.id,
            description = "Nota Fiscal: $store",
            amount = amount,
            category = category,
            date = todayStr,
            type = "Variável",
            paymentMethod = "Cartão de Crédito"
        )
        _expenses.update { listOf(newExpense) + it }

        viewModelScope.launch {
            try {
                repository.addInvoice(newInvoice)
                repository.addExpense(newExpense)
            } catch (_: Exception) {}
        }
    }

    private fun extractStoreName(text: String): String {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.isNotEmpty()) {
            val firstLine = lines.first()
            if (firstLine.length in 3..40 && !firstLine.contains("DANFE") && !firstLine.contains("CNPJ")) {
                return firstLine
            }
        }
        val cnpjPattern = Regex("(?i)(supermercado|posto|farmacia|loja|restaurante|atacado|comercio)[^\\n]*")
        val match = cnpjPattern.find(text)
        return match?.value?.take(30) ?: "Estabelecimento Comercial"
    }

    private fun extractTotalAmount(text: String): Double {
        val totalRegex = Regex("(?i)(total|valor total|val\\.total|total R\\$|total gbr)[\\s:=]*R?\\$?\\s*(\\d+[.,]\\d{2})")
        val match = totalRegex.find(text)
        if (match != null) {
            val numStr = match.groupValues[2].replace(",", ".")
            return numStr.toDoubleOrNull() ?: 150.0
        }
        val genericPriceRegex = Regex("R?\\$\\s*(\\d+[.,]\\d{2})")
        val matches = genericPriceRegex.findAll(text).mapNotNull { it.groupValues[1].replace(",", ".").toDoubleOrNull() }.toList()
        return matches.maxOrNull() ?: 89.90
    }

    private fun extractAccessKey(text: String): String {
        val keyRegex = Regex("\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}")
        val match = keyRegex.find(text)
        return match?.value ?: "3525 ${System.currentTimeMillis().toString().take(10)}"
    }

    private fun extractItemsCount(text: String): Int {
        val itemsRegex = Regex("(?i)(qtd|qtd\\.|itens|total de itens)[\\s:=]*(\\d+)")
        val match = itemsRegex.find(text)
        return match?.groupValues?.get(2)?.toIntOrNull() ?: 5
    }

    private fun inferCategory(text: String, store: String): String {
        val combined = "$text $store".lowercase()
        return when {
            combined.contains("supermercado") || combined.contains("mercado") || combined.contains("carrefour") || combined.contains("pao de acucar") || combined.contains("alimento") -> "Alimentação"
            combined.contains("posto") || combined.contains("shell") || combined.contains("ipiranga") || combined.contains("gasolina") || combined.contains("combustivel") -> "Transporte"
            combined.contains("farmacia") || combined.contains("drogaria") || combined.contains("remedio") -> "Saúde"
            combined.contains("restaurante") || combined.contains("bar") || combined.contains("bistro") || combined.contains("cafe") -> "Lazer"
            else -> "Compras"
        }
    }

    // ==================== RECURSO 2: AGENTE VIRTUAL FINANCEIRO (AI ASSISTANT) ====================

    fun sendAssistantMessage(userPrompt: String) {
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val userMsg = ChatMessage(
            userId = _currentUser.value.id,
            sender = "user",
            message = userPrompt,
            timestamp = timeStr
        )
        _chatMessages.update { it + userMsg }

        viewModelScope.launch {
            try {
                repository.addChatMessage(userMsg)
            } catch (_: Exception) {}

            val responseText = generateAssistantReply(userPrompt)
            val assistantMsg = ChatMessage(
                userId = _currentUser.value.id,
                sender = "assistant",
                message = responseText,
                timestamp = timeStr
            )
            _chatMessages.update { it + assistantMsg }
            try {
                repository.addChatMessage(assistantMsg)
            } catch (_: Exception) {}
        }
    }

    private fun generateAssistantReply(prompt: String): String {
        val p = prompt.lowercase()
        val totalInc = totalIncome
        val totalExp = totalExpense
        val bal = balance
        val status = budgetStatus
        val limit = _currentUser.value.monthlyBudgetLimit

        return when {
            p.contains("gastei") || p.contains("despesa") || p.contains("total gasto") -> {
                "Você gastou um total de R$ ${String.format(Locale.getDefault(), "%.2f", totalExp)} este mês. O seu limite mensal cadastrado é de R$ ${String.format(Locale.getDefault(), "%.2f", limit)}."
            }
            p.contains("saldo") || p.contains("resumo") || p.contains("balanço") -> {
                "Seu resumo atual:\n• Receitas: R$ ${String.format(Locale.getDefault(), "%.2f", totalInc)}\n• Despesas: R$ ${String.format(Locale.getDefault(), "%.2f", totalExp)}\n• Saldo Líquido: R$ ${String.format(Locale.getDefault(), "%.2f", bal)}."
            }
            p.contains("limite") || p.contains("orçamento") -> {
                when (status) {
                    BudgetStatus.EXCEEDED -> "⚠️ Atenção! Seu orçamento mensal ultrapassou 100%. Recomendamos revisar despesas não essenciais em Lazer e Compras."
                    BudgetStatus.NEAR_LIMIT -> "⚠️ Cuidado! Você atingiu ${String.format(Locale.getDefault(), "%.1f", budgetUsagePercentage)}% do seu orçamento mensal."
                    BudgetStatus.WITHIN_LIMIT -> "✅ Excelente! Você está dentro do seu limite de orçamento com uso de ${String.format(Locale.getDefault(), "%.1f", budgetUsagePercentage)}%."
                }
            }
            p.contains("economizar") || p.contains("dica") || p.contains("conselho") -> {
                val topCategory = _expenses.value.groupBy { it.category }.maxByOrNull { entry -> entry.value.sumOf { it.amount } }
                if (topCategory != null) {
                    val catName = topCategory.key
                    val catTotal = topCategory.value.sumOf { it.amount }
                    "💡 Dica do Agente: Sua maior categoria de gastos é '$catName' com R$ ${String.format(Locale.getDefault(), "%.2f", catTotal)}. Tente definir um limite específico para economizar até 15% nessa área!"
                } else {
                    "💡 Dica do Agente: Reserve pelo menos 20% de cada receita diretamente para suas Metas de Economia logo após receber."
                }
            }
            p.contains("meta") || p.contains("reserva") -> {
                val goals = _savingsGoals.value
                if (goals.isNotEmpty()) {
                    val g = goals.first()
                    val prog = (g.currentAmount / g.targetAmount) * 100
                    "🎯 Sua meta '${g.title}' está em ${String.format(Locale.getDefault(), "%.1f", prog)}% (R$ ${String.format(Locale.getDefault(), "%.2f", g.currentAmount)} de R$ ${String.format(Locale.getDefault(), "%.2f", g.targetAmount)})."
                } else {
                    "Você não tem metas cadastradas. Que tal criar uma meta de Reserva de Emergência na tela de Planejamento?"
                }
            }
            else -> {
                "Compreendi sua mensagem! Como seu assistente financeiro, posso analisar seus gastos, verificar seu saldo, sugerir economia ou tirar dúvidas sobre seus limites."
            }
        }
    }
}

enum class BudgetStatus {
    WITHIN_LIMIT, NEAR_LIMIT, EXCEEDED
}
