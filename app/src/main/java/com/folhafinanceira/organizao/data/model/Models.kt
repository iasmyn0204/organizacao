package com.folhafinanceira.organizao.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: String = "user_1",
    @SerialName("name") val name: String = "Usuário Organização",
    @SerialName("email") val email: String = "usuario@folhafinanceira.com",
    @SerialName("monthly_budget_limit") val monthlyBudgetLimit: Double = 3000.0,
    @SerialName("is_dark_mode") val isDarkMode: Boolean = false
)

@Serializable
data class Income(
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String = "user_1",
    @SerialName("description") val description: String,
    @SerialName("amount") val amount: Double,
    @SerialName("category") val category: String, // Salário, Benefícios, Investimentos, Renda Extra, Outros
    @SerialName("date") val date: String // YYYY-MM-DD
)

@Serializable
data class Expense(
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String = "user_1",
    @SerialName("description") val description: String,
    @SerialName("amount") val amount: Double,
    @SerialName("category") val category: String, // Alimentação, Transporte, Lazer, Saúde, Educação, Moradia, Compras, Outros
    @SerialName("date") val date: String, // YYYY-MM-DD
    @SerialName("type") val type: String, // Fixa, Variável
    @SerialName("payment_method") val paymentMethod: String // Pix, Dinheiro, Cartão de Crédito, Cartão de Débito
)

@Serializable
data class CategoryLimit(
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String = "user_1",
    @SerialName("category") val category: String,
    @SerialName("limit_amount") val limitAmount: Double
)

@Serializable
data class SavingsGoal(
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String = "user_1",
    @SerialName("title") val title: String,
    @SerialName("target_amount") val targetAmount: Double,
    @SerialName("current_amount") val currentAmount: Double,
    @SerialName("deadline") val deadline: String
)

@Serializable
data class RecurringExpense(
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String = "user_1",
    @SerialName("description") val description: String,
    @SerialName("amount") val amount: Double,
    @SerialName("category") val category: String,
    @SerialName("due_date") val dueDate: String, // Dia do vencimento e.g. "Dia 10"
    @SerialName("payment_method") val paymentMethod: String
)

@Serializable
data class Invoice(
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String = "user_1",
    @SerialName("store_name") val storeName: String,
    @SerialName("access_key") val accessKey: String = "",
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("date") val date: String,
    @SerialName("category") val category: String,
    @SerialName("items_count") val itemsCount: Int = 1,
    @SerialName("raw_text") val rawText: String = ""
)

@Serializable
data class ChatMessage(
    @SerialName("id") val id: String = java.util.UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String = "user_1",
    @SerialName("sender") val sender: String, // "user" ou "assistant"
    @SerialName("message") val message: String,
    @SerialName("timestamp") val timestamp: String = "",
    @SerialName("action_type") val actionType: String = "" // "expense_added", "budget_alert", "info"
)
