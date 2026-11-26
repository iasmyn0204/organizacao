package com.folhafinanceira.organizao.data.remote

import com.folhafinanceira.organizao.data.model.*
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class SupabaseRepository {

    private val supabase = SupabaseClient.client

    // ==================== AUTHENTICATION ====================

    suspend fun signUp(email: String, pass: String, name: String): User? {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = pass
        }
        val userId = supabase.auth.currentUserOrNull()?.id ?: "user_1"
        val newUser = User(
            id = userId,
            name = name,
            email = email
        )
        try {
            supabase.from("users").insert(newUser)
        } catch (_: Exception) {
        }
        return newUser
    }

    suspend fun signIn(email: String, pass: String): User? {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = pass
        }
        val currentAuthUser = supabase.auth.currentUserOrNull() ?: return null
        return try {
            supabase.from("users")
                .select { filter { eq("id", currentAuthUser.id) } }
                .decodeSingle<User>()
        } catch (_: Exception) {
            User(
                id = currentAuthUser.id,
                name = currentAuthUser.userMetadata?.get("name")?.toString() ?: "Usuário",
                email = currentAuthUser.email ?: email
            )
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    // ==================== USERS / PROFILES ====================

    suspend fun updateUser(user: User) {
        supabase.from("users").upsert(user)
    }

    // ==================== INCOMES ====================

    suspend fun getIncomes(userId: String): List<Income> {
        return supabase.from("incomes")
            .select { filter { eq("user_id", userId) } }
            .decodeList<Income>()
    }

    suspend fun addIncome(income: Income): Income {
        return supabase.from("incomes").insert(income) {
            select()
        }.decodeSingle<Income>()
    }

    suspend fun deleteIncome(id: String) {
        supabase.from("incomes").delete {
            filter { eq("id", id) }
        }
    }

    // ==================== EXPENSES ====================

    suspend fun getExpenses(userId: String): List<Expense> {
        return supabase.from("expenses")
            .select { filter { eq("user_id", userId) } }
            .decodeList<Expense>()
    }

    suspend fun addExpense(expense: Expense): Expense {
        return supabase.from("expenses").insert(expense) {
            select()
        }.decodeSingle<Expense>()
    }

    suspend fun deleteExpense(id: String) {
        supabase.from("expenses").delete {
            filter { eq("id", id) }
        }
    }

    // ==================== CATEGORY LIMITS ====================

    suspend fun getCategoryLimits(userId: String): List<CategoryLimit> {
        return supabase.from("category_limits")
            .select { filter { eq("user_id", userId) } }
            .decodeList<CategoryLimit>()
    }

    suspend fun upsertCategoryLimit(limit: CategoryLimit) {
        supabase.from("category_limits").upsert(limit)
    }

    // ==================== SAVINGS GOALS ====================

    suspend fun getSavingsGoals(userId: String): List<SavingsGoal> {
        return supabase.from("savings_goals")
            .select { filter { eq("user_id", userId) } }
            .decodeList<SavingsGoal>()
    }

    suspend fun addSavingsGoal(goal: SavingsGoal): SavingsGoal {
        return supabase.from("savings_goals").insert(goal) {
            select()
        }.decodeSingle<SavingsGoal>()
    }

    suspend fun updateGoalProgress(goal: SavingsGoal) {
        supabase.from("savings_goals").upsert(goal)
    }

    suspend fun deleteSavingsGoal(id: String) {
        supabase.from("savings_goals").delete {
            filter { eq("id", id) }
        }
    }

    // ==================== RECURRING EXPENSES ====================

    suspend fun getRecurringExpenses(userId: String): List<RecurringExpense> {
        return supabase.from("recurring_expenses")
            .select { filter { eq("user_id", userId) } }
            .decodeList<RecurringExpense>()
    }

    suspend fun addRecurringExpense(recurring: RecurringExpense): RecurringExpense {
        return supabase.from("recurring_expenses").insert(recurring) {
            select()
        }.decodeSingle<RecurringExpense>()
    }

    suspend fun deleteRecurringExpense(id: String) {
        supabase.from("recurring_expenses").delete {
            filter { eq("id", id) }
        }
    }

    // ==================== INVOICES (NOTAS FISCAIS) ====================

    suspend fun getInvoices(userId: String): List<Invoice> {
        return supabase.from("invoices")
            .select { filter { eq("user_id", userId) } }
            .decodeList<Invoice>()
    }

    suspend fun addInvoice(invoice: Invoice): Invoice {
        return supabase.from("invoices").insert(invoice) {
            select()
        }.decodeSingle<Invoice>()
    }

    suspend fun deleteInvoice(id: String) {
        supabase.from("invoices").delete {
            filter { eq("id", id) }
        }
    }

    // ==================== CHAT MESSAGES (AGENTE VIRTUAL) ====================

    suspend fun getChatMessages(userId: String): List<ChatMessage> {
        return supabase.from("chat_messages")
            .select { filter { eq("user_id", userId) } }
            .decodeList<ChatMessage>()
    }

    suspend fun addChatMessage(message: ChatMessage): ChatMessage {
        return supabase.from("chat_messages").insert(message) {
            select()
        }.decodeSingle<ChatMessage>()
    }
}
