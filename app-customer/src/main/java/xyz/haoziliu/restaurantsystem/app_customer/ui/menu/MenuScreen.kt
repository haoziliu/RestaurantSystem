package xyz.haoziliu.restaurantsystem.app_customer.ui.menu

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import xyz.haoziliu.restaurantsystem.core.domain.model.MenuItem

@Composable
fun MenuScreen(
    onCartClicked: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel() // Hilt 自动注入
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    var selectedItem by remember { mutableStateOf<MenuItem?>(null) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column() {
            // 顶部栏
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("点单菜单", style = MaterialTheme.typography.headlineMedium)

                Button(onClick = onCartClicked) {
                    Text("购物车")
                }

                // 调试用：同步按钮
                Button(onClick = { viewModel.syncMenu() }) {
                    Text("同步菜单")
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.categories.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无菜单数据，请点击上方同步按钮")
                }
            } else {
                // 简单的分类列表展示
                LazyColumn {
                    uiState.categories.forEach { category ->
                        item {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(category.items) { menuItem ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                onClick = { selectedItem = menuItem }, // ✅ 点击弹出
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(menuItem.name, style = MaterialTheme.typography.titleMedium)
                                    Text("¥${menuItem.price}", style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 渲染弹窗
        selectedItem?.let { item ->
            ProductDetailSheet(
                menuItem = item,
                onDismiss = { selectedItem = null },
                onAddToCart = { options ->
                    viewModel.addToCart(item, options)
                    selectedItem = null // 关闭弹窗
                }
            )
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is MenuUiEvent.ShowToast -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}