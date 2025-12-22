package com.haoziliu.restaurantsystem.core.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.haoziliu.restaurantsystem.core.data.remote.model.OrderDto
import com.haoziliu.restaurantsystem.core.data.remote.model.toDomain
import com.haoziliu.restaurantsystem.core.data.remote.model.toDto
import com.haoziliu.restaurantsystem.core.domain.model.Order
import com.haoziliu.restaurantsystem.core.domain.model.OrderStatus
import com.haoziliu.restaurantsystem.core.domain.repository.OrderRepository
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OrderRepository {

    override suspend fun createOrder(order: Order): Result<String> {
        return try {
            val orderDto = order.toDto()
            
            // 使用 .add() 让 Firestore 自动生成 ID
            val documentRef = firestore.collection("orders")
                .add(orderDto)
                .await()
            
            // 最佳实践：把生成的 ID 回写到文档里，方便后续查询
            val generatedId = documentRef.id
            documentRef.update("id", generatedId)
            
            Result.success(generatedId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // 老板端：实时监听活跃订单
    override fun observeActiveOrders(): Flow<List<Order>> = callbackFlow {
        val listenerRegistration = firestore.collection("orders")
            // 过滤：只看没完成的、没取消的
            .whereNotIn("status", listOf(OrderStatus.COMPLETED.name, OrderStatus.CANCELLED.name))
            // 排序：最新的在最上面 (需要 Firestore 索引，运行一次App报错后点击链接创建即可)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // 或者 log 错误
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val orders = snapshot.toObjects(OrderDto::class.java).map { it.toDomain() }
                    trySend(orders)
                }
            }
        
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return try {
            firestore.collection("orders")
                .document(orderId)
                .update("status", newStatus.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}