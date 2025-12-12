package xyz.haoziliu.restaurantsystem.app_customer.ui.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun CartScreen(
    onOrderSuccess: (String) -> Unit, // 回调：跳转到成功页
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 监听副作用：如果下单成功，跳转
    LaunchedEffect(uiState.orderSuccessTicket) {
        uiState.orderSuccessTicket?.let { ticket ->
            onOrderSuccess(ticket)
            viewModel.onOrderSuccessConsumed()
        }
    }

    Scaffold(
        bottomBar = {
            if (uiState.items.isNotEmpty()) {
                Button(
                    onClick = { viewModel.submitOrder() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    enabled = !uiState.isSubmitting
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("去结算 - ¥${String.format("%.2f", uiState.total)}", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "购物车",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            if (uiState.items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("购物车是空的", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(uiState.items) { item ->
                        CartItemRow(
                            item = item,
                            onRemove = { viewModel.removeItem(item.menuItemId) } // 需完善 CartRepo 的 remove 逻辑
                        )
                        HorizontalDivider()
                    }
                }
            }
            
            // 显示错误提示
            uiState.error?.let { err ->
                Text(err, color = Color.Red, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun CartItemRow(item: xyz.haoziliu.restaurantsystem.core.domain.model.OrderItem, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.menuItemName, style = MaterialTheme.typography.titleMedium)
            // 显示选中的规格
            if (item.selectedOptions.isNotEmpty()) {
                Text(
                    item.selectedOptions.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text("¥${item.itemTotal}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }
        
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.Gray)
        }
    }
}