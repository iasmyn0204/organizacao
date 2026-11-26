package com.folhafinanceira.organizao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folhafinanceira.organizao.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningScreen(viewModel: FinanceViewModel) {
    val goals by viewModel.savingsGoals.collectAsState()
    val recurring by viewModel.recurringExpenses.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddRecurringDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Planejamento e Metas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header for Goals
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Metas de Economia", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showAddGoalDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar Meta", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            items(goals) { goal ->
                val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat() else 0f
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(goal.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text("Até ${goal.deadline}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${currencyFormat.format(goal.currentAmount)} / ${currencyFormat.format(goal.targetAmount)}", style = MaterialTheme.typography.bodySmall)
                            OutlinedButton(
                                onClick = { viewModel.updateGoalProgress(goal.id, 200.0) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("+ R$ 200", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Header for Recurring Expenses
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Despesas Recorrentes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showAddRecurringDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar Recorrente", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            items(recurring) { rec ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(rec.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text("Vencimento: ${rec.dueDate} • ${rec.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = currencyFormat.format(rec.amount),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showAddGoalDialog) {
        var title by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("") }
        var current by remember { mutableStateOf("") }
        var deadline by remember { mutableStateOf("2025-12-31") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = { Text("Nova Meta de Economia") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título da Meta") }, shape = RoundedCornerShape(10.dp))
                    OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Valor Alvo (R$)") }, shape = RoundedCornerShape(10.dp))
                    OutlinedTextField(value = current, onValueChange = { current = it }, label = { Text("Valor Atual Inicial (R$)") }, shape = RoundedCornerShape(10.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tAmount = target.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val cAmount = current.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && tAmount > 0) {
                            viewModel.addSavingsGoal(title, tAmount, cAmount, deadline)
                            showAddGoalDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showAddRecurringDialog) {
        var desc by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var cat by remember { mutableStateOf("Moradia") }
        var due by remember { mutableStateOf("Dia 10") }

        AlertDialog(
            onDismissRequest = { showAddRecurringDialog = false },
            title = { Text("Nova Despesa Recorrente") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrição") }, shape = RoundedCornerShape(10.dp))
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Valor (R$)") }, shape = RoundedCornerShape(10.dp))
                    OutlinedTextField(value = due, onValueChange = { due = it }, label = { Text("Vencimento (e.g. Dia 05)") }, shape = RoundedCornerShape(10.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val a = amount.replace(",", ".").toDoubleOrNull() ?: 0.0
                        if (desc.isNotBlank() && a > 0) {
                            viewModel.addRecurringExpense(desc, a, cat, due, "Pix")
                            showAddRecurringDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRecurringDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
