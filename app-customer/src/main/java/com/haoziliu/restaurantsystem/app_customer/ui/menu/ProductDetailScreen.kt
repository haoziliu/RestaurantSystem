package com.haoziliu.restaurantsystem.app_customer.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.haoziliu.restaurantsystem.core.domain.model.MenuItem
import com.haoziliu.restaurantsystem.core.domain.model.MenuModifierGroup
import com.haoziliu.restaurantsystem.core.domain.model.ModifierSelectionType
import com.haoziliu.restaurantsystem.core.domain.model.OrderOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
    menuItem: MenuItem,
    onDismiss: () -> Unit,
    onAddToCart: (List<OrderOption>) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    // 状态：记录用户选了哪些 Options
    // Map<GroupId, SelectedOptionId> (对于单选)
    // Map<GroupId, Set<SelectedOptionId>> (对于多选)
    // 为了简单，我们用一个 MutableStateList 存所有选中的 Option 对象
    val selectedOptions = remember { mutableStateListOf<OrderOption>() }

    LaunchedEffect(lifecycleOwner) {
        for (group in menuItem.modifierGroups) {
            if (group.selectionType == ModifierSelectionType.SINGLE_SELECT) {
                group.options.firstOrNull()?.let { option ->
                    val orderOption = OrderOption(option.id, option.label, option.priceDelta)
                    selectedOptions.add(orderOption)
                }
            }
        }
    }

    // 计算当前总价 (基础价 + 选项加价)
    val currentTotalPrice = menuItem.price + selectedOptions.sumOf { it.priceDelta }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 标题
            Text(menuItem.name, style = MaterialTheme.typography.headlineMedium)
            Text(menuItem.description ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            
            Spacer(modifier = Modifier.height(16.dp))

            // 渲染所有规格组 (Modifier Groups)
            menuItem.modifierGroups.forEach { group ->
                ModifierGroupSection(
                    group = group,
                    selectedOptions = selectedOptions,
                    onOptionToggle = { option, isSelected ->
                        if (group.selectionType == ModifierSelectionType.SINGLE_SELECT) {
                            // 单选：先移除该组其他的，再添加这个
                            // 注意：这里需要根据 Option ID 找到它所属的 Group ID，
                            // 但 Domain Model 里 Option 没存 Group ID。
                            // 简化逻辑：我们直接在 UI 层遍历移除同组的。
                            val optionsInThisGroup = group.options.map { it.id }
                            selectedOptions.removeIf { it.optionId in optionsInThisGroup }
                            selectedOptions.add(option)
                        } else {
                            // 多选
                            if (isSelected) selectedOptions.add(option)
                            else selectedOptions.remove(option)
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 底部按钮
            Button(
                onClick = { onAddToCart(selectedOptions.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("加入购物车 - €${String.format("%.2f", currentTotalPrice)}")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ModifierGroupSection(
    group: MenuModifierGroup,
    selectedOptions: List<OrderOption>, // 当前已选的列表
    onOptionToggle: (OrderOption, Boolean) -> Unit
) {
    Column {
        Text(
            text = "${group.title} ${if(group.isRequired) "(必选)" else "(可选)"}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        group.options.forEach { option ->
            val isSelected = selectedOptions.any { it.optionId == option.id }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                if (group.selectionType == ModifierSelectionType.SINGLE_SELECT) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { 
                             val orderOption = OrderOption(option.id, option.label, option.priceDelta)
                             onOptionToggle(orderOption, true) 
                        }
                    )
                } else {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                             val orderOption = OrderOption(option.id, option.label, option.priceDelta)
                             onOptionToggle(orderOption, checked)
                        }
                    )
                }
                Text(text = option.label, modifier = Modifier.weight(1f))
                if (option.priceDelta > 0) {
                    Text(text = "+€${option.priceDelta}", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}