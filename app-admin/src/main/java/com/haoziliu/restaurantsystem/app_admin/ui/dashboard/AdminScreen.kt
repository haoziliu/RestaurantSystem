package com.haoziliu.restaurantsystem.app_admin.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.haoziliu.restaurantsystem.core.domain.model.Order

@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // 使用 LazyColumn 展示三个分区的订单
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. 待支付/新订单 (红色预警)
                if (uiState.pendingOrders.isNotEmpty()) {
                    item { SectionHeader("🔔 待支付 / 新订单", Color(0xFFE57373)) }
                    items(uiState.pendingOrders) { order ->
                        OrderCard(
                            order = order,
                            actionLabel = "确认收款",
                            onAction = { viewModel.markAsPreparing(order) }, // 简化流程：收款即开始制作
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    }
                }

                // 2. 制作中 (黄色进行中)
                if (uiState.preparingOrders.isNotEmpty()) {
                    item { SectionHeader("👨‍🍳 制作中 / 厨房", Color(0xFFFFB74D)) }
                    items(uiState.preparingOrders) { order ->
                        OrderCard(
                            order = order,
                            actionLabel = "制作完成 -> 叫号",
                            onAction = { viewModel.markAsReady(order) },
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                // 3. 待取餐 (绿色完成)
                if (uiState.readyOrders.isNotEmpty()) {
                    item { SectionHeader("✅ 请取餐 / 叫号中", Color(0xFF81C784)) }
                    items(uiState.readyOrders) { order ->
                        OrderCard(
                            order = order,
                            actionLabel = "已取餐 (归档)",
                            onAction = { viewModel.markAsCompleted(order) },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }

                if (uiState.orders.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无活跃订单", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun OrderCard(
    order: Order,
    actionLabel: String,
    onAction: () -> Unit,
    containerColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${order.ticketNum}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "€${String.format("%.2f", order.totalPrice)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 显示菜品详情
            order.items.forEach { item ->
                Text(
                    text = "${item.quantity} x ${item.menuItemName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (item.selectedOptions.isNotEmpty()) {
                    Text(
                        text = "   ${item.selectedOptions.joinToString { it.name }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(actionLabel)
            }
        }
    }
}