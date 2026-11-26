package com.folhafinanceira.organizao.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.folhafinanceira.organizao.ui.viewmodel.FinanceViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitsScreen(viewModel: FinanceViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val categoryLimits by viewModel.categoryLimits.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    var editingCategory by remember { mutableStateOf<String?>(null) }
    var newLimitInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Limites de Gastos e Orçamento", fontWeight = FontWeight.Bold) },
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
            // General Limit Overview Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Limite Geral Mensal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currencyFormat.format(user.monthlyBudgetLimit),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "O sistema alerta automaticamente ao atingir 80% e 100% do orçamento.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Limites por Categoria",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(categoryLimits) { catLimit ->
                val spent = expenses.filter { it.category == catLimit.category }.sumOf { it.amount }
                val ratio = if (catLimit.limitAmount > 0) (spent / catLimit.limitAmount).toFloat() else 0f
                val isExceeded = ratio > 1f
                val isNear = ratio >= 0.8f && !isExceeded

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
                            Text(catLimit.category, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            IconButton(onClick = {
                                editingCategory = catLimit.category
                                newLimitInput = catLimit.limitAmount.toString()
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar Limite", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { ratio.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = when {
                                isExceeded -> Color(0xFFD32F2F)
                                isNear -> Color(0xFFF57C00)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gasto: ${currencyFormat.format(spent)}", style = MaterialTheme.typography.bodySmall)
                            Text("Limite: ${currencyFormat.format(catLimit.limitAmount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }

                        if (isExceeded) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("⚠️ Orçamento da categoria excedido!", color = Color(0xFFD32F2F), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        } else if (isNear) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("⚡ Próximo do limite (≥80%)", color = Color(0xFFF57C00), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (editingCategory != null) {
        AlertDialog(
            onDismissRequest = { editingCategory = null },
            title = { Text("Editar Limite: $editingCategory") },
            text = {
                OutlinedTextField(
                    value = newLimitInput,
                    onValueChange = { newLimitInput = it },
                    label = { Text("Novo Valor Limite (R$)") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLimit = newLimitInput.replace(",", ".").toDoubleOrNull()
                        if (newLimit != null && newLimit > 0) {
                            viewModel.updateCategoryLimit(editingCategory!!, newLimit)
                            editingCategory = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategory = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
