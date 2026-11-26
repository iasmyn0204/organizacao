package com.folhafinanceira.organizao.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folhafinanceira.organizao.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    viewModel: FinanceViewModel,
    initialIsExpense: Boolean = true,
    onDismiss: () -> Unit
) {
    var isExpense by remember { mutableStateOf(initialIsExpense) }
    var description by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(if (initialIsExpense) "Alimentação" else "Salário") }
    var paymentMethod by remember { mutableStateOf("Pix") }
    var type by remember { mutableStateOf("Variável") }

    val expenseCategories = listOf("Alimentação", "Transporte", "Lazer", "Saúde", "Educação", "Moradia", "Compras", "Outros")
    val incomeCategories = listOf("Salário", "Benefícios", "Investimentos", "Renda Extra", "Outros")
    val paymentMethods = listOf("Pix", "Dinheiro", "Cartão de Crédito", "Cartão de Débito")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isExpense) "Adicionar Despesa" else "Adicionar Receita", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { isExpense = true; category = "Alimentação" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isExpense) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Despesa", color = if (isExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }

                    OutlinedButton(
                        onClick = { isExpense = false; category = "Salário" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (!isExpense) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Receita", color = if (!isExpense) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Valor (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Categoria: $category", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val cats = if (isExpense) expenseCategories.take(4) else incomeCategories.take(4)
                    cats.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                if (isExpense) {
                    Text("Forma de Pagamento: $paymentMethod", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        paymentMethods.take(3).forEach { pm ->
                            FilterChip(
                                selected = paymentMethod == pm,
                                onClick = { paymentMethod = pm },
                                label = { Text(pm, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                    if (amount > 0 && description.isNotBlank()) {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        if (isExpense) {
                            viewModel.addExpense(description, amount, category, currentDate, type, paymentMethod)
                        } else {
                            viewModel.addIncome(description, amount, category, currentDate)
                        }
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
